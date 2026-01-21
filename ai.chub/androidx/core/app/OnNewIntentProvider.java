package androidx.core.app;

import android.content.Intent;
import androidx.core.util.Consumer;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface OnNewIntentProvider {
    void addOnNewIntentListener(Consumer<Intent> consumer);

    void removeOnNewIntentListener(Consumer<Intent> consumer);
}
