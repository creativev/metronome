package me.creativei.metronome;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;


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
        private TextView txtBpm;
        private MainActivity context;
        private final int beatSound;
        private final Button btnUp;
        private final Button btnDown;

        private BeatsWidget(final MainActivity context) {
            this.context = context;

            beatsButtons[0] = (ImageButton) context.findViewById(R.id.imgBeat1);
            beatsButtons[1] = (ImageButton) context.findViewById(R.id.imgBeat2);
            beatsButtons[2] = (ImageButton) context.findViewById(R.id.imgBeat3);
            beatsButtons[3] = (ImageButton) context.findViewById(R.id.imgBeat4);
            selected = 0;

            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            beatSound = soundPool.load(context, R.raw.click, 1);

            startButton = (ToggleButton) context.findViewById(R.id.btnStart);
            txtBpm = (TextView) context.findViewById(R.id.txtBPM);
            btnUp = (Button) context.findViewById(R.id.btnBPMUp);
            btnDown = (Button) context.findViewById(R.id.btnBPMDown);
        }

        public void start() {
            final BeatsTimer beatsTimer = new BeatsTimer(bpmToDelay(parseBpm()), new Runnable() {
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
            }, new Runnable() {
                @Override
                public void run() {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            beatsButtons[selected].setImageResource(R.drawable.ic_beats_off);
                            selected = -1;
                        }
                    });
                }
            });

            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (startButton.isChecked()) {
                        beatsTimer.start();
                    } else {
                        beatsTimer.stop();
                    }
                }
            });
            btnUp.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int initialBpm = parseBpm();
                    int finalBpm = initialBpm - (initialBpm % 10) + 10;
                    txtBpm.setText(finalBpm + " BPM");
                    beatsTimer.update(bpmToDelay(finalBpm));
                    return true;
                }
            });
            btnUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int initialBpm = parseBpm();
                    int finalBpm = initialBpm + 1;
                    txtBpm.setText(finalBpm + " BPM");
                    beatsTimer.update(bpmToDelay(finalBpm));
                }
            });

            btnDown.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int initialBpm = parseBpm();
                    int sub = initialBpm % 10 == 0 ? 10 : initialBpm % 10;
                    int finalBpm = initialBpm - sub;
                    txtBpm.setText(finalBpm + " BPM");
                    beatsTimer.update(bpmToDelay(finalBpm));
                    return true;
                }
            });
            btnDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int initialBpm = parseBpm();
                    int finalBpm = initialBpm - 1;
                    txtBpm.setText(finalBpm + " BPM");
                    beatsTimer.update(bpmToDelay(finalBpm));
                }
            });
        }

        private int bpmToDelay(int finalBpm) {
            return (int) (60.0 * 1000.0 / finalBpm);
        }

        private int parseBpm() {
            return Integer.parseInt(txtBpm.getText().toString().split(" ")[0]);
        }
    }

    public static class BeatsTimer {
        private int delay;
        private Runnable onStartTask;
        private Runnable onStopTask;
        private Timer timer;
        private boolean isRunning;

        public BeatsTimer(int delay, Runnable onStartTask, Runnable onStopTask) {
            this.delay = delay;
            this.onStartTask = onStartTask;
            this.onStopTask = onStopTask;
        }

        public void start() {
            isRunning = true;
            timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    onStartTask.run();
                }
            }, 0, delay);
        }

        public void update(int delay) {
            this.delay = delay;
            Log.d(Constants.LOG_TAG, "Delay is " + delay);
            if (isRunning) {
                stop();
                start();
            }
        }

        public void stop() {
            timer.cancel();
            isRunning = false;
            onStopTask.run();
        }
    }
}
