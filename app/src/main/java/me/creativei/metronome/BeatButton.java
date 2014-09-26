package me.creativei.metronome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

public class BeatButton extends ImageButton implements View.OnClickListener {

    public static final String PREF_BEATSBUTTON = "BEATSBUTTON_PREF";
    public static final String PREF_BEATBUTTON_PREFIX = "BEATSBUTTON_INDEX_";

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

    private BeatState state;
    private Context context;
    private SoundPlayer soundPlayer;

    public BeatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void init(final Context context, final int i, SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(state.next());
                SharedPreferences.Editor editor = context.getSharedPreferences(PREF_BEATSBUTTON, Context.MODE_PRIVATE).edit();
                editor.putInt(PREF_BEATBUTTON_PREFIX + i, state.ordinal());
                editor.apply();
            }
        });
        setState(BeatState.values()[context.getSharedPreferences(PREF_BEATSBUTTON, Context.MODE_PRIVATE).getInt(PREF_BEATBUTTON_PREFIX + i, 0)]);
    }

    @Override
    public void onClick(View v) {

    }

    public void play() {
        if (state == BeatState.TICK) {
            soundPlayer.tick();
        } else if (state == BeatState.TOCK) {
            soundPlayer.tock();
        }
        updateImageWithState(state, true);
    }

    public void fade() {
        updateImageWithState(state, false);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable parcelable) {
        SavedState beatState = (SavedState) parcelable;
        super.onRestoreInstanceState(beatState.getSuperState());
        setState(beatState.state);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        return new SavedState(parcelable, state);
    }

    private static class SavedState extends BaseSavedState {
        public final BeatState state;

        public SavedState(Parcel source) {
            super(source);
            state = source.readParcelable(null);
        }

        public SavedState(Parcelable superState, BeatState state) {
            super(superState);
            this.state = state;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeParcelable(state, flags);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private void updateImageWithState(BeatState state, boolean glow) {
        String resourceId = "beat_" + state.toString().toLowerCase();
        resourceId = glow ? resourceId + "_glow" : resourceId;
        setImageResource(Utils.getResource(resourceId, R.drawable.class));
    }

    private void setState(BeatState state) {
        this.state = state;
        SharedPreferences sharedPreferences = context.getSharedPreferences(("BEATFRAGMENT_" + getId()), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("STATE", state.ordinal());
        editor.apply();
        updateImageWithState(state, false);
    }
}
