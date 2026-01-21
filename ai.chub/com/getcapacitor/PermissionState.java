package com.getcapacitor;

import java.util.Locale;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public enum PermissionState {
    GRANTED("granted"),
    DENIED("denied"),
    PROMPT("prompt"),
    PROMPT_WITH_RATIONALE("prompt-with-rationale");
    
    private String state;

    PermissionState(String str) {
        this.state = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.state;
    }

    public static PermissionState byState(String str) {
        return valueOf(str.toUpperCase(Locale.ROOT).replace('-', '_'));
    }
}
