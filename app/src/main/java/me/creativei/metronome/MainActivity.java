package me.creativei.metronome;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;


public class MainActivity extends ActionBarActivity implements BeatFragment.Callback {

    public static final String PREF_KEEP_SCREEN_ON = "KEEP_SCREEN_ON";
    private BeatsWidget beatsWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupScreenOnButton();

        beatsWidget = new BeatsWidget(this);
        beatsWidget.onCreate();

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        AdView adView = (AdView) findViewById(R.id.adView);
        adView.loadAd(adRequest);

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.newTracker(R.xml.tracker);
    }

    @Override
    protected void onResume() {
        super.onResume();
        beatsWidget.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        beatsWidget.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.onPause();
    }

    private void setupScreenOnButton() {
        final ToggleButton screenOn = (ToggleButton) findViewById(R.id.btnScreenOn);
        boolean keepScreenWake = getAppStatePref().getBoolean(PREF_KEEP_SCREEN_ON, false);
        screenOn.setChecked(keepScreenWake);
        screenOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getAppStatePref();
                SharedPreferences.Editor editor = preferences.edit();
                ToggleButton screenOnBtn = (ToggleButton) v;
                editor.putBoolean(PREF_KEEP_SCREEN_ON, screenOnBtn.isChecked());
                editor.apply();

                if (screenOnBtn.isChecked())
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                else
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });
        if (keepScreenWake)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private SharedPreferences getAppStatePref() {
        return getPreferences(MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beatPlayed() {
        beatsWidget.beatPlayed();
    }
}
