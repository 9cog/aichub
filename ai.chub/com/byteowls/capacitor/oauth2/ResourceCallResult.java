package com.byteowls.capacitor.oauth2;

import com.getcapacitor.JSObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class ResourceCallResult {
    private boolean error;
    private String errorMsg;
    private JSObject response;

    public boolean isError() {
        return this.error;
    }

    public void setError(boolean z) {
        this.error = z;
    }

    public JSObject getResponse() {
        return this.response;
    }

    public void setResponse(JSObject jSObject) {
        this.response = jSObject;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String str) {
        this.errorMsg = str;
    }
}
