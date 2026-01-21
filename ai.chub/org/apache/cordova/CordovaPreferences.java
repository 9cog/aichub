package org.apache.cordova;

import android.os.Bundle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import okhttp3.HttpUrl;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CordovaPreferences {
    private Bundle preferencesBundleExtras;
    private HashMap<String, String> prefs = new HashMap<>(20);

    public void setPreferencesBundle(Bundle extras) {
        this.preferencesBundleExtras = extras;
    }

    public void set(String name, String value) {
        this.prefs.put(name.toLowerCase(Locale.ENGLISH), value);
    }

    public void set(String name, boolean value) {
        set(name, HttpUrl.FRAGMENT_ENCODE_SET + value);
    }

    public void set(String name, int value) {
        set(name, HttpUrl.FRAGMENT_ENCODE_SET + value);
    }

    public void set(String name, double value) {
        set(name, HttpUrl.FRAGMENT_ENCODE_SET + value);
    }

    public Map<String, String> getAll() {
        return this.prefs;
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        String str = this.prefs.get(name.toLowerCase(Locale.ENGLISH));
        return str != null ? Boolean.parseBoolean(str) : defaultValue;
    }

    public boolean contains(String name) {
        return getString(name, null) != null;
    }

    public int getInteger(String name, int defaultValue) {
        String str = this.prefs.get(name.toLowerCase(Locale.ENGLISH));
        return str != null ? (int) Long.decode(str).longValue() : defaultValue;
    }

    public double getDouble(String name, double defaultValue) {
        String str = this.prefs.get(name.toLowerCase(Locale.ENGLISH));
        return str != null ? Double.valueOf(str).doubleValue() : defaultValue;
    }

    public String getString(String name, String defaultValue) {
        String str = this.prefs.get(name.toLowerCase(Locale.ENGLISH));
        return str != null ? str : defaultValue;
    }
}
