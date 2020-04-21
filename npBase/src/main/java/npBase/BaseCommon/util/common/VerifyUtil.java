package npBase.BaseCommon.util.common;

import android.text.TextUtils;

/**
 * Created by nopointer on 2018/8/23.
 * 校验工具类，做常规的校验
 */

public final class VerifyUtil {
    private VerifyUtil() {
    }


    //    public static final String RULE_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
//    public static final String RULE_EMAIL ="\\w+(\\.\\w)*@\\w+(\\.\\w{2,3}){1,3}";
    public static final String RULE_EMAIL = "(.+)(\\w+)(@{1})(.+)(\\.)(\\w+)$";


    /**
     * 字符串的长度校验
     *
     * @param string 需要校验的字符串
     * @param minLen 最小长度
     * @param maxLen 最大长度
     * @return
     */
    public static boolean verifyLength(String string, int minLen, int maxLen) {
        if (string == null || TextUtils.isEmpty(string)) return false;
        int len = string.length();
        if (len < minLen || len > maxLen) return false;
        return true;
    }

    /**
     * 邮箱校验
     *
     * @param email 邮箱号
     * @return
     */
    public static boolean verifyEmail(String email) {
        return email.matches(RULE_EMAIL);
    }


}
