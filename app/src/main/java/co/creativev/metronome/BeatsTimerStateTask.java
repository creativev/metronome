package co.creativev.metronome;

import android.app.Activity;

public class BeatsTimerStateTask implements TimerStateTask {
    public static final int SELECTED_NONE = -1;
    private int selected = SELECTED_NONE;
    private Activity context;
    private Callback callback;

    public BeatsTimerStateTask(Activity context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    public synchronized void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void runStartTask() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (selected == SELECTED_NONE) {
                    selected = callback.nextVisibleBeatIndex(0);
                } else {
                    callback.fade(selected);
                    selected = callback.nextVisibleBeatIndex(selected + 1);
                }
                callback.play(selected);
            }
        });
    }

    @Override
    public void runStopTask() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.fade(selected);
                selected = SELECTED_NONE;
            }
        });
    }

    @Override
    public void runPauseTask() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.fade(selected);
            }
        });
    }

    public static interface Callback {
        public int nextVisibleBeatIndex(int from);

        public void play(int i);

        public void fade(int i);
    }
}
