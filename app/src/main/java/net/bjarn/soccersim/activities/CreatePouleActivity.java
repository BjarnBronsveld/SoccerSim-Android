package net.bjarn.soccersim.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.bjarn.soccersim.R;
import net.bjarn.soccersim.objects.Game;
import net.bjarn.soccersim.objects.Poule;
import net.bjarn.soccersim.objects.Team;
import net.bjarn.soccersim.adapters.TeamSelectionAdapter;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

public class CreatePouleActivity extends AppCompatActivity {

    @BindView(R.id.team_selection_recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.start_poule_button) Button mStartPouleButton;
    @BindView(R.id.start_poule_button_disabled) Button mStartPouleButtonDisabled;

    private Realm mRealm;

    private RealmList<Team> mSelectedTeams = new RealmList<>();

    private RealmChangeListener<RealmResults<Team>> mTeamRealmChangeListener = new RealmChangeListener<RealmResults<Team>>() {
        @Override
        public void onChange(RealmResults<Team> teams) {
            // Enable or disable the start button depending if requirements are met
            if (getSelectedTeams().size() == 4) {
                mStartPouleButtonDisabled.setVisibility(View.GONE);
                mStartPouleButton.setVisibility(View.VISIBLE);
            } else {
                mStartPouleButtonDisabled.setVisibility(View.VISIBLE);
                mStartPouleButton.setVisibility(View.GONE);
            }

            // Clear the selected teams first before adding the newly selected teams
            mSelectedTeams.clear();
            mSelectedTeams.addAll(getSelectedTeams());
        }
    };

    private View.OnClickListener mStartPouleOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final RealmList<Game> games = getPouleGames(mRealm);

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Toast.makeText(getApplicationContext(), R.string.toast_creating_poule_msg, Toast.LENGTH_SHORT).show();


                    // Create a new Poule object in Realm with the selected teams and generated games
                    Poule poule = realm.createObject(Poule.class, new Poule().getUuid());
                    poule.setParticipants(mSelectedTeams);
                    poule.setGames(games);

                    Toast.makeText(getApplicationContext(), R.string.toast_poule_created_msg, Toast.LENGTH_LONG).show();

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poule);

        ButterKnife.bind(this);

        this.setTitle(getString(R.string.title_create_poule));

        mRealm = Realm.getDefaultInstance();

        RealmResults<Team> teams = mRealm.where(Team.class).findAll();
        teams.addChangeListener(mTeamRealmChangeListener);                                          // This listener will listen for changes to the Team objects

        TeamSelectionAdapter teamSelectionAdapter = new TeamSelectionAdapter(teams, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(teamSelectionAdapter);

        mStartPouleButton.setOnClickListener(mStartPouleOnClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecyclerView.setAdapter(null);
        mRealm.close();
    }

    /**
     * Get all Team objects selected by the user and return them into a
     * RealmResults object.
     *
     * @return A new instance of RealmResults<Team>
     */
    private RealmResults<Team> getSelectedTeams() {
        return mRealm.where(Team.class).equalTo("selected", true).findAll();
    }

    /**
     * Generate six games in three rounds using selected teams and add these
     * games to Realm.
     *
     * @return RealmList<Game>
     */
    private RealmList<Game> getPouleGames(Realm realm) {
        final RealmList<Game> games = new RealmList<>();

        final String team1Uuid = mSelectedTeams.get(0).getUuid();
        final String team2Uuid = mSelectedTeams.get(1).getUuid();
        final String team3Uuid = mSelectedTeams.get(2).getUuid();
        final String team4Uuid = mSelectedTeams.get(3).getUuid();


        final List<Game> tempGames = Arrays.asList(
                new Game(team1Uuid, team2Uuid, 1),
                new Game(team3Uuid, team4Uuid, 1),
                new Game(team2Uuid, team3Uuid, 2),
                new Game(team4Uuid, team1Uuid, 2),
                new Game(team3Uuid, team1Uuid, 3),
                new Game(team2Uuid, team4Uuid, 3)
        );

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                // Add all created games to realm
                realm.insert(tempGames);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // If successful, pass all created games to the games list which will be returned
                games.addAll(tempGames);
            }
        });

        return games;
    }

}
