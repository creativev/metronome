package me.creativei.metronome;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import me.creativei.metronome.exception.NoBeatVisibleException;

public class BeatsVizWidget implements BeatFragment.Callback {
    private MainActivity context;
    private LinearLayout[] containers;
    private BeatFragment[] beatFragments = new BeatFragment[12];
    private SoundPool soundPool;
    private int tick, tock;
    public int length = beatFragments.length;

    public BeatsVizWidget(MainActivity context) {
        this.context = context;
        for (int i = 0; i < beatFragments.length; i++) {
            beatFragments[i] = new BeatFragment(context, this, ((ImageButton) context.findViewById(Utils.getResourcesId("btnBeats" + (i + 1)))));
        }
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        tick = soundPool.load(context, R.raw.tick, 1);
        tock = soundPool.load(context, R.raw.tock, 1);
        containers = new LinearLayout[]{
                (LinearLayout) context.findViewById(R.id.beatsVizContainer1),
                (LinearLayout) context.findViewById(R.id.beatsVizContainer2),
                (LinearLayout) context.findViewById(R.id.beatsVizContainer3)
        };
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
        animateRows(numBeats);
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

    private void animateRows(int numBeats) {
        double beatsPerRow = context.isInPortrait() ? 4.0 : 6.0;
        int visibleRows = (int) Math.ceil(numBeats / beatsPerRow);
        for (int currentRow = 1; currentRow <= containers.length; currentRow++) {
            LinearLayout container = containers[currentRow - 1];
            if (currentRow <= visibleRows && container.getVisibility() != View.VISIBLE) {
                container.setVisibility(View.VISIBLE);
            } else if (currentRow > visibleRows && container.getVisibility() != View.GONE) {
                container.setVisibility(View.GONE);
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        for (BeatFragment beatFragment : beatFragments) {
            bundle.putParcelable(beatFragment.getId(), beatFragment.onSaveInstanceState());
        }
    }

    public void onRestoreInstanceState(Bundle bundle) {
        for (BeatFragment beatFragment : beatFragments) {
            beatFragment.onRestoreInstanceState(bundle.getBundle(beatFragment.getId()));
        }
    }

    public void restoreFromPref() {
        for (BeatFragment beatFragment : beatFragments) {
            beatFragment.restoreFromPref();
        }
    }
}
