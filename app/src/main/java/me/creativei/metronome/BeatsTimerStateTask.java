package me.creativei.metronome;

import android.app.Activity;
import android.os.Bundle;

public class BeatsTimerStateTask implements TimerStateTask {
    public static final String BEATSTIMERTASK_SELECTED = "BEATSTIMERTASK_SELECTED";
    public static final int SELECTED_NONE = -1;

    private int selected = SELECTED_NONE;
    private Activity context;
    private BeatsVizWidget beatsVizWidget;

    public BeatsTimerStateTask(Activity context, BeatsVizWidget beatsVizWidget) {
        this.context = context;
        this.beatsVizWidget = beatsVizWidget;
    }

    @Override
    public void runStartTask() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (selected == SELECTED_NONE) {
                    selected = beatsVizWidget.nextVisibleBeatIndex(0);
                } else {
                    beatsVizWidget.fade(selected);
                    selected = beatsVizWidget.nextVisibleBeatIndex(selected + 1);
                }
                beatsVizWidget.play(selected);
            }
        });
    }

    @Override
    public void runStopTask() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                beatsVizWidget.fade(selected);
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
                beatsVizWidget.fade(selected);
            }
        });
    }
}
