package com.byteowls.capacitor.oauth2;

import com.getcapacitor.JSObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public abstract class ConfigUtils {
    public static String getParamString(JSObject jSObject, String str) {
        return (String) getParam(String.class, jSObject, str);
    }

    public static <T> T getParam(Class<T> cls, JSObject jSObject, String str) {
        return (T) getParam(cls, jSObject, str, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x008f A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0090 A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static <T> T getParam(Class<T> cls, JSObject jSObject, String str, T t) {
        T t2;
        String deepestKey = getDeepestKey(str);
        if (deepestKey != null) {
            try {
                JSObject deepestObject = getDeepestObject(jSObject, str);
                if (deepestObject.has(deepestKey)) {
                    if (cls.isAssignableFrom(String.class)) {
                        t2 = (T) deepestObject.getString(deepestKey);
                    } else if (cls.isAssignableFrom(Boolean.class)) {
                        t2 = (T) Boolean.valueOf(deepestObject.optBoolean(deepestKey));
                    } else if (cls.isAssignableFrom(Double.class)) {
                        t2 = (T) Double.valueOf(deepestObject.getDouble(deepestKey));
                    } else if (cls.isAssignableFrom(Integer.class)) {
                        t2 = (T) Integer.valueOf(deepestObject.getInt(deepestKey));
                    } else if (cls.isAssignableFrom(Long.class)) {
                        t2 = (T) Long.valueOf(deepestObject.getLong(deepestKey));
                    } else if (cls.isAssignableFrom(Float.class)) {
                        t2 = (T) Float.valueOf(Double.valueOf(deepestObject.getDouble(deepestKey)).floatValue());
                    } else if (cls.isAssignableFrom(Integer.class)) {
                        t2 = (T) Integer.valueOf(deepestObject.getInt(deepestKey));
                    }
                    return t2 != null ? t : t2;
                }
                t2 = null;
                if (t2 != null) {
                }
            } catch (Exception unused) {
            }
        }
        return t;
    }

    public static Map<String, String> getParamMap(JSObject jSObject, String str) {
        HashMap hashMap = new HashMap();
        String deepestKey = getDeepestKey(str);
        if (deepestKey != null) {
            try {
                JSONObject jSONObject = getDeepestObject(jSObject, str).getJSONObject(deepestKey);
                Iterator<String> keys = jSONObject.keys();
                while (keys.hasNext()) {
                    String next = keys.next();
                    if (next != null && next.trim().length() > 0) {
                        try {
                            hashMap.put(next, jSONObject.getString(next));
                        } catch (JSONException unused) {
                        }
                    }
                }
            } catch (Exception unused2) {
            }
        }
        return hashMap;
    }

    public static String getDeepestKey(String str) {
        String[] split = str.split("\\.");
        if (split.length > 0) {
            return split[split.length - 1];
        }
        return null;
    }

    public static JSObject getDeepestObject(JSObject jSObject, String str) {
        String[] split = str.split("\\.");
        for (int i = 0; i < split.length - 1; i++) {
            jSObject = jSObject.getJSObject(split[i]);
        }
        return jSObject;
    }

    public static <T> T getOverwrittenAndroidParam(Class<T> cls, JSObject jSObject, String str) {
        T t = (T) getParam(cls, jSObject, str);
        T t2 = (T) getParam(cls, jSObject, "android." + str);
        return t2 != null ? t2 : t;
    }

    public static Map<String, String> getOverwrittenAndroidParamMap(JSObject jSObject, String str) {
        Map<String, String> paramMap = getParamMap(jSObject, str);
        Map<String, String> paramMap2 = getParamMap(jSObject, "android." + str);
        HashMap hashMap = new HashMap(paramMap);
        hashMap.putAll(paramMap2);
        return hashMap;
    }

    public static String getRandomString(int i) {
        char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        char[] cArr2 = new char[i];
        Random random = new Random();
        for (int i2 = 0; i2 < i; i2++) {
            cArr2[i2] = cArr[random.nextInt(62)];
        }
        return new String(cArr2);
    }

    public static String trimToNull(String str) {
        if (str == null || str.trim().length() != 0) {
            return str;
        }
        return null;
    }
}
