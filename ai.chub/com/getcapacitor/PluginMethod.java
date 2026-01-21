package com.getcapacitor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public @interface PluginMethod {
    public static final String RETURN_CALLBACK = "callback";
    public static final String RETURN_NONE = "none";
    public static final String RETURN_PROMISE = "promise";

    String returnType() default "promise";
}
