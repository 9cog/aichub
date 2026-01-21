package com.getcapacitor;

import com.getcapacitor.util.JSONUtils;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class PluginConfig {
    private final JSONObject config;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PluginConfig(JSONObject jSONObject) {
        this.config = jSONObject;
    }

    public String getString(String str) {
        return getString(str, null);
    }

    public String getString(String str, String str2) {
        return JSONUtils.getString(this.config, str, str2);
    }

    public boolean getBoolean(String str, boolean z) {
        return JSONUtils.getBoolean(this.config, str, z);
    }

    public int getInt(String str, int i) {
        return JSONUtils.getInt(this.config, str, i);
    }

    public String[] getArray(String str) {
        return getArray(str, null);
    }

    public String[] getArray(String str, String[] strArr) {
        return JSONUtils.getArray(this.config, str, strArr);
    }

    public JSONObject getObject(String str) {
        return JSONUtils.getObject(this.config, str);
    }

    public boolean isEmpty() {
        return this.config.length() == 0;
    }

    public JSONObject getConfigJSON() {
        return this.config;
    }
}
