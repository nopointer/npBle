package demo.nopointer.ble.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import demo.nopointer.ble.R;
import demo.nopointer.ble.utils.ToastHelper;
import npBase.BaseCommon.absimpl.NpEditTextWatchImpl;

public abstract class LogCfgDialog extends Dialog {
    private EditText log_name_edit;
    private TextView log_path_tv;
    private AppCompatCheckBox show_time_ck;

    public LogCfgDialog(@NonNull Context paramContext) {
//        super(paramContext, 2131755612);
        super(paramContext);
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_log_cfg);
        log_name_edit = ((EditText) findViewById(R.id.log_name_edit));
        log_path_tv = ((TextView) findViewById(R.id.log_path_tv));
        show_time_ck = ((AppCompatCheckBox) findViewById(R.id.show_time_ck));
        log_name_edit.addTextChangedListener(new NpEditTextWatchImpl() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append("日志保存路径：npBle/Logs/");
                localStringBuilder.append(log_name_edit.getText().toString());
                localStringBuilder.append(".txt");
                log_path_tv.setText(localStringBuilder.toString());
            }
        });

        findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                dismiss();
            }
        });
        findViewById(R.id.sure_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String text = log_name_edit.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    ToastHelper.getToastHelper().show("请输入文件名 ！！！");
                    return;
                }
                boolean bool = show_time_ck.isChecked();
                onSure(text, bool);
                dismiss();
            }
        });
    }

    protected abstract void onSure(String paramString, boolean paramBoolean);
}