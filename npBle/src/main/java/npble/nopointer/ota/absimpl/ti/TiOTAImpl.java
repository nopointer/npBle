package npble.nopointer.ota.absimpl.ti;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import npble.nopointer.ble.conn.NpBleAbsConnManager;
import npble.nopointer.exception.NpBleUUIDNullException;
import npble.nopointer.log.NpBleLog;
import npble.nopointer.ota.callback.NpOtaCallback;
import npble.nopointer.util.BleUtil;

import static npble.nopointer.ota.NpOtaErrCode.TI_FAILURE;


class TiOTAImpl extends NpBleAbsConnManager implements TIBleCfg {


    private static final int OAD_IMG_HDR_SIZE = 8;
    private static final int OAD_BLOCK_SIZE = 16;
    private static final int FILE_BUFFER_SIZE = 0x40000;
    private static final int OAD_BUFFER_SIZE = 2 + OAD_BLOCK_SIZE;
    private static final int HAL_FLASH_WORD_SIZE = 4;


    private String filePath = null;

    private NpOtaCallback otaCallback;
    private ImgHdr mFileImgHdr = new ImgHdr();

    private byte[] mFileBuffer = new byte[FILE_BUFFER_SIZE];

    private final byte[] mOadBuffer = new byte[OAD_BUFFER_SIZE];

    public TiOTAImpl(Context context) {
        super(context);
        setMustUUID(UUID_OTA_SEND_DATA);
    }

    private boolean mProgramming = false;

    private ProgInfo mProgInfo = new ProgInfo();
    private Handler handler = new Handler();
    private int imageDataIndex = 0;
    private boolean isSuccess = false;


    private byte[] imageByes = null;

    public void setImageByes(byte[] imageByes) {
        this.imageByes = imageByes;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setOtaCallback(NpOtaCallback otaCallback) {
        this.otaCallback = otaCallback;
    }

    @Override
    public void onFinishTaskAfterConn() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadFile();
            }
        }, 500);

    }

    @Override
    public void onDataReceive(byte[] data, UUID uuid) {
//        ycBleLog.e("接收到数据" + uuid.toString() + "//" + BleUtil.byte2HexStr(data));
//        if (uuid.equals(UUID_OTA_RECV_DATA)) {
//
//        } else if (uuid.equals(UUID_OTA_SEND_DATA)) {
//            if (mProgramming) {
////                postDta(data);
//            }
//        }
    }


    @Override
    protected void onBeforeWriteData(UUID uuid, byte[] data) {

    }

    @Override
    public void loadCfg() {
//        addBleUnitTask(BleUnitTask.createEnableNotify(UUID_OTA_SERVICE, UUID_OTA_SEND_DATA, "打开通知"));
//        addBleUnitTask(BleUnitTask.createEnableNotify(UUID_OTA_SERVICE, UUID_OTA_SEND_DATA, "打开通知"));
//        addBleUnitTask(BleUnitTask.createEnableNotify(UUID_OTA_SERVICE, UUID_OTA_RECV_DATA, "打开通知"));
//        addBleUnitTask(BleUnitTask.createWriteWithOutResp(UUID_OTA_SERVICE, UUID_OTA_RECV_DATA, new byte[]{0}, "查询版本"));
//        addBleUnitTask(BleUnitTask.createWriteWithOutResp(UUID_OTA_SERVICE, UUID_OTA_RECV_DATA, new byte[]{1}, "查询版本"));
    }

    @Override
    public void onConnException() {
        if (otaCallback != null) {
            if (!isSuccess) {
                otaCallback.onFailure(TI_FAILURE, "connException");
            }
        }

    }

    @Override
    protected void onDataWriteSuccess(UUID uuid, byte[] data) {
        if (mProgramming) {
            postDta(imageDataIndex++);
        }
    }

    @Override
    protected void onDataWriteFail(UUID uuid, byte[] data, int status) {

    }


    public void startOTA(String mac) {
        connDevice(mac);
    }

    public void stopOTA() {
        mProgramming = false;
        isSuccess = false;
        disConnectDevice();
    }

    private void start() {
        byte[] buf = new byte[OAD_IMG_HDR_SIZE + 2 + 2];
        buf[0] = (byte) (mFileImgHdr.ver & 0xff);
        buf[1] = (byte) (mFileImgHdr.ver >> 8);
        buf[2] = (byte) (mFileImgHdr.len & 0xff);
        buf[3] = (byte) (mFileImgHdr.len >> 8);
        System.arraycopy(mFileImgHdr.uid, 0, buf, 4, 4);
        mProgramming = true;
        mProgInfo.reset();
        try {
            writeCharacteristicWithOutResponse(UUID_OTA_SERVICE, UUID_OTA_RECV_DATA, buf);
        } catch (NpBleUUIDNullException e) {
            e.printStackTrace();
        }

    }


    private void postDta(int index) {
        programBlock(index);
    }

    private void programBlock(int block) {
        if (mProgInfo.iBlocks < mProgInfo.nBlocks) {
            mProgramming = true;
            String msg = new String();

            mProgInfo.iBlocks = (short) block;

            // Prepare block
            mOadBuffer[0] = (byte) (mProgInfo.iBlocks & 0xFF);
            mOadBuffer[1] = (byte) (mProgInfo.iBlocks >> 8);
            System.arraycopy(mFileBuffer, mProgInfo.iBytes, mOadBuffer, 2, OAD_BLOCK_SIZE);

            boolean success = writeImageData(mOadBuffer);
            // Send block
//            ycBleLog.e("FwUpdateActivity" + String.format("TX Block %02x%02x", mOadBuffer[1], mOadBuffer[0]));

            if (success) {
                // Update stats
                mProgInfo.iBlocks++;
                mProgInfo.iBytes += OAD_BLOCK_SIZE;
                float progress = (mProgInfo.iBlocks * 100) / mProgInfo.nBlocks;
                if (otaCallback != null) {
                    otaCallback.onProgress((int) progress);
                }
                NpBleLog.log("progress===>" + progress);
                if (mProgInfo.iBlocks == mProgInfo.nBlocks) {
                    NpBleLog.log("OTA 完成 Programming finished");
                    isSuccess = true;
                    if (otaCallback != null) {
                        otaCallback.onSuccess();
                    }
                }
            } else {
                mProgramming = false;
                msg = "GATT writeCharacteristic failed\n";
                if (otaCallback != null) {
                    otaCallback.onFailure(TI_FAILURE, "writeCharacteristic failed");
                }
            }
            if (!success) {
                NpBleLog.log(msg);
            }
        } else {
            mProgramming = false;
        }
    }

    private boolean writeImageData(byte data[]) {
        try {
            writeCharacteristicWithOutResponse(UUID_OTA_SERVICE, UUID_OTA_SEND_DATA, data);
            return true;
        } catch (NpBleUUIDNullException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void loadFile() {
        // Load binary file
        if (TextUtils.isEmpty(filePath) && imageByes != null) {
            if (mFileBuffer.length < imageByes.length) {
                mFileBuffer = new byte[imageByes.length];
            }
            System.arraycopy(imageByes, 0, mFileBuffer, 0, imageByes.length);
        } else {
            try {
                // Read the file raw into a buffer
                InputStream stream;
                File f = new File(filePath);
                stream = new FileInputStream(f);
                stream.read(mFileBuffer, 0, mFileBuffer.length);
                stream.close();
            } catch (IOException e) {
                // Handle exceptions here
                NpBleLog.log("File open failed: " + filePath + "\n");
            }
        }
        // Show image info
        mFileImgHdr.ver = BleUtil.byte2ShortLR(mFileBuffer[5], mFileBuffer[4]);
        mFileImgHdr.len = BleUtil.byte2ShortLR(mFileBuffer[7], mFileBuffer[6]);
        mFileImgHdr.imgType = ((mFileImgHdr.ver & 1) == 1) ? 'B' : 'A';

        start();
    }

    private class ImgHdr {
        short ver;
        short len;
        Character imgType;
        byte[] uid = new byte[4];
    }


    private class ProgInfo {
        int iBytes = 0; // Number of bytes programmed
        short iBlocks = 0; // Number of blocks programmed
        short nBlocks = 0; // Total number of blocks
        int iTimeElapsed = 0; // Time elapsed in milliseconds

        void reset() {
            iBytes = 0;
            iBlocks = 0;
            iTimeElapsed = 0;
            nBlocks = (short) (mFileImgHdr.len / (OAD_BLOCK_SIZE / HAL_FLASH_WORD_SIZE));
        }
    }


}
