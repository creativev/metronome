package me.creativei.metronome;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.lang.reflect.Field;

public class BeatsWidget {
    public static final String PREF_BEATS_PATTERN_VAL = "PREF_BEATS_PATTERN_VAL";
    public static final String PREF_BPM_VAL = "PREF_BPM_VAL";
    private final int DEFAULT_BPM_VAL = 60;
    private MainActivity context;
    private ToggleButton btnStart;
    private BeatFragment[] beatFragments = new BeatFragment[8];
    private SoundPool soundPool;
    private int beatSound;
    private BeatsTimer beatsTimer;

    private NumberWidget bpmWidget;
    private NumberWidget beatsPatternWidget;

    public BeatsWidget(MainActivity context) {
        this.context = context;

        for (int i = 0; i < beatFragments.length; i++) {
            beatFragments[i] = new BeatFragment(context, ((ImageButton) context.findViewById(getResourcesId("btnBeats" + (i + 1)))));
        }
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        beatSound = soundPool.load(context, R.raw.click, 1);

        if (context.isInPortrait()) {
            btnStart = (ToggleButton) context.findViewById(R.id.btnStart);
            TextView txtBpm = (TextView) context.findViewById(R.id.txtBPM);
            Button btnUp = (Button) context.findViewById(R.id.btnBPMUp);
            Button btnDown = (Button) context.findViewById(R.id.btnBPMDown);
            bpmWidget = new NumberWidget(txtBpm, btnUp, btnDown, true, 10, 300, "%d BPM", new NumberWidget.NumberWidgetValueChangeListener() {
                @Override
                public void valueChanged(int value) {
                    updateBpm(value, beatsTimer);
                }
            });

            TextView txtNumBeats = (TextView) context.findViewById(R.id.txtNumBeats);
            Button btnNumBeatsUp = (Button) context.findViewById(R.id.btnNumBeatsUp);
            Button btnNumBeatsDown = (Button) context.findViewById(R.id.btnNumBeatsDown);
            beatsPatternWidget = new NumberWidget(txtNumBeats, btnNumBeatsUp, btnNumBeatsDown,
                    false, 1, 8, "%d",
                    new NumberWidget.NumberWidgetValueChangeListener() {
                        @Override
                        public void valueChanged(int value) {
                            syncBeatsPatternWidget(value);
                        }
                    });
        }
    }

    public void onCreate() {
        if (!context.isInPortrait()) {
            beatsTimer = new BeatsTimer(bpmToDelay(savedBpm()), new BeatsTimerStateTask(context, beatFragments));
            syncBeatsPatternWidget(savedBeatsPatternPosition());
            return;
        }
        beatsTimer = new BeatsTimer(bpmToDelay(parseBpm()), new BeatsTimerStateTask(context, beatFragments));
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

        // Restore App State from Pref
        beatsPatternWidget.setValue(context.getPreferences(Context.MODE_PRIVATE).getInt(PREF_BEATS_PATTERN_VAL, 0));
        bpmWidget.setValue(context.getPreferences(Context.MODE_PRIVATE).getInt(PREF_BPM_VAL, DEFAULT_BPM_VAL));
    }

    private void syncBeatsPatternWidget(int numBeats) {
        saveBeatsPattern(numBeats);
        for (int i = 0; i < beatFragments.length; i++) {
            BeatFragment beatFragment = beatFragments[i];
            if (i < numBeats) {
                if (!beatFragment.isBeatVisible()) {
                    beatFragment.show();
                }
            } else {
                beatFragment.hide();
            }
        }
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

    public void onSaveInstanceState(Bundle bundle) {
        Log.d(Constants.LOG_TAG, "onSave: Layout orientation portrait:" + context.isInPortrait());
        beatsTimer.onSaveInstanceState(bundle);
    }

    public void onRestoreInstanceState(Bundle bundle) {
        Log.d(Constants.LOG_TAG, "onRestore: Layout orientation portrait:" + context.isInPortrait());
        // New layout is in portrait, restore btn state
        beatsTimer.onRestoreInstanceState(bundle);
        if (context.isInPortrait())
            btnStart.setChecked(beatsTimer.isRunning());
    }

    private int parseBpm() {
        return bpmWidget.getValue();
    }

    private void updateBpm(int finalBpm, BeatsTimer beatsTimer) {
        saveBpm(finalBpm);
        beatsTimer.update(bpmToDelay(finalBpm));
    }

    private int bpmToDelay(int finalBpm) {
        return (int) (60.0 * 1000.0 / finalBpm);
    }

    private int savedBpm() {
        return context.getPreferences(Context.MODE_PRIVATE).getInt(PREF_BPM_VAL, DEFAULT_BPM_VAL);
    }

    private void saveBpm(int finalBpm) {
        SharedPreferences.Editor editor = context.getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt(PREF_BPM_VAL, finalBpm);
        editor.apply();
    }

    private int savedBeatsPatternPosition() {
        return context.getPreferences(Context.MODE_PRIVATE).getInt(PREF_BEATS_PATTERN_VAL, 0);
    }

    private void saveBeatsPattern(int position) {
        SharedPreferences appStatePref = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appStatePref.edit();
        editor.putInt(PREF_BEATS_PATTERN_VAL, position);
        editor.apply();
    }

    private int getResourcesId(String resourceId) {
        try {
            Field idField = R.id.class.getDeclaredField(resourceId);
            return idField.getInt(idField);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
