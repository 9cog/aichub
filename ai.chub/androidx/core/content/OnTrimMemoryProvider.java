package androidx.core.content;

import androidx.core.util.Consumer;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface OnTrimMemoryProvider {
    void addOnTrimMemoryListener(Consumer<Integer> consumer);

    void removeOnTrimMemoryListener(Consumer<Integer> consumer);
}
