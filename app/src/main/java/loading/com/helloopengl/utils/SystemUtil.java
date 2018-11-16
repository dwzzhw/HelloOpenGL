package loading.com.helloopengl.utils;

import android.os.Looper;

public class SystemUtil {
    public static boolean isMainThread() {
        boolean isMainThread = false;
        Thread curThread = Thread.currentThread();
        Looper mainLooper = Looper.getMainLooper();
        if (mainLooper != null) {
            Thread mainThread = mainLooper.getThread();
            isMainThread = (curThread.getId() == mainThread.getId());
        }
        return isMainThread;
    }
}
