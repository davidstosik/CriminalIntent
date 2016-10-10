package fr.davidstosik.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.Date;
import java.util.UUID;

import fr.davidstosik.criminalintent.databinding.FragmentCrimeBinding;

public class CrimeFragment extends Fragment {

    private static final String TAG = "CrimeFragment";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE_TIME = "DialogDateTime";
    private static final int REQUEST_DATE_TIME = 0;

    private Crime mCrime;
    private FragmentCrimeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        UUID id = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        Log.d(TAG, String.format("Crime id in intent's extra: %s", id.toString()));
        mCrime = CrimeLab.get(getActivity()).getCrime(id);
        Log.d(TAG, String.format("Was a Crime retrieved? %s", String.valueOf(mCrime != null)));
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult()");
        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "RESULT is not OK");
            return;
        }
        switch (requestCode) {
            case REQUEST_DATE_TIME:
                Log.d(TAG, "REQUEST_DATE_TIME");
                Date date = DateTimePickerFragment.getDate(data);
                Log.d(TAG, "retrieved date: " + date.toString());
                mCrime.setDate(date);
                updateDate();
                break;
        }
    }

    private void updateDate() {
        String date = DateFormat.getLongDateFormat(getActivity()).format(mCrime.getDate());
        binding.crimeDateButton.setText(date);
        String time = DateFormat.getTimeFormat(getActivity()).format(mCrime.getDate());
        binding.crimeTimeButton.setText(time);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_crime, container, false);
        binding.crimeTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });
        binding.crimeTitleField.setText(mCrime.getTitle());
        updateDate();
        binding.crimeDateButton.setOnClickListener(new PickerButtonClickListener());
        binding.crimeTimeButton.setOnClickListener(new PickerButtonClickListener());
        binding.crimeSolved.setChecked(mCrime.isSolved());
        binding.crimeSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        return binding.getRoot();
    }

    private class PickerButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (false) { // FIXME: depends on device size
                FragmentManager manager = getFragmentManager();
                DateTimePickerFragment dialog;
                switch (v.getId()) {
                    case R.id.crime_date_button:
                        dialog = DatePickerFragment.newInstance(mCrime.getDate());
                        break;
                    case R.id.crime_time_button:
                        dialog = TimePickerFragment.newInstance(mCrime.getDate());
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE_TIME);
                dialog.show(manager, DIALOG_DATE_TIME);
            } else {
                Intent intent;
                switch (v.getId()) {
                    case R.id.crime_date_button:
                        intent = DateTimePickerActivity.newDatePickerIntent(getActivity(), mCrime.getDate());
                        break;
                    case R.id.crime_time_button:
                        intent = DateTimePickerActivity.newTimePickerIntent(getActivity(), mCrime.getDate());
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                startActivityForResult(intent, REQUEST_DATE_TIME);
            }
        }
    }
}