package co.creativev.metronome;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import co.creativev.listener.ContinuousLongClickListener;

public class NumberWidget {
    private TextView textView;
    private String displayFormat;
    private final NumberWidgetValueChangeListener listener;
    private int value;

    public NumberWidget(final TextView textView, Button up, Button down,
                        boolean longPress, final int min, final int max,
                        final String displayFormat, final NumberWidgetValueChangeListener listener) {
        this.textView = textView;
        this.displayFormat = displayFormat;
        this.listener = listener;
        attachListeners(up, down, longPress, min, max);
    }

    public NumberWidget(Button up, Button down, boolean longPress, int min, int max, NumberWidgetValueChangeListener listener) {
        this.listener = listener;
        attachListeners(up, down, longPress, min, max);
    }

    private void attachListeners(Button up, Button down, boolean longPress, final int min, final int max) {
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newValue = Math.min(max, value + 1);
                setValue(newValue);
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newValue = Math.max(min, value - 1);
                setValue(newValue);
            }
        });

        if (longPress) {
            ContinuousLongClickListener.setListener(up, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int newValue = Math.min(max, value - (value % 10) + 10);
                    setValue(newValue);
                    return true;
                }
            });
            ContinuousLongClickListener.setListener(down, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int newValue = Math.max(min, value - (value % 10 == 0 ? 10 : value % 10));
                    setValue(newValue);
                    return true;
                }
            });
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        if (textView != null)
            textView.setText(String.format(displayFormat, value));
        listener.valueChanged(value);
    }

    public static interface NumberWidgetValueChangeListener {
        public void valueChanged(int value);
    }
}
