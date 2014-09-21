package me.creativei.metronome;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import me.creativei.listener.ContinuousLongClickListener;

public class BeatsWidget {
    public static final String PREF_BEATS_PATTERN_VAL = "PREF_BEATS_PATTERN_VAL";
    public static final String PREF_BPM_VAL = "PREF_BPM_VAL";
    private MainActivity context;
    private final ToggleButton btnStart;
    private final Spinner beatsPatternOptions;
    private final String[] beatsPattern;
    private BeatFragment[] beatFragments = new BeatFragment[4];
    private int selected = -1;
    private TextView txtBpm;
    private final Button btnUp;
    private final Button btnDown;
    private SoundPool soundPool;
    private int beatSound;
    private BeatsTimer beatsTimer;

    public BeatsWidget(MainActivity context) {
        this.context = context;

        beatsPattern = context.getResources().getStringArray(R.array.beatsPattern);
        for (int i = 0; i < beatFragments.length; i++) {
            beatFragments[i] = new BeatFragment();
        }

        btnStart = (ToggleButton) context.findViewById(R.id.btnStart);
        txtBpm = (TextView) context.findViewById(R.id.txtBPM);
        btnUp = (Button) context.findViewById(R.id.btnBPMUp);
        btnDown = (Button) context.findViewById(R.id.btnBPMDown);
        beatsPatternOptions = (Spinner) context.findViewById(R.id.optBeatsPattern);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        beatSound = soundPool.load(context, R.raw.click, 1);
    }

    public void onCreate() {
        FragmentTransaction fragmentTransaction = context.getSupportFragmentManager().beginTransaction();
        for (BeatFragment beatFragment : beatFragments) {
            fragmentTransaction.add(R.id.beatsPatternContainer, beatFragment);
        }
        fragmentTransaction.commit();

        beatsTimer = new BeatsTimer(bpmToDelay(parseBpm()), new BeatsTimerStateTask(context, beatFragments, -1));

        beatsPatternOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveBeatsPatternState(position);
                String selectedPattern = beatsPattern[position];
                int numberOfBeats = Integer.parseInt(selectedPattern.split("/")[0]);

                FragmentTransaction fragmentTransaction1 = context.getSupportFragmentManager().beginTransaction();
                for (int i = 0; i < beatFragments.length; i++) {
                    BeatFragment beatFragment = beatFragments[i];
                    if (i < numberOfBeats) {
                        if (!beatFragment.isBeatVisible()) {
                            beatFragment.show();
                            fragmentTransaction1.add(R.id.beatsPatternContainer, beatFragment);
                        }
                    } else {
                        beatFragment.hide();
                        fragmentTransaction1.remove(beatFragment);
                    }
                }
                fragmentTransaction1.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnStart.isChecked()) {
                    beatsTimer.start();
                } else {
                    beatsTimer.stop();
                }
            }
        });

        ContinuousLongClickListener.setListener(btnUp, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int initialBpm = parseBpm();
                int finalBpm = initialBpm - (initialBpm % 10) + 10;
                updateBpm(finalBpm, beatsTimer);
                return true;
            }
        });


        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int initialBpm = parseBpm();
                int finalBpm = initialBpm + 1;
                updateBpm(finalBpm, beatsTimer);
            }
        });

        ContinuousLongClickListener.setListener(btnDown, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int initialBpm = parseBpm();
                int sub = initialBpm % 10 == 0 ? 10 : initialBpm % 10;
                int finalBpm = initialBpm - sub;
                updateBpm(finalBpm, beatsTimer);
                return true;
            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int initialBpm = parseBpm();
                int finalBpm = initialBpm - 1;
                updateBpm(finalBpm, beatsTimer);
            }
        });

        // Restore App State from Pref
        beatsPatternOptions.setSelection(context.getPreferences(Context.MODE_PRIVATE).getInt(PREF_BEATS_PATTERN_VAL, 0));
        updateBpm(context.getPreferences(Context.MODE_PRIVATE).getInt(PREF_BPM_VAL, 60), beatsTimer);
    }


    private void updateBpm(int finalBpm, BeatsTimer beatsTimer) {
        SharedPreferences.Editor editor = context.getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt(PREF_BPM_VAL, finalBpm);
        editor.apply();

        txtBpm.setText(finalBpm + " BPM");
        beatsTimer.update(bpmToDelay(finalBpm));
    }

    private void saveBeatsPatternState(int position) {
        SharedPreferences appStatePref = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appStatePref.edit();
        editor.putInt(PREF_BEATS_PATTERN_VAL, position);
        editor.apply();
    }

    private int bpmToDelay(int finalBpm) {
        return (int) (60.0 * 1000.0 / finalBpm);
    }

    private int parseBpm() {
        return Integer.parseInt(txtBpm.getText().toString().split(" ")[0]);
    }

    public void beatPlayed() {
        soundPool.play(beatSound, 1, 1, 1, 0, 1);
    }

    public void onResume() {
        beatsTimer.resume();
    }

    public void onPause() {
        beatsTimer.pause();
    }
}
