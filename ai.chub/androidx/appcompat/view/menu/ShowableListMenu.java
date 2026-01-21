package androidx.appcompat.view.menu;

import android.widget.ListView;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface ShowableListMenu {
    void dismiss();

    ListView getListView();

    boolean isShowing();

    void show();
}
