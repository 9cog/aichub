package org.apache.cordova;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.cordova.PluginResult;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class NativeToJsMessageQueue {
    private static int COMBINED_RESPONSE_CUTOFF = 16777216;
    static final boolean DISABLE_EXEC_CHAINING = false;
    private static final boolean FORCE_ENCODE_USING_EVAL = false;
    private static final String LOG_TAG = "JsMessageQueue";
    private BridgeMode activeBridgeMode;
    private boolean paused;
    private final LinkedList<JsMessage> queue = new LinkedList<>();
    private ArrayList<BridgeMode> bridgeModes = new ArrayList<>();

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static abstract class BridgeMode {
        public void notifyOfFlush(NativeToJsMessageQueue queue, boolean fromOnlineEvent) {
        }

        public abstract void onNativeToJsMessageAvailable(NativeToJsMessageQueue queue);

        public void reset() {
        }
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class NoOpBridgeMode extends BridgeMode {
        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void onNativeToJsMessageAvailable(NativeToJsMessageQueue queue) {
        }
    }

    public void addBridgeMode(BridgeMode bridgeMode) {
        this.bridgeModes.add(bridgeMode);
    }

    public boolean isBridgeEnabled() {
        return this.activeBridgeMode != null;
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public void setBridgeMode(int value) {
        if (value < -1 || value >= this.bridgeModes.size()) {
            LOG.d(LOG_TAG, "Invalid NativeToJsBridgeMode: " + value);
            return;
        }
        BridgeMode bridgeMode = value < 0 ? null : this.bridgeModes.get(value);
        if (bridgeMode != this.activeBridgeMode) {
            StringBuilder sb = new StringBuilder("Set native->JS mode to ");
            sb.append(bridgeMode == null ? "null" : bridgeMode.getClass().getSimpleName());
            LOG.d(LOG_TAG, sb.toString());
            synchronized (this) {
                this.activeBridgeMode = bridgeMode;
                if (bridgeMode != null) {
                    bridgeMode.reset();
                    if (!this.paused && !this.queue.isEmpty()) {
                        bridgeMode.onNativeToJsMessageAvailable(this);
                    }
                }
            }
        }
    }

    public void reset() {
        synchronized (this) {
            this.queue.clear();
            setBridgeMode(-1);
        }
    }

    private int calculatePackedMessageLength(JsMessage message) {
        int calculateEncodedLength = message.calculateEncodedLength();
        return String.valueOf(calculateEncodedLength).length() + calculateEncodedLength + 1;
    }

    private void packMessage(JsMessage message, StringBuilder sb) {
        sb.append(message.calculateEncodedLength());
        sb.append(' ');
        message.encodeAsMessage(sb);
    }

    public String popAndEncode(boolean fromOnlineEvent) {
        int i;
        synchronized (this) {
            BridgeMode bridgeMode = this.activeBridgeMode;
            if (bridgeMode == null) {
                return null;
            }
            bridgeMode.notifyOfFlush(this, fromOnlineEvent);
            if (this.queue.isEmpty()) {
                return null;
            }
            Iterator<JsMessage> it = this.queue.iterator();
            int i2 = 0;
            int i3 = 0;
            while (it.hasNext()) {
                int calculatePackedMessageLength = calculatePackedMessageLength(it.next());
                if (i2 > 0 && (i = COMBINED_RESPONSE_CUTOFF) > 0 && i3 + calculatePackedMessageLength > i) {
                    break;
                }
                i3 += calculatePackedMessageLength;
                i2++;
            }
            StringBuilder sb = new StringBuilder(i3);
            for (int i4 = 0; i4 < i2; i4++) {
                packMessage(this.queue.removeFirst(), sb);
            }
            if (!this.queue.isEmpty()) {
                sb.append('*');
            }
            return sb.toString();
        }
    }

    public String popAndEncodeAsJs() {
        int i;
        synchronized (this) {
            if (this.queue.size() == 0) {
                return null;
            }
            Iterator<JsMessage> it = this.queue.iterator();
            int i2 = 0;
            int i3 = 0;
            while (it.hasNext()) {
                int calculateEncodedLength = it.next().calculateEncodedLength() + 50;
                if (i2 > 0 && (i = COMBINED_RESPONSE_CUTOFF) > 0 && i3 + calculateEncodedLength > i) {
                    break;
                }
                i3 += calculateEncodedLength;
                i2++;
            }
            int i4 = i2 == this.queue.size() ? 1 : 0;
            StringBuilder sb = new StringBuilder(i3 + (i4 != 0 ? 0 : 100));
            for (int i5 = 0; i5 < i2; i5++) {
                JsMessage removeFirst = this.queue.removeFirst();
                if (i4 != 0 && i5 + 1 == i2) {
                    removeFirst.encodeAsJsMessage(sb);
                } else {
                    sb.append("try{");
                    removeFirst.encodeAsJsMessage(sb);
                    sb.append("}finally{");
                }
            }
            if (i4 == 0) {
                sb.append("window.setTimeout(function(){cordova.require('cordova/plugin/android/polling').pollOnce();},0);");
            }
            while (i4 < i2) {
                sb.append('}');
                i4++;
            }
            return sb.toString();
        }
    }

    public void addJavaScript(String statement) {
        enqueueMessage(new JsMessage(statement));
    }

    public void addPluginResult(PluginResult result, String callbackId) {
        if (callbackId == null) {
            LOG.e(LOG_TAG, "Got plugin result with no callbackId", new Throwable());
            return;
        }
        boolean z = result.getStatus() == PluginResult.Status.NO_RESULT.ordinal();
        boolean keepCallback = result.getKeepCallback();
        if (z && keepCallback) {
            return;
        }
        enqueueMessage(new JsMessage(result, callbackId));
    }

    private void enqueueMessage(JsMessage message) {
        synchronized (this) {
            if (this.activeBridgeMode == null) {
                LOG.d(LOG_TAG, "Dropping Native->JS message due to disabled bridge");
                return;
            }
            this.queue.add(message);
            if (!this.paused) {
                this.activeBridgeMode.onNativeToJsMessageAvailable(this);
            }
        }
    }

    public void setPaused(boolean value) {
        BridgeMode bridgeMode;
        if (this.paused && value) {
            LOG.e(LOG_TAG, "nested call to setPaused detected.", new Throwable());
        }
        this.paused = value;
        if (value) {
            return;
        }
        synchronized (this) {
            if (!this.queue.isEmpty() && (bridgeMode = this.activeBridgeMode) != null) {
                bridgeMode.onNativeToJsMessageAvailable(this);
            }
        }
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class LoadUrlBridgeMode extends BridgeMode {
        private final CordovaInterface cordova;
        private final CordovaWebViewEngine engine;

        public LoadUrlBridgeMode(CordovaWebViewEngine engine, CordovaInterface cordova) {
            this.engine = engine;
            this.cordova = cordova;
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void onNativeToJsMessageAvailable(final NativeToJsMessageQueue queue) {
            this.cordova.getActivity().runOnUiThread(new Runnable() { // from class: org.apache.cordova.NativeToJsMessageQueue.LoadUrlBridgeMode.1
                @Override // java.lang.Runnable
                public void run() {
                    String popAndEncodeAsJs = queue.popAndEncodeAsJs();
                    if (popAndEncodeAsJs != null) {
                        CordovaWebViewEngine cordovaWebViewEngine = LoadUrlBridgeMode.this.engine;
                        cordovaWebViewEngine.loadUrl("javascript:" + popAndEncodeAsJs, false);
                    }
                }
            });
        }
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class OnlineEventsBridgeMode extends BridgeMode {
        private final OnlineEventsBridgeModeDelegate delegate;
        private boolean ignoreNextFlush;
        private boolean online;

        /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
        public interface OnlineEventsBridgeModeDelegate {
            void runOnUiThread(Runnable r);

            void setNetworkAvailable(boolean value);
        }

        public OnlineEventsBridgeMode(OnlineEventsBridgeModeDelegate delegate) {
            this.delegate = delegate;
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void reset() {
            this.delegate.runOnUiThread(new Runnable() { // from class: org.apache.cordova.NativeToJsMessageQueue.OnlineEventsBridgeMode.1
                @Override // java.lang.Runnable
                public void run() {
                    OnlineEventsBridgeMode.this.online = false;
                    OnlineEventsBridgeMode.this.ignoreNextFlush = true;
                    OnlineEventsBridgeMode.this.delegate.setNetworkAvailable(true);
                }
            });
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void onNativeToJsMessageAvailable(final NativeToJsMessageQueue queue) {
            this.delegate.runOnUiThread(new Runnable() { // from class: org.apache.cordova.NativeToJsMessageQueue.OnlineEventsBridgeMode.2
                @Override // java.lang.Runnable
                public void run() {
                    if (queue.isEmpty()) {
                        return;
                    }
                    OnlineEventsBridgeMode.this.ignoreNextFlush = false;
                    OnlineEventsBridgeMode.this.delegate.setNetworkAvailable(OnlineEventsBridgeMode.this.online);
                }
            });
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void notifyOfFlush(final NativeToJsMessageQueue queue, boolean fromOnlineEvent) {
            if (!fromOnlineEvent || this.ignoreNextFlush) {
                return;
            }
            this.online = !this.online;
        }
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class EvalBridgeMode extends BridgeMode {
        private final CordovaInterface cordova;
        private final CordovaWebViewEngine engine;

        public EvalBridgeMode(CordovaWebViewEngine engine, CordovaInterface cordova) {
            this.engine = engine;
            this.cordova = cordova;
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void onNativeToJsMessageAvailable(final NativeToJsMessageQueue queue) {
            this.cordova.getActivity().runOnUiThread(new Runnable() { // from class: org.apache.cordova.NativeToJsMessageQueue.EvalBridgeMode.1
                @Override // java.lang.Runnable
                public void run() {
                    String popAndEncodeAsJs = queue.popAndEncodeAsJs();
                    if (popAndEncodeAsJs != null) {
                        EvalBridgeMode.this.engine.evaluateJavascript(popAndEncodeAsJs, null);
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class JsMessage {
        final String jsPayloadOrCallbackId;
        final PluginResult pluginResult;

        JsMessage(String js) {
            js.getClass();
            this.jsPayloadOrCallbackId = js;
            this.pluginResult = null;
        }

        JsMessage(PluginResult pluginResult, String callbackId) {
            if (callbackId == null || pluginResult == null) {
                throw null;
            }
            this.jsPayloadOrCallbackId = callbackId;
            this.pluginResult = pluginResult;
        }

        static int calculateEncodedLengthHelper(PluginResult pluginResult) {
            switch (pluginResult.getMessageType()) {
                case 1:
                    return pluginResult.getStrMessage().length() + 1;
                case 2:
                default:
                    return pluginResult.getMessage().length();
                case 3:
                    return pluginResult.getMessage().length() + 1;
                case 4:
                case 5:
                    return 1;
                case 6:
                    return pluginResult.getMessage().length() + 1;
                case 7:
                    return pluginResult.getMessage().length() + 1;
                case 8:
                    int i = 1;
                    for (int i2 = 0; i2 < pluginResult.getMultipartMessagesSize(); i2++) {
                        int calculateEncodedLengthHelper = calculateEncodedLengthHelper(pluginResult.getMultipartMessage(i2));
                        i += String.valueOf(calculateEncodedLengthHelper).length() + 1 + calculateEncodedLengthHelper;
                    }
                    return i;
            }
        }

        int calculateEncodedLength() {
            PluginResult pluginResult = this.pluginResult;
            if (pluginResult == null) {
                return this.jsPayloadOrCallbackId.length() + 1;
            }
            return String.valueOf(pluginResult.getStatus()).length() + 2 + 1 + this.jsPayloadOrCallbackId.length() + 1 + calculateEncodedLengthHelper(this.pluginResult);
        }

        static void encodeAsMessageHelper(StringBuilder sb, PluginResult pluginResult) {
            switch (pluginResult.getMessageType()) {
                case 1:
                    sb.append('s');
                    sb.append(pluginResult.getStrMessage());
                    return;
                case 2:
                default:
                    sb.append(pluginResult.getMessage());
                    return;
                case 3:
                    sb.append('n');
                    sb.append(pluginResult.getMessage());
                    return;
                case 4:
                    sb.append(pluginResult.getMessage().charAt(0));
                    return;
                case 5:
                    sb.append('N');
                    return;
                case 6:
                    sb.append('A');
                    sb.append(pluginResult.getMessage());
                    return;
                case 7:
                    sb.append('S');
                    sb.append(pluginResult.getMessage());
                    return;
                case 8:
                    sb.append('M');
                    for (int i = 0; i < pluginResult.getMultipartMessagesSize(); i++) {
                        PluginResult multipartMessage = pluginResult.getMultipartMessage(i);
                        sb.append(String.valueOf(calculateEncodedLengthHelper(multipartMessage)));
                        sb.append(' ');
                        encodeAsMessageHelper(sb, multipartMessage);
                    }
                    return;
            }
        }

        void encodeAsMessage(StringBuilder sb) {
            PluginResult pluginResult = this.pluginResult;
            if (pluginResult == null) {
                sb.append('J');
                sb.append(this.jsPayloadOrCallbackId);
                return;
            }
            int status = pluginResult.getStatus();
            boolean z = status == PluginResult.Status.NO_RESULT.ordinal();
            boolean z2 = status == PluginResult.Status.OK.ordinal();
            boolean keepCallback = this.pluginResult.getKeepCallback();
            sb.append((z || z2) ? 'S' : 'F');
            sb.append(keepCallback ? '1' : '0');
            sb.append(status);
            sb.append(' ');
            sb.append(this.jsPayloadOrCallbackId);
            sb.append(' ');
            encodeAsMessageHelper(sb, this.pluginResult);
        }

        void buildJsMessage(StringBuilder sb) {
            int messageType = this.pluginResult.getMessageType();
            if (messageType == 5) {
                sb.append("null");
            } else if (messageType == 6) {
                sb.append("cordova.require('cordova/base64').toArrayBuffer('");
                sb.append(this.pluginResult.getMessage());
                sb.append("')");
            } else if (messageType == 7) {
                sb.append("atob('");
                sb.append(this.pluginResult.getMessage());
                sb.append("')");
            } else if (messageType == 8) {
                int multipartMessagesSize = this.pluginResult.getMultipartMessagesSize();
                for (int i = 0; i < multipartMessagesSize; i++) {
                    new JsMessage(this.pluginResult.getMultipartMessage(i), this.jsPayloadOrCallbackId).buildJsMessage(sb);
                    if (i < multipartMessagesSize - 1) {
                        sb.append(",");
                    }
                }
            } else {
                sb.append(this.pluginResult.getMessage());
            }
        }

        void encodeAsJsMessage(StringBuilder sb) {
            PluginResult pluginResult = this.pluginResult;
            if (pluginResult == null) {
                sb.append(this.jsPayloadOrCallbackId);
                return;
            }
            int status = pluginResult.getStatus();
            boolean z = status == PluginResult.Status.OK.ordinal() || status == PluginResult.Status.NO_RESULT.ordinal();
            sb.append("cordova.callbackFromNative('");
            sb.append(this.jsPayloadOrCallbackId);
            sb.append("',");
            sb.append(z);
            sb.append(",");
            sb.append(status);
            sb.append(",[");
            buildJsMessage(sb);
            sb.append("],");
            sb.append(this.pluginResult.getKeepCallback());
            sb.append(");");
        }
    }
}
