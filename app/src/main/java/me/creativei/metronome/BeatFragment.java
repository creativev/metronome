package me.creativei.metronome;

import android.content.Context;
import android.content.SharedPreferences;
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
                return MUTE;
            }
        }, MUTE {
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
    private final MainActivity context;
    private Callback handler;

    public BeatFragment(MainActivity context, Callback handler, ImageButton imageButton) {
        this.context = context;
        this.handler = handler;
        this.imageButton = imageButton;
        this.imageButton.setOnClickListener(this);
        state = BeatState.TICK;
    }

    @Override
    public void onClick(View v) {
        setState(state.next());
    }

    public void play() {
        if (!isBeatVisible()) throw new InvisibleBeatPlayedException();

        if (state == BeatState.TICK) {
            handler.tick();
        } else if (state == BeatState.TOCK) {
            handler.tock();
        }
        updateImageWithState(state, true);
    }

    public void fade() {
        updateImageWithState(state, false);
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

    public void onRestoreInstanceState(Bundle savedState) {
        setState((BeatState) savedState.getParcelable(BEAT_STATE));
        setInvisible(savedState.getBoolean(BEAT_HIDDEN));
    }

    public Bundle onSaveInstanceState() {
        Bundle toBeSaved = new Bundle();
        toBeSaved.putParcelable(BEAT_STATE, state);
        toBeSaved.putBoolean(BEAT_HIDDEN, hidden);
        return toBeSaved;
    }

    private void updateImageWithState(BeatState state, boolean glow) {
        String resourceId = "beat_" + state.toString().toLowerCase();
        resourceId = glow ? resourceId + "_glow" : resourceId;
        imageButton.setImageResource(Utils.getResource(resourceId, R.drawable.class));
    }

    private void setInvisible(boolean val) {
        hidden = val;
        imageButton.setVisibility(val ? View.GONE : View.VISIBLE);
    }

    public String getId() {
        return Integer.toString(imageButton.getId());
    }

    public void restoreFromPref() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(("BEATFRAGMENT_" + imageButton.getId()), Context.MODE_PRIVATE);
        setState(BeatState.values()[sharedPreferences.getInt("STATE", 0)]);
        updateImageWithState(state, false);
    }

    private void setState(BeatState state) {
        this.state = state;
        SharedPreferences sharedPreferences = context.getSharedPreferences(("BEATFRAGMENT_" + imageButton.getId()), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("STATE", state.ordinal());
        editor.apply();
        updateImageWithState(state, false);
    }

    public static interface Callback {
        public void tick();

        public void tock();
    }
}
