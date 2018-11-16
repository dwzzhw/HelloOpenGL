package loading.com.helloopengl.mp4animation.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import java.io.File;

import loading.com.helloopengl.R;
import loading.com.helloopengl.inner.nopub.GiftPlayer;
import loading.com.helloopengl.mp4animation.AnimationRenderer;
import loading.com.helloopengl.mp4animation.IAnimationPlayer;
import loading.com.helloopengl.mp4animation.PlayCallback;
import loading.com.helloopengl.mp4animation.PlayState;
import loading.com.helloopengl.utils.Loger;
import loading.com.helloopengl.utils.ShadeUtils;

/**
 * Note: 同时两个Animation采用软解在Xiaomi mix2s上回Native Exception
 */
public class AnimationView extends FrameLayout implements PlayCallback, AnimationMaskView.IOnErrorLayerClickListener {
    private static final String TAG = "AnimationView";
    private GiftView mAnimationView;
    private IAnimationPlayer mAnimationPlayer;
    private AnimationMaskView maskView;

    private static final int DEFAULT_FPS = 24;

    private int mFps = DEFAULT_FPS;
    private int mSplitOrien = ShadeUtils.VIDEO_SPLIT_HORIZONTAL;
    private boolean mEnableHardwareDecode = true;

    private String mLocalAnimationPath;
    private String mRemoteAnimationUrl;

    public AnimationView(@NonNull Context context) {
        this(context, null);
    }

    public AnimationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.animation_view_layout, this, true);
        mAnimationView = (GiftView) findViewById(R.id.gif_player_anim_view);
        maskView = (AnimationMaskView) findViewById(R.id.gif_player_mask_view);
        maskView.setErrorClickListener(this);

        if (attrs != null) {
            TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.AnimationView);
            if (typeArray != null) {
                try {
                    mFps = typeArray.getInt(R.styleable.AnimationView_fps, DEFAULT_FPS);
                    mSplitOrien = typeArray.getInt(R.styleable.AnimationView_split_orien, ShadeUtils.VIDEO_SPLIT_HORIZONTAL);
                    mEnableHardwareDecode = typeArray.getBoolean(R.styleable.AnimationView_enableHardwareDecode, true);
                } catch (Exception e) {
                    Loger.e(TAG, "exception: " + e);
                } finally {
                    typeArray.recycle();
                }
            }
        }
        Loger.d(TAG, "-->initView(), mFps=" + mFps + ", mSplitOrien=" + mSplitOrien + ", mEnableHardwareDecode=" + mEnableHardwareDecode);

        mAnimationView.setEGLContextClientVersion(2);
        AnimationRenderer animationRenderer = new AnimationRenderer(getContext());
        animationRenderer.setSplitOrien(ShadeUtils.VIDEO_SPLIT_HORIZONTAL);
        mAnimationView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mAnimationView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mAnimationView.setZOrderOnTop(true);
        mAnimationView.setRenderer(animationRenderer);
        mAnimationView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mAnimationPlayer = new GiftPlayer(context, this, mEnableHardwareDecode);
//        mAnimationPlayer = mockPlayer;
        mAnimationPlayer.setGiftView(mAnimationView, animationRenderer);
        mAnimationPlayer.setFPS(mFps);
        mAnimationPlayer.setSplitOrien(mSplitOrien); // 视频的切割方向
    }

    private IAnimationPlayer mockPlayer = new IAnimationPlayer() {
        @Override
        public void setGiftView(GiftView textureView, AnimationRenderer animationRenderer) {

        }

        @Override
        public void setFPS(int fps) {

        }

        @Override
        public void setSplitOrien(int splitOrien) {

        }

        @Override
        public void startPlay(File file) {

        }
    };

    public boolean loadLocalAnimation(String filePath) {
        boolean result = false;
        mLocalAnimationPath = filePath;
        if (!TextUtils.isEmpty(filePath)) {
            File animFile = new File(filePath);
            if (animFile.exists()) {
                maskView.showLoadingView();
                mAnimationPlayer.startPlay(animFile);
                result = true;
            }
        }
        Loger.d(TAG, "-->loadLocalAnimation(), filePath=" + filePath + ", result=" + result);
        return result;
    }

    @Override
    public void onVideoRender(int frameIndex) {
        maskView.hide();
//        Loger.d(TAG, "-->onVideoRender(), frameIndex=" + frameIndex);
    }

    @Override
    public void onVideoComplete(PlayState playState) {
        Loger.d(TAG, "-->onVideoComplete(), playState=" + playState);
    }

    @Override
    public void onVideoDestroy() {
        Loger.d(TAG, "-->onVideoDestroy()");
    }

    @Override
    public void onFailed(int errorType, String errorMsg, int codecType) {
        Loger.d(TAG, "-->onFailed(), errorType=" + errorType + ", errorMsg=" + errorMsg + ", codecType=" + codecType);
        maskView.showErrorView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Loger.d(TAG, "-->onDetachedFromWindow()");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Loger.d(TAG, "-->onAttachedToWindow()");
    }

    @Override
    public void onErrorLayerClicked() {
        Loger.d(TAG, "-->onErrorLayerClicked()");
        if (!TextUtils.isEmpty(mLocalAnimationPath)) {
            loadLocalAnimation(mLocalAnimationPath);
        }
    }
}