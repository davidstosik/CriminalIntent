package fr.davidstosik.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private static final int REQUEST_CONTACT = 1;

    private Crime mCrime;
    private FragmentCrimeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID id = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        Log.d(TAG, String.format("Crime id in intent's extra: %s", id.toString()));
        mCrime = CrimeLab.get(getActivity()).getCrime(id);
        Log.d(TAG, String.format("Was a Crime retrieved? %s", String.valueOf(mCrime != null)));
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
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
            case REQUEST_CONTACT:
                if (data == null) {
                    return;
                }
                Uri contactUri = data.getData();
                String[] queryFields = new String[] {
                        ContactsContract.Contacts.DISPLAY_NAME
                };
                Cursor c = getActivity().getContentResolver().query(
                        contactUri, queryFields, null, null, null);
                try {
                    if (c.getCount() == 0) {
                        return;
                    }
                    c.moveToFirst();
                    String suspect = c.getString(0);
                    mCrime.setSuspect(suspect);
                    binding.crimeSuspectButton.setText(suspect);
                } finally {
                    c.close();
                }
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
        if (mCrime.getSuspect() != null) {
            binding.crimeSuspectButton.setText(mCrime.getSuspect());
        }
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
        binding.crimeReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        binding.crimeSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            binding.crimeSuspectButton.setEnabled(false);
        }
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu()");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getCrimeReport() {
        String solvedString;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateString = DateFormat.getMediumDateFormat(getActivity()).format(mCrime.getDate());

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    private class PickerButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (getResources().getBoolean(R.bool.large_layout)) {
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