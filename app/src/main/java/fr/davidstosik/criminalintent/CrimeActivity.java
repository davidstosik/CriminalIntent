package fr.davidstosik.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    private static final String TAG = "CrimeActivity";
    private static final String EXTRA_CRIME_ID = "fr.davidstosik.criminalintent.crime_id";
    private UUID mCrimeId;

    @Override
    protected Fragment createFragment() {
        mCrimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(mCrimeId);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed()");
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CRIME_ID, mCrimeId);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Log.d(TAG, "in newIntent()");
        Log.d(TAG, "crimeId = " + crimeId.toString());
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    public static UUID getModifiedCrimeId(Intent result) {
        return (UUID) result.getSerializableExtra(EXTRA_CRIME_ID);
    }
}
