package com.capacitorjs.plugins.statusbar;

import android.view.View;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class StatusBar {
    private final AppCompatActivity activity;
    private int currentStatusBarColor;
    private final String defaultStyle = getStyle();

    public StatusBar(AppCompatActivity appCompatActivity) {
        this.activity = appCompatActivity;
        this.currentStatusBarColor = appCompatActivity.getWindow().getStatusBarColor();
    }

    public void setStyle(String str) {
        Window window = this.activity.getWindow();
        View decorView = window.getDecorView();
        if (str.equals("DEFAULT")) {
            str = this.defaultStyle;
        }
        WindowCompat.getInsetsController(window, decorView).setAppearanceLightStatusBars(!str.equals("DARK"));
    }

    public void setBackgroundColor(int i) {
        Window window = this.activity.getWindow();
        window.clearFlags(67108864);
        window.addFlags(Integer.MIN_VALUE);
        window.setStatusBarColor(i);
        this.currentStatusBarColor = i;
    }

    public void hide() {
        WindowCompat.getInsetsController(this.activity.getWindow(), this.activity.getWindow().getDecorView()).hide(WindowInsetsCompat.Type.statusBars());
    }

    public void show() {
        WindowCompat.getInsetsController(this.activity.getWindow(), this.activity.getWindow().getDecorView()).show(WindowInsetsCompat.Type.statusBars());
    }

    public void setOverlaysWebView(Boolean bool) {
        View decorView = this.activity.getWindow().getDecorView();
        int systemUiVisibility = decorView.getSystemUiVisibility();
        if (bool.booleanValue()) {
            decorView.setSystemUiVisibility(systemUiVisibility | 256 | 1024);
            this.currentStatusBarColor = this.activity.getWindow().getStatusBarColor();
            this.activity.getWindow().setStatusBarColor(0);
            return;
        }
        decorView.setSystemUiVisibility(systemUiVisibility & (-257) & (-1025));
        this.activity.getWindow().setStatusBarColor(this.currentStatusBarColor);
    }

    private boolean getIsOverlaid() {
        return (this.activity.getWindow().getDecorView().getSystemUiVisibility() & 1024) == 1024;
    }

    public StatusBarInfo getInfo() {
        Window window = this.activity.getWindow();
        WindowInsetsCompat rootWindowInsets = ViewCompat.getRootWindowInsets(window.getDecorView());
        boolean z = rootWindowInsets != null && rootWindowInsets.isVisible(WindowInsetsCompat.Type.statusBars());
        StatusBarInfo statusBarInfo = new StatusBarInfo();
        statusBarInfo.setStyle(getStyle());
        statusBarInfo.setOverlays(getIsOverlaid());
        statusBarInfo.setVisible(z);
        statusBarInfo.setColor(String.format("#%06X", Integer.valueOf(window.getStatusBarColor() & ViewCompat.MEASURED_SIZE_MASK)));
        return statusBarInfo;
    }

    private String getStyle() {
        return WindowCompat.getInsetsController(this.activity.getWindow(), this.activity.getWindow().getDecorView()).isAppearanceLightStatusBars() ? "LIGHT" : "DARK";
    }
}
