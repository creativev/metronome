package me.creativei.metronome;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class MainActivity extends ActionBarActivity {
    public static final String PREF_KEEP_SCREEN_ON = "KEEP_SCREEN_ON";
    public static final String TEST_DEVICE = "D27BE559F36AC73AFA3ED3E64322B072";
    private BeatsWidget beatsWidget;
    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isInPortrait()) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            supportRequestWindowFeature(Window.FEATURE_ACTION_BAR);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        setContentView(R.layout.activity_main);

        beatsWidget = new BeatsWidget(this);
        beatsWidget.onCreate();

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(TEST_DEVICE)
                .build();
        AdView adView = (AdView) findViewById(R.id.adView);
        adView.loadAd(adRequest);

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        tracker = analytics.newTracker(R.xml.tracker);
    }

    public boolean isInPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        beatsWidget.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        beatsWidget.onResume();
        tracker.setScreenName("me.creativei.metronome.MainActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    protected void onPause() {
        super.onPause();
        beatsWidget.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        beatsWidget.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem screenAwakeOption = menu.findItem(R.id.menu_screen_awake);
        boolean isScreenWakeOn = getAppStatePref().getBoolean(PREF_KEEP_SCREEN_ON, false);
        setupScreenWakeMenu(screenAwakeOption, isScreenWakeOn);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_screen_awake) {
            boolean wasScreenWakeOn = item.isChecked();
            boolean isScreenWakeOn = !wasScreenWakeOn;
            saveScreenState(isScreenWakeOn);
            setupScreenWakeMenu(item, isScreenWakeOn);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupScreenWakeMenu(MenuItem item, boolean isScreenWakeOn) {
        item.setChecked(isScreenWakeOn);
        item.setIcon(isScreenWakeOn ? R.drawable.ic_screen_on : R.drawable.ic_screen_off);
        if (isScreenWakeOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private SharedPreferences getAppStatePref() {
        return getPreferences(MODE_PRIVATE);
    }

    private void saveScreenState(boolean isChecked) {
        SharedPreferences preferences = getAppStatePref();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_KEEP_SCREEN_ON, isChecked);
        editor.apply();
    }
}
