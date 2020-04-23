package demo.np.deviceuicustom;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import butterknife.OnClick;
import demo.np.deviceuicustom.activity.scan.MultiChoiceScanActivity;
import demo.np.deviceuicustom.base.TitleActivity;
import npBase.BaseCommon.util.NpAppBaseUtils;

public class MainActivity extends TitleActivity {


    private int count = 0;

    @Override
    public int loadLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        super.initView();
        titleBar.setTitle(R.string.app_name_main);
        titleBar.setLeftText(NpAppBaseUtils.getVersionName(this));
        titleBar.setLeftViewOnClickListener(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }

    @OnClick({R.id.version_tv, R.id.ota, R.id.start})
    void click(View view) {
        switch (view.getId()) {
            case R.id.version_tv:
                count++;
                if (count >= 5) {
//                    findViewById(2131230950).setVisibility(0);
                }
                break;
            case R.id.ota:
                startActivity(new Intent(MainActivity.this, MultiChoiceScanActivity.class).putExtra("type", 1));
                break;

            case R.id.start:
                startActivity(new Intent(MainActivity.this, MultiChoiceScanActivity.class).putExtra("type", 2));
                break;
        }
    }
}
