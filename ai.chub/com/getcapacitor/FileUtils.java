package com.getcapacitor;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import okhttp3.HttpUrl;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class FileUtils {
    private static String CapacitorFileScheme = "/_capacitor_file_";

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public enum Type {
        IMAGE("image");
        
        private String type;

        Type(String str) {
            this.type = str;
        }
    }

    public static String getPortablePath(Context context, String str, Uri uri) {
        String fileUrlForUri = getFileUrlForUri(context, uri);
        if (fileUrlForUri.startsWith("file://")) {
            fileUrlForUri = fileUrlForUri.replace("file://", HttpUrl.FRAGMENT_ENCODE_SET);
        }
        return str + Bridge.CAPACITOR_FILE_START + fileUrlForUri;
    }

    public static String getFileUrlForUri(Context context, Uri uri) {
        Uri uri2 = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String documentId = DocumentsContract.getDocumentId(uri);
                String[] split = documentId.split(":");
                if ("primary".equalsIgnoreCase(split[0])) {
                    return legacyPrimaryPath(split[1]);
                }
                int indexOf = documentId.indexOf(58, 1);
                String substring = documentId.substring(0, indexOf);
                String substring2 = documentId.substring(indexOf + 1);
                String pathToNonPrimaryVolume = getPathToNonPrimaryVolume(context, substring);
                if (pathToNonPrimaryVolume != null) {
                    String str = pathToNonPrimaryVolume + "/" + substring2;
                    File file = new File(str);
                    if (file.exists() && file.canRead()) {
                        return str;
                    }
                    return null;
                }
            } else if (isDownloadsDocument(uri)) {
                return getDataColumn(context, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(DocumentsContract.getDocumentId(uri)).longValue()), null, null);
            } else {
                if (isMediaDocument(uri)) {
                    String[] split2 = DocumentsContract.getDocumentId(uri).split(":");
                    String str2 = split2[0];
                    if ("image".equals(str2)) {
                        uri2 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(str2)) {
                        uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(str2)) {
                        uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    return getDataColumn(context, uri2, "_id=?", new String[]{split2[1]});
                }
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return isGooglePhotosUri(uri) ? uri.getLastPathSegment() : getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static String legacyPrimaryPath(String str) {
        return Environment.getExternalStorageDirectory() + "/" + str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String readFileFromAssets(AssetManager assetManager, String str) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(str)));
        try {
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    sb.append(readLine);
                    sb.append("\n");
                } else {
                    String sb2 = sb.toString();
                    bufferedReader.close();
                    return sb2;
                }
            }
        } catch (Throwable th) {
            try {
                bufferedReader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String readFileFromDisk(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        try {
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    sb.append(readLine);
                    sb.append("\n");
                } else {
                    String sb2 = sb.toString();
                    bufferedReader.close();
                    return sb2;
                }
            }
        } catch (Throwable th) {
            try {
                bufferedReader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private static String getDataColumn(Context context, Uri uri, String str, String[] strArr) {
        Cursor cursor = null;
        r7 = null;
        String string = null;
        Cursor cursor2 = null;
        try {
            try {
                Cursor query = context.getContentResolver().query(uri, new String[]{"_data"}, str, strArr, null);
                if (query != null) {
                    try {
                        if (query.moveToFirst()) {
                            string = query.getString(query.getColumnIndexOrThrow("_data"));
                        }
                    } catch (IllegalArgumentException unused) {
                        cursor = query;
                        String copyFilePath = getCopyFilePath(uri, context);
                        if (cursor != null) {
                            cursor.close();
                        }
                        return copyFilePath;
                    } catch (Throwable th) {
                        th = th;
                        cursor2 = query;
                        if (cursor2 != null) {
                            cursor2.close();
                        }
                        throw th;
                    }
                }
                if (query != null) {
                    query.close();
                }
                return string == null ? getCopyFilePath(uri, context) : string;
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IllegalArgumentException unused2) {
        }
    }

    private static String getCopyFilePath(Uri uri, Context context) {
        Cursor query = context.getContentResolver().query(uri, null, null, null, null);
        int columnIndex = query.getColumnIndex("_display_name");
        query.moveToFirst();
        File file = new File(context.getFilesDir(), query.getString(columnIndex));
        try {
            InputStream openInputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[Math.min(openInputStream.available(), 1048576)];
            while (true) {
                int read = openInputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                fileOutputStream.write(bArr, 0, read);
            }
            openInputStream.close();
            fileOutputStream.close();
            if (query != null) {
                query.close();
            }
            return file.getPath();
        } catch (Exception unused) {
            if (query != null) {
                query.close();
                return null;
            }
            return null;
        } catch (Throwable th) {
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private static String getPathToNonPrimaryVolume(Context context, String str) {
        String absolutePath;
        int indexOf;
        File[] externalCacheDirs = context.getExternalCacheDirs();
        if (externalCacheDirs != null) {
            for (File file : externalCacheDirs) {
                if (file != null && (absolutePath = file.getAbsolutePath()) != null && (indexOf = absolutePath.indexOf(str)) != -1) {
                    return absolutePath.substring(0, indexOf) + str;
                }
            }
            return null;
        }
        return null;
    }
}
