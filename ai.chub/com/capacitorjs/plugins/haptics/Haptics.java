package com.capacitorjs.plugins.haptics;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import com.capacitorjs.plugins.haptics.arguments.HapticsSelectionType;
import com.capacitorjs.plugins.haptics.arguments.HapticsVibrationType;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class Haptics {
    private Context context;
    private boolean selectionStarted = false;
    private final Vibrator vibrator;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Haptics(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= 31) {
            this.vibrator = ((VibratorManager) context.getSystemService("vibrator_manager")).getDefaultVibrator();
        } else {
            this.vibrator = getDeprecatedVibrator(context);
        }
    }

    private Vibrator getDeprecatedVibrator(Context context) {
        return (Vibrator) context.getSystemService("vibrator");
    }

    public void vibrate(int i) {
        if (Build.VERSION.SDK_INT >= 26) {
            this.vibrator.vibrate(VibrationEffect.createOneShot(i, -1));
        } else {
            vibratePre26(i);
        }
    }

    private void vibratePre26(int i) {
        this.vibrator.vibrate(i);
    }

    private void vibratePre26(long[] jArr, int i) {
        this.vibrator.vibrate(jArr, i);
    }

    public void selectionStart() {
        this.selectionStarted = true;
    }

    public void selectionChanged() {
        if (this.selectionStarted) {
            performHaptics(new HapticsSelectionType());
        }
    }

    public void selectionEnd() {
        this.selectionStarted = false;
    }

    public void performHaptics(HapticsVibrationType hapticsVibrationType) {
        if (Build.VERSION.SDK_INT >= 26) {
            this.vibrator.vibrate(VibrationEffect.createWaveform(hapticsVibrationType.getTimings(), hapticsVibrationType.getAmplitudes(), -1));
        } else {
            vibratePre26(hapticsVibrationType.getOldSDKPattern(), -1);
        }
    }
}
