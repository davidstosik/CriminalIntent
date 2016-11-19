package fr.davidstosik.criminalintent;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Pair;

/**
 * Created by sto on 2016/11/19.
 */

class ContactsUtils {
    private Activity mActivity;

    ContactsUtils(Activity activity) {
        mActivity = activity;
    }

    long getContactIdFromName(String name) {
        Cursor cursor = mActivity.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                new String[]{ ContactsContract.Contacts._ID },
                ContactsContract.Contacts.DISPLAY_NAME + " = ?",
                new String[]{ name },
                null
        );
        long result = 0;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    result = Long.parseLong(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                }
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    String getPhoneNumberFromContactId(long id) {
        Cursor cursor = mActivity.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
                ContactsContract.Data.CONTACT_ID + " = ?" + " AND " +
                        ContactsContract.RawContactsEntity.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'",
                new String[] { String.valueOf(id) },
                ContactsContract.RawContactsEntity.IS_PRIMARY + " DESC"
        );

        String result = null;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    Pair<String, Long> getContactNameAndIdFromUri(Uri uri) {
        Pair<String, Long> result = null;
        String[] queryFields = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts._ID,
        };
        Cursor c = mActivity.getContentResolver().query(
                uri, queryFields, null, null, null);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    long id = c.getLong(c.getColumnIndex(ContactsContract.Contacts._ID));
                    result = new Pair<String, Long>(name, Long.valueOf(id));
                }
            } finally {
                c.close();
            }
        }
        return result;
    }
}
