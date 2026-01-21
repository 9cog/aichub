package org.apache.cordova;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface ICordovaCookieManager {
    void clearCookies();

    void flush();

    String getCookie(final String url);

    void setCookie(final String url, final String value);

    void setCookiesEnabled(boolean accept);
}
