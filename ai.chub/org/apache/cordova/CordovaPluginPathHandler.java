package org.apache.cordova;

import androidx.webkit.WebViewAssetLoader;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CordovaPluginPathHandler {
    private final WebViewAssetLoader.PathHandler handler;

    public CordovaPluginPathHandler(WebViewAssetLoader.PathHandler handler) {
        this.handler = handler;
    }

    public WebViewAssetLoader.PathHandler getPathHandler() {
        return this.handler;
    }
}
