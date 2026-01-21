package net.openid.appauth.browser;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class AnyBrowserMatcher implements BrowserMatcher {
    public static final AnyBrowserMatcher INSTANCE = new AnyBrowserMatcher();

    @Override // net.openid.appauth.browser.BrowserMatcher
    public boolean matches(BrowserDescriptor descriptor) {
        return true;
    }

    private AnyBrowserMatcher() {
    }
}
