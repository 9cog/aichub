package com.getcapacitor;

import org.json.JSONException;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class JSValue {
    private final Object value;

    public JSValue(PluginCall pluginCall, String str) {
        this.value = toValue(pluginCall, str);
    }

    public Object getValue() {
        return this.value;
    }

    public String toString() {
        return getValue().toString();
    }

    public JSObject toJSObject() throws JSONException {
        Object obj = this.value;
        if (obj instanceof JSObject) {
            return (JSObject) obj;
        }
        throw new JSONException("JSValue could not be coerced to JSObject.");
    }

    public JSArray toJSArray() throws JSONException {
        Object obj = this.value;
        if (obj instanceof JSArray) {
            return (JSArray) obj;
        }
        throw new JSONException("JSValue could not be coerced to JSArray.");
    }

    private Object toValue(PluginCall pluginCall, String str) {
        JSArray array = pluginCall.getArray(str, null);
        if (array != null) {
            return array;
        }
        JSObject object = pluginCall.getObject(str, null);
        if (object != null) {
            return object;
        }
        String string = pluginCall.getString(str, null);
        return string != null ? string : pluginCall.getData().opt(str);
    }
}
