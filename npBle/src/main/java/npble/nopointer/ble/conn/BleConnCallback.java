package npble.nopointer.ble.conn;

import npble.nopointer.core.BleConnState;

/**
 * Ble连接回调接口
 */
public interface BleConnCallback {
    void onConnState(BleConnState bleConnState);
}
