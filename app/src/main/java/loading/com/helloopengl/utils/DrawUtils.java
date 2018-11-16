package loading.com.helloopengl.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;

public class DrawUtils {
    public static Paint CLEAR_PAINT;
    public static Rect RECT;

    static {
        CLEAR_PAINT = new Paint();
        CLEAR_PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        RECT = new Rect();
    }

    public static void clearCanvas(Canvas canvas) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {        //修复android 4.3版本对drawColor抛出的异常
            clearCanvasWithRect(canvas, 0, 0, canvas.getWidth(), canvas.getHeight());
        } else {
            clearCanvasWithColor(canvas);
        }
    }

    public static void clearCanvasWithColor(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }


    public static void clearCanvasWithRect(Canvas canvas, int left, int top, int right, int bottom) {
        RECT.set(left, top, right, bottom);
        clearCanvasWithRect(canvas, RECT);
    }

    public static void clearCanvasWithRect(Canvas canvas, Rect rect) {
        if (rect.width() <= 0 || rect.height() <= 0) {
            return;
        }
        canvas.drawRect(rect, CLEAR_PAINT);
    }
}
