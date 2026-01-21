package net.openid.appauth;

import android.net.Uri;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
class AdditionalParamsProcessor {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static Set<String> builtInParams(String... params) {
        if (params == null || params.length == 0) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashSet(Arrays.asList(params)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Map<String, String> checkAdditionalParams(Map<String, String> params, Set<String> builtInParams) {
        if (params == null) {
            return Collections.emptyMap();
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Preconditions.checkNotNull(key, "additional parameter keys cannot be null");
            Preconditions.checkNotNull(value, "additional parameter values cannot be null");
            Preconditions.checkArgument(!builtInParams.contains(key), "Parameter %s is directly supported via the authorization request builder, use the builder method instead", key);
            linkedHashMap.put(key, value);
        }
        return Collections.unmodifiableMap(linkedHashMap);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Map<String, String> extractAdditionalParams(JSONObject json, Set<String> builtInParams) throws JSONException {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            if (!builtInParams.contains(next)) {
                linkedHashMap.put(next, json.get(next).toString());
            }
        }
        return linkedHashMap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Map<String, String> extractAdditionalParams(Uri uri, Set<String> builtInParams) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (String str : uri.getQueryParameterNames()) {
            if (!builtInParams.contains(str)) {
                linkedHashMap.put(str, uri.getQueryParameter(str));
            }
        }
        return linkedHashMap;
    }

    private AdditionalParamsProcessor() {
    }
}
