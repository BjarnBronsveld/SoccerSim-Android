package net.bjarn.soccersim;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.realm.Realm;

import static net.bjarn.soccersim.Constants.PREF_FIRST_RUN;

public class SoccerSim extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean(PREF_FIRST_RUN, true)) {
            Helper.createTeams(getApplicationContext());
        }
    }

}
