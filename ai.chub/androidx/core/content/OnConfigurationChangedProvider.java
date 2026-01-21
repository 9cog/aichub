package androidx.core.content;

import android.content.res.Configuration;
import androidx.core.util.Consumer;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface OnConfigurationChangedProvider {
    void addOnConfigurationChangedListener(Consumer<Configuration> consumer);

    void removeOnConfigurationChangedListener(Consumer<Configuration> consumer);
}
