package org.apache.cordova;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.cordova.CordovaResourceApi;
import org.json.JSONArray;
import org.json.JSONException;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CordovaPlugin {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public CordovaInterface cordova;
    protected CordovaPreferences preferences;
    private String serviceName;
    public CordovaWebView webView;

    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        return false;
    }

    public CordovaPluginPathHandler getPathHandler() {
        return null;
    }

    public boolean hasPermisssion() {
        return true;
    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void onDestroy() {
    }

    public Object onMessage(String id, Object data) {
        return null;
    }

    public void onNewIntent(Intent intent) {
    }

    public boolean onOverrideUrlLoading(String url) {
        return false;
    }

    public void onPause(boolean multitasking) {
    }

    public boolean onReceivedClientCertRequest(CordovaWebView view, ICordovaClientCertRequest request) {
        return false;
    }

    public boolean onReceivedHttpAuthRequest(CordovaWebView view, ICordovaHttpAuthHandler handler, String host, String realm) {
        return false;
    }

    @Deprecated
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
    }

    public void onReset() {
    }

    public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext) {
    }

    public void onResume(boolean multitasking) {
    }

    public Bundle onSaveInstanceState() {
        return null;
    }

    public void onStart() {
    }

    public void onStop() {
    }

    protected void pluginInitialize() {
    }

    public Uri remapUri(Uri uri) {
        return null;
    }

    public void requestPermissions(int requestCode) {
    }

    public Boolean shouldAllowNavigation(String url) {
        return null;
    }

    public Boolean shouldAllowRequest(String url) {
        return null;
    }

    public Boolean shouldOpenExternalUrl(String url) {
        return null;
    }

    public final void privateInitialize(String serviceName, CordovaInterface cordova, CordovaWebView webView, CordovaPreferences preferences) {
        this.serviceName = serviceName;
        this.cordova = cordova;
        this.webView = webView;
        this.preferences = preferences;
        initialize(cordova, webView);
        pluginInitialize();
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public boolean execute(String action, String rawArgs, CallbackContext callbackContext) throws JSONException {
        return execute(action, new JSONArray(rawArgs), callbackContext);
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        return execute(action, new CordovaArgs(args), callbackContext);
    }

    public Boolean shouldAllowBridgeAccess(String url) {
        return shouldAllowNavigation(url);
    }

    public CordovaResourceApi.OpenForReadResult handleOpenForRead(Uri uri) throws IOException {
        throw new FileNotFoundException("Plugin can't handle uri: " + uri);
    }

    protected Uri toPluginUri(Uri origUri) {
        return new Uri.Builder().scheme(CordovaResourceApi.PLUGIN_URI_SCHEME).authority(this.serviceName).appendQueryParameter("origUri", origUri.toString()).build();
    }

    protected Uri fromPluginUri(Uri pluginUri) {
        return Uri.parse(pluginUri.getQueryParameter("origUri"));
    }
}
