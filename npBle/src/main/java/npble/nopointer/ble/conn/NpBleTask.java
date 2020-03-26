package npble.nopointer.ble.conn;

import java.util.Arrays;
import java.util.UUID;

import no.nordicsemi.android.ble.Request;
import no.nordicsemi.android.ble.WriteRequest;

public class NpBleTask {

    private Request request;
    private UUID uuid;
    private byte[] data;
    private String msg;


    public NpBleTask() {
    }

    public NpBleTask(UUID uuid, byte[] data) {
        this.uuid = uuid;
        this.data = data;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    @Override
    public String toString() {
        return "BleTask{" +
                "request=" + request +
                ", uuid=" + uuid +
                ", data=" + Arrays.toString(data) +
                '}';
    }

    public static NpBleTask createWriteTask(WriteRequest writeRequest, UUID uuid, byte[] data, String... msg) {
        NpBleTask task = new NpBleTask(uuid, data);
        task.setRequest(writeRequest);
        if (msg != null && msg.length > 0) {
            task.setMsg(msg[0]);
        }
        return task;
    }

    public static NpBleTask createEnableNotifyTask(WriteRequest writeRequest, UUID uuid, String... msg) {
        NpBleTask task = new NpBleTask();
        task.setUuid(uuid);
        task.setRequest(writeRequest);
        if (msg != null && msg.length > 0) {
            task.setMsg(msg[0]);
        }
        return task;
    }
}
