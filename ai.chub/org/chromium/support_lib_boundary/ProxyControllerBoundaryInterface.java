package org.chromium.support_lib_boundary;

import java.util.concurrent.Executor;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface ProxyControllerBoundaryInterface {
    void clearProxyOverride(Runnable runnable, Executor executor);

    void setProxyOverride(String[][] strArr, String[] strArr2, Runnable runnable, Executor executor);

    void setProxyOverride(String[][] strArr, String[] strArr2, Runnable runnable, Executor executor, boolean z);
}
