package org.apache.cordova.engine;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class SystemWebView extends WebView implements CordovaWebViewEngine.EngineView {
    SystemWebChromeClient chromeClient;
    private CordovaInterface cordova;
    private SystemWebViewEngine parentEngine;
    private SystemWebViewClient viewClient;

    public SystemWebView(Context context) {
        this(context, null);
    }

    public SystemWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init(SystemWebViewEngine parentEngine, CordovaInterface cordova) {
        this.cordova = cordova;
        this.parentEngine = parentEngine;
        if (this.viewClient == null) {
            setWebViewClient(new SystemWebViewClient(parentEngine));
        }
        if (this.chromeClient == null) {
            setWebChromeClient(new SystemWebChromeClient(parentEngine));
        }
    }

    @Override // org.apache.cordova.CordovaWebViewEngine.EngineView
    public CordovaWebView getCordovaWebView() {
        SystemWebViewEngine systemWebViewEngine = this.parentEngine;
        if (systemWebViewEngine != null) {
            return systemWebViewEngine.getCordovaWebView();
        }
        return null;
    }

    @Override // android.webkit.WebView
    public void setWebViewClient(WebViewClient client) {
        this.viewClient = (SystemWebViewClient) client;
        super.setWebViewClient(client);
    }

    @Override // android.webkit.WebView
    public void setWebChromeClient(WebChromeClient client) {
        this.chromeClient = (SystemWebChromeClient) client;
        super.setWebChromeClient(client);
    }

    @Override // android.webkit.WebView, android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        Boolean onDispatchKeyEvent = this.parentEngine.client.onDispatchKeyEvent(event);
        if (onDispatchKeyEvent != null) {
            return onDispatchKeyEvent.booleanValue();
        }
        return super.dispatchKeyEvent(event);
    }
}
