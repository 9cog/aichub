package net.openid.appauth.internal;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.browser.customtabs.CustomTabsService;
import androidx.core.util.Pair;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.openid.appauth.Preconditions;
import okhttp3.HttpUrl;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class UriUtil {
    private UriUtil() {
        throw new IllegalStateException("This type is not intended to be instantiated");
    }

    public static Uri parseUriIfAvailable(String uri) {
        if (uri == null) {
            return null;
        }
        return Uri.parse(uri);
    }

    public static void appendQueryParameterIfNotNull(Uri.Builder uriBuilder, String paramName, Object value) {
        if (value == null || value.toString() == null) {
            return;
        }
        uriBuilder.appendQueryParameter(paramName, value.toString());
    }

    public static Long getLongQueryParameter(Uri uri, String param) {
        String queryParameter = uri.getQueryParameter(param);
        if (queryParameter != null) {
            return Long.valueOf(Long.parseLong(queryParameter));
        }
        return null;
    }

    public static List<Bundle> toCustomTabUriBundle(Uri[] uris, int startIndex) {
        Preconditions.checkArgument(startIndex >= 0, "startIndex must be positive");
        if (uris == null || uris.length <= startIndex) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(uris.length - startIndex);
        while (startIndex < uris.length) {
            if (uris[startIndex] == null) {
                Logger.warn("Null URI in possibleUris list - ignoring", new Object[0]);
            } else {
                Bundle bundle = new Bundle();
                bundle.putParcelable(CustomTabsService.KEY_URL, uris[startIndex]);
                arrayList.add(bundle);
            }
            startIndex++;
        }
        return arrayList;
    }

    public static String formUrlEncode(Map<String, String> parameters) {
        if (parameters == null) {
            return HttpUrl.FRAGMENT_ENCODE_SET;
        }
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            arrayList.add(entry.getKey() + "=" + formUrlEncodeValue(entry.getValue()));
        }
        return TextUtils.join("&", arrayList);
    }

    public static String formUrlEncodeValue(String value) {
        Preconditions.checkNotNull(value);
        try {
            return URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException unused) {
            throw new IllegalStateException("Unable to encode using UTF-8");
        }
    }

    public static List<Pair<String, String>> formUrlDecode(String encoded) {
        if (TextUtils.isEmpty(encoded)) {
            return Collections.emptyList();
        }
        String[] split = encoded.split("&");
        ArrayList arrayList = new ArrayList();
        for (String str : split) {
            String[] split2 = str.split("=");
            try {
                arrayList.add(Pair.create(split2[0], URLDecoder.decode(split2[1], "utf-8")));
            } catch (UnsupportedEncodingException e) {
                Logger.error("Unable to decode parameter, ignoring", e);
            }
        }
        return arrayList;
    }

    public static Map<String, String> formUrlDecodeUnique(String encoded) {
        List<Pair<String, String>> formUrlDecode = formUrlDecode(encoded);
        HashMap hashMap = new HashMap();
        for (Pair<String, String> pair : formUrlDecode) {
            hashMap.put(pair.first, pair.second);
        }
        return hashMap;
    }
}
