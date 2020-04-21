package npBase.BaseCommon.absimpl;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * 文本框的输入监听 抽象实现类 ，简化代码
 */

public abstract class NpEditTextWatchImpl implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
