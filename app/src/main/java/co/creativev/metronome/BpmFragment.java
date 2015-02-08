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
import android.widget.TextView;

public class BpmFragment extends Fragment {

    public static final String PREF_CONSTANT = "BPMFRAGMENT";
    public static final String PREF_VALUE = "BPMFRAGMENT_VALUE";
    public static final int DEF_VALUE = 60;
    private Activity activity;
    private BpmFragment.Callback callback;
    private NumberWidget numberWidget;

    public BpmFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        callback = (Callback) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bpm_fragment, container);
        Typeface fontCabin = Typeface.createFromAsset(activity.getAssets(), "fonts/Cabin-Regular.ttf");
        Typeface fontAwesome = Typeface.createFromAsset(activity.getAssets(), "fonts/fontawesome-webfont.ttf");
        TextView txtBpm = (TextView) view.findViewById(R.id.txtBPM);
        txtBpm.setTypeface(fontCabin);
        Button btnUp = (Button) view.findViewById(R.id.btnBPMUp);
        Button btnDown = (Button) view.findViewById(R.id.btnBPMDown);
        btnUp.setTypeface(fontAwesome);
        btnDown.setTypeface(fontAwesome);

        numberWidget = new NumberWidget(txtBpm, btnUp, btnDown, true, 10, 300, "%d BPM", new NumberWidget.NumberWidgetValueChangeListener() {
            @Override
            public void valueChanged(int value) {
                SharedPreferences bpmfragment = activity.getSharedPreferences(PREF_CONSTANT, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = bpmfragment.edit();
                editor.putInt(PREF_VALUE, value);
                editor.apply();

                callback.bpmChanged(value);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        numberWidget.setValue(activity.getSharedPreferences(PREF_CONSTANT, Context.MODE_PRIVATE).getInt(PREF_VALUE, DEF_VALUE));
    }

    public int getBpm() {
        return numberWidget.getValue();
    }

    public static interface Callback {
        public void bpmChanged(int value);
    }
}
