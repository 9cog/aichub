package androidx.core.content;

import android.os.RemoteException;
import androidx.core.app.unusedapprestrictions.IUnusedAppRestrictionsBackportCallback;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class UnusedAppRestrictionsBackportCallback {
    private IUnusedAppRestrictionsBackportCallback mCallback;

    public UnusedAppRestrictionsBackportCallback(IUnusedAppRestrictionsBackportCallback iUnusedAppRestrictionsBackportCallback) {
        this.mCallback = iUnusedAppRestrictionsBackportCallback;
    }

    public void onResult(boolean z, boolean z2) throws RemoteException {
        this.mCallback.onIsPermissionRevocationEnabledForAppResult(z, z2);
    }
}
