package net.bjarn.soccersim.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.bjarn.soccersim.R;
import net.bjarn.soccersim.objects.Game;
import net.bjarn.soccersim.objects.Team;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class PreviousGamesAdapter extends RealmRecyclerViewAdapter<Game, PreviousGamesAdapter.PreviousGameViewHolder> {

    private Realm mRealm;

    /**
     * Public constructor for PreviousGameAdapter
     *
     * @param data An OrderedRealmCollection containing {@link Game} objects
     */
    public PreviousGamesAdapter(OrderedRealmCollection<Game> data) {
        super(data, true);
        setHasStableIds(false);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public PreviousGamesAdapter.PreviousGameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_previousgame, parent, false);
        return new PreviousGamesAdapter.PreviousGameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PreviousGamesAdapter.PreviousGameViewHolder holder, int position) {
        final Game game = getItem(position);
        holder.data = game;

        Team homeTeam = mRealm.where(Team.class).equalTo("uuid", game.getTeamHome()).findFirst();
        Team awayTeam = mRealm.where(Team.class).equalTo("uuid", game.getTeamAway()).findFirst();

        holder.homeFlag.setText(homeTeam.getFlag());
        holder.homeName.setText(homeTeam.getName());
        holder.homeScore.setText(String.valueOf(game.getScoreHome()));
        holder.awayFlag.setText(awayTeam.getFlag());
        holder.awayName.setText(awayTeam.getName());
        holder.awayScore.setText(String.valueOf(game.getScoreAway()));
    }

    class PreviousGameViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.home_team_flag) TextView homeFlag;
        @BindView(R.id.home_team_name) TextView homeName;
        @BindView(R.id.home_team_score) TextView homeScore;
        @BindView(R.id.away_team_flag) TextView awayFlag;
        @BindView(R.id.away_team_name) TextView awayName;
        @BindView(R.id.away_team_score) TextView awayScore;
        public Game data;

        PreviousGameViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
