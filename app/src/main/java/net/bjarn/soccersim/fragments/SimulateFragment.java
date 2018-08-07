package net.bjarn.soccersim.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import net.bjarn.soccersim.Helper;
import net.bjarn.soccersim.R;
import net.bjarn.soccersim.adapters.PreviousGamesAdapter;
import net.bjarn.soccersim.objects.Game;
import net.bjarn.soccersim.objects.Poule;
import net.bjarn.soccersim.objects.Team;

import java.util.ArrayList;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class SimulateFragment extends Fragment {

    @BindView(R.id.container) ConstraintLayout mContainerView;
    @BindView(R.id.game_details_cardview) CardView mGameDetailsView;
    @BindView(R.id.round_textview) TextView mRoundTextView;
    @BindView(R.id.next_current_game_textview) TextView mGameTextView;
    @BindView(R.id.start_game_fab) FloatingActionButton mStartGameButton;

    @BindView(R.id.home_team_flag) TextView mHomeTeamFlag;
    @BindView(R.id.away_team_flag) TextView mAwayTeamFlag;
    @BindView(R.id.home_team_name) TextView mHomeTeamName;
    @BindView(R.id.away_team_name) TextView mAwayTeamName;
    @BindView(R.id.home_team_score) TickerView mHomeTeamScore;
    @BindView(R.id.away_team_score) TickerView mAwayTeamScore;

    @BindView(R.id.previous_games_view) RecyclerView mPreviousGamesRecyclerView;
    @BindView(R.id.previous_games_empty) TextView mPreviousGamesEmptyTextView;

    private Realm mRealm;
    private SharedPreferences mSharedPreferences;
    private boolean mGameIsBusy = false;                                                            // Changes when a game is running or ending
    private Game game;
    private Team mHomeTeam;
    private Team mAwayTeam;
    private RealmResults<Game> previousGames;
    private PreviousGamesAdapter previousGamesAdapter;

    private NavigableMap<Integer, Integer> possibleScores = new TreeMap<>();
    private ArrayList<Integer> soundEffects = new ArrayList<>();

    private View.OnClickListener mStartGameOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mGameIsBusy) {
                mGameIsBusy = true;
                mStartGameButton.setImageResource(R.drawable.ic_fast_forward_white_24dp);
                simulateGame();
            } else {
                mGameIsBusy = false;
                mStartGameButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                setupNextGame();
                updatePreviousGames();
                if (game == null) {
                    ResultsFragment fragment = ResultsFragment.newInstance(true);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_frame, fragment)
                            .commit();
                }
            }
        }
    };

    public SimulateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SimulateFragment.
     */
    public static SimulateFragment newInstance() {
        SimulateFragment fragment = new SimulateFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRealm = Realm.getDefaultInstance();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Add all possible scores with [chance, score]
        possibleScores.put(100, 0);
        possibleScores.put(50, 1);
        possibleScores.put(20, 2);
        possibleScores.put(5, 3);
        possibleScores.put(0, 4);

        // Add sound effects to arraylist so we can pick a random sound later on!
        soundEffects.add(R.raw.sfx_1);
        soundEffects.add(R.raw.sfx_2);
        soundEffects.add(R.raw.sfx_3);
        soundEffects.add(R.raw.sfx_4);
        soundEffects.add(R.raw.sfx_5);
        soundEffects.add(R.raw.sfx_6);
        soundEffects.add(R.raw.sfx_7);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simulate, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mStartGameButton.setOnClickListener(mStartGameOnClickListener);

        // Set up scoreboard tickerviews
        mHomeTeamScore.setCharacterLists(TickerUtils.provideNumberList());
        mAwayTeamScore.setCharacterLists(TickerUtils.provideNumberList());

        RealmResults<Poule> poules = mRealm.where(Poule.class)
                .findAll();

        // Check if there are any poules, if not, return to prevent a crash since this view requires a poule.
        if (poules.isEmpty()) {
            return;
        }

        previousGames = mRealm.where(Game.class)
                .equalTo("isFinished", true)
                .findAll();

        previousGamesAdapter = new PreviousGamesAdapter(previousGames);
        mPreviousGamesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPreviousGamesRecyclerView.setAdapter(previousGamesAdapter);

        togglePreviousGamesVisibility();

        setupNextGame();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    /**
     * Set up the next game. This method will update the UI and resets the score. It also gets the
     * next unfinished {@link Game} object and sets it to {@link #game}
     */
    private void setupNextGame() {
        game = mRealm.where(Game.class)
                .equalTo("isFinished", false)
                .findFirst();
        if (game == null) { // No more games left. Poule is over.
            mStartGameButton.setImageResource(R.drawable.ic_autorenew_white_24dp);
            mStartGameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.resetGame(mRealm, getActivity());
                }
            });
            return;
        }

        // Reset score to 0.
        setScore(0, 0);

        mHomeTeam = mRealm.where(Team.class)
                .equalTo("uuid", game.getTeamHome())
                .findFirst();
        mAwayTeam = mRealm.where(Team.class)
                .equalTo("uuid", game.getTeamAway())
                .findFirst();

        mRoundTextView.setText(getString(R.string.game_round) + " " + game.getRound());
        mGameTextView.setText(R.string.next_game);
        mHomeTeamFlag.setText(mHomeTeam.getFlag());
        mAwayTeamFlag.setText(mAwayTeam.getFlag());
        toggleGameScoreVisibility();
        mHomeTeamName.setText(mHomeTeam.getName());
        mAwayTeamName.setText(mAwayTeam.getName());
    }

    /**
     * Simulate the game.
     */
    private void simulateGame() {
        mGameTextView.setText(R.string.current_game);
        toggleGameScoreVisibility();

        final Random random = new Random();
        float homeWinChance = calculateWinChance(mHomeTeam, random);
        float awayWinChance = calculateWinChance(mAwayTeam, random);

        int homeScore = generateScore(random);
        int awayScore = generateScore(random);

        // If chance to win for homeTeam is higher, give that team more chance to get a higher score.
        if (homeWinChance > awayWinChance) {
            while (homeScore < awayScore) {
                homeScore = generateScore(random);
            }
        } else if (homeWinChance < awayWinChance) { // Else, give awayTeam a chance on a higher score.
            while (awayScore < homeScore) {
                awayScore = generateScore(random);
            }
        }
        // If the chances are equal, it uses the earlier generated scores which are completely random.

        final int finalHomeScore = homeScore;
        final int finalAwayScore = awayScore;
        final String homeTeamUuid = mHomeTeam.getUuid();
        final String awayTeamUuid = mAwayTeam.getUuid();
        final String gameUuid = game.getUuid();
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Game currentGame = realm.where(Game.class)
                        .equalTo("uuid", gameUuid)
                        .findFirst();

                currentGame.setScoreHome(finalHomeScore);
                currentGame.setScoreAway(finalAwayScore);
                currentGame.setFinished(true);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Team homeTeam = realm.where(Team.class)
                                .equalTo("uuid", homeTeamUuid)
                                .findFirst();
                        homeTeam.setGoals(homeTeam.getGoals() + finalHomeScore);
                        homeTeam.setCounterpoints(homeTeam.getCounterpoints() + finalAwayScore);

                        Team awayTeam = realm.where(Team.class)
                                .equalTo("uuid", awayTeamUuid)
                                .findFirst();
                        awayTeam.setGoals(awayTeam.getGoals() + finalAwayScore);
                        awayTeam.setCounterpoints(awayTeam.getCounterpoints() + finalHomeScore);

                        // Check who is the winner. Winner gets 3 points added to their score.
                        if (finalHomeScore > finalAwayScore) {                                      // Home team won, home team gets 3 points
                            homeTeam.setScore(homeTeam.getScore() + 3);
                        } else if (finalHomeScore < finalAwayScore) {                               // Away team won, away team gets 3 points
                            awayTeam.setScore(awayTeam.getScore() + 3);
                        } else {                                                                    // Draw, both teams get 1 point
                            homeTeam.setScore(homeTeam.getScore() + 1);
                            awayTeam.setScore(awayTeam.getScore() + 1);
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        setScore(game.getScoreHome(), game.getScoreAway());
                        Helper.playSoundEffect(soundEffects.get(random.nextInt(soundEffects.size())), mSharedPreferences, getContext());
                    }
                });
            }
        });
    }

    /**
     * Calculate the win chance for a team.
     * This method adds all stat points for a {@link Team}, multiplied by importance with a random integer
     * to randomize outcome a bit more.
     *
     * It also calculates a random percentage. If this percentage is above 95, there will be an injured
     * player and the team gets 10 points in disadvantage. The player will be notified of this by an
     * alert dialog.
     *
     * @param team Team to calculate win chance for.
     * @return Win chance for the team
     */
    private float calculateWinChance(Team team, Random random) {

        float keeper = team.getKeeper() * 2;
        float defense = team.getDefense() * 3;
        float midfield = team.getMidfield() * 1;
        float attack = team.getAttack() * 3;
        float randomStat = random.nextInt(10 + 1);                                           // Generates a random integer to randomize stats a bit more.

        // Calculate chance of team having an injured player, if true, remove 10 stat points.
        int percent = random.nextInt(100 + 1);

        if (percent < 95) { // There is a 5 percent chance of having an injured player.
            return keeper + defense + midfield + attack + randomStat;
        } else { // Someone got injured, remove a statpoint and show alert dialog.
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle(R.string.dialog_injured_player);
            dialog.setMessage(getString(R.string.dialog_injured_player_msg, team.getName()));
            dialog.setNeutralButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialog.show();

            Helper.playSoundEffect(R.raw.sfx_8, mSharedPreferences, getContext());

            return keeper + defense + midfield + attack + randomStat - 10;
        }
    }

    /**
     * Generate a random score by generating a random integer between 0 and 100. The
     * closest key value from the {@link #possibleScores} map will be used.
     *
     * @return Picked score integer
     */
    private int generateScore(Random random) {
        int randomNumber = random.nextInt(100 + 1);

        int higher = possibleScores.ceilingKey(randomNumber);
        int lower = possibleScores.floorKey(randomNumber);

        return possibleScores.get(Math.abs(randomNumber - lower) > Math.abs(randomNumber - higher) ? higher : lower);
    }

    /**
     * Update the {@link #mPreviousGamesRecyclerView} containing previously played games.
     * Called when a new game has been simulated.
     */
    private void updatePreviousGames() {
        previousGames = mRealm.where(Game.class)
                .equalTo("isFinished", true)
                .findAll();

        togglePreviousGamesVisibility();

        previousGamesAdapter.notifyDataSetChanged();
    }

    /**
     * This method will toggle the visibility of the {@link #mPreviousGamesRecyclerView} and
     * {@link #mPreviousGamesEmptyTextView}, depending on the size of {@link #previousGames}.
     */
    private void togglePreviousGamesVisibility() {
        mPreviousGamesEmptyTextView.setVisibility(previousGames.size() > 0 ? View.GONE : View.VISIBLE);
        mPreviousGamesRecyclerView.setVisibility(previousGames.size() > 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * This method will toggle the visibility of the scoreboard depending of the game state.
     * If a game is running, the scoreboard will appear. Otherwise, the teamflags will show up.
     */
    private void toggleGameScoreVisibility() {
        mHomeTeamScore.setVisibility(mGameIsBusy ? View.VISIBLE : View.GONE);
        mHomeTeamFlag.setVisibility(mGameIsBusy ? View.GONE : View.VISIBLE);
        mAwayTeamScore.setVisibility(mGameIsBusy ? View.VISIBLE : View.GONE);
        mAwayTeamFlag.setVisibility(mGameIsBusy ? View.GONE : View.VISIBLE);
    }

    /**
     * Update the scoreboard of the current game.
     *
     * @param home Sets the home team score
     * @param away Sets the away team score
     */
    private void setScore(int home, int away) {
        mHomeTeamScore.setText(String.valueOf(home));
        mAwayTeamScore.setText(String.valueOf(away));
    }
}
