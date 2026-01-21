package com.capacitorjs.plugins.filesystem;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import androidx.browser.trusted.sharing.ShareTarget;
import androidx.core.app.NotificationCompat;
import com.capacitorjs.plugins.filesystem.exceptions.CopyFailedException;
import com.capacitorjs.plugins.filesystem.exceptions.DirectoryExistsException;
import com.capacitorjs.plugins.filesystem.exceptions.DirectoryNotFoundException;
import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import com.getcapacitor.plugin.util.CapacitorHttpUrlConnection;
import com.getcapacitor.plugin.util.HttpRequestHandler;
import com.google.android.gms.common.internal.ImagesContract;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import okhttp3.HttpUrl;
import org.json.JSONException;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class Filesystem {
    private Context context;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Filesystem(Context context) {
        this.context = context;
    }

    public String readFile(String str, String str2, Charset charset) throws IOException {
        InputStream inputStream = getInputStream(str, str2);
        if (charset != null) {
            return readFileAsString(inputStream, charset.name());
        }
        return readFileAsBase64EncodedData(inputStream);
    }

    public void saveFile(File file, String str, Charset charset, Boolean bool) throws IOException {
        if (charset != null) {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, bool.booleanValue()), charset));
            bufferedWriter.write(str);
            bufferedWriter.close();
            return;
        }
        if (str.contains(",")) {
            str = str.split(",")[1];
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file, bool.booleanValue());
        fileOutputStream.write(Base64.decode(str, 2));
        fileOutputStream.close();
    }

    public boolean deleteFile(String str, String str2) throws FileNotFoundException {
        File fileObject = getFileObject(str, str2);
        if (!fileObject.exists()) {
            throw new FileNotFoundException("File does not exist");
        }
        return fileObject.delete();
    }

    public boolean mkdir(String str, String str2, Boolean bool) throws DirectoryExistsException {
        File fileObject = getFileObject(str, str2);
        if (fileObject.exists()) {
            throw new DirectoryExistsException("Directory exists");
        }
        if (bool.booleanValue()) {
            return fileObject.mkdirs();
        }
        return fileObject.mkdir();
    }

    public File[] readdir(String str, String str2) throws DirectoryNotFoundException {
        File fileObject = getFileObject(str, str2);
        if (fileObject != null && fileObject.exists()) {
            return fileObject.listFiles();
        }
        throw new DirectoryNotFoundException("Directory does not exist");
    }

    public File copy(String str, String str2, String str3, String str4, boolean z) throws IOException, CopyFailedException {
        if (str4 == null) {
            str4 = str2;
        }
        File fileObject = getFileObject(str, str2);
        File fileObject2 = getFileObject(str3, str4);
        if (fileObject != null) {
            if (fileObject2 == null) {
                throw new CopyFailedException("to file is null");
            }
            if (fileObject2.equals(fileObject)) {
                return fileObject2;
            }
            if (!fileObject.exists()) {
                throw new CopyFailedException("The source object does not exist");
            }
            if (fileObject2.getParentFile().isFile()) {
                throw new CopyFailedException("The parent object of the destination is a file");
            }
            if (!fileObject2.getParentFile().exists()) {
                throw new CopyFailedException("The parent object of the destination does not exist");
            }
            if (fileObject2.isDirectory()) {
                throw new CopyFailedException("Cannot overwrite a directory");
            }
            fileObject2.delete();
            if (z) {
                if (!fileObject.renameTo(fileObject2)) {
                    throw new CopyFailedException("Unable to rename, unknown reason");
                }
            } else {
                copyRecursively(fileObject, fileObject2);
            }
            return fileObject2;
        }
        throw new CopyFailedException("from file is null");
    }

    public InputStream getInputStream(String str, String str2) throws IOException {
        if (str2 == null) {
            Uri parse = Uri.parse(str);
            if (parse.getScheme().equals("content")) {
                return this.context.getContentResolver().openInputStream(parse);
            }
            return new FileInputStream(new File(parse.getPath()));
        }
        File directory = getDirectory(str2);
        if (directory == null) {
            throw new IOException("Directory not found");
        }
        return new FileInputStream(new File(directory, str));
    }

    public String readFileAsString(InputStream inputStream, String str) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr);
            if (read != -1) {
                byteArrayOutputStream.write(bArr, 0, read);
            } else {
                return byteArrayOutputStream.toString(str);
            }
        }
    }

    public String readFileAsBase64EncodedData(InputStream inputStream) throws IOException {
        FileInputStream fileInputStream = (FileInputStream) inputStream;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[1024];
        while (true) {
            int read = fileInputStream.read(bArr);
            if (read != -1) {
                byteArrayOutputStream.write(bArr, 0, read);
            } else {
                fileInputStream.close();
                return Base64.encodeToString(byteArrayOutputStream.toByteArray(), 2);
            }
        }
    }

    public File getDirectory(String str) {
        Context context = this.context;
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1038134325:
                if (str.equals("EXTERNAL")) {
                    c = 0;
                    break;
                }
                break;
            case -564829544:
                if (str.equals("DOCUMENTS")) {
                    c = 1;
                    break;
                }
                break;
            case 2090922:
                if (str.equals("DATA")) {
                    c = 2;
                    break;
                }
                break;
            case 63879010:
                if (str.equals("CACHE")) {
                    c = 3;
                    break;
                }
                break;
            case 884191387:
                if (str.equals("LIBRARY")) {
                    c = 4;
                    break;
                }
                break;
            case 1013698023:
                if (str.equals("EXTERNAL_STORAGE")) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return context.getExternalFilesDir(null);
            case 1:
                return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            case 2:
            case 4:
                return context.getFilesDir();
            case 3:
                return context.getCacheDir();
            case 5:
                return Environment.getExternalStorageDirectory();
            default:
                return null;
        }
    }

    public File getFileObject(String str, String str2) {
        if (str2 == null) {
            Uri parse = Uri.parse(str);
            if (parse.getScheme() == null || parse.getScheme().equals("file")) {
                return new File(parse.getPath());
            }
        }
        File directory = getDirectory(str2);
        if (directory == null) {
            return null;
        }
        if (!directory.exists()) {
            directory.mkdir();
        }
        return new File(directory, str);
    }

    public Charset getEncoding(String str) {
        if (str == null) {
            return null;
        }
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case 3600241:
                if (str.equals("utf8")) {
                    c = 0;
                    break;
                }
                break;
            case 93106001:
                if (str.equals("ascii")) {
                    c = 1;
                    break;
                }
                break;
            case 111607308:
                if (str.equals("utf16")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return StandardCharsets.UTF_8;
            case 1:
                return StandardCharsets.US_ASCII;
            case 2:
                return StandardCharsets.UTF_16;
            default:
                return null;
        }
    }

    public void deleteRecursively(File file) throws IOException {
        if (file.isFile()) {
            file.delete();
            return;
        }
        for (File file2 : file.listFiles()) {
            deleteRecursively(file2);
        }
        file.delete();
    }

    public void copyRecursively(File file, File file2) throws IOException {
        String[] list;
        if (file.isDirectory()) {
            file2.mkdir();
            for (String str : file.list()) {
                copyRecursively(new File(file, str), new File(file2, str));
            }
            return;
        }
        if (!file2.getParentFile().exists()) {
            file2.getParentFile().mkdirs();
        }
        if (!file2.exists()) {
            file2.createNewFile();
        }
        FileChannel channel = new FileInputStream(file).getChannel();
        try {
            FileChannel channel2 = new FileOutputStream(file2).getChannel();
            channel2.transferFrom(channel, 0L, channel.size());
            if (channel2 != null) {
                channel2.close();
            }
            if (channel != null) {
                channel.close();
            }
        } catch (Throwable th) {
            if (channel != null) {
                try {
                    channel.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x00c9  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00ee A[EDGE_INSN: B:26:0x00ee->B:16:0x00ee ?: BREAK  , SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public JSObject downloadFile(PluginCall pluginCall, Bridge bridge, HttpRequestHandler.ProgressEmitter progressEmitter) throws IOException, URISyntaxException, JSONException {
        int parseInt;
        byte[] bArr;
        int i;
        int read;
        String string = pluginCall.getString(ImagesContract.URL, HttpUrl.FRAGMENT_ENCODE_SET);
        JSObject object = pluginCall.getObject("headers", new JSObject());
        JSObject object2 = pluginCall.getObject("params", new JSObject());
        Integer num = pluginCall.getInt("connectTimeout");
        Integer num2 = pluginCall.getInt("readTimeout");
        Boolean bool = pluginCall.getBoolean("disableRedirects");
        Boolean bool2 = pluginCall.getBoolean("shouldEncodeUrlParams", true);
        Boolean bool3 = pluginCall.getBoolean(NotificationCompat.CATEGORY_PROGRESS, false);
        String upperCase = pluginCall.getString("method", ShareTarget.METHOD_GET).toUpperCase(Locale.ROOT);
        String string2 = pluginCall.getString("path");
        String string3 = pluginCall.getString("directory", Environment.DIRECTORY_DOWNLOADS);
        URL url = new URL(string);
        File fileObject = getFileObject(string2, string3);
        CapacitorHttpUrlConnection build = new HttpRequestHandler.HttpURLConnectionBuilder().setUrl(url).setMethod(upperCase).setHeaders(object).setUrlParams(object2, bool2.booleanValue()).setConnectTimeout(num).setReadTimeout(num2).setDisableRedirects(bool).openConnection().build();
        build.setSSLSocketFactory(bridge);
        InputStream inputStream = build.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream(fileObject, false);
        String headerField = build.getHeaderField("content-length");
        if (headerField != null) {
            try {
                parseInt = Integer.parseInt(headerField);
            } catch (NumberFormatException unused) {
            }
            bArr = new byte[1024];
            long currentTimeMillis = System.currentTimeMillis();
            i = 0;
            while (true) {
                read = inputStream.read(bArr);
                if (read > 0) {
                    break;
                }
                fileOutputStream.write(bArr, 0, read);
                i += read;
                if (bool3.booleanValue() && progressEmitter != null) {
                    long currentTimeMillis2 = System.currentTimeMillis();
                    if (currentTimeMillis2 - currentTimeMillis > 100) {
                        progressEmitter.emit(Integer.valueOf(i), Integer.valueOf(parseInt));
                        currentTimeMillis = currentTimeMillis2;
                    }
                }
            }
            if (bool3.booleanValue() && progressEmitter != null) {
                progressEmitter.emit(Integer.valueOf(i), Integer.valueOf(parseInt));
            }
            inputStream.close();
            fileOutputStream.close();
            return new JSObject(fileObject) { // from class: com.capacitorjs.plugins.filesystem.Filesystem.1
                final /* synthetic */ File val$file;

                {
                    this.val$file = fileObject;
                    put("path", fileObject.getAbsolutePath());
                }
            };
        }
        parseInt = 0;
        bArr = new byte[1024];
        long currentTimeMillis3 = System.currentTimeMillis();
        i = 0;
        while (true) {
            read = inputStream.read(bArr);
            if (read > 0) {
            }
        }
        if (bool3.booleanValue()) {
            progressEmitter.emit(Integer.valueOf(i), Integer.valueOf(parseInt));
        }
        inputStream.close();
        fileOutputStream.close();
        return new JSObject(fileObject) { // from class: com.capacitorjs.plugins.filesystem.Filesystem.1
            final /* synthetic */ File val$file;

            {
                this.val$file = fileObject;
                put("path", fileObject.getAbsolutePath());
            }
        };
    }
}
