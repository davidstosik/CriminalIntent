package fr.davidstosik.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.Date;

/**
 * Created by sto on 10/10/16.
 */

public class DateTimePickerActivity extends SingleFragmentActivity {

    private static final String TAG = "DateTimePickerActivity";
    private static final String EXTRA_DATE_TIME = "fr.davidstosik.criminalintent.datetimepickeractivity.date_time";
    private static final String EXTRA_PICKER_TYPE = "fr.davidstosik.criminalintent.datetimepickeractivity.picker_type";
    private static final String DATE_PICKER = "date_picker";
    private static final String TIME_PICKER = "time_picker";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        Log.d(TAG, "createFragment()");
        String type = getIntent().getStringExtra(EXTRA_PICKER_TYPE);
        Log.d(TAG, "EXTRA_PICKER_TYPE: " + type);
        switch (type) {
            case DATE_PICKER:
                return DatePickerFragment.newInstance(getIntentDate());
            case TIME_PICKER:
                return TimePickerFragment.newInstance(getIntentDate());
            default:
                throw new IllegalArgumentException();
        }
    }

    protected Date getIntentDate() {
        return (Date) getIntent().getSerializableExtra(EXTRA_DATE_TIME);
    }

    public static Intent newIntent(Context packageContext, Date date, String type) {
        Log.d(TAG, "newIntent()");
        Intent intent = new Intent(packageContext, DateTimePickerActivity.class);
        intent.putExtra(EXTRA_DATE_TIME, date);
        intent.putExtra(EXTRA_PICKER_TYPE, type);
        return intent;
    }

    public static Intent newDatePickerIntent(Context packageContext, Date date) {
        return newIntent(packageContext, date, DATE_PICKER);
    }

    public static Intent newTimePickerIntent(Context packageContext, Date date) {
        return newIntent(packageContext, date, TIME_PICKER);
    }
}
