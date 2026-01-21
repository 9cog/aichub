package com.android.billingclient.api;

import okhttp3.HttpUrl;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class BillingResult {
    private int zza;
    private String zzb;

    /* compiled from: com.android.billingclient:billing@@5.0.0 */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class Builder {
        private int zza;
        private String zzb = HttpUrl.FRAGMENT_ENCODE_SET;

        private Builder() {
        }

        /* synthetic */ Builder(zzba zzbaVar) {
        }

        public BillingResult build() {
            BillingResult billingResult = new BillingResult();
            billingResult.zza = this.zza;
            billingResult.zzb = this.zzb;
            return billingResult;
        }

        public Builder setDebugMessage(String str) {
            this.zzb = str;
            return this;
        }

        public Builder setResponseCode(int i) {
            this.zza = i;
            return this;
        }
    }

    public static Builder newBuilder() {
        return new Builder(null);
    }

    public String getDebugMessage() {
        return this.zzb;
    }

    public int getResponseCode() {
        return this.zza;
    }

    public String toString() {
        String zzl = com.google.android.gms.internal.play_billing.zzb.zzl(this.zza);
        String str = this.zzb;
        return "Response Code: " + zzl + ", Debug Message: " + str;
    }
}
