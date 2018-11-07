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

package loading.com.helloopengl.apidemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import loading.com.helloopengl.R;
import loading.com.helloopengl.utils.Loger;

/**
 * This sample shows how to check for OpenGL ES 2.0 support at runtime, and then
 * use either OpenGL ES 1.0 or OpenGL ES 2.0, as appropriate.
 */
public class GLES20Activity extends Activity {
    private static final String TAG = "GLES20Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_gles20);
        mGLSurfaceView = findViewById(R.id.glsurface_01);
        mGLSurfaceView02 = findViewById(R.id.glsurface_02);
        Loger.d(TAG, "dwz-->onCreate(), mGLSurfaceView=" + mGLSurfaceView + ", mGLSurfaceView02=" + mGLSurfaceView02);

//        mGLSurfaceView = new GLSurfaceView(this);
        if (detectOpenGLES20()) {
            // Tell the surface view we want to create an OpenGL ES 2.0-compatible
            // context, and set an OpenGL ES 2.0-compatible renderer.
            mGLSurfaceView.setEGLContextClientVersion(2);
            mGLSurfaceView02.setEGLContextClientVersion(2);
            GLES20TriangleRenderer renderer = new GLES20TriangleRenderer(this, false);
            mGLSurfaceView.setRenderer(renderer);
            GLES20TriangleRenderer renderer02 = new GLES20TriangleRenderer(this, true);
            mGLSurfaceView02.setRenderer(renderer02);
        } else {
            // Set an OpenGL ES 1.x-compatible renderer. In a real application
            // this renderer might approximate the same output as the 2.0 renderer.
            mGLSurfaceView.setRenderer(new TriangleRenderer(this));
        }

//        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//        setContentView(mGLSurfaceView);
    }

    private boolean detectOpenGLES20() {
        ActivityManager am =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return (info.reqGlEsVersion >= 0x20000);
    }

    @Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        mGLSurfaceView.onResume();
        mGLSurfaceView02.onResume();
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        mGLSurfaceView.onPause();
        mGLSurfaceView02.onPause();
    }

    private GLSurfaceView mGLSurfaceView;
    private GLSurfaceView mGLSurfaceView02;
}
