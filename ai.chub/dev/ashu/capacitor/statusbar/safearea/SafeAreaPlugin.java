package dev.ashu.capacitor.statusbar.safearea;

import android.content.res.Resources;
import android.graphics.Insets;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.WindowInsets;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
@CapacitorPlugin(name = "SafeArea")
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class SafeAreaPlugin extends Plugin {
    private SafeArea implementation = new SafeArea();

    @PluginMethod
    public void getStatusBarHeight(PluginCall pluginCall) {
        Resources resources = getActivity().getApplicationContext().getResources();
        int identifier = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            int dimensionPixelSize = resources.getDimensionPixelSize(identifier);
            JSObject jSObject = new JSObject();
            jSObject.put("height", dimensionPixelSize / resources.getDisplayMetrics().density);
            pluginCall.resolve(jSObject);
            return;
        }
        pluginCall.reject("Status bar height not obtained");
    }

    @PluginMethod
    public void getSafeAreaInsets(PluginCall pluginCall) {
        float f;
        float f2;
        float f3;
        if (Build.VERSION.SDK_INT >= 23) {
            Resources resources = getActivity().getApplicationContext().getResources();
            WindowInsets rootWindowInsets = getActivity().getWindow().getDecorView().getRootWindowInsets();
            if (Build.VERSION.SDK_INT >= 29 && rootWindowInsets != null) {
                DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
                float safeInsetLeft = displayCutout != null ? displayCutout.getSafeInsetLeft() : 0.0f;
                float safeInsetRight = displayCutout != null ? displayCutout.getSafeInsetRight() : 0.0f;
                float safeInsetTop = displayCutout != null ? displayCutout.getSafeInsetTop() : 0.0f;
                r2 = displayCutout != null ? displayCutout.getSafeInsetBottom() : 0.0f;
                Insets systemWindowInsets = rootWindowInsets.getSystemWindowInsets();
                f2 = Math.max(safeInsetLeft, systemWindowInsets.left) / resources.getDisplayMetrics().density;
                f3 = Math.max(safeInsetRight, systemWindowInsets.right) / resources.getDisplayMetrics().density;
                f = Math.max(r2, systemWindowInsets.bottom) / resources.getDisplayMetrics().density;
                r2 = Math.max(safeInsetTop, systemWindowInsets.top) / resources.getDisplayMetrics().density;
                JSObject jSObject = new JSObject();
                jSObject.put("top", r2);
                jSObject.put("bottom", f);
                jSObject.put("left", f2);
                jSObject.put("right", f3);
                pluginCall.resolve(jSObject);
            }
        }
        f = 0.0f;
        f2 = 0.0f;
        f3 = 0.0f;
        JSObject jSObject2 = new JSObject();
        jSObject2.put("top", r2);
        jSObject2.put("bottom", f);
        jSObject2.put("left", f2);
        jSObject2.put("right", f3);
        pluginCall.resolve(jSObject2);
    }
}
