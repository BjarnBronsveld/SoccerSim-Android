package net.bjarn.soccersim.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.bjarn.soccersim.R;
import net.bjarn.soccersim.objects.Game;
import net.bjarn.soccersim.objects.Team;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {

    private Realm mRealm;
    private Context mContext;
    private ArrayList<Team> teams;

    public ResultsAdapter(ArrayList<Team> data, Context context) {
        setHasStableIds(false);

        this.teams = data;
        this.mContext = context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRealm = Realm.getDefaultInstance();
    }

    @NonNull
    @Override
    public ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_results, parent, false);
        return new ResultsAdapter.ResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsViewHolder holder, int position) {
        final Team team = teams.get(position);
        holder.data = team;

        if (position == 0) holder.itemView.setBackgroundColor(mContext.getColor(R.color.colorPrimary));
        else if (position == 1) holder.itemView.setBackgroundColor(mContext.getColor(R.color.colorPrimaryLight));

        int gamesHome = mRealm.where(Game.class)
                .equalTo("teamHome", team.getUuid())
                .equalTo("isFinished", true).findAll().size();
        int gamesAway = mRealm.where(Game.class)
                .equalTo("teamAway", team.getUuid())
                .equalTo("isFinished", true).findAll().size();

        holder.position.setText(position + 1 + ".");
        holder.name.setText(team.getFlag() + " " + team.getName());

        holder.played.setText(String.valueOf(gamesHome + gamesAway));
        holder.score.setText(String.valueOf(team.getScore()));
        holder.goalDifference.setText(String.valueOf(team.getGoalDifference()));
        holder.goals.setText(String.valueOf(team.getGoals()));
        holder.counterpoints.setText(String.valueOf(team.getCounterpoints()));
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    class ResultsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.team_position) TextView position;
        @BindView(R.id.team_flag_name) TextView name;
        @BindView(R.id.team_played) TextView played;
        @BindView(R.id.team_score) TextView score;
        @BindView(R.id.team_goal_difference) TextView goalDifference;
        @BindView(R.id.team_goals) TextView goals;
        @BindView(R.id.team_counterpoints) TextView counterpoints;
        public Team data;

        ResultsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
