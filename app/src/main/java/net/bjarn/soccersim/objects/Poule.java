package net.bjarn.soccersim.objects;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Poule extends RealmObject {

    @PrimaryKey
    @Required
    private String uuid = UUID.randomUUID().toString();

    private RealmList<Team> participants = new RealmList<>();
    private RealmList<Game> games = new RealmList<>();

    public Poule() {
        // Required empty constructor
    }

    /**
     * Public Poule Constructor
     *
     * @param participants Maximum of 4 teams playing in the poule
     * @param games        All generated games to be or already played
     */
    public Poule(RealmList<Team> participants, RealmList<Game> games) {
        this.participants = participants;
        this.games = games;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public RealmList<Team> getParticipants() {
        return participants;
    }

    public void setParticipants(RealmList<Team> participants) {
        this.participants = participants;
    }

    public RealmList<Game> getGames() {
        return games;
    }

    public void setGames(RealmList<Game> games) {
        this.games = games;
    }
}
