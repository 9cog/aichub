package com.android.billingclient.api;

import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class ProductDetails {
    private final String zza;
    private final JSONObject zzb;
    private final String zzc;
    private final String zzd;
    private final String zze;
    private final String zzf;
    private final String zzg;
    private final String zzh;
    private final List zzi;

    /* compiled from: com.android.billingclient:billing@@5.0.0 */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class OneTimePurchaseOfferDetails {
        private final String zza;
        private final long zzb;
        private final String zzc;
        private final String zzd;
        private final String zze;

        OneTimePurchaseOfferDetails(JSONObject jSONObject) {
            this.zza = jSONObject.optString("formattedPrice");
            this.zzb = jSONObject.optLong("priceAmountMicros");
            this.zzc = jSONObject.optString("priceCurrencyCode");
            this.zzd = jSONObject.optString("offerIdToken");
            this.zze = jSONObject.optString("offerId");
            jSONObject.optInt("offerType");
        }

        public String getFormattedPrice() {
            return this.zza;
        }

        public long getPriceAmountMicros() {
            return this.zzb;
        }

        public String getPriceCurrencyCode() {
            return this.zzc;
        }

        public final String zza() {
            return this.zzd;
        }
    }

    /* compiled from: com.android.billingclient:billing@@5.0.0 */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class PricingPhase {
        private final int billingCycleCount;
        private final String billingPeriod;
        private final String formattedPrice;
        private final long priceAmountMicros;
        private final String priceCurrencyCode;
        private final int recurrenceMode;

        PricingPhase(JSONObject jSONObject) {
            this.billingPeriod = jSONObject.optString("billingPeriod");
            this.priceCurrencyCode = jSONObject.optString("priceCurrencyCode");
            this.formattedPrice = jSONObject.optString("formattedPrice");
            this.priceAmountMicros = jSONObject.optLong("priceAmountMicros");
            this.recurrenceMode = jSONObject.optInt("recurrenceMode");
            this.billingCycleCount = jSONObject.optInt("billingCycleCount");
        }

        public int getBillingCycleCount() {
            return this.billingCycleCount;
        }

        public String getBillingPeriod() {
            return this.billingPeriod;
        }

        public String getFormattedPrice() {
            return this.formattedPrice;
        }

        public long getPriceAmountMicros() {
            return this.priceAmountMicros;
        }

        public String getPriceCurrencyCode() {
            return this.priceCurrencyCode;
        }

        public int getRecurrenceMode() {
            return this.recurrenceMode;
        }
    }

    /* compiled from: com.android.billingclient:billing@@5.0.0 */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class PricingPhases {
        private final List<PricingPhase> pricingPhaseList;

        PricingPhases(JSONArray jSONArray) {
            ArrayList arrayList = new ArrayList();
            if (jSONArray != null) {
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject optJSONObject = jSONArray.optJSONObject(i);
                    if (optJSONObject != null) {
                        arrayList.add(new PricingPhase(optJSONObject));
                    }
                }
            }
            this.pricingPhaseList = arrayList;
        }

        public List<PricingPhase> getPricingPhaseList() {
            return this.pricingPhaseList;
        }
    }

    /* compiled from: com.android.billingclient:billing@@5.0.0 */
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public @interface RecurrenceMode {
        public static final int FINITE_RECURRING = 2;
        public static final int INFINITE_RECURRING = 1;
        public static final int NON_RECURRING = 3;
    }

    /* compiled from: com.android.billingclient:billing@@5.0.0 */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class SubscriptionOfferDetails {
        private final zzbg installmentPlanDetails;
        private final List<String> offerTags;
        private final String offerToken;
        private final PricingPhases pricingPhases;

        SubscriptionOfferDetails(JSONObject jSONObject) throws JSONException {
            this.offerToken = jSONObject.getString("offerIdToken");
            this.pricingPhases = new PricingPhases(jSONObject.getJSONArray("pricingPhases"));
            JSONObject optJSONObject = jSONObject.optJSONObject("installmentPlanDetails");
            this.installmentPlanDetails = optJSONObject == null ? null : new zzbg(optJSONObject);
            ArrayList arrayList = new ArrayList();
            JSONArray optJSONArray = jSONObject.optJSONArray("offerTags");
            if (optJSONArray != null) {
                for (int i = 0; i < optJSONArray.length(); i++) {
                    arrayList.add(optJSONArray.getString(i));
                }
            }
            this.offerTags = arrayList;
        }

        public zzbg getInstallmentPlanDetails() {
            return this.installmentPlanDetails;
        }

        public List<String> getOfferTags() {
            return this.offerTags;
        }

        public String getOfferToken() {
            return this.offerToken;
        }

        public PricingPhases getPricingPhases() {
            return this.pricingPhases;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProductDetails(String str) throws JSONException {
        this.zza = str;
        JSONObject jSONObject = new JSONObject(str);
        this.zzb = jSONObject;
        String optString = jSONObject.optString("productId");
        this.zzc = optString;
        String optString2 = jSONObject.optString("type");
        this.zzd = optString2;
        if (TextUtils.isEmpty(optString)) {
            throw new IllegalArgumentException("Product id cannot be empty.");
        }
        if (TextUtils.isEmpty(optString2)) {
            throw new IllegalArgumentException("Product type cannot be empty.");
        }
        this.zze = jSONObject.optString("title");
        this.zzf = jSONObject.optString("name");
        this.zzg = jSONObject.optString("description");
        this.zzh = jSONObject.optString("skuDetailsToken");
        if (optString2.equals("inapp")) {
            this.zzi = null;
            return;
        }
        ArrayList arrayList = new ArrayList();
        JSONArray optJSONArray = jSONObject.optJSONArray("subscriptionOfferDetails");
        if (optJSONArray != null) {
            for (int i = 0; i < optJSONArray.length(); i++) {
                arrayList.add(new SubscriptionOfferDetails(optJSONArray.getJSONObject(i)));
            }
        }
        this.zzi = arrayList;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ProductDetails) {
            return TextUtils.equals(this.zza, ((ProductDetails) obj).zza);
        }
        return false;
    }

    public String getDescription() {
        return this.zzg;
    }

    public String getName() {
        return this.zzf;
    }

    public OneTimePurchaseOfferDetails getOneTimePurchaseOfferDetails() {
        JSONObject optJSONObject = this.zzb.optJSONObject("oneTimePurchaseOfferDetails");
        if (optJSONObject != null) {
            return new OneTimePurchaseOfferDetails(optJSONObject);
        }
        return null;
    }

    public String getProductId() {
        return this.zzc;
    }

    public String getProductType() {
        return this.zzd;
    }

    public List<SubscriptionOfferDetails> getSubscriptionOfferDetails() {
        return this.zzi;
    }

    public String getTitle() {
        return this.zze;
    }

    public final int hashCode() {
        return this.zza.hashCode();
    }

    public final String toString() {
        String str = this.zza;
        String obj = this.zzb.toString();
        String str2 = this.zzc;
        String str3 = this.zzd;
        String str4 = this.zze;
        String str5 = this.zzh;
        String valueOf = String.valueOf(this.zzi);
        return "ProductDetails{jsonString='" + str + "', parsedJson=" + obj + ", productId='" + str2 + "', productType='" + str3 + "', title='" + str4 + "', productDetailsToken='" + str5 + "', subscriptionOfferDetails=" + valueOf + "}";
    }

    public final String zza() {
        return this.zzb.optString("packageName");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String zzb() {
        return this.zzh;
    }
}
