package net.bjarn.soccersim.objects;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Game extends RealmObject {

    @PrimaryKey
    @Required
    private String uuid = UUID.randomUUID().toString();

    private String teamHome;
    private String teamAway;

    private int scoreHome = 0;
    private int scoreAway = 0;

    private int round;

    private boolean isFinished = false;

    public Game() {
        // Required empty constructor
    }

    /**
     * Game constructor
     *
     * @param teamHome UUID of team playing home
     * @param teamAway UUID of team playing away
     * @param round Number of round the game is in
     */
    public Game(String teamHome, String teamAway, int round) {
        this.teamHome = teamHome;
        this.teamAway = teamAway;
        this.round = round;
    }

    /**
     * Game constructor
     *
     * @param teamHome UUID of team playing home
     * @param teamAway UUID of team playing away
     * @param scoreHome Score of home team
     * @param scoreAway Score of away team
     * @param round Number of round the game is in
     * @param isFinished Boolean to determine if the game has been played
     */
    public Game(String teamHome, String teamAway, int scoreHome, int scoreAway, int round, boolean isFinished) {
        this.teamHome = teamHome;
        this.teamAway = teamAway;
        this.scoreHome = scoreHome;
        this.scoreAway = scoreAway;
        this.round = round;
        this.isFinished = isFinished;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTeamHome() {
        return teamHome;
    }

    public void setTeamHome(String teamHome) {
        this.teamHome = teamHome;
    }

    public String getTeamAway() {
        return teamAway;
    }

    public void setTeamAway(String teamAway) {
        this.teamAway = teamAway;
    }

    public int getScoreHome() {
        return scoreHome;
    }

    public void setScoreHome(int scoreHome) {
        this.scoreHome = scoreHome;
    }

    public int getScoreAway() {
        return scoreAway;
    }

    public void setScoreAway(int scoreAway) {
        this.scoreAway = scoreAway;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }
}
