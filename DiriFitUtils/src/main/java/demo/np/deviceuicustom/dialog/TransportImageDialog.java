package demo.np.deviceuicustom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import demo.np.deviceuicustom.R;
import npwidget.nopointer.progress.NpRectProgressView;

public abstract class TransportImageDialog extends Dialog {
    private NpRectProgressView npRectProgressView;
    private Button cancelBtn;

    public TransportImageDialog(@NonNull Context context) {
        super(context, R.style.dialog_style_not_title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_transport_image);
        npRectProgressView = findViewById(R.id.progress);
        cancelBtn = findViewById(R.id.cancel_btn);
        npRectProgressView.setUseRoundMode(true);
        npRectProgressView.setProgressColor(0xFF01B4ED);
        npRectProgressView.setBgColor(0xFFEEEEEE);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });
    }

    /**
     * 更新进度
     */
    public void updateProgress(float progress) {
        show();
        npRectProgressView.setProgress(progress);
    }

    protected abstract void onCancel();
}
