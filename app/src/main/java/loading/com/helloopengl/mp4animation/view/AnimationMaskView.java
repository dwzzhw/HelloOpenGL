package loading.com.helloopengl.mp4animation.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import loading.com.helloopengl.R;
import loading.com.helloopengl.utils.Loger;
import loading.com.helloopengl.utils.SystemUtil;
import loading.com.helloopengl.utils.UiThreadUtil;

public class AnimationMaskView extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "AnimationMaskView";
    private TextView mTipsView;

    private IOnErrorLayerClickListener mErrorClickListener;

    public AnimationMaskView(@NonNull Context context) {
        this(context, null);
    }

    public AnimationMaskView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationMaskView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.animation_mask_layout, this, true);
        mTipsView = (TextView) findViewById(R.id.gif_player_tips_view);
    }

    public void showLoadingView() {
        Loger.d(TAG, "-->showLoadingView()");
        updateTipsTxtAndVisibility("加载中...", true);
    }

    public void showErrorView() {
        Loger.d(TAG, "-->showErrorView()");
        updateTipsTxtAndVisibility("点击重试", true);
    }

    private void updateTipsTxtAndVisibility(final String tipsTxt, final boolean visible) {
        Loger.d(TAG, "-->updateTipsTxtAndVisibility(), tipsTxt=" + tipsTxt + ", visible=" + visible);
        if (SystemUtil.isMainThread()) {
            if (mTipsView != null) {
                mTipsView.setText(tipsTxt);
            }
            setCompVisibility(visible);
        } else {
            UiThreadUtil.postRunnable(new Runnable() {
                @Override
                public void run() {
                    if (mTipsView != null) {
                        mTipsView.setText(tipsTxt);
                    }
                    setCompVisibility(visible);
                }
            });
        }
    }

    public void hide() {
        if (getVisibility() != View.GONE) {
            updateTipsTxtAndVisibility(null, false);
        }
    }

    private void setCompVisibility(boolean visible) {
        final int targetValue = visible ? View.VISIBLE : View.GONE;
        if (getVisibility() != targetValue) {
            Loger.d(TAG, "-->setCompVisibility(), visible=" + visible);
            if (SystemUtil.isMainThread()) {
                setVisibility(targetValue);
            } else {
                UiThreadUtil.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        setVisibility(targetValue);
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mErrorClickListener != null) {
            mErrorClickListener.onErrorLayerClicked();
        }
    }

    public void setErrorClickListener(IOnErrorLayerClickListener errorClickListener) {
        this.mErrorClickListener = errorClickListener;
    }

    public interface IOnErrorLayerClickListener {
        void onErrorLayerClicked();
    }
}
