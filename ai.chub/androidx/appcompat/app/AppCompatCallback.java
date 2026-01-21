package androidx.appcompat.app;

import androidx.appcompat.view.ActionMode;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface AppCompatCallback {
    void onSupportActionModeFinished(ActionMode actionMode);

    void onSupportActionModeStarted(ActionMode actionMode);

    ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback);
}
