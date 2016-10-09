package fr.davidstosik.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = setUpView(inflater, container);
        view.findViewById(R.id.dialog_date_submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(Activity.RESULT_OK, getPickerCalendar().getTime());
                dismiss();
            }
        });
        return view;
    }

    protected View setUpView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.dialog_container, container);

        ((TextView) view.findViewById(R.id.dialog_container_title)).setText(getTitleResId());

        ViewStub stub = (ViewStub) view.findViewById(R.id.dialog_container_picker_stub);
        stub.setLayoutResource(getLayoutResId());
        View inflated = stub.inflate();

        return view;
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
