package demo.np.deviceuicustom.activity.ota;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import demo.np.deviceuicustom.R;
import demo.np.deviceuicustom.activity.firmware.FirmwarelistActivity;
import demo.np.deviceuicustom.base.TitleActivity;

/**
 * 批量OTA
 */
public class BatchOTAActivity extends TitleActivity {


    @BindView(R.id.select_btn_tv)
    TextView select_btn_tv;//当前选择的固件

    //当前选择的固件路径
    private String selectBinPath = null;

    @BindView(R.id.start_ota_btn)
    Button start_ota_btn;//开始按钮

    @BindView(R.id.stop_ota_btn)
    Button stop_ota_btn;//停止按钮

    @Override
    public int loadLayout() {
        return R.layout.activity_bacth_ota;
    }

    @Override
    public void initView() {
        super.initView();
        titleBar.setTitle("OTA");
    }


    @OnClick(R.id.select_bin_btn)
    void click(View view) {
        switch (view.getId()) {
            case R.id.select_bin_btn: {
                Intent intent = new Intent(this, FirmwarelistActivity.class);
                startActivityForResult(intent, 100);
            }
            break;
        }
    }

    //刷新显示的固件
    private void refreshSelectBin() {
        if (TextUtils.isEmpty(selectBinPath)) {
            select_btn_tv.setText("当前选择的固件:无");
            start_ota_btn.setEnabled(false);
            stop_ota_btn.setEnabled(false);
        } else {
            select_btn_tv.setText("当前选择的固件:" + selectBinPath);
            start_ota_btn.setEnabled(true);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                selectBinPath = data.getStringExtra("binPath");
                refreshSelectBin();
            }
        }
    }
}
