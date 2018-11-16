package loading.com.helloopengl.mp4animation.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import loading.com.helloopengl.utils.Loger;

/**
 * 礼物特效的View
 * Created by skindhu on 2017/2/20.
 */

public class GiftView extends GLSurfaceView {
    private static final String TAG = "SuperGiftPlayer.GiftView";

    private TextureAvailableListener mAvailableListener;
    private TextureDestroyListener mDestroyAvailableListener;

    public GiftView(Context context) {
        super(context);
        init();
    }

    public GiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        Loger.d(TAG, "init");
//        setOpaque(false);
//        setSurfaceTextureListener(this);
    }

    public void setTextureAvailableListener(TextureAvailableListener listener) {
        synchronized (this) {
            mAvailableListener = listener;
        }
    }

    public void setTextureDestroyListener(TextureDestroyListener listener) {
        synchronized (this) {
            mDestroyAvailableListener = listener;
        }
    }

    public interface TextureAvailableListener {
        void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height);
    }

    public interface TextureDestroyListener {
        void onSurfaceTextureDestroyed(SurfaceTexture surface);
    }
}
