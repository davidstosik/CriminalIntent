package fr.davidstosik.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    private static final String TAG = "CrimeActivity";
    private static final String EXTRA_CRIME_ID = "fr.davidstosik.criminalintent.crime_activity.crime_id";

    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed()");

        FragmentManager fm = getSupportFragmentManager();
        CrimeFragment fragment = (CrimeFragment) fm.findFragmentById(R.id.fragment_container);

        Intent intent = new Intent();
        int result = fragment.returnResult(intent);
        setResult(result, intent);

        super.onBackPressed();
    }

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Log.d(TAG, "in newIntent()");
        Log.d(TAG, "crimeId = " + crimeId.toString());
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    public static UUID getModifiedCrimeId(Intent data) {
        return CrimeFragment.getModifiedCrimeId(data);
    }
}
