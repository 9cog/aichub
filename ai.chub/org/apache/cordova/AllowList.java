package org.apache.cordova;

import android.net.Uri;
import androidx.webkit.ProxyConfig;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class AllowList {
    public static final String TAG = "CordovaAllowList";
    private ArrayList<URLPattern> allowList = new ArrayList<>();

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    private static class URLPattern {
        public Pattern host;
        public Pattern path;
        public Integer port;
        public Pattern scheme;

        private String regexFromPattern(String pattern, boolean allowWildcards) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pattern.length(); i++) {
                char charAt = pattern.charAt(i);
                if (charAt == '*' && allowWildcards) {
                    sb.append(".");
                } else if ("\\.[]{}()^$?+|".indexOf(charAt) > -1) {
                    sb.append('\\');
                }
                sb.append(charAt);
            }
            return sb.toString();
        }

        /* JADX WARN: Removed duplicated region for block: B:11:0x0026 A[Catch: NumberFormatException -> 0x0088, TryCatch #0 {NumberFormatException -> 0x0088, blocks: (B:4:0x000c, B:7:0x0013, B:9:0x0020, B:11:0x0026, B:17:0x0058, B:20:0x005f, B:23:0x0070, B:26:0x0079, B:27:0x0085, B:21:0x006c, B:12:0x0029, B:14:0x0031, B:15:0x004c, B:8:0x001e), top: B:31:0x000c }] */
        /* JADX WARN: Removed duplicated region for block: B:12:0x0029 A[Catch: NumberFormatException -> 0x0088, TryCatch #0 {NumberFormatException -> 0x0088, blocks: (B:4:0x000c, B:7:0x0013, B:9:0x0020, B:11:0x0026, B:17:0x0058, B:20:0x005f, B:23:0x0070, B:26:0x0079, B:27:0x0085, B:21:0x006c, B:12:0x0029, B:14:0x0031, B:15:0x004c, B:8:0x001e), top: B:31:0x000c }] */
        /* JADX WARN: Removed duplicated region for block: B:23:0x0070 A[Catch: NumberFormatException -> 0x0088, TryCatch #0 {NumberFormatException -> 0x0088, blocks: (B:4:0x000c, B:7:0x0013, B:9:0x0020, B:11:0x0026, B:17:0x0058, B:20:0x005f, B:23:0x0070, B:26:0x0079, B:27:0x0085, B:21:0x006c, B:12:0x0029, B:14:0x0031, B:15:0x004c, B:8:0x001e), top: B:31:0x000c }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public URLPattern(String scheme, String host, String port, String path) throws MalformedURLException {
            if (scheme != null) {
                try {
                    if (!ProxyConfig.MATCH_ALL_SCHEMES.equals(scheme)) {
                        this.scheme = Pattern.compile(regexFromPattern(scheme, false), 2);
                        if (!ProxyConfig.MATCH_ALL_SCHEMES.equals(host)) {
                            this.host = null;
                        } else if (host.startsWith("*.")) {
                            this.host = Pattern.compile("([a-z0-9.-]*\\.)?" + regexFromPattern(host.substring(2), false), 2);
                        } else {
                            this.host = Pattern.compile(regexFromPattern(host, false), 2);
                        }
                        if (port != null && !ProxyConfig.MATCH_ALL_SCHEMES.equals(port)) {
                            this.port = Integer.valueOf(Integer.parseInt(port, 10));
                            if (path != null && !"/*".equals(path)) {
                                this.path = Pattern.compile(regexFromPattern(path, true));
                                return;
                            }
                            this.path = null;
                        }
                        this.port = null;
                        if (path != null) {
                            this.path = Pattern.compile(regexFromPattern(path, true));
                            return;
                        }
                        this.path = null;
                    }
                } catch (NumberFormatException unused) {
                    throw new MalformedURLException("Port must be a number");
                }
            }
            this.scheme = null;
            if (!ProxyConfig.MATCH_ALL_SCHEMES.equals(host)) {
            }
            if (port != null) {
                this.port = Integer.valueOf(Integer.parseInt(port, 10));
                if (path != null) {
                }
                this.path = null;
            }
            this.port = null;
            if (path != null) {
            }
            this.path = null;
        }

        public boolean matches(Uri uri) {
            try {
                Pattern pattern = this.scheme;
                if (pattern == null || pattern.matcher(uri.getScheme()).matches()) {
                    Pattern pattern2 = this.host;
                    if (pattern2 == null || pattern2.matcher(uri.getHost()).matches()) {
                        Integer num = this.port;
                        if (num == null || num.equals(Integer.valueOf(uri.getPort()))) {
                            Pattern pattern3 = this.path;
                            if (pattern3 != null) {
                                if (!pattern3.matcher(uri.getPath()).matches()) {
                                    return false;
                                }
                            }
                            return true;
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            } catch (Exception e) {
                LOG.d(AllowList.TAG, e.toString());
                return false;
            }
        }
    }

    public void addAllowListEntry(String origin, boolean subdomains) {
        String str = ProxyConfig.MATCH_ALL_SCHEMES;
        if (this.allowList != null) {
            try {
                if (origin.compareTo(ProxyConfig.MATCH_ALL_SCHEMES) == 0) {
                    LOG.d(TAG, "Unlimited access to network resources");
                    this.allowList = null;
                    return;
                }
                Matcher matcher = Pattern.compile("^((\\*|[A-Za-z-]+):(//)?)?(\\*|((\\*\\.)?[^*/:]+))?(:(\\d+))?(/.*)?").matcher(origin);
                if (matcher.matches()) {
                    String group = matcher.group(2);
                    String group2 = matcher.group(4);
                    if ((!"file".equals(group) && !"content".equals(group)) || group2 != null) {
                        str = group2;
                    }
                    String group3 = matcher.group(8);
                    String group4 = matcher.group(9);
                    if (group == null) {
                        this.allowList.add(new URLPattern("http", str, group3, group4));
                        this.allowList.add(new URLPattern("https", str, group3, group4));
                        return;
                    }
                    this.allowList.add(new URLPattern(group, str, group3, group4));
                }
            } catch (Exception unused) {
                LOG.d(TAG, "Failed to add origin %s", origin);
            }
        }
    }

    public boolean isUrlAllowListed(String uri) {
        if (this.allowList == null) {
            return true;
        }
        Uri parse = Uri.parse(uri);
        Iterator<URLPattern> it = this.allowList.iterator();
        while (it.hasNext()) {
            if (it.next().matches(parse)) {
                return true;
            }
        }
        return false;
    }
}
