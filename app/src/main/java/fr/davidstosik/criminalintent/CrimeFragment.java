package fr.davidstosik.criminalintent;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.UUID;

import fr.davidstosik.criminalintent.databinding.FragmentCrimeBinding;

public class CrimeFragment extends Fragment {

    private static final String TAG = "CrimeFragment";
    private static final String ARG_CRIME_ID = "crime_id";

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
        String date = DateFormat.format("EEEE, MMM dd, yyyyy", mCrime.getDate()).toString();
        binding.crimeDate.setText(date);
        binding.crimeSolved.setChecked(mCrime.isSolved());
        binding.crimeSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        return binding.getRoot();
    }
}