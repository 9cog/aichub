package org.apache.cordova;

import java.util.Arrays;
import org.json.JSONException;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class PermissionHelper {
    private static final String LOG_TAG = "CordovaPermissionHelper";

    public static void requestPermission(CordovaPlugin plugin, int requestCode, String permission) {
        requestPermissions(plugin, requestCode, new String[]{permission});
    }

    public static void requestPermissions(CordovaPlugin plugin, int requestCode, String[] permissions) {
        plugin.cordova.requestPermissions(plugin, requestCode, permissions);
    }

    public static boolean hasPermission(CordovaPlugin plugin, String permission) {
        return plugin.cordova.hasPermission(permission);
    }

    private static void deliverPermissionResult(CordovaPlugin plugin, int requestCode, String[] permissions) {
        int[] iArr = new int[permissions.length];
        Arrays.fill(iArr, 0);
        try {
            plugin.onRequestPermissionResult(requestCode, permissions, iArr);
            plugin.onRequestPermissionsResult(requestCode, permissions, iArr);
        } catch (JSONException e) {
            LOG.e(LOG_TAG, "JSONException when delivering permissions results", e);
        }
    }
}
