package com.getcapacitor.plugin;

import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.google.android.gms.common.internal.ImagesContract;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import okhttp3.HttpUrl;
@CapacitorPlugin
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CapacitorCookies extends Plugin {
    CapacitorCookieManager cookieManager;

    @Override // com.getcapacitor.Plugin
    public void load() {
        this.bridge.getWebView().addJavascriptInterface(this, "CapacitorCookiesAndroidInterface");
        CapacitorCookieManager capacitorCookieManager = new CapacitorCookieManager(null, CookiePolicy.ACCEPT_ALL, this.bridge);
        this.cookieManager = capacitorCookieManager;
        capacitorCookieManager.removeSessionCookies();
        CookieHandler.setDefault(this.cookieManager);
        super.load();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.getcapacitor.Plugin
    public void handleOnDestroy() {
        super.handleOnDestroy();
        this.cookieManager.removeSessionCookies();
    }

    @JavascriptInterface
    public boolean isEnabled() {
        return getBridge().getConfig().getPluginConfiguration("CapacitorCookies").getBoolean("enabled", false);
    }

    private boolean isAllowingInsecureCookies() {
        return getBridge().getConfig().getPluginConfiguration("CapacitorCookies").getBoolean("androidCustomSchemeAllowInsecureAccess", false);
    }

    @JavascriptInterface
    public void setCookie(String str, String str2) {
        this.cookieManager.setCookie(str, str2);
    }

    @PluginMethod
    public void getCookies(final PluginCall pluginCall) {
        HttpCookie[] cookies;
        if (isAllowingInsecureCookies()) {
            String string = pluginCall.getString(ImagesContract.URL);
            JSObject jSObject = new JSObject();
            for (HttpCookie httpCookie : this.cookieManager.getCookies(string)) {
                jSObject.put(httpCookie.getName(), httpCookie.getValue());
            }
            pluginCall.resolve(jSObject);
            return;
        }
        this.bridge.eval("document.cookie", new ValueCallback() { // from class: com.getcapacitor.plugin.CapacitorCookies$$ExternalSyntheticLambda0
            @Override // android.webkit.ValueCallback
            public final void onReceiveValue(Object obj) {
                CapacitorCookies.lambda$getCookies$0(PluginCall.this, (String) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getCookies$0(PluginCall pluginCall, String str) {
        String[] split = str.substring(1, str.length() - 1).split(";");
        JSObject jSObject = new JSObject();
        for (String str2 : split) {
            if (str2.length() > 0) {
                String[] split2 = str2.split("=", 2);
                if (split2.length == 2) {
                    String trim = split2[0].trim();
                    String trim2 = split2[1].trim();
                    try {
                        trim = URLDecoder.decode(split2[0].trim(), StandardCharsets.UTF_8.name());
                        trim2 = URLDecoder.decode(split2[1].trim(), StandardCharsets.UTF_8.name());
                    } catch (UnsupportedEncodingException unused) {
                    }
                    jSObject.put(trim, trim2);
                }
            }
        }
        pluginCall.resolve(jSObject);
    }

    @PluginMethod
    public void setCookie(PluginCall pluginCall) {
        String string = pluginCall.getString("key");
        if (string == null) {
            pluginCall.reject("Must provide key");
        }
        String string2 = pluginCall.getString("value");
        if (string2 == null) {
            pluginCall.reject("Must provide value");
        }
        this.cookieManager.setCookie(pluginCall.getString(ImagesContract.URL), string, string2, pluginCall.getString("expires", HttpUrl.FRAGMENT_ENCODE_SET), pluginCall.getString("path", "/"));
        pluginCall.resolve();
    }

    @PluginMethod
    public void deleteCookie(PluginCall pluginCall) {
        String string = pluginCall.getString("key");
        if (string == null) {
            pluginCall.reject("Must provide key");
        }
        String string2 = pluginCall.getString(ImagesContract.URL);
        CapacitorCookieManager capacitorCookieManager = this.cookieManager;
        capacitorCookieManager.setCookie(string2, string + "=; Expires=Wed, 31 Dec 2000 23:59:59 GMT");
        pluginCall.resolve();
    }

    @PluginMethod
    public void clearCookies(PluginCall pluginCall) {
        HttpCookie[] cookies;
        String string = pluginCall.getString(ImagesContract.URL);
        for (HttpCookie httpCookie : this.cookieManager.getCookies(string)) {
            this.cookieManager.setCookie(string, httpCookie.getName() + "=; Expires=Wed, 31 Dec 2000 23:59:59 GMT");
        }
        pluginCall.resolve();
    }

    @PluginMethod
    public void clearAllCookies(PluginCall pluginCall) {
        this.cookieManager.removeAllCookies();
        pluginCall.resolve();
    }
}
