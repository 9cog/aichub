package net.openid.appauth.connectivity;

import android.net.Uri;
import java.io.IOException;
import java.net.HttpURLConnection;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface ConnectionBuilder {
    HttpURLConnection openConnection(Uri uri) throws IOException;
}
