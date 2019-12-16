package npble.nopointer.ble.conn.callbacks;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import java.util.UUID;

import no.nordicsemi.android.ble.callback.FailCallback;

/**
 * 数据写失败的回调
 */
public abstract class NpFailCallback implements FailCallback {

    private UUID uuid;
    private byte[] data;

    public NpFailCallback(UUID uuid, byte[] data) {
        this.uuid = uuid;
        this.data = data;
    }

    @Override
    public void onRequestFailed(@NonNull BluetoothDevice device, int status) {
        onRequestFailed(uuid, data, status);
    }

    public abstract void onRequestFailed(UUID uuid, byte[] data, int status);
}
