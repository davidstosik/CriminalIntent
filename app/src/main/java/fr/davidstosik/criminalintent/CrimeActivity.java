package fr.davidstosik.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    private static final String TAG = "CrimeActivity";
    public static final String EXTRA_CRIME_ID = "fr.davidstosik.criminalintent.crime_id";

    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Log.d(TAG, "in newIntent()");
        Log.d(TAG, "crimeId = " + crimeId.toString());
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }
}
