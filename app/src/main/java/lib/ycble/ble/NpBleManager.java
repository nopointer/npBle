package lib.ycble.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

import no.nordicsemi.android.ble.callback.WriteProgressCallback;
import npble.nopointer.ble.conn.NpBleAbsConnManager;
import npble.nopointer.exception.BleUUIDNullException;
import npble.nopointer.log.ycBleLog;
import npble.nopointer.ota.absimpl.xc.no.nordicsemi.android.BleManagerCallbacks;
import npble.nopointer.util.BleUtil;

public class NpBleManager extends NpBleAbsConnManager implements BleUUIDCfg {
    /**
     * The manager constructor.
     * <p>
     * After constructing the manager, the callbacks object must be set with
     * {@link #setGattCallbacks(BleManagerCallbacks)}.
     * <p>
     * To connect a device, call {@link #connect(BluetoothDevice)}.
     *
     * @param context the context.
     */
    private NpBleManager(Context context) {

        super(context);
    }

    @Override
    public void loadCfg() {
        try {
            setNotificationCallback(U_SER, U_notify);
            enableNotifications(U_SER, U_notify);
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x13,19,12,15});
            byte[] data = createPushMsgContent("哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哦嗯嗯嗯123你好你好任务我偶然和维护费艾特内容给如果以隔热清入关前二个偶尔end", 2);
            writeCharacteristicWithMostPack(U_SER, U_write, data, 0, data.length, new WriteProgressCallback() {
                @Override
                public void onPacketSent(@NonNull BluetoothDevice device, @Nullable byte[] data, int index) {
                    try {
                        ycBleLog.e("onPacketSent ： " + BleUtil.byte2HexStr(data) + "///" + index);
                        writeCharacteristicWithMostPack(U_SER, U_write, data, (index + 1) * 20, 4, this);
                    } catch (BleUUIDNullException e) {
                        e.printStackTrace();
                    }
                }
            });
//            writeCharacteristic(U_SER,U_write,new byte[]{0x13,19,12,14});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x13,19,12,13});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x13,19,12,12});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
        } catch (BleUUIDNullException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    protected void onDataReceive(byte[] data, UUID uuid) {

    }


    private static NpBleManager instance = null;

    public static NpBleManager getInstance(Context context) {
        if (instance == null) {
            instance = new NpBleManager(context);
        }
        return instance;
    }


    public static byte[] createPushMsgContent(String content, int type) {
        ArrayList<byte[]> result = new ArrayList<>();
        int index = 0;
        try {
            byte[] allData = content.getBytes("utf-8");
            int len = allData.length % 17 == 0 ? allData.length / 17 : allData.length / 17 + 1;
            if (len > 10) len = 10;
            for (int i = 0; i < len; i++) {
                byte[] tmp = new byte[20];
                tmp[0] = 0x73;
                tmp[1] = (byte) index++;
                tmp[2] = (byte) type;
                int tmpLen = (allData.length - 17 * i);
                tmpLen = tmpLen >= 17 ? 17 : tmpLen;
                System.arraycopy(allData, 17 * i, tmp, 3, tmpLen);
                result.add(tmp);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (byte[] bytes : result) {
            stringBuilder.append(npble.nopointer.util.BleUtil.byte2HexStr(bytes));
        }
        return BleUtil.hexStr2Byte(stringBuilder.toString());

    }


}
