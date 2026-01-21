package androidx.browser.trusted;

import android.os.Bundle;
import com.android.tools.r8.annotations.SynthesizedClassV2;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface TrustedWebActivityDisplayMode {
    public static final String KEY_ID = "androidx.browser.trusted.displaymode.KEY_ID";

    Bundle toBundle();

    @SynthesizedClassV2(kind = 8, versionHash = "b33e07cc0d03f9f0e6c4c883743d0373fd130388f5a551bfa15ea60a927a2ecb")
    /* renamed from: androidx.browser.trusted.TrustedWebActivityDisplayMode$-CC  reason: invalid class name */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public final /* synthetic */ class CC {
        public static TrustedWebActivityDisplayMode fromBundle(Bundle bundle) {
            if (bundle.getInt(TrustedWebActivityDisplayMode.KEY_ID) == 1) {
                return ImmersiveMode.fromBundle(bundle);
            }
            return new DefaultMode();
        }
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class DefaultMode implements TrustedWebActivityDisplayMode {
        private static final int ID = 0;

        @Override // androidx.browser.trusted.TrustedWebActivityDisplayMode
        public Bundle toBundle() {
            Bundle bundle = new Bundle();
            bundle.putInt(TrustedWebActivityDisplayMode.KEY_ID, 0);
            return bundle;
        }
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class ImmersiveMode implements TrustedWebActivityDisplayMode {
        private static final int ID = 1;
        public static final String KEY_CUTOUT_MODE = "androidx.browser.trusted.displaymode.KEY_CUTOUT_MODE";
        public static final String KEY_STICKY = "androidx.browser.trusted.displaymode.KEY_STICKY";
        private final boolean mIsSticky;
        private final int mLayoutInDisplayCutoutMode;

        public ImmersiveMode(boolean z, int i) {
            this.mIsSticky = z;
            this.mLayoutInDisplayCutoutMode = i;
        }

        static TrustedWebActivityDisplayMode fromBundle(Bundle bundle) {
            return new ImmersiveMode(bundle.getBoolean(KEY_STICKY), bundle.getInt(KEY_CUTOUT_MODE));
        }

        @Override // androidx.browser.trusted.TrustedWebActivityDisplayMode
        public Bundle toBundle() {
            Bundle bundle = new Bundle();
            bundle.putInt(TrustedWebActivityDisplayMode.KEY_ID, 1);
            bundle.putBoolean(KEY_STICKY, this.mIsSticky);
            bundle.putInt(KEY_CUTOUT_MODE, this.mLayoutInDisplayCutoutMode);
            return bundle;
        }

        public boolean isSticky() {
            return this.mIsSticky;
        }

        public int layoutInDisplayCutoutMode() {
            return this.mLayoutInDisplayCutoutMode;
        }
    }
}
