package demo.np.deviceuicustom.ble.imageTransport;

/**
 * 设备的图片传输回调
 */
public interface DevImageTransportCallback {

    /**
     * 准备好了
     */
    public void onReady();

    /**
     * 传输结束
     */
    public void onFinish();

    /**
     * 当前进度
     *
     * @param progress
     */
    public void onProgress(float progress);

}
