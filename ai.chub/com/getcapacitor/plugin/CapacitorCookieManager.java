package com.getcapacitor.plugin;

import com.getcapacitor.Bridge;
import com.getcapacitor.Logger;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CapacitorCookieManager extends CookieManager {
    private final String TAG;
    private final String localUrl;
    private final String serverUrl;
    private final android.webkit.CookieManager webkitCookieManager;

    public CapacitorCookieManager(Bridge bridge) {
        this(null, null, bridge);
    }

    public CapacitorCookieManager(CookieStore cookieStore, CookiePolicy cookiePolicy, Bridge bridge) {
        super(cookieStore, cookiePolicy);
        this.TAG = "CapacitorCookies";
        this.webkitCookieManager = android.webkit.CookieManager.getInstance();
        this.localUrl = bridge.getLocalUrl();
        this.serverUrl = bridge.getServerUrl();
    }

    public void removeSessionCookies() {
        this.webkitCookieManager.removeSessionCookies(null);
    }

    public String getSanitizedDomain(String str) throws URISyntaxException {
        if (str == null || str.isEmpty()) {
            str = this.serverUrl;
        }
        try {
            new URI(str);
        } catch (Exception unused) {
            str = this.localUrl;
            try {
                new URI(str);
            } catch (Exception e) {
                Logger.error("CapacitorCookies", "Failed to get sanitized URL.", e);
                throw e;
            }
        }
        return str;
    }

    private String getDomainFromCookieString(String str) throws URISyntaxException {
        String[] split = str.toLowerCase(Locale.ROOT).split("domain=");
        return getSanitizedDomain(split.length <= 1 ? null : split[1].split(";")[0].trim());
    }

    public String getCookieString(String str) {
        try {
            String sanitizedDomain = getSanitizedDomain(str);
            Logger.info("CapacitorCookies", "Getting cookies at: '" + sanitizedDomain + "'");
            return this.webkitCookieManager.getCookie(sanitizedDomain);
        } catch (Exception e) {
            Logger.error("CapacitorCookies", "Failed to get cookies at the given URL.", e);
            return null;
        }
    }

    public HttpCookie getCookie(String str, String str2) {
        HttpCookie[] cookies;
        for (HttpCookie httpCookie : getCookies(str)) {
            if (httpCookie.getName().equals(str2)) {
                return httpCookie;
            }
        }
        return null;
    }

    public HttpCookie[] getCookies(String str) {
        try {
            ArrayList arrayList = new ArrayList();
            String cookieString = getCookieString(str);
            if (cookieString != null) {
                for (String str2 : cookieString.split(";")) {
                    HttpCookie httpCookie = HttpCookie.parse(str2).get(0);
                    httpCookie.setValue(httpCookie.getValue());
                    arrayList.add(httpCookie);
                }
            }
            return (HttpCookie[]) arrayList.toArray(new HttpCookie[arrayList.size()]);
        } catch (Exception unused) {
            return new HttpCookie[0];
        }
    }

    public void setCookie(String str, String str2) {
        try {
            String sanitizedDomain = getSanitizedDomain(str);
            Logger.info("CapacitorCookies", "Setting cookie '" + str2 + "' at: '" + sanitizedDomain + "'");
            this.webkitCookieManager.setCookie(sanitizedDomain, str2);
            flush();
        } catch (Exception e) {
            Logger.error("CapacitorCookies", "Failed to set cookie.", e);
        }
    }

    public void setCookie(String str, String str2, String str3) {
        setCookie(str, str2 + "=" + str3);
    }

    public void setCookie(String str, String str2, String str3, String str4, String str5) {
        setCookie(str, str2 + "=" + str3 + "; expires=" + str4 + "; path=" + str5);
    }

    public void removeAllCookies() {
        this.webkitCookieManager.removeAllCookies(null);
        flush();
    }

    public void flush() {
        this.webkitCookieManager.flush();
    }

    @Override // java.net.CookieManager, java.net.CookieHandler
    public void put(URI uri, Map<String, List<String>> map) {
        if (uri == null || map == null) {
            return;
        }
        for (String str : map.keySet()) {
            if (str != null && (str.equalsIgnoreCase("Set-Cookie2") || str.equalsIgnoreCase("Set-Cookie"))) {
                for (String str2 : (List) Objects.requireNonNull(map.get(str))) {
                    try {
                        setCookie(uri.toString(), str2);
                        setCookie(getDomainFromCookieString(str2), str2);
                    } catch (Exception unused) {
                    }
                }
            }
        }
    }

    @Override // java.net.CookieManager, java.net.CookieHandler
    public Map<String, List<String>> get(URI uri, Map<String, List<String>> map) {
        if (uri == null || map == null) {
            throw new IllegalArgumentException("Argument is null");
        }
        String uri2 = uri.toString();
        HashMap hashMap = new HashMap();
        String cookieString = getCookieString(uri2);
        if (cookieString != null) {
            hashMap.put("Cookie", Collections.singletonList(cookieString));
        }
        return hashMap;
    }

    @Override // java.net.CookieManager
    public CookieStore getCookieStore() {
        throw new UnsupportedOperationException();
    }
}
