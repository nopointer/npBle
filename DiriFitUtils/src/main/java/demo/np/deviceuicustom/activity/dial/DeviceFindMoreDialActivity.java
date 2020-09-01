package demo.np.deviceuicustom.activity.dial;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import demo.np.deviceuicustom.R;
import demo.np.deviceuicustom.activity.firmware.PathEntity;
import demo.np.deviceuicustom.base.TitleActivity;
import demo.np.deviceuicustom.ble.NpBleManager;
import demo.np.deviceuicustom.ble.imageTransport.ColorCfg;
import demo.np.deviceuicustom.ble.imageTransport.DevImageTransportCallback;
import demo.np.deviceuicustom.ble.imageTransport.DevImageUtils;
import demo.np.deviceuicustom.ble.imageTransport.DialImageBean;
import demo.np.deviceuicustom.ble.imageTransport.UIModeStyle;
import demo.np.deviceuicustom.ble.utils.DevDataUtils;
import demo.np.deviceuicustom.dialog.TransportImageDialog;
import demo.np.deviceuicustom.sharedpreferences.SharedPreferecelastPath;
import npBase.BaseCommon.util.toast.ToastHelper;
import npble.nopointer.ble.conn.NpBleConnCallback;
import npble.nopointer.ble.conn.NpBleConnState;
import npble.nopointer.log.NpBleLog;


public class DeviceFindMoreDialActivity extends TitleActivity implements NpBleConnCallback {

    @BindView(R.id.bin_file_tv)
    TextView bin_file_tv;

    private String imagePath = null;
    private TransportImageDialog transportImageDialog = null;

    //是否显示传输对话框
    private boolean isShowTrainDialog = true;

    private boolean isTraining =false;

    public int loadLayout() {
        return R.layout.activity_device_find_more_layout;
    }


    public void initView() {
        super.initView();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        titleBar.setTitle("自定义表盘");

        titleBar.setLeftViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTraining){
                    sureEndTransportImageProgress();
                }else {
                    finish();
                }
            }
        });

        PathEntity localPathEntity2 = SharedPreferecelastPath.read();
        PathEntity localPathEntity1 = localPathEntity2;
        if (localPathEntity2 == null) {
            localPathEntity1 = new PathEntity();
        }
        imagePath = localPathEntity1.getDialBinPath();
        if (TextUtils.isEmpty(imagePath)) {
            bin_file_tv.setText("点击选择表盘");
        } else {
            bin_file_tv.setText(imagePath);
        }

        if (transportImageDialog == null) {
            transportImageDialog = new TransportImageDialog(this) {
                protected void onCancel() {
                    sureEndTransportImageProgress();
                }
            };
            transportImageDialog.setCancelable(false);
            transportImageDialog.setCanceledOnTouchOutside(false);
        }
//        BleManager.getBleManager().setImageTransportCallback(new ImageTransportCallback() {
//            public void imageCompressSuccess() {
//
//            }
//
//            public void onFinish() {
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        dismissLoadingDialog();
//                        if (transportImageDialog != null) {
//                            transportImageDialog.dismiss();
//                        }
//                        showSuccessDialog(getResources().getString(2131624042));
//                        BleManager.getBleManager().writeData(DevDataPackUtils.updateImageMode(0, 2));
//                        new Handler().postDelayed(new Runnable() {
//                            public void run() {
//                                BleManager.getBleManager().writeData(DevDataPackUtils.switchDevDialUI(UIModeStyle.MORE_DIAL));
//                            }
//                        }, 500L);
//                    }
//                });
//            }
//
//            public void onProgress(float paramAnonymousFloat) {
//                handler.sendMessage(handler.obtainMessage(1, Float.valueOf(paramAnonymousFloat)));
//            }
//        });
//        BleManager.getBleManager().registerConnCallback(this);
        onConnState(NpBleManager.getInstance().getBleConnState());
    }


    @OnClick({R.id.bin_file_tv, R.id.start_btn})
    void click(View view) {
        switch (view.getId()) {
            case R.id.bin_file_tv:
                loadFile();
                break;
            case R.id.start_btn:
                start();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK) && (requestCode == 1000)) {
            List<String> pathList = data.getStringArrayListExtra(Constant.RESULT_INFO);
            if ((pathList != null) && (pathList.size() > 0)) {
                imagePath = pathList.get(0);
                bin_file_tv.setText(imagePath);
            }
        }
    }

    public void onConnState(NpBleConnState npBleConnState) {
        if (npBleConnState != NpBleConnState.CONNECTED) {
            runOnUiThread(new Runnable() {
                public void run() {
                    ToastHelper.getToastHelper().show("未连接");
                    finish();
                }
            });
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (transportImageDialog != null) {
            if (transportImageDialog.isShowing()) {
                transportImageDialog.dismiss();
            }
            transportImageDialog = null;
        }
        NpBleManager.getInstance().disConnectDevice();
    }


    private void loadFile() {
//        new LFilePicker().withActivity(this).withRequestCode(1000).withStartPath("/storage/emulated/0/DiriFit Test/dials").withMaxNum(1).withMutilyMode(false).withIsGreater(true).start();
        new LFilePicker().withActivity(this).withRequestCode(1000).withStartPath("/storage/emulated/0").withMaxNum(1).withMutilyMode(false).withIsGreater(true).start();
    }

    private void start() {
        if (TextUtils.isEmpty(imagePath)) {
            ToastHelper.getToastHelper().show("先选择文件");
            return;
        }
        showLoadingDialog("");

        PathEntity pathEntity = SharedPreferecelastPath.read();
        if (pathEntity == null) {
            pathEntity = new PathEntity();
        }
        pathEntity.setDialBinPath(imagePath);
        SharedPreferecelastPath.save(pathEntity);
        isShowTrainDialog =true;
        dismissLoadingDialog();
        transportImageDialog.updateProgress(0.0F);

        DialImageBean dialImageBean = new DialImageBean();
        dialImageBean.setColorCfg(ColorCfg.BIN_FILE);
        dialImageBean.setImagePath(imagePath);
        DevImageUtils.getInstance().setDialImageBean(dialImageBean);
        isTraining =true;

        DevImageUtils.getInstance().setReceiveImageDataCallback(new DevImageTransportCallback() {
            @Override
            public void onReady() {
                //表盘数据装载解析并装载好了
                NpBleLog.log("表盘数据装载解析并装载好了");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NpBleManager.getInstance().writeData(DevDataUtils.updateImageMode(1, 2));
                    }
                },2000);
            }

            @Override
            public void onFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        if (transportImageDialog != null) {
                            transportImageDialog.dismiss();
                        }
                        showSuccessDialog("传输完成");
                        isTraining =false;
                    }
                });
                NpBleManager.getInstance().writeData(DevDataUtils.updateImageMode(0, 2));
                NpBleManager.getInstance().writeData(DevDataUtils.switchDevDialUI(UIModeStyle.MORE_DIAL));
            }

            @Override
            public void onProgress(float progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isShowTrainDialog && transportImageDialog != null) {
                            transportImageDialog.updateProgress(progress);
                        }
                    }
                });
            }
        });
        DevImageUtils.getInstance().start();
    }

    /**
     * 停止传输表盘bin
     */
    private void sureEndTransportImageProgress() {
        isShowTrainDialog = false;
        transportImageDialog.dismiss();
        new QMUIDialog.MessageDialogBuilder(this)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .setMessage(getString(R.string.give_up_upload_image))
                .addAction(getString(R.string.continue_upload), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        isShowTrainDialog = true;
                        transportImageDialog.show();
                    }
                })
                .addAction(0, getString(R.string.give_up), QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        transportImageDialog.dismiss();
                        DevImageUtils.getInstance().stop();
                        NpBleManager.getInstance().writeData(DevDataUtils.updateImageMode(0, 2));
                        NpBleManager.getInstance().writeData(DevDataUtils.switchDevDialUI(UIModeStyle.DEFAULT1));
                        finish();
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }


}

