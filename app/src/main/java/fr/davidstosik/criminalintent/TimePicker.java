package fr.davidstosik.criminalintent;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

/**
 * Created by sto on 10/9/16.
 */

public class TimePicker extends android.widget.TimePicker {
    public TimePicker(Context context) {
        super(context);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getHour() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.getHour();
        } else {
            return super.getCurrentHour();
        }
    }

    @Override
    public int getMinute() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.getMinute();
        } else {
            return super.getCurrentMinute();
        }
    }

    @Override
    public void setHour(int hour) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setHour(hour);
        } else {
            super.setCurrentHour(hour);
        }
    }

    @Override
    public void setMinute(int minute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setMinute(minute);
        } else {
            super.setCurrentMinute(minute);
        }
    }

}
