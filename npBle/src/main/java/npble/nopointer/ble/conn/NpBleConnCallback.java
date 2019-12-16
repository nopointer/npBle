package npble.nopointer.ble.conn;

import npble.nopointer.core.NpBleConnState;

/**
 * Ble连接回调接口
 */
public interface NpBleConnCallback {
    void onConnState(NpBleConnState bleConnState);
}
