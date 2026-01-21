package net.openid.appauth;

import android.text.TextUtils;
import java.util.Collection;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class Preconditions {
    public static <T> T checkNotNull(T reference) {
        reference.getClass();
        return reference;
    }

    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference != null) {
            return reference;
        }
        throw new NullPointerException(String.valueOf(errorMessage));
    }

    public static String checkNotEmpty(String str, Object errorMessage) {
        checkNotNull(str, errorMessage);
        checkArgument(!TextUtils.isEmpty(str), errorMessage);
        return str;
    }

    public static <T extends Collection<?>> T checkCollectionNotEmpty(T collection, Object errorMessage) {
        checkNotNull(collection, errorMessage);
        checkArgument(!collection.isEmpty(), errorMessage);
        return collection;
    }

    public static String checkNullOrNotEmpty(String str, Object errorMessage) {
        if (str != null) {
            checkNotEmpty(str, errorMessage);
        }
        return str;
    }

    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void checkArgument(boolean expression, String errorTemplate, Object... params) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(errorTemplate, params));
        }
    }

    private Preconditions() {
        throw new IllegalStateException("This type is not intended to be instantiated");
    }
}
