package co.creativev.metronome;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NumBeatFragment extends Fragment {
    public static final String PREF_NUMBEAT = "NUMBEATFRAGMENT";
    public static final String PREF_VALUE = "NUMBEATFRAGMENT_VALUE";
    public static final int DEF_VALUE = 4;
    private Activity activity;
    private Callback callback;
    private NumberWidget numBeatsWidget;

    public NumBeatFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        callback = ((Callback) activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.num_beat_fragment, container, false);

        Typeface fontAwesome = Typeface.createFromAsset(activity.getAssets(), "fonts/fontawesome-webfont.ttf");
        Button btnNumBeatsUp = (Button) view.findViewById(R.id.btnNumBeatsUp);
        Button btnNumBeatsDown = (Button) view.findViewById(R.id.btnNumBeatsDown);
        btnNumBeatsDown.setTypeface(fontAwesome);
        btnNumBeatsUp.setTypeface(fontAwesome);
        numBeatsWidget = new NumberWidget(btnNumBeatsUp, btnNumBeatsDown, false, 1,
                12, new NumberWidget.NumberWidgetValueChangeListener() {
            @Override
            public void valueChanged(int value) {
                SharedPreferences.Editor editor = activity.getSharedPreferences(PREF_NUMBEAT, Context.MODE_PRIVATE).edit();
                editor.putInt(PREF_VALUE, value);
                editor.apply();

                callback.numBeatsChanged(value);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        numBeatsWidget.setValue(activity.getSharedPreferences(PREF_NUMBEAT, Context.MODE_PRIVATE).getInt(PREF_VALUE, DEF_VALUE));
    }

    public static interface Callback {
        public void numBeatsChanged(int value);
    }
}
