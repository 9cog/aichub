package net.openid.appauth.browser;

import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class BrowserDescriptor {
    private static final String DIGEST_SHA_512 = "SHA-512";
    private static final int PRIME_HASH_FACTOR = 92821;
    public final String packageName;
    public final Set<String> signatureHashes;
    public final Boolean useCustomTab;
    public final String version;

    public BrowserDescriptor(PackageInfo packageInfo, boolean useCustomTab) {
        this(packageInfo.packageName, generateSignatureHashes(packageInfo.signatures), packageInfo.versionName, useCustomTab);
    }

    public BrowserDescriptor(String packageName, Set<String> signatureHashes, String version, boolean useCustomTab) {
        this.packageName = packageName;
        this.signatureHashes = signatureHashes;
        this.version = version;
        this.useCustomTab = Boolean.valueOf(useCustomTab);
    }

    public BrowserDescriptor changeUseCustomTab(boolean newUseCustomTabValue) {
        return new BrowserDescriptor(this.packageName, this.signatureHashes, this.version, newUseCustomTabValue);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof BrowserDescriptor)) {
            return false;
        }
        BrowserDescriptor browserDescriptor = (BrowserDescriptor) obj;
        return this.packageName.equals(browserDescriptor.packageName) && this.version.equals(browserDescriptor.version) && this.useCustomTab == browserDescriptor.useCustomTab && this.signatureHashes.equals(browserDescriptor.signatureHashes);
    }

    public int hashCode() {
        int hashCode = (((this.packageName.hashCode() * PRIME_HASH_FACTOR) + this.version.hashCode()) * PRIME_HASH_FACTOR) + (this.useCustomTab.booleanValue() ? 1 : 0);
        for (String str : this.signatureHashes) {
            hashCode = (hashCode * PRIME_HASH_FACTOR) + str.hashCode();
        }
        return hashCode;
    }

    public static String generateSignatureHash(Signature signature) {
        try {
            return Base64.encodeToString(MessageDigest.getInstance(DIGEST_SHA_512).digest(signature.toByteArray()), 10);
        } catch (NoSuchAlgorithmException unused) {
            throw new IllegalStateException("Platform does not supportSHA-512 hashing");
        }
    }

    public static Set<String> generateSignatureHashes(Signature[] signatures) {
        HashSet hashSet = new HashSet();
        for (Signature signature : signatures) {
            hashSet.add(generateSignatureHash(signature));
        }
        return hashSet;
    }
}
