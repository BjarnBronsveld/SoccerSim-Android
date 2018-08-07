package net.bjarn.soccersim.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.bjarn.soccersim.R;
import net.bjarn.soccersim.objects.Team;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class TeamSelectionAdapter extends RealmRecyclerViewAdapter<Team, TeamSelectionAdapter.TeamViewHolder> {

    private RecyclerView mRecyclerView;
    private final Context mContext;

    public TeamSelectionAdapter(OrderedRealmCollection<Team> data, Context context) {
        super(data, true);
        setHasStableIds(false);

        this.mContext = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @Override
    public TeamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_selection, parent, false);
        return new TeamViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TeamViewHolder holder, int position) {
        final Team team = getItem(position);
        holder.data = team;

        holder.container.setBackgroundColor(team.isSelected() ? mContext.getColor(R.color.colorPrimaryLight) : Color.WHITE);

        holder.flag.setText(team.getFlag());
        holder.name.setText(team.getName());

        holder.keeperStat.setText(String.valueOf(team.getKeeper()));
        holder.defenseStat.setText(String.valueOf(team.getDefense()));
        holder.midfieldStat.setText(String.valueOf(team.getMidfield()));
        holder.attackStat.setText(String.valueOf(team.getAttack()));

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.statContainerLayout.getVisibility() == View.GONE) {
                    holder.statContainerLayout.setVisibility(View.VISIBLE);
                    holder.expandCollapseImageView.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                } else {
                    holder.statContainerLayout.setVisibility(View.GONE);
                    holder.expandCollapseImageView.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
                TransitionManager.beginDelayedTransition(mRecyclerView, new AutoTransition());
            }
        });

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try (Realm realm = Realm.getDefaultInstance()) {
                    if (amountSelectedTeams(realm) < 4 || team.isSelected()) {
                        realm.beginTransaction();
                        team.setSelected(!team.isSelected());
                        realm.commitTransaction();
                    }
                    else {
                        Toast.makeText(mContext, R.string.toast_max_4_teams_in_poule, Toast.LENGTH_LONG).show();
                    }
                }

                return true;
            }
        });
    }

    /**
     * Get the amount of selected teams.
     *
     * @param realm An instance of Realm
     * @return Integer of the amount of selected teams.
     */
    private int amountSelectedTeams(Realm realm) {
        return realm.where(Team.class).equalTo("selected", true).findAll().size();
    }

    class TeamViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.container) CardView container;
        @BindView(R.id.team_flag) TextView flag;
        @BindView(R.id.team_name) TextView name;
        @BindView(R.id.expanded_stat_view) ConstraintLayout statContainerLayout;
        @BindView(R.id.expand_collapse_image) ImageView expandCollapseImageView;
        @BindView(R.id.keeper_stat_textview) TextView keeperStat;
        @BindView(R.id.defense_stat_textview) TextView defenseStat;
        @BindView(R.id.midfield_stat_textview) TextView midfieldStat;
        @BindView(R.id.attack_stat_textview) TextView attackStat;
        public Team data;

        TeamViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
