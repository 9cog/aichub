package net.openid.appauth;

import android.text.TextUtils;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
final class AsciiStringListUtil {
    private AsciiStringListUtil() {
        throw new IllegalStateException("This type is not intended to be instantiated");
    }

    public static String iterableToString(Iterable<String> strings) {
        if (strings == null) {
            return null;
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (String str : strings) {
            Preconditions.checkArgument(!TextUtils.isEmpty(str), "individual scopes cannot be null or empty");
            linkedHashSet.add(str);
        }
        if (linkedHashSet.isEmpty()) {
            return null;
        }
        return TextUtils.join(" ", linkedHashSet);
    }

    public static Set<String> stringToSet(String spaceDelimitedStr) {
        if (spaceDelimitedStr == null) {
            return null;
        }
        List asList = Arrays.asList(TextUtils.split(spaceDelimitedStr, " "));
        LinkedHashSet linkedHashSet = new LinkedHashSet(asList.size());
        linkedHashSet.addAll(asList);
        return linkedHashSet;
    }
}
