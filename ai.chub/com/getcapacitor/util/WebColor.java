package com.getcapacitor.util;

import android.graphics.Color;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class WebColor {
    public static int parseColor(String str) {
        if (str.charAt(0) != '#') {
            str = "#" + str;
        }
        if (str.length() != 7 && str.length() != 9) {
            throw new IllegalArgumentException("The encoded color space is invalid or unknown");
        }
        if (str.length() == 7) {
            return Color.parseColor(str);
        }
        return Color.parseColor("#" + str.substring(7) + str.substring(1, 7));
    }
}
