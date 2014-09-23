package me.creativei.metronome;

import android.view.View;
import android.widget.ImageButton;

import me.creativei.metronome.exception.InvisibleBeatPlayedException;

public class BeatFragment implements View.OnClickListener {
    private BeatState state;
    private Callback handler;

    private static enum BeatState {
        ON, HIDDEN, MUTED
    }

    private ImageButton imageButton;

    public BeatFragment(Callback handler, ImageButton imageButton) {
        this.handler = handler;
        this.imageButton = imageButton;
        imageButton.setOnClickListener(this);
        state = BeatState.ON;
    }

    @Override
    public void onClick(View v) {
        if (state == BeatState.ON) {
            imageButton.setImageResource(R.drawable.ic_beats_off);
        } else {
            imageButton.setImageResource(R.drawable.ic_beats_on);
        }
    }

    public void play() {
        if (!isBeatVisible()) throw new InvisibleBeatPlayedException();

        if (state == BeatState.ON) {
            handler.beatPlayed();
        }
        imageButton.setImageResource(R.drawable.ic_beats_on);
    }

    public void fade() {
        imageButton.setImageResource(R.drawable.ic_beats_off);
    }

    public boolean isBeatVisible() {
        return state != BeatState.HIDDEN;
    }

    public void show() {
        state = BeatState.ON;
        imageButton.setVisibility(View.VISIBLE);
    }

    public void hide() {
        state = BeatState.HIDDEN;
        imageButton.setVisibility(View.INVISIBLE);
    }

    public static interface Callback {
        public void beatPlayed();
    }
}
