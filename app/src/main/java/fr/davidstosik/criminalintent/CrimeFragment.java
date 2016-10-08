package fr.davidstosik.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.databinding.DataBindingUtil;
import android.widget.CompoundButton;

import fr.davidstosik.criminalintent.databinding.FragmentCrimeBinding;

public class CrimeFragment extends Fragment {

    private static final String TAG = "CrimeFragment";
    private Crime mCrime;
    private FragmentCrimeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        mCrime = new Crime();
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
        // String date = DateFormat.getLongDateFormat(getActivity()).format(mCrime.getDate());
        String date = DateFormat.format("EEEE, MMM dd, yyyyy", mCrime.getDate()).toString();
        binding.crimeDate.setText(date);
        binding.crimeDate.setEnabled(false);
        binding.crimeSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        return binding.getRoot();
    }

}
