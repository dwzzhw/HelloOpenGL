package loading.com.helloopengl.mp4animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import loading.com.helloopengl.R;
import loading.com.helloopengl.utils.Loger;
import loading.com.helloopengl.utils.ShadeUtils;

public class AnimationRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "AnimationRenderer";
    private static float squareSize = 1.0f;

    private Context mContext;
    private boolean isSurfaceTextureReady = false;
    private SurfaceTexture mSurfaceTexture;
    private int mCurrentFrameIndex = 0;
    // 纹理切割方向
    private int mSplitOrien;

    public AnimationRenderer(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Loger.d(TAG, "-->onSurfaceCreated()");
        isSurfaceTextureReady = true;
        initGLComponents();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        updateTextureSize(width, height);
        Loger.d(TAG, "-->onSurfaceChanged()");
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
        }

        renderFrame(false);
    }

    public boolean isReady() {
        return isSurfaceTextureReady;
    }

    public void setCurrentFrameIndex(int currentFrameIndex) {
        this.mCurrentFrameIndex = currentFrameIndex;
    }

    private int mVideoWidth;
    private int mVideoHeight;
    private boolean mSurfaceSizeChanged = false;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private float mAnimBoundVertex[] = {
            -squareSize, squareSize,    // top left
            -squareSize, -squareSize,    // bottom left
            squareSize, -squareSize,    // bottom right
            squareSize, squareSize     // top right
    };
    private float mLogoVertex[] = {
            -0.15f, 0.35f,    // top left
            -0.15f, 0.17f,    // bottom left
            0.15f, 0.17f,    // bottom right
            0.15f, 0.35f     // top right
    };

    private static short mVertexDrawOrder[] = {0, 1, 2, 0, 2, 3};

    // 横向切割使用的纹理坐标(注意，不管是两个视频是横向还是竖向排列,rgb视频必须放在第一个)
    private float horizontal_textureCoords_alpha[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            0.5f, 0.0f,
            0.5f, 1.0f};
    private float horizontal_textureCoords_rgb[] = {
            0.5f, 1.0f,
            0.5f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    };

    // 竖向切割使用的纹理坐标
    private float vertical_textureCoords_alpha[] = {
            0.0f, 1.0f,
            0.0f, 0.5f,
            1.0f, 0.5f,
            1.0f, 1.0f};

    private float vertical_textureCoords_rgb[] = {
            0.0f, 0.5f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 0.5f
    };

    //    private float logo_textureCoords[] = {
//            0.0f, 1.0f,
//            0.0f, 0.0f,
//            1.0f, 0.0f,
//            1.0f, 1.0f
//    };
    private float logo_textureCoords[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f
    };

    //GLSL related data
    private int[] mTextures = new int[5];
    private int mShaderProgram;
    private FloatBuffer mVertexBuffer;
    private ShortBuffer mDrawListBuffer;

    private FloatBuffer mLogoVertexBuffer;
    private FloatBuffer mTextureLogoBuffer;
    private FloatBuffer mTextureAlphaBuffer;
    private FloatBuffer mTextureRgbBuffer;
    private byte[] mByteU;

    private int mIsYUVHandle = 0;
    private int mTextureParamHandle;
    private int mTexture2DYHandle = 0;
    private int mTexture2DUHandle = 0;
    private int mTexture2DVHandle = 0;
    private int mTextureCoordinateAlphaHandle;
    private int mTextureCoordinateRgbHandle;
    private int mPositionHandle;

    private int mLogoPositionHandle;
    private int mIsDrawLogoHandle = 0;
    private int mTextureLogoHandle = 0;
    private int mTextureCoordinateLogoHandle;
    private int mPosMatrixHandler = 0;

    private void initGLComponents() {
        setupVertexBuffer();
        setupTextureCoordsBuffer();
        setupTexture();
        setupGraphics();
    }

    private void setupVertexBuffer() {
        mDrawListBuffer = ShadeUtils.createShortByteBuffer(mVertexDrawOrder);
        mVertexBuffer = ShadeUtils.createFloatByteBuffer(mAnimBoundVertex);
        mLogoVertexBuffer = ShadeUtils.createFloatByteBuffer(mLogoVertex);
    }

    private void setupTextureCoordsBuffer() {
        mTextureAlphaBuffer = ShadeUtils.createFloatByteBuffer(isHorizontalSplit() ? horizontal_textureCoords_alpha : vertical_textureCoords_alpha);
        mTextureRgbBuffer = ShadeUtils.createFloatByteBuffer(isHorizontalSplit() ? horizontal_textureCoords_rgb : vertical_textureCoords_rgb);
        mTextureLogoBuffer = ShadeUtils.createFloatByteBuffer(logo_textureCoords);
    }

    private boolean isHorizontalSplit() {
        return mSplitOrien == ShadeUtils.VIDEO_SPLIT_HORIZONTAL;
    }

    private void setupTexture() {
        // 启用纹理
        //GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // 生成纹理对象的textures（用于存储纹理数据）
        GLES20.glGenTextures(5, mTextures, 0);
        checkGlError("glGenTextures");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextures[0]);
        checkGlError("Texture bind");
        ShadeUtils.initCurrentTextureParams();

        // Y分量texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[1]);
        checkGlError("Y Texture bind");
        ShadeUtils.initCurrentTextureParams();

        // U分量texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[2]);
        checkGlError("U Texture bind");
        ShadeUtils.initCurrentTextureParams();

        // V分量texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[3]);
        checkGlError("VTexture bind");
        ShadeUtils.initCurrentTextureParams();

        // Bitmap texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[4]);
        checkGlError("Bitmap Texture bind");
        ShadeUtils.initCurrentTextureParams2(GLES20.GL_REPEAT);

        Bitmap testBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test_icon);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, testBitmap, 0);

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    private void setupGraphics() {
        mShaderProgram = ShadeUtils.createProgram(mContext, "shade/animation_vertex.glsl", "shade/animation_fragment.glsl");

        GLES20.glUseProgram(mShaderProgram);
        mIsYUVHandle = GLES20.glGetUniformLocation(mShaderProgram, "isYUV");
        mTextureParamHandle = GLES20.glGetUniformLocation(mShaderProgram, "texture");
        mTexture2DYHandle = GLES20.glGetUniformLocation(mShaderProgram, "s_texture_2D_Y");
        mTexture2DUHandle = GLES20.glGetUniformLocation(mShaderProgram, "s_texture_2D_U");
        mTexture2DVHandle = GLES20.glGetUniformLocation(mShaderProgram, "s_texture_2D_V");
        mTextureCoordinateAlphaHandle = GLES20.glGetAttribLocation(mShaderProgram, "vTexCoordinateAlpha");
        mTextureCoordinateRgbHandle = GLES20.glGetAttribLocation(mShaderProgram, "vTexCoordinateRgb");
        mPositionHandle = GLES20.glGetAttribLocation(mShaderProgram, "vPosition");

        mLogoPositionHandle = GLES20.glGetAttribLocation(mShaderProgram, "vLogoPosition");
        mTextureCoordinateLogoHandle = GLES20.glGetAttribLocation(mShaderProgram, "vLogoTexCoordinate");
        mIsDrawLogoHandle = GLES20.glGetUniformLocation(mShaderProgram, "isDrawLogoFlag");
        mTextureLogoHandle = GLES20.glGetUniformLocation(mShaderProgram, "texture_logo");
        mPosMatrixHandler = GLES20.glGetUniformLocation(mShaderProgram, "u_Matrix");
    }

    public void updateTextureSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
    }

    /**
     * 设置纹理切割方向
     */
    public void setSplitOrien(int splitOrien) {
        if (mSplitOrien != splitOrien) {
            Loger.i(TAG, "change spliOrien, origin = " + mSplitOrien + ", new splitOrien=" + splitOrien);
            mSplitOrien = splitOrien;
            // 纹理切割方向发生改变，需要重建纹理坐标
            setupTextureCoordsBuffer();
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = new SurfaceTexture(getExternalTexture());
        }
        return mSurfaceTexture;
    }

    public void releaseSurfaceTexture() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
    }

    public boolean isSurfaceTextureExist() {
        return mSurfaceTexture != null;
    }

    public void renderFrame(boolean isYUV) {
//        Loger.d(TAG, "-->renderFrame(), mCurrentFrameIndex=" + mCurrentFrameIndex + ", isYUV=" + isYUV);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // TextureView的Surface尺寸变更时，必须重新设置视口
        if (mSurfaceSizeChanged && mSurfaceWidth > 0 && mSurfaceHeight > 0) {
            Loger.i(TAG, "GLES20.glViewport");
            mSurfaceSizeChanged = false;
            GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
        }
        drawTexture(isYUV);
    }

    public void uploadTextureYUV(byte[] yuv) {
        if (isReady() && yuv != null && mVideoWidth > 0 && mVideoHeight > 0) {
            ByteBuffer yBuffer = ByteBuffer.wrap(yuv, 0, mVideoWidth * mVideoHeight);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[1]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, mVideoWidth, mVideoHeight, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, yBuffer);

            if (this.mByteU == null || this.mByteU.length != mVideoWidth * mVideoHeight / 4) {
                this.mByteU = new byte[mVideoWidth * mVideoHeight / 4];
            }

            yBuffer.position(mVideoWidth * mVideoHeight);
            yBuffer.limit(mVideoWidth * mVideoHeight * 5 / 4);
            yBuffer.get(mByteU);

            ByteBuffer uBuffer = ByteBuffer.wrap(mByteU);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[2]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, mVideoWidth / 2, mVideoHeight / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, uBuffer);

            yBuffer.position(mVideoWidth * mVideoHeight * 5 / 4);
            yBuffer.limit(mVideoWidth * mVideoHeight * 3 / 2);
            yBuffer.get(mByteU);

            ByteBuffer vBuffer = ByteBuffer.wrap(mByteU);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[3]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, mVideoWidth / 2, mVideoHeight / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, vBuffer);

            renderFrame(true);
        }
    }

    private void drawTexture(boolean isYUV) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glUseProgram(mShaderProgram);
        checkGlError("drawTexture:glUseProgram");

        ShadeUtils.assignVertexAttributeFloatArray(mPositionHandle, mVertexBuffer, 2);
        ShadeUtils.assignVertexAttributeFloatArray(mTextureCoordinateAlphaHandle, mTextureAlphaBuffer, 2);
        ShadeUtils.assignVertexAttributeFloatArray(mTextureCoordinateRgbHandle, mTextureRgbBuffer, 2);
        ShadeUtils.assignVertexAttributeFloatArray(mLogoPositionHandle, mLogoVertexBuffer, 2);
        ShadeUtils.assignVertexAttributeFloatArray(mTextureCoordinateLogoHandle, mTextureLogoBuffer, 2);

        GLES20.glUniform1i(mIsYUVHandle, isYUV ? 1 : 0);
        GLES20.glUniform1i(mIsDrawLogoHandle, 0);
        if (!isYUV) {
            uniformRGBTexture();

        }
        uniformLogoTexture();
        // 这里就算走硬解，也必须按drawYUVTexture,否则在drawElements的时候会报0x502错误, 猜测是GLSL中定义的sampler2D必须进行赋值
        uniformYUVTexture();

        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, mVertexDrawOrder.length, GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);
        checkGlError("drawTexture:glDrawElements");

        if (mCurrentFrameIndex > 50 && mCurrentFrameIndex < 80 || mCurrentFrameIndex > 150 && mCurrentFrameIndex < 190) {
            GLES20.glUniform1i(mIsDrawLogoHandle, 1);
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, mVertexDrawOrder.length, GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);
            checkGlError("drawTexture:glDrawElements 2");
        }

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateAlphaHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateRgbHandle);
        GLES20.glDisableVertexAttribArray(mLogoPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateLogoHandle);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glUseProgram(0);
    }

    private void uniformRGBTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        checkGlError("uniformRGBTexture:glActiveTexture");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextures[0]);
        checkGlError("uniformRGBTexture:glGenTextures");
        GLES20.glUniform1i(mTextureParamHandle, 0);
        checkGlError("uniformRGBTexture:glUniform1i");
    }

    private void uniformYUVTexture() {
        ShadeUtils.assign2DTextureRef(GLES20.GL_TEXTURE1, mTextures[1], mTexture2DYHandle, 1);
        ShadeUtils.assign2DTextureRef(GLES20.GL_TEXTURE2, mTextures[2], mTexture2DUHandle, 2);
        ShadeUtils.assign2DTextureRef(GLES20.GL_TEXTURE3, mTextures[3], mTexture2DVHandle, 3);
    }

    private void uniformLogoTexture() {
        ShadeUtils.assign2DTextureRef(GLES20.GL_TEXTURE4, mTextures[4], mTextureLogoHandle, 4);
    }

    public void updateEglSurfaceSize(int width, int height) {
        mSurfaceSizeChanged = true;
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Loger.e(TAG, op + ": glError " + ShadeUtils.getEGLErrorString(error));
        }
    }

    public void clear() {
        Loger.d(TAG, "dwz-->clear()");
//        renderFrame(false);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    public void release() {
        deleteTextures();
        mTextures = null;
    }

    void deleteTextures() {
        if (mTextures != null) {
            GLES20.glDeleteTextures(4, mTextures, 0);
        }
    }

    public int getExternalTexture() {
        return mTextures[0];
    }
}
