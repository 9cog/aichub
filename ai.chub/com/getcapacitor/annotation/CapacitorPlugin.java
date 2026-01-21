package com.getcapacitor.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public @interface CapacitorPlugin {
    String name() default "";

    Permission[] permissions() default {};

    int[] requestCodes() default {};
}
