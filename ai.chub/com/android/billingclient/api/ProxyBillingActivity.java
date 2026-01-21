package com.android.billingclient.api;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.ResultReceiver;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class ProxyBillingActivity extends Activity {
    static final String KEY_IN_APP_MESSAGE_RESULT_RECEIVER = "in_app_message_result_receiver";
    static final String KEY_PRICE_CHANGE_RESULT_RECEIVER = "result_receiver";
    private static final String KEY_SEND_CANCELLED_BROADCAST_IF_FINISHED = "send_cancelled_broadcast_if_finished";
    private static final int REQUEST_CODE_IN_APP_MESSAGE_FLOW = 101;
    private static final int REQUEST_CODE_LAUNCH_ACTIVITY = 100;
    private static final String TAG = "ProxyBillingActivity";
    private ResultReceiver inAppMessageResultReceiver;
    private ResultReceiver priceChangeResultReceiver;
    private boolean sendCancelledBroadcastIfFinished;

    private Intent makeAlternativeBillingIntent(String str) {
        Intent intent = new Intent("com.android.vending.billing.ALTERNATIVE_BILLING");
        intent.setPackage(getApplicationContext().getPackageName());
        intent.putExtra("ALTERNATIVE_BILLING_USER_CHOICE_DATA", str);
        return intent;
    }

    private Intent makePurchasesUpdatedIntent() {
        Intent intent = new Intent("com.android.vending.billing.PURCHASES_UPDATED");
        intent.setPackage(getApplicationContext().getPackageName());
        return intent;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0039  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0045  */
    @Override // android.app.Activity
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    protected void onActivityResult(int i, int i2, Intent intent) {
        ResultReceiver resultReceiver;
        Intent makePurchasesUpdatedIntent;
        super.onActivityResult(i, i2, intent);
        if (i == 100) {
            int responseCode = com.google.android.gms.internal.play_billing.zzb.zzi(intent, TAG).getResponseCode();
            if (i2 == -1) {
                if (responseCode != 0) {
                    i2 = -1;
                } else {
                    responseCode = 0;
                    resultReceiver = this.priceChangeResultReceiver;
                    if (resultReceiver == null) {
                        resultReceiver.send(responseCode, intent != null ? intent.getExtras() : null);
                    } else {
                        if (intent != null) {
                            if (intent.getExtras() != null) {
                                String string = intent.getExtras().getString("ALTERNATIVE_BILLING_USER_CHOICE_DATA");
                                if (string != null) {
                                    makePurchasesUpdatedIntent = makeAlternativeBillingIntent(string);
                                } else {
                                    makePurchasesUpdatedIntent = makePurchasesUpdatedIntent();
                                    makePurchasesUpdatedIntent.putExtras(intent.getExtras());
                                }
                            } else {
                                makePurchasesUpdatedIntent = makePurchasesUpdatedIntent();
                                com.google.android.gms.internal.play_billing.zzb.zzo(TAG, "Got null bundle!");
                                makePurchasesUpdatedIntent.putExtra("RESPONSE_CODE", 6);
                                makePurchasesUpdatedIntent.putExtra("DEBUG_MESSAGE", "An internal error occurred.");
                            }
                        } else {
                            makePurchasesUpdatedIntent = makePurchasesUpdatedIntent();
                        }
                        sendBroadcast(makePurchasesUpdatedIntent);
                    }
                }
            }
            com.google.android.gms.internal.play_billing.zzb.zzo(TAG, "Activity finished with resultCode " + i2 + " and billing's responseCode: " + responseCode);
            resultReceiver = this.priceChangeResultReceiver;
            if (resultReceiver == null) {
            }
        } else if (i == REQUEST_CODE_IN_APP_MESSAGE_FLOW) {
            int zza = com.google.android.gms.internal.play_billing.zzb.zza(intent, TAG);
            ResultReceiver resultReceiver2 = this.inAppMessageResultReceiver;
            if (resultReceiver2 != null) {
                resultReceiver2.send(zza, intent != null ? intent.getExtras() : null);
            }
        } else {
            com.google.android.gms.internal.play_billing.zzb.zzo(TAG, "Got onActivityResult with wrong requestCode: " + i + "; skipping...");
        }
        this.sendCancelledBroadcastIfFinished = false;
        finish();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        PendingIntent pendingIntent;
        int i;
        super.onCreate(bundle);
        if (bundle == null) {
            com.google.android.gms.internal.play_billing.zzb.zzn(TAG, "Launching Play Store billing flow");
            try {
                if (getIntent().hasExtra("BUY_INTENT")) {
                    pendingIntent = (PendingIntent) getIntent().getParcelableExtra("BUY_INTENT");
                } else if (getIntent().hasExtra("SUBS_MANAGEMENT_INTENT")) {
                    pendingIntent = (PendingIntent) getIntent().getParcelableExtra("SUBS_MANAGEMENT_INTENT");
                    this.priceChangeResultReceiver = (ResultReceiver) getIntent().getParcelableExtra(KEY_PRICE_CHANGE_RESULT_RECEIVER);
                } else if (getIntent().hasExtra("IN_APP_MESSAGE_INTENT")) {
                    pendingIntent = (PendingIntent) getIntent().getParcelableExtra("IN_APP_MESSAGE_INTENT");
                    this.inAppMessageResultReceiver = (ResultReceiver) getIntent().getParcelableExtra(KEY_IN_APP_MESSAGE_RESULT_RECEIVER);
                    i = REQUEST_CODE_IN_APP_MESSAGE_FLOW;
                    this.sendCancelledBroadcastIfFinished = true;
                    startIntentSenderForResult(pendingIntent.getIntentSender(), i, new Intent(), 0, 0, 0);
                    return;
                } else {
                    pendingIntent = null;
                }
                this.sendCancelledBroadcastIfFinished = true;
                startIntentSenderForResult(pendingIntent.getIntentSender(), i, new Intent(), 0, 0, 0);
                return;
            } catch (IntentSender.SendIntentException e) {
                com.google.android.gms.internal.play_billing.zzb.zzp(TAG, "Got exception while trying to start a purchase flow.", e);
                ResultReceiver resultReceiver = this.priceChangeResultReceiver;
                if (resultReceiver != null) {
                    resultReceiver.send(6, null);
                } else {
                    ResultReceiver resultReceiver2 = this.inAppMessageResultReceiver;
                    if (resultReceiver2 != null) {
                        resultReceiver2.send(0, null);
                    } else {
                        Intent makePurchasesUpdatedIntent = makePurchasesUpdatedIntent();
                        makePurchasesUpdatedIntent.putExtra("RESPONSE_CODE", 6);
                        makePurchasesUpdatedIntent.putExtra("DEBUG_MESSAGE", "An internal error occurred.");
                        sendBroadcast(makePurchasesUpdatedIntent);
                    }
                }
                this.sendCancelledBroadcastIfFinished = false;
                finish();
                return;
            }
            i = 100;
        } else {
            com.google.android.gms.internal.play_billing.zzb.zzn(TAG, "Launching Play Store billing flow from savedInstanceState");
            this.sendCancelledBroadcastIfFinished = bundle.getBoolean(KEY_SEND_CANCELLED_BROADCAST_IF_FINISHED, false);
            if (bundle.containsKey(KEY_PRICE_CHANGE_RESULT_RECEIVER)) {
                this.priceChangeResultReceiver = (ResultReceiver) bundle.getParcelable(KEY_PRICE_CHANGE_RESULT_RECEIVER);
            } else if (bundle.containsKey(KEY_IN_APP_MESSAGE_RESULT_RECEIVER)) {
                this.inAppMessageResultReceiver = (ResultReceiver) bundle.getParcelable(KEY_IN_APP_MESSAGE_RESULT_RECEIVER);
            }
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing() && this.sendCancelledBroadcastIfFinished) {
            Intent makePurchasesUpdatedIntent = makePurchasesUpdatedIntent();
            makePurchasesUpdatedIntent.putExtra("RESPONSE_CODE", 1);
            makePurchasesUpdatedIntent.putExtra("DEBUG_MESSAGE", "Billing dialog closed.");
            sendBroadcast(makePurchasesUpdatedIntent);
        }
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        ResultReceiver resultReceiver = this.priceChangeResultReceiver;
        if (resultReceiver != null) {
            bundle.putParcelable(KEY_PRICE_CHANGE_RESULT_RECEIVER, resultReceiver);
        }
        ResultReceiver resultReceiver2 = this.inAppMessageResultReceiver;
        if (resultReceiver2 != null) {
            bundle.putParcelable(KEY_IN_APP_MESSAGE_RESULT_RECEIVER, resultReceiver2);
        }
        bundle.putBoolean(KEY_SEND_CANCELLED_BROADCAST_IF_FINISHED, this.sendCancelledBroadcastIfFinished);
    }
}
