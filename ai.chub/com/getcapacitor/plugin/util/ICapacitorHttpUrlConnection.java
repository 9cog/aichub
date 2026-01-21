package com.getcapacitor.plugin.util;

import java.io.IOException;
import java.io.InputStream;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface ICapacitorHttpUrlConnection {
    InputStream getErrorStream();

    String getHeaderField(String str);

    InputStream getInputStream() throws IOException;
}
