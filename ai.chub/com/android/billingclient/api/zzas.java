package com.android.billingclient.api;

import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class zzas {
    private final List zza;
    private final BillingResult zzb;

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzas(BillingResult billingResult, List list) {
        this.zza = list;
        this.zzb = billingResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final BillingResult zza() {
        return this.zzb;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final List zzb() {
        return this.zza;
    }
}
