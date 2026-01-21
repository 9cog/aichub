package net.openid.appauth.browser;

import java.util.Collections;
import java.util.Set;
import net.openid.appauth.browser.Browsers;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class VersionedBrowserMatcher implements BrowserMatcher {
    private String mPackageName;
    private Set<String> mSignatureHashes;
    private boolean mUsingCustomTab;
    private VersionRange mVersionRange;
    public static final VersionedBrowserMatcher CHROME_CUSTOM_TAB = new VersionedBrowserMatcher(Browsers.Chrome.PACKAGE_NAME, Browsers.Chrome.SIGNATURE_SET, true, VersionRange.atLeast(Browsers.Chrome.MINIMUM_VERSION_FOR_CUSTOM_TAB));
    public static final VersionedBrowserMatcher CHROME_BROWSER = new VersionedBrowserMatcher(Browsers.Chrome.PACKAGE_NAME, Browsers.Chrome.SIGNATURE_SET, false, VersionRange.ANY_VERSION);
    public static final VersionedBrowserMatcher FIREFOX_CUSTOM_TAB = new VersionedBrowserMatcher(Browsers.Firefox.PACKAGE_NAME, Browsers.Firefox.SIGNATURE_SET, true, VersionRange.atLeast(Browsers.Firefox.MINIMUM_VERSION_FOR_CUSTOM_TAB));
    public static final VersionedBrowserMatcher FIREFOX_BROWSER = new VersionedBrowserMatcher(Browsers.Firefox.PACKAGE_NAME, Browsers.Firefox.SIGNATURE_SET, false, VersionRange.ANY_VERSION);
    public static final VersionedBrowserMatcher SAMSUNG_BROWSER = new VersionedBrowserMatcher(Browsers.SBrowser.PACKAGE_NAME, Browsers.SBrowser.SIGNATURE_SET, false, VersionRange.ANY_VERSION);
    public static final VersionedBrowserMatcher SAMSUNG_CUSTOM_TAB = new VersionedBrowserMatcher(Browsers.SBrowser.PACKAGE_NAME, Browsers.SBrowser.SIGNATURE_SET, true, VersionRange.atLeast(Browsers.SBrowser.MINIMUM_VERSION_FOR_CUSTOM_TAB));

    public VersionedBrowserMatcher(String packageName, String signatureHash, boolean usingCustomTab, VersionRange versionRange) {
        this(packageName, Collections.singleton(signatureHash), usingCustomTab, versionRange);
    }

    public VersionedBrowserMatcher(String packageName, Set<String> signatureHashes, boolean usingCustomTab, VersionRange versionRange) {
        this.mPackageName = packageName;
        this.mSignatureHashes = signatureHashes;
        this.mUsingCustomTab = usingCustomTab;
        this.mVersionRange = versionRange;
    }

    @Override // net.openid.appauth.browser.BrowserMatcher
    public boolean matches(BrowserDescriptor descriptor) {
        return this.mPackageName.equals(descriptor.packageName) && this.mUsingCustomTab == descriptor.useCustomTab.booleanValue() && this.mVersionRange.matches(descriptor.version) && this.mSignatureHashes.equals(descriptor.signatureHashes);
    }
}
