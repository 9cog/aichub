package org.apache.cordova.engine;

import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.internal.AssetHelper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import okhttp3.HttpUrl;
import org.apache.cordova.AuthenticationToken;
import org.apache.cordova.CordovaClientCertRequest;
import org.apache.cordova.CordovaHttpAuthHandler;
import org.apache.cordova.CordovaPluginPathHandler;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginManager;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class SystemWebViewClient extends WebViewClient {
    private static final String TAG = "SystemWebViewClient";
    private final WebViewAssetLoader assetLoader;
    boolean isCurrentlyLoading;
    protected final SystemWebViewEngine parentEngine;
    private boolean doClearHistory = false;
    private Hashtable<String, AuthenticationToken> authenticationTokens = new Hashtable<>();

    public SystemWebViewClient(final SystemWebViewEngine parentEngine) {
        this.parentEngine = parentEngine;
        WebViewAssetLoader.Builder httpAllowed = new WebViewAssetLoader.Builder().setDomain(parentEngine.preferences.getString("hostname", "localhost")).setHttpAllowed(true);
        httpAllowed.addPathHandler("/", new WebViewAssetLoader.PathHandler() { // from class: org.apache.cordova.engine.SystemWebViewClient$$ExternalSyntheticLambda0
            @Override // androidx.webkit.WebViewAssetLoader.PathHandler
            public final WebResourceResponse handle(String str) {
                return SystemWebViewClient.this.m1841lambda$new$0$orgapachecordovaengineSystemWebViewClient(parentEngine, str);
            }
        });
        this.assetLoader = httpAllowed.build();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$new$0$org-apache-cordova-engine-SystemWebViewClient  reason: not valid java name */
    public /* synthetic */ WebResourceResponse m1841lambda$new$0$orgapachecordovaengineSystemWebViewClient(final SystemWebViewEngine parentEngine, String path) {
        WebResourceResponse handle;
        try {
            PluginManager pluginManager = this.parentEngine.pluginManager;
            if (pluginManager != null) {
                Iterator<CordovaPluginPathHandler> it = pluginManager.getPluginPathHandlers().iterator();
                while (it.hasNext()) {
                    CordovaPluginPathHandler next = it.next();
                    if (next.getPathHandler() != null && (handle = next.getPathHandler().handle(path)) != null) {
                        return handle;
                    }
                }
            }
            if (path.isEmpty()) {
                path = "index.html";
            }
            AssetManager assets = parentEngine.webView.getContext().getAssets();
            InputStream open = assets.open("www/" + path, 2);
            String str = "text/html";
            String fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(path);
            if (fileExtensionFromUrl != null) {
                if (!path.endsWith(".js") && !path.endsWith(".mjs")) {
                    str = path.endsWith(".wasm") ? "application/wasm" : MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionFromUrl);
                }
                str = "application/javascript";
            }
            return new WebResourceResponse(str, null, open);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.e(TAG, e.getMessage());
            return null;
        }
    }

    @Override // android.webkit.WebViewClient
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return this.parentEngine.client.onNavigationAttempt(url);
    }

    @Override // android.webkit.WebViewClient
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        AuthenticationToken authenticationToken = getAuthenticationToken(host, realm);
        if (authenticationToken != null) {
            handler.proceed(authenticationToken.getUserName(), authenticationToken.getPassword());
            return;
        }
        PluginManager pluginManager = this.parentEngine.pluginManager;
        if (pluginManager != null && pluginManager.onReceivedHttpAuthRequest(null, new CordovaHttpAuthHandler(handler), host, realm)) {
            this.parentEngine.client.clearLoadTimeoutTimer();
        } else {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }
    }

    @Override // android.webkit.WebViewClient
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        PluginManager pluginManager = this.parentEngine.pluginManager;
        if (pluginManager != null && pluginManager.onReceivedClientCertRequest(null, new CordovaClientCertRequest(request))) {
            this.parentEngine.client.clearLoadTimeoutTimer();
        } else {
            super.onReceivedClientCertRequest(view, request);
        }
    }

    @Override // android.webkit.WebViewClient
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        this.isCurrentlyLoading = true;
        this.parentEngine.bridge.reset();
        this.parentEngine.client.onPageStarted(url);
    }

    @Override // android.webkit.WebViewClient
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (this.isCurrentlyLoading || url.startsWith("about:")) {
            this.isCurrentlyLoading = false;
            if (this.doClearHistory) {
                view.clearHistory();
                this.doClearHistory = false;
            }
            this.parentEngine.client.onPageFinishedLoading(url);
        }
    }

    @Override // android.webkit.WebViewClient
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (this.isCurrentlyLoading) {
            LOG.d(TAG, "CordovaWebViewClient.onReceivedError: Error code=%s Description=%s URL=%s", Integer.valueOf(errorCode), description, failingUrl);
            if (errorCode == -10) {
                this.parentEngine.client.clearLoadTimeoutTimer();
                if (view.canGoBack()) {
                    view.goBack();
                    return;
                }
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
            this.parentEngine.client.onReceivedError(errorCode, description, failingUrl);
        }
    }

    @Override // android.webkit.WebViewClient
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        try {
            if ((this.parentEngine.cordova.getActivity().getPackageManager().getApplicationInfo(this.parentEngine.cordova.getActivity().getPackageName(), 128).flags & 2) != 0) {
                handler.proceed();
            } else {
                super.onReceivedSslError(view, handler, error);
            }
        } catch (PackageManager.NameNotFoundException unused) {
            super.onReceivedSslError(view, handler, error);
        }
    }

    public void setAuthenticationToken(AuthenticationToken authenticationToken, String host, String realm) {
        if (host == null) {
            host = HttpUrl.FRAGMENT_ENCODE_SET;
        }
        if (realm == null) {
            realm = HttpUrl.FRAGMENT_ENCODE_SET;
        }
        this.authenticationTokens.put(host.concat(realm), authenticationToken);
    }

    public AuthenticationToken removeAuthenticationToken(String host, String realm) {
        return this.authenticationTokens.remove(host.concat(realm));
    }

    public AuthenticationToken getAuthenticationToken(String host, String realm) {
        AuthenticationToken authenticationToken = this.authenticationTokens.get(host.concat(realm));
        if (authenticationToken == null) {
            AuthenticationToken authenticationToken2 = this.authenticationTokens.get(host);
            if (authenticationToken2 == null) {
                authenticationToken2 = this.authenticationTokens.get(realm);
            }
            AuthenticationToken authenticationToken3 = authenticationToken2;
            return authenticationToken3 == null ? this.authenticationTokens.get(HttpUrl.FRAGMENT_ENCODE_SET) : authenticationToken3;
        }
        return authenticationToken;
    }

    public void clearAuthenticationTokens() {
        this.authenticationTokens.clear();
    }

    @Override // android.webkit.WebViewClient
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        try {
            if (!this.parentEngine.pluginManager.shouldAllowRequest(url)) {
                LOG.w(TAG, "URL blocked by allow list: " + url);
                return new WebResourceResponse(AssetHelper.DEFAULT_MIME_TYPE, "UTF-8", null);
            }
            CordovaResourceApi cordovaResourceApi = this.parentEngine.resourceApi;
            Uri parse = Uri.parse(url);
            Uri remapUri = cordovaResourceApi.remapUri(parse);
            if (parse.equals(remapUri) && !needsSpecialsInAssetUrlFix(parse) && !needsContentUrlFix(parse)) {
                return null;
            }
            CordovaResourceApi.OpenForReadResult openForRead = cordovaResourceApi.openForRead(remapUri, true);
            return new WebResourceResponse(openForRead.mimeType, "UTF-8", openForRead.inputStream);
        } catch (IOException e) {
            if (!(e instanceof FileNotFoundException)) {
                LOG.e(TAG, "Error occurred while loading a file (returning a 404).", e);
            }
            return new WebResourceResponse(AssetHelper.DEFAULT_MIME_TYPE, "UTF-8", null);
        }
    }

    private static boolean needsContentUrlFix(Uri uri) {
        return "content".equals(uri.getScheme());
    }

    private static boolean needsSpecialsInAssetUrlFix(Uri uri) {
        if (CordovaResourceApi.getUriType(uri) != 1) {
            return false;
        }
        if (uri.getQuery() == null && uri.getFragment() == null) {
            uri.toString().contains("%");
            return false;
        }
        return true;
    }

    @Override // android.webkit.WebViewClient
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return this.assetLoader.shouldInterceptRequest(request.getUrl());
    }
}
