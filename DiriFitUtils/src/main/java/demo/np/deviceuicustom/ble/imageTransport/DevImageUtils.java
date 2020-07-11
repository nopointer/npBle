package demo.np.deviceuicustom.ble.imageTransport;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import demo.np.deviceuicustom.ble.NpBleManager;
import npLog.nopointer.core.NpLog;
import npble.nopointer.util.BleUtil;

public class DevImageUtils {

    private static DevImageUtils instance = new DevImageUtils();

    private DevImageUtils() {
    }

    public static DevImageUtils getInstance() {
        return instance;
    }

    private DialImageBean dialImageBean = null;

    //当前传输的索引
    private int transportIndex = 0;
    //当前是否在传输
    private boolean isTransportIng = false;

    private byte imageByteArray[] = null;

    private long totalBytePckCount;

    public void setDialImageBean(DialImageBean dialImageBean) {
        this.dialImageBean = dialImageBean;
    }


    /**
     * 准备工作
     */
    private void prepare() {
        if (dialImageBean == null) {
            NpBleLog.log("当前没有传输图片对象，dialImageBean=null");
            return;
        }
        if (dialImageBean.getColorCfg() == null) {
            dialImageBean.setColorCfg(ColorCfg.RGB_556);
        }

        //如果不是二进制文件的话，要对图片进行裁剪压缩
        if (dialImageBean.getColorCfg() != ColorCfg.BIN_FILE) {
            if (TextUtils.isEmpty(dialImageBean.getImagePath())) {
                NpBleLog.log("没有指定表盘图片路径！");
                return;
            }
            File file = new File(dialImageBean.getImagePath());
            if (!file.exists()) {
                NpBleLog.log("指定的表盘图片不存在");
                return;
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //原始图片的位图
                        Bitmap bitmap = BitmapFactory.decodeFile(dialImageBean.getImagePath());
                        //需要裁剪成表盘打大小位图
                        Bitmap tmp = resizeBitmap(bitmap, dialImageBean.getImageWidth(), dialImageBean.getImageHeight());
                        bitmap2RGB(tmp, dialImageBean.getImageWidth(), dialImageBean.getImageHeight(), dialImageBean.getColorCfg());
                        onReady();
                    }
                }).start();
                return;
            }
        } else {
            try {
                File file = new File(dialImageBean.getImagePath());
                imageByteArray = new byte[(int) file.length()];
                int len = new FileInputStream(dialImageBean.getImagePath()).read(imageByteArray);
                NpBleLog.log("len:" + len);
                onReady();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onReady() {
        dialImageBean.calculationData();
        totalBytePckCount = dialImageBean.getTotalBytePckCount();
        transportIndex = 0;

        if (devImageTransportCallback != null) {
            devImageTransportCallback.onReady();
        }
    }

    public void start() {
        if (isTransportIng) {
            NpBleLog.log("当前正在传输图片的嘛");
            return;
        } else {
            isTransportIng = true;
            prepare();
        }
    }

    public void stop() {
        isTransportIng = false;
        dialImageBean = null;
    }

    private void onFinish() {
        NpBleLog.log("加载完成了");
        if (devImageTransportCallback != null) {
            devImageTransportCallback.onFinish();
        }
    }

    /**
     * 下一包数据
     */
    public void next() {
        if (isTransportIng) {
            if (transportIndex < totalBytePckCount) {
                int tmpIndex = transportIndex;
                writeImageData(tmpIndex, true);
                transportIndex++;
            } else {
                isTransportIng = false;
                onFinish();
            }
        } else {
            NpBleLog.log("当前不是传输模式");
        }
    }

    /**
     * 下一包数据
     *
     * @param index
     */
    public void withNext(int index) {
        if (isTransportIng) {
            transportIndex = index;
            if (transportIndex < totalBytePckCount) {
                int tmpIndex = transportIndex;
                writeImageData(tmpIndex, false);
                transportIndex++;
            } else {
                onFinish();
            }
        } else {
            NpBleLog.log("当前不是传输模式");
        }
    }


    /**
     * 写数据
     *
     * @param index             数据索引位置
     * @param isContinuousState 是否是连续状态，如果是连续状态的话，就是每写一百包就停止一下
     */
    private void writeImageData(int index, boolean isContinuousState) {
        //图片的数据
        byte imageBytes[] = new byte[dialImageBean.getSinglePckDataLen()];
        //实际要写下去的ble数据
        byte bleData[] = new byte[20];
        if (imageByteArray == null) {
            onFinish();
            return;
        }
        //加载图片/二进制文件 到内存里面
        System.arraycopy(imageByteArray, index * dialImageBean.getSinglePckDataLen(), imageBytes, 0, imageBytes.length);

        bleData[0] = (byte) ((index & 0xff00) >> 8);
        bleData[1] = (byte) (index & 0xff);

        //拼装成带有索引的BLE 协议数据（这里指本项目的协议）
        System.arraycopy(imageBytes, 0, bleData, 2, imageBytes.length);

        if (isContinuousState) {
            if (index == 0 || index % 100 != 0) {
                NpBleManager.getInstance().writeImageData(bleData);
            } else {
                NpBleLog.log("整百包:暂停，等待索引");
            }
        } else {
            NpBleManager.getInstance().writeImageData(bleData);
        }

        float progress = (index + 1.0f) / totalBytePckCount;
        if (devImageTransportCallback != null) {
            devImageTransportCallback.onProgress(progress);
        }

    }


    private static Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap != null) {
            int bmpWidth = bitmap.getWidth();
            int bmpHeight = bitmap.getHeight();

            float scaleW = ((float) width * 1.0f) / bmpWidth;
            float scaleH = ((float) height * 1.0f) / bmpHeight;
            Matrix matrix = new Matrix();
//            matrix.setScale(1, 1);//水平翻转
            matrix.postScale(scaleW, scaleH);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
            return resizedBitmap;
        } else {
            return null;
        }
    }


    private void bitmap2RGB(Bitmap bitmap, int imageWidth, int imageHeight, ColorCfg colorCfg) {
        //实际的bitmap的大小 ARGB_8888
        int[] bitmap_data = new int[bitmap.getWidth() * bitmap.getHeight()];

        bitmap.getPixels(bitmap_data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        Bitmap resultBmp = null;
        switch (colorCfg) {
            case RGB_556:
            default:
                resultBmp = Bitmap.createBitmap(bitmap_data, imageWidth, imageHeight, Bitmap.Config.RGB_565);
                break;
        }

        int bytes = resultBmp.getByteCount();
        NpBleLog.log("debug===分配的数据长度是:" + bytes);

        ByteBuffer buf = ByteBuffer.allocate(bytes);
        resultBmp.copyPixelsToBuffer(buf);
        imageByteArray = buf.array();

        NpBleLog.log("=======================");
        NpBleLog.log("imageByteArray:" + BleUtil.byte2HexStr(imageByteArray));
        NpBleLog.log("=======================");
        NpBleLog.log("debug===转换后的是:" + imageByteArray.length);
    }


    private DevImageTransportCallback devImageTransportCallback = null;

    public void setReceiveImageDataCallback(DevImageTransportCallback devImageTransportCallback) {
        this.devImageTransportCallback = devImageTransportCallback;
    }
}
