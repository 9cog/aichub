package org.chromium.support_lib_boundary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class ProcessGlobalConfigConstants {
    public static final String DATA_DIRECTORY_SUFFIX = "DATA_DIRECTORY_SUFFIX";

    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public @interface ProcessGlobalConfigMapKey {
    }

    private ProcessGlobalConfigConstants() {
    }
}
