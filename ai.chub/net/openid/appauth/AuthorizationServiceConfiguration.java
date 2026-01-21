package net.openid.appauth;

import android.net.Uri;
import android.os.AsyncTask;
import androidx.browser.trusted.sharing.ShareTarget;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationServiceDiscovery;
import net.openid.appauth.connectivity.ConnectionBuilder;
import net.openid.appauth.connectivity.DefaultConnectionBuilder;
import net.openid.appauth.internal.Logger;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class AuthorizationServiceConfiguration {
    private static final String KEY_AUTHORIZATION_ENDPOINT = "authorizationEndpoint";
    private static final String KEY_DISCOVERY_DOC = "discoveryDoc";
    private static final String KEY_END_SESSION_ENPOINT = "endSessionEndpoint";
    private static final String KEY_REGISTRATION_ENDPOINT = "registrationEndpoint";
    private static final String KEY_TOKEN_ENDPOINT = "tokenEndpoint";
    public static final String OPENID_CONFIGURATION_RESOURCE = "openid-configuration";
    public static final String WELL_KNOWN_PATH = ".well-known";
    public final Uri authorizationEndpoint;
    public final AuthorizationServiceDiscovery discoveryDoc;
    public final Uri endSessionEndpoint;
    public final Uri registrationEndpoint;
    public final Uri tokenEndpoint;

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public interface RetrieveConfigurationCallback {
        void onFetchConfigurationCompleted(AuthorizationServiceConfiguration serviceConfiguration, AuthorizationException ex);
    }

    public AuthorizationServiceConfiguration(Uri authorizationEndpoint, Uri tokenEndpoint) {
        this(authorizationEndpoint, tokenEndpoint, null);
    }

    public AuthorizationServiceConfiguration(Uri authorizationEndpoint, Uri tokenEndpoint, Uri registrationEndpoint) {
        this(authorizationEndpoint, tokenEndpoint, registrationEndpoint, null);
    }

    public AuthorizationServiceConfiguration(Uri authorizationEndpoint, Uri tokenEndpoint, Uri registrationEndpoint, Uri endSessionEndpoint) {
        this.authorizationEndpoint = (Uri) Preconditions.checkNotNull(authorizationEndpoint);
        this.tokenEndpoint = (Uri) Preconditions.checkNotNull(tokenEndpoint);
        this.registrationEndpoint = registrationEndpoint;
        this.endSessionEndpoint = endSessionEndpoint;
        this.discoveryDoc = null;
    }

    public AuthorizationServiceConfiguration(AuthorizationServiceDiscovery discoveryDoc) {
        Preconditions.checkNotNull(discoveryDoc, "docJson cannot be null");
        this.discoveryDoc = discoveryDoc;
        this.authorizationEndpoint = discoveryDoc.getAuthorizationEndpoint();
        this.tokenEndpoint = discoveryDoc.getTokenEndpoint();
        this.registrationEndpoint = discoveryDoc.getRegistrationEndpoint();
        this.endSessionEndpoint = discoveryDoc.getEndSessionEndpoint();
    }

    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        JsonUtil.put(jSONObject, KEY_AUTHORIZATION_ENDPOINT, this.authorizationEndpoint.toString());
        JsonUtil.put(jSONObject, KEY_TOKEN_ENDPOINT, this.tokenEndpoint.toString());
        Uri uri = this.registrationEndpoint;
        if (uri != null) {
            JsonUtil.put(jSONObject, KEY_REGISTRATION_ENDPOINT, uri.toString());
        }
        Uri uri2 = this.endSessionEndpoint;
        if (uri2 != null) {
            JsonUtil.put(jSONObject, KEY_END_SESSION_ENPOINT, uri2.toString());
        }
        AuthorizationServiceDiscovery authorizationServiceDiscovery = this.discoveryDoc;
        if (authorizationServiceDiscovery != null) {
            JsonUtil.put(jSONObject, KEY_DISCOVERY_DOC, authorizationServiceDiscovery.docJson);
        }
        return jSONObject;
    }

    public String toJsonString() {
        return toJson().toString();
    }

    public static AuthorizationServiceConfiguration fromJson(JSONObject json) throws JSONException {
        Preconditions.checkNotNull(json, "json object cannot be null");
        if (json.has(KEY_DISCOVERY_DOC)) {
            try {
                return new AuthorizationServiceConfiguration(new AuthorizationServiceDiscovery(json.optJSONObject(KEY_DISCOVERY_DOC)));
            } catch (AuthorizationServiceDiscovery.MissingArgumentException e) {
                throw new JSONException("Missing required field in discovery doc: " + e.getMissingField());
            }
        }
        Preconditions.checkArgument(json.has(KEY_AUTHORIZATION_ENDPOINT), "missing authorizationEndpoint");
        Preconditions.checkArgument(json.has(KEY_TOKEN_ENDPOINT), "missing tokenEndpoint");
        return new AuthorizationServiceConfiguration(JsonUtil.getUri(json, KEY_AUTHORIZATION_ENDPOINT), JsonUtil.getUri(json, KEY_TOKEN_ENDPOINT), JsonUtil.getUriIfDefined(json, KEY_REGISTRATION_ENDPOINT), JsonUtil.getUriIfDefined(json, KEY_END_SESSION_ENPOINT));
    }

    public static AuthorizationServiceConfiguration fromJson(String jsonStr) throws JSONException {
        Preconditions.checkNotNull(jsonStr, "json cannot be null");
        return fromJson(new JSONObject(jsonStr));
    }

    public static void fetchFromIssuer(Uri openIdConnectIssuerUri, RetrieveConfigurationCallback callback) {
        fetchFromUrl(buildConfigurationUriFromIssuer(openIdConnectIssuerUri), callback);
    }

    public static void fetchFromIssuer(Uri openIdConnectIssuerUri, RetrieveConfigurationCallback callback, ConnectionBuilder connectionBuilder) {
        fetchFromUrl(buildConfigurationUriFromIssuer(openIdConnectIssuerUri), callback, connectionBuilder);
    }

    static Uri buildConfigurationUriFromIssuer(Uri openIdConnectIssuerUri) {
        return openIdConnectIssuerUri.buildUpon().appendPath(WELL_KNOWN_PATH).appendPath(OPENID_CONFIGURATION_RESOURCE).build();
    }

    public static void fetchFromUrl(Uri openIdConnectDiscoveryUri, RetrieveConfigurationCallback callback) {
        fetchFromUrl(openIdConnectDiscoveryUri, callback, DefaultConnectionBuilder.INSTANCE);
    }

    public static void fetchFromUrl(Uri openIdConnectDiscoveryUri, RetrieveConfigurationCallback callback, ConnectionBuilder connectionBuilder) {
        Preconditions.checkNotNull(openIdConnectDiscoveryUri, "openIDConnectDiscoveryUri cannot be null");
        Preconditions.checkNotNull(callback, "callback cannot be null");
        Preconditions.checkNotNull(connectionBuilder, "connectionBuilder must not be null");
        new ConfigurationRetrievalAsyncTask(openIdConnectDiscoveryUri, connectionBuilder, callback).execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class ConfigurationRetrievalAsyncTask extends AsyncTask<Void, Void, AuthorizationServiceConfiguration> {
        private RetrieveConfigurationCallback mCallback;
        private ConnectionBuilder mConnectionBuilder;
        private AuthorizationException mException = null;
        private Uri mUri;

        ConfigurationRetrievalAsyncTask(Uri uri, ConnectionBuilder connectionBuilder, RetrieveConfigurationCallback callback) {
            this.mUri = uri;
            this.mConnectionBuilder = connectionBuilder;
            this.mCallback = callback;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Not initialized variable reg: 1, insn: 0x0073: MOVE  (r0 I:??[OBJECT, ARRAY]) = (r1 I:??[OBJECT, ARRAY]), block:B:27:0x0073 */
        @Override // android.os.AsyncTask
        public AuthorizationServiceConfiguration doInBackground(Void... voids) {
            InputStream inputStream;
            InputStream inputStream2;
            InputStream inputStream3 = null;
            try {
                try {
                    HttpURLConnection openConnection = this.mConnectionBuilder.openConnection(this.mUri);
                    openConnection.setRequestMethod(ShareTarget.METHOD_GET);
                    openConnection.setDoInput(true);
                    openConnection.connect();
                    inputStream = openConnection.getInputStream();
                    try {
                        AuthorizationServiceConfiguration authorizationServiceConfiguration = new AuthorizationServiceConfiguration(new AuthorizationServiceDiscovery(new JSONObject(Utils.readInputStream(inputStream))));
                        Utils.closeQuietly(inputStream);
                        return authorizationServiceConfiguration;
                    } catch (IOException e) {
                        e = e;
                        Logger.errorWithStack(e, "Network error when retrieving discovery document", new Object[0]);
                        this.mException = AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.NETWORK_ERROR, e);
                        Utils.closeQuietly(inputStream);
                        return null;
                    } catch (AuthorizationServiceDiscovery.MissingArgumentException e2) {
                        e = e2;
                        Logger.errorWithStack(e, "Malformed discovery document", new Object[0]);
                        this.mException = AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.INVALID_DISCOVERY_DOCUMENT, e);
                        Utils.closeQuietly(inputStream);
                        return null;
                    } catch (JSONException e3) {
                        e = e3;
                        Logger.errorWithStack(e, "Error parsing discovery document", new Object[0]);
                        this.mException = AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.JSON_DESERIALIZATION_ERROR, e);
                        Utils.closeQuietly(inputStream);
                        return null;
                    }
                } catch (Throwable th) {
                    th = th;
                    inputStream3 = inputStream2;
                    Utils.closeQuietly(inputStream3);
                    throw th;
                }
            } catch (IOException e4) {
                e = e4;
                inputStream = null;
            } catch (AuthorizationServiceDiscovery.MissingArgumentException e5) {
                e = e5;
                inputStream = null;
            } catch (JSONException e6) {
                e = e6;
                inputStream = null;
            } catch (Throwable th2) {
                th = th2;
                Utils.closeQuietly(inputStream3);
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(AuthorizationServiceConfiguration configuration) {
            AuthorizationException authorizationException = this.mException;
            if (authorizationException != null) {
                this.mCallback.onFetchConfigurationCompleted(null, authorizationException);
            } else {
                this.mCallback.onFetchConfigurationCompleted(configuration, null);
            }
        }
    }
}
