package demo.np.deviceuicustom.ble.imageTransport;

/**
 * 颜色配置
 */
public enum ColorCfg {
    /**
     * 二进制文件，不需要计算大小，直接取文件大小
     */
    BIN_FILE,
    /**
     * 32位 4个字节
     */
    ARGB_8888,
    /**
     * 16为 2个字节
     */
    RGB_556,
}
