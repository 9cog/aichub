package ai.chub.plugins.updater;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.content.FileProvider;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.File;
import java.util.Objects;
@CapacitorPlugin(name = "SelfUpdater")
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class SelfUpdaterPlugin extends Plugin {
    private SelfUpdater implementation = new SelfUpdater();

    @PluginMethod
    public void installApk(PluginCall pluginCall) {
        Uri fromFile;
        String str = getContext().getFilesDir() + File.separator + ((String) Objects.requireNonNull(pluginCall.getString("filePath")));
        Log.i("FILE TO INSTALL: ", str);
        File file = new File(str);
        Intent intent = new Intent("android.intent.action.INSTALL_PACKAGE");
        if (Build.VERSION.SDK_INT >= 24) {
            fromFile = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
            intent.addFlags(1);
        } else {
            fromFile = Uri.fromFile(file);
        }
        Log.i("URI TO INSTALL: ", fromFile.getPath());
        intent.setDataAndType(fromFile, "application/vnd.android.package-archive");
        intent.addFlags(268435456);
        getActivity().startActivity(intent);
        pluginCall.resolve();
    }

    @PluginMethod
    public void echo(PluginCall pluginCall) {
        String string = pluginCall.getString("value");
        JSObject jSObject = new JSObject();
        jSObject.put("value", this.implementation.echo(string));
        pluginCall.resolve(jSObject);
    }
}
