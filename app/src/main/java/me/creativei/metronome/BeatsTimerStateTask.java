package me.creativei.metronome;

import android.app.Activity;

import me.creativei.metronome.exception.NoBeatVisibleException;

public class BeatsTimerStateTask implements TimerStateTask {
    private int selected = -1;
    private Activity context;
    private BeatFragment[] beatFragments;

    public BeatsTimerStateTask(Activity context, BeatFragment[] beatFragments, int selected) {
        this.selected = selected;
        this.context = context;
        this.beatFragments = beatFragments;
    }

    @Override
    public void runStartTask() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (selected == -1) {
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
                selected = -1;
            }
        });
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
