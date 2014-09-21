package me.creativei.metronome;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import me.creativei.metronome.exception.InvisibleBeatPlayedException;

public class BeatFragment extends Fragment implements View.OnClickListener {
    private BeatState state;
    private Callback handler;

    private static enum BeatState {
        ON, HIDDEN, MUTED
    }

    private ImageButton imageButton;

    public BeatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        handler = (Callback) activity;
        imageButton = new ImageButton(activity);
        imageButton.setImageResource(R.drawable.ic_beats_on);
        imageButton.setOnClickListener(this);
        state = BeatState.ON;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return imageButton;
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
    }

    public void hide() {
        state = BeatState.HIDDEN;
    }

    public static interface Callback {
        public void beatPlayed();
    }
}
