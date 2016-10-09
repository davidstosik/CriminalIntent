package fr.davidstosik.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by sto on 10/9/16.
 */

public abstract class DateTimePickerFragment extends DialogFragment {

    private static final String TAG = "DateTimePickerFragment";
    protected static final String ARG_DATE = "date";
    private static final String EXTRA_DATE = "fr.davidstosik.criminalintent.date";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog");

        return new AlertDialog.Builder(getActivity())
                .setView(setUpView())
                .setTitle(getTitleResId())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "DialogInterface.OnClickListener.onClick");
                        sendResult(Activity.RESULT_OK, getPickerCalendar().getTime());
                    }
                })
                .create();
    }

    protected View setUpView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(getLayoutResId(), container);
    }

    protected View setUpView() {
        return setUpView(LayoutInflater.from(getActivity()), null);
    }

    protected abstract int getLayoutResId();
    protected abstract int getTitleResId();

    protected abstract Calendar getPickerCalendar();

    protected Calendar getCrimeCalendar() {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
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
