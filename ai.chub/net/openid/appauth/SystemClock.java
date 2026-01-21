package net.openid.appauth;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
class SystemClock implements Clock {
    public static final SystemClock INSTANCE = new SystemClock();

    private SystemClock() {
    }

    @Override // net.openid.appauth.Clock
    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
