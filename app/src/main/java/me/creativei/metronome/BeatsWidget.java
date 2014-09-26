package me.creativei.metronome;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BeatsWidget {
    public static final String PREF_BEATS_PATTERN_VAL = "PREF_BEATS_PATTERN_VAL";
    public static final String PREF_BPM_VAL = "PREF_BPM_VAL";
    private final int DEFAULT_BPM_VAL = 60;
    private final BeatsVizLayout beatsVizLayout;
    private MainActivity context;
    private PlayButton btnStart;
    private BeatsTimer beatsTimer;

    private NumberWidget bpmWidget;
    private NumberWidget beatsPatternWidget;

    public BeatsWidget(MainActivity context) {
        this.context = context;
        beatsVizLayout = (BeatsVizLayout) context.findViewById(R.id.beatsVizContainer);

        if (!context.isInPortrait()) return;

        btnStart = (PlayButton) context.findViewById(R.id.btnStart);

        Typeface fontAwesome = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");
        Typeface fontCabin = Typeface.createFromAsset(context.getAssets(), "fonts/Cabin-Regular.ttf");
        TextView txtBpm = (TextView) context.findViewById(R.id.txtBPM);
        txtBpm.setTypeface(fontCabin);
        Button btnUp = (Button) context.findViewById(R.id.btnBPMUp);
        Button btnDown = (Button) context.findViewById(R.id.btnBPMDown);
        btnUp.setTypeface(fontAwesome);
        btnDown.setTypeface(fontAwesome);
        bpmWidget = new NumberWidget(txtBpm, btnUp, btnDown, true, 10, 300, "%d BPM", new NumberWidget.NumberWidgetValueChangeListener() {
            @Override
            public void valueChanged(int value) {
                updateBpm(value, beatsTimer);
            }
        });

        Button btnNumBeatsUp = (Button) context.findViewById(R.id.btnNumBeatsUp);
        Button btnNumBeatsDown = (Button) context.findViewById(R.id.btnNumBeatsDown);
        btnNumBeatsDown.setTypeface(fontAwesome);
        btnNumBeatsUp.setTypeface(fontAwesome);
        beatsPatternWidget = new NumberWidget(btnNumBeatsUp, btnNumBeatsDown, false, 1,
                beatsVizLayout.length, new NumberWidget.NumberWidgetValueChangeListener() {
            @Override
            public void valueChanged(int value) {
                syncBeatsPatternWidget(value);
            }
        });
    }

    public void onCreate() {
        if (!context.isInPortrait()) {
            beatsTimer = new BeatsTimer(bpmToDelay(savedBpm()), new BeatsTimerStateTask(context, beatsVizLayout));
            syncBeatsPatternWidget(savedBeatsPatternPosition());
            return;
        }
        beatsTimer = new BeatsTimer(bpmToDelay(parseBpm()), new BeatsTimerStateTask(context, beatsVizLayout));
        btnStart.init(beatsTimer);
        // Restore App State from Pref
        beatsPatternWidget.setValue(context.getPreferences(Context.MODE_PRIVATE).getInt(PREF_BEATS_PATTERN_VAL, 4));
        bpmWidget.setValue(context.getPreferences(Context.MODE_PRIVATE).getInt(PREF_BPM_VAL, DEFAULT_BPM_VAL));
//        beatsVizLayout.restoreFromPref();
    }

    private void syncBeatsPatternWidget(int numBeats) {
        saveBeatsPattern(numBeats);
        beatsVizLayout.sync(numBeats);
    }

    public void onResume() {
        beatsTimer.resume();
    }

    public void onPause() {
        beatsTimer.pause();
    }

    public void onSaveInstanceState(Bundle bundle) {
        beatsTimer.onSaveInstanceState(bundle);
    }

    public void onRestoreInstanceState(Bundle bundle) {
        beatsTimer.onRestoreInstanceState(bundle);
        // New layout is in portrait, restore btn state
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
        return context.getPreferences(Context.MODE_PRIVATE).getInt(PREF_BEATS_PATTERN_VAL, 4);
    }

    private void saveBeatsPattern(int position) {
        SharedPreferences appStatePref = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appStatePref.edit();
        editor.putInt(PREF_BEATS_PATTERN_VAL, position);
        editor.apply();
    }

}
