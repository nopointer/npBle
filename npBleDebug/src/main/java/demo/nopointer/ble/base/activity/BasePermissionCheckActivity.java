package demo.nopointer.ble.base.activity;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.List;

import npPermission.nopointer.core.RequestPermissionInfo;
import npPermission.nopointer.core.YCPermissionRequester;
import npPermission.nopointer.core.callback.PermissionCallback;


/**
 * Created by nopointer on 2018/9/1.
 * 权限检测器
 */

public abstract class BasePermissionCheckActivity extends BaseActivity implements PermissionCallback {

    private YCPermissionRequester ycPermissionRequester = null;

    public void requestPermission(RequestPermissionInfo requestPermissionInfo) {
        if (requestPermissionInfo == null) {
            requestPermissionInfo = new RequestPermissionInfo();
        }
        if (ycPermissionRequester == null) {
            ycPermissionRequester = new YCPermissionRequester(requestPermissionInfo);
        }
        ycPermissionRequester.requestPermission(this, this);
    }



    protected abstract RequestPermissionInfo loadPermissionsConfig();

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ycPermissionRequester == null) return;
        ycPermissionRequester.onRequestPermissionsResult(this, requestCode, permissions, grantResults, this);
    }


    @Override
    public void onGetAllPermission() {
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (ycPermissionRequester == null) return;
        RequestPermissionInfo requestPermissionInfo = ycPermissionRequester.getPermissionInfo();
        if (requestPermissionInfo != null && !TextUtils.isEmpty(requestPermissionInfo.getAgainPermissionMessage())) {
            ycPermissionRequester.checkDeniedPermissionsNeverAskAgain(this, perms);
        }
    }
}
