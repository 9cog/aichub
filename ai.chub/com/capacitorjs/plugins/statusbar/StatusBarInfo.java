package com.capacitorjs.plugins.statusbar;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class StatusBarInfo {
    private String color;
    private boolean overlays;
    private String style;
    private boolean visible;

    public boolean isOverlays() {
        return this.overlays;
    }

    public void setOverlays(boolean z) {
        this.overlays = z;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean z) {
        this.visible = z;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String str) {
        this.style = str;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String str) {
        this.color = str;
    }
}
