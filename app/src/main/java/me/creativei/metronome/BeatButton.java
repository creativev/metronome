package me.creativei.metronome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

public class BeatButton extends ImageButton implements View.OnClickListener {

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

    private BeatState state;
    private Context context;
    private SoundPlayer soundPlayer;

    public BeatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOnClickListener(this);
        state = BeatState.TICK;
    }

    public void init(SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

    @Override
    public void onClick(View v) {
        setState(state.next());
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

    public void onRestoreInstanceState(Bundle savedState) {
        setState((BeatState) savedState.getParcelable(BEAT_STATE));
    }

    public Bundle onSaveInstanceState() {
        Bundle toBeSaved = new Bundle();
        toBeSaved.putParcelable(BEAT_STATE, state);
        return toBeSaved;
    }

    private void updateImageWithState(BeatState state, boolean glow) {
        String resourceId = "beat_" + state.toString().toLowerCase();
        resourceId = glow ? resourceId + "_glow" : resourceId;
        setImageResource(Utils.getResource(resourceId, R.drawable.class));
    }

    public void restoreFromPref() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(("BEATFRAGMENT_" + getId()), Context.MODE_PRIVATE);
        setState(BeatState.values()[sharedPreferences.getInt("STATE", 0)]);
        updateImageWithState(state, false);
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
