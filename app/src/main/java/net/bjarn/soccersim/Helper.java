package net.bjarn.soccersim;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;

import net.bjarn.soccersim.activities.MainActivity;
import net.bjarn.soccersim.objects.Team;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;

import static net.bjarn.soccersim.Constants.PREF_SOUND_ENABLED;

public class Helper {

    /**
     * Show an alert dialog to let the user confirm their choice. This method will delete all data
     * in Realm so the user will be able to create a new poule. This can not be undone.
     *
     * @param realm An instance of Realm
     * @param activity An instance of the current Activity
     */
    public static void resetGame(final Realm realm, final Activity activity) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.dialog_are_you_sure)
                .setMessage(R.string.dialog_are_you_sure_msg);

        dialog.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // Delete all data in Realm.
                        realm.deleteAll();
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        // Deletion in Realm was successful, re-add all teams and restart the app.
                        createTeams(activity);
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                });
            }
        });

        dialog.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Creates {@link Team} objects with desired stats and adds them to the default Realm database.
     *
     * It is possible to add more teams in the future.
     *
     * @param context Required instance of Context
     */
    public static void createTeams(Context context) {
        Realm realm = Realm.getDefaultInstance();

        final List<Team> teams = Arrays.asList(
                new Team(context.getString(R.string.country_nl), context.getString(R.string.flag_nl), 6.0f, 6.9f, 8.9f, 7.9f),
                new Team(context.getString(R.string.country_de), context.getString(R.string.flag_de), 9.0f, 7.6f, 7.2f, 8.9f),
                new Team(context.getString(R.string.country_br), context.getString(R.string.flag_br), 5.6f, 6.5f, 6.2f, 8.8f),
                new Team(context.getString(R.string.country_mx), context.getString(R.string.flag_mx), 8.5f, 7.3f, 6.5f, 5.6f),
                new Team(context.getString(R.string.country_fr), context.getString(R.string.flag_fr), 7.6f, 7.9f, 7.2f, 8.1f),
                new Team(context.getString(R.string.country_it), context.getString(R.string.flag_it), 6.8f, 5.8f, 5.9f, 7.8f),
                new Team(context.getString(R.string.country_be), context.getString(R.string.flag_be), 7.8f, 6.8f, 3.9f, 7.1f),
                new Team(context.getString(R.string.country_us), context.getString(R.string.flag_us), 4.6f, 2.8f, 5.6f, 3.8f),
                new Team(context.getString(R.string.country_ma), context.getString(R.string.flag_ma), 6.1f, 4.7f, 5.8f, 6.9f),
                new Team(context.getString(R.string.country_es), context.getString(R.string.flag_es), 7.1f, 8.3f, 6.4f, 9.1f)
        );

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.insert(teams);
            }
        });
    }

    /**
     * Play a sound effect.
     *
     * @param sound Id of the sound effect
     * @param preferences Instance of SharedPreferences
     * @param context Instance of current Context
     */
    public static void playSoundEffect(int sound, SharedPreferences preferences, Context context) {
        if (preferences.getBoolean(PREF_SOUND_ENABLED, true)) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, sound);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mediaPlayer.start();
        }
    }

}
