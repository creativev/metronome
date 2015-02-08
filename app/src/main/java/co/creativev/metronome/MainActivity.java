package co.creativev.metronome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.flurry.android.FlurryAgent;
import com.inmobi.commons.InMobi;
import com.inmobi.monetization.IMBanner;


public class MainActivity extends ActionBarActivity implements BeatsTimerStateTask.Callback, BpmFragment.Callback, NumBeatFragment.Callback {
    public static final String PREF_KEEP_SCREEN_ON = "KEEP_SCREEN_ON";
    public static final String TEST_DEVICE = "D27BE559F36AC73AFA3ED3E64322B072";
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private BeatsVizLayout beatsVizLayout;
    private BeatsTimer beatsTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isInPortrait()) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_main);

        beatsVizLayout = (BeatsVizLayout) findViewById(R.id.beatsVizContainer);
        FragmentManager fragmentManager = getSupportFragmentManager();
        beatsTimer = (BeatsTimer) fragmentManager.findFragmentByTag(TAG_TASK_FRAGMENT);
        if (beatsTimer == null) {
            beatsTimer = new BeatsTimer();
            fragmentManager.beginTransaction().add(beatsTimer, TAG_TASK_FRAGMENT).commit();
        }

        PlayButton btnStart = (PlayButton) findViewById(R.id.btnStart);
        btnStart.init(beatsTimer);

        InMobi.initialize(this, getString(R.string.inmobi_id));
        IMBanner adView = (IMBanner) findViewById(R.id.adView);
        adView.loadBanner();
        FlurryAgent.init(this, getString(R.string.flurry_analytics_id));
    }

    public boolean isInPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        beatsTimer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FlurryAgent.onStartSession(this);
        beatsTimer.resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isInPortrait()) return true;

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
        if (id == R.id.menu_other_apps) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.play_publisher_url)));
            startActivity(intent);
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

    @Override
    public void bpmChanged(int value) {
        beatsTimer.update(bpmToDelay(value));
    }

    @Override
    public void numBeatsChanged(int value) {
        beatsVizLayout.sync(value);
    }

    private int bpmToDelay(int finalBpm) {
        return (int) (60.0 * 1000.0 / finalBpm);
    }

    @Override
    public int nextVisibleBeatIndex(int from) {
        return beatsVizLayout.nextVisibleBeatIndex(from);
    }

    @Override
    public void play(int i) {
        beatsVizLayout.play(i);
    }

    @Override
    public void fade(int i) {
        beatsVizLayout.fade(i);
    }
}
