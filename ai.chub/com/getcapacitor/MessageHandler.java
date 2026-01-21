package com.getcapacitor;

import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import androidx.core.app.NotificationCompat;
import androidx.webkit.JavaScriptReplyProxy;
import androidx.webkit.WebMessageCompat;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;
import net.openid.appauth.AuthorizationException;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class MessageHandler {
    private Bridge bridge;
    private org.apache.cordova.PluginManager cordovaPluginManager;
    private JavaScriptReplyProxy javaScriptReplyProxy;
    private WebView webView;

    public MessageHandler(Bridge bridge, WebView webView, org.apache.cordova.PluginManager pluginManager) {
        this.bridge = bridge;
        this.webView = webView;
        this.cordovaPluginManager = pluginManager;
        if (WebViewFeature.isFeatureSupported("WEB_MESSAGE_LISTENER") && !bridge.getConfig().isUsingLegacyBridge()) {
            try {
                WebViewCompat.addWebMessageListener(webView, "androidBridge", bridge.getAllowedOriginRules(), new WebViewCompat.WebMessageListener() { // from class: com.getcapacitor.MessageHandler$$ExternalSyntheticLambda0
                    @Override // androidx.webkit.WebViewCompat.WebMessageListener
                    public final void onPostMessage(WebView webView2, WebMessageCompat webMessageCompat, Uri uri, boolean z, JavaScriptReplyProxy javaScriptReplyProxy) {
                        MessageHandler.this.lambda$new$0(webView2, webMessageCompat, uri, z, javaScriptReplyProxy);
                    }
                });
                return;
            } catch (Exception unused) {
                webView.addJavascriptInterface(this, "androidBridge");
                return;
            }
        }
        webView.addJavascriptInterface(this, "androidBridge");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(WebView webView, WebMessageCompat webMessageCompat, Uri uri, boolean z, JavaScriptReplyProxy javaScriptReplyProxy) {
        if (z) {
            postMessage(webMessageCompat.getData());
            this.javaScriptReplyProxy = javaScriptReplyProxy;
            return;
        }
        Logger.warn("Plugin execution is allowed in Main Frame only");
    }

    @JavascriptInterface
    public void postMessage(String str) {
        try {
            JSObject jSObject = new JSObject(str);
            String string = jSObject.getString("type");
            boolean z = true;
            boolean z2 = string != null;
            boolean z3 = z2 && string.equals("cordova");
            if (!z2 || !string.equals("js.error")) {
                z = false;
            }
            String string2 = jSObject.getString("callbackId");
            if (z3) {
                String string3 = jSObject.getString(NotificationCompat.CATEGORY_SERVICE);
                String string4 = jSObject.getString("action");
                String string5 = jSObject.getString("actionArgs");
                String tags = Logger.tags("Plugin");
                Logger.verbose(tags, "To native (Cordova plugin): callbackId: " + string2 + ", service: " + string3 + ", action: " + string4 + ", actionArgs: " + string5);
                callCordovaPluginMethod(string2, string3, string4, string5);
            } else if (z) {
                Logger.error("JavaScript Error: " + str);
            } else {
                String string6 = jSObject.getString("pluginId");
                String string7 = jSObject.getString("methodName");
                JSObject jSObject2 = jSObject.getJSObject("options", new JSObject());
                String tags2 = Logger.tags("Plugin");
                Logger.verbose(tags2, "To native (Capacitor plugin): callbackId: " + string2 + ", pluginId: " + string6 + ", methodName: " + string7);
                callPluginMethod(string2, string6, string7, jSObject2);
            }
        } catch (Exception e) {
            Logger.error("Post message error:", e);
        }
    }

    public void sendResponseMessage(PluginCall pluginCall, PluginResult pluginResult, PluginResult pluginResult2) {
        JavaScriptReplyProxy javaScriptReplyProxy;
        try {
            PluginResult pluginResult3 = new PluginResult();
            pluginResult3.put("save", pluginCall.isKeptAlive());
            pluginResult3.put("callbackId", pluginCall.getCallbackId());
            pluginResult3.put("pluginId", pluginCall.getPluginId());
            pluginResult3.put("methodName", pluginCall.getMethodName());
            if (pluginResult2 != null) {
                pluginResult3.put("success", false);
                pluginResult3.put(AuthorizationException.PARAM_ERROR, pluginResult2);
                Logger.debug("Sending plugin error: " + pluginResult3.toString());
            } else {
                pluginResult3.put("success", true);
                if (pluginResult != null) {
                    pluginResult3.put("data", pluginResult);
                }
            }
            if (!pluginCall.getCallbackId().equals(PluginCall.CALLBACK_ID_DANGLING)) {
                if (this.bridge.getConfig().isUsingLegacyBridge()) {
                    legacySendResponseMessage(pluginResult3);
                } else if (WebViewFeature.isFeatureSupported("WEB_MESSAGE_LISTENER") && (javaScriptReplyProxy = this.javaScriptReplyProxy) != null) {
                    javaScriptReplyProxy.postMessage(pluginResult3.toString());
                } else {
                    legacySendResponseMessage(pluginResult3);
                }
            } else {
                this.bridge.getApp().fireRestoredResult(pluginResult3);
            }
        } catch (Exception e) {
            Logger.error("sendResponseMessage: error: " + e);
        }
        if (pluginCall.isKeptAlive()) {
            return;
        }
        pluginCall.release(this.bridge);
    }

    private void legacySendResponseMessage(PluginResult pluginResult) {
        final String str = "window.Capacitor.fromNative(" + pluginResult.toString() + ")";
        final WebView webView = this.webView;
        webView.post(new Runnable() { // from class: com.getcapacitor.MessageHandler$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                webView.evaluateJavascript(str, null);
            }
        });
    }

    private void callPluginMethod(String str, String str2, String str3, JSObject jSObject) {
        this.bridge.callPluginMethod(str2, str3, new PluginCall(this, str2, str, str3, jSObject));
    }

    private void callCordovaPluginMethod(final String str, final String str2, final String str3, final String str4) {
        this.bridge.execute(new Runnable() { // from class: com.getcapacitor.MessageHandler$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MessageHandler.this.lambda$callCordovaPluginMethod$2(str2, str3, str, str4);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$callCordovaPluginMethod$2(String str, String str2, String str3, String str4) {
        this.cordovaPluginManager.exec(str, str2, str3, str4);
    }
}
