package me.creativei.metronome;

import android.app.Activity;
import android.os.Bundle;

public class BeatsTimerStateTask implements TimerStateTask {
    public static final String BEATSTIMERTASK_SELECTED = "BEATSTIMERTASK_SELECTED";
    public static final int SELECTED_NONE = -1;

    private int selected = SELECTED_NONE;
    private Activity context;
    private BeatsVizLayout beatsVizLayout;

    public BeatsTimerStateTask(Activity context, BeatsVizLayout beatsVizLayout) {
        this.context = context;
        this.beatsVizLayout = beatsVizLayout;
    }

    @Override
    public void runStartTask() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (selected == SELECTED_NONE) {
                    selected = beatsVizLayout.nextVisibleBeatIndex(0);
                } else {
                    beatsVizLayout.fade(selected);
                    selected = beatsVizLayout.nextVisibleBeatIndex(selected + 1);
                }
                beatsVizLayout.play(selected);
            }
        });
    }

    @Override
    public void runStopTask() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                beatsVizLayout.fade(selected);
                selected = SELECTED_NONE;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt(BEATSTIMERTASK_SELECTED, selected);
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        selected = bundle.getInt(BEATSTIMERTASK_SELECTED, SELECTED_NONE);
    }

    @Override
    public void runPauseTask() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                beatsVizLayout.fade(selected);
            }
        });
    }
}
