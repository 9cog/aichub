package androidx.appcompat.widget;

import android.view.MenuItem;
import androidx.appcompat.view.menu.MenuBuilder;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface MenuItemHoverListener {
    void onItemHoverEnter(MenuBuilder menuBuilder, MenuItem menuItem);

    void onItemHoverExit(MenuBuilder menuBuilder, MenuItem menuItem);
}
