package com.capicitor_subscriptions.capacitor;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import androidx.browser.trusted.sharing.ShareTarget;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchaseHistoryParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import okhttp3.HttpUrl;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class Subscriptions {
    private Activity activity;
    private BillingClient billingClient;
    public Context context;
    private SubscriptionsPlugin plugin;
    private int billingClientIsConnected = 0;
    private String googleVerifyEndpoint = HttpUrl.FRAGMENT_ENCODE_SET;
    private String googleBid = HttpUrl.FRAGMENT_ENCODE_SET;

    public Subscriptions(SubscriptionsPlugin subscriptionsPlugin, BillingClient billingClient) {
        this.activity = null;
        this.context = null;
        this.plugin = subscriptionsPlugin;
        this.billingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() { // from class: com.capicitor_subscriptions.capacitor.Subscriptions.1
            @Override // com.android.billingclient.api.BillingClientStateListener
            public void onBillingServiceDisconnected() {
            }

            @Override // com.android.billingclient.api.BillingClientStateListener
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == 0) {
                    Subscriptions.this.billingClientIsConnected = 1;
                    return;
                }
                Subscriptions.this.billingClientIsConnected = billingResult.getResponseCode();
            }
        });
        this.activity = subscriptionsPlugin.getActivity();
        this.context = subscriptionsPlugin.getContext();
    }

    public String echo(String str) {
        Log.i("Echo", str);
        return str;
    }

    public void setGoogleVerificationDetails(String str, String str2) {
        this.googleVerifyEndpoint = str;
        this.googleBid = str2;
        Log.i("SET-VERIFY", "Verification values updated");
    }

    public void getProductDetails(String str, final PluginCall pluginCall) {
        List<QueryProductDetailsParams.Product> m;
        final JSObject jSObject = new JSObject();
        int i = this.billingClientIsConnected;
        if (i == 1) {
            QueryProductDetailsParams.Product build = QueryProductDetailsParams.Product.newBuilder().setProductId(str).setProductType("subs").build();
            QueryProductDetailsParams.Builder newBuilder = QueryProductDetailsParams.newBuilder();
            m = Subscriptions$$ExternalSyntheticBackport0.m(new Object[]{build});
            this.billingClient.queryProductDetailsAsync(newBuilder.setProductList(m).build(), new ProductDetailsResponseListener() { // from class: com.capicitor_subscriptions.capacitor.Subscriptions$$ExternalSyntheticLambda5
                @Override // com.android.billingclient.api.ProductDetailsResponseListener
                public final void onProductDetailsResponse(BillingResult billingResult, List list) {
                    Subscriptions.lambda$getProductDetails$0(JSObject.this, pluginCall, billingResult, list);
                }
            });
        } else if (i == 2) {
            jSObject.put("responseCode", 500);
            jSObject.put("responseMessage", "Android: BillingClient failed to initialise");
            pluginCall.resolve(jSObject);
        } else {
            jSObject.put("responseCode", i);
            jSObject.put("responseMessage", "Android: BillingClient failed to initialise");
            jSObject.put("responseCode", 503);
            jSObject.put("responseMessage", "Android: BillingClient is still initialising");
            pluginCall.resolve(jSObject);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getProductDetails$0(JSObject jSObject, PluginCall pluginCall, BillingResult billingResult, List list) {
        try {
            ProductDetails productDetails = (ProductDetails) list.get(0);
            String productId = productDetails.getProductId();
            String title = productDetails.getTitle();
            String description = productDetails.getDescription();
            Log.i("productIdentifier", productId);
            Log.i("displayName", title);
            Log.i("desc", description);
            String formattedPrice = productDetails.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
            JSObject jSObject2 = new JSObject();
            jSObject2.put("productIdentifier", productId);
            jSObject2.put("displayName", title);
            jSObject2.put("description", description);
            jSObject2.put("price", formattedPrice);
            jSObject.put("responseCode", 0);
            jSObject.put("responseMessage", "Successfully found the product details for given productIdentifier");
            jSObject.put("data", (Object) jSObject2);
        } catch (Exception e) {
            Log.e("Err", e.toString());
            jSObject.put("responseCode", 1);
            jSObject.put("responseMessage", "Could not find a product matching the given productIdentifier");
        }
        pluginCall.resolve(jSObject);
    }

    public void getLatestTransaction(final String str, final PluginCall pluginCall) {
        final JSObject jSObject = new JSObject();
        if (this.billingClientIsConnected == 1) {
            this.billingClient.queryPurchaseHistoryAsync(QueryPurchaseHistoryParams.newBuilder().setProductType("subs").build(), new PurchaseHistoryResponseListener() { // from class: com.capicitor_subscriptions.capacitor.Subscriptions$$ExternalSyntheticLambda3
                @Override // com.android.billingclient.api.PurchaseHistoryResponseListener
                public final void onPurchaseHistoryResponse(BillingResult billingResult, List list) {
                    Subscriptions.this.m76xb6f289ea(str, jSObject, pluginCall, billingResult, list);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$getLatestTransaction$1$com-capicitor_subscriptions-capacitor-Subscriptions  reason: not valid java name */
    public /* synthetic */ void m76xb6f289ea(String str, JSObject jSObject, PluginCall pluginCall, BillingResult billingResult, List list) {
        boolean z = false;
        for (int i = 0; list != null && i < list.size() && !z; i++) {
            try {
                JSObject jSObject2 = new JSObject(((PurchaseHistoryRecord) list.get(i)).getOriginalJson());
                Log.i("PurchaseHistory", jSObject2.toString());
                if (jSObject2.get("productId").equals(str)) {
                    z = true;
                    JSObject jSObject3 = new JSObject();
                    try {
                        String expiryDateFromGoogle = getExpiryDateFromGoogle(str, jSObject2.get("purchaseToken").toString());
                        if (expiryDateFromGoogle != null) {
                            jSObject3.put("expiryDate", expiryDateFromGoogle);
                        }
                        new SimpleDateFormat("dd-MM-yyyy hh:mm");
                        Calendar.getInstance().setTimeInMillis(Long.parseLong(jSObject2.get("purchaseTime").toString()));
                        jSObject3.put("productIdentifier", jSObject2.get("productId"));
                        jSObject3.put("originalId", jSObject2.get("orderId"));
                        jSObject3.put("transactionId", jSObject2.get("orderId"));
                        jSObject.put("responseCode", 0);
                        jSObject.put("responseMessage", "Successfully found the latest transaction matching given productIdentifier");
                        jSObject.put("data", (Object) jSObject3);
                    } catch (Exception e) {
                        e = e;
                        e.printStackTrace();
                    }
                }
            } catch (Exception e2) {
                e = e2;
            }
        }
        if (!z) {
            jSObject.put("responseCode", 3);
            jSObject.put("responseMessage", "No transaction for given productIdentifier, or it could not be verified");
        }
        pluginCall.resolve(jSObject);
    }

    public void getCurrentEntitlements(final PluginCall pluginCall) {
        final JSObject jSObject = new JSObject();
        if (this.billingClientIsConnected == 1) {
            this.billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType("subs").build(), new PurchasesResponseListener() { // from class: com.capicitor_subscriptions.capacitor.Subscriptions$$ExternalSyntheticLambda4
                @Override // com.android.billingclient.api.PurchasesResponseListener
                public final void onQueryPurchasesResponse(BillingResult billingResult, List list) {
                    Subscriptions.this.m75xa8793d3(jSObject, pluginCall, billingResult, list);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$getCurrentEntitlements$2$com-capicitor_subscriptions-capacitor-Subscriptions  reason: not valid java name */
    public /* synthetic */ void m75xa8793d3(JSObject jSObject, PluginCall pluginCall, BillingResult billingResult, List list) {
        try {
            if (Integer.valueOf(list.size()).intValue() > 0) {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    Purchase purchase = (Purchase) list.get(i);
                    String expiryDateFromGoogle = getExpiryDateFromGoogle(purchase.getProducts().get(0), purchase.getPurchaseToken());
                    String orderId = purchase.getOrderId();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(String.valueOf(purchase.getPurchaseTime())));
                    arrayList.add(new JSObject().put("productIdentifier", purchase.getProducts().get(0)).put("expiryDate", expiryDateFromGoogle).put("originalStartDate", simpleDateFormat.format(calendar.getTime())).put("originalId", orderId).put("transactionId", orderId));
                }
                jSObject.put("responseCode", 0);
                jSObject.put("responseMessage", "Successfully found all entitlements across all product types");
                jSObject.put("data", (Object) arrayList);
            } else {
                Log.i("No Purchases", "No active subscriptions found");
                jSObject.put("responseCode", 1);
                jSObject.put("responseMessage", "No entitlements were found");
            }
            pluginCall.resolve(jSObject);
        } catch (Exception e) {
            Log.e("Error", e.toString());
            jSObject.put("responseCode", 2);
            jSObject.put("responseMessage", e.toString());
        }
        pluginCall.resolve(jSObject);
    }

    public void purchaseProduct(String str, final PluginCall pluginCall) {
        List<QueryProductDetailsParams.Product> m;
        final JSObject jSObject = new JSObject();
        if (this.billingClientIsConnected == 1) {
            QueryProductDetailsParams.Product build = QueryProductDetailsParams.Product.newBuilder().setProductId(str).setProductType("subs").build();
            QueryProductDetailsParams.Builder newBuilder = QueryProductDetailsParams.newBuilder();
            m = Subscriptions$$ExternalSyntheticBackport0.m(new Object[]{build});
            this.billingClient.queryProductDetailsAsync(newBuilder.setProductList(m).build(), new ProductDetailsResponseListener() { // from class: com.capicitor_subscriptions.capacitor.Subscriptions$$ExternalSyntheticLambda2
                @Override // com.android.billingclient.api.ProductDetailsResponseListener
                public final void onProductDetailsResponse(BillingResult billingResult, List list) {
                    Subscriptions.this.m77xb637719f(jSObject, pluginCall, billingResult, list);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$purchaseProduct$3$com-capicitor_subscriptions-capacitor-Subscriptions  reason: not valid java name */
    public /* synthetic */ void m77xb637719f(JSObject jSObject, PluginCall pluginCall, BillingResult billingResult, List list) {
        List<BillingFlowParams.ProductDetailsParams> m;
        try {
            ProductDetails productDetails = (ProductDetails) list.get(0);
            BillingFlowParams.Builder newBuilder = BillingFlowParams.newBuilder();
            m = Subscriptions$$ExternalSyntheticBackport0.m(new Object[]{BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(productDetails).setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken()).build()});
            Log.i("RESULT", this.billingClient.launchBillingFlow(this.activity, newBuilder.setProductDetailsParamsList(m).build()).toString());
            jSObject.put("responseCode", 0);
            jSObject.put("responseMessage", "Successfully opened native popover");
        } catch (Exception e) {
            e.printStackTrace();
            jSObject.put("responseCode", 1);
            jSObject.put("responseMessage", "Failed to open native popover");
        }
        pluginCall.resolve(jSObject);
    }

    private String getExpiryDateFromGoogle(String str, String str2) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(this.googleVerifyEndpoint + "?bid=" + this.googleBid + "&subId=" + str + "&purchaseToken=" + str2).openConnection();
            httpURLConnection.setRequestMethod(ShareTarget.METHOD_GET);
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    sb.append(readLine.trim());
                    Log.i("Response Line", readLine);
                }
                if (httpURLConnection.getResponseCode() != 200) {
                    bufferedReader.close();
                    return null;
                }
                JSObject jSObject = new JSObject(new JSObject(new JSObject(sb.toString()).get("googleResponce").toString()).get("payload").toString());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z '('z')'");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(jSObject.get("expiryTimeMillis").toString()));
                Log.i("EXPIRY", simpleDateFormat.format(calendar.getTime()));
                String format = simpleDateFormat.format(calendar.getTime());
                bufferedReader.close();
                return format;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }
}
