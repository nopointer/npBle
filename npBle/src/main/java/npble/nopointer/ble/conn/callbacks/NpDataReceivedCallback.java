package npble.nopointer.ble.conn.callbacks;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import java.util.UUID;

import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;

public abstract class NpDataReceivedCallback implements DataReceivedCallback {

    private UUID uuid;

    public NpDataReceivedCallback(UUID uuid) {
        this.uuid = uuid;
    }


    @Override
    public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
        onDataReceived(device, data, uuid);
    }

    public abstract void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data, UUID uuid);
}
