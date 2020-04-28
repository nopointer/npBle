package demo.np.deviceuicustom.ble.utils;

import demo.np.deviceuicustom.ble.BleSomeCfg;
import demo.np.deviceuicustom.ble.imageTransport.UIModeStyle;

public class DevDataUtils implements BleSomeCfg {
    private DevDataUtils() {
    }


    /**
     * 设备ui指令
     *
     * @param updateState 更新状态 0更新完成 1开始更新
     * @param dialType    0编辑的表盘 1更多表盘
     * @return
     */
    public static byte[] updateImageMode(int updateState, int dialType) {
        byte[] data = new byte[7];
        int index = 5;
        data[index++] = (byte) updateState;
        setDateHeaderAndLen(data, STE_DEVICE_UI);
        data[6] = (byte) dialType;
        return data;
    }

    /**
     * 设备ui指令
     *
     * @param commandType 1开始更新，请求设备端接收数据，0数据下发完成 请求设备端执行更新ui
     * @return
     */
    public static byte[] controlDevUI(int commandType) {
        byte[] data = new byte[6];
        int index = 3;
        data[index++] = (byte) 0xFF;
        data[index++] = (byte) 0x33;
        data[index++] = (byte) commandType;
        setDateHeaderAndLen(data);
        return data;
    }



    /**
     * 切换表盘
     *
     * @param uiModeStyle 0 1 默认表盘，2自定义编辑表盘 3更多表盘
     * @return
     */
    public static byte[] switchDevDialUI(UIModeStyle uiModeStyle) {
        byte[] data = new byte[6];
        int index = 5;
        data[index++] = (byte) uiModeStyle.getType();
        setDateHeaderAndLen(data, CHOICE_DEVICE_UI_TYPE);
        return data;
    }


    public static byte[] setDevUICfg() {
        byte[] data = new byte[10];
        int index = 3;
        data[index++] = (byte) 0xFF;
        data[index++] = (byte) 0x32;
        data[index++] = (byte) 0x01;
        data[index++] = (byte) 0x02;
        data[index++] = (byte) 0x00;
        data[index++] = (byte) 0x01;
        data[index++] = (byte) 0x03;
        setDateHeaderAndLen(data);
        return data;
    }

    /**
     * 选择表盘
     *
     * @param number
     * @return
     */
    public static byte[] choiceDevUIType(int number) {
        byte[] data = new byte[6];
        int index = 3;
        data[index++] = (byte) 0xFF;
        data[index++] = (byte) 0x29;
        data[index++] = (byte) number;
        setDateHeaderAndLen(data);
        return data;
    }


    /**
     * 封装头部数据 和数据长度
     *
     * @param data
     */
    private static void setDateHeaderAndLen(byte[] data) {
        data[0] = (byte) 0xAB;
        int len = data.length - 3;
        data[1] = (byte) ((len & 0xff00) >> 8);
        data[2] = (byte) (len & 0xff);
    }


    /**
     * 封装头部数据 和数据长度
     *
     * @param data
     */
    private static void setDateHeaderAndLen(byte[] data, int dataType) {
        data[0] = (byte) 0xAB;
        int len = data.length - 3;
        data[1] = (byte) ((len & 0xff00) >> 8);
        data[2] = (byte) (len & 0xff);
        data[3] = (byte) ((dataType & 0xff00) >> 8);
        data[4] = (byte) (dataType & 0xff);
    }



    public static byte[] powderOff(){
        byte[] data = new byte[8];
        int index = 3;
        data[index++] = (byte) 0xFF;
        data[index++] = (byte) 0xFE;
        data[index++] = (byte) 0x80;
        data[index++] = (byte) 0x00;
        data[index++] = (byte) 0x01;
        setDateHeaderAndLen(data);
        return data;
    }

}
