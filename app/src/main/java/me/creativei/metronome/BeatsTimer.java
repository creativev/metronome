package me.creativei.metronome;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import static me.creativei.metronome.Constants.LOG_TAG;

public class BeatsTimer extends Fragment {
    private TimerStateTask timerStateTask;
    private Timer timer;
    private int delay;
    private boolean isRunning;
    private long lastRun;
    private Callback callback;

    public BeatsTimer() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (Callback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (callback == null) return;
        timerStateTask = callback.getTimerStateTask();
        delay = callback.getDelay();
    }

    public void start() {
        isRunning = true;
        timer = new Timer(true);
        timer.scheduleAtFixedRate(newTimerTask(), 0, delay);
    }

    public void update(int delay) {
        this.delay = delay;
        Log.d(LOG_TAG, "Current delay is " + delay);
        if (isRunning) {
            if (timer != null)
                timer.cancel();
            long initialDelay = Math.max(0, delay - (System.currentTimeMillis() - lastRun));
            timer = new Timer(true);
            timer.scheduleAtFixedRate(newTimerTask(), (int) initialDelay, delay);
        }
    }

    public void stop() {
        if (isRunning) {
            timer.cancel();
            isRunning = false;
            timerStateTask.runStopTask();
        }
    }

    private TimerTask newTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                lastRun = System.currentTimeMillis();
                timerStateTask.runStartTask();
            }
        };
    }

    public void resume() {
        if (isRunning)
            update(delay);
    }

    public void pause() {
        if (isRunning) {
            timer.cancel();
            timerStateTask.runPauseTask();
        }

    }

    public static interface Callback {
        public TimerStateTask getTimerStateTask();

        int getDelay();
    }
}
