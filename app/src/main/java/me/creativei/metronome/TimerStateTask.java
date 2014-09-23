package me.creativei.metronome;

import android.os.Bundle;

public interface TimerStateTask {
    void runStartTask();

    void runStopTask();

    void runPauseTask();

    void onSaveInstanceState(Bundle bundle);

    void onRestoreInstanceState(Bundle bundle);
}
