package org.chromium.support_lib_boundary;

import java.lang.reflect.InvocationHandler;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface WebMessageBoundaryInterface extends FeatureFlagHolderBoundaryInterface {
    @Deprecated
    String getData();

    InvocationHandler getMessagePayload();

    InvocationHandler[] getPorts();
}
