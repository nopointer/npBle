package npble.nopointer.ble.conn;

import java.util.Arrays;
import java.util.UUID;

import no.nordicsemi.android.ble.Request;
import no.nordicsemi.android.ble.WriteRequest;

public class BleTask {

    private Request request;
    private UUID uuid;
    private byte[] data;
    private String msg;


    public BleTask() {
    }

    public BleTask(UUID uuid, byte[] data) {
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

    public static BleTask createWriteTask(WriteRequest writeRequest, UUID uuid, byte[] data, String... msg) {
        BleTask task = new BleTask(uuid, data);
        task.setRequest(writeRequest);
        if (msg != null && msg.length > 0) {
            task.setMsg(msg[0]);
        }
        return task;
    }

    public static BleTask createEnableNotifyTask(WriteRequest writeRequest, UUID uuid,  String... msg) {
        BleTask task = new BleTask();
        task.setUuid(uuid);
        task.setRequest(writeRequest);
        if (msg != null && msg.length > 0) {
            task.setMsg(msg[0]);
        }
        return task;
    }
}
