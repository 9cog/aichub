package net.openid.appauth;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.internal.Logger;
import org.json.JSONException;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class AuthorizationManagementActivity extends AppCompatActivity {
    static final String KEY_AUTHORIZATION_STARTED = "authStarted";
    static final String KEY_AUTH_INTENT = "authIntent";
    static final String KEY_AUTH_REQUEST = "authRequest";
    static final String KEY_AUTH_REQUEST_TYPE = "authRequestType";
    static final String KEY_CANCEL_INTENT = "cancelIntent";
    static final String KEY_COMPLETE_INTENT = "completeIntent";
    private Intent mAuthIntent;
    private AuthorizationManagementRequest mAuthRequest;
    private boolean mAuthorizationStarted = false;
    private PendingIntent mCancelIntent;
    private PendingIntent mCompleteIntent;

    public static Intent createStartIntent(Context context, AuthorizationManagementRequest request, Intent authIntent, PendingIntent completeIntent, PendingIntent cancelIntent) {
        Intent createBaseIntent = createBaseIntent(context);
        createBaseIntent.putExtra(KEY_AUTH_INTENT, authIntent);
        createBaseIntent.putExtra(KEY_AUTH_REQUEST, request.jsonSerializeString());
        createBaseIntent.putExtra(KEY_AUTH_REQUEST_TYPE, AuthorizationManagementUtil.requestTypeFor(request));
        createBaseIntent.putExtra(KEY_COMPLETE_INTENT, completeIntent);
        createBaseIntent.putExtra(KEY_CANCEL_INTENT, cancelIntent);
        return createBaseIntent;
    }

    public static Intent createStartForResultIntent(Context context, AuthorizationManagementRequest request, Intent authIntent) {
        return createStartIntent(context, request, authIntent, null, null);
    }

    public static Intent createResponseHandlingIntent(Context context, Uri responseUri) {
        Intent createBaseIntent = createBaseIntent(context);
        createBaseIntent.setData(responseUri);
        createBaseIntent.addFlags(603979776);
        return createBaseIntent;
    }

    private static Intent createBaseIntent(Context context) {
        return new Intent(context, AuthorizationManagementActivity.class);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            extractState(getIntent().getExtras());
        } else {
            extractState(savedInstanceState);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (!this.mAuthorizationStarted) {
            try {
                startActivity(this.mAuthIntent);
                this.mAuthorizationStarted = true;
                return;
            } catch (ActivityNotFoundException unused) {
                handleBrowserNotFound();
                finish();
                return;
            }
        }
        if (getIntent().getData() != null) {
            handleAuthorizationComplete();
        } else {
            handleAuthorizationCanceled();
        }
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_AUTHORIZATION_STARTED, this.mAuthorizationStarted);
        outState.putParcelable(KEY_AUTH_INTENT, this.mAuthIntent);
        outState.putString(KEY_AUTH_REQUEST, this.mAuthRequest.jsonSerializeString());
        outState.putString(KEY_AUTH_REQUEST_TYPE, AuthorizationManagementUtil.requestTypeFor(this.mAuthRequest));
        outState.putParcelable(KEY_COMPLETE_INTENT, this.mCompleteIntent);
        outState.putParcelable(KEY_CANCEL_INTENT, this.mCancelIntent);
    }

    private void handleAuthorizationComplete() {
        Uri data = getIntent().getData();
        Intent extractResponseData = extractResponseData(data);
        if (extractResponseData == null) {
            Logger.error("Failed to extract OAuth2 response from redirect", new Object[0]);
            return;
        }
        extractResponseData.setData(data);
        sendResult(this.mCompleteIntent, extractResponseData, -1);
    }

    private void handleAuthorizationCanceled() {
        Logger.debug("Authorization flow canceled by user", new Object[0]);
        sendResult(this.mCancelIntent, AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW, null).toIntent(), 0);
    }

    private void handleBrowserNotFound() {
        Logger.debug("Authorization flow canceled due to missing browser", new Object[0]);
        sendResult(this.mCancelIntent, AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.PROGRAM_CANCELED_AUTH_FLOW, null).toIntent(), 0);
    }

    private void extractState(Bundle state) {
        if (state == null) {
            Logger.warn("No stored state - unable to handle response", new Object[0]);
            finish();
            return;
        }
        this.mAuthIntent = (Intent) state.getParcelable(KEY_AUTH_INTENT);
        this.mAuthorizationStarted = state.getBoolean(KEY_AUTHORIZATION_STARTED, false);
        this.mCompleteIntent = (PendingIntent) state.getParcelable(KEY_COMPLETE_INTENT);
        this.mCancelIntent = (PendingIntent) state.getParcelable(KEY_CANCEL_INTENT);
        try {
            String string = state.getString(KEY_AUTH_REQUEST, null);
            this.mAuthRequest = string != null ? AuthorizationManagementUtil.requestFrom(string, state.getString(KEY_AUTH_REQUEST_TYPE, null)) : null;
        } catch (JSONException unused) {
            sendResult(this.mCancelIntent, AuthorizationException.AuthorizationRequestErrors.INVALID_REQUEST.toIntent(), 0);
        }
    }

    private void sendResult(PendingIntent callback, Intent cancelData, int resultCode) {
        if (callback != null) {
            try {
                callback.send(this, 0, cancelData);
                return;
            } catch (PendingIntent.CanceledException e) {
                Logger.error("Failed to send cancel intent", e);
                return;
            }
        }
        setResult(resultCode, cancelData);
    }

    private Intent extractResponseData(Uri responseUri) {
        if (responseUri.getQueryParameterNames().contains(AuthorizationException.PARAM_ERROR)) {
            return AuthorizationException.fromOAuthRedirect(responseUri).toIntent();
        }
        AuthorizationManagementResponse responseWith = AuthorizationManagementUtil.responseWith(this.mAuthRequest, responseUri);
        if ((this.mAuthRequest.getState() == null && responseWith.getState() != null) || (this.mAuthRequest.getState() != null && !this.mAuthRequest.getState().equals(responseWith.getState()))) {
            Logger.warn("State returned in authorization response (%s) does not match state from request (%s) - discarding response", responseWith.getState(), this.mAuthRequest.getState());
            return AuthorizationException.AuthorizationRequestErrors.STATE_MISMATCH.toIntent();
        }
        return responseWith.toIntent();
    }
}
