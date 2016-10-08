package fr.davidstosik.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by sto on 10/3/16.
 */
public class CrimeLab {

    private static final String TAG = "CrimeLab";
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;

    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0);
            mCrimes.add(crime);
        }
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        Log.d(TAG, String.format("getCrime(%s)", id.toString()));
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                Log.d(TAG, "Returning a Crime.");
                return crime;
            }
        }
        Log.d(TAG, "Returning null.");
        return null;
    }
}
