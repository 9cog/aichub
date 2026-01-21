package com.capacitorjs.plugins.haptics.arguments;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public enum HapticsImpactType implements HapticsVibrationType {
    LIGHT("LIGHT", new long[]{0, 50}, new int[]{0, 110}, new long[]{0, 20}),
    MEDIUM("MEDIUM", new long[]{0, 43}, new int[]{0, 180}, new long[]{0, 43}),
    HEAVY("HEAVY", new long[]{0, 60}, new int[]{0, 255}, new long[]{0, 61});
    
    private final int[] amplitudes;
    private final long[] oldSDKPattern;
    private final long[] timings;
    private final String type;

    HapticsImpactType(String str, long[] jArr, int[] iArr, long[] jArr2) {
        this.type = str;
        this.timings = jArr;
        this.amplitudes = iArr;
        this.oldSDKPattern = jArr2;
    }

    public static HapticsImpactType fromString(String str) {
        HapticsImpactType[] values;
        for (HapticsImpactType hapticsImpactType : values()) {
            if (hapticsImpactType.type.equals(str)) {
                return hapticsImpactType;
            }
        }
        return HEAVY;
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
