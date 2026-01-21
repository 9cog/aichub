package com.getcapacitor;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import kotlin.UByte;
import okhttp3.HttpUrl;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class AppUUID {
    private static final String KEY = "CapacitorAppUUID";

    public static String getAppUUID(AppCompatActivity appCompatActivity) throws Exception {
        assertAppUUID(appCompatActivity);
        return readUUID(appCompatActivity);
    }

    public static void regenerateAppUUID(AppCompatActivity appCompatActivity) throws Exception {
        try {
            writeUUID(appCompatActivity, generateUUID());
        } catch (NoSuchAlgorithmException unused) {
            throw new Exception("Capacitor App UUID could not be generated.");
        }
    }

    private static void assertAppUUID(AppCompatActivity appCompatActivity) throws Exception {
        if (readUUID(appCompatActivity).equals(HttpUrl.FRAGMENT_ENCODE_SET)) {
            regenerateAppUUID(appCompatActivity);
        }
    }

    private static String generateUUID() throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        return bytesToHex(messageDigest.digest());
    }

    private static String readUUID(AppCompatActivity appCompatActivity) {
        return appCompatActivity.getPreferences(0).getString(KEY, HttpUrl.FRAGMENT_ENCODE_SET);
    }

    private static void writeUUID(AppCompatActivity appCompatActivity, String str) {
        SharedPreferences.Editor edit = appCompatActivity.getPreferences(0).edit();
        edit.putString(KEY, str);
        edit.apply();
    }

    private static String bytesToHex(byte[] bArr) {
        byte[] bytes = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
        byte[] bArr2 = new byte[bArr.length * 2];
        for (int i = 0; i < bArr.length; i++) {
            int i2 = bArr[i] & UByte.MAX_VALUE;
            int i3 = i * 2;
            bArr2[i3] = bytes[i2 >>> 4];
            bArr2[i3 + 1] = bytes[i2 & 15];
        }
        return new String(bArr2, StandardCharsets.UTF_8);
    }
}
