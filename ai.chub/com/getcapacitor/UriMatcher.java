package com.getcapacitor;

import android.net.Uri;
import androidx.webkit.ProxyConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class UriMatcher {
    private static final int EXACT = 0;
    private static final int MASK = 3;
    static final Pattern PATH_SPLIT_PATTERN = Pattern.compile("/");
    private static final int REST = 2;
    private static final int TEXT = 1;
    private ArrayList<UriMatcher> mChildren;
    private Object mCode;
    private String mText;
    private int mWhich;

    public UriMatcher(Object obj) {
        this.mCode = obj;
        this.mWhich = -1;
        this.mChildren = new ArrayList<>();
        this.mText = null;
    }

    private UriMatcher() {
        this.mCode = null;
        this.mWhich = -1;
        this.mChildren = new ArrayList<>();
        this.mText = null;
    }

    public void addURI(String str, String str2, String str3, Object obj) {
        String[] strArr;
        String str4;
        String str5 = str3;
        if (obj == null) {
            throw new IllegalArgumentException("Code can't be null");
        }
        if (str5 != null) {
            if (!str3.isEmpty() && str5.charAt(0) == '/') {
                str5 = str5.substring(1);
            }
            strArr = PATH_SPLIT_PATTERN.split(str5);
        } else {
            strArr = null;
        }
        int length = strArr != null ? strArr.length : 0;
        UriMatcher uriMatcher = this;
        int i = -2;
        while (i < length) {
            if (i == -2) {
                str4 = str;
            } else {
                str4 = i == -1 ? str2 : strArr[i];
            }
            ArrayList<UriMatcher> arrayList = uriMatcher.mChildren;
            int size = arrayList.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size) {
                    break;
                }
                UriMatcher uriMatcher2 = arrayList.get(i2);
                if (str4.equals(uriMatcher2.mText)) {
                    uriMatcher = uriMatcher2;
                    break;
                }
                i2++;
            }
            if (i2 == size) {
                UriMatcher uriMatcher3 = new UriMatcher();
                if (i == -1 && str4.contains(ProxyConfig.MATCH_ALL_SCHEMES)) {
                    uriMatcher3.mWhich = 3;
                } else if (str4.equals("**")) {
                    uriMatcher3.mWhich = 2;
                } else if (str4.equals(ProxyConfig.MATCH_ALL_SCHEMES)) {
                    uriMatcher3.mWhich = 1;
                } else {
                    uriMatcher3.mWhich = 0;
                }
                uriMatcher3.mText = str4;
                uriMatcher.mChildren.add(uriMatcher3);
                uriMatcher = uriMatcher3;
            }
            i++;
        }
        uriMatcher.mCode = obj;
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x0059, code lost:
        if (com.getcapacitor.util.HostMask.Parser.parse(r10.mText).matches(r5) == false) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0065, code lost:
        if (r10.mText.equals(r5) != false) goto L37;
     */
    /* JADX WARN: Removed duplicated region for block: B:40:0x006b A[LOOP:1: B:20:0x0039->B:40:0x006b, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:51:0x006e A[EDGE_INSN: B:51:0x006e->B:41:0x006e ?: BREAK  , SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public Object match(Uri uri) {
        String authority;
        List<String> pathSegments = uri.getPathSegments();
        int size = pathSegments.size();
        if (size == 0 && uri.getAuthority() == null) {
            return this.mCode;
        }
        UriMatcher uriMatcher = this;
        int i = -2;
        while (i < size) {
            if (i == -2) {
                authority = uri.getScheme();
            } else {
                authority = i == -1 ? uri.getAuthority() : pathSegments.get(i);
            }
            ArrayList<UriMatcher> arrayList = uriMatcher.mChildren;
            if (arrayList == null) {
                break;
            }
            int size2 = arrayList.size();
            UriMatcher uriMatcher2 = null;
            for (int i2 = 0; i2 < size2; i2++) {
                UriMatcher uriMatcher3 = arrayList.get(i2);
                int i3 = uriMatcher3.mWhich;
                if (i3 != 0) {
                    if (i3 != 1) {
                        if (i3 == 2) {
                            return uriMatcher3.mCode;
                        }
                        if (i3 == 3) {
                        }
                        if (uriMatcher2 == null) {
                            break;
                        }
                    }
                    uriMatcher2 = uriMatcher3;
                    if (uriMatcher2 == null) {
                    }
                }
            }
            uriMatcher = uriMatcher2;
            if (uriMatcher == null) {
                return null;
            }
            i++;
        }
        return uriMatcher.mCode;
    }
}
