package npble.nopointer.ble.conn.callbacks;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import java.util.UUID;

import no.nordicsemi.android.ble.callback.SuccessCallback;

/**
 * 数据写成功的回调
 */
public abstract class NpSuccessCallback implements SuccessCallback {

    private UUID uuid;
    private byte[] data;

    public NpSuccessCallback(UUID uuid, byte[] data) {
        this.uuid = uuid;
        this.data = data;
    }

    @Override
    public void onRequestCompleted(@NonNull BluetoothDevice device) {
        onRequestCompleted(uuid, data);
    }

    public abstract void onRequestCompleted(UUID uuid, byte[] data);
}
