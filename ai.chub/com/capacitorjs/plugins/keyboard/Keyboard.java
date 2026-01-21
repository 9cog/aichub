package com.capacitorjs.plugins.keyboard;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.getcapacitor.Logger;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class Keyboard {
    static final String EVENT_KB_DID_HIDE = "keyboardDidHide";
    static final String EVENT_KB_DID_SHOW = "keyboardDidShow";
    static final String EVENT_KB_WILL_HIDE = "keyboardWillHide";
    static final String EVENT_KB_WILL_SHOW = "keyboardWillShow";
    private AppCompatActivity activity;
    private FrameLayout.LayoutParams frameLayoutParams;
    private KeyboardEventListener keyboardEventListener;
    private ViewTreeObserver.OnGlobalLayoutListener list;
    private View mChildOfContent;
    private View rootView;
    private int usableHeightPrevious;

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    interface KeyboardEventListener {
        void onKeyboardEvent(String str, int i);
    }

    public KeyboardEventListener getKeyboardEventListener() {
        return this.keyboardEventListener;
    }

    public void setKeyboardEventListener(KeyboardEventListener keyboardEventListener) {
        this.keyboardEventListener = keyboardEventListener;
    }

    public Keyboard(final AppCompatActivity appCompatActivity, final boolean z) {
        this.activity = appCompatActivity;
        final float f = appCompatActivity.getResources().getDisplayMetrics().density;
        FrameLayout frameLayout = (FrameLayout) appCompatActivity.getWindow().getDecorView().findViewById(16908290);
        this.rootView = frameLayout.getRootView();
        this.list = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.capacitorjs.plugins.keyboard.Keyboard.1
            int previousHeightDiff = 0;

            /* JADX WARN: Removed duplicated region for block: B:27:0x00a0  */
            /* JADX WARN: Removed duplicated region for block: B:30:0x00ab  */
            /* JADX WARN: Removed duplicated region for block: B:31:0x00c2  */
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public void onGlobalLayout() {
                int legacyStableInsetBottom;
                int i;
                int i2;
                Rect rect = new Rect();
                Keyboard.this.rootView.getWindowVisibleDisplayFrame(rect);
                int height = Keyboard.this.rootView.getRootView().getHeight();
                int i3 = rect.bottom;
                if (Build.VERSION.SDK_INT >= 30) {
                    legacyStableInsetBottom = Keyboard.this.rootView.getRootWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars()).bottom;
                } else if (Build.VERSION.SDK_INT >= 23) {
                    legacyStableInsetBottom = Keyboard.this.getLegacyStableInsetBottom(Keyboard.this.rootView.getRootWindowInsets());
                } else {
                    height = Keyboard.this.getLegacySizePoint().y;
                    i = (int) ((height - i3) / f);
                    if (i <= 100 && i != this.previousHeightDiff) {
                        if (z) {
                            possiblyResizeChildOfContent(true);
                        }
                        if (Keyboard.this.keyboardEventListener != null) {
                            Keyboard.this.keyboardEventListener.onKeyboardEvent(Keyboard.EVENT_KB_WILL_SHOW, i);
                            Keyboard.this.keyboardEventListener.onKeyboardEvent(Keyboard.EVENT_KB_DID_SHOW, i);
                        } else {
                            Logger.warn("Native Keyboard Event Listener not found");
                        }
                    } else {
                        i2 = this.previousHeightDiff;
                        if (i != i2 && i2 - i > 100) {
                            if (z) {
                                possiblyResizeChildOfContent(false);
                            }
                            if (Keyboard.this.keyboardEventListener == null) {
                                Keyboard.this.keyboardEventListener.onKeyboardEvent(Keyboard.EVENT_KB_WILL_HIDE, 0);
                                Keyboard.this.keyboardEventListener.onKeyboardEvent(Keyboard.EVENT_KB_DID_HIDE, 0);
                            } else {
                                Logger.warn("Native Keyboard Event Listener not found");
                            }
                        }
                    }
                    this.previousHeightDiff = i;
                }
                i3 += legacyStableInsetBottom;
                i = (int) ((height - i3) / f);
                if (i <= 100) {
                }
                i2 = this.previousHeightDiff;
                if (i != i2) {
                    if (z) {
                    }
                    if (Keyboard.this.keyboardEventListener == null) {
                    }
                }
                this.previousHeightDiff = i;
            }

            private void possiblyResizeChildOfContent(boolean z2) {
                int computeUsableHeight = z2 ? computeUsableHeight() : -1;
                if (Keyboard.this.usableHeightPrevious != computeUsableHeight) {
                    Keyboard.this.frameLayoutParams.height = computeUsableHeight;
                    Keyboard.this.mChildOfContent.requestLayout();
                    Keyboard.this.usableHeightPrevious = computeUsableHeight;
                }
            }

            private int computeUsableHeight() {
                Rect rect = new Rect();
                Keyboard.this.mChildOfContent.getWindowVisibleDisplayFrame(rect);
                return isOverlays() ? rect.bottom : rect.height();
            }

            private boolean isOverlays() {
                return (appCompatActivity.getWindow().getDecorView().getSystemUiVisibility() & 1024) == 1024;
            }
        };
        this.mChildOfContent = frameLayout.getChildAt(0);
        this.rootView.getViewTreeObserver().addOnGlobalLayoutListener(this.list);
        this.frameLayoutParams = (FrameLayout.LayoutParams) this.mChildOfContent.getLayoutParams();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getLegacyStableInsetBottom(WindowInsets windowInsets) {
        return windowInsets.getStableInsetBottom();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Point getLegacySizePoint() {
        Display defaultDisplay = this.activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        return point;
    }

    public void show() {
        ((InputMethodManager) this.activity.getSystemService("input_method")).showSoftInput(this.activity.getCurrentFocus(), 0);
    }

    public boolean hide() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.activity.getSystemService("input_method");
        View currentFocus = this.activity.getCurrentFocus();
        if (currentFocus == null) {
            return false;
        }
        inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 2);
        return true;
    }
}
