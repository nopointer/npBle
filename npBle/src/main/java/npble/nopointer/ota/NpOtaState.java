package npble.nopointer.ota;

public enum NpOtaState {

    /**
     * 连接
     */
    connecting,
    /**
     * 开始
     */
    starting,
    /**
     * 连接
     */
    switching_to_dfu,
    /**
     * 连接
     */
    dfu_uploading,

}
