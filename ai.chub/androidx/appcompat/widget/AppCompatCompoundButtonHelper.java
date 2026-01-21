package androidx.appcompat.widget;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import androidx.appcompat.R;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.CompoundButtonCompat;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
class AppCompatCompoundButtonHelper {
    private ColorStateList mButtonTintList = null;
    private PorterDuff.Mode mButtonTintMode = null;
    private boolean mHasButtonTint = false;
    private boolean mHasButtonTintMode = false;
    private boolean mSkipNextApply;
    private final CompoundButton mView;

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getCompoundPaddingLeft(int i) {
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppCompatCompoundButtonHelper(CompoundButton compoundButton) {
        this.mView = compoundButton;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0067 A[Catch: all -> 0x008f, TryCatch #1 {all -> 0x008f, blocks: (B:3:0x001f, B:5:0x0027, B:7:0x002f, B:12:0x0042, B:14:0x004a, B:16:0x0052, B:17:0x005f, B:19:0x0067, B:20:0x0072, B:22:0x007a), top: B:30:0x001f }] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x007a A[Catch: all -> 0x008f, TRY_LEAVE, TryCatch #1 {all -> 0x008f, blocks: (B:3:0x001f, B:5:0x0027, B:7:0x002f, B:12:0x0042, B:14:0x004a, B:16:0x0052, B:17:0x005f, B:19:0x0067, B:20:0x0072, B:22:0x007a), top: B:30:0x001f }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void loadFromAttributes(AttributeSet attributeSet, int i) {
        boolean z;
        int resourceId;
        int resourceId2;
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), attributeSet, R.styleable.CompoundButton, i, 0);
        CompoundButton compoundButton = this.mView;
        ViewCompat.saveAttributeDataForStyleable(compoundButton, compoundButton.getContext(), R.styleable.CompoundButton, attributeSet, obtainStyledAttributes.getWrappedTypeArray(), i, 0);
        try {
            if (obtainStyledAttributes.hasValue(R.styleable.CompoundButton_buttonCompat) && (resourceId2 = obtainStyledAttributes.getResourceId(R.styleable.CompoundButton_buttonCompat, 0)) != 0) {
                try {
                    CompoundButton compoundButton2 = this.mView;
                    compoundButton2.setButtonDrawable(AppCompatResources.getDrawable(compoundButton2.getContext(), resourceId2));
                    z = true;
                } catch (Resources.NotFoundException unused) {
                }
                if (!z && obtainStyledAttributes.hasValue(R.styleable.CompoundButton_android_button) && (resourceId = obtainStyledAttributes.getResourceId(R.styleable.CompoundButton_android_button, 0)) != 0) {
                    CompoundButton compoundButton3 = this.mView;
                    compoundButton3.setButtonDrawable(AppCompatResources.getDrawable(compoundButton3.getContext(), resourceId));
                }
                if (obtainStyledAttributes.hasValue(R.styleable.CompoundButton_buttonTint)) {
                    CompoundButtonCompat.setButtonTintList(this.mView, obtainStyledAttributes.getColorStateList(R.styleable.CompoundButton_buttonTint));
                }
                if (obtainStyledAttributes.hasValue(R.styleable.CompoundButton_buttonTintMode)) {
                    CompoundButtonCompat.setButtonTintMode(this.mView, DrawableUtils.parseTintMode(obtainStyledAttributes.getInt(R.styleable.CompoundButton_buttonTintMode, -1), null));
                }
            }
            z = false;
            if (!z) {
                CompoundButton compoundButton32 = this.mView;
                compoundButton32.setButtonDrawable(AppCompatResources.getDrawable(compoundButton32.getContext(), resourceId));
            }
            if (obtainStyledAttributes.hasValue(R.styleable.CompoundButton_buttonTint)) {
            }
            if (obtainStyledAttributes.hasValue(R.styleable.CompoundButton_buttonTintMode)) {
            }
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSupportButtonTintList(ColorStateList colorStateList) {
        this.mButtonTintList = colorStateList;
        this.mHasButtonTint = true;
        applyButtonTint();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ColorStateList getSupportButtonTintList() {
        return this.mButtonTintList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSupportButtonTintMode(PorterDuff.Mode mode) {
        this.mButtonTintMode = mode;
        this.mHasButtonTintMode = true;
        applyButtonTint();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PorterDuff.Mode getSupportButtonTintMode() {
        return this.mButtonTintMode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onSetButtonDrawable() {
        if (this.mSkipNextApply) {
            this.mSkipNextApply = false;
            return;
        }
        this.mSkipNextApply = true;
        applyButtonTint();
    }

    void applyButtonTint() {
        Drawable buttonDrawable = CompoundButtonCompat.getButtonDrawable(this.mView);
        if (buttonDrawable != null) {
            if (this.mHasButtonTint || this.mHasButtonTintMode) {
                Drawable mutate = DrawableCompat.wrap(buttonDrawable).mutate();
                if (this.mHasButtonTint) {
                    DrawableCompat.setTintList(mutate, this.mButtonTintList);
                }
                if (this.mHasButtonTintMode) {
                    DrawableCompat.setTintMode(mutate, this.mButtonTintMode);
                }
                if (mutate.isStateful()) {
                    mutate.setState(this.mView.getDrawableState());
                }
                this.mView.setButtonDrawable(mutate);
            }
        }
    }
}
