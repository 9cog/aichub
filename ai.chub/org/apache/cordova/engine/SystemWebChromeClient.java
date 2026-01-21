package org.apache.cordova.engine;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import java.util.Arrays;
import org.apache.cordova.CordovaDialogsHelper;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class SystemWebChromeClient extends WebChromeClient {
    private static final int FILECHOOSER_RESULTCODE = 5173;
    private static final String LOG_TAG = "SystemWebChromeClient";
    private long MAX_QUOTA = 104857600;
    private Context appContext;
    private CordovaDialogsHelper dialogsHelper;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private View mVideoProgressView;
    protected final SystemWebViewEngine parentEngine;

    public SystemWebChromeClient(SystemWebViewEngine parentEngine) {
        this.parentEngine = parentEngine;
        Context context = parentEngine.webView.getContext();
        this.appContext = context;
        this.dialogsHelper = new CordovaDialogsHelper(context);
    }

    @Override // android.webkit.WebChromeClient
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        this.dialogsHelper.showAlert(message, new CordovaDialogsHelper.Result() { // from class: org.apache.cordova.engine.SystemWebChromeClient.1
            @Override // org.apache.cordova.CordovaDialogsHelper.Result
            public void gotResult(boolean success, String value) {
                if (success) {
                    result.confirm();
                } else {
                    result.cancel();
                }
            }
        });
        return true;
    }

    @Override // android.webkit.WebChromeClient
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        this.dialogsHelper.showConfirm(message, new CordovaDialogsHelper.Result() { // from class: org.apache.cordova.engine.SystemWebChromeClient.2
            @Override // org.apache.cordova.CordovaDialogsHelper.Result
            public void gotResult(boolean success, String value) {
                if (success) {
                    result.confirm();
                } else {
                    result.cancel();
                }
            }
        });
        return true;
    }

    @Override // android.webkit.WebChromeClient
    public boolean onJsPrompt(WebView view, String origin, String message, String defaultValue, final JsPromptResult result) {
        String promptOnJsPrompt = this.parentEngine.bridge.promptOnJsPrompt(origin, message, defaultValue);
        if (promptOnJsPrompt != null) {
            result.confirm(promptOnJsPrompt);
            return true;
        }
        this.dialogsHelper.showPrompt(message, defaultValue, new CordovaDialogsHelper.Result() { // from class: org.apache.cordova.engine.SystemWebChromeClient.3
            @Override // org.apache.cordova.CordovaDialogsHelper.Result
            public void gotResult(boolean success, String value) {
                if (success) {
                    result.confirm(value);
                } else {
                    result.cancel();
                }
            }
        });
        return true;
    }

    @Override // android.webkit.WebChromeClient
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
        LOG.d(LOG_TAG, "onExceededDatabaseQuota estimatedSize: %d  currentQuota: %d  totalUsedQuota: %d", Long.valueOf(estimatedSize), Long.valueOf(currentQuota), Long.valueOf(totalUsedQuota));
        quotaUpdater.updateQuota(this.MAX_QUOTA);
    }

    @Override // android.webkit.WebChromeClient
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback);
        callback.invoke(origin, true, false);
        CordovaPlugin plugin = this.parentEngine.pluginManager.getPlugin("Geolocation");
        if (plugin == null || plugin.hasPermisssion()) {
            return;
        }
        plugin.requestPermissions(0);
    }

    @Override // android.webkit.WebChromeClient
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        this.parentEngine.getCordovaWebView().showCustomView(view, callback);
    }

    @Override // android.webkit.WebChromeClient
    public void onHideCustomView() {
        this.parentEngine.getCordovaWebView().hideCustomView();
    }

    @Override // android.webkit.WebChromeClient
    public View getVideoLoadingProgressView() {
        if (this.mVideoProgressView == null) {
            LinearLayout linearLayout = new LinearLayout(this.parentEngine.getView().getContext());
            linearLayout.setOrientation(1);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
            layoutParams.addRule(13);
            linearLayout.setLayoutParams(layoutParams);
            ProgressBar progressBar = new ProgressBar(this.parentEngine.getView().getContext());
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
            layoutParams2.gravity = 17;
            progressBar.setLayoutParams(layoutParams2);
            linearLayout.addView(progressBar);
            this.mVideoProgressView = linearLayout;
        }
        return this.mVideoProgressView;
    }

    @Override // android.webkit.WebChromeClient
    public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathsCallback, final WebChromeClient.FileChooserParams fileChooserParams) {
        boolean z = false;
        if (fileChooserParams.getMode() == 1) {
            z = true;
        }
        Intent createIntent = fileChooserParams.createIntent();
        createIntent.putExtra("android.intent.extra.ALLOW_MULTIPLE", z);
        String[] acceptTypes = fileChooserParams.getAcceptTypes();
        if (acceptTypes.length > 1) {
            createIntent.setType("*/*");
            createIntent.putExtra("android.intent.extra.MIME_TYPES", acceptTypes);
        }
        try {
            this.parentEngine.cordova.startActivityForResult(new CordovaPlugin() { // from class: org.apache.cordova.engine.SystemWebChromeClient.4
                @Override // org.apache.cordova.CordovaPlugin
                public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                    Uri[] uriArr;
                    if (resultCode == -1 && intent != null) {
                        if (intent.getClipData() != null) {
                            int itemCount = intent.getClipData().getItemCount();
                            uriArr = new Uri[itemCount];
                            for (int i = 0; i < itemCount; i++) {
                                uriArr[i] = intent.getClipData().getItemAt(i).getUri();
                                LOG.d(SystemWebChromeClient.LOG_TAG, "Receive file chooser URL: " + uriArr[i]);
                            }
                        } else if (intent.getData() != null) {
                            uriArr = WebChromeClient.FileChooserParams.parseResult(resultCode, intent);
                            LOG.d(SystemWebChromeClient.LOG_TAG, "Receive file chooser URL: " + uriArr);
                        }
                        filePathsCallback.onReceiveValue(uriArr);
                    }
                    uriArr = null;
                    filePathsCallback.onReceiveValue(uriArr);
                }
            }, createIntent, FILECHOOSER_RESULTCODE);
        } catch (ActivityNotFoundException e) {
            LOG.w("No activity found to handle file chooser intent.", e);
            filePathsCallback.onReceiveValue(null);
        }
        return true;
    }

    @Override // android.webkit.WebChromeClient
    public void onPermissionRequest(final PermissionRequest request) {
        LOG.d(LOG_TAG, "onPermissionRequest: " + Arrays.toString(request.getResources()));
        request.grant(request.getResources());
    }

    public void destroyLastDialog() {
        this.dialogsHelper.destroyLastDialog();
    }
}
