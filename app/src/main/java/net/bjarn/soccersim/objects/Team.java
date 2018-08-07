package net.bjarn.soccersim.objects;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Team extends RealmObject {

    @PrimaryKey
    @Required
    private String uuid = UUID.randomUUID().toString();
    @Required
    private String name;
    @Required
    private String flag;

    private float keeper;
    private float defense;
    private float midfield;
    private float attack;

    private int goals = 0;                                                                          // Goals made by the team
    private int counterpoints = 0;                                                                  // Goals against the team
    private int score = 0;                                                                          // Points received for draws and wins

    // This variable is used for poule creation. Gets set to true when selected in recyclerview.
    private boolean selected = false;

    public Team() {
        // Required empty constructor
    }

    /**
     * Public Team Constructor
     *
     * @param name Name of the team
     * @param flag Emoji flag of the team
     * @param keeper Average statistic of the goalkeeper
     * @param defense The minimum, average and maximum statistic of the defenders
     * @param midfield The minimum, average and maximum statistic of the midfielders
     * @param attack The minimum, average and maximum statistic of the attackers
     */
    public Team(String name, String flag, float keeper, float defense, float midfield, float attack) {
        this.name = name;
        this.flag = flag;
        this.keeper = keeper;
        this.defense = defense;
        this.midfield = midfield;
        this.attack = attack;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public float getKeeper() {
        return keeper;
    }

    public void setKeeper(float keeper) {
        this.keeper = keeper;
    }

    public float getDefense() {
        return defense;
    }

    public void setDefense(float defense) {
        this.defense = defense;
    }

    public float getMidfield() {
        return midfield;
    }

    public void setMidfield(float midfield) {
        this.midfield = midfield;
    }

    public float getAttack() {
        return attack;
    }

    public void setAttack(float attack) {
        this.attack = attack;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getCounterpoints() {
        return counterpoints;
    }

    public void setCounterpoints(int counterpoints) {
        this.counterpoints = counterpoints;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getGoalDifference() {
        return getGoals() - getCounterpoints();
    }
}
