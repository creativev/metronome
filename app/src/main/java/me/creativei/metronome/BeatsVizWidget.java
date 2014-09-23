package me.creativei.metronome;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.ImageButton;

import me.creativei.metronome.exception.NoBeatVisibleException;

public class BeatsVizWidget implements BeatFragment.Callback {
    private BeatFragment[] beatFragments = new BeatFragment[8];
    private SoundPool soundPool;
    private int tick, tock;

    public BeatsVizWidget(Activity context) {
        for (int i = 0; i < beatFragments.length; i++) {
            beatFragments[i] = new BeatFragment(this, ((ImageButton) context.findViewById(Utils.getResourcesId("btnBeats" + (i + 1)))));
        }
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        tick = soundPool.load(context, R.raw.tick, 1);
        tock = soundPool.load(context, R.raw.tock, 1);
    }

    @Override
    public void tick() {
        soundPool.play(tick, 1, 1, 1, 0, 1);
    }

    @Override
    public void tock() {
        soundPool.play(tock, 1, 1, 1, 0, 1);
    }

    public int nextVisibleBeatIndex(int from) {
        for (int i = 0; i < beatFragments.length; i++) {
            int index = (from + i) % beatFragments.length;
            BeatFragment beatFragment = beatFragments[index];
            if (beatFragment.isBeatVisible()) return index;
        }
        throw new NoBeatVisibleException();
    }

    public void play(int i) {
        beatFragments[i].play();
    }

    public void fade(int i) {
        beatFragments[i].fade();
    }

    public void sync(int numBeats) {
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

    public void onSaveInstanceState(Bundle bundle) {
        for (BeatFragment beatFragment : beatFragments) {
            beatFragment.onSaveInstanceState(bundle);
        }
    }

    public void onRestoreInstanceState(Bundle bundle) {
        for (BeatFragment beatFragment : beatFragments) {
            beatFragment.onRestoreInstanceState(bundle);
        }
    }
}
