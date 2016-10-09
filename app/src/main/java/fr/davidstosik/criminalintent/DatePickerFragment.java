package fr.davidstosik.criminalintent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by sto on 10/10/16.
 */

public class DatePickerFragment extends DateTimePickerFragment {

    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getTitleResId() {
        return R.string.date_picker_title;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_date;
    }

    @Override
    protected View setUpView(LayoutInflater inflater, ViewGroup container) {
        View view = super.setUpView(inflater, container);

        Calendar calendar = getCrimeCalendar();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        mDatePicker = (DatePicker) view.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year, month, day, null);

        return view;
    }

    @Override
    public Calendar getPickerCalendar() {
        Calendar calendar = getCrimeCalendar();
        int year = mDatePicker.getYear();
        int month = mDatePicker.getMonth();
        int day = mDatePicker.getDayOfMonth();
        calendar.set(year, month, day);
        return calendar;
    }
}
