package npble.nopointer.ota;

/**
 * 设备类别
 */
public enum NpFirmType {
    /**
     * DA
     */
//    DIALOG("dialog"),
    NORDIC("nordic"),
    /**
     * TI的oad 貌似有2种，目前待验证，默认的就用cc254x系列
     */
    TI("ti"),
//    SYD("盛源达"),
    TELINK("泰凌微"),
    HTX("汉天下"),
    FREQCHIP("富芮坤"),
    XR("新蕊");;

    private NpFirmType(String name) {

    }


}
