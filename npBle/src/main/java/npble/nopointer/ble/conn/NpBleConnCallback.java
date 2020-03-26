package npble.nopointer.ble.conn;

/**
 * Ble连接回调接口
 */
public interface NpBleConnCallback {
    void onConnState(NpBleConnState bleConnState);
}
