package androidx.core.app;

import android.app.ActivityManager;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class ActivityManagerCompat {
    private ActivityManagerCompat() {
    }

    public static boolean isLowRamDevice(ActivityManager activityManager) {
        return activityManager.isLowRamDevice();
    }
}
