package org.apache.cordova;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import java.util.HashMap;
import okhttp3.HttpUrl;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CoreAndroid extends CordovaPlugin {
    public static final String PLUGIN_NAME = "CoreAndroid";
    protected static final String TAG = "CordovaApp";
    private CallbackContext messageChannel;
    private final Object messageChannelLock = new Object();
    private PluginResult pendingPause;
    private PluginResult pendingResume;
    private BroadcastReceiver telephonyReceiver;

    public void fireJavascriptEvent(String action) {
        sendEventMessage(action);
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void pluginInitialize() {
        initTelephonyReceiver();
    }

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        PluginResult.Status status = PluginResult.Status.OK;
        try {
            if (action.equals("clearCache")) {
                clearCache();
            } else if (action.equals("show")) {
                this.cordova.getActivity().runOnUiThread(new Runnable() { // from class: org.apache.cordova.CoreAndroid.1
                    @Override // java.lang.Runnable
                    public void run() {
                        CoreAndroid.this.webView.getPluginManager().postMessage("spinner", "stop");
                    }
                });
            } else if (action.equals("loadUrl")) {
                loadUrl(args.getString(0), args.optJSONObject(1));
            } else if (!action.equals("cancelLoadUrl")) {
                if (action.equals("clearHistory")) {
                    clearHistory();
                } else if (action.equals("backHistory")) {
                    backHistory();
                } else if (action.equals("overrideButton")) {
                    overrideButton(args.getString(0), args.getBoolean(1));
                } else if (action.equals("overrideBackbutton")) {
                    overrideBackbutton(args.getBoolean(0));
                } else if (action.equals("exitApp")) {
                    exitApp();
                } else if (action.equals("messageChannel")) {
                    synchronized (this.messageChannelLock) {
                        this.messageChannel = callbackContext;
                        PluginResult pluginResult = this.pendingPause;
                        if (pluginResult != null) {
                            sendEventMessage(pluginResult);
                            this.pendingPause = null;
                        }
                        PluginResult pluginResult2 = this.pendingResume;
                        if (pluginResult2 != null) {
                            sendEventMessage(pluginResult2);
                            this.pendingResume = null;
                        }
                    }
                    return true;
                }
            }
            callbackContext.sendPluginResult(new PluginResult(status, HttpUrl.FRAGMENT_ENCODE_SET));
            return true;
        } catch (JSONException unused) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
            return false;
        }
    }

    public void clearCache() {
        this.cordova.getActivity().runOnUiThread(new Runnable() { // from class: org.apache.cordova.CoreAndroid.2
            @Override // java.lang.Runnable
            public void run() {
                CoreAndroid.this.webView.clearCache();
            }
        });
    }

    public void loadUrl(String url, JSONObject props) throws JSONException {
        boolean z;
        boolean z2;
        LOG.d("App", "App.loadUrl(" + url + "," + props + ")");
        HashMap hashMap = new HashMap();
        int i = 0;
        if (props != null) {
            JSONArray names = props.names();
            int i2 = 0;
            z = false;
            z2 = false;
            while (i < names.length()) {
                String string = names.getString(i);
                if (string.equals("wait")) {
                    i2 = props.getInt(string);
                } else if (string.equalsIgnoreCase("openexternal")) {
                    z = props.getBoolean(string);
                } else if (string.equalsIgnoreCase("clearhistory")) {
                    z2 = props.getBoolean(string);
                } else {
                    Object obj = props.get(string);
                    if (obj != null) {
                        if (obj.getClass().equals(String.class)) {
                            hashMap.put(string, (String) obj);
                        } else if (obj.getClass().equals(Boolean.class)) {
                            hashMap.put(string, (Boolean) obj);
                        } else if (obj.getClass().equals(Integer.class)) {
                            hashMap.put(string, (Integer) obj);
                        }
                    }
                }
                i++;
            }
            i = i2;
        } else {
            z = false;
            z2 = false;
        }
        if (i > 0) {
            try {
                synchronized (this) {
                    wait(i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.webView.showWebPage(url, z, z2, hashMap);
    }

    public void clearHistory() {
        this.cordova.getActivity().runOnUiThread(new Runnable() { // from class: org.apache.cordova.CoreAndroid.3
            @Override // java.lang.Runnable
            public void run() {
                CoreAndroid.this.webView.clearHistory();
            }
        });
    }

    public void backHistory() {
        this.cordova.getActivity().runOnUiThread(new Runnable() { // from class: org.apache.cordova.CoreAndroid.4
            @Override // java.lang.Runnable
            public void run() {
                CoreAndroid.this.webView.backHistory();
            }
        });
    }

    public void overrideBackbutton(boolean override) {
        LOG.i("App", "WARNING: Back Button Default Behavior will be overridden.  The backbutton event will be fired!");
        this.webView.setButtonPlumbedToJs(4, override);
    }

    public void overrideButton(String button, boolean override) {
        LOG.i("App", "WARNING: Volume Button Default Behavior will be overridden.  The volume event will be fired!");
        if (button.equals("volumeup")) {
            this.webView.setButtonPlumbedToJs(24, override);
        } else if (button.equals("volumedown")) {
            this.webView.setButtonPlumbedToJs(25, override);
        } else if (button.equals("menubutton")) {
            this.webView.setButtonPlumbedToJs(82, override);
        }
    }

    public boolean isBackbuttonOverridden() {
        return this.webView.isButtonPlumbedToJs(4);
    }

    public void exitApp() {
        this.webView.getPluginManager().postMessage("exit", null);
    }

    private void initTelephonyReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        this.telephonyReceiver = new BroadcastReceiver() { // from class: org.apache.cordova.CoreAndroid.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (intent != null && intent.getAction().equals("android.intent.action.PHONE_STATE") && intent.hasExtra("state")) {
                    String stringExtra = intent.getStringExtra("state");
                    if (stringExtra.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        LOG.i(CoreAndroid.TAG, "Telephone RINGING");
                        CoreAndroid.this.webView.getPluginManager().postMessage("telephone", "ringing");
                    } else if (stringExtra.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        LOG.i(CoreAndroid.TAG, "Telephone OFFHOOK");
                        CoreAndroid.this.webView.getPluginManager().postMessage("telephone", "offhook");
                    } else if (stringExtra.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        LOG.i(CoreAndroid.TAG, "Telephone IDLE");
                        CoreAndroid.this.webView.getPluginManager().postMessage("telephone", "idle");
                    }
                }
            }
        };
        this.webView.getContext().registerReceiver(this.telephonyReceiver, intentFilter);
    }

    private void sendEventMessage(String action) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("action", action);
        } catch (JSONException e) {
            LOG.e(TAG, "Failed to create event message", e);
        }
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jSONObject);
        if (this.messageChannel == null) {
            LOG.i(TAG, "Request to send event before messageChannel initialised: " + action);
            if ("pause".equals(action)) {
                this.pendingPause = pluginResult;
                return;
            } else if ("resume".equals(action)) {
                this.pendingPause = null;
                return;
            } else {
                return;
            }
        }
        sendEventMessage(pluginResult);
    }

    private void sendEventMessage(PluginResult payload) {
        payload.setKeepCallback(true);
        CallbackContext callbackContext = this.messageChannel;
        if (callbackContext != null) {
            callbackContext.sendPluginResult(payload);
        }
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onDestroy() {
        this.webView.getContext().unregisterReceiver(this.telephonyReceiver);
    }

    public void sendResumeEvent(PluginResult resumeEvent) {
        synchronized (this.messageChannelLock) {
            if (this.messageChannel != null) {
                sendEventMessage(resumeEvent);
            } else {
                this.pendingResume = resumeEvent;
            }
        }
    }

    public static Object getBuildConfigValue(Context ctx, String key) {
        try {
            return Class.forName(ctx.getClass().getPackage().getName() + ".BuildConfig").getField(key).get(null);
        } catch (ClassNotFoundException e) {
            LOG.d(TAG, "Unable to get the BuildConfig, is this built with ANT?");
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e2) {
            LOG.d(TAG, "Illegal Access Exception: Let's print a stack trace.");
            e2.printStackTrace();
            return null;
        } catch (NoSuchFieldException unused) {
            LOG.d(TAG, key + " is not a valid field. Check your build.gradle");
            return null;
        } catch (NullPointerException e3) {
            LOG.d(TAG, "Null Pointer Exception: Let's print a stack trace.");
            e3.printStackTrace();
            return null;
        }
    }
}
