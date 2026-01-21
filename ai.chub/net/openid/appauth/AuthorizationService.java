package net.openid.appauth;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.trusted.sharing.ShareTarget;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.Map;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.IdToken;
import net.openid.appauth.RegistrationResponse;
import net.openid.appauth.TokenResponse;
import net.openid.appauth.browser.BrowserDescriptor;
import net.openid.appauth.browser.BrowserSelector;
import net.openid.appauth.browser.CustomTabManager;
import net.openid.appauth.connectivity.ConnectionBuilder;
import net.openid.appauth.internal.Logger;
import net.openid.appauth.internal.UriUtil;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class AuthorizationService {
    private final BrowserDescriptor mBrowser;
    private final AppAuthConfiguration mClientConfiguration;
    Context mContext;
    private final CustomTabManager mCustomTabManager;
    private boolean mDisposed;

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public interface RegistrationResponseCallback {
        void onRegistrationRequestCompleted(RegistrationResponse response, AuthorizationException ex);
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public interface TokenResponseCallback {
        void onTokenRequestCompleted(TokenResponse response, AuthorizationException ex);
    }

    public AuthorizationService(Context context) {
        this(context, AppAuthConfiguration.DEFAULT);
    }

    public AuthorizationService(Context context, AppAuthConfiguration clientConfiguration) {
        this(context, clientConfiguration, BrowserSelector.select(context, clientConfiguration.getBrowserMatcher()), new CustomTabManager(context));
    }

    AuthorizationService(Context context, AppAuthConfiguration clientConfiguration, BrowserDescriptor browser, CustomTabManager customTabManager) {
        this.mDisposed = false;
        this.mContext = (Context) Preconditions.checkNotNull(context);
        this.mClientConfiguration = clientConfiguration;
        this.mCustomTabManager = customTabManager;
        this.mBrowser = browser;
        if (browser == null || !browser.useCustomTab.booleanValue()) {
            return;
        }
        customTabManager.bind(browser.packageName);
    }

    public CustomTabManager getCustomTabManager() {
        return this.mCustomTabManager;
    }

    public BrowserDescriptor getBrowserDescriptor() {
        return this.mBrowser;
    }

    public CustomTabsIntent.Builder createCustomTabsIntentBuilder(Uri... possibleUris) {
        checkNotDisposed();
        return this.mCustomTabManager.createTabBuilder(possibleUris);
    }

    public void performAuthorizationRequest(AuthorizationRequest request, PendingIntent completedIntent) {
        performAuthorizationRequest(request, completedIntent, null, createCustomTabsIntentBuilder(new Uri[0]).build());
    }

    public void performAuthorizationRequest(AuthorizationRequest request, PendingIntent completedIntent, PendingIntent canceledIntent) {
        performAuthorizationRequest(request, completedIntent, canceledIntent, createCustomTabsIntentBuilder(new Uri[0]).build());
    }

    public void performAuthorizationRequest(AuthorizationRequest request, PendingIntent completedIntent, CustomTabsIntent customTabsIntent) {
        performAuthorizationRequest(request, completedIntent, null, customTabsIntent);
    }

    public void performAuthorizationRequest(AuthorizationRequest request, PendingIntent completedIntent, PendingIntent canceledIntent, CustomTabsIntent customTabsIntent) {
        performAuthManagementRequest(request, completedIntent, canceledIntent, customTabsIntent);
    }

    public void performEndSessionRequest(EndSessionRequest request, PendingIntent completedIntent) {
        performEndSessionRequest(request, completedIntent, null, createCustomTabsIntentBuilder(new Uri[0]).build());
    }

    public void performEndSessionRequest(EndSessionRequest request, PendingIntent completedIntent, PendingIntent canceledIntent) {
        performEndSessionRequest(request, completedIntent, canceledIntent, createCustomTabsIntentBuilder(new Uri[0]).build());
    }

    public void performEndSessionRequest(EndSessionRequest request, PendingIntent completedIntent, CustomTabsIntent customTabsIntent) {
        performEndSessionRequest(request, completedIntent, null, customTabsIntent);
    }

    public void performEndSessionRequest(EndSessionRequest request, PendingIntent completedIntent, PendingIntent canceledIntent, CustomTabsIntent customTabsIntent) {
        performAuthManagementRequest(request, completedIntent, canceledIntent, customTabsIntent);
    }

    private void performAuthManagementRequest(AuthorizationManagementRequest request, PendingIntent completedIntent, PendingIntent canceledIntent, CustomTabsIntent customTabsIntent) {
        checkNotDisposed();
        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(completedIntent);
        Preconditions.checkNotNull(customTabsIntent);
        Intent prepareAuthorizationRequestIntent = prepareAuthorizationRequestIntent(request, customTabsIntent);
        Context context = this.mContext;
        context.startActivity(AuthorizationManagementActivity.createStartIntent(context, request, prepareAuthorizationRequestIntent, completedIntent, canceledIntent));
    }

    public Intent getAuthorizationRequestIntent(AuthorizationRequest request, CustomTabsIntent customTabsIntent) {
        return AuthorizationManagementActivity.createStartForResultIntent(this.mContext, request, prepareAuthorizationRequestIntent(request, customTabsIntent));
    }

    public Intent getAuthorizationRequestIntent(AuthorizationRequest request) {
        return getAuthorizationRequestIntent(request, createCustomTabsIntentBuilder(new Uri[0]).build());
    }

    public Intent getEndSessionRequestIntent(EndSessionRequest request, CustomTabsIntent customTabsIntent) {
        return AuthorizationManagementActivity.createStartForResultIntent(this.mContext, request, prepareAuthorizationRequestIntent(request, customTabsIntent));
    }

    public Intent getEndSessionRequestIntent(EndSessionRequest request) {
        return getEndSessionRequestIntent(request, createCustomTabsIntentBuilder(new Uri[0]).build());
    }

    public void performTokenRequest(TokenRequest request, TokenResponseCallback callback) {
        performTokenRequest(request, NoClientAuthentication.INSTANCE, callback);
    }

    public void performTokenRequest(TokenRequest request, ClientAuthentication clientAuthentication, TokenResponseCallback callback) {
        checkNotDisposed();
        Logger.debug("Initiating code exchange request to %s", request.configuration.tokenEndpoint);
        new TokenRequestTask(request, clientAuthentication, this.mClientConfiguration.getConnectionBuilder(), SystemClock.INSTANCE, callback, Boolean.valueOf(this.mClientConfiguration.getSkipIssuerHttpsCheck())).execute(new Void[0]);
    }

    public void performRegistrationRequest(RegistrationRequest request, RegistrationResponseCallback callback) {
        checkNotDisposed();
        Logger.debug("Initiating dynamic client registration %s", request.configuration.registrationEndpoint.toString());
        new RegistrationRequestTask(request, this.mClientConfiguration.getConnectionBuilder(), callback).execute(new Void[0]);
    }

    public void dispose() {
        if (this.mDisposed) {
            return;
        }
        this.mCustomTabManager.dispose();
        this.mDisposed = true;
    }

    private void checkNotDisposed() {
        if (this.mDisposed) {
            throw new IllegalStateException("Service has been disposed and rendered inoperable");
        }
    }

    private Intent prepareAuthorizationRequestIntent(AuthorizationManagementRequest request, CustomTabsIntent customTabsIntent) {
        Intent intent;
        checkNotDisposed();
        if (this.mBrowser == null) {
            throw new ActivityNotFoundException();
        }
        Uri uri = request.toUri();
        if (this.mBrowser.useCustomTab.booleanValue()) {
            intent = customTabsIntent.intent;
        } else {
            intent = new Intent("android.intent.action.VIEW");
        }
        intent.setPackage(this.mBrowser.packageName);
        intent.setData(uri);
        Logger.debug("Using %s as browser for auth, custom tab = %s", intent.getPackage(), this.mBrowser.useCustomTab.toString());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class TokenRequestTask extends AsyncTask<Void, Void, JSONObject> {
        private TokenResponseCallback mCallback;
        private ClientAuthentication mClientAuthentication;
        private Clock mClock;
        private final ConnectionBuilder mConnectionBuilder;
        private AuthorizationException mException;
        private TokenRequest mRequest;
        private boolean mSkipIssuerHttpsCheck;

        TokenRequestTask(TokenRequest request, ClientAuthentication clientAuthentication, ConnectionBuilder connectionBuilder, Clock clock, TokenResponseCallback callback, Boolean skipIssuerHttpsCheck) {
            this.mRequest = request;
            this.mClientAuthentication = clientAuthentication;
            this.mConnectionBuilder = connectionBuilder;
            this.mClock = clock;
            this.mCallback = callback;
            this.mSkipIssuerHttpsCheck = skipIssuerHttpsCheck.booleanValue();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public JSONObject doInBackground(Void... voids) {
            InputStream inputStream;
            InputStream errorStream;
            InputStream inputStream2 = null;
            try {
                try {
                    HttpURLConnection openConnection = this.mConnectionBuilder.openConnection(this.mRequest.configuration.tokenEndpoint);
                    openConnection.setRequestMethod(ShareTarget.METHOD_POST);
                    openConnection.setRequestProperty("Content-Type", ShareTarget.ENCODING_TYPE_URL_ENCODED);
                    addJsonToAcceptHeader(openConnection);
                    openConnection.setDoOutput(true);
                    Map<String, String> requestHeaders = this.mClientAuthentication.getRequestHeaders(this.mRequest.clientId);
                    if (requestHeaders != null) {
                        for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                            openConnection.setRequestProperty(entry.getKey(), entry.getValue());
                        }
                    }
                    Map<String, String> requestParameters = this.mRequest.getRequestParameters();
                    Map<String, String> requestParameters2 = this.mClientAuthentication.getRequestParameters(this.mRequest.clientId);
                    if (requestParameters2 != null) {
                        requestParameters.putAll(requestParameters2);
                    }
                    String formUrlEncode = UriUtil.formUrlEncode(requestParameters);
                    openConnection.setRequestProperty("Content-Length", String.valueOf(formUrlEncode.length()));
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openConnection.getOutputStream());
                    outputStreamWriter.write(formUrlEncode);
                    outputStreamWriter.flush();
                    if (openConnection.getResponseCode() >= 200 && openConnection.getResponseCode() < 300) {
                        errorStream = openConnection.getInputStream();
                    } else {
                        errorStream = openConnection.getErrorStream();
                    }
                } catch (Throwable th) {
                    th = th;
                    inputStream2 = inputStream;
                }
            } catch (IOException e) {
                e = e;
                inputStream = null;
            } catch (JSONException e2) {
                e = e2;
                inputStream = null;
            } catch (Throwable th2) {
                th = th2;
            }
            try {
                JSONObject jSONObject = new JSONObject(Utils.readInputStream(errorStream));
                Utils.closeQuietly(errorStream);
                return jSONObject;
            } catch (IOException e3) {
                inputStream = errorStream;
                e = e3;
                Logger.debugWithStack(e, "Failed to complete exchange request", new Object[0]);
                this.mException = AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.NETWORK_ERROR, e);
                Utils.closeQuietly(inputStream);
                return null;
            } catch (JSONException e4) {
                inputStream = errorStream;
                e = e4;
                Logger.debugWithStack(e, "Failed to complete exchange request", new Object[0]);
                this.mException = AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.JSON_DESERIALIZATION_ERROR, e);
                Utils.closeQuietly(inputStream);
                return null;
            } catch (Throwable th3) {
                th = th3;
                inputStream2 = errorStream;
                Utils.closeQuietly(inputStream2);
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(JSONObject json) {
            AuthorizationException fromTemplate;
            AuthorizationException authorizationException = this.mException;
            if (authorizationException != null) {
                this.mCallback.onTokenRequestCompleted(null, authorizationException);
            } else if (json.has(AuthorizationException.PARAM_ERROR)) {
                try {
                    String string = json.getString(AuthorizationException.PARAM_ERROR);
                    fromTemplate = AuthorizationException.fromOAuthTemplate(AuthorizationException.TokenRequestErrors.byString(string), string, json.optString(AuthorizationException.PARAM_ERROR_DESCRIPTION, null), UriUtil.parseUriIfAvailable(json.optString(AuthorizationException.PARAM_ERROR_URI)));
                } catch (JSONException e) {
                    fromTemplate = AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.JSON_DESERIALIZATION_ERROR, e);
                }
                this.mCallback.onTokenRequestCompleted(null, fromTemplate);
            } else {
                try {
                    TokenResponse build = new TokenResponse.Builder(this.mRequest).fromResponseJson(json).build();
                    if (build.idToken != null) {
                        try {
                            try {
                                IdToken.from(build.idToken).validate(this.mRequest, this.mClock, this.mSkipIssuerHttpsCheck);
                            } catch (AuthorizationException e2) {
                                this.mCallback.onTokenRequestCompleted(null, e2);
                                return;
                            }
                        } catch (IdToken.IdTokenException | JSONException e3) {
                            this.mCallback.onTokenRequestCompleted(null, AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.ID_TOKEN_PARSING_ERROR, e3));
                            return;
                        }
                    }
                    Logger.debug("Token exchange with %s completed", this.mRequest.configuration.tokenEndpoint);
                    this.mCallback.onTokenRequestCompleted(build, null);
                } catch (JSONException e4) {
                    this.mCallback.onTokenRequestCompleted(null, AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.JSON_DESERIALIZATION_ERROR, e4));
                }
            }
        }

        private void addJsonToAcceptHeader(URLConnection conn) {
            if (TextUtils.isEmpty(conn.getRequestProperty("Accept"))) {
                conn.setRequestProperty("Accept", "application/json");
            }
        }
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    private static class RegistrationRequestTask extends AsyncTask<Void, Void, JSONObject> {
        private RegistrationResponseCallback mCallback;
        private final ConnectionBuilder mConnectionBuilder;
        private AuthorizationException mException;
        private RegistrationRequest mRequest;

        RegistrationRequestTask(RegistrationRequest request, ConnectionBuilder connectionBuilder, RegistrationResponseCallback callback) {
            this.mRequest = request;
            this.mConnectionBuilder = connectionBuilder;
            this.mCallback = callback;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r2v0 */
        /* JADX WARN: Type inference failed for: r2v1, types: [java.io.InputStream] */
        /* JADX WARN: Type inference failed for: r2v2 */
        @Override // android.os.AsyncTask
        public JSONObject doInBackground(Void... voids) {
            InputStream inputStream;
            String jsonString = this.mRequest.toJsonString();
            ?? r2 = 0;
            try {
                try {
                    HttpURLConnection openConnection = this.mConnectionBuilder.openConnection(this.mRequest.configuration.registrationEndpoint);
                    openConnection.setRequestMethod(ShareTarget.METHOD_POST);
                    openConnection.setRequestProperty("Content-Type", "application/json");
                    openConnection.setDoOutput(true);
                    openConnection.setRequestProperty("Content-Length", String.valueOf(jsonString.length()));
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openConnection.getOutputStream());
                    outputStreamWriter.write(jsonString);
                    outputStreamWriter.flush();
                    inputStream = openConnection.getInputStream();
                    try {
                        JSONObject jSONObject = new JSONObject(Utils.readInputStream(inputStream));
                        Utils.closeQuietly(inputStream);
                        return jSONObject;
                    } catch (IOException e) {
                        e = e;
                        Logger.debugWithStack(e, "Failed to complete registration request", new Object[0]);
                        this.mException = AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.NETWORK_ERROR, e);
                        Utils.closeQuietly(inputStream);
                        return null;
                    } catch (JSONException e2) {
                        e = e2;
                        Logger.debugWithStack(e, "Failed to complete registration request", new Object[0]);
                        this.mException = AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.JSON_DESERIALIZATION_ERROR, e);
                        Utils.closeQuietly(inputStream);
                        return null;
                    }
                } catch (Throwable th) {
                    th = th;
                    r2 = jsonString;
                    Utils.closeQuietly(r2);
                    throw th;
                }
            } catch (IOException e3) {
                e = e3;
                inputStream = null;
            } catch (JSONException e4) {
                e = e4;
                inputStream = null;
            } catch (Throwable th2) {
                th = th2;
                Utils.closeQuietly(r2);
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(JSONObject json) {
            AuthorizationException fromTemplate;
            AuthorizationException authorizationException = this.mException;
            if (authorizationException != null) {
                this.mCallback.onRegistrationRequestCompleted(null, authorizationException);
            } else if (json.has(AuthorizationException.PARAM_ERROR)) {
                try {
                    String string = json.getString(AuthorizationException.PARAM_ERROR);
                    fromTemplate = AuthorizationException.fromOAuthTemplate(AuthorizationException.RegistrationRequestErrors.byString(string), string, json.getString(AuthorizationException.PARAM_ERROR_DESCRIPTION), UriUtil.parseUriIfAvailable(json.getString(AuthorizationException.PARAM_ERROR_URI)));
                } catch (JSONException e) {
                    fromTemplate = AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.JSON_DESERIALIZATION_ERROR, e);
                }
                this.mCallback.onRegistrationRequestCompleted(null, fromTemplate);
            } else {
                try {
                    RegistrationResponse build = new RegistrationResponse.Builder(this.mRequest).fromResponseJson(json).build();
                    Logger.debug("Dynamic registration with %s completed", this.mRequest.configuration.registrationEndpoint);
                    this.mCallback.onRegistrationRequestCompleted(build, null);
                } catch (RegistrationResponse.MissingArgumentException e2) {
                    Logger.errorWithStack(e2, "Malformed registration response", new Object[0]);
                    this.mException = AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.INVALID_REGISTRATION_RESPONSE, e2);
                } catch (JSONException e3) {
                    this.mCallback.onRegistrationRequestCompleted(null, AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.JSON_DESERIALIZATION_ERROR, e3));
                }
            }
        }
    }
}
