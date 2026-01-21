package com.codetrixstudio.capacitor.GoogleAuth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.activity.result.ActivityResult;
import com.codetrixstudio.capacitor.GoogleAuth.capacitorgoogleauth.R;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import okhttp3.HttpUrl;
import org.json.JSONException;
import org.json.JSONObject;
@CapacitorPlugin
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class GoogleAuth extends Plugin {
    private static final String FIELD_ACCESS_TOKEN = "accessToken";
    private static final String FIELD_TOKEN_EXPIRES = "expires";
    private static final String FIELD_TOKEN_EXPIRES_IN = "expires_in";
    public static final int KAssumeStaleTokenSec = 60;
    private static final int SIGN_IN_CANCELLED = 12501;
    private static final String VERIFY_TOKEN_URL = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=";
    private GoogleSignInClient googleSignInClient;

    @Override // com.getcapacitor.Plugin
    public void load() {
        String string = getConfig().getString("androidClientId", getConfig().getString("clientId", getContext().getString(R.string.server_client_id)));
        boolean z = getConfig().getBoolean("forceCodeForRefreshToken", false);
        GoogleSignInOptions.Builder requestEmail = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(string).requestEmail();
        if (z) {
            requestEmail.requestServerAuthCode(string, true);
        }
        String[] array = getConfig().getArray("scopes", new String[0]);
        Scope[] scopeArr = new Scope[array.length - 1];
        Scope scope = new Scope(array[0]);
        for (int i = 1; i < array.length; i++) {
            scopeArr[i - 1] = new Scope(array[i]);
        }
        requestEmail.requestScopes(scope, scopeArr);
        this.googleSignInClient = GoogleSignIn.getClient(getContext(), requestEmail.build());
    }

    @PluginMethod
    public void signIn(PluginCall pluginCall) {
        startActivityForResult(pluginCall, this.googleSignInClient.getSignInIntent(), "signInResult");
    }

    @ActivityCallback
    protected void signInResult(final PluginCall pluginCall, ActivityResult activityResult) {
        if (pluginCall == null) {
            return;
        }
        try {
            final GoogleSignInAccount result = GoogleSignIn.getSignedInAccountFromIntent(activityResult.getData()).getResult(ApiException.class);
            Executors.newSingleThreadExecutor().execute(new Runnable() { // from class: com.codetrixstudio.capacitor.GoogleAuth.GoogleAuth$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    GoogleAuth.this.lambda$signInResult$0(result, pluginCall);
                }
            });
        } catch (ApiException e) {
            if (12501 == e.getStatusCode()) {
                pluginCall.reject("The user canceled the sign-in flow.", HttpUrl.FRAGMENT_ENCODE_SET + e.getStatusCode());
                return;
            }
            pluginCall.reject("Something went wrong", HttpUrl.FRAGMENT_ENCODE_SET + e.getStatusCode());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$signInResult$0(GoogleSignInAccount googleSignInAccount, PluginCall pluginCall) {
        try {
            JSONObject authToken = getAuthToken(googleSignInAccount.getAccount(), true);
            JSObject jSObject = new JSObject();
            jSObject.put("idToken", googleSignInAccount.getIdToken());
            jSObject.put(FIELD_ACCESS_TOKEN, authToken.get(FIELD_ACCESS_TOKEN));
            jSObject.put(FIELD_TOKEN_EXPIRES, authToken.get(FIELD_TOKEN_EXPIRES));
            jSObject.put(FIELD_TOKEN_EXPIRES_IN, authToken.get(FIELD_TOKEN_EXPIRES_IN));
            JSObject jSObject2 = new JSObject();
            jSObject2.put("serverAuthCode", googleSignInAccount.getServerAuthCode());
            jSObject2.put("idToken", googleSignInAccount.getIdToken());
            jSObject2.put("authentication", (Object) jSObject);
            jSObject2.put("name", googleSignInAccount.getDisplayName());
            jSObject2.put("displayName", googleSignInAccount.getDisplayName());
            jSObject2.put("email", googleSignInAccount.getEmail());
            jSObject2.put("familyName", googleSignInAccount.getFamilyName());
            jSObject2.put("givenName", googleSignInAccount.getGivenName());
            jSObject2.put("id", googleSignInAccount.getId());
            jSObject2.put("imageUrl", googleSignInAccount.getPhotoUrl());
            pluginCall.resolve(jSObject2);
        } catch (Exception e) {
            e.printStackTrace();
            pluginCall.reject("Something went wrong while retrieving access token", e);
        }
    }

    @PluginMethod
    public void refresh(PluginCall pluginCall) {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        if (lastSignedInAccount == null) {
            pluginCall.reject("User not logged in.");
            return;
        }
        try {
            JSONObject authToken = getAuthToken(lastSignedInAccount.getAccount(), true);
            JSObject jSObject = new JSObject();
            jSObject.put("idToken", lastSignedInAccount.getIdToken());
            jSObject.put(FIELD_ACCESS_TOKEN, authToken.get(FIELD_ACCESS_TOKEN));
            jSObject.put("refreshToken", HttpUrl.FRAGMENT_ENCODE_SET);
            pluginCall.resolve(jSObject);
        } catch (Exception e) {
            e.printStackTrace();
            pluginCall.reject("Something went wrong while retrieving access token", e);
        }
    }

    @PluginMethod
    public void signOut(final PluginCall pluginCall) {
        this.googleSignInClient.signOut().addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() { // from class: com.codetrixstudio.capacitor.GoogleAuth.GoogleAuth.2
            @Override // com.google.android.gms.tasks.OnSuccessListener
            public void onSuccess(Void r1) {
                pluginCall.resolve();
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() { // from class: com.codetrixstudio.capacitor.GoogleAuth.GoogleAuth.1
            @Override // com.google.android.gms.tasks.OnFailureListener
            public void onFailure(Exception exc) {
                pluginCall.reject("Sign out failed", exc);
            }
        });
    }

    @PluginMethod
    public void initialize(PluginCall pluginCall) {
        pluginCall.resolve();
    }

    private JSONObject getAuthToken(Account account, boolean z) throws Exception {
        AccountManager accountManager = AccountManager.get(getContext());
        String string = accountManager.getAuthToken(account, "oauth2:profile email", (Bundle) null, false, (AccountManagerCallback<Bundle>) null, (Handler) null).getResult().getString("authtoken");
        try {
            return verifyToken(string);
        } catch (IOException e) {
            if (z) {
                accountManager.invalidateAuthToken("com.google", string);
                return getAuthToken(account, false);
            }
            throw e;
        }
    }

    private JSONObject verifyToken(String str) throws IOException, JSONException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(VERIFY_TOKEN_URL + str).openConnection();
        httpURLConnection.setInstanceFollowRedirects(true);
        String fromStream = fromStream(new BufferedInputStream(httpURLConnection.getInputStream()));
        Log.d("AuthenticatedBackend", "token: " + str + ", verification: " + fromStream);
        JSONObject jSONObject = new JSONObject(fromStream);
        int i = jSONObject.getInt(FIELD_TOKEN_EXPIRES_IN);
        if (i < 60) {
            throw new IOException("Auth token soon expiring.");
        }
        jSONObject.put(FIELD_ACCESS_TOKEN, str);
        jSONObject.put(FIELD_TOKEN_EXPIRES, i + (System.currentTimeMillis() / 1000));
        return jSONObject;
    }

    private static String fromStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                sb.append(readLine);
                sb.append("\n");
            } else {
                bufferedReader.close();
                return sb.toString();
            }
        }
    }
}
