package fr.davidstosik.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import fr.davidstosik.criminalintent.databinding.FragmentCrimeBinding;

public class CrimeFragment extends Fragment {

    private static final String TAG = "CrimeFragment";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE_TIME = "DialogDateTime";
    private static final int REQUEST_DATE_TIME = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_PERMISSION_CONTACTS_FOR_VIEW = 0;
    private static final int REQUEST_PERMISSION_CONTACTS_FOR_CALL = 1;

    private Crime mCrime;
    private FragmentCrimeBinding mBinding;
    private File mPhotoFile;
    private boolean mPhotoViewNeedsUpdate;

    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
        void onCrimeDeleted(Crime crime);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID id = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        Log.d(TAG, String.format("Crime id in intent's extra: %s", (id == null ? "NULL" : id.toString())));
        mCrime = CrimeLab.get(getActivity()).getCrime(id);
        Log.d(TAG, String.format("Was a Crime retrieved? %s", String.valueOf(mCrime != null)));
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_crime, container, false);
        mBinding.viewCameraAndTitle.crimeTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });
        mBinding.viewCameraAndTitle.crimeTitleField.setText(mCrime.getTitle());
        updateSuspectView();
        updateDateView();
        mPhotoViewNeedsUpdate = true;
        mBinding.viewCameraAndTitle.crimePhoto.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mPhotoViewNeedsUpdate) {
                    updatePhotoView();
                    mPhotoViewNeedsUpdate = false;
                }
            }
        });

        mBinding.crimeDateButton.setOnClickListener(new PickerButtonClickListener());
        mBinding.crimeTimeButton.setOnClickListener(new PickerButtonClickListener());
        mBinding.crimeSolved.setChecked(mCrime.isSolved());
        mBinding.crimeViewSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view();
            }
        });
        mBinding.crimeCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });
        mBinding.crimeSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
        mBinding.crimeSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mBinding.crimeSuspectButton.setEnabled(false);
        }
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mBinding.viewCameraAndTitle.crimeCamera.setEnabled(canTakePhoto);
        if (canTakePhoto) {
            Uri uri = FileProvider.getUriForFile(getContext(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mBinding.viewCameraAndTitle.crimeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        mBinding.viewCameraAndTitle.crimePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPhotoFile != null && mPhotoFile.exists()) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    DialogFragment newFragment = CrimePhotoDialogFragment.newInstance(mPhotoFile);
                    newFragment.show(ft, "dialog");
                }
            }
        });
        return mBinding.getRoot();
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
                updateCrime();
                updateDateView();
                break;
            case REQUEST_CONTACT:
                if (data == null) {
                    return;
                }
                Uri contactUri = data.getData();
                Pair<String, Long> pair = new ContactsUtils(getActivity()).getContactNameAndIdFromUri(contactUri);
                mCrime.setSuspect(pair.first);
                mCrime.setSuspectId(pair.second.longValue());
                updateCrime();
                updateSuspectView();
                break;
            case REQUEST_PHOTO:
                mPhotoViewNeedsUpdate = true;
                updateCrime();
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
                mCallbacks.onCrimeDeleted(mCrime);
                return true;
            case R.id.menu_item_share_crime:
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setText(getCrimeReport())
                        .setType("text/plain")
                        .setChooserTitle(R.string.send_report)
                        .createChooserIntent();
                startActivity(i);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void updateDateView() {
        String date = DateFormat.getLongDateFormat(getActivity()).format(mCrime.getDate());
        mBinding.crimeDateButton.setText(date);
        String time = DateFormat.getTimeFormat(getActivity()).format(mCrime.getDate());
        mBinding.crimeTimeButton.setText(time);
    }

    private void updateSuspectView() {
        String label = mCrime.getSuspect();

        boolean enableButtons = true;
        if (label == null) {
            label = getString(R.string.choose_suspect_button);
            enableButtons = false;
        }
        mBinding.crimeViewSuspectButton.setEnabled(enableButtons);
        mBinding.crimeCallButton.setEnabled(enableButtons);
        mBinding.crimeSuspectButton.setText(label);
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mBinding.viewCameraAndTitle.crimePhoto.setImageDrawable(null);
        } else {
            int height = mBinding.viewCameraAndTitle.crimePhoto.getHeight();
            int width = mBinding.viewCameraAndTitle.crimePhoto.getWidth();
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), width, height);
            mBinding.viewCameraAndTitle.crimePhoto.setImageBitmap(bitmap);
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

        return getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
    }

    private boolean ensureCrimeSuspectId(final int action) {
        if (mCrime.getSuspectId() != 0) {
            return true;
        }
        if (!PermissionsUtils.requestContactPermissionWithDialog(getActivity(), action)) {
            return false;
        }
        long id = new ContactsUtils(getActivity()).getContactIdFromName(mCrime.getSuspect());
        mCrime.setSuspectId(id);
        return id != 0;
    }

    private void view() {
        if (!ensureCrimeSuspectId(REQUEST_PERMISSION_CONTACTS_FOR_VIEW)) {
            return;
        }

        Uri suspectUri = Uri.withAppendedPath(Contacts.CONTENT_URI, String.valueOf(mCrime.getSuspectId()));
        Intent i = new Intent(Intent.ACTION_VIEW, suspectUri);
        startActivity(i);
    }

    private void call() {
        if (!ensureCrimeSuspectId(REQUEST_PERMISSION_CONTACTS_FOR_CALL)
                || !PermissionsUtils.requestContactPermission(getActivity(), REQUEST_PERMISSION_CONTACTS_FOR_CALL)) {
            return;
        }
        String phoneNb = new ContactsUtils(getActivity()).getPhoneNumberFromContactId(mCrime.getSuspectId());

        if (phoneNb == null || phoneNb.isEmpty()) {
            Toast.makeText(getContext(), R.string.no_phone_number_toast, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Uri phoneUri = Uri.parse("tel:" + phoneNb);
        Intent i = new Intent(Intent.ACTION_DIAL, phoneUri);
        startActivity(i);
    }

    private void updateCrime() {
        Log.d(TAG, "updateCrime()");
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
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
