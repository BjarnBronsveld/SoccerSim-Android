package net.bjarn.soccersim.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import net.bjarn.soccersim.R;
import net.bjarn.soccersim.fragments.ResultsFragment;
import net.bjarn.soccersim.fragments.MoreFragment;
import net.bjarn.soccersim.fragments.SimulateFragment;
import net.bjarn.soccersim.objects.Poule;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static net.bjarn.soccersim.Constants.*;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.navigation) BottomNavigationView mNavigation;

    private SharedPreferences mSharedPreferences;
    private Realm mRealm;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_simulate:
                    fragment = SimulateFragment.newInstance();
                    break;
                case R.id.navigation_results:
                    fragment = ResultsFragment.newInstance(false);
                    break;
                case R.id.navigation_more:
                    fragment = MoreFragment.newInstance();
                    break;
            }

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_frame, fragment);
            transaction.commit();

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mRealm = Realm.getDefaultInstance();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_frame, SimulateFragment.newInstance());
        transaction.commit();

        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (mSharedPreferences.getBoolean(PREF_FIRST_RUN, true)) {
            showWelcomeAlert(this);
            return;
        }

        RealmResults<Poule> poules = mRealm.where(Poule.class)
                .findAll();

        if (poules.isEmpty()) {
            showNoPouleAlert(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    /**
     * Shows the user a welcome alert with a button opening {@link CreatePouleActivity}.
     *
     * @param context Context of current activity
     */
    private void showWelcomeAlert(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_welcome);
        builder.setMessage(R.string.dialog_first_run_msg);
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.dialog_create_poule_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSharedPreferences.edit().putBoolean(PREF_FIRST_RUN, false).apply();

                startActivity(new Intent(context, CreatePouleActivity.class));
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Show the user a no poule alert with a button opening {@link CreatePouleActivity}.
     *
     * @param context Context of current activity
     */
    private void showNoPouleAlert(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_no_poule);
        builder.setMessage(R.string.dialog_no_poule_found_msg);
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.dialog_create_poule_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(context, CreatePouleActivity.class));
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
