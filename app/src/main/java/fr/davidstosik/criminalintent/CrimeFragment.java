package fr.davidstosik.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContactsEntity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

import fr.davidstosik.criminalintent.databinding.FragmentCrimeBinding;

public class CrimeFragment extends Fragment {

    private static final String TAG = "CrimeFragment";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE_TIME = "DialogDateTime";
    private static final int REQUEST_DATE_TIME = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PERMISSION_CONTACTS_FOR_VIEW = 0;
    private static final int REQUEST_PERMISSION_CONTACTS_FOR_CALL = 1;

    private Crime mCrime;
    private FragmentCrimeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID id = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        Log.d(TAG, String.format("Crime id in intent's extra: %s", (id == null ? "NULL" : id.toString())));
        mCrime = CrimeLab.get(getActivity()).getCrime(id);
        Log.d(TAG, String.format("Was a Crime retrieved? %s", String.valueOf(mCrime != null)));
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
        updateSuspect();
        updateDate();
        binding.crimeDateButton.setOnClickListener(new PickerButtonClickListener());
        binding.crimeTimeButton.setOnClickListener(new PickerButtonClickListener());
        binding.crimeSolved.setChecked(mCrime.isSolved());
        binding.crimeViewSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view();
            }
        });
        binding.crimeCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });
        binding.crimeSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        binding.crimeReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setText(getCrimeReport())
                        .setType("text/plain")
                        .setChooserTitle(R.string.send_report)
                        .createChooserIntent();
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
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
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
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
                Log.d(TAG, "contactUri: " + contactUri.toString());
                String[] queryFields = new String[] {
                        Contacts.DISPLAY_NAME,
                        Contacts._ID,
                };
                Cursor c = getActivity().getContentResolver().query(
                        contactUri, queryFields, null, null, null);
                if (c != null) {
                    try {
                        if (c.moveToFirst()) {
                            String suspect = c.getString(c.getColumnIndex(Contacts.DISPLAY_NAME));
                            long suspectId = c.getLong(c.getColumnIndex(Contacts._ID));
                            Log.d(TAG, String.format("Suspect: %s (%d)", suspect, suspectId));
                            mCrime.setSuspect(suspect);
                            mCrime.setSuspectId(suspectId);
                            updateSuspect();
                        }
                    } finally {
                        c.close();
                    }
                }
                break;
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        Log.d(TAG, String.format("(Crime Suspect: %s)", mCrime.getSuspect()));

        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "denied");
            Toast.makeText(getContext(), R.string.permission_denied_toast, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        switch (requestCode) {
            case REQUEST_PERMISSION_CONTACTS_FOR_VIEW:
                Log.d(TAG, "REQUEST_PERMISSION_CONTACTS_FOR_VIEW");
                view();
                break;
            case REQUEST_PERMISSION_CONTACTS_FOR_CALL:
                Log.d(TAG, "REQUEST_PERMISSION_CONTACTS_FOR_CALL");
                call();
                break;
            default:
                break;
        }
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void updateDate() {
        String date = DateFormat.getLongDateFormat(getActivity()).format(mCrime.getDate());
        binding.crimeDateButton.setText(date);
        String time = DateFormat.getTimeFormat(getActivity()).format(mCrime.getDate());
        binding.crimeTimeButton.setText(time);
    }

    private void updateSuspect() {
        String label = mCrime.getSuspect();

        boolean enableButtons = true;
        if (label == null) {
            label = getString(R.string.choose_suspect_button);
            enableButtons = false;
        }
        binding.crimeViewSuspectButton.setEnabled(enableButtons);
        binding.crimeCallButton.setEnabled(enableButtons);
        binding.crimeSuspectButton.setText(label);
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

        return getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
    }

    private boolean checkContactPermission() {
        Log.d(TAG, "checkContactPermission()");
        int permission = ContextCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.READ_CONTACTS
        );
        Log.d(TAG, "permission was: " + String.valueOf(permission));
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private boolean requestContactPermission(int action) {
        Log.d(TAG, "requestContactPermission()");

        if (checkContactPermission()) {
            Log.d(TAG, "granted");
            return true;
        }

        Log.d(TAG, "requesting permission to read contact");
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.READ_CONTACTS},
                action
        );
        return false;
    }

    private boolean ensureCrimeSuspectId(final int action) {
        Log.d(TAG, "ensureCrimeSuspectId()");

        if (mCrime.getSuspectId() != 0) {
            Log.d(TAG, "suspect id is " + mCrime.getSuspectId());
            return true;
        }

        if (!checkContactPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_CONTACTS)) {
                Log.d(TAG, "about to display an AlertDialog");
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.contacts_permission_dialog_title)
                        .setMessage(R.string.contacts_permission_dialog_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                requestContactPermission(action);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            } else {
                Toast.makeText(getContext(), R.string.permission_denied_toast, Toast.LENGTH_SHORT)
                        .show();
            }
            return false;
        }
        Cursor cursor = getActivity().getContentResolver().query(
                Contacts.CONTENT_URI,
                new String[]{ Contacts._ID },
                Contacts.DISPLAY_NAME + " = ?",
                new String[]{ mCrime.getSuspect() },
                null
        );
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    long suspectId = Long.parseLong(cursor.getString(cursor.getColumnIndex(Contacts._ID)));
                    mCrime.setSuspectId(suspectId);
                    Log.d(TAG, "Set supected id to " + suspectId);
                    return true;
                }
            } finally {
                cursor.close();
            }
        }
        return false;
    }

    private String getCrimeSuspectPhoneNumber() {
        Log.d(TAG, "getCrimeSuspectPhoneNumber()");

        if (!ensureCrimeSuspectId(REQUEST_PERMISSION_CONTACTS_FOR_CALL)
                || !requestContactPermission(REQUEST_PERMISSION_CONTACTS_FOR_CALL)) {
            return null;
        }

        Cursor cursor = getActivity().getContentResolver().query(
                Data.CONTENT_URI,
                new String[] { Phone.NUMBER },
                Data.CONTACT_ID + " = ?" + " AND " +
                        RawContactsEntity.MIMETYPE + " = '" + Phone.CONTENT_ITEM_TYPE + "'",
                new String[] { String.valueOf(mCrime.getSuspectId()) },
                RawContactsEntity.IS_PRIMARY + " DESC"
        );

        String phoneNb = "";
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    phoneNb = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    Log.d(TAG, "Phone nb: " + phoneNb);
                }
            } finally {
                cursor.close();
            }
        }
        return phoneNb;
    }

    private void view() {
        Log.d(TAG, "view()");

        if (!ensureCrimeSuspectId(REQUEST_PERMISSION_CONTACTS_FOR_VIEW)) {
            return;
        }

        Uri suspectUri = Uri.withAppendedPath(Contacts.CONTENT_URI, String.valueOf(mCrime.getSuspectId()));
        Log.d(TAG, "URI: " + suspectUri.toString());
        Intent i = new Intent(Intent.ACTION_VIEW, suspectUri);
        startActivity(i);
    }

    private void call() {
        Log.d(TAG, "call()");

        String phoneNb = getCrimeSuspectPhoneNumber();
        if (phoneNb == null) {
            return;
        }

        if (phoneNb.isEmpty()) {
            Toast.makeText(getContext(), R.string.no_phone_number_toast, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Uri phoneUri = Uri.parse("tel:" + phoneNb);
        Intent i = new Intent(Intent.ACTION_DIAL, phoneUri);
        startActivity(i);
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