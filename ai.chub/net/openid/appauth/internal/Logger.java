package net.openid.appauth.internal;

import android.util.Log;
import net.openid.appauth.Preconditions;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class Logger {
    static final String LOG_TAG = "AppAuth";
    private static Logger sInstance;
    private final LogWrapper mLog;
    private final int mLogLevel;

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public interface LogWrapper {
        String getStackTraceString(Throwable tr);

        boolean isLoggable(String tag, int level);

        void println(int level, String tag, String message);
    }

    public static synchronized Logger getInstance() {
        Logger logger;
        synchronized (Logger.class) {
            if (sInstance == null) {
                sInstance = new Logger(AndroidLogWrapper.INSTANCE);
            }
            logger = sInstance;
        }
        return logger;
    }

    public static synchronized void setInstance(Logger logger) {
        synchronized (Logger.class) {
            sInstance = logger;
        }
    }

    Logger(LogWrapper log) {
        this.mLog = (LogWrapper) Preconditions.checkNotNull(log);
        int i = 7;
        while (i >= 2 && this.mLog.isLoggable(LOG_TAG, i)) {
            i--;
        }
        this.mLogLevel = i + 1;
    }

    public static void verbose(String message, Object... messageParams) {
        getInstance().log(2, null, message, messageParams);
    }

    public static void verboseWithStack(Throwable tr, String message, Object... messageParams) {
        getInstance().log(2, tr, message, messageParams);
    }

    public static void debug(String message, Object... messageParams) {
        getInstance().log(3, null, message, messageParams);
    }

    public static void debugWithStack(Throwable tr, String message, Object... messageParams) {
        getInstance().log(3, tr, message, messageParams);
    }

    public static void info(String message, Object... messageParams) {
        getInstance().log(4, null, message, messageParams);
    }

    public static void infoWithStack(Throwable tr, String message, Object... messageParams) {
        getInstance().log(4, tr, message, messageParams);
    }

    public static void warn(String message, Object... messageParams) {
        getInstance().log(5, null, message, messageParams);
    }

    public static void warnWithStack(Throwable tr, String message, Object... messageParams) {
        getInstance().log(5, tr, message, messageParams);
    }

    public static void error(String message, Object... messageParams) {
        getInstance().log(6, null, message, messageParams);
    }

    public static void errorWithStack(Throwable tr, String message, Object... messageParams) {
        getInstance().log(6, tr, message, messageParams);
    }

    public void log(int level, Throwable tr, String message, Object... messageParams) {
        if (this.mLogLevel > level) {
            return;
        }
        if (messageParams != null && messageParams.length >= 1) {
            message = String.format(message, messageParams);
        }
        if (tr != null) {
            message = message + "\n" + this.mLog.getStackTraceString(tr);
        }
        this.mLog.println(level, LOG_TAG, message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class AndroidLogWrapper implements LogWrapper {
        private static final AndroidLogWrapper INSTANCE = new AndroidLogWrapper();

        private AndroidLogWrapper() {
        }

        @Override // net.openid.appauth.internal.Logger.LogWrapper
        public void println(int level, String tag, String message) {
            Log.println(level, tag, message);
        }

        @Override // net.openid.appauth.internal.Logger.LogWrapper
        public boolean isLoggable(String tag, int level) {
            return Log.isLoggable(tag, level);
        }

        @Override // net.openid.appauth.internal.Logger.LogWrapper
        public String getStackTraceString(Throwable tr) {
            return Log.getStackTraceString(tr);
        }
    }
}
