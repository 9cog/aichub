package com.getcapacitor.plugin.util;

import android.text.TextUtils;
import android.util.Base64;
import androidx.browser.trusted.sharing.ShareTarget;
import androidx.core.app.NotificationCompat;
import com.getcapacitor.Bridge;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.JSValue;
import com.getcapacitor.PluginCall;
import com.google.android.gms.common.internal.ImagesContract;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.openid.appauth.AuthorizationException;
import okhttp3.HttpUrl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class HttpRequestHandler {

    @FunctionalInterface
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public interface ProgressEmitter {
        void emit(Integer num, Integer num2);
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public enum ResponseType {
        ARRAY_BUFFER("arraybuffer"),
        BLOB("blob"),
        DOCUMENT("document"),
        JSON("json"),
        TEXT("text");
        
        static final ResponseType DEFAULT;
        private final String name;

        static {
            ResponseType responseType;
            DEFAULT = responseType;
        }

        ResponseType(String str) {
            this.name = str;
        }

        public static ResponseType parse(String str) {
            ResponseType[] values;
            for (ResponseType responseType : values()) {
                if (responseType.name.equalsIgnoreCase(str)) {
                    return responseType;
                }
            }
            return DEFAULT;
        }
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class HttpURLConnectionBuilder {
        public Integer connectTimeout;
        public CapacitorHttpUrlConnection connection;
        public Boolean disableRedirects;
        public JSObject headers;
        public String method;
        public Integer readTimeout;
        public URL url;

        public HttpURLConnectionBuilder setConnectTimeout(Integer num) {
            this.connectTimeout = num;
            return this;
        }

        public HttpURLConnectionBuilder setReadTimeout(Integer num) {
            this.readTimeout = num;
            return this;
        }

        public HttpURLConnectionBuilder setDisableRedirects(Boolean bool) {
            this.disableRedirects = bool;
            return this;
        }

        public HttpURLConnectionBuilder setHeaders(JSObject jSObject) {
            this.headers = jSObject;
            return this;
        }

        public HttpURLConnectionBuilder setMethod(String str) {
            this.method = str;
            return this;
        }

        public HttpURLConnectionBuilder setUrl(URL url) {
            this.url = url;
            return this;
        }

        public HttpURLConnectionBuilder openConnection() throws IOException {
            CapacitorHttpUrlConnection capacitorHttpUrlConnection = new CapacitorHttpUrlConnection((HttpURLConnection) this.url.openConnection());
            this.connection = capacitorHttpUrlConnection;
            capacitorHttpUrlConnection.setAllowUserInteraction(false);
            this.connection.setRequestMethod(this.method);
            Integer num = this.connectTimeout;
            if (num != null) {
                this.connection.setConnectTimeout(num.intValue());
            }
            Integer num2 = this.readTimeout;
            if (num2 != null) {
                this.connection.setReadTimeout(num2.intValue());
            }
            Boolean bool = this.disableRedirects;
            if (bool != null) {
                this.connection.setDisableRedirects(bool.booleanValue());
            }
            this.connection.setRequestHeaders(this.headers);
            return this;
        }

        public HttpURLConnectionBuilder setUrlParams(JSObject jSObject) throws MalformedURLException, URISyntaxException, JSONException {
            return setUrlParams(jSObject, true);
        }

        public HttpURLConnectionBuilder setUrlParams(JSObject jSObject, boolean z) throws URISyntaxException, MalformedURLException {
            String query = this.url.getQuery();
            String str = HttpUrl.FRAGMENT_ENCODE_SET;
            if (query == null) {
                query = HttpUrl.FRAGMENT_ENCODE_SET;
            }
            Iterator<String> keys = jSObject.keys();
            if (keys.hasNext()) {
                StringBuilder sb = new StringBuilder(query);
                while (keys.hasNext()) {
                    String next = keys.next();
                    try {
                        StringBuilder sb2 = new StringBuilder();
                        JSONArray jSONArray = jSObject.getJSONArray(next);
                        for (int i = 0; i < jSONArray.length(); i++) {
                            addUrlParam(sb2, next, jSONArray.getString(i), z);
                            if (i != jSONArray.length() - 1) {
                                sb2.append("&");
                            }
                        }
                        if (sb.length() > 0) {
                            sb.append("&");
                        }
                        sb.append((CharSequence) sb2);
                    } catch (JSONException unused) {
                        if (sb.length() > 0) {
                            sb.append("&");
                        }
                        addUrlParam(sb, next, jSObject.getString(next), z);
                    }
                }
                String sb3 = sb.toString();
                URI uri = this.url.toURI();
                StringBuilder sb4 = new StringBuilder();
                sb4.append(uri.getScheme());
                sb4.append("://");
                sb4.append(uri.getAuthority());
                sb4.append(uri.getPath());
                sb4.append(sb3.equals(HttpUrl.FRAGMENT_ENCODE_SET) ? HttpUrl.FRAGMENT_ENCODE_SET : "?" + sb3);
                if (uri.getFragment() != null) {
                    str = uri.getFragment();
                }
                sb4.append(str);
                this.url = new URL(sb4.toString());
                return this;
            }
            return this;
        }

        private static void addUrlParam(StringBuilder sb, String str, String str2, boolean z) {
            if (z) {
                try {
                    str = URLEncoder.encode(str, "UTF-8");
                    str2 = URLEncoder.encode(str2, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e.getCause());
                }
            }
            sb.append(str);
            sb.append("=");
            sb.append(str2);
        }

        public CapacitorHttpUrlConnection build() {
            return this.connection;
        }
    }

    public static JSObject buildResponse(CapacitorHttpUrlConnection capacitorHttpUrlConnection) throws IOException, JSONException {
        return buildResponse(capacitorHttpUrlConnection, ResponseType.DEFAULT);
    }

    public static JSObject buildResponse(CapacitorHttpUrlConnection capacitorHttpUrlConnection, ResponseType responseType) throws IOException, JSONException {
        int responseCode = capacitorHttpUrlConnection.getResponseCode();
        JSObject jSObject = new JSObject();
        jSObject.put(NotificationCompat.CATEGORY_STATUS, responseCode);
        jSObject.put("headers", (Object) buildResponseHeaders(capacitorHttpUrlConnection));
        jSObject.put(ImagesContract.URL, (Object) capacitorHttpUrlConnection.getURL());
        jSObject.put("data", readData(capacitorHttpUrlConnection, responseType));
        if (capacitorHttpUrlConnection.getErrorStream() != null) {
            jSObject.put(AuthorizationException.PARAM_ERROR, true);
        }
        return jSObject;
    }

    public static Object readData(ICapacitorHttpUrlConnection iCapacitorHttpUrlConnection, ResponseType responseType) throws IOException, JSONException {
        InputStream errorStream = iCapacitorHttpUrlConnection.getErrorStream();
        String headerField = iCapacitorHttpUrlConnection.getHeaderField("Content-Type");
        if (errorStream != null) {
            if (isOneOf(headerField, MimeType.APPLICATION_JSON, MimeType.APPLICATION_VND_API_JSON)) {
                return parseJSON(readStreamAsString(errorStream));
            }
            return readStreamAsString(errorStream);
        } else if (headerField != null && headerField.contains(MimeType.APPLICATION_JSON.getValue())) {
            return parseJSON(readStreamAsString(iCapacitorHttpUrlConnection.getInputStream()));
        } else {
            InputStream inputStream = iCapacitorHttpUrlConnection.getInputStream();
            int i = AnonymousClass1.$SwitchMap$com$getcapacitor$plugin$util$HttpRequestHandler$ResponseType[responseType.ordinal()];
            if (i == 1 || i == 2) {
                return readStreamAsBase64(inputStream);
            }
            if (i == 3) {
                return parseJSON(readStreamAsString(inputStream));
            }
            return readStreamAsString(inputStream);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.getcapacitor.plugin.util.HttpRequestHandler$1  reason: invalid class name */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$getcapacitor$plugin$util$HttpRequestHandler$ResponseType;

        static {
            int[] iArr = new int[ResponseType.values().length];
            $SwitchMap$com$getcapacitor$plugin$util$HttpRequestHandler$ResponseType = iArr;
            try {
                iArr[ResponseType.ARRAY_BUFFER.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$getcapacitor$plugin$util$HttpRequestHandler$ResponseType[ResponseType.BLOB.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$getcapacitor$plugin$util$HttpRequestHandler$ResponseType[ResponseType.JSON.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$getcapacitor$plugin$util$HttpRequestHandler$ResponseType[ResponseType.DOCUMENT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$getcapacitor$plugin$util$HttpRequestHandler$ResponseType[ResponseType.TEXT.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    public static boolean isOneOf(String str, MimeType... mimeTypeArr) {
        if (str != null) {
            for (MimeType mimeType : mimeTypeArr) {
                if (str.contains(mimeType.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static JSObject buildResponseHeaders(CapacitorHttpUrlConnection capacitorHttpUrlConnection) {
        JSObject jSObject = new JSObject();
        for (Map.Entry<String, List<String>> entry : capacitorHttpUrlConnection.getHeaderFields().entrySet()) {
            jSObject.put(entry.getKey(), TextUtils.join(", ", entry.getValue()));
        }
        return jSObject;
    }

    public static Object parseJSON(String str) throws JSONException {
        new JSONObject();
        try {
            if ("null".equals(str.trim())) {
                return JSONObject.NULL;
            }
            if ("true".equals(str.trim())) {
                return true;
            }
            if ("false".equals(str.trim())) {
                return false;
            }
            if (str.trim().length() <= 0) {
                return HttpUrl.FRAGMENT_ENCODE_SET;
            }
            if (str.trim().matches("^\".*\"$")) {
                return str.trim().substring(1, str.trim().length() - 1);
            }
            if (str.trim().matches("^-?\\d+$")) {
                return Integer.valueOf(Integer.parseInt(str.trim()));
            }
            if (str.trim().matches("^-?\\d+(\\.\\d+)?$")) {
                return Double.valueOf(Double.parseDouble(str.trim()));
            }
            try {
                return new JSObject(str);
            } catch (JSONException unused) {
                return new JSArray(str);
            }
        } catch (JSONException unused2) {
            return str;
        }
    }

    public static String readStreamAsBase64(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byte[] bArr = new byte[1024];
            while (true) {
                int read = inputStream.read(bArr);
                if (read != -1) {
                    byteArrayOutputStream.write(bArr, 0, read);
                } else {
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encodeToString = Base64.encodeToString(byteArray, 0, byteArray.length, 0);
                    byteArrayOutputStream.close();
                    return encodeToString;
                }
            }
        } catch (Throwable th) {
            try {
                byteArrayOutputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static String readStreamAsString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            StringBuilder sb = new StringBuilder();
            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                sb.append(readLine);
                readLine = bufferedReader.readLine();
                if (readLine != null) {
                    sb.append(System.getProperty("line.separator"));
                }
            }
            String sb2 = sb.toString();
            bufferedReader.close();
            return sb2;
        } catch (Throwable th) {
            try {
                bufferedReader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static JSObject request(PluginCall pluginCall, String str, Bridge bridge) throws IOException, URISyntaxException, JSONException {
        String string = pluginCall.getString(ImagesContract.URL, HttpUrl.FRAGMENT_ENCODE_SET);
        JSObject object = pluginCall.getObject("headers", new JSObject());
        JSObject object2 = pluginCall.getObject("params", new JSObject());
        Integer num = pluginCall.getInt("connectTimeout");
        Integer num2 = pluginCall.getInt("readTimeout");
        Boolean bool = pluginCall.getBoolean("disableRedirects");
        Boolean bool2 = pluginCall.getBoolean("shouldEncodeUrlParams", true);
        ResponseType parse = ResponseType.parse(pluginCall.getString("responseType"));
        String string2 = pluginCall.getString("dataType");
        if (str == null) {
            str = pluginCall.getString("method", ShareTarget.METHOD_GET);
        }
        String upperCase = str.toUpperCase(Locale.ROOT);
        boolean z = upperCase.equals("DELETE") || upperCase.equals("PATCH") || upperCase.equals(ShareTarget.METHOD_POST) || upperCase.equals("PUT");
        URL url = new URL(string);
        CapacitorHttpUrlConnection build = new HttpURLConnectionBuilder().setUrl(url).setMethod(upperCase).setHeaders(object).setUrlParams(object2, bool2.booleanValue()).setConnectTimeout(num).setReadTimeout(num2).setDisableRedirects(bool).openConnection().build();
        if (bridge != null && !isDomainExcludedFromSSL(bridge, url).booleanValue()) {
            build.setSSLSocketFactory(bridge);
        }
        if (z) {
            JSValue jSValue = new JSValue(pluginCall, "data");
            if (jSValue.getValue() != null) {
                build.setDoOutput(true);
                build.setRequestBody(pluginCall, jSValue, string2);
            }
        }
        pluginCall.getData().put("activeCapacitorHttpUrlConnection", (Object) build);
        build.connect();
        JSObject buildResponse = buildResponse(build, parse);
        build.disconnect();
        pluginCall.getData().remove("activeCapacitorHttpUrlConnection");
        return buildResponse;
    }

    public static Boolean isDomainExcludedFromSSL(Bridge bridge, URL url) {
        try {
            Class<?> cls = Class.forName("io.ionic.sslpinning.SSLPinning");
            return (Boolean) cls.getDeclaredMethod("isDomainExcluded", Bridge.class, URL.class).invoke(cls.newInstance(), bridge, url);
        } catch (Exception unused) {
            return false;
        }
    }
}
