package loading.com.helloopengl.mp4animation;

/**
 * 视频播放回调
 */

public interface PlayCallback {
    void onVideoRender(int frameIndex);

    void onVideoComplete(PlayState playState);

    void onVideoDestroy();

    void onFailed(int errorType, String errorMsg, int codecType);
}
