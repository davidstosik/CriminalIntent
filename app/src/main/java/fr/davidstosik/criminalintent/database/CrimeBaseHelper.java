package fr.davidstosik.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static fr.davidstosik.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by sto on 2016/11/05.
 */

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 3;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CrimeTable.NAME + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + CrimeTable.Cols.UUID +
                ", " + CrimeTable.Cols.TITLE +
                ", " + CrimeTable.Cols.DATE +
                ", " + CrimeTable.Cols.SOLVED +
                ", " + CrimeTable.Cols.SUSPECT +
                ", " + CrimeTable.Cols.SUSPECT_ID +
        ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + CrimeTable.NAME + " ADD COLUMN " + CrimeTable.Cols.SUSPECT);
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + CrimeTable.NAME + " ADD COLUMN " + CrimeTable.Cols.SUSPECT_ID);
        }
    }
}
