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
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public void addCrime(Crime c) {
        mCrimes.add(c);
    }

    public void deleteCrime(Crime c) {
        if (c == null) {
            return;
        }
        int index = getPosition(c.getId());
        if (index == -1) {
            return;
        }
        mCrimes.remove(index);
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

    public int getPosition(UUID crimeId) {
        for (int position = 0; position < mCrimes.size(); position++) {
            if (mCrimes.get(position).getId().equals(crimeId)) {
                return position;
            }
        }
        return -1;
    }
}
