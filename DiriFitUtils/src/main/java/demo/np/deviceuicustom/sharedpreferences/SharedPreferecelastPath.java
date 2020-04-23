package demo.np.deviceuicustom.sharedpreferences;

import demo.np.deviceuicustom.activity.firmware.PathEntity;
import npBase.BaseCommon.util.sharedpreferences.SaveObjectUtils;

public class SharedPreferecelastPath {
    public static void clear() {
        save(null);
    }

    public static PathEntity read() {
        return (PathEntity) SaveObjectUtils.getObject("pthBean_cfg", PathEntity.class);
    }

    public static void save(PathEntity paramPathEntity) {
        SaveObjectUtils.setObject("pthBean_cfg", paramPathEntity);
    }
}
