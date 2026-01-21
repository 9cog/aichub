package com.capacitorjs.plugins.device;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.webkit.WebView;
import androidx.core.app.NotificationCompat;
import net.openid.appauth.browser.Browsers;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class Device {
    private Context context;

    public String getPlatform() {
        return "android";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Device(Context context) {
        this.context = context;
    }

    public long getMemUsed() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public long getDiskFree() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        return statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
    }

    public long getDiskTotal() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        return statFs.getBlockCountLong() * statFs.getBlockSizeLong();
    }

    public long getRealDiskFree() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        return statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
    }

    public long getRealDiskTotal() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        return statFs.getBlockCountLong() * statFs.getBlockSizeLong();
    }

    public String getUuid() {
        return Settings.Secure.getString(this.context.getContentResolver(), "android_id");
    }

    public float getBatteryLevel() {
        int i;
        Intent registerReceiver = this.context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        int i2 = -1;
        if (registerReceiver != null) {
            int intExtra = registerReceiver.getIntExtra("level", -1);
            i = registerReceiver.getIntExtra("scale", -1);
            i2 = intExtra;
        } else {
            i = -1;
        }
        return i2 / i;
    }

    public boolean isCharging() {
        Intent registerReceiver = this.context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (registerReceiver != null) {
            int intExtra = registerReceiver.getIntExtra(NotificationCompat.CATEGORY_STATUS, -1);
            return intExtra == 2 || intExtra == 5;
        }
        return false;
    }

    public boolean isVirtual() {
        return Build.FINGERPRINT.contains("generic") || Build.PRODUCT.contains("sdk");
    }

    public String getName() {
        if (Build.VERSION.SDK_INT >= 25) {
            return Settings.Global.getString(this.context.getContentResolver(), "device_name");
        }
        return null;
    }

    public String getWebViewVersion() {
        PackageInfo packageInfo;
        if (Build.VERSION.SDK_INT >= 26) {
            packageInfo = WebView.getCurrentWebViewPackage();
        } else {
            try {
                packageInfo = getWebViewVersionSubAndroid26();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                packageInfo = null;
            }
        }
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return Build.VERSION.RELEASE;
    }

    private PackageInfo getWebViewVersionSubAndroid26() throws PackageManager.NameNotFoundException {
        return this.context.getPackageManager().getPackageInfo(Build.VERSION.SDK_INT >= 24 ? Browsers.Chrome.PACKAGE_NAME : "com.google.android.webview", 0);
    }
}
