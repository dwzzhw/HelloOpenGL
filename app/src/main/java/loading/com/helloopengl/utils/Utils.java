package loading.com.helloopengl.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by loading on 3/14/17.
 */

public class Utils {
    /**
     * 从assert下的json文件读取测试数据
     *
     * @param strAssertFileName
     * @return
     */
    public static String readAssertResource(Context context, String strAssertFileName) {
        AssetManager assetManager = context.getAssets();
        String strResponse = "";
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(strAssertFileName);
            strResponse = getStringFromInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return strResponse;
    }

    private static String getStringFromInputStream(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        try {
            int length;
            byte[] buffer = new byte[1024];
            while ((length = inputStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, length));
            }
        } catch (IOException e) {
        }
        return sb.toString();
    }

    private static GradientDrawable getTL2BRGradientDrawable(int[] colors) {
        GradientDrawable gradientDrawable
                = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        gradientDrawable.setCornerRadius(0.0f);//rectangle
        return gradientDrawable;
    }

    public static LayerDrawable getTL2BRGradientMaskDrawable(int[] colors, int maskResId, Context context) {
        return new LayerDrawable(new Drawable[]{
                getTL2BRGradientDrawable(colors),
                context.getResources().getDrawable(maskResId)
        });
    }
}
