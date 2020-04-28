package demo.np.deviceuicustom.base;

import android.view.View;

import demo.np.deviceuicustom.R;
import npBase.BaseCommon.widget.TitleBar;
import npPermission.nopointer.core.RequestPermissionInfo;


/**
 */

public abstract class TitleActivity extends BasePermissionCheckActivity {

    //标题栏
    public TitleBar titleBar;

    @Override
    public void initView() {
        //初始化标题栏
        titleBar = findViewById(R.id.titleBar);
        //默认背景颜色为主色调
        titleBar.setBackgroundColor(this.getResources().getColor(R.color.white));
        titleBar.setTitleColor(this.getResources().getColor(R.color.black));
        //默认左边为返回
        titleBar.setLeftImage(R.mipmap.icon_back);
        titleBar.setLeftTextColor(0xFF000000);
        titleBar.setRightTextColor(0xFF000000);
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
