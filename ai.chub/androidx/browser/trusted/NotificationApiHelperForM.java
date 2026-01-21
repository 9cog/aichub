package androidx.browser.trusted;

import android.app.NotificationManager;
import android.os.Parcelable;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class NotificationApiHelperForM {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static Parcelable[] getActiveNotifications(NotificationManager notificationManager) {
        return notificationManager.getActiveNotifications();
    }

    private NotificationApiHelperForM() {
    }
}
