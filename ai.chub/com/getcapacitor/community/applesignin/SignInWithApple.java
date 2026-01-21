package com.getcapacitor.community.applesignin;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
@NativePlugin
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class SignInWithApple extends Plugin {
    @PluginMethod
    public void echo(PluginCall pluginCall) {
        String string = pluginCall.getString("value");
        JSObject jSObject = new JSObject();
        jSObject.put("value", string);
        pluginCall.success(jSObject);
    }
}
