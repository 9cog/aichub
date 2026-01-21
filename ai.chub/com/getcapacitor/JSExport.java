package com.getcapacitor;

import android.content.Context;
import android.text.TextUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import okhttp3.HttpUrl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class JSExport {
    private static String CALLBACK_PARAM = "_callback";
    private static String CATCHALL_OPTIONS_PARAM = "_options";

    public static String getGlobalJS(Context context, boolean z, boolean z2) {
        return "window.Capacitor = { DEBUG: " + z2 + ", isLoggingEnabled: " + z + ", Plugins: {} };";
    }

    public static String getCordovaJS(Context context) {
        try {
            return FileUtils.readFileFromAssets(context.getAssets(), "public/cordova.js");
        } catch (IOException unused) {
            Logger.error("Unable to read public/cordova.js file, Cordova plugins will not work");
            return HttpUrl.FRAGMENT_ENCODE_SET;
        }
    }

    public static String getCordovaPluginsFileJS(Context context) {
        try {
            return FileUtils.readFileFromAssets(context.getAssets(), "public/cordova_plugins.js");
        } catch (IOException unused) {
            Logger.error("Unable to read public/cordova_plugins.js file, Cordova plugins will not work");
            return HttpUrl.FRAGMENT_ENCODE_SET;
        }
    }

    public static String getPluginJS(Collection<PluginHandle> collection) {
        ArrayList arrayList = new ArrayList();
        JSONArray jSONArray = new JSONArray();
        arrayList.add("// Begin: Capacitor Plugin JS");
        for (PluginHandle pluginHandle : collection) {
            arrayList.add("(function(w) {\nvar a = (w.Capacitor = w.Capacitor || {});\nvar p = (a.Plugins = a.Plugins || {});\nvar t = (p['" + pluginHandle.getId() + "'] = {});\nt.addListener = function(eventName, callback) {\n  return w.Capacitor.addListener('" + pluginHandle.getId() + "', eventName, callback);\n}");
            for (PluginMethodHandle pluginMethodHandle : pluginHandle.getMethods()) {
                if (!pluginMethodHandle.getName().equals("addListener") && !pluginMethodHandle.getName().equals("removeListener")) {
                    arrayList.add(generateMethodJS(pluginHandle, pluginMethodHandle));
                }
            }
            arrayList.add("})(window);\n");
            jSONArray.put(createPluginHeader(pluginHandle));
        }
        return TextUtils.join("\n", arrayList) + "\nwindow.Capacitor.PluginHeaders = " + jSONArray.toString() + ";";
    }

    public static String getCordovaPluginJS(Context context) {
        return getFilesContent(context, "public/plugins");
    }

    public static String getFilesContent(Context context, String str) {
        String[] list;
        StringBuilder sb = new StringBuilder();
        try {
            list = context.getAssets().list(str);
        } catch (IOException unused) {
            Logger.warn("Unable to read file at path " + str);
        }
        if (list.length > 0) {
            for (String str2 : list) {
                if (!str2.endsWith(".map")) {
                    sb.append(getFilesContent(context, str + "/" + str2));
                }
            }
            return sb.toString();
        }
        return FileUtils.readFileFromAssets(context.getAssets(), str);
    }

    private static JSONObject createPluginHeader(PluginHandle pluginHandle) {
        JSONObject jSONObject = new JSONObject();
        Collection<PluginMethodHandle> methods = pluginHandle.getMethods();
        try {
            String id = pluginHandle.getId();
            JSONArray jSONArray = new JSONArray();
            jSONObject.put("name", id);
            for (PluginMethodHandle pluginMethodHandle : methods) {
                jSONArray.put(createPluginMethodHeader(pluginMethodHandle));
            }
            jSONObject.put("methods", jSONArray);
        } catch (JSONException unused) {
        }
        return jSONObject;
    }

    private static JSONObject createPluginMethodHeader(PluginMethodHandle pluginMethodHandle) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("name", pluginMethodHandle.getName());
            if (!pluginMethodHandle.getReturnType().equals("none")) {
                jSONObject.put("rtype", pluginMethodHandle.getReturnType());
            }
        } catch (JSONException unused) {
        }
        return jSONObject;
    }

    public static String getBridgeJS(Context context) throws JSExportException {
        return getFilesContent(context, "native-bridge.js");
    }

    private static String generateMethodJS(PluginHandle pluginHandle, PluginMethodHandle pluginMethodHandle) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(CATCHALL_OPTIONS_PARAM);
        String returnType = pluginMethodHandle.getReturnType();
        if (returnType.equals(PluginMethod.RETURN_CALLBACK)) {
            arrayList2.add(CALLBACK_PARAM);
        }
        arrayList.add("t['" + pluginMethodHandle.getName() + "'] = function(" + TextUtils.join(", ", arrayList2) + ") {");
        returnType.hashCode();
        char c = 65535;
        switch (returnType.hashCode()) {
            case -309216997:
                if (returnType.equals(PluginMethod.RETURN_PROMISE)) {
                    c = 0;
                    break;
                }
                break;
            case -172220347:
                if (returnType.equals(PluginMethod.RETURN_CALLBACK)) {
                    c = 1;
                    break;
                }
                break;
            case 3387192:
                if (returnType.equals("none")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                arrayList.add("return w.Capacitor.nativePromise('" + pluginHandle.getId() + "', '" + pluginMethodHandle.getName() + "', " + CATCHALL_OPTIONS_PARAM + ")");
                break;
            case 1:
                arrayList.add("return w.Capacitor.nativeCallback('" + pluginHandle.getId() + "', '" + pluginMethodHandle.getName() + "', " + CATCHALL_OPTIONS_PARAM + ", " + CALLBACK_PARAM + ")");
                break;
            case 2:
                arrayList.add("return w.Capacitor.nativeCallback('" + pluginHandle.getId() + "', '" + pluginMethodHandle.getName() + "', " + CATCHALL_OPTIONS_PARAM + ")");
                break;
        }
        arrayList.add("}");
        return TextUtils.join("\n", arrayList);
    }
}
