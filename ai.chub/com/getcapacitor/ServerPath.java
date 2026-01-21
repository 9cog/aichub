package com.getcapacitor;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class ServerPath {
    private final String path;
    private final PathType type;

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public enum PathType {
        BASE_PATH,
        ASSET_PATH
    }

    public ServerPath(PathType pathType, String str) {
        this.type = pathType;
        this.path = str;
    }

    public PathType getType() {
        return this.type;
    }

    public String getPath() {
        return this.path;
    }
}
