package org.chromium.support_lib_boundary;

import java.util.concurrent.Callable;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface IsomorphicObjectBoundaryInterface {
    Object getOrCreatePeer(Callable<Object> callable);
}
