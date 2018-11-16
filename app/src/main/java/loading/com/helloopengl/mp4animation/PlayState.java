package loading.com.helloopengl.mp4animation;

/**
 * 播放状态相关参数
 */

public class PlayState {
    public int codecType; // 解码器类型
    public int avgFps; // 平均fps

    public PlayState(int codecType, int avgFps) {
        this.codecType = codecType;
        this.avgFps = avgFps;
    }
}
