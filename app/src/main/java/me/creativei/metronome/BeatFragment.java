package me.creativei.metronome;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageButton;

import me.creativei.metronome.exception.InvisibleBeatPlayedException;

public class BeatFragment implements View.OnClickListener {

    public static final String BEAT_STATE = "BEAT_STATE";
    public static final String BEAT_HIDDEN = "BEAT_HIDDEN";

    private static enum BeatState implements Parcelable {
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.ordinal());
        }

        public static final Parcelable.Creator<BeatState> CREATOR = new Creator<BeatState>() {
            @Override
            public BeatState createFromParcel(Parcel source) {
                int index = source.readInt();
                return BeatState.values()[index];
            }

            @Override
            public BeatState[] newArray(int size) {
                return new BeatState[size];
            }
        };
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
        updateImageButton(state.next());
    }

    public void play() {
        if (!isBeatVisible()) throw new InvisibleBeatPlayedException();

        if (state == BeatState.TICK) {
            handler.tick();
        } else if (state == BeatState.TOCK) {
            handler.tock();
        }
        imageButton.setImageResource(R.drawable.beat_glow);
    }

    public void fade() {
        imageButton.setImageResource(R.drawable.beat);
    }

    public boolean isBeatVisible() {
        return !hidden;
    }

    public void show() {
        setInvisible(false);
    }

    public void hide() {
        setInvisible(true);
    }

    public void onRestoreInstanceState(Bundle bundle) {
        Bundle savedState = bundle.getBundle(Integer.toString(imageButton.getId()));
        updateImageButton((BeatState) savedState.getParcelable(BEAT_STATE));
        setInvisible(savedState.getBoolean(BEAT_HIDDEN));
    }

    public void onSaveInstanceState(Bundle bundle) {
        Bundle toBeSaved = new Bundle();
        toBeSaved.putParcelable(BEAT_STATE, state);
        toBeSaved.putBoolean(BEAT_HIDDEN, hidden);
        bundle.putParcelable(Integer.toString(imageButton.getId()), toBeSaved);
    }

    private void updateImageButton(BeatState state) {
        this.state = state;
        imageButton.setImageResource(Utils.getResource("ic_beat_" + state.toString().toLowerCase(), R.drawable.class));
    }

    private void setInvisible(boolean val) {
        hidden = val;
        imageButton.setVisibility(val ? View.GONE : View.VISIBLE);
    }

    public static interface Callback {
        public void tick();

        public void tock();
    }
}
