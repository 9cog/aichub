package com.getcapacitor.community.media;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
@CapacitorPlugin(name = "Media", permissions = {@Permission(alias = "publicStorage", strings = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}), @Permission(alias = "publicStorage13Plus", strings = {"android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO"})})
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class MediaPlugin extends Plugin {
    private static final int API_LEVEL_29 = 29;
    private static final int API_LEVEL_33 = 33;
    public static final String EC_ACCESS_DENIED = "accessDenied";
    public static final String EC_ARG_ERROR = "argumentError";
    public static final String EC_DOWNLOAD_ERROR = "downloadError";
    public static final String EC_FS_ERROR = "filesystemError";
    private static final String PERMISSION_DENIED_ERROR = "Unable to access media, user denied permission request";

    @PluginMethod
    public void getMedias(PluginCall pluginCall) {
        pluginCall.unimplemented();
    }

    @PluginMethod
    public void getMediaByIdentifier(PluginCall pluginCall) {
        pluginCall.unimplemented("No need to do this on Android -- the identifier is the file path.");
    }

    @PluginMethod
    public void getAlbums(PluginCall pluginCall) {
        Log.d("DEBUG LOG", "GET ALBUMS");
        if (isStoragePermissionGranted()) {
            Log.d("DEBUG LOG", "HAS PERMISSION");
            _getAlbums(pluginCall);
            return;
        }
        Log.d("DEBUG LOG", "NOT ALLOWED");
        this.bridge.saveCall(pluginCall);
        requestAllPermissions(pluginCall, "permissionCallback");
    }

    @PluginMethod
    public void savePhoto(PluginCall pluginCall) {
        Log.d("DEBUG LOG", "SAVE PHOTO TO ALBUM");
        if (isStoragePermissionGranted()) {
            Log.d("DEBUG LOG", "HAS PERMISSION");
            _saveMedia(pluginCall);
            return;
        }
        Log.d("DEBUG LOG", "NOT ALLOWED");
        this.bridge.saveCall(pluginCall);
        requestAllPermissions(pluginCall, "permissionCallback");
        Log.d("DEBUG LOG", "___SAVE PHOTO TO ALBUM AFTER PERMISSION REQUEST");
    }

    @PluginMethod
    public void saveVideo(PluginCall pluginCall) {
        Log.d("DEBUG LOG", "SAVE VIDEO TO ALBUM");
        if (isStoragePermissionGranted()) {
            Log.d("DEBUG LOG", "HAS PERMISSION");
            _saveMedia(pluginCall);
            return;
        }
        Log.d("DEBUG LOG", "NOT ALLOWED");
        this.bridge.saveCall(pluginCall);
        requestAllPermissions(pluginCall, "permissionCallback");
    }

    @PluginMethod
    public void createAlbum(PluginCall pluginCall) {
        Log.d("DEBUG LOG", "CREATE ALBUM");
        if (isStoragePermissionGranted()) {
            Log.d("DEBUG LOG", "HAS PERMISSION");
            _createAlbum(pluginCall);
            return;
        }
        Log.d("DEBUG LOG", "NOT ALLOWED");
        this.bridge.saveCall(pluginCall);
        requestAllPermissions(pluginCall, "permissionCallback");
    }

    @PermissionCallback
    private void permissionCallback(PluginCall pluginCall) {
        if (!isStoragePermissionGranted()) {
            Logger.debug(getLogTag(), "User denied storage permission");
            pluginCall.reject("Unable to complete operation; user denied permission request.", EC_ACCESS_DENIED);
            return;
        }
        String methodName = pluginCall.getMethodName();
        methodName.hashCode();
        char c = 65535;
        switch (methodName.hashCode()) {
            case -521524525:
                if (methodName.equals("createAlbum")) {
                    c = 0;
                    break;
                }
                break;
            case 169931445:
                if (methodName.equals("savePhoto")) {
                    c = 1;
                    break;
                }
                break;
            case 175491326:
                if (methodName.equals("saveVideo")) {
                    c = 2;
                    break;
                }
                break;
            case 280851162:
                if (methodName.equals("getAlbums")) {
                    c = 3;
                    break;
                }
                break;
            case 617984005:
                if (methodName.equals("getMedias")) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                _createAlbum(pluginCall);
                return;
            case 1:
            case 2:
                _saveMedia(pluginCall);
                return;
            case 3:
                _getAlbums(pluginCall);
                return;
            case 4:
                pluginCall.unimplemented();
                return;
            default:
                return;
        }
    }

    private boolean isGalleryMode() {
        return getConfig().getBoolean("androidGalleryMode", false);
    }

    private boolean isStoragePermissionGranted() {
        if (isGalleryMode()) {
            return getPermissionState(Build.VERSION.SDK_INT >= 33 ? "publicStorage13Plus" : "publicStorage") == PermissionState.GRANTED;
        }
        return true;
    }

    private void _getAlbums(PluginCall pluginCall) {
        Uri uri;
        Uri uri2;
        File[] listFiles;
        Log.d("DEBUG LOG", "___GET ALBUMS");
        JSObject jSObject = new JSObject();
        JSArray jSArray = new JSArray();
        HashSet hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        String[] strArr = {"bucket_display_name", "bucket_id", "_data"};
        if (isGalleryMode()) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else {
            uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        }
        Uri uri3 = uri;
        if (isGalleryMode()) {
            uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            uri2 = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
        }
        Cursor[] cursorArr = {getActivity().getContentResolver().query(uri3, strArr, null, null, null), getActivity().getContentResolver().query(uri2, strArr, null, null, null)};
        for (int i = 0; i < 2; i++) {
            Cursor cursor = cursorArr[i];
            while (cursor.moveToNext()) {
                String string = cursor.getString(cursor.getColumnIndex("bucket_display_name"));
                String string2 = cursor.getString(cursor.getColumnIndex("bucket_id"));
                if (!hashSet.contains(string2)) {
                    File file = new File(cursor.getString(cursor.getColumnIndex("_data")));
                    JSObject jSObject2 = new JSObject();
                    jSObject2.put("name", string);
                    jSObject2.put("identifier", file.getParent());
                    jSArray.put(jSObject2);
                    hashSet.add(string2);
                    hashSet2.add(file.getParent());
                }
            }
            cursor.close();
        }
        for (File file2 : new File(_getAlbumsPath()).listFiles()) {
            if (file2.isDirectory() && !hashSet2.contains(file2.getAbsolutePath())) {
                JSObject jSObject3 = new JSObject();
                jSObject3.put("name", file2.getName());
                jSObject3.put("identifier", file2.getAbsolutePath());
                hashSet2.add(file2.getAbsolutePath());
                jSArray.put(jSObject3);
            }
        }
        jSObject.put("albums", (Object) jSArray);
        Log.d("DEBUG LOG", String.valueOf(jSObject));
        Log.d("DEBUG LOG", "___GET ALBUMS FINISHED");
        pluginCall.resolve(jSObject);
    }

    @PluginMethod
    public void getAlbumsPath(PluginCall pluginCall) {
        JSObject jSObject = new JSObject();
        jSObject.put("path", _getAlbumsPath());
        pluginCall.resolve(jSObject);
    }

    private String _getAlbumsPath() {
        return getContext().getExternalMediaDirs()[0].getAbsolutePath();
    }

    private void _saveMedia(PluginCall pluginCall) {
        File createTempFile;
        Log.d("DEBUG LOG", "___SAVE MEDIA TO ALBUM");
        String string = pluginCall.getString("path");
        if (string == null) {
            pluginCall.reject("Input file path is required", EC_ARG_ERROR);
            return;
        }
        if (string.startsWith("data:")) {
            try {
                byte[] decode = Base64.decode(string.substring(string.indexOf(",") + 1), 0);
                String extensionFromMimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(string.split(";", 2)[0].split(":")[1]);
                if (extensionFromMimeType == null || extensionFromMimeType.isEmpty()) {
                    pluginCall.reject("Cannot identify media type to save image.", EC_ARG_ERROR);
                    return;
                }
                try {
                    createTempFile = File.createTempFile("tmp", "." + extensionFromMimeType, getContext().getCacheDir());
                    FileOutputStream fileOutputStream = new FileOutputStream(createTempFile);
                    fileOutputStream.write(decode);
                    fileOutputStream.close();
                } catch (IOException unused) {
                    pluginCall.reject("Temporary file creation from data URL failed", EC_FS_ERROR);
                    return;
                }
            } catch (Exception unused2) {
                pluginCall.reject("Data URL parsing failed.", EC_ARG_ERROR);
                return;
            }
        } else if (string.startsWith("http://") || string.startsWith("https://")) {
            try {
                Response execute = new OkHttpClient().newCall(new Request.Builder().url(string).build()).execute();
                if (!execute.isSuccessful() || execute.body() == null) {
                    throw new IOException();
                }
                String fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(string);
                if (fileExtensionFromUrl.isEmpty()) {
                    ResponseBody body = execute.body();
                    if (body == null) {
                        pluginCall.reject("Download failed", EC_DOWNLOAD_ERROR);
                        return;
                    }
                    MediaType contentType = body.contentType();
                    if (contentType == null) {
                        pluginCall.reject("Cannot identify media type to save image.", EC_ARG_ERROR);
                        return;
                    }
                    fileExtensionFromUrl = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType.type() + "/" + contentType.subtype());
                }
                if (fileExtensionFromUrl == null || fileExtensionFromUrl.isEmpty()) {
                    pluginCall.reject("Cannot identify media type to save image.", EC_ARG_ERROR);
                    return;
                }
                try {
                    File createTempFile2 = File.createTempFile("tmp", "." + fileExtensionFromUrl, getContext().getCacheDir());
                    FileOutputStream fileOutputStream2 = new FileOutputStream(createTempFile2);
                    fileOutputStream2.write(execute.body().bytes());
                    fileOutputStream2.close();
                    createTempFile = createTempFile2;
                } catch (IOException unused3) {
                    pluginCall.reject("Saving download to device failed.", EC_FS_ERROR);
                    return;
                }
            } catch (IOException unused4) {
                pluginCall.reject("Download failed", EC_DOWNLOAD_ERROR);
                return;
            }
        } else {
            createTempFile = new File(Uri.parse(string).getPath());
        }
        String string2 = pluginCall.getString("albumIdentifier");
        Log.d("SDK BUILD VERSION", String.valueOf(Build.VERSION.SDK_INT));
        if (string2 != null) {
            File file = new File(string2);
            if (!file.exists() || !file.isDirectory()) {
                pluginCall.reject("Album identifier does not exist, use getAlbums() to get", EC_ARG_ERROR);
                return;
            }
            Log.d("ENV LOG - ALBUM DIR", String.valueOf(file));
            try {
                String format = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                File copyFile = copyFile(createTempFile, file, pluginCall.getString("fileName", "IMG_" + format));
                scanPhoto(copyFile);
                JSObject jSObject = new JSObject();
                jSObject.put("filePath", copyFile.toString());
                pluginCall.resolve(jSObject);
                return;
            } catch (RuntimeException e) {
                pluginCall.reject("Error occurred: " + e, EC_ARG_ERROR);
                return;
            }
        }
        pluginCall.reject("Album identifier required", EC_ARG_ERROR);
    }

    private void _createAlbum(PluginCall pluginCall) {
        Log.d("DEBUG LOG", "___CREATE ALBUM");
        String string = pluginCall.getString("name");
        if (string == null) {
            pluginCall.reject("Album name must be given!", EC_ARG_ERROR);
            return;
        }
        File file = new File(_getAlbumsPath(), string);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Log.d("DEBUG LOG", "___ERROR ALBUM");
                pluginCall.reject("Cant create album", EC_FS_ERROR);
                return;
            }
            Log.d("DEBUG LOG", "___SUCCESS ALBUM CREATED");
            pluginCall.success();
            return;
        }
        Log.d("DEBUG LOG", "___ERROR ALBUM ALREADY EXISTS");
        pluginCall.reject("Album already exists", EC_FS_ERROR);
    }

    private File copyFile(File file, File file2, String str) {
        if (!file2.exists() && !file2.mkdir()) {
            throw new RuntimeException("Destination folder does not exist and cannot be created.");
        }
        String absolutePath = file.getAbsolutePath();
        String substring = absolutePath.substring(absolutePath.lastIndexOf("."));
        File file3 = new File(file2, str + substring);
        try {
            FileChannel channel = new FileInputStream(file).getChannel();
            try {
                FileChannel channel2 = new FileOutputStream(file3).getChannel();
                try {
                    try {
                        channel.transferTo(0L, channel.size(), channel2);
                        if (channel != null) {
                            try {
                                channel.close();
                            } catch (IOException e) {
                                Log.d("SaveImage", "Error closing input file channel: " + e.getMessage());
                            }
                        }
                        if (channel2 != null) {
                            try {
                                channel2.close();
                            } catch (IOException e2) {
                                Log.d("SaveImage", "Error closing output file channel: " + e2.getMessage());
                            }
                        }
                        return file3;
                    } catch (IOException e3) {
                        throw new RuntimeException("Error transfering file, error: " + e3.getMessage());
                    }
                } catch (Throwable th) {
                    if (channel != null) {
                        try {
                            channel.close();
                        } catch (IOException e4) {
                            Log.d("SaveImage", "Error closing input file channel: " + e4.getMessage());
                        }
                    }
                    if (channel2 != null) {
                        try {
                            channel2.close();
                        } catch (IOException e5) {
                            Log.d("SaveImage", "Error closing output file channel: " + e5.getMessage());
                        }
                    }
                    throw th;
                }
            } catch (FileNotFoundException e6) {
                throw new RuntimeException("Copy file not found: " + file3 + ", error: " + e6.getMessage());
            }
        } catch (FileNotFoundException e7) {
            throw new RuntimeException("Source file not found: " + file + ", error: " + e7.getMessage());
        }
    }

    private void scanPhoto(File file) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intent.setData(Uri.fromFile(file));
        this.bridge.getActivity().sendBroadcast(intent);
    }
}
