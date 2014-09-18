package me.creativei.metronome;

import android.media.AudioManager;
import android.media.SoundPool;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import me.creativei.listener.ContinuousLongPressListener;

public class BeatsWidget {
    private final ToggleButton startButton;
    private SoundPool soundPool;
    private ImageButton[] beatsButtons = new ImageButton[4];
    private int selected = -1;
    private TextView txtBpm;
    private MainActivity context;
    private final int beatSound;
    private final Button btnUp;
    private final Button btnDown;

    public BeatsWidget(MainActivity context) {
        this.context = context;

        beatsButtons[0] = (ImageButton) context.findViewById(R.id.imgBeat1);
        beatsButtons[1] = (ImageButton) context.findViewById(R.id.imgBeat2);
        beatsButtons[2] = (ImageButton) context.findViewById(R.id.imgBeat3);
        beatsButtons[3] = (ImageButton) context.findViewById(R.id.imgBeat4);

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

        ContinuousLongPressListener.setListener(btnUp, new View.OnLongClickListener() {
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

        ContinuousLongPressListener.setListener(btnDown, new View.OnLongClickListener() {
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
