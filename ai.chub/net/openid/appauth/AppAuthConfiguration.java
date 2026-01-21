package net.openid.appauth;

import net.openid.appauth.browser.AnyBrowserMatcher;
import net.openid.appauth.browser.BrowserMatcher;
import net.openid.appauth.connectivity.ConnectionBuilder;
import net.openid.appauth.connectivity.DefaultConnectionBuilder;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class AppAuthConfiguration {
    public static final AppAuthConfiguration DEFAULT = new Builder().build();
    private final BrowserMatcher mBrowserMatcher;
    private final ConnectionBuilder mConnectionBuilder;
    private final boolean mSkipIssuerHttpsCheck;

    private AppAuthConfiguration(BrowserMatcher browserMatcher, ConnectionBuilder connectionBuilder, Boolean skipIssuerHttpsCheck) {
        this.mBrowserMatcher = browserMatcher;
        this.mConnectionBuilder = connectionBuilder;
        this.mSkipIssuerHttpsCheck = skipIssuerHttpsCheck.booleanValue();
    }

    public BrowserMatcher getBrowserMatcher() {
        return this.mBrowserMatcher;
    }

    public ConnectionBuilder getConnectionBuilder() {
        return this.mConnectionBuilder;
    }

    public boolean getSkipIssuerHttpsCheck() {
        return this.mSkipIssuerHttpsCheck;
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class Builder {
        private BrowserMatcher mBrowserMatcher = AnyBrowserMatcher.INSTANCE;
        private ConnectionBuilder mConnectionBuilder = DefaultConnectionBuilder.INSTANCE;
        private boolean mSkipIssuerHttpsCheck;
        private boolean mSkipNonceVerification;

        public Builder setBrowserMatcher(BrowserMatcher browserMatcher) {
            Preconditions.checkNotNull(browserMatcher, "browserMatcher cannot be null");
            this.mBrowserMatcher = browserMatcher;
            return this;
        }

        public Builder setConnectionBuilder(ConnectionBuilder connectionBuilder) {
            Preconditions.checkNotNull(connectionBuilder, "connectionBuilder cannot be null");
            this.mConnectionBuilder = connectionBuilder;
            return this;
        }

        public Builder setSkipIssuerHttpsCheck(Boolean skipIssuerHttpsCheck) {
            this.mSkipIssuerHttpsCheck = skipIssuerHttpsCheck.booleanValue();
            return this;
        }

        public AppAuthConfiguration build() {
            return new AppAuthConfiguration(this.mBrowserMatcher, this.mConnectionBuilder, Boolean.valueOf(this.mSkipIssuerHttpsCheck));
        }
    }
}
