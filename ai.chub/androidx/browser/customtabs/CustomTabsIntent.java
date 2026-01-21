package androidx.browser.customtabs;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.RemoteViews;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsSession;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.BundleCompat;
import androidx.core.content.ContextCompat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class CustomTabsIntent {
    public static final int ACTIVITY_HEIGHT_ADJUSTABLE = 1;
    public static final int ACTIVITY_HEIGHT_DEFAULT = 0;
    public static final int ACTIVITY_HEIGHT_FIXED = 2;
    private static final int ACTIVITY_HEIGHT_MAX = 2;
    public static final int CLOSE_BUTTON_POSITION_DEFAULT = 0;
    public static final int CLOSE_BUTTON_POSITION_END = 2;
    private static final int CLOSE_BUTTON_POSITION_MAX = 2;
    public static final int CLOSE_BUTTON_POSITION_START = 1;
    public static final int COLOR_SCHEME_DARK = 2;
    public static final int COLOR_SCHEME_LIGHT = 1;
    private static final int COLOR_SCHEME_MAX = 2;
    public static final int COLOR_SCHEME_SYSTEM = 0;
    public static final String EXTRA_ACTION_BUTTON_BUNDLE = "android.support.customtabs.extra.ACTION_BUTTON_BUNDLE";
    public static final String EXTRA_ACTIVITY_HEIGHT_RESIZE_BEHAVIOR = "androidx.browser.customtabs.extra.ACTIVITY_HEIGHT_RESIZE_BEHAVIOR";
    public static final String EXTRA_CLOSE_BUTTON_ICON = "android.support.customtabs.extra.CLOSE_BUTTON_ICON";
    public static final String EXTRA_CLOSE_BUTTON_POSITION = "androidx.browser.customtabs.extra.CLOSE_BUTTON_POSITION";
    public static final String EXTRA_COLOR_SCHEME = "androidx.browser.customtabs.extra.COLOR_SCHEME";
    public static final String EXTRA_COLOR_SCHEME_PARAMS = "androidx.browser.customtabs.extra.COLOR_SCHEME_PARAMS";
    @Deprecated
    public static final String EXTRA_DEFAULT_SHARE_MENU_ITEM = "android.support.customtabs.extra.SHARE_MENU_ITEM";
    public static final String EXTRA_ENABLE_INSTANT_APPS = "android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS";
    public static final String EXTRA_ENABLE_URLBAR_HIDING = "android.support.customtabs.extra.ENABLE_URLBAR_HIDING";
    public static final String EXTRA_EXIT_ANIMATION_BUNDLE = "android.support.customtabs.extra.EXIT_ANIMATION_BUNDLE";
    public static final String EXTRA_INITIAL_ACTIVITY_HEIGHT_PX = "androidx.browser.customtabs.extra.INITIAL_ACTIVITY_HEIGHT_PX";
    public static final String EXTRA_MENU_ITEMS = "android.support.customtabs.extra.MENU_ITEMS";
    public static final String EXTRA_NAVIGATION_BAR_COLOR = "androidx.browser.customtabs.extra.NAVIGATION_BAR_COLOR";
    public static final String EXTRA_NAVIGATION_BAR_DIVIDER_COLOR = "androidx.browser.customtabs.extra.NAVIGATION_BAR_DIVIDER_COLOR";
    public static final String EXTRA_REMOTEVIEWS = "android.support.customtabs.extra.EXTRA_REMOTEVIEWS";
    public static final String EXTRA_REMOTEVIEWS_CLICKED_ID = "android.support.customtabs.extra.EXTRA_REMOTEVIEWS_CLICKED_ID";
    public static final String EXTRA_REMOTEVIEWS_PENDINGINTENT = "android.support.customtabs.extra.EXTRA_REMOTEVIEWS_PENDINGINTENT";
    public static final String EXTRA_REMOTEVIEWS_VIEW_IDS = "android.support.customtabs.extra.EXTRA_REMOTEVIEWS_VIEW_IDS";
    public static final String EXTRA_SECONDARY_TOOLBAR_COLOR = "android.support.customtabs.extra.SECONDARY_TOOLBAR_COLOR";
    public static final String EXTRA_SESSION = "android.support.customtabs.extra.SESSION";
    public static final String EXTRA_SESSION_ID = "android.support.customtabs.extra.SESSION_ID";
    public static final String EXTRA_SHARE_STATE = "androidx.browser.customtabs.extra.SHARE_STATE";
    public static final String EXTRA_TINT_ACTION_BUTTON = "android.support.customtabs.extra.TINT_ACTION_BUTTON";
    public static final String EXTRA_TITLE_VISIBILITY_STATE = "android.support.customtabs.extra.TITLE_VISIBILITY";
    public static final String EXTRA_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR";
    public static final String EXTRA_TOOLBAR_CORNER_RADIUS_DP = "androidx.browser.customtabs.extra.TOOLBAR_CORNER_RADIUS_DP";
    public static final String EXTRA_TOOLBAR_ITEMS = "android.support.customtabs.extra.TOOLBAR_ITEMS";
    private static final String EXTRA_USER_OPT_OUT_FROM_CUSTOM_TABS = "android.support.customtabs.extra.user_opt_out";
    private static final String HTTP_ACCEPT_LANGUAGE = "Accept-Language";
    public static final String KEY_DESCRIPTION = "android.support.customtabs.customaction.DESCRIPTION";
    public static final String KEY_ICON = "android.support.customtabs.customaction.ICON";
    public static final String KEY_ID = "android.support.customtabs.customaction.ID";
    public static final String KEY_MENU_ITEM_TITLE = "android.support.customtabs.customaction.MENU_ITEM_TITLE";
    public static final String KEY_PENDING_INTENT = "android.support.customtabs.customaction.PENDING_INTENT";
    private static final int MAX_TOOLBAR_CORNER_RADIUS_DP = 16;
    private static final int MAX_TOOLBAR_ITEMS = 5;
    public static final int NO_TITLE = 0;
    public static final int SHARE_STATE_DEFAULT = 0;
    private static final int SHARE_STATE_MAX = 2;
    public static final int SHARE_STATE_OFF = 2;
    public static final int SHARE_STATE_ON = 1;
    public static final int SHOW_PAGE_TITLE = 1;
    public static final int TOOLBAR_ACTION_BUTTON_ID = 0;
    public final Intent intent;
    public final Bundle startAnimationBundle;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public @interface ActivityHeightResizeBehavior {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public @interface CloseButtonPosition {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public @interface ColorScheme {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public @interface ShareState {
    }

    public static int getMaxToolbarItems() {
        return 5;
    }

    public void launchUrl(Context context, Uri uri) {
        this.intent.setData(uri);
        ContextCompat.startActivity(context, this.intent, this.startAnimationBundle);
    }

    CustomTabsIntent(Intent intent, Bundle bundle) {
        this.intent = intent;
        this.startAnimationBundle = bundle;
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class Builder {
        private ArrayList<Bundle> mActionButtons;
        private SparseArray<Bundle> mColorSchemeParamBundles;
        private Bundle mDefaultColorSchemeBundle;
        private ArrayList<Bundle> mMenuItems;
        private Bundle mStartAnimationBundle;
        private final Intent mIntent = new Intent("android.intent.action.VIEW");
        private final CustomTabColorSchemeParams.Builder mDefaultColorSchemeBuilder = new CustomTabColorSchemeParams.Builder();
        private int mShareState = 0;
        private boolean mInstantAppsEnabled = true;

        public Builder() {
        }

        public Builder(CustomTabsSession customTabsSession) {
            if (customTabsSession != null) {
                setSession(customTabsSession);
            }
        }

        public Builder setSession(CustomTabsSession customTabsSession) {
            this.mIntent.setPackage(customTabsSession.getComponentName().getPackageName());
            setSessionParameters(customTabsSession.getBinder(), customTabsSession.getId());
            return this;
        }

        public Builder setPendingSession(CustomTabsSession.PendingSession pendingSession) {
            setSessionParameters(null, pendingSession.getId());
            return this;
        }

        private void setSessionParameters(IBinder iBinder, PendingIntent pendingIntent) {
            Bundle bundle = new Bundle();
            BundleCompat.putBinder(bundle, CustomTabsIntent.EXTRA_SESSION, iBinder);
            if (pendingIntent != null) {
                bundle.putParcelable(CustomTabsIntent.EXTRA_SESSION_ID, pendingIntent);
            }
            this.mIntent.putExtras(bundle);
        }

        @Deprecated
        public Builder setToolbarColor(int i) {
            this.mDefaultColorSchemeBuilder.setToolbarColor(i);
            return this;
        }

        @Deprecated
        public Builder enableUrlBarHiding() {
            this.mIntent.putExtra(CustomTabsIntent.EXTRA_ENABLE_URLBAR_HIDING, true);
            return this;
        }

        public Builder setUrlBarHidingEnabled(boolean z) {
            this.mIntent.putExtra(CustomTabsIntent.EXTRA_ENABLE_URLBAR_HIDING, z);
            return this;
        }

        public Builder setCloseButtonIcon(Bitmap bitmap) {
            this.mIntent.putExtra(CustomTabsIntent.EXTRA_CLOSE_BUTTON_ICON, bitmap);
            return this;
        }

        public Builder setShowTitle(boolean z) {
            this.mIntent.putExtra(CustomTabsIntent.EXTRA_TITLE_VISIBILITY_STATE, z ? 1 : 0);
            return this;
        }

        public Builder addMenuItem(String str, PendingIntent pendingIntent) {
            if (this.mMenuItems == null) {
                this.mMenuItems = new ArrayList<>();
            }
            Bundle bundle = new Bundle();
            bundle.putString(CustomTabsIntent.KEY_MENU_ITEM_TITLE, str);
            bundle.putParcelable(CustomTabsIntent.KEY_PENDING_INTENT, pendingIntent);
            this.mMenuItems.add(bundle);
            return this;
        }

        @Deprecated
        public Builder addDefaultShareMenuItem() {
            setShareState(1);
            return this;
        }

        @Deprecated
        public Builder setDefaultShareMenuItemEnabled(boolean z) {
            if (z) {
                setShareState(1);
            } else {
                setShareState(2);
            }
            return this;
        }

        public Builder setShareState(int i) {
            if (i < 0 || i > 2) {
                throw new IllegalArgumentException("Invalid value for the shareState argument");
            }
            this.mShareState = i;
            if (i == 1) {
                this.mIntent.putExtra(CustomTabsIntent.EXTRA_DEFAULT_SHARE_MENU_ITEM, true);
            } else if (i == 2) {
                this.mIntent.putExtra(CustomTabsIntent.EXTRA_DEFAULT_SHARE_MENU_ITEM, false);
            } else {
                this.mIntent.removeExtra(CustomTabsIntent.EXTRA_DEFAULT_SHARE_MENU_ITEM);
            }
            return this;
        }

        public Builder setActionButton(Bitmap bitmap, String str, PendingIntent pendingIntent, boolean z) {
            Bundle bundle = new Bundle();
            bundle.putInt(CustomTabsIntent.KEY_ID, 0);
            bundle.putParcelable(CustomTabsIntent.KEY_ICON, bitmap);
            bundle.putString(CustomTabsIntent.KEY_DESCRIPTION, str);
            bundle.putParcelable(CustomTabsIntent.KEY_PENDING_INTENT, pendingIntent);
            this.mIntent.putExtra(CustomTabsIntent.EXTRA_ACTION_BUTTON_BUNDLE, bundle);
            this.mIntent.putExtra(CustomTabsIntent.EXTRA_TINT_ACTION_BUTTON, z);
            return this;
        }

        public Builder setActionButton(Bitmap bitmap, String str, PendingIntent pendingIntent) {
            return setActionButton(bitmap, str, pendingIntent, false);
        }

        @Deprecated
        public Builder addToolbarItem(int i, Bitmap bitmap, String str, PendingIntent pendingIntent) throws IllegalStateException {
            if (this.mActionButtons == null) {
                this.mActionButtons = new ArrayList<>();
            }
            if (this.mActionButtons.size() >= 5) {
                throw new IllegalStateException("Exceeded maximum toolbar item count of 5");
            }
            Bundle bundle = new Bundle();
            bundle.putInt(CustomTabsIntent.KEY_ID, i);
            bundle.putParcelable(CustomTabsIntent.KEY_ICON, bitmap);
            bundle.putString(CustomTabsIntent.KEY_DESCRIPTION, str);
            bundle.putParcelable(CustomTabsIntent.KEY_PENDING_INTENT, pendingIntent);
            this.mActionButtons.add(bundle);
            return this;
        }

        @Deprecated
        public Builder setSecondaryToolbarColor(int i) {
            this.mDefaultColorSchemeBuilder.setSecondaryToolbarColor(i);
            return this;
        }

        @Deprecated
        public Builder setNavigationBarColor(int i) {
            this.mDefaultColorSchemeBuilder.setNavigationBarColor(i);
            return this;
        }

        @Deprecated
        public Builder setNavigationBarDividerColor(int i) {
            this.mDefaultColorSchemeBuilder.setNavigationBarDividerColor(i);
            return this;
        }

        public Builder setSecondaryToolbarViews(RemoteViews remoteViews, int[] iArr, PendingIntent pendingIntent) {
            this.mIntent.putExtra(CustomTabsIntent.EXTRA_REMOTEVIEWS, remoteViews);
            this.mIntent.putExtra(CustomTabsIntent.EXTRA_REMOTEVIEWS_VIEW_IDS, iArr);
            this.mIntent.putExtra(CustomTabsIntent.EXTRA_REMOTEVIEWS_PENDINGINTENT, pendingIntent);
            return this;
        }

        public Builder setInstantAppsEnabled(boolean z) {
            this.mInstantAppsEnabled = z;
            return this;
        }

        public Builder setStartAnimations(Context context, int i, int i2) {
            this.mStartAnimationBundle = ActivityOptionsCompat.makeCustomAnimation(context, i, i2).toBundle();
            return this;
        }

        public Builder setExitAnimations(Context context, int i, int i2) {
            this.mIntent.putExtra(CustomTabsIntent.EXTRA_EXIT_ANIMATION_BUNDLE, ActivityOptionsCompat.makeCustomAnimation(context, i, i2).toBundle());
            return this;
        }

        public Builder setColorScheme(int i) {
            if (i < 0 || i > 2) {
                throw new IllegalArgumentException("Invalid value for the colorScheme argument");
            }
            this.mIntent.putExtra(CustomTabsIntent.EXTRA_COLOR_SCHEME, i);
            return this;
        }

        public Builder setColorSchemeParams(int i, CustomTabColorSchemeParams customTabColorSchemeParams) {
            if (i < 0 || i > 2 || i == 0) {
                throw new IllegalArgumentException("Invalid colorScheme: " + i);
            }
            if (this.mColorSchemeParamBundles == null) {
                this.mColorSchemeParamBundles = new SparseArray<>();
            }
            this.mColorSchemeParamBundles.put(i, customTabColorSchemeParams.toBundle());
            return this;
        }

        public Builder setDefaultColorSchemeParams(CustomTabColorSchemeParams customTabColorSchemeParams) {
            this.mDefaultColorSchemeBundle = customTabColorSchemeParams.toBundle();
            return this;
        }

        public Builder setInitialActivityHeightPx(int i, int i2) {
            if (i > 0) {
                if (i2 < 0 || i2 > 2) {
                    throw new IllegalArgumentException("Invalid value for the activityHeightResizeBehavior argument");
                }
                this.mIntent.putExtra(CustomTabsIntent.EXTRA_INITIAL_ACTIVITY_HEIGHT_PX, i);
                this.mIntent.putExtra(CustomTabsIntent.EXTRA_ACTIVITY_HEIGHT_RESIZE_BEHAVIOR, i2);
                return this;
            }
            throw new IllegalArgumentException("Invalid value for the initialHeightPx argument");
        }

        public Builder setInitialActivityHeightPx(int i) {
            return setInitialActivityHeightPx(i, 0);
        }

        public Builder setToolbarCornerRadiusDp(int i) {
            if (i < 0 || i > 16) {
                throw new IllegalArgumentException("Invalid value for the cornerRadiusDp argument");
            }
            this.mIntent.putExtra(CustomTabsIntent.EXTRA_TOOLBAR_CORNER_RADIUS_DP, i);
            return this;
        }

        public Builder setCloseButtonPosition(int i) {
            if (i < 0 || i > 2) {
                throw new IllegalArgumentException("Invalid value for the position argument");
            }
            this.mIntent.putExtra(CustomTabsIntent.EXTRA_CLOSE_BUTTON_POSITION, i);
            return this;
        }

        public CustomTabsIntent build() {
            if (!this.mIntent.hasExtra(CustomTabsIntent.EXTRA_SESSION)) {
                setSessionParameters(null, null);
            }
            ArrayList<Bundle> arrayList = this.mMenuItems;
            if (arrayList != null) {
                this.mIntent.putParcelableArrayListExtra(CustomTabsIntent.EXTRA_MENU_ITEMS, arrayList);
            }
            ArrayList<Bundle> arrayList2 = this.mActionButtons;
            if (arrayList2 != null) {
                this.mIntent.putParcelableArrayListExtra(CustomTabsIntent.EXTRA_TOOLBAR_ITEMS, arrayList2);
            }
            this.mIntent.putExtra(CustomTabsIntent.EXTRA_ENABLE_INSTANT_APPS, this.mInstantAppsEnabled);
            this.mIntent.putExtras(this.mDefaultColorSchemeBuilder.build().toBundle());
            Bundle bundle = this.mDefaultColorSchemeBundle;
            if (bundle != null) {
                this.mIntent.putExtras(bundle);
            }
            if (this.mColorSchemeParamBundles != null) {
                Bundle bundle2 = new Bundle();
                bundle2.putSparseParcelableArray(CustomTabsIntent.EXTRA_COLOR_SCHEME_PARAMS, this.mColorSchemeParamBundles);
                this.mIntent.putExtras(bundle2);
            }
            this.mIntent.putExtra(CustomTabsIntent.EXTRA_SHARE_STATE, this.mShareState);
            if (Build.VERSION.SDK_INT >= 24) {
                setCurrentLocaleAsDefaultAcceptLanguage();
            }
            return new CustomTabsIntent(this.mIntent, this.mStartAnimationBundle);
        }

        private void setCurrentLocaleAsDefaultAcceptLanguage() {
            String defaultLocale = Api24Impl.getDefaultLocale();
            if (TextUtils.isEmpty(defaultLocale)) {
                return;
            }
            Bundle bundleExtra = this.mIntent.hasExtra("com.android.browser.headers") ? this.mIntent.getBundleExtra("com.android.browser.headers") : new Bundle();
            if (bundleExtra.containsKey(CustomTabsIntent.HTTP_ACCEPT_LANGUAGE)) {
                return;
            }
            bundleExtra.putString(CustomTabsIntent.HTTP_ACCEPT_LANGUAGE, defaultLocale);
            this.mIntent.putExtra("com.android.browser.headers", bundleExtra);
        }
    }

    public static Intent setAlwaysUseBrowserUI(Intent intent) {
        if (intent == null) {
            intent = new Intent("android.intent.action.VIEW");
        }
        intent.addFlags(268435456);
        intent.putExtra(EXTRA_USER_OPT_OUT_FROM_CUSTOM_TABS, true);
        return intent;
    }

    public static boolean shouldAlwaysUseBrowserUI(Intent intent) {
        return intent.getBooleanExtra(EXTRA_USER_OPT_OUT_FROM_CUSTOM_TABS, false) && (intent.getFlags() & 268435456) != 0;
    }

    public static CustomTabColorSchemeParams getColorSchemeParams(Intent intent, int i) {
        Bundle bundle;
        if (i < 0 || i > 2 || i == 0) {
            throw new IllegalArgumentException("Invalid colorScheme: " + i);
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return CustomTabColorSchemeParams.fromBundle(null);
        }
        CustomTabColorSchemeParams fromBundle = CustomTabColorSchemeParams.fromBundle(extras);
        SparseArray sparseParcelableArray = extras.getSparseParcelableArray(EXTRA_COLOR_SCHEME_PARAMS);
        return (sparseParcelableArray == null || (bundle = (Bundle) sparseParcelableArray.get(i)) == null) ? fromBundle : CustomTabColorSchemeParams.fromBundle(bundle).withDefaults(fromBundle);
    }

    public static int getActivityResizeBehavior(Intent intent) {
        return intent.getIntExtra(EXTRA_ACTIVITY_HEIGHT_RESIZE_BEHAVIOR, 0);
    }

    public static int getInitialActivityHeightPx(Intent intent) {
        return intent.getIntExtra(EXTRA_INITIAL_ACTIVITY_HEIGHT_PX, 0);
    }

    public static int getToolbarCornerRadiusDp(Intent intent) {
        return intent.getIntExtra(EXTRA_TOOLBAR_CORNER_RADIUS_DP, 16);
    }

    public static int getCloseButtonPosition(Intent intent) {
        return intent.getIntExtra(EXTRA_CLOSE_BUTTON_POSITION, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class Api24Impl {
        private Api24Impl() {
        }

        static String getDefaultLocale() {
            LocaleList adjustedDefault = LocaleList.getAdjustedDefault();
            if (adjustedDefault.size() > 0) {
                return adjustedDefault.get(0).toLanguageTag();
            }
            return null;
        }
    }
}
