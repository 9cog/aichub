package com.getcapacitor;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.getcapacitor.util.PermissionHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONException;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class Plugin {
    private static final String BUNDLE_PERSISTED_OPTIONS_JSON_KEY = "_json";
    protected Bridge bridge;
    protected PluginHandle handle;
    private String lastPluginCallId;
    @Deprecated
    protected PluginCall savedLastCall;
    private final Map<String, ActivityResultLauncher<Intent>> activityLaunchers = new HashMap();
    private final Map<String, ActivityResultLauncher<String[]>> permissionLaunchers = new HashMap();
    private final Map<String, List<PluginCall>> eventListeners = new HashMap();
    private final Map<String, List<JSObject>> retainedEventArguments = new HashMap();

    /* JADX INFO: Access modifiers changed from: protected */
    @Deprecated
    public void handleOnActivityResult(int i, int i2, Intent intent) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleOnConfigurationChanged(Configuration configuration) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleOnDestroy() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleOnNewIntent(Intent intent) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleOnPause() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleOnRestart() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleOnResume() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleOnStart() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleOnStop() {
    }

    public void load() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void restoreState(Bundle bundle) {
    }

    public Boolean shouldOverrideLoad(Uri uri) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initializeActivityLaunchers() {
        ArrayList<Method> arrayList = new ArrayList();
        for (Class<?> cls = getClass(); !cls.getName().equals(Object.class.getName()); cls = cls.getSuperclass()) {
            arrayList.addAll(Arrays.asList(cls.getDeclaredMethods()));
        }
        for (final Method method : arrayList) {
            if (method.isAnnotationPresent(ActivityCallback.class)) {
                this.activityLaunchers.put(method.getName(), this.bridge.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.getcapacitor.Plugin$$ExternalSyntheticLambda0
                    @Override // androidx.activity.result.ActivityResultCallback
                    public final void onActivityResult(Object obj) {
                        Plugin.this.lambda$initializeActivityLaunchers$0(method, (ActivityResult) obj);
                    }
                }));
            } else if (method.isAnnotationPresent(PermissionCallback.class)) {
                this.permissionLaunchers.put(method.getName(), this.bridge.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback() { // from class: com.getcapacitor.Plugin$$ExternalSyntheticLambda1
                    @Override // androidx.activity.result.ActivityResultCallback
                    public final void onActivityResult(Object obj) {
                        Plugin.this.lambda$initializeActivityLaunchers$1(method, (Map) obj);
                    }
                }));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: triggerPermissionCallback */
    public void lambda$initializeActivityLaunchers$1(Method method, Map<String, Boolean> map) {
        PluginCall permissionCall = this.bridge.getPermissionCall(this.handle.getId());
        if (this.bridge.validatePermissions(this, permissionCall, map)) {
            try {
                method.setAccessible(true);
                method.invoke(this, permissionCall);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: triggerActivityCallback */
    public void lambda$initializeActivityLaunchers$0(Method method, ActivityResult activityResult) {
        PluginCall savedCall = this.bridge.getSavedCall(this.lastPluginCallId);
        if (savedCall == null) {
            savedCall = this.bridge.getPluginCallForLastActivity();
        }
        try {
            method.setAccessible(true);
            method.invoke(this, savedCall, activityResult);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void startActivityForResult(PluginCall pluginCall, Intent intent, String str) {
        ActivityResultLauncher<Intent> activityLauncherOrReject = getActivityLauncherOrReject(pluginCall, str);
        if (activityLauncherOrReject == null) {
            return;
        }
        this.bridge.setPluginCallForLastActivity(pluginCall);
        this.lastPluginCallId = pluginCall.getCallbackId();
        this.bridge.saveCall(pluginCall);
        activityLauncherOrReject.launch(intent);
    }

    private void permissionActivityResult(PluginCall pluginCall, String[] strArr, String str) {
        ActivityResultLauncher<String[]> permissionLauncherOrReject = getPermissionLauncherOrReject(pluginCall, str);
        if (permissionLauncherOrReject == null) {
            return;
        }
        this.bridge.savePermissionCall(pluginCall);
        permissionLauncherOrReject.launch(strArr);
    }

    public Context getContext() {
        return this.bridge.getContext();
    }

    public AppCompatActivity getActivity() {
        return this.bridge.getActivity();
    }

    public void setBridge(Bridge bridge) {
        this.bridge = bridge;
    }

    public Bridge getBridge() {
        return this.bridge;
    }

    public void setPluginHandle(PluginHandle pluginHandle) {
        this.handle = pluginHandle;
    }

    public PluginHandle getPluginHandle() {
        return this.handle;
    }

    public String getAppId() {
        return getContext().getPackageName();
    }

    @Deprecated
    public void saveCall(PluginCall pluginCall) {
        this.savedLastCall = pluginCall;
    }

    @Deprecated
    public void freeSavedCall() {
        this.savedLastCall.release(this.bridge);
        this.savedLastCall = null;
    }

    @Deprecated
    public PluginCall getSavedCall() {
        return this.savedLastCall;
    }

    public PluginConfig getConfig() {
        return this.bridge.getConfig().getPluginConfiguration(this.handle.getId());
    }

    @Deprecated
    public Object getConfigValue(String str) {
        try {
            return getConfig().getConfigJSON().get(str);
        } catch (JSONException unused) {
            return null;
        }
    }

    @Deprecated
    public boolean hasDefinedPermissions(String[] strArr) {
        for (String str : strArr) {
            if (!PermissionHelper.hasDefinedPermission(getContext(), str)) {
                return false;
            }
        }
        return true;
    }

    @Deprecated
    public boolean hasDefinedRequiredPermissions() {
        CapacitorPlugin pluginAnnotation = this.handle.getPluginAnnotation();
        if (pluginAnnotation == null) {
            return hasDefinedPermissions(this.handle.getLegacyPluginAnnotation().permissions());
        }
        for (Permission permission : pluginAnnotation.permissions()) {
            for (String str : permission.strings()) {
                if (!PermissionHelper.hasDefinedPermission(getContext(), str)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isPermissionDeclared(String str) {
        Permission[] permissions;
        CapacitorPlugin pluginAnnotation = this.handle.getPluginAnnotation();
        if (pluginAnnotation != null) {
            for (Permission permission : pluginAnnotation.permissions()) {
                if (str.equalsIgnoreCase(permission.alias())) {
                    boolean z = true;
                    for (String str2 : permission.strings()) {
                        z = z && PermissionHelper.hasDefinedPermission(getContext(), str2);
                    }
                    return z;
                }
            }
        }
        Logger.error(String.format("isPermissionDeclared: No alias defined for %s or missing @CapacitorPlugin annotation.", str));
        return false;
    }

    @Deprecated
    public boolean hasPermission(String str) {
        return ActivityCompat.checkSelfPermission(getContext(), str) == 0;
    }

    @Deprecated
    public boolean hasRequiredPermissions() {
        CapacitorPlugin pluginAnnotation = this.handle.getPluginAnnotation();
        if (pluginAnnotation == null) {
            for (String str : this.handle.getLegacyPluginAnnotation().permissions()) {
                if (ActivityCompat.checkSelfPermission(getContext(), str) != 0) {
                    return false;
                }
            }
            return true;
        }
        for (Permission permission : pluginAnnotation.permissions()) {
            for (String str2 : permission.strings()) {
                if (ActivityCompat.checkSelfPermission(getContext(), str2) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void requestAllPermissions(PluginCall pluginCall, String str) {
        CapacitorPlugin pluginAnnotation = this.handle.getPluginAnnotation();
        if (pluginAnnotation != null) {
            HashSet hashSet = new HashSet();
            for (Permission permission : pluginAnnotation.permissions()) {
                hashSet.addAll(Arrays.asList(permission.strings()));
            }
            permissionActivityResult(pluginCall, (String[]) hashSet.toArray(new String[0]), str);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void requestPermissionForAlias(String str, PluginCall pluginCall, String str2) {
        requestPermissionForAliases(new String[]{str}, pluginCall, str2);
    }

    protected void requestPermissionForAliases(String[] strArr, PluginCall pluginCall, String str) {
        if (strArr.length == 0) {
            Logger.error("No permission alias was provided");
            return;
        }
        String[] permissionStringsForAliases = getPermissionStringsForAliases(strArr);
        if (permissionStringsForAliases.length > 0) {
            permissionActivityResult(pluginCall, permissionStringsForAliases, str);
        }
    }

    private String[] getPermissionStringsForAliases(String[] strArr) {
        Permission[] permissions;
        CapacitorPlugin pluginAnnotation = this.handle.getPluginAnnotation();
        HashSet hashSet = new HashSet();
        for (Permission permission : pluginAnnotation.permissions()) {
            if (Arrays.asList(strArr).contains(permission.alias())) {
                hashSet.addAll(Arrays.asList(permission.strings()));
            }
        }
        return (String[]) hashSet.toArray(new String[0]);
    }

    private ActivityResultLauncher<Intent> getActivityLauncherOrReject(PluginCall pluginCall, String str) {
        ActivityResultLauncher<Intent> activityResultLauncher = this.activityLaunchers.get(str);
        if (activityResultLauncher == null) {
            String format = String.format(Locale.US, "There is no ActivityCallback method registered for the name: %s. Please define a callback method annotated with @ActivityCallback that receives arguments: (PluginCall, ActivityResult)", str);
            Logger.error(format);
            pluginCall.reject(format);
            return null;
        }
        return activityResultLauncher;
    }

    private ActivityResultLauncher<String[]> getPermissionLauncherOrReject(PluginCall pluginCall, String str) {
        ActivityResultLauncher<String[]> activityResultLauncher = this.permissionLaunchers.get(str);
        if (activityResultLauncher == null) {
            String format = String.format(Locale.US, "There is no PermissionCallback method registered for the name: %s. Please define a callback method annotated with @PermissionCallback that receives arguments: (PluginCall)", str);
            Logger.error(format);
            pluginCall.reject(format);
            return null;
        }
        return activityResultLauncher;
    }

    @Deprecated
    public void pluginRequestAllPermissions() {
        NativePlugin legacyPluginAnnotation = this.handle.getLegacyPluginAnnotation();
        ActivityCompat.requestPermissions(getActivity(), legacyPluginAnnotation.permissions(), legacyPluginAnnotation.permissionRequestCode());
    }

    @Deprecated
    public void pluginRequestPermission(String str, int i) {
        ActivityCompat.requestPermissions(getActivity(), new String[]{str}, i);
    }

    @Deprecated
    public void pluginRequestPermissions(String[] strArr, int i) {
        ActivityCompat.requestPermissions(getActivity(), strArr, i);
    }

    public PermissionState getPermissionState(String str) {
        return getPermissionStates().get(str);
    }

    public Map<String, PermissionState> getPermissionStates() {
        return this.bridge.getPermissionStates(this);
    }

    private void addEventListener(String str, PluginCall pluginCall) {
        List<PluginCall> list = this.eventListeners.get(str);
        if (list == null || list.isEmpty()) {
            ArrayList arrayList = new ArrayList();
            this.eventListeners.put(str, arrayList);
            arrayList.add(pluginCall);
            sendRetainedArgumentsForEvent(str);
            return;
        }
        list.add(pluginCall);
    }

    private void removeEventListener(String str, PluginCall pluginCall) {
        List<PluginCall> list = this.eventListeners.get(str);
        if (list == null) {
            return;
        }
        list.remove(pluginCall);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyListeners(String str, JSObject jSObject, boolean z) {
        String logTag = getLogTag();
        Logger.verbose(logTag, "Notifying listeners for event " + str);
        List<PluginCall> list = this.eventListeners.get(str);
        if (list == null || list.isEmpty()) {
            String logTag2 = getLogTag();
            Logger.debug(logTag2, "No listeners found for event " + str);
            if (z) {
                List<JSObject> list2 = this.retainedEventArguments.get(str);
                if (list2 == null) {
                    list2 = new ArrayList<>();
                }
                list2.add(jSObject);
                this.retainedEventArguments.put(str, list2);
                return;
            }
            return;
        }
        Iterator it = new CopyOnWriteArrayList(list).iterator();
        while (it.hasNext()) {
            ((PluginCall) it.next()).resolve(jSObject);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyListeners(String str, JSObject jSObject) {
        notifyListeners(str, jSObject, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasListeners(String str) {
        List<PluginCall> list = this.eventListeners.get(str);
        if (list == null) {
            return false;
        }
        return !list.isEmpty();
    }

    private void sendRetainedArgumentsForEvent(String str) {
        List<JSObject> list = this.retainedEventArguments.get(str);
        if (list == null) {
            return;
        }
        this.retainedEventArguments.remove(str);
        for (JSObject jSObject : list) {
            notifyListeners(str, jSObject);
        }
    }

    @PluginMethod(returnType = "none")
    public void addListener(PluginCall pluginCall) {
        String string = pluginCall.getString("eventName");
        pluginCall.setKeepAlive(true);
        addEventListener(string, pluginCall);
    }

    @PluginMethod(returnType = "none")
    public void removeListener(PluginCall pluginCall) {
        String string = pluginCall.getString("eventName");
        PluginCall savedCall = this.bridge.getSavedCall(pluginCall.getString("callbackId"));
        if (savedCall != null) {
            removeEventListener(string, savedCall);
            this.bridge.releaseCall(savedCall);
        }
    }

    @PluginMethod(returnType = PluginMethod.RETURN_PROMISE)
    public void removeAllListeners(PluginCall pluginCall) {
        this.eventListeners.clear();
        pluginCall.resolve();
    }

    @PluginMethod
    @PermissionCallback
    public void checkPermissions(PluginCall pluginCall) {
        Map<String, PermissionState> permissionStates = getPermissionStates();
        if (permissionStates.size() == 0) {
            pluginCall.resolve();
            return;
        }
        JSObject jSObject = new JSObject();
        for (Map.Entry<String, PermissionState> entry : permissionStates.entrySet()) {
            jSObject.put(entry.getKey(), (Object) entry.getValue());
        }
        pluginCall.resolve(jSObject);
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x006d  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00c1  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x00e4  */
    @PluginMethod
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void requestPermissions(PluginCall pluginCall) {
        List list;
        HashSet hashSet;
        Permission[] permissions;
        Permission[] permissions2;
        CapacitorPlugin pluginAnnotation = this.handle.getPluginAnnotation();
        if (pluginAnnotation == null) {
            handleLegacyPermission(pluginCall);
            return;
        }
        HashSet<String> hashSet2 = new HashSet();
        JSArray array = pluginCall.getArray("permissions");
        String[] strArr = null;
        if (array != null) {
            try {
                list = array.toList();
            } catch (JSONException unused) {
            }
            hashSet = new HashSet();
            if (list != null || list.isEmpty()) {
                for (Permission permission : pluginAnnotation.permissions()) {
                    if (permission.strings().length == 0 || (permission.strings().length == 1 && permission.strings()[0].isEmpty())) {
                        if (!permission.alias().isEmpty()) {
                            hashSet2.add(permission.alias());
                        }
                    } else {
                        hashSet.add(permission.alias());
                    }
                }
                strArr = (String[]) hashSet.toArray(new String[0]);
            } else {
                for (Permission permission2 : pluginAnnotation.permissions()) {
                    if (list.contains(permission2.alias())) {
                        hashSet.add(permission2.alias());
                    }
                }
                if (hashSet.isEmpty()) {
                    pluginCall.reject("No valid permission alias was requested of this plugin.");
                } else {
                    strArr = (String[]) hashSet.toArray(new String[0]);
                }
            }
            if (strArr == null && strArr.length > 0) {
                requestPermissionForAliases(strArr, pluginCall, "checkPermissions");
                return;
            } else if (hashSet2.isEmpty()) {
                JSObject jSObject = new JSObject();
                for (String str : hashSet2) {
                    jSObject.put(str, PermissionState.GRANTED.toString());
                }
                pluginCall.resolve(jSObject);
                return;
            } else {
                pluginCall.resolve();
                return;
            }
        }
        list = null;
        hashSet = new HashSet();
        if (list != null) {
        }
        while (r3 < r2) {
        }
        strArr = (String[]) hashSet.toArray(new String[0]);
        if (strArr == null) {
        }
        if (hashSet2.isEmpty()) {
        }
    }

    private void handleLegacyPermission(PluginCall pluginCall) {
        NativePlugin legacyPluginAnnotation = this.handle.getLegacyPluginAnnotation();
        String[] permissions = legacyPluginAnnotation.permissions();
        if (permissions.length > 0) {
            saveCall(pluginCall);
            pluginRequestPermissions(permissions, legacyPluginAnnotation.permissionRequestCode());
            return;
        }
        pluginCall.resolve();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Deprecated
    public void handleRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        String[] undefinedPermissions;
        if (hasDefinedPermissions(strArr)) {
            return;
        }
        StringBuilder sb = new StringBuilder("Missing the following permissions in AndroidManifest.xml:\n");
        for (String str : PermissionHelper.getUndefinedPermissions(getContext(), strArr)) {
            sb.append(str + "\n");
        }
        this.savedLastCall.reject(sb.toString());
        this.savedLastCall = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Bundle saveInstanceState() {
        PluginCall savedCall = this.bridge.getSavedCall(this.lastPluginCallId);
        if (savedCall == null) {
            return null;
        }
        Bundle bundle = new Bundle();
        JSObject data = savedCall.getData();
        if (data != null) {
            bundle.putString(BUNDLE_PERSISTED_OPTIONS_JSON_KEY, data.toString());
        }
        return bundle;
    }

    @Deprecated
    protected void startActivityForResult(PluginCall pluginCall, Intent intent, int i) {
        this.bridge.startActivityForPluginWithResult(pluginCall, intent, i);
    }

    public void execute(Runnable runnable) {
        this.bridge.execute(runnable);
    }

    protected String getLogTag(String... strArr) {
        return Logger.tags(strArr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getLogTag() {
        return Logger.tags(getClass().getSimpleName());
    }
}
