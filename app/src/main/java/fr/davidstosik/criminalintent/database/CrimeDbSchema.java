package fr.davidstosik.criminalintent.database;

/**
 * Created by sto on 2016/11/05.
 */

public class CrimeDbSchema {
    public static class CrimeTable {
        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
        }
    }
}
