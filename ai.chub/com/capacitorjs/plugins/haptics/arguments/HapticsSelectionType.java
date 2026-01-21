package com.capacitorjs.plugins.haptics.arguments;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class HapticsSelectionType implements HapticsVibrationType {
    private static final long[] timings = {0, 100};
    private static final int[] amplitudes = {0, 100};
    private static final long[] oldSDKPattern = {0, 70};

    @Override // com.capacitorjs.plugins.haptics.arguments.HapticsVibrationType
    public long[] getTimings() {
        return timings;
    }

    @Override // com.capacitorjs.plugins.haptics.arguments.HapticsVibrationType
    public int[] getAmplitudes() {
        return amplitudes;
    }

    @Override // com.capacitorjs.plugins.haptics.arguments.HapticsVibrationType
    public long[] getOldSDKPattern() {
        return oldSDKPattern;
    }
}
