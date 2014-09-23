package me.creativei.metronome;

import android.app.Activity;
import android.os.Bundle;

import me.creativei.metronome.exception.NoBeatVisibleException;

public class BeatsTimerStateTask implements TimerStateTask {
    public static final String BEATSTIMERTASK_SELECTED = "BEATSTIMERTASK_SELECTED";
    public static final int SELECTED_NONE = -1;

    private int selected = SELECTED_NONE;
    private Activity context;
    private BeatFragment[] beatFragments;

    public BeatsTimerStateTask(Activity context, BeatFragment[] beatFragments) {
        this.context = context;
        this.beatFragments = beatFragments;
    }

    @Override
    public void runStartTask() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (selected == SELECTED_NONE) {
                    selected = nextVisibleBeatIndex(beatFragments, 0);
                } else {
                    beatFragments[selected].fade();
                    selected = nextVisibleBeatIndex(beatFragments, selected + 1);
                }
                beatFragments[selected].play();
            }
        });
    }

    @Override
    public void runStopTask() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                beatFragments[selected].fade();
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
                beatFragments[selected].fade();
            }
        });
    }

    private int nextVisibleBeatIndex(BeatFragment[] beatFragments1, int from) {
        for (int i = 0; i < beatFragments1.length; i++) {
            int index = (from + i) % beatFragments1.length;
            BeatFragment beatFragment = beatFragments1[index];
            if (beatFragment.isBeatVisible()) return index;
        }
        throw new NoBeatVisibleException();
    }
}
