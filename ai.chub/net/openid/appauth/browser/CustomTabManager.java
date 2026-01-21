package net.openid.appauth.browser;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import androidx.browser.customtabs.CustomTabsCallback;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import net.openid.appauth.internal.Logger;
import net.openid.appauth.internal.UriUtil;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CustomTabManager {
    private static final long CLIENT_WAIT_TIME = 1;
    private final AtomicReference<CustomTabsClient> mClient = new AtomicReference<>();
    private final CountDownLatch mClientLatch = new CountDownLatch(1);
    private CustomTabsServiceConnection mConnection;
    private final WeakReference<Context> mContextRef;

    public CustomTabManager(Context context) {
        this.mContextRef = new WeakReference<>(context);
    }

    public synchronized void bind(String browserPackage) {
        if (this.mConnection != null) {
            return;
        }
        this.mConnection = new CustomTabsServiceConnection() { // from class: net.openid.appauth.browser.CustomTabManager.1
            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                Logger.debug("CustomTabsService is disconnected", new Object[0]);
                setClient(null);
            }

            @Override // androidx.browser.customtabs.CustomTabsServiceConnection
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                Logger.debug("CustomTabsService is connected", new Object[0]);
                customTabsClient.warmup(0L);
                setClient(customTabsClient);
            }

            private void setClient(CustomTabsClient client) {
                CustomTabManager.this.mClient.set(client);
                CustomTabManager.this.mClientLatch.countDown();
            }
        };
        Context context = this.mContextRef.get();
        if (context == null || !CustomTabsClient.bindCustomTabsService(context, browserPackage, this.mConnection)) {
            Logger.info("Unable to bind custom tabs service", new Object[0]);
            this.mClientLatch.countDown();
        }
    }

    public CustomTabsIntent.Builder createTabBuilder(Uri... possibleUris) {
        return new CustomTabsIntent.Builder(createSession(null, possibleUris));
    }

    public synchronized void dispose() {
        if (this.mConnection == null) {
            return;
        }
        Context context = this.mContextRef.get();
        if (context != null) {
            context.unbindService(this.mConnection);
        }
        this.mClient.set(null);
        Logger.debug("CustomTabsService is disconnected", new Object[0]);
    }

    public CustomTabsSession createSession(CustomTabsCallback callbacks, Uri... possibleUris) {
        CustomTabsClient client = getClient();
        if (client == null) {
            return null;
        }
        CustomTabsSession newSession = client.newSession(callbacks);
        if (newSession == null) {
            Logger.warn("Failed to create custom tabs session through custom tabs client", new Object[0]);
            return null;
        }
        if (possibleUris != null && possibleUris.length > 0) {
            newSession.mayLaunchUrl(possibleUris[0], null, UriUtil.toCustomTabUriBundle(possibleUris, 1));
        }
        return newSession;
    }

    public CustomTabsClient getClient() {
        try {
            this.mClientLatch.await(CLIENT_WAIT_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException unused) {
            Logger.info("Interrupted while waiting for browser connection", new Object[0]);
            this.mClientLatch.countDown();
        }
        return this.mClient.get();
    }
}
