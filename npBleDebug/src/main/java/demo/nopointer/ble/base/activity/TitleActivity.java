package demo.nopointer.ble.base.activity;

import android.view.View;

import demo.nopointer.ble.R;
import demo.nopointer.ble.bleModule.NpBleManager;
import npBase.BaseCommon.widget.TitleBar;
import npPermission.nopointer.core.RequestPermissionInfo;


/**
 * Created by wangquan on 18/9/3.
 */

public abstract class TitleActivity extends BasePermissionCheckActivity {

    //标题栏
    public TitleBar titleBar;

    protected NpBleManager npBleManager =NpBleManager.getInstance();

    @Override
    public void initView() {
        //初始化标题栏
        titleBar = findViewById(R.id.titleBar);
        //默认背景颜色为主色调
        titleBar.setBackgroundColor(this.getResources().getColor(R.color.white));
        titleBar.setTitleColor(this.getResources().getColor(R.color.black));
        titleBar.setLeftTextColor(R.color.black);
        //默认左边为返回
        titleBar.setLeftImage(R.mipmap.icon_back);
        titleBar.setLeftViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected RequestPermissionInfo loadPermissionsConfig() {
        return null;
    }
}
