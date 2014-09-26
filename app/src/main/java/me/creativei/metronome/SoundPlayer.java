package me.creativei.metronome;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPlayer {
    private SoundPool soundPool;
    private int tick, tock;

    public SoundPlayer(Context context) {
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        tick = soundPool.load(context, R.raw.tick, 1);
        tock = soundPool.load(context, R.raw.tock, 1);
    }

    public void tick() {
        soundPool.play(tick, 1, 1, 1, 0, 1);
    }

    public void tock() {
        soundPool.play(tock, 1, 1, 1, 0, 1);
    }
}
