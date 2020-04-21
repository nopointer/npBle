package npBase.BaseCommon.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.List;

import butterknife.ButterKnife;
import npBase.BaseCommon.util.log.LogUtil;
import npPermission.nopointer.core.RequestPermissionInfo;
import npPermission.nopointer.core.YCPermissionRequester;
import npPermission.nopointer.core.callback.PermissionCallback;


/**
 * 基本共用的activity,集成权限申请
 */

public abstract class NpBaseActivity extends FragmentActivity implements PermissionCallback {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加到页面栈
        ActivityManager.getInstance().putActivity(this);
        //沉浸式
        QMUIStatusBarHelper.translucent(this);
        if (isDarkMode()) {
            QMUIStatusBarHelper.setStatusBarDarkMode(this);
        } else {
            QMUIStatusBarHelper.setStatusBarLightMode(this);
        }
        afterCreateBeforeInitView();
        //加载布局
        setContentView(loadLayout());
        ButterKnife.bind(this);
        //初始化组件
        initView();

        afterInitView();
    }

    /**
     * 创建之后，初始化view之前
     */
    protected void afterCreateBeforeInitView() {

    }

    protected void afterInitView(){

    }

    /**
     * 设置布局文件
     *
     * @return
     */
    protected abstract int loadLayout();

    /**
     * 初始化组件
     */
    protected abstract void initView();

    /**
     * 是否是dark模式
     *
     * @return
     */
    protected boolean isDarkMode() {
        return false;
    }


    @Override
    protected void onDestroy() {
        ActivityManager.getInstance().close(this);
        super.onDestroy();
    }


    /**
     * 跳转界面
     *
     * @param argClass
     */
    public void startActivity(Class<?> argClass) {
        startActivity(new Intent(this, argClass));
    }

    /**
     * 跳转后关闭界面
     *
     * @param argClass
     */
    public void startActivityAndFinish(Class<?> argClass) {
        startActivity(new Intent(this, argClass));
        this.finish();
    }

    /**
     * 跳转界面，加回调
     *
     * @param argClass
     * @param argRequestCode
     */
    public void startActivityForResult(Class<?> argClass, int argRequestCode) {
        startActivityForResult(new Intent(this, argClass), argRequestCode);
    }


    @Override
    public void finish() {
        releaseResource();
        super.finish();
    }

    /**
     * 在调用finish后会释放资源
     */
    public void releaseResource() {
    }


    private YCPermissionRequester ycPermissionRequester = null;

    public void requestPermission(RequestPermissionInfo requestPermissionInfo) {
        if (requestPermissionInfo == null) {
            LogUtil.e("debug==没有需要请求的权限");
            return;
        }
        if (ycPermissionRequester == null) {
            ycPermissionRequester = new YCPermissionRequester(requestPermissionInfo);
        }
        ycPermissionRequester.requestPermission(this, this);
    }


    protected RequestPermissionInfo loadPermissionsConfig() {
        return null;
    }

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


    /**
     * 打开相机
     */
    public void camera() {
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
                .enableCrop(true)
                .cropWH(300, 300)
                .scaleEnabled(true)
                .freeStyleCropEnabled(true)
                .withAspectRatio(1, 1)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    /**
     * 打开相册
     */
    public void galleryMultiple() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .isCamera(false)
                .maxSelectNum(9)
                .enableCrop(false)
                .compress(true)
                .rotateEnabled(true)
                .scaleEnabled(true)
                .selectionMode(PictureConfig.MULTIPLE)
                .freeStyleCropEnabled(true)
                .withAspectRatio(1, 1)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    /**
     * 打开相册
     */
    public void gallery() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .isCamera(false)
                .enableCrop(true)
                .rotateEnabled(true)
                .cropWH(300, 300)
                .scaleEnabled(true)
                .selectionMode(PictureConfig.SINGLE)
                .freeStyleCropEnabled(true)
                .withAspectRatio(1, 1)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            onImageSelect(PictureSelector.obtainMultipleResult(data));
        }
    }


    /**
     * 预览
     * @param position
     * @param argLocalMedia
     */
    public void externalPicturePreview(int position,List<LocalMedia> argLocalMedia){
        PictureSelector.create(this).externalPicturePreview(position,argLocalMedia);
    }



    protected void onImageSelect(List<LocalMedia> selectList) {

    }

}
