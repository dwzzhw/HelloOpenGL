package loading.com.helloopengl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import loading.com.helloopengl.utils.Loger;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView mTipsView;
    private Button mTestBtn01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTipsView = findViewById(R.id.tips_area);
        mTestBtn01 = findViewById(R.id.test_btn_01);
        mTestBtn01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTest();
                mTipsView.setText("Test Button 01 is clicked.");
            }
        });
    }

    private void doTest() {
        Loger.d(TAG, "-->doTest()");
        Toast.makeText(this, "Do Test clicked", Toast.LENGTH_SHORT).show();
    }
}
