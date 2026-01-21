package org.apache.cordova;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.util.Base64;
import android.webkit.MimeTypeMap;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CordovaResourceApi {
    private static final String[] LOCAL_FILE_PROJECTION = {"_data"};
    private static final String LOG_TAG = "CordovaResourceApi";
    public static final String PLUGIN_URI_SCHEME = "cdvplugin";
    public static final int URI_TYPE_ASSET = 1;
    public static final int URI_TYPE_CONTENT = 2;
    public static final int URI_TYPE_DATA = 4;
    public static final int URI_TYPE_FILE = 0;
    public static final int URI_TYPE_HTTP = 5;
    public static final int URI_TYPE_HTTPS = 6;
    public static final int URI_TYPE_PLUGIN = 7;
    public static final int URI_TYPE_RESOURCE = 3;
    public static final int URI_TYPE_UNKNOWN = -1;
    public static Thread jsThread;
    private final AssetManager assetManager;
    private final ContentResolver contentResolver;
    private final PluginManager pluginManager;
    private boolean threadCheckingEnabled = true;

    public CordovaResourceApi(Context context, PluginManager pluginManager) {
        this.contentResolver = context.getContentResolver();
        this.assetManager = context.getAssets();
        this.pluginManager = pluginManager;
    }

    public void setThreadCheckingEnabled(boolean value) {
        this.threadCheckingEnabled = value;
    }

    public boolean isThreadCheckingEnabled() {
        return this.threadCheckingEnabled;
    }

    public static int getUriType(Uri uri) {
        assertNonRelative(uri);
        String scheme = uri.getScheme();
        if ("content".equalsIgnoreCase(scheme)) {
            return 2;
        }
        if ("android.resource".equalsIgnoreCase(scheme)) {
            return 3;
        }
        if ("file".equalsIgnoreCase(scheme)) {
            return uri.getPath().startsWith("/android_asset/") ? 1 : 0;
        } else if ("data".equalsIgnoreCase(scheme)) {
            return 4;
        } else {
            if ("http".equalsIgnoreCase(scheme)) {
                return 5;
            }
            if ("https".equalsIgnoreCase(scheme)) {
                return 6;
            }
            return PLUGIN_URI_SCHEME.equalsIgnoreCase(scheme) ? 7 : -1;
        }
    }

    public Uri remapUri(Uri uri) {
        assertNonRelative(uri);
        Uri remapUri = this.pluginManager.remapUri(uri);
        return remapUri != null ? remapUri : uri;
    }

    public String remapPath(String path) {
        return remapUri(Uri.fromFile(new File(path))).getPath();
    }

    public File mapUriToFile(Uri uri) {
        assertBackgroundThread();
        int uriType = getUriType(uri);
        if (uriType != 0) {
            if (uriType != 2) {
                return null;
            }
            ContentResolver contentResolver = this.contentResolver;
            String[] strArr = LOCAL_FILE_PROJECTION;
            Cursor query = contentResolver.query(uri, strArr, null, null, null);
            if (query != null) {
                try {
                    int columnIndex = query.getColumnIndex(strArr[0]);
                    if (columnIndex != -1 && query.getCount() > 0) {
                        query.moveToFirst();
                        String string = query.getString(columnIndex);
                        if (string != null) {
                            return new File(string);
                        }
                    }
                    return null;
                } finally {
                    query.close();
                }
            }
            return null;
        }
        return new File(uri.getPath());
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0046 A[ExcHandler: IOException -> 0x0046, RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public String getMimeType(Uri uri) {
        switch (getUriType(uri)) {
            case 0:
            case 1:
                return getMimeTypeFromPath(uri.getPath());
            case 2:
            case 3:
                return this.contentResolver.getType(uri);
            case 4:
                return getDataUriMimeType(uri);
            case 5:
            case 6:
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                    httpURLConnection.setDoInput(false);
                    httpURLConnection.setRequestMethod("HEAD");
                    String headerField = httpURLConnection.getHeaderField("Content-Type");
                    return headerField != null ? headerField.split(";")[0] : headerField;
                } catch (IOException unused) {
                }
            default:
                return null;
        }
    }

    private String getMimeTypeFromPath(String path) {
        int lastIndexOf = path.lastIndexOf(46);
        if (lastIndexOf != -1) {
            path = path.substring(lastIndexOf + 1);
        }
        String lowerCase = path.toLowerCase(Locale.getDefault());
        return lowerCase.equals("3ga") ? "audio/3gpp" : lowerCase.equals("js") ? "text/javascript" : MimeTypeMap.getSingleton().getMimeTypeFromExtension(lowerCase);
    }

    public OpenForReadResult openForRead(Uri uri) throws IOException {
        return openForRead(uri, false);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public OpenForReadResult openForRead(Uri uri, boolean skipThreadCheck) throws IOException {
        InputStream open;
        long available;
        InputStream inputStream;
        if (!skipThreadCheck) {
            assertBackgroundThread();
        }
        switch (getUriType(uri)) {
            case 0:
                FileInputStream fileInputStream = new FileInputStream(uri.getPath());
                return new OpenForReadResult(uri, fileInputStream, getMimeTypeFromPath(uri.getPath()), fileInputStream.getChannel().size(), null);
            case 1:
                String substring = uri.getPath().substring(15);
                AssetFileDescriptor assetFileDescriptor = null;
                try {
                    assetFileDescriptor = this.assetManager.openFd(substring);
                    open = assetFileDescriptor.createInputStream();
                    available = assetFileDescriptor.getLength();
                } catch (FileNotFoundException unused) {
                    open = this.assetManager.open(substring);
                    available = open.available();
                }
                return new OpenForReadResult(uri, open, getMimeTypeFromPath(substring), available, assetFileDescriptor);
            case 2:
            case 3:
                String type = this.contentResolver.getType(uri);
                AssetFileDescriptor openAssetFileDescriptor = this.contentResolver.openAssetFileDescriptor(uri, "r");
                return new OpenForReadResult(uri, openAssetFileDescriptor.createInputStream(), type, openAssetFileDescriptor.getLength(), openAssetFileDescriptor);
            case 4:
                OpenForReadResult readDataUri = readDataUri(uri);
                if (readDataUri != null) {
                    return readDataUri;
                }
                break;
            case 5:
            case 6:
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                httpURLConnection.setRequestProperty("Accept-Encoding", "gzip");
                httpURLConnection.setDoInput(true);
                String headerField = httpURLConnection.getHeaderField("Content-Type");
                if (headerField != null) {
                    headerField = headerField.split(";")[0];
                }
                String str = headerField;
                int contentLength = httpURLConnection.getContentLength();
                if ("gzip".equals(httpURLConnection.getContentEncoding())) {
                    inputStream = new GZIPInputStream(httpURLConnection.getInputStream());
                } else {
                    inputStream = httpURLConnection.getInputStream();
                }
                return new OpenForReadResult(uri, inputStream, str, contentLength, null);
            case 7:
                CordovaPlugin plugin = this.pluginManager.getPlugin(uri.getHost());
                if (plugin == null) {
                    throw new FileNotFoundException("Invalid plugin ID in URI: " + uri);
                }
                return plugin.handleOpenForRead(uri);
        }
        throw new FileNotFoundException("URI not supported by CordovaResourceApi: " + uri);
    }

    public OutputStream openOutputStream(Uri uri) throws IOException {
        return openOutputStream(uri, false);
    }

    public OutputStream openOutputStream(Uri uri, boolean append) throws IOException {
        assertBackgroundThread();
        int uriType = getUriType(uri);
        if (uriType == 0) {
            File file = new File(uri.getPath());
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
            return new FileOutputStream(file, append);
        } else if (uriType == 2 || uriType == 3) {
            return this.contentResolver.openAssetFileDescriptor(uri, append ? "wa" : "w").createOutputStream();
        } else {
            throw new FileNotFoundException("URI not supported by CordovaResourceApi: " + uri);
        }
    }

    public HttpURLConnection createHttpConnection(Uri uri) throws IOException {
        assertBackgroundThread();
        return (HttpURLConnection) new URL(uri.toString()).openConnection();
    }

    public void copyResource(OpenForReadResult input, OutputStream outputStream) throws IOException {
        assertBackgroundThread();
        try {
            InputStream inputStream = input.inputStream;
            if ((inputStream instanceof FileInputStream) && (outputStream instanceof FileOutputStream)) {
                FileChannel channel = ((FileInputStream) input.inputStream).getChannel();
                FileChannel channel2 = ((FileOutputStream) outputStream).getChannel();
                long j = input.length;
                channel.position(input.assetFd != null ? input.assetFd.getStartOffset() : 0L);
                channel2.transferFrom(channel, 0L, j);
            } else {
                byte[] bArr = new byte[8192];
                while (true) {
                    int read = inputStream.read(bArr, 0, 8192);
                    if (read <= 0) {
                        break;
                    }
                    outputStream.write(bArr, 0, read);
                }
            }
        } finally {
            input.inputStream.close();
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public void copyResource(Uri sourceUri, OutputStream outputStream) throws IOException {
        copyResource(openForRead(sourceUri), outputStream);
    }

    public void copyResource(Uri sourceUri, Uri dstUri) throws IOException {
        copyResource(openForRead(sourceUri), openOutputStream(dstUri));
    }

    private void assertBackgroundThread() {
        if (this.threadCheckingEnabled) {
            Thread currentThread = Thread.currentThread();
            if (currentThread == Looper.getMainLooper().getThread()) {
                throw new IllegalStateException("Do not perform IO operations on the UI thread. Use CordovaInterface.getThreadPool() instead.");
            }
            if (currentThread == jsThread) {
                throw new IllegalStateException("Tried to perform an IO operation on the WebCore thread. Use CordovaInterface.getThreadPool() instead.");
            }
        }
    }

    private String getDataUriMimeType(Uri uri) {
        String schemeSpecificPart = uri.getSchemeSpecificPart();
        int indexOf = schemeSpecificPart.indexOf(44);
        if (indexOf == -1) {
            return null;
        }
        String[] split = schemeSpecificPart.substring(0, indexOf).split(";");
        if (split.length > 0) {
            return split[0];
        }
        return null;
    }

    private OpenForReadResult readDataUri(Uri uri) {
        byte[] bytes;
        String schemeSpecificPart = uri.getSchemeSpecificPart();
        int indexOf = schemeSpecificPart.indexOf(44);
        if (indexOf == -1) {
            return null;
        }
        String[] split = schemeSpecificPart.substring(0, indexOf).split(";");
        String str = split.length > 0 ? split[0] : null;
        boolean z = false;
        for (int i = 1; i < split.length; i++) {
            if ("base64".equalsIgnoreCase(split[i])) {
                z = true;
            }
        }
        String substring = schemeSpecificPart.substring(indexOf + 1);
        if (z) {
            bytes = Base64.decode(substring, 0);
        } else {
            try {
                bytes = substring.getBytes("UTF-8");
            } catch (UnsupportedEncodingException unused) {
                bytes = substring.getBytes();
            }
        }
        return new OpenForReadResult(uri, new ByteArrayInputStream(bytes), str, bytes.length, null);
    }

    private static void assertNonRelative(Uri uri) {
        if (!uri.isAbsolute()) {
            throw new IllegalArgumentException("Relative URIs are not supported.");
        }
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class OpenForReadResult {
        public final AssetFileDescriptor assetFd;
        public final InputStream inputStream;
        public final long length;
        public final String mimeType;
        public final Uri uri;

        public OpenForReadResult(Uri uri, InputStream inputStream, String mimeType, long length, AssetFileDescriptor assetFd) {
            this.uri = uri;
            this.inputStream = inputStream;
            this.mimeType = mimeType;
            this.length = length;
            this.assetFd = assetFd;
        }
    }
}
