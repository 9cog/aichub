package org.apache.cordova;

import java.security.SecureRandom;
import okhttp3.HttpUrl;
import org.json.JSONArray;
import org.json.JSONException;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CordovaBridge {
    private static final String LOG_TAG = "CordovaBridge";
    private volatile int expectedBridgeSecret = -1;
    private NativeToJsMessageQueue jsMessageQueue;
    private PluginManager pluginManager;

    public CordovaBridge(PluginManager pluginManager, NativeToJsMessageQueue jsMessageQueue) {
        this.pluginManager = pluginManager;
        this.jsMessageQueue = jsMessageQueue;
    }

    public String jsExec(int bridgeSecret, String service, String action, String callbackId, String arguments) throws JSONException, IllegalAccessException {
        String str;
        if (verifySecret("exec()", bridgeSecret)) {
            if (arguments == null) {
                return "@Null arguments.";
            }
            this.jsMessageQueue.setPaused(true);
            try {
                CordovaResourceApi.jsThread = Thread.currentThread();
                this.pluginManager.exec(service, action, callbackId, arguments);
                str = this.jsMessageQueue.popAndEncode(false);
            } finally {
                try {
                    return str;
                } finally {
                }
            }
            return str;
        }
        return null;
    }

    public void jsSetNativeToJsBridgeMode(int bridgeSecret, int value) throws IllegalAccessException {
        if (verifySecret("setNativeToJsBridgeMode()", bridgeSecret)) {
            this.jsMessageQueue.setBridgeMode(value);
        }
    }

    public String jsRetrieveJsMessages(int bridgeSecret, boolean fromOnlineEvent) throws IllegalAccessException {
        if (verifySecret("retrieveJsMessages()", bridgeSecret)) {
            return this.jsMessageQueue.popAndEncode(fromOnlineEvent);
        }
        return null;
    }

    private boolean verifySecret(String action, int bridgeSecret) throws IllegalAccessException {
        if (this.jsMessageQueue.isBridgeEnabled()) {
            if (this.expectedBridgeSecret < 0 || bridgeSecret != this.expectedBridgeSecret) {
                LOG.e(LOG_TAG, "Bridge access attempt with wrong secret token, possibly from malicious code. Disabling exec() bridge!");
                clearBridgeSecret();
                throw new IllegalAccessException();
            }
            return true;
        } else if (bridgeSecret == -1) {
            LOG.d(LOG_TAG, action + " call made before bridge was enabled.");
            return false;
        } else {
            LOG.d(LOG_TAG, "Ignoring " + action + " from previous page load.");
            return false;
        }
    }

    void clearBridgeSecret() {
        this.expectedBridgeSecret = -1;
    }

    public boolean isSecretEstablished() {
        return this.expectedBridgeSecret != -1;
    }

    int generateBridgeSecret() {
        this.expectedBridgeSecret = new SecureRandom().nextInt(Integer.MAX_VALUE);
        return this.expectedBridgeSecret;
    }

    public void reset() {
        this.jsMessageQueue.reset();
        clearBridgeSecret();
    }

    public String promptOnJsPrompt(String origin, String message, String defaultValue) {
        if (defaultValue != null && defaultValue.startsWith("gap:")) {
            try {
                JSONArray jSONArray = new JSONArray(defaultValue.substring(4));
                String jsExec = jsExec(jSONArray.getInt(0), jSONArray.getString(1), jSONArray.getString(2), jSONArray.getString(3), message);
                return jsExec == null ? HttpUrl.FRAGMENT_ENCODE_SET : jsExec;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return HttpUrl.FRAGMENT_ENCODE_SET;
            } catch (JSONException e2) {
                e2.printStackTrace();
                return HttpUrl.FRAGMENT_ENCODE_SET;
            }
        } else if (defaultValue != null && defaultValue.startsWith("gap_bridge_mode:")) {
            try {
                jsSetNativeToJsBridgeMode(Integer.parseInt(defaultValue.substring(16)), Integer.parseInt(message));
            } catch (IllegalAccessException e3) {
                e3.printStackTrace();
            } catch (NumberFormatException e4) {
                e4.printStackTrace();
            }
            return HttpUrl.FRAGMENT_ENCODE_SET;
        } else if (defaultValue != null && defaultValue.startsWith("gap_poll:")) {
            try {
                String jsRetrieveJsMessages = jsRetrieveJsMessages(Integer.parseInt(defaultValue.substring(9)), "1".equals(message));
                return jsRetrieveJsMessages == null ? HttpUrl.FRAGMENT_ENCODE_SET : jsRetrieveJsMessages;
            } catch (IllegalAccessException e5) {
                e5.printStackTrace();
                return HttpUrl.FRAGMENT_ENCODE_SET;
            }
        } else if (defaultValue == null || !defaultValue.startsWith("gap_init:")) {
            return null;
        } else {
            if (this.pluginManager.shouldAllowBridgeAccess(origin)) {
                this.jsMessageQueue.setBridgeMode(Integer.parseInt(defaultValue.substring(9)));
                int generateBridgeSecret = generateBridgeSecret();
                return HttpUrl.FRAGMENT_ENCODE_SET + generateBridgeSecret;
            }
            LOG.e(LOG_TAG, "gap_init called from restricted origin: " + origin);
            return HttpUrl.FRAGMENT_ENCODE_SET;
        }
    }
}
