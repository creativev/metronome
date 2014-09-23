package me.creativei.metronome;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import me.creativei.metronome.exception.InvisibleBeatPlayedException;

public class BeatFragment implements View.OnClickListener {
    private static enum BeatState {
        TICK {
            @Override
            public BeatState next() {
                return TOCK;
            }
        }, TOCK {
            @Override
            public BeatState next() {
                return MUTED;
            }
        }, MUTED {
            @Override
            public BeatState next() {
                return TICK;
            }
        };

        public abstract BeatState next();
    }

    private ImageButton imageButton;
    private BeatState state;
    private boolean hidden;
    private Callback handler;

    public BeatFragment(Callback handler, ImageButton imageButton) {
        this.handler = handler;
        this.imageButton = imageButton;
        this.imageButton.setOnClickListener(this);
        state = BeatState.TOCK;
    }

    @Override
    public void onClick(View v) {
        state = state.next();
        imageButton.setImageResource(Utils.getResource("ic_beat_" + state.toString().toLowerCase(), R.drawable.class));
    }

    public void play() {
        if (!isBeatVisible()) throw new InvisibleBeatPlayedException();

        if (state == BeatState.TICK) {
            handler.tick();
        } else if (state == BeatState.TOCK) {
            handler.tock();
        }
        imageButton.setBackgroundResource(R.drawable.ic_beat_glow);
    }

    public void fade() {
        imageButton.setBackgroundResource(0);
    }

    public boolean isBeatVisible() {
        return !hidden;
    }

    public void show() {
        hidden = false;
        imageButton.setVisibility(View.VISIBLE);
    }

    public void hide() {
        hidden = true;
        imageButton.setVisibility(View.GONE);
    }

    public void onRestoreInstanceState(Bundle bundle) {

    }

    public void onSaveInstanceState(Bundle bundle) {

    }

    public static interface Callback {
        public void tick();

        public void tock();
    }
}
