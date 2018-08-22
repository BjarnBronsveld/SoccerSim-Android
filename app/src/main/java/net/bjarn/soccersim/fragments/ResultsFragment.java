package net.bjarn.soccersim.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bjarn.soccersim.R;
import net.bjarn.soccersim.adapters.ResultsAdapter;
import net.bjarn.soccersim.objects.Game;
import net.bjarn.soccersim.objects.Poule;
import net.bjarn.soccersim.objects.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class ResultsFragment extends Fragment {

    @BindView(R.id.results_recyclerview)
    RecyclerView mRecyclerView;

    private Realm mRealm;

    private boolean mShowWinner = false;

    private ArrayList<Team> mTeams = new ArrayList<>();

    public ResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ResultsFragment.
     */
    public static ResultsFragment newInstance(boolean showWinner) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putBoolean("show_winner", showWinner);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mShowWinner = getArguments().getBoolean("show_winner");
        }

        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Clear arraylist to prevent duplicates
        mTeams.clear();

        Poule poule = mRealm.where(Poule.class)
                .findFirst();

        mTeams.addAll(poule.getParticipants());

        Comparator<Team> comparator = new Comparator<Team>() {
            @Override
            public int compare(final Team o1, final Team o2) {
                return orderResults(o1, o2);
            }
        };

        Collections.sort(mTeams, comparator);
        // Reverse the arraylist
        Collections.reverse(mTeams);

        if (mShowWinner) showResultsAlert();

        // Recyclerview always contains 4 teams. Make hasFixedSize true to optimize performance.
        mRecyclerView.setHasFixedSize(true);

        ResultsAdapter resultsAdapter = new ResultsAdapter(mTeams, getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(resultsAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    /**
     * This method will compare statistics of two {@link Team} objects. The returned integer will be
     * -1, 0 or 1. Depending on the outcome.
     *
     * The returned integer will be used in a comperator to sort the ranking list.
     *
     * @param team1 Instance of Team object
     * @param team2 Instance of Team object
     * @return An integer (-1, 0 or 1) depending on the outcome
     */
    private int orderResults(Team team1, Team team2) {
        if (team1.getScore() > team2.getScore()) {                                                  // Team 1 has a higher score, will rank higher.
            return 1;
        }
        else if (team1.getScore() < team2.getScore()) {                                             // Team 2 has a higher score, will rank higher.
            return -1;
        }                                                                                           // Both teams have the same score. Continue to comparing the goal difference.
        else if (team1.getGoalDifference() > team2.getGoalDifference()) {                           // Team 1 has a higher goal difference, will rank higher.
            return 1;
        }
        else if (team1.getGoalDifference() < team2.getGoalDifference()) {                           // Team 2 has a higher goal difference, will rank higher.
            return -1;
        }                                                                                           // Both teams have the same goal difference. Continue to comparing made goals.
        else if (team1.getGoals() > team2.getGoals()) {                                             // Team 1 has more goals, will rank higher.
            return 1;
        }
        else if (team1.getGoals() < team2.getGoals()) {                                             // Team 2 has more goals, will rank higher.
            return -1;
        }                                                                                           // Both teams have the same amount of goals. Continue to comparing counterpoints.
        else if (team1.getCounterpoints() < team2.getCounterpoints()) {                             // Team 1 has less counterpoints, will rank higher.
            return 1;
        }
        else if (team1.getCounterpoints() > team2.getCounterpoints()) {                             // Team 2 has less counterpoints, will rank higher.
            return -1;
        }
        else {                                                                                      // All previous comparisons had an equal outcome. Continue to comparing the best mutual matches of both teams.
            Game game1 = mRealm.where(Game.class)
                    .equalTo("teamHome", team1.getUuid())
                    .equalTo("teamAway", team2.getUuid())
                    .findFirst();
            Game game2 = mRealm.where(Game.class)
                    .equalTo("teamHome", team2.getUuid())
                    .equalTo("teamAway", team1.getUuid())
                    .findFirst();

            // Check if game1 or game2 is null. This varies depending on if team1 played at home of
            // away.
            if (game1 != null) {
                return Integer.compare(game1.getScoreHome(), game1.getScoreAway());
            } else if (game2 != null) {
                return Integer.compare(game2.getScoreAway(), game2.getScoreHome());
            }

            // Teams did not play a match against each other yet. Let them share their position.
            return 0;
        }
    }

    /**
     * Show the user an alert to show which teams will play in the knock-out phase.
     */
    private void showResultsAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_knockout_phase)
                .setMessage(getString(R.string.dialog_knockout_msg,
                        mTeams.get(0).getName(),
                        mTeams.get(1).getName()))
                .setPositiveButton(getString(R.string.dialog_close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }
}
