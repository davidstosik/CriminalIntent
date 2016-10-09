package fr.davidstosik.criminalintent;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by sto on 10/10/16.
 */

public class TimePickerFragment extends DateTimePickerFragment {

    private TimePicker mTimePicker;

    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getTitleResId() {
        return R.string.time_picker_title;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_time;
    }

    @Override
    protected View setUpView(LayoutInflater inflater, ViewGroup container) {
        View view = super.setUpView(inflater, container);

        Calendar calendar = getCrimeCalendar();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        mTimePicker = (TimePicker) view.findViewById(R.id.dialog_time_time_picker);
        mTimePicker.setHour(hour);
        mTimePicker.setMinute(minute);
        mTimePicker.setIs24HourView(DateFormat.is24HourFormat(getActivity()));

        return view;
    }

    @Override
    public Calendar getPickerCalendar() {
        Calendar calendar = getCrimeCalendar();
        int hour = mTimePicker.getHour();
        int minute = mTimePicker.getMinute();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar;
    }
}
