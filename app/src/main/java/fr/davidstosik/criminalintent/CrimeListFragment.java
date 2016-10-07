package fr.davidstosik.criminalintent;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import fr.davidstosik.criminalintent.databinding.FragmentCrimeListBinding;
import fr.davidstosik.criminalintent.databinding.ListItemCrimeBinding;

/**
 * Created by sto on 10/3/16.
 */
public class CrimeListFragment extends Fragment {

    private FragmentCrimeListBinding binding;
    private CrimeAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_crime_list, container, false);
        binding.crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return binding.crimeRecyclerView;
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        mAdapter = new CrimeAdapter(crimes);
        binding.crimeRecyclerView.setAdapter(mAdapter);
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
            mCrime = crime;
            ListItemCrimeBinding binding = DataBindingUtil.getBinding(mItemView);
            binding.listItemCrimeTitleTextView.setText(crime.getTitle());
            binding.listItemCrimeDateTextView.setText(crime.getDate().toString());
            binding.listItemCrimeSolvedCheckbox.setChecked(crime.isSolved());
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(),
                    mCrime.getTitle() + " clicked!", Toast.LENGTH_SHORT)
                    .show();
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
