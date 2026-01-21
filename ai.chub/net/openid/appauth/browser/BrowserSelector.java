package net.openid.appauth.browser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import okhttp3.HttpUrl;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class BrowserSelector {
    static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";
    static final Intent BROWSER_INTENT = new Intent().setAction("android.intent.action.VIEW").addCategory("android.intent.category.BROWSABLE").setData(Uri.fromParts("http", HttpUrl.FRAGMENT_ENCODE_SET, null));
    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";

    /* JADX WARN: Removed duplicated region for block: B:33:0x007e A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x007a A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static List<BrowserDescriptor> getAllBrowsers(Context context) {
        int i;
        PackageManager packageManager = context.getPackageManager();
        ArrayList arrayList = new ArrayList();
        int i2 = Build.VERSION.SDK_INT >= 23 ? 131136 : 64;
        Intent intent = BROWSER_INTENT;
        ResolveInfo resolveActivity = packageManager.resolveActivity(intent, 0);
        String str = resolveActivity != null ? resolveActivity.activityInfo.packageName : null;
        for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(intent, i2)) {
            if (isFullBrowser(resolveInfo)) {
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(resolveInfo.activityInfo.packageName, 64);
                    if (hasWarmupService(packageManager, resolveInfo.activityInfo.packageName)) {
                        i = 1;
                        BrowserDescriptor browserDescriptor = new BrowserDescriptor(packageInfo, true);
                        if (resolveInfo.activityInfo.packageName.equals(str)) {
                            arrayList.add(0, browserDescriptor);
                            BrowserDescriptor browserDescriptor2 = new BrowserDescriptor(packageInfo, false);
                            if (!resolveInfo.activityInfo.packageName.equals(str)) {
                                arrayList.add(i, browserDescriptor2);
                            } else {
                                arrayList.add(browserDescriptor2);
                            }
                        } else {
                            arrayList.add(browserDescriptor);
                        }
                    }
                    i = 0;
                    BrowserDescriptor browserDescriptor22 = new BrowserDescriptor(packageInfo, false);
                    if (!resolveInfo.activityInfo.packageName.equals(str)) {
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                }
            }
        }
        return arrayList;
    }

    public static BrowserDescriptor select(Context context, BrowserMatcher browserMatcher) {
        BrowserDescriptor browserDescriptor = null;
        for (BrowserDescriptor browserDescriptor2 : getAllBrowsers(context)) {
            if (browserMatcher.matches(browserDescriptor2)) {
                if (browserDescriptor2.useCustomTab.booleanValue()) {
                    return browserDescriptor2;
                }
                if (browserDescriptor == null) {
                    browserDescriptor = browserDescriptor2;
                }
            }
        }
        return browserDescriptor;
    }

    private static boolean hasWarmupService(PackageManager pm, String packageName) {
        Intent intent = new Intent();
        intent.setAction("android.support.customtabs.action.CustomTabsService");
        intent.setPackage(packageName);
        return pm.resolveService(intent, 0) != null;
    }

    private static boolean isFullBrowser(ResolveInfo resolveInfo) {
        if (resolveInfo.filter != null && resolveInfo.filter.hasAction("android.intent.action.VIEW") && resolveInfo.filter.hasCategory("android.intent.category.BROWSABLE") && resolveInfo.filter.schemesIterator() != null && resolveInfo.filter.authoritiesIterator() == null) {
            Iterator<String> schemesIterator = resolveInfo.filter.schemesIterator();
            boolean z = false;
            boolean z2 = false;
            while (schemesIterator.hasNext()) {
                String next = schemesIterator.next();
                z |= "http".equals(next);
                z2 |= "https".equals(next);
                if (z && z2) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
