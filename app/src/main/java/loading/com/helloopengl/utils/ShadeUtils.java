package loading.com.helloopengl.utils;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;

public class ShadeUtils {
    private static final String TAG = "ShadeUtils";
    /*************** 视频切割方向 **************************/
    // 横向切割
    public static final int VIDEO_SPLIT_HORIZONTAL = 1;
    // 纵向切割
    public static final int VIDEO_SPLIT_VERTICAL = 2;

    public static int createProgram(Context context, String vertexResAssetPath, String fragmentResAssetPath) {
        return createProgram(Utils.readAssertResource(context, vertexResAssetPath), Utils.readAssertResource(context, fragmentResAssetPath));
    }

    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShade = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        int fragmentShade = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        int programHandler = GLES20.glCreateProgram();
        if (programHandler != 0 && vertexShade != 0 && fragmentShade != 0) {
            GLES20.glAttachShader(programHandler, vertexShade);
            GLES20.glAttachShader(programHandler, fragmentShade);
            GLES20.glLinkProgram(programHandler);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandler, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == GLES20.GL_FALSE) {
                Log.e(TAG, "-->createProgram(), compiling program fail:" + GLES20.glGetProgramInfoLog(programHandler));
                GLES20.glDeleteProgram(programHandler);
                programHandler = 0;
            }
        }
        if (programHandler == 0) {
            throw new RuntimeException("Fail to creating program.");
        }
        return programHandler;
    }

    public static int loadShader(int shaderType, String source) {
        int shaderHandler = GLES20.glCreateShader(shaderType);
        if (shaderHandler != 0) {
            GLES20.glShaderSource(shaderHandler, source);
            GLES20.glCompileShader(shaderHandler);
            // 获取编译状态
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandler, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == GLES20.GL_FALSE) {
                Loger.e(TAG, "-->loadShader() fail, shaderType=" + shaderType + ", error msg: " + GLES20.glGetShaderInfoLog(shaderHandler));
                GLES20.glDeleteShader(shaderHandler);
                shaderHandler = 0;
            }
        }
        if (shaderHandler == 0) {
            throw new RuntimeException("Fail to creating shader.");
        }
        return shaderHandler;
    }

    public static FloatBuffer createFloatByteBuffer(float[] floatArray) {
        FloatBuffer floatBuffer = null;
        if (floatArray != null && floatArray.length > 0) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(floatArray.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            floatBuffer = byteBuffer.asFloatBuffer();
            floatBuffer.put(floatArray);
            floatBuffer.position(0);
        }
        return floatBuffer;
    }

    public static ShortBuffer createShortByteBuffer(short[] shortArray) {
        ShortBuffer shortBuffer = null;
        if (shortArray != null && shortArray.length > 0) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(shortArray.length * 2);
            byteBuffer.order(ByteOrder.nativeOrder());
            shortBuffer = byteBuffer.asShortBuffer();
            shortBuffer.put(shortArray);
            shortBuffer.position(0);
        }
        return shortBuffer;
    }

    public static void initCurrentTextureParams() {
        initCurrentTextureParams2(GLES20.GL_CLAMP_TO_EDGE);
    }

    public static void initCurrentTextureParams2(int borderWrapStyle) {
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, borderWrapStyle);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, borderWrapStyle);
    }


    public static void assignVertexAttributeFloatArray(int arrayHandler, FloatBuffer arrayBuffer, int vertexSize) {
        GLES20.glEnableVertexAttribArray(arrayHandler);
        checkGlError("assignVertexAttributeFloatArray:glEnableVertexAttribArray, handler=" + arrayBuffer);
        GLES20.glVertexAttribPointer(arrayHandler, vertexSize, GLES20.GL_FLOAT, false, 0, arrayBuffer);
        checkGlError("assignVertexAttributeFloatArray:glVertexAttribPointer, handler=" + arrayBuffer);
    }

    /**
     * 绑定Texture引用内容
     *
     * @param textureRefId   如:  GLES20.GL_TEXTURE2
     * @param textureResId   如:  mTextures[2]
     * @param textureHandler 如:  mTextureLogoHandle
     * @param index          如:  2, 与textureRefId对应
     */
    public static void assign2DTextureRef(int textureRefId, int textureResId, int textureHandler, int index) {
        GLES20.glActiveTexture(textureRefId);
        ShadeUtils.checkGlError("assign2DTextureRef:glActiveTexture, texture index=" + index);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureResId);
        ShadeUtils.checkGlError("assign2DTextureRef:glGenTextures, texture index=" + index);
        GLES20.glUniform1i(textureHandler, index);
        ShadeUtils.checkGlError("assign2DTextureRef:glUniform1i, texture index=" + index);
    }

    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Loger.e(TAG, "-->checkGlError(), " + op + ": glError " + getEGLErrorString(error));
        }
    }

    /**
     * 返回EGL 错误码对应的错误信息
     *
     * @param error EGL错误码
     * @return 错误信息
     */
    public static String getEGLErrorString(int error) {
        switch (error) {
            case EGL10.EGL_SUCCESS:
                return "EGL_SUCCESS";
            case EGL10.EGL_NOT_INITIALIZED:
                return "EGL_NOT_INITIALIZED";
            case EGL10.EGL_BAD_ACCESS:
                return "EGL_BAD_ACCESS";
            case EGL10.EGL_BAD_ALLOC:
                return "EGL_BAD_ALLOC";
            case EGL10.EGL_BAD_ATTRIBUTE:
                return "EGL_BAD_ATTRIBUTE";
            case EGL10.EGL_BAD_CONFIG:
                return "EGL_BAD_CONFIG";
            case EGL10.EGL_BAD_CONTEXT:
                return "EGL_BAD_CONTEXT";
            case EGL10.EGL_BAD_CURRENT_SURFACE:
                return "EGL_BAD_CURRENT_SURFACE";
            case EGL10.EGL_BAD_DISPLAY:
                return "EGL_BAD_DISPLAY";
            case EGL10.EGL_BAD_MATCH:
                return "EGL_BAD_MATCH";
            case EGL10.EGL_BAD_NATIVE_PIXMAP:
                return "EGL_BAD_NATIVE_PIXMAP";
            case EGL10.EGL_BAD_NATIVE_WINDOW:
                return "EGL_BAD_NATIVE_WINDOW";
            case EGL10.EGL_BAD_PARAMETER:
                return "EGL_BAD_PARAMETER";
            case EGL10.EGL_BAD_SURFACE:
                return "EGL_BAD_SURFACE";
            case EGL11.EGL_CONTEXT_LOST:
                return "EGL_CONTEXT_LOST";
            default:
                return "0x" + Integer.toHexString(error);
        }
    }
}
