package npble.nopointer.ble.conn.callbacks;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import java.util.UUID;

import no.nordicsemi.android.ble.callback.DataSentCallback;
import no.nordicsemi.android.ble.data.Data;

public abstract class NpDataSentCallback implements DataSentCallback {


    private UUID uuid;

    public NpDataSentCallback(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data) {
        onDataSent(device, data, uuid);
    }

    public abstract void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data, UUID uuid);


}
