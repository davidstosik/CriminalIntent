package fr.davidstosik.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by sto on 10/3/16.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}