package net.openid.appauth.browser;

import java.util.Arrays;
import java.util.List;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class BrowserAllowList implements BrowserMatcher {
    private List<BrowserMatcher> mBrowserMatchers;

    public BrowserAllowList(BrowserMatcher... matchers) {
        this.mBrowserMatchers = Arrays.asList(matchers);
    }

    @Override // net.openid.appauth.browser.BrowserMatcher
    public boolean matches(BrowserDescriptor descriptor) {
        for (BrowserMatcher browserMatcher : this.mBrowserMatchers) {
            if (browserMatcher.matches(descriptor)) {
                return true;
            }
        }
        return false;
    }
}
