package com.github.shareme.gwseula.library;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

/**
 * Eula class that provides a way to build a dialog with the eula in it and
 * ties eula to version code so when updated new eula is first shown.
 *
 * Usage, resource strings to replace with your own under your res/values/strings.xml
 * are
 * <code>
 *     <string name="eula">Eula: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus sagittis, est in pulvinar varius, nulla metus suscipit enim, eu volutpat sem magna sed lacus. Donec tempor libero eget dui euismod eget luctus libero tempor. Quisque felis mauris, malesuada accumsan vulputate quis, sodales et nibh. Mauris tellus neque, interdum quis blandit sagittis, tincidunt at urna. Cras luctus feugiat metus, in sollicitudin felis laoreet sed. Mauris posuere mattis felis. Morbi enim odio, accumsan ac tincidunt pharetra, mattis sit amet turpis. Vivamus lobortis aliquet ante nec aliquet. Suspendisse potenti. Praesent rutrum condimentum ante et congue. In faucibus posuere arcu, ut congue sem dictum ac. Cras ante libero, pellentesque et fermentum ut, congue nec mi. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Sed euismod malesuada sapien ut eleifend. </string>
 *     <string name="updates">Upates in this version: Donec sollicitudin sodales turpis eget congue. Nulla facilisi. Proin facilisis eros vel mauris mollis tempus. </string>
 *
 * </code>
 *
 * in  the activity onCreate:
 * <code>
 *     new Eula(this).show()
 * </code>
 * Created by fgrott on 10/9/2015.
 */
@SuppressWarnings("unused")
public class Eula {

    private String EULA_PREFIX = "eula_";
    private Activity mActivity;

    public Eula(Activity context) {
        mActivity = context;
    }

    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
            pi = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }

    public void show() {
        PackageInfo versionInfo = getPackageInfo();

        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
        final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
        if(!hasBeenShown){

            // Show the Eula
            String title = mActivity.getString(R.string.app_name) + " v" + versionInfo.versionName;

            //Includes the updates as well so users know what changed.
            String message = mActivity.getString(R.string.updates) + "\n\n" + mActivity.getString(R.string.eula);

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Mark this version as read.
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(eulaKey, true);
                            //changed from an immediate write to write when ready
                            editor.apply();
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Close the activity as they have declined the EULA
                            mActivity.finish();
                        }

                    });
            builder.create().show();
        }
    }

}
