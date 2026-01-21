package com.capacitorjs.plugins.keyboard;

import android.os.Handler;
import android.os.Looper;
import com.capacitorjs.plugins.keyboard.Keyboard;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
@CapacitorPlugin(name = "Keyboard")
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class KeyboardPlugin extends Plugin {
    private Keyboard implementation;

    @Override // com.getcapacitor.Plugin
    public void load() {
        execute(new Runnable() { // from class: com.capacitorjs.plugins.keyboard.KeyboardPlugin$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                KeyboardPlugin.this.lambda$load$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$load$0() {
        Keyboard keyboard = new Keyboard(getActivity(), getConfig().getBoolean("resizeOnFullScreen", false));
        this.implementation = keyboard;
        keyboard.setKeyboardEventListener(new Keyboard.KeyboardEventListener() { // from class: com.capacitorjs.plugins.keyboard.KeyboardPlugin$$ExternalSyntheticLambda2
            @Override // com.capacitorjs.plugins.keyboard.Keyboard.KeyboardEventListener
            public final void onKeyboardEvent(String str, int i) {
                KeyboardPlugin.this.onKeyboardEvent(str, i);
            }
        });
    }

    @PluginMethod
    public void show(final PluginCall pluginCall) {
        execute(new Runnable() { // from class: com.capacitorjs.plugins.keyboard.KeyboardPlugin$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                KeyboardPlugin.this.lambda$show$2(pluginCall);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$show$2(final PluginCall pluginCall) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.capacitorjs.plugins.keyboard.KeyboardPlugin$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                KeyboardPlugin.this.lambda$show$1(pluginCall);
            }
        }, 350L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$show$1(PluginCall pluginCall) {
        this.implementation.show();
        pluginCall.resolve();
    }

    @PluginMethod
    public void hide(final PluginCall pluginCall) {
        execute(new Runnable() { // from class: com.capacitorjs.plugins.keyboard.KeyboardPlugin$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                KeyboardPlugin.this.lambda$hide$3(pluginCall);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$hide$3(PluginCall pluginCall) {
        if (!this.implementation.hide()) {
            pluginCall.reject("Can't close keyboard, not currently focused");
        } else {
            pluginCall.resolve();
        }
    }

    @PluginMethod
    public void setAccessoryBarVisible(PluginCall pluginCall) {
        pluginCall.unimplemented();
    }

    @PluginMethod
    public void setStyle(PluginCall pluginCall) {
        pluginCall.unimplemented();
    }

    @PluginMethod
    public void setResizeMode(PluginCall pluginCall) {
        pluginCall.unimplemented();
    }

    @PluginMethod
    public void getResizeMode(PluginCall pluginCall) {
        pluginCall.unimplemented();
    }

    @PluginMethod
    public void setScroll(PluginCall pluginCall) {
        pluginCall.unimplemented();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onKeyboardEvent(String str, int i) {
        JSObject jSObject = new JSObject();
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -662060934:
                if (str.equals("keyboardDidHide")) {
                    c = 0;
                    break;
                }
                break;
            case -661733835:
                if (str.equals("keyboardDidShow")) {
                    c = 1;
                    break;
                }
                break;
            case -34092741:
                if (str.equals("keyboardWillHide")) {
                    c = 2;
                    break;
                }
                break;
            case -33765642:
                if (str.equals("keyboardWillShow")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 2:
                this.bridge.triggerWindowJSEvent(str);
                notifyListeners(str, jSObject);
                return;
            case 1:
            case 3:
                this.bridge.triggerWindowJSEvent(str, "{ 'keyboardHeight': " + i + " }");
                jSObject.put("keyboardHeight", i);
                notifyListeners(str, jSObject);
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.getcapacitor.Plugin
    public void handleOnDestroy() {
        this.implementation.setKeyboardEventListener(null);
    }
}
