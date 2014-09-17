package me.creativei.metronome;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

import static me.creativei.metronome.Constants.LOG_TAG;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new BeatsWidget(this).start();
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

    private static class BeatsWidget {
        private final ToggleButton startButton;
        private SoundPool soundPool;
        private Timer timer;
        private ImageButton[] beatsButtons = new ImageButton[4];
        private int selected;

        private BeatsWidget(final MainActivity context) {
            beatsButtons[0] = (ImageButton) context.findViewById(R.id.imgBeat1);
            beatsButtons[1] = (ImageButton) context.findViewById(R.id.imgBeat2);
            beatsButtons[2] = (ImageButton) context.findViewById(R.id.imgBeat3);
            beatsButtons[3] = (ImageButton) context.findViewById(R.id.imgBeat4);
            selected = 0;

            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            final int beatSound = soundPool.load(context, R.raw.click, 1);

            startButton = (ToggleButton) context.findViewById(R.id.btnStart);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (startButton.isChecked()) {
                        timer = new Timer(true);
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        soundPool.play(beatSound, 1, 1, 1, 0, 1);
                                        if (selected == -1) {
                                            selected = 0;
                                            beatsButtons[selected].setImageResource(R.drawable.ic_beats_on);
                                        } else {
                                            beatsButtons[selected].setImageResource(R.drawable.ic_beats_off);
                                            selected = (selected + 1) % 4;
                                            beatsButtons[selected].setImageResource(R.drawable.ic_beats_on);
                                        }
                                    }
                                });
                            }
                        }, 0, 1000);

                    } else {
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                beatsButtons[selected].setImageResource(R.drawable.ic_beats_off);
                                selected = -1;
                            }
                        });
                    }
                }
            });

//            Button resetButton = (Button) context.findViewById(R.id.btnReset);
//            resetButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    beatsButtons[selected].setBackgroundResource(R.drawable.ic_beats_off);
//                    selected = 0;
//                    beatsButtons[selected].setBackgroundResource(R.drawable.ic_beats_on);
//                }
//            });
        }

        public void start() {

        }
    }
}
