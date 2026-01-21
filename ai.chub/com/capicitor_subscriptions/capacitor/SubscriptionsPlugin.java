package com.capicitor_subscriptions.capacitor;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.List;
@CapacitorPlugin(name = "Subscriptions")
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class SubscriptionsPlugin extends Plugin {
    private BillingClient billingClient;
    private Subscriptions implementation;
    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() { // from class: com.capicitor_subscriptions.capacitor.SubscriptionsPlugin$$ExternalSyntheticLambda1
        @Override // com.android.billingclient.api.PurchasesUpdatedListener
        public final void onPurchasesUpdated(BillingResult billingResult, List list) {
            SubscriptionsPlugin.this.m79x530952a2(billingResult, list);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$new$1$com-capicitor_subscriptions-capacitor-SubscriptionsPlugin  reason: not valid java name */
    public /* synthetic */ void m79x530952a2(BillingResult billingResult, List list) {
        final JSObject jSObject = new JSObject();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                final Purchase purchase = (Purchase) list.get(i);
                if (!purchase.isAcknowledged() && billingResult.getResponseCode() == 0 && purchase.getPurchaseState() != 2) {
                    this.billingClient.acknowledgePurchase(AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(), new AcknowledgePurchaseResponseListener() { // from class: com.capicitor_subscriptions.capacitor.SubscriptionsPlugin$$ExternalSyntheticLambda0
                        @Override // com.android.billingclient.api.AcknowledgePurchaseResponseListener
                        public final void onAcknowledgePurchaseResponse(BillingResult billingResult2) {
                            SubscriptionsPlugin.this.m78x425385e1(purchase, jSObject, billingResult2);
                        }
                    });
                } else {
                    jSObject.put("successful", false);
                    notifyListeners("ANDROID-PURCHASE-RESPONSE", jSObject);
                }
            }
            return;
        }
        jSObject.put("successful", false);
        notifyListeners("ANDROID-PURCHASE-RESPONSE", jSObject);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$new$0$com-capicitor_subscriptions-capacitor-SubscriptionsPlugin  reason: not valid java name */
    public /* synthetic */ void m78x425385e1(Purchase purchase, JSObject jSObject, BillingResult billingResult) {
        Log.i("Purchase ack", purchase.getOriginalJson());
        billingResult.getResponseCode();
        jSObject.put("successful", true);
        notifyListeners("ANDROID-PURCHASE-RESPONSE", jSObject);
    }

    @Override // com.getcapacitor.Plugin
    public void load() {
        BillingClient build = BillingClient.newBuilder(getContext()).setListener(this.purchasesUpdatedListener).enablePendingPurchases().build();
        this.billingClient = build;
        this.implementation = new Subscriptions(this, build);
    }

    @PluginMethod
    public void setGoogleVerificationDetails(PluginCall pluginCall) {
        String string = pluginCall.getString("googleVerifyEndpoint");
        String string2 = pluginCall.getString("bid");
        if (string != null && string2 != null) {
            this.implementation.setGoogleVerificationDetails(string, string2);
        } else {
            pluginCall.reject("Missing required parameters");
        }
    }

    @PluginMethod
    public void echo(PluginCall pluginCall) {
        String string = pluginCall.getString("value");
        JSObject jSObject = new JSObject();
        jSObject.put("value", this.implementation.echo(string));
        pluginCall.resolve(jSObject);
    }

    @PluginMethod
    public void getProductDetails(PluginCall pluginCall) {
        String string = pluginCall.getString("productIdentifier");
        if (string == null) {
            pluginCall.reject("Must provide a productID");
        }
        this.implementation.getProductDetails(string, pluginCall);
    }

    @PluginMethod
    public void purchaseProduct(PluginCall pluginCall) {
        String string = pluginCall.getString("productIdentifier");
        if (string == null) {
            pluginCall.reject("Must provide a productID");
        }
        this.implementation.purchaseProduct(string, pluginCall);
    }

    @PluginMethod
    public void getLatestTransaction(PluginCall pluginCall) {
        String string = pluginCall.getString("productIdentifier");
        if (string == null) {
            pluginCall.reject("Must provide a productID");
        }
        this.implementation.getLatestTransaction(string, pluginCall);
    }

    @PluginMethod
    public void getCurrentEntitlements(PluginCall pluginCall) {
        this.implementation.getCurrentEntitlements(pluginCall);
    }

    @PluginMethod
    public void manageSubscriptions(PluginCall pluginCall) {
        String string = pluginCall.getString("productIdentifier");
        String string2 = pluginCall.getString("bid");
        if (string == null) {
            pluginCall.reject("Must provide a productID");
        }
        if (string2 == null) {
            pluginCall.reject("Must provide a bundleID");
        }
        getActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/account/subscriptions?sku=" + string + "&package=" + string2)));
    }
}
