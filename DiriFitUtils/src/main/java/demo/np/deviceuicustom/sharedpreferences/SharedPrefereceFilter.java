package demo.np.deviceuicustom.sharedpreferences;

import demo.np.deviceuicustom.sharedpreferences.bean.FilterBean;
import npBase.BaseCommon.util.sharedpreferences.SaveObjectUtils;

public class SharedPrefereceFilter {
    public static void clear() {
        save(null);
    }

    public static FilterBean read() {
        return SaveObjectUtils.getObject("cfg_filter_bean", FilterBean.class);
    }

    public static void save(FilterBean filterBean) {
        SaveObjectUtils.setObject("cfg_filter_bean", filterBean);
    }
}