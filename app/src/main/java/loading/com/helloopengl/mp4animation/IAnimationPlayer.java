package loading.com.helloopengl.mp4animation;

import java.io.File;

import loading.com.helloopengl.mp4animation.view.GiftView;

public interface IAnimationPlayer {
    void setGiftView(GiftView textureView, AnimationRenderer animationRenderer);

    void setFPS(int fps);

    void setSplitOrien(int splitOrien);

    void startPlay(final File file);
}
