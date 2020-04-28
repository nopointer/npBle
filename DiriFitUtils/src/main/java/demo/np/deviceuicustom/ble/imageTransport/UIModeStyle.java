package demo.np.deviceuicustom.ble.imageTransport;

import java.io.Serializable;

public enum UIModeStyle implements Serializable {
    /**
     * 默认1
     */
    DEFAULT1(0),
    /**
     * 默认2
     */
    DEFAULT2(1),
    /**
     * 自定义
     */
    CUSTOM(2),

    /**
     * 更多表盘
     */
    MORE_DIAL(3);

    private int type = 0;

    private UIModeStyle(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }


    public static UIModeStyle getUIModeStyle(int uiStyleType) {
        switch (uiStyleType) {
            case 0:
                return DEFAULT1;
            case 1:
                return DEFAULT2;
        }
        return null;
    }
}