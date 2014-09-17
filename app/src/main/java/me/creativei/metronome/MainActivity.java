package me.creativei.metronome;

import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

import static me.creativei.metronome.Constants.LOG_TAG;


public class MainActivity extends ActionBarActivity {

    private SoundPool soundPool;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ToggleButton startButton = (ToggleButton) findViewById(R.id.btnStart);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        final int beatSound = soundPool.load(this, R.raw.click, 1);

        timer = new Timer(true);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startButton.isChecked()) {
                    // play beats
                    Log.d(LOG_TAG, "Playing beats");
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            soundPool.play(beatSound, 1, 1, 1, 0, 1);
                        }
                    }, 100, 1000);

                } else {
                    // pause beats
                    Log.d(LOG_TAG, "Pausing beats");
                    timer.cancel();
                }
            }
        });
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
}
