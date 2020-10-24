package demo.nopointer.npDemo.base.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import butterknife.ButterKnife;
import npBase.BaseCommon.util.toast.ToastHelper;


/**
 * Created by wangquan on 18/9/3.
 */

public abstract class BaseActivity extends FragmentActivity {

    //进度框
    private QMUITipDialog.Builder builder;
    private QMUITipDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式
        QMUIStatusBarHelper.translucent(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        ToastHelper.getToastHelper().setActivity(this);
        //加载布局
        setContentView(loadLayout());
        ButterKnife.bind(this);
        //初始化组件
        initView();
    }


    /**
     * 设置布局文件
     *
     * @return
     */
    public abstract int loadLayout();

    /**
     * 初始化组件
     */
    public abstract void initView();

    /**
     * Toast
     *
     * @param argMessage
     */
    public void showToast(String argMessage) {
        Toast.makeText(this, argMessage, Toast.LENGTH_SHORT).show();
    }

    /**
     * Toast
     *
     * @param argMessageId
     */
    public void showToast(final int argMessageId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, getResources().getString(argMessageId), Toast.LENGTH_SHORT).show();
            }
        });

    }

    QMUITipDialog qmuiTipDialog = null;

    /**
     * 成功提示框
     *
     * @param argMessage
     */
    public void showSuccessDialog(String argMessage) {
        try {
            qmuiTipDialog = new QMUITipDialog.Builder(this).setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS).setTipWord(argMessage).create();
            qmuiTipDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (qmuiTipDialog != null) {
                        qmuiTipDialog.dismiss();
                    }
                }
            }, 1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 失败提示框
     *
     * @param argMessage
     */
    public void showFailDialog(String argMessage) {
        try {
            final QMUITipDialog qmuiTipDialog = new QMUITipDialog.Builder(this).setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL).setTipWord(argMessage).create();
            qmuiTipDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    qmuiTipDialog.dismiss();
                }
            }, 1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 失败提示框
     *
     * @param argMessage
     */
    public void showFailDialogNotClose(String argMessage) {
        try {
            final QMUITipDialog qmuiTipDialog = new QMUITipDialog.Builder(this).setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL).setTipWord(argMessage).create();
            qmuiTipDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提示框
     *
     * @param argMessage
     */
    public void showMessageDialog(final String argMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final QMUITipDialog qmuiTipDialog = new QMUITipDialog.Builder(BaseActivity.this).setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO).setTipWord(argMessage).create();
                    qmuiTipDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            qmuiTipDialog.dismiss();
                        }
                    }, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 显示加载框
     */
    public void showLoadingDialog(final String argMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (loadingDialog == null || !loadingDialog.isShowing()) {
                        builder = new QMUITipDialog.Builder(BaseActivity.this).setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING);
                        loadingDialog = builder.setTipWord(argMessage).create();
                        setLoadingDialogCancelable(false);
                        loadingDialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 设置loading框是否可返回关闭
     */
    public void setLoadingDialogCancelable(boolean argFlag) {
        if (loadingDialog != null) {
            loadingDialog.setCanceledOnTouchOutside(argFlag);
        }
    }

    public void setCancelable(boolean argFlag){
        if (loadingDialog != null) {
            loadingDialog.setCancelable(argFlag);
        }
    }

    /**
     * 设置loading框取消回调事件
     *
     * @param argOnCancelListener
     */
    public void setLoadingDialogOnCancelListener(DialogInterface.OnCancelListener argOnCancelListener) {
        if (loadingDialog != null) {
            loadingDialog.setOnCancelListener(argOnCancelListener);
        }
    }

    /**
     * 关闭加载框
     */
    public void dismissLoadingDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        });
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
    protected void onDestroy() {
        if (qmuiTipDialog != null) {
            qmuiTipDialog.cancel();
            qmuiTipDialog = null;
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        releaseRes();
        super.finish();
    }

    public void releaseRes(){

    }
}
