package fr.davidstosik.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.List;
import java.util.UUID;

import fr.davidstosik.criminalintent.databinding.FragmentCrimeListBinding;
import fr.davidstosik.criminalintent.databinding.ListItemCrimeBinding;

/**
 * Created by sto on 10/3/16.
 */
public class CrimeListFragment extends Fragment {

    private static final String TAG = "CrimeListFragment";
    public static final int REQUEST_CRIME = 1;
    private FragmentCrimeListBinding binding;
    private CrimeAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        Log.d(TAG, "onCreateView()");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_crime_list, container, false);
        binding.crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return binding.crimeRecyclerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        Log.d(TAG, "updateUI()");
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (mAdapter == null) {
            Log.d(TAG, "mAdapter is null");
            mAdapter = new CrimeAdapter(crimes);
            binding.crimeRecyclerView.setAdapter(mAdapter);
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View mItemView;
        private Crime mCrime;

        public CrimeHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mItemView.setOnClickListener(this);
        }

        public View getView() {
            return mItemView;
        }

        public void bindCrime(Crime crime) {
            Log.d(TAG, "bindCrime()");
            mCrime = crime;
            ListItemCrimeBinding binding = DataBindingUtil.getBinding(mItemView);
            binding.listItemCrimeTitleTextView.setText(crime.getTitle());
            binding.listItemCrimeDateTextView.setText(crime.getDate().toString());
            binding.listItemCrimeSolvedCheckbox.setChecked(crime.isSolved());
            binding.listItemCrimeSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mCrime.setSolved(isChecked);
                }
            });
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, String.format("Calling new intent on %s", mCrime.getId().toString()));
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivityForResult(intent, REQUEST_CRIME);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult()");
        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "resultCode not OK");
            return;
        }
        switch (requestCode) {
            case REQUEST_CRIME:
                Log.d(TAG, "REQUEST_CRIME");
//                UUID crimeId = CrimeActivity.getModifiedCrimeId(data);
//                Log.d(TAG, "Crime id = " + crimeId.toString());
//                int position = CrimeLab.get(getContext()).getPosition(crimeId);
//                Log.d(TAG, "position = " + String.valueOf(position));
//                mAdapter.notifyItemChanged(position);
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;
        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = DataBindingUtil.inflate(layoutInflater, R.layout.list_item_crime, parent, false).getRoot();
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}
