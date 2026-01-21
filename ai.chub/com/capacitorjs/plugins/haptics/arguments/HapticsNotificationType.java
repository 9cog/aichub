package com.capacitorjs.plugins.haptics.arguments;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public enum HapticsNotificationType implements HapticsVibrationType {
    SUCCESS("SUCCESS", new long[]{0, 35, 65, 21}, new int[]{0, 250, 0, 180}, new long[]{0, 35, 65, 21}),
    WARNING("WARNING", new long[]{0, 30, 40, 30, 50, 60}, new int[]{255, 255, 255, 255, 255, 255}, new long[]{0, 30, 40, 30, 50, 60}),
    ERROR("ERROR", new long[]{0, 27, 45, 50}, new int[]{0, 120, 0, 250}, new long[]{0, 27, 45, 50});
    
    private final int[] amplitudes;
    private final long[] oldSDKPattern;
    private final long[] timings;
    private final String type;

    HapticsNotificationType(String str, long[] jArr, int[] iArr, long[] jArr2) {
        this.type = str;
        this.timings = jArr;
        this.amplitudes = iArr;
        this.oldSDKPattern = jArr2;
    }

    public static HapticsNotificationType fromString(String str) {
        HapticsNotificationType[] values;
        for (HapticsNotificationType hapticsNotificationType : values()) {
            if (hapticsNotificationType.type.equals(str)) {
                return hapticsNotificationType;
            }
        }
        return SUCCESS;
    }

    @Override // com.capacitorjs.plugins.haptics.arguments.HapticsVibrationType
    public long[] getTimings() {
        return this.timings;
    }

    @Override // com.capacitorjs.plugins.haptics.arguments.HapticsVibrationType
    public int[] getAmplitudes() {
        return this.amplitudes;
    }

    @Override // com.capacitorjs.plugins.haptics.arguments.HapticsVibrationType
    public long[] getOldSDKPattern() {
        return this.oldSDKPattern;
    }
}
