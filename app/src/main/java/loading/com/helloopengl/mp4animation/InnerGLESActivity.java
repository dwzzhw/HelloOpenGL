/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package loading.com.helloopengl.mp4animation;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import loading.com.helloopengl.R;
import loading.com.helloopengl.mp4animation.view.AnimationView;
import loading.com.helloopengl.utils.Loger;

public class InnerGLESActivity extends Activity {
    private static final String TAG = "InnerGLESActivity";
    private Button mRenderBtn;
    private TextureView mTextureView;

    // TextureRender是否已初始化
//    private boolean mTexturerRenderInited = false;
//    private TexturerRender mTexturerRender;
//    private SurfaceTexture mOutputSurface;
    int mVideoWidth;
    int mVideoHeight;
    private AnimationView mAnimationView;
    public String[] mDemoName = {"demo.mp4", "Demo1.mp4", "champion.mp4", "file_lion.mp4"};
    private boolean mCanPlay;
    public static String DIR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_inner_gles);
        Loger.d(TAG, "dwz-->onCreate()");

        mRenderBtn = findViewById(R.id.render_btn);
        mTextureView = findViewById(R.id.target_texture_view);
        mRenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualRender();
            }
        });
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Loger.d(TAG, "-->onSurfaceTextureAvailable(), surface=" + surface + ", width=" + width + ", height=" + height);
                updateTextureSize(width, height);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                Loger.d(TAG, "-->onSurfaceTextureSizeChanged(), surface=" + surface + ", width=" + width + ", height=" + height);
                updateTextureSize(width, height);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                Loger.d(TAG, "-->onSurfaceTextureDestroyed()");
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                Loger.d(TAG, "-->onSurfaceTextureUpdated()");
            }
        });

        mAnimationView = findViewById(R.id.my_anim_view);
        initAnimationView();
    }

    private void manualRender() {
        Loger.d(TAG, "-->manualRender()");
//        initTexturerRender();

//        if (mTexturerRender != null) {
//            mTexturerRender.renderFrame(false);
//        }
    }

    private void initAnimationView() {
        DIR = getExternalFilesDir(null).getAbsolutePath();
        mCanPlay = false;
        mAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCanPlay) {
                    mAnimationView.loadLocalAnimation(DIR + "/" + mDemoName[2]);
//                    mAnimationView.loadAnimationFromUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536838839670&di=4f9a6369cbfa1446bde8e3d2132448dd&imgtype=0&src=http%3A%2F%2Fb.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2F838ba61ea8d3fd1fb0502abc354e251f94ca5fdf.jpg");

                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                copyAssetsToStorage();
            }
        }).start();
    }

//    void initTexturerRender() {
//        if (!mTexturerRenderInited || mTexturerRender == null) {
//            mTexturerRenderInited = true;
//            mTexturerRender = new TexturerRender(this, 1);
//            mTexturerRender.init(mTextureView.getSurfaceTexture());
//        } else {
//            mTexturerRender.setSplitOrien(1);
//        }
//        mTexturerRender.updateTextureSize(mVideoWidth, mVideoHeight);
//    }

    void updateTextureSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
    }

    private void copyAssetsToStorage() {
        OutputStream outputStream;
        InputStream inputStream;
        byte[] buf = new byte[4096];
        int len;
        for (String name : mDemoName) {
            try {
                inputStream = getAssets().open(name);
                outputStream = new FileOutputStream(DIR + "/" + name);
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mCanPlay = true;
    }
}
