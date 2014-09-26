package me.creativei.metronome;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ToggleButton;

public class PlayButton extends ToggleButton {
    public PlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface fontAwesome = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");
        setTypeface(fontAwesome);
        setTextColor(context.getResources().getColor(R.color.green_dark));
    }

    public void init(final BeatsTimer beatsTimer) {
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked()) {
                    beatsTimer.start();
                    setTextColor(getContext().getResources().getColor(R.color.red_dark));
                } else {
                    beatsTimer.stop();
                    setTextColor(getContext().getResources().getColor(R.color.green_dark));
                }
            }
        });
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if (isChecked())
            setTextColor(getContext().getResources().getColor(R.color.red_dark));
        else
            setTextColor(getContext().getResources().getColor(R.color.green_dark));
    }
}
