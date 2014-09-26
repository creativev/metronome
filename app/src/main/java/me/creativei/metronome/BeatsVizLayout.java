package me.creativei.metronome;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import me.creativei.metronome.exception.NoBeatVisibleException;

import static me.creativei.metronome.Utils.isInPortrait;

public class BeatsVizLayout extends LinearLayout {
    private Context context;
    private LinearLayout[] containers;
    private BeatButton[] beatButtons = new BeatButton[12];
    public int length = beatButtons.length;
    private int visible = 0;

    public BeatsVizLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        SoundPlayer soundPlayer = new SoundPlayer(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        for (int i = 0; i < beatButtons.length; i++) {
            beatButtons[i] = (BeatButton) layoutInflater.inflate(R.layout.beat_button, null);
            beatButtons[i].init(soundPlayer);
        }
        containers = new LinearLayout[numRows()];
        for (int i = 0; i < numRows(); i++) {
            containers[i] = (LinearLayout) layoutInflater.inflate(R.layout.beat_container, null);
        }
    }

    public int nextVisibleBeatIndex(int from) {
        if (visible == 0)
            throw new NoBeatVisibleException();
        if (from < visible) return from;
        return 0;
    }

    public void play(int i) {
        beatButtons[i].play();
    }

    public void fade(int i) {
        beatButtons[i].fade();
    }

    public void sync(int numBeats) {
        if (numBeats < visible)
            while (numBeats != visible)
                remove();
        else
            while (numBeats != visible)
                add();
    }

    private void add() {
        addContainersIfRequired();
        int containerToAdd = visible / numCols();
        containers[containerToAdd].addView(beatButtons[visible]);
        visible++;
    }

    private void remove() {
        int toRemoveAtIndex = visible - 1;
        int containerToRemove = toRemoveAtIndex / numCols();
        containers[containerToRemove].removeView(beatButtons[toRemoveAtIndex]);
        visible--;
        removeContainersIfRequired();
    }

    private void removeContainersIfRequired() {
        if (visible % numCols() == 0) {
            int containerToRemove = visible / numCols();
            removeView(containers[containerToRemove]);
        }
    }

    private void addContainersIfRequired() {
        if (visible % numCols() == 0) {
            int containerToAdd = visible / numCols();
            addView(containers[containerToAdd]);
        }
    }

    private int numRows() {
        return isInPortrait(context) ? 3 : 2;
    }

    private int numCols() {
        return isInPortrait(context) ? 4 : 6;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        Parcelable[] beatStates = new Parcelable[beatButtons.length];
        for (int i = 0; i < beatButtons.length; i++) {
            beatStates[i] = beatButtons[i].onSaveInstanceState();
        }
        return new SavedState(parcelable, beatStates);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        for (int i = 0; i < beatButtons.length; i++) {
            BeatButton beatButton = beatButtons[i];
            beatButton.onRestoreInstanceState(savedState.beatStates[i]);
        }
    }

    private static class SavedState extends BaseSavedState {
        public final Parcelable[] beatStates;

        private SavedState(Parcel source) {
            super(source);
            beatStates = source.readParcelableArray(null);
        }

        public SavedState(Parcelable superState, Parcelable[] beatStates) {
            super(superState);
            this.beatStates = beatStates;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeParcelableArray(beatStates, flags);
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
}
