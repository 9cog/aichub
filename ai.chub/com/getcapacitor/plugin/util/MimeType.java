package com.getcapacitor.plugin.util;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
enum MimeType {
    APPLICATION_JSON("application/json"),
    APPLICATION_VND_API_JSON("application/vnd.api+json"),
    TEXT_HTML("text/html");
    
    private final String value;

    MimeType(String str) {
        this.value = str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getValue() {
        return this.value;
    }
}
