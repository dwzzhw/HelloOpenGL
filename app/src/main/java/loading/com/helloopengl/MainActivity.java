package loading.com.helloopengl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import loading.com.helloopengl.apidemo.GLES20Activity;
import loading.com.helloopengl.mp4animation.InnerGLESActivity;
import loading.com.helloopengl.utils.Loger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private TextView mTipsView;
    private Button mTestBtn01;
    private Button mTestBtn02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTipsView = findViewById(R.id.tips_area);
        mTestBtn01 = findViewById(R.id.test_btn_01);
        mTestBtn01.setOnClickListener(this);
        mTestBtn02 = findViewById(R.id.test_btn_02);
        mTestBtn02.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_btn_01:
                openGLES20PageFromApiDemo();
                mTipsView.setText("Test Button 01 is clicked.");
                break;
            case R.id.test_btn_02:
                openInnerGLESPage();
                mTipsView.setText("Test Button 02 is clicked.");
                break;
        }
    }

    private void openGLES20PageFromApiDemo() {
        Loger.d(TAG, "-->openGLES20PageFromApiDemo()");
        Toast.makeText(this, "openGLES20PageFromApiDemo", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, GLES20Activity.class);
        startActivity(intent);
    }

    private void openInnerGLESPage() {
        Loger.d(TAG, "-->openInnerGLESPage()");
        Toast.makeText(this, "openInnerGLESPage", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, InnerGLESActivity.class);
        startActivity(intent);
    }
}
