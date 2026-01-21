package com.byteowls.capacitor.oauth2;

import android.os.AsyncTask;
import android.util.Log;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.TokenResponse;
import okhttp3.HttpUrl;
import org.json.JSONException;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class ResourceUrlAsyncTask extends AsyncTask<String, Void, ResourceCallResult> {
    private static final String ERR_GENERAL = "ERR_GENERAL";
    private static final String ERR_NO_ACCESS_TOKEN = "ERR_NO_ACCESS_TOKEN";
    private static final String MSG_RETURNED_TO_JS = "Returned to JS:\n";
    private final TokenResponse accessTokenResponse;
    private final AuthorizationResponse authorizationResponse;
    private final String logTag;
    private final OAuth2Options options;
    private final PluginCall pluginCall;

    public ResourceUrlAsyncTask(PluginCall pluginCall, OAuth2Options oAuth2Options, String str, AuthorizationResponse authorizationResponse, TokenResponse tokenResponse) {
        this.pluginCall = pluginCall;
        this.options = oAuth2Options;
        this.logTag = str;
        this.authorizationResponse = authorizationResponse;
        this.accessTokenResponse = tokenResponse;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0112, code lost:
        if (0 == 0) goto L33;
     */
    @Override // android.os.AsyncTask
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public ResourceCallResult doInBackground(String... strArr) {
        ResourceCallResult resourceCallResult = new ResourceCallResult();
        String resourceUrl = this.options.getResourceUrl();
        String str = strArr[0];
        if (resourceUrl != null) {
            String str2 = this.logTag;
            Log.i(str2, "Resource url: GET " + resourceUrl);
            if (str != null) {
                String str3 = this.logTag;
                Log.i(str3, "Access token:\n" + str);
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(resourceUrl).openConnection();
                    httpURLConnection.addRequestProperty("Authorization", String.format("Bearer %s", str));
                    if (this.options.getAdditionalResourceHeaders() != null) {
                        for (Map.Entry<String, String> entry : this.options.getAdditionalResourceHeaders().entrySet()) {
                            httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue());
                        }
                    }
                    InputStream inputStream = null;
                    try {
                        try {
                            if (httpURLConnection.getResponseCode() >= 200 && httpURLConnection.getResponseCode() < 300) {
                                inputStream = httpURLConnection.getInputStream();
                            } else {
                                inputStream = httpURLConnection.getErrorStream();
                                resourceCallResult.setError(true);
                            }
                            String readInputStream = readInputStream(inputStream);
                            if (!resourceCallResult.isError()) {
                                JSObject jSObject = new JSObject(readInputStream);
                                if (this.options.isLogsEnabled()) {
                                    String str4 = this.logTag;
                                    Log.i(str4, "Resource response:\n" + readInputStream);
                                }
                                OAuth2Utils.assignResponses(jSObject, str, this.authorizationResponse, this.accessTokenResponse);
                                if (this.options.isLogsEnabled()) {
                                    String str5 = this.logTag;
                                    Log.i(str5, MSG_RETURNED_TO_JS + jSObject);
                                }
                                resourceCallResult.setResponse(jSObject);
                            } else {
                                resourceCallResult.setErrorMsg(readInputStream);
                            }
                        } catch (IOException e) {
                            Log.e(this.logTag, HttpUrl.FRAGMENT_ENCODE_SET, e);
                            httpURLConnection.disconnect();
                            if (0 != 0) {
                            }
                        } catch (JSONException e2) {
                            Log.e(this.logTag, "Resource response no valid json.", e2);
                            httpURLConnection.disconnect();
                        }
                    } finally {
                        httpURLConnection.disconnect();
                        if (0 != 0) {
                            inputStream.close();
                        }
                    }
                } catch (MalformedURLException e3) {
                    String str6 = this.logTag;
                    Log.e(str6, "Invalid resource url '" + resourceUrl + "'", e3);
                } catch (IOException e4) {
                    Log.e(this.logTag, "Unexpected error", e4);
                }
            } else {
                if (this.options.isLogsEnabled()) {
                    Log.i(this.logTag, "No accessToken was provided although you configured a resourceUrl. Remove the resourceUrl from the config.");
                }
                this.pluginCall.reject(ERR_NO_ACCESS_TOKEN);
            }
        } else {
            JSObject jSObject2 = new JSObject();
            OAuth2Utils.assignResponses(jSObject2, str, this.authorizationResponse, this.accessTokenResponse);
            if (this.options.isLogsEnabled()) {
                String str7 = this.logTag;
                Log.i(str7, MSG_RETURNED_TO_JS + jSObject2);
            }
            resourceCallResult.setResponse(jSObject2);
        }
        return resourceCallResult;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(ResourceCallResult resourceCallResult) {
        if (resourceCallResult != null) {
            if (!resourceCallResult.isError()) {
                this.pluginCall.resolve(resourceCallResult.getResponse());
                return;
            }
            Log.e(this.logTag, resourceCallResult.getErrorMsg());
            this.pluginCall.reject(ERR_GENERAL, resourceCallResult.getErrorMsg());
            return;
        }
        this.pluginCall.reject(ERR_GENERAL);
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            char[] cArr = new char[1024];
            StringBuilder sb = new StringBuilder();
            while (true) {
                int read = bufferedReader.read(cArr);
                if (read != -1) {
                    sb.append(cArr, 0, read);
                } else {
                    String sb2 = sb.toString();
                    bufferedReader.close();
                    return sb2;
                }
            }
        } catch (Throwable th) {
            try {
                bufferedReader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }
}
