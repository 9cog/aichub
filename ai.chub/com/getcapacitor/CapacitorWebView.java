package com.getcapacitor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.WebView;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CapacitorWebView extends WebView {
    private Bridge bridge;
    private BaseInputConnection capInputConnection;

    public CapacitorWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setBridge(Bridge bridge) {
        this.bridge = bridge;
    }

    @Override // android.webkit.WebView, android.view.View
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        CapConfig loadDefault;
        Bridge bridge = this.bridge;
        if (bridge != null) {
            loadDefault = bridge.getConfig();
        } else {
            loadDefault = CapConfig.loadDefault(getContext());
        }
        if (loadDefault.isInputCaptured()) {
            if (this.capInputConnection == null) {
                this.capInputConnection = new BaseInputConnection(this, false);
            }
            return this.capInputConnection;
        }
        return super.onCreateInputConnection(editorInfo);
    }

    @Override // android.webkit.WebView, android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 2) {
            evaluateJavascript("document.activeElement.value = document.activeElement.value + '" + keyEvent.getCharacters() + "';", null);
            return false;
        }
        return super.dispatchKeyEvent(keyEvent);
    }
}
