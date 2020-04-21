package npBase.BaseCommon.util.view;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;

import npBase.BaseCommon.util.log.LogUtil;

/**
 * 文本过滤器，禁止输入空格和换行
 */

public class NpEditTextUtil {


    private NpEditTextUtil() {
    }

    /**
     * 禁止edittext输入空格和换行
     *
     * @param editText
     */
    public static void setFilter(EditText editText) {
        if (editText == null) {
            LogUtil.e("editText 为空！！！");
        }
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ") || source.toString().contentEquals("\n")) return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /**
     * 设置editText显示明文还是密文
     *
     * @param editText
     * @param show
     */
    public static void setShowPwd(EditText editText, boolean show) {
        if (editText == null) {
            LogUtil.e("editText 为空！！！");
        }
        if (show) {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        editText.setSelection(editText.getText().toString().length());
    }


}
