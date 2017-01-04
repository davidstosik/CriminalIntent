package fr.davidstosik.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by sto on 10/9/16.
 */

public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callbacks {

    private static final String TAG = "CrimePagerActivity";
    private static final String EXTRA_CRIME_ID = "fr.davidstosik.criminalintent.crime_pager_activity.crime_id";
    private static final String EXTRA_CHANGED_CRIME_IDS = "fr.davidstosik.criminalintent.crime_pager_activity.changed_crime_ids";
    private static final String KEY_CHANGED_CRIME_IDS = "changed_crime_ids";
    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private Set<UUID> mChangedCrimeIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();

        if (savedInstanceState != null) {
            mChangedCrimeIds = (HashSet<UUID>) savedInstanceState.getSerializable(KEY_CHANGED_CRIME_IDS);
            Log.d(TAG, "Retrieved saved instance of mChangedCrimeIds: " + mChangedCrimeIds.toString());
        }
        if (mChangedCrimeIds == null) {
            mChangedCrimeIds = new HashSet<>();
            Log.d(TAG, "New empty instance of mChangedCrimeIds");
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                super.setPrimaryItem(container, position, object);
                Crime crime = mCrimes.get(position);
                mChangedCrimeIds.add(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        mViewPager.setCurrentItem(CrimeLab.get(this).getPosition(crimeId));
    }

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "Saving instance of mChangedCrimeIds: " + mChangedCrimeIds.toString());
        savedInstanceState.putSerializable(KEY_CHANGED_CRIME_IDS, (Serializable) mChangedCrimeIds);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed()");
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CHANGED_CRIME_IDS, (Serializable) mChangedCrimeIds);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*
         * Source: https://code.google.com/p/android/issues/detail?id=189121#c5
         * child v4.fragments aren't receiving this due to bug. So forward to child fragments manually
         * https://code.google.com/p/android/issues/detail?id=189121
         */
        FragmentStatePagerAdapter fspa = (FragmentStatePagerAdapter)mViewPager.getAdapter();
        Fragment currentFragment = (Fragment)fspa.instantiateItem(mViewPager, mViewPager.getCurrentItem());
        if (currentFragment != null) {
            currentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        // Nothing to do.
    }

    @Override
    public void onCrimeDeleted(Crime crime) {
        finish();
    }

    public static Set<UUID> getModifiedCrimeIds(Intent result) {
        return (HashSet<UUID>) result.getSerializableExtra(EXTRA_CHANGED_CRIME_IDS);
    }
}
