package com.getcapacitor.plugin.util;

import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import androidx.browser.trusted.sharing.ShareTarget;
import com.getcapacitor.Bridge;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.JSValue;
import com.getcapacitor.PluginCall;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.HttpUrl;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CapacitorHttpUrlConnection implements ICapacitorHttpUrlConnection {
    private final HttpURLConnection connection;

    public CapacitorHttpUrlConnection(HttpURLConnection httpURLConnection) {
        this.connection = httpURLConnection;
        setDefaultRequestProperties();
    }

    public HttpURLConnection getHttpConnection() {
        return this.connection;
    }

    public void disconnect() {
        this.connection.disconnect();
    }

    public void setAllowUserInteraction(boolean z) {
        this.connection.setAllowUserInteraction(z);
    }

    public void setRequestMethod(String str) throws ProtocolException {
        this.connection.setRequestMethod(str);
    }

    public void setConnectTimeout(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("timeout can not be negative");
        }
        this.connection.setConnectTimeout(i);
    }

    public void setReadTimeout(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("timeout can not be negative");
        }
        this.connection.setReadTimeout(i);
    }

    public void setDisableRedirects(boolean z) {
        this.connection.setInstanceFollowRedirects(!z);
    }

    public void setRequestHeaders(JSObject jSObject) {
        Iterator<String> keys = jSObject.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            this.connection.setRequestProperty(next, jSObject.getString(next));
        }
    }

    public void setDoOutput(boolean z) {
        this.connection.setDoOutput(z);
    }

    public void setRequestBody(PluginCall pluginCall, JSValue jSValue) throws JSONException, IOException {
        setRequestBody(pluginCall, jSValue, null);
    }

    public void setRequestBody(PluginCall pluginCall, JSValue jSValue, String str) throws JSONException, IOException {
        String str2;
        String requestProperty = this.connection.getRequestProperty("Content-Type");
        if (requestProperty == null || requestProperty.isEmpty()) {
            return;
        }
        if (requestProperty.contains("application/json")) {
            JSArray jSArray = null;
            String str3 = HttpUrl.FRAGMENT_ENCODE_SET;
            if (jSValue != null) {
                str2 = jSValue.toString();
            } else {
                jSArray = pluginCall.getArray("data", null);
                str2 = HttpUrl.FRAGMENT_ENCODE_SET;
            }
            if (jSArray != null) {
                str2 = jSArray.toString();
            } else if (jSValue == null) {
                str2 = pluginCall.getString("data");
            }
            if (str2 != null) {
                str3 = str2;
            }
            writeRequestBody(str3);
        } else if (str != null && str.equals("file")) {
            DataOutputStream dataOutputStream = new DataOutputStream(this.connection.getOutputStream());
            try {
                if (Build.VERSION.SDK_INT >= 26) {
                    dataOutputStream.write(Base64.getDecoder().decode(jSValue.toString()));
                }
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (Throwable th) {
                try {
                    dataOutputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } else if (requestProperty.contains(ShareTarget.ENCODING_TYPE_URL_ENCODED)) {
            try {
                writeObjectRequestBody(jSValue.toJSObject());
            } catch (Exception unused) {
                writeRequestBody(jSValue.toString());
            }
        } else if (str != null && str.equals("formData")) {
            writeFormDataRequestBody(requestProperty, jSValue.toJSArray());
        } else {
            writeRequestBody(jSValue.toString());
        }
    }

    private void writeRequestBody(String str) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(this.connection.getOutputStream());
        try {
            dataOutputStream.write(str.getBytes(StandardCharsets.UTF_8));
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (Throwable th) {
            try {
                dataOutputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private void writeObjectRequestBody(JSObject jSObject) throws IOException, JSONException {
        DataOutputStream dataOutputStream = new DataOutputStream(this.connection.getOutputStream());
        try {
            Iterator<String> keys = jSObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                Object obj = jSObject.get(next);
                dataOutputStream.writeBytes(next);
                dataOutputStream.writeBytes("=");
                dataOutputStream.writeBytes(URLEncoder.encode(obj.toString(), "UTF-8"));
                if (keys.hasNext()) {
                    dataOutputStream.writeBytes("&");
                }
            }
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (Throwable th) {
            try {
                dataOutputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private void writeFormDataRequestBody(String str, JSArray jSArray) throws IOException, JSONException {
        DataOutputStream dataOutputStream = new DataOutputStream(this.connection.getOutputStream());
        try {
            String str2 = str.split(";")[1].split("=")[1];
            for (Object obj : jSArray.toList()) {
                if (obj instanceof JSONObject) {
                    JSONObject jSONObject = (JSONObject) obj;
                    String string = jSONObject.getString("type");
                    String string2 = jSONObject.getString("key");
                    String string3 = jSONObject.getString("value");
                    if (string.equals("string")) {
                        dataOutputStream.writeBytes("--" + str2 + "\r\n");
                        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + string2 + "\"\r\n\r\n");
                        dataOutputStream.writeBytes(string3);
                        dataOutputStream.writeBytes("\r\n");
                    } else if (string.equals("base64File")) {
                        String string4 = jSONObject.getString("fileName");
                        String string5 = jSONObject.getString("contentType");
                        dataOutputStream.writeBytes("--" + str2 + "\r\n");
                        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + string2 + "\"; filename=\"" + string4 + "\"\r\n");
                        StringBuilder sb = new StringBuilder();
                        sb.append("Content-Type: ");
                        sb.append(string5);
                        sb.append("\r\n");
                        dataOutputStream.writeBytes(sb.toString());
                        dataOutputStream.writeBytes("Content-Transfer-Encoding: binary\r\n");
                        dataOutputStream.writeBytes("\r\n");
                        if (Build.VERSION.SDK_INT >= 26) {
                            dataOutputStream.write(Base64.getDecoder().decode(string3));
                        }
                        dataOutputStream.writeBytes("\r\n");
                    }
                }
            }
            dataOutputStream.writeBytes("--" + str2 + "--\r\n");
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (Throwable th) {
            try {
                dataOutputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public void connect() throws IOException {
        this.connection.connect();
    }

    public int getResponseCode() throws IOException {
        return this.connection.getResponseCode();
    }

    public URL getURL() {
        return this.connection.getURL();
    }

    @Override // com.getcapacitor.plugin.util.ICapacitorHttpUrlConnection
    public InputStream getErrorStream() {
        return this.connection.getErrorStream();
    }

    @Override // com.getcapacitor.plugin.util.ICapacitorHttpUrlConnection
    public String getHeaderField(String str) {
        return this.connection.getHeaderField(str);
    }

    @Override // com.getcapacitor.plugin.util.ICapacitorHttpUrlConnection
    public InputStream getInputStream() throws IOException {
        return this.connection.getInputStream();
    }

    public Map<String, List<String>> getHeaderFields() {
        return this.connection.getHeaderFields();
    }

    private void setDefaultRequestProperties() {
        String buildDefaultAcceptLanguageProperty = buildDefaultAcceptLanguageProperty();
        if (TextUtils.isEmpty(buildDefaultAcceptLanguageProperty)) {
            return;
        }
        this.connection.setRequestProperty("Accept-Language", buildDefaultAcceptLanguageProperty);
    }

    private String buildDefaultAcceptLanguageProperty() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= 24) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        if (TextUtils.isEmpty(language)) {
            return HttpUrl.FRAGMENT_ENCODE_SET;
        }
        if (!TextUtils.isEmpty(country)) {
            return String.format("%s-%s,%s;q=0.5", language, country, language);
        }
        return String.format("%s;q=0.5", language);
    }

    public void setSSLSocketFactory(Bridge bridge) {
        try {
            Class<?> cls = Class.forName("io.ionic.sslpinning.SSLPinning");
            SSLSocketFactory sSLSocketFactory = (SSLSocketFactory) cls.getDeclaredMethod("getSSLSocketFactory", Bridge.class).invoke(cls.newInstance(), bridge);
            if (sSLSocketFactory != null) {
                ((HttpsURLConnection) this.connection).setSSLSocketFactory(sSLSocketFactory);
            }
        } catch (Exception unused) {
        }
    }
}
