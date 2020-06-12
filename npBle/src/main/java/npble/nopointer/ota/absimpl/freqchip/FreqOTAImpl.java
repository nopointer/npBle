package npble.nopointer.ota.absimpl.freqchip;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import npble.nopointer.ble.conn.NpBleAbsConnManager;
import npble.nopointer.exception.NpBleUUIDNullException;
import npble.nopointer.log.NpBleLog;
import npble.nopointer.ota.NpOtaErrCode;
import npble.nopointer.ota.callback.NpOtaCallback;
import npble.nopointer.util.BleUtil;

import static npble.nopointer.ota.absimpl.freqchip.FreqchipUtils.OTA_CMD_WRITE_DATA;

class FreqOTAImpl extends NpBleAbsConnManager implements FreqBleCfg {

    private String filePath = null;

    private NpOtaCallback otaCallback;


    private long leng;

    private FileInputStream isfile = null;
    private InputStream input;

    private int firstaddr = 0;
    private byte[] baseaddr = null;
    private int sencondaddr = 0x14000;
    private int recv_data;
    private int writePrecent;
    private boolean writeStatus = false;


    private MyHandler myHandler = new MyHandler();

    public FreqOTAImpl(Context context) {
        super(context);
        setMustUUID(UUID_OTA_SEND_DATA);
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setOtaCallback(NpOtaCallback otaCallback) {
        this.otaCallback = otaCallback;
    }

    @Override
    public void onFinishTaskAfterConn() {
        if (verifyFile()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        doSendFileByBluetooth(filePath);
                        myHandler.sendEmptyMessage(1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (NpBleUUIDNullException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public void onDataReceive(byte[] data, UUID uuid) {
        NpBleLog.log("接收到数据" + BleUtil.byte2HexStr(data));
        baseaddr = data;
        setRecv_data(1);
    }

    @Override
    protected void onBeforeWriteData(UUID uuid, byte[] data) {

    }

    @Override
    public void loadCfg() {
        try {
            setNotificationCallback(UUID_OTA_SERVICE, UUID_OTA_RECV_DATA);
            enableNotifications(UUID_OTA_SERVICE, UUID_OTA_RECV_DATA);
        } catch (NpBleUUIDNullException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnException() {

    }

    @Override
    protected void onDataWriteSuccess(UUID uuid, byte[] data) {

    }

    @Override
    protected void onDataWriteFail(UUID uuid, byte[] data, int status) {

    }



    /**
     * 验证OTA文件
     *
     * @return
     */
    private boolean verifyFile() {
        InputStream input;
        byte[] Buffer = new byte[4];
        File file = new File(filePath);
        try {
            FileInputStream infile = new FileInputStream(file);
            try {
                infile.skip(0x167);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            input = new BufferedInputStream(infile);
            try {
                input.read(Buffer, 0, 4);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        System.out.println("buffer: " + Buffer[0] + " " + Buffer[1]);
        if ((Buffer[0] != 0x52) || (Buffer[1] != 0x51) || (Buffer[2] != 0x51) || (Buffer[3] != 0x52)) {
            if (otaCallback != null) {
                otaCallback.onFailure(NpOtaErrCode.FWK_FILE_INVALIDE, "文件校验不通过");
            }
            return false;
        }
        if (file.length() < 100) {
            if (otaCallback != null) {
                otaCallback.onFailure(NpOtaErrCode.FWK_FILE_INVALIDE, "文件校验不通过");
            }
            return false;
        }
        return true;
    }


    public void doSendFileByBluetooth(String filePath)
            throws FileNotFoundException, NpBleUUIDNullException {
        if (!filePath.equals(null)) {
            int read_count;
            int i = 0;
            int addr;
            int delay_num;
            int lastReadCount = 0;
            int packageSize = 235;
            long send_times;
            int send_offset;
            int send_each_count = 300;
            int send_data_count = 0;
            byte[] inputBuffer = new byte[packageSize];
            File file = new File(filePath);// 成文件路径中获取文件
            isfile = new FileInputStream(file);
            leng = file.length();
            send_times = leng / send_each_count;
            send_offset = (int) (leng % send_each_count);
            input = new BufferedInputStream(isfile);
            setRecv_data(0);
            send_data(FreqchipUtils.OTA_CMD_GET_STR_BASE, 0, null, 0);

            while (getRecv_data() != 1) {
                if (checkDisconnect()) {
                    return;
                }
            }

            if (FreqchipUtils.bytetoint(baseaddr) == firstaddr) {
                addr = sencondaddr;
            } else {
                addr = firstaddr;
            }
            setRecv_data(0);
            page_erase(addr, leng);
            try {
                while (((read_count = input.read(inputBuffer, 0, packageSize)) != -1)) {
                    send_data(OTA_CMD_WRITE_DATA, addr, inputBuffer, read_count);
                    //for(delay_num = 0;delay_num < 10000;delay_num++);
                    addr += read_count;
                    lastReadCount = read_count;
                    send_data_count += read_count;
                    //System.out.println("times" + i + " " + read_count);
                    i++;
                    writePrecent = (int) (((float) send_data_count / leng) * 100);
                    //进度
                    myHandler.sendEmptyMessage(1);
                    while (!writeStatus) ;
                    writeStatus = false;
                    while (getRecv_data() != 1) {
                        if (checkDisconnect()) {
                            return;
                        }
                    }
                    setRecv_data(0);
                }
                while (FreqchipUtils.bytetoint(baseaddr) != (addr - lastReadCount)) {
                    if (checkDisconnect()) {
                        return;
                    }
                }
                send_data(FreqchipUtils.OTA_CMD_REBOOT, 0, null, 0);
                myHandler.sendEmptyMessage(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            NpBleLog.log("请选择要发送的文件");
        }
    }

    private boolean send_data(int type, int addr, byte[] buffer, int length) throws NpBleUUIDNullException {
        byte[] cmd_write = null;
        byte[] result_cmd = null;
        byte[] cmd = new byte[1];
        cmd_write = FreqchipUtils.cmd_operation(type, length, addr);
        if ((type == FreqchipUtils.OTA_CMD_GET_STR_BASE) || ((type == FreqchipUtils.OTA_CMD_PAGE_ERASE))) {
            result_cmd = cmd_write;
        } else if (type == FreqchipUtils.OTA_CMD_REBOOT) {
            cmd[0] = (byte) (type & 0xff);
            result_cmd = cmd;
        } else {
            result_cmd = FreqchipUtils.byteMerger(cmd_write, buffer);
        }
        return writeData(result_cmd);

    }


    private boolean writeData(byte[] data) {
        try {
            writeCharacteristicWithOutResponse(UUID_OTA_SERVICE, UUID_OTA_SEND_DATA, data);
            return true;
        } catch (NpBleUUIDNullException e) {
            e.printStackTrace();
            return false;
        }
    }


    public int getRecv_data() {
        return recv_data;
    }

    public void setRecv_data(int recv_data) {
        this.recv_data = recv_data;
    }

    private int page_erase(int addr, long length) throws NpBleUUIDNullException {

        long count = length / 0x1000;
        if ((length % 0x1000) != 0) {
            count++;
        }
        for (int i = 0; i < count; i++) {
            send_data(FreqchipUtils.OTA_CMD_PAGE_ERASE, addr, null, 0);
            while (getRecv_data() != 1) ;
            setRecv_data(0);
            addr += 0x1000;
        }
        return 0;
    }

    boolean checkDisconnect() {
        if (!isConnected()) {
            myHandler.sendEmptyMessage(2);
            return true;
        }
        return false;
    }


    private class MyHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (otaCallback != null) {
                        otaCallback.onSuccess();
                    }
                    break;
                case 1:
                    if (otaCallback != null) {
                        otaCallback.onProgress(writePrecent);
                    }
                    break;
                case 2:
                    if (otaCallback != null) {
                        otaCallback.onFailure(NpOtaErrCode.LOST_CONN, "disconnected");
                    }
                    break;
                default:
                    break;
            }
        }
    }


}
