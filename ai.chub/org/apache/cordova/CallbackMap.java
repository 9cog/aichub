package org.apache.cordova;

import android.util.Pair;
import android.util.SparseArray;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CallbackMap {
    private int currentCallbackId = 0;
    private SparseArray<Pair<CordovaPlugin, Integer>> callbacks = new SparseArray<>();

    public synchronized int registerCallback(CordovaPlugin receiver, int requestCode) {
        int i;
        i = this.currentCallbackId;
        this.currentCallbackId = i + 1;
        this.callbacks.put(i, new Pair<>(receiver, Integer.valueOf(requestCode)));
        return i;
    }

    public synchronized Pair<CordovaPlugin, Integer> getAndRemoveCallback(int mappedId) {
        Pair<CordovaPlugin, Integer> pair;
        pair = this.callbacks.get(mappedId);
        this.callbacks.remove(mappedId);
        return pair;
    }
}
