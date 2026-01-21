package androidx.appcompat.widget;

import android.graphics.Rect;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface FitWindowsViewGroup {

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public interface OnFitSystemWindowsListener {
        void onFitSystemWindows(Rect rect);
    }

    void setOnFitSystemWindowsListener(OnFitSystemWindowsListener onFitSystemWindowsListener);
}
