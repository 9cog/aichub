package androidx.core.app;

import androidx.core.util.Consumer;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface OnMultiWindowModeChangedProvider {
    void addOnMultiWindowModeChangedListener(Consumer<MultiWindowModeChangedInfo> consumer);

    void removeOnMultiWindowModeChangedListener(Consumer<MultiWindowModeChangedInfo> consumer);
}
