package com.getcapacitor;

import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebView;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public abstract class WebViewListener {
    public void onPageLoaded(WebView webView) {
    }

    public void onPageStarted(WebView webView) {
    }

    public void onReceivedError(WebView webView) {
    }

    public void onReceivedHttpError(WebView webView) {
    }

    public boolean onRenderProcessGone(WebView webView, RenderProcessGoneDetail renderProcessGoneDetail) {
        return false;
    }
}
