package fr.davidstosik.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by sto on 10/9/16.
 */

public class DateTimePickerFragment extends DialogFragment {

    private static final String TAG = "DateTimePickerFragment";
    private static final String ARG_DATE = "date";
    private static final String ARG_TYPE = "type";
    private static final String ARG_TYPE_DATE = "type_date";
    private static final String ARG_TYPE_TIME = "type_time";
    private static final String EXTRA_DATE = "fr.davidstosik.criminalintent.date";

    private View mPicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog");
        Calendar calendar = getCurrentCalendar();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        View v;
        int res_title_id;
        switch (getArguments().getString(ARG_TYPE)) {
            case ARG_TYPE_DATE:
                Log.d(TAG, "ARG_TYPE_DATE");
                v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
                mPicker = v.findViewById(R.id.dialog_date_date_picker);
                ((DatePicker) mPicker).init(year, month, day, null);
                res_title_id = R.string.date_picker_title;
                break;
            case ARG_TYPE_TIME:
                Log.d(TAG, "ARG_TYPE_TIME");
                v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
                mPicker = v.findViewById(R.id.dialog_time_time_picker);
                ((TimePicker) mPicker).setHour(hour);
                ((TimePicker) mPicker).setMinute(minute);
                ((TimePicker) mPicker).setIs24HourView(DateFormat.is24HourFormat(getActivity()));
                res_title_id = R.string.time_picker_title;
                break;
            default:
                throw new IllegalArgumentException("Invalid argument type");
        }


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(res_title_id)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "DialogInterface.OnClickListener.onClick");
                        Calendar calendar = getCurrentCalendar();
                        switch (getArguments().getString(ARG_TYPE)) {
                            case ARG_TYPE_DATE:
                                Log.d(TAG, "ARG_TYPE_DATE");
                                int year = ((DatePicker) mPicker).getYear();
                                int month = ((DatePicker) mPicker).getMonth();
                                int day = ((DatePicker) mPicker).getDayOfMonth();
                                calendar.set(year, month, day);
                                break;
                            case ARG_TYPE_TIME:
                                Log.d(TAG, "ARG_TYPE_TIME");
                                int hour = ((TimePicker) mPicker).getHour();
                                int minute = ((TimePicker) mPicker).getMinute();
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);
                                break;
                        }
                        sendResult(Activity.RESULT_OK, calendar.getTime());
                    }
                })
                .create();
    }

    private Calendar getCurrentCalendar() {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private static DateTimePickerFragment newInstance(String type, Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        args.putString(ARG_TYPE, type);

        DateTimePickerFragment fragment = new DateTimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static DateTimePickerFragment newDatePickerInstance(Date date) {
        return newInstance(ARG_TYPE_DATE, date);
    }

    public static DateTimePickerFragment newTimePickerInstance(Date date) {
        return newInstance(ARG_TYPE_TIME, date);
    }

    public void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    public static Date getDate(Intent data) {
        return (Date) data.getSerializableExtra(EXTRA_DATE);
    }
}
