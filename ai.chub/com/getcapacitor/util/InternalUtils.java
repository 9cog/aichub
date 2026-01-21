package com.getcapacitor.util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class InternalUtils {
    public static PackageInfo getPackageInfo(PackageManager packageManager, String str) throws PackageManager.NameNotFoundException {
        return getPackageInfo(packageManager, str, 0L);
    }

    public static PackageInfo getPackageInfo(PackageManager packageManager, String str, long j) throws PackageManager.NameNotFoundException {
        if (Build.VERSION.SDK_INT >= 33) {
            return packageManager.getPackageInfo(str, PackageManager.PackageInfoFlags.of(j));
        }
        return getPackageInfoLegacy(packageManager, str, (int) j);
    }

    private static PackageInfo getPackageInfoLegacy(PackageManager packageManager, String str, long j) throws PackageManager.NameNotFoundException {
        return packageManager.getPackageInfo(str, (int) j);
    }
}
