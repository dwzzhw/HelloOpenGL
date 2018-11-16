package loading.com.helloopengl.utils;

import android.os.Handler;
import android.os.Looper;

public class UiThreadUtil {
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void runOnUiThread(Runnable runnable) {
        if ( runnable != null ) {
            if ( SystemUtil.isMainThread() ) {
                runnable.run();
            } else {
                mHandler.post(runnable);
            }
        }
    }

    public static void removeRunnable(Runnable runnable) {
        if ( runnable != null ) {
            mHandler.removeCallbacks(runnable);
        }
    }


    public static boolean postDelay(Runnable action, long delayMillis) {
        boolean result = false;
        if ( action != null ) {
            result = mHandler.postDelayed(action, delayMillis);
        }
        return result;
    }

    public static void postRunnable(Runnable runnable) {
        if ( runnable != null ) {
            mHandler.post(runnable);
        }
    }
}
