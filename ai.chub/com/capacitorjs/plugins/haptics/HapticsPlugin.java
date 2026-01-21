package com.capacitorjs.plugins.haptics;

import com.capacitorjs.plugins.haptics.arguments.HapticsImpactType;
import com.capacitorjs.plugins.haptics.arguments.HapticsNotificationType;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
@CapacitorPlugin(name = "Haptics")
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class HapticsPlugin extends Plugin {
    private Haptics implementation;

    @Override // com.getcapacitor.Plugin
    public void load() {
        this.implementation = new Haptics(getContext());
    }

    @PluginMethod
    public void vibrate(PluginCall pluginCall) {
        this.implementation.vibrate(pluginCall.getInt("duration", 300).intValue());
        pluginCall.resolve();
    }

    @PluginMethod
    public void impact(PluginCall pluginCall) {
        this.implementation.performHaptics(HapticsImpactType.fromString(pluginCall.getString("style")));
        pluginCall.resolve();
    }

    @PluginMethod
    public void notification(PluginCall pluginCall) {
        this.implementation.performHaptics(HapticsNotificationType.fromString(pluginCall.getString("type")));
        pluginCall.resolve();
    }

    @PluginMethod
    public void selectionStart(PluginCall pluginCall) {
        this.implementation.selectionStart();
        pluginCall.resolve();
    }

    @PluginMethod
    public void selectionChanged(PluginCall pluginCall) {
        this.implementation.selectionChanged();
        pluginCall.resolve();
    }

    @PluginMethod
    public void selectionEnd(PluginCall pluginCall) {
        this.implementation.selectionEnd();
        pluginCall.resolve();
    }
}
