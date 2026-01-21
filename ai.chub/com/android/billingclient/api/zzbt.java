package com.android.billingclient.api;

import android.text.TextUtils;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class zzbt {
    private String zza;

    private zzbt() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public /* synthetic */ zzbt(zzbs zzbsVar) {
    }

    public final zzbt zza(String str) {
        this.zza = str;
        return this;
    }

    public final zzbv zzb() {
        if (!TextUtils.isEmpty(this.zza)) {
            return new zzbv(this.zza, null, null, 0, null);
        }
        throw new IllegalArgumentException("SKU must be set.");
    }
}
