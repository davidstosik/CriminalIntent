package fr.davidstosik.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by sto on 2016/11/19.
 */

class PermissionsUtils {
    private static boolean checkContactPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED;
    }

    static boolean requestContactPermission(Activity activity, int action) {
        if (checkContactPermission(activity)) {
            return true;
        }
        ActivityCompat.requestPermissions(
                activity,
                new String[] { Manifest.permission.READ_CONTACTS },
                action
        );
        return false;
    }

    static boolean requestContactPermissionWithDialog(final Activity activity, final int action) {
        if (checkContactPermission(activity)) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_CONTACTS)) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.contacts_permission_dialog_title)
                    .setMessage(R.string.contacts_permission_dialog_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            requestContactPermission(activity, action);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        } else {
            Toast.makeText(activity, R.string.permission_denied_toast, Toast.LENGTH_SHORT)
                    .show();
        }
        return false;
    }
}
