package com.tchvu3.capacitorvoicerecorder;

import com.getcapacitor.JSObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class ResponseGenerator {
    private static final String STATUS_RESPONSE_KEY = "status";
    private static final String VALUE_RESPONSE_KEY = "value";

    public static JSObject fromBoolean(boolean z) {
        return z ? successResponse() : failResponse();
    }

    public static JSObject successResponse() {
        JSObject jSObject = new JSObject();
        jSObject.put(VALUE_RESPONSE_KEY, true);
        return jSObject;
    }

    public static JSObject failResponse() {
        JSObject jSObject = new JSObject();
        jSObject.put(VALUE_RESPONSE_KEY, false);
        return jSObject;
    }

    public static JSObject dataResponse(Object obj) {
        JSObject jSObject = new JSObject();
        jSObject.put(VALUE_RESPONSE_KEY, obj);
        return jSObject;
    }

    public static JSObject statusResponse(CurrentRecordingStatus currentRecordingStatus) {
        JSObject jSObject = new JSObject();
        jSObject.put("status", currentRecordingStatus.name());
        return jSObject;
    }
}
