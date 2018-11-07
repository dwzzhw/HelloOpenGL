package loading.com.helloopengl.utils;

import android.text.TextUtils;

public class Loger {
    private final static String LOGTAG = "HelloOpenGl";
    private static boolean isDebug = true;

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    public static void v(String tag, String log) {
        if (isDebug) {
            android.util.Log.i(LOGTAG, getLogMsg(tag, log));
        }
    }

    public static void v(String tag, String log, Throwable e) {
        if (isDebug) {
            android.util.Log.i(LOGTAG, getLogMsg(tag, log), e);
        }
    }

    public static void d(String tag, String log) {
        if (isDebug) {
            android.util.Log.i(LOGTAG, getLogMsg(tag, log));
        }
    }

    public static void d(String tag, String log, Throwable e) {
        if (isDebug) {
            android.util.Log.i(LOGTAG, getLogMsg(tag, log), e);
        }
    }

    public static void i(String tag, String log) {
        if (isDebug) {
            android.util.Log.i(LOGTAG, getLogMsg(tag, log));
        }
    }

    public static void i(String tag, String log, Throwable e) {
        if (isDebug) {
            android.util.Log.i(LOGTAG, getLogMsg(tag, log), e);
        }
    }

    public static void w(String tag, String log) {
        if (isDebug) {
            android.util.Log.w(LOGTAG, getLogMsg(tag, log));
        }
    }

    public static void w(String tag, String log, Throwable e) {
        if (isDebug) {
            android.util.Log.w(LOGTAG, getLogMsg(tag, log), e);
        }
    }

    public static void e(String tag, String log) {
        if (isDebug) {
            android.util.Log.e(LOGTAG, getLogMsg(tag, log));
        }
    }

    public static void e(String tag, String log, Throwable e) {
        if (isDebug) {
            android.util.Log.e(LOGTAG, getLogMsg(tag, log), e);
        }
    }

    public static void e(String msg, Throwable tr) {
        if (isDebug) {
            android.util.Log.e(LOGTAG, getLogMsg(null, msg), tr);
        }
    }

    public static void wtf(String tag, String log) {
        android.util.Log.wtf(LOGTAG, getLogMsg(tag, log));
    }

    public static void wtf(String tag, String log, Throwable e) {
        android.util.Log.wtf(LOGTAG, getLogMsg(tag, log), e);
    }

    private static String getLogMsg(String tag, String log) {
        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(DateUtil.convertTime(System.currentTimeMillis(), "yyyy/MM/dd HH:mm:ss")); // SimpleDateFormat不是线程安全的
        if ( !TextUtils.isEmpty(tag) ) {
            stringBuilder.append("==[").append(tag).append("]==  ");
        }
        if ( log != null ) {
            stringBuilder.append(log);
        }
        return stringBuilder.toString();
    }

    public static void printStackTrace(String tag, Throwable tr) {
        if (isDebug) {
            android.util.Log.e(tag,
                    android.util.Log.getStackTraceString(tr));
        }
    }

    public static void printStackTrace(String tag) {
        printStackTrace(tag, new Throwable());
    }

    public static void printStackTraceInfo(String tag) {
        if (isDebug) {
            StackTraceElement[] tStackTraceElements = Thread.currentThread().getStackTrace();
            for (StackTraceElement tTraceElement : tStackTraceElements) {
                android.util.Log.i(tag, tTraceElement.toString());
            }
        }
    }
}
