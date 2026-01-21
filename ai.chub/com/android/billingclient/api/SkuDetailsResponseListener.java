package com.android.billingclient.api;

import java.util.List;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
@Deprecated
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface SkuDetailsResponseListener {
    void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list);
}
