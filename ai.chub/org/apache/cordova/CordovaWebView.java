package org.apache.cordova;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebChromeClient;
import java.util.List;
import java.util.Map;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface CordovaWebView {
    public static final String CORDOVA_VERSION = "10.1.1";

    boolean backHistory();

    boolean canGoBack();

    void clearCache();

    @Deprecated
    void clearCache(boolean b);

    void clearHistory();

    Context getContext();

    ICordovaCookieManager getCookieManager();

    CordovaWebViewEngine getEngine();

    PluginManager getPluginManager();

    CordovaPreferences getPreferences();

    CordovaResourceApi getResourceApi();

    String getUrl();

    View getView();

    void handleDestroy();

    void handlePause(boolean keepRunning);

    void handleResume(boolean keepRunning);

    void handleStart();

    void handleStop();

    @Deprecated
    void hideCustomView();

    void init(CordovaInterface cordova, List<PluginEntry> pluginEntries, CordovaPreferences preferences);

    boolean isButtonPlumbedToJs(int keyCode);

    @Deprecated
    boolean isCustomViewShowing();

    boolean isInitialized();

    void loadUrl(String url);

    void loadUrlIntoView(String url, boolean recreatePlugins);

    void onNewIntent(Intent intent);

    Object postMessage(String id, Object data);

    @Deprecated
    void sendJavascript(String statememt);

    void sendPluginResult(PluginResult cr, String callbackId);

    void setButtonPlumbedToJs(int keyCode, boolean override);

    @Deprecated
    void showCustomView(View view, WebChromeClient.CustomViewCallback callback);

    void showWebPage(String url, boolean openExternal, boolean clearHistory, Map<String, Object> params);

    void stopLoading();
}
