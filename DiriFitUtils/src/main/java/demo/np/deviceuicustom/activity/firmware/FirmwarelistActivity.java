package demo.np.deviceuicustom.activity.firmware;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import demo.np.deviceuicustom.R;
import demo.np.deviceuicustom.base.TitleActivity;
import demo.np.deviceuicustom.net.NetManager;
import demo.np.deviceuicustom.net.NpResponseListener;
import demo.np.deviceuicustom.net.download.DownloadListener;
import demo.np.deviceuicustom.net.download.NetDownloadManager;
import demo.np.deviceuicustom.net.entity.Data;
import demo.np.deviceuicustom.net.entity.FirmwareInfoEntity;
import npBase.BaseCommon.absimpl.NpEditTextWatchImpl;
import npBase.BaseCommon.util.log.LogUtil;
import npBase.BaseCommon.util.toast.ToastHelper;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 固件管理列表
 */
public class FirmwarelistActivity extends TitleActivity {

    private BinListAdapter binListAdapter;

    @BindView(R.id.binListView)
    RecyclerView binListView;//固件列表

    @BindView(R.id.bin_file_tv)
    TextView bin_file_tv;//当前选择的固件路径 textview

    @BindView(R.id.bin_name)
    EditText bin_name;//搜索下载的固件名称 edittext

    @BindView(R.id.sure_btn)
    Button sure_btn;//确定按钮

    @BindView(R.id.download_btn)
    Button download_btn;//下载按钮

    private List<File> files = new ArrayList();

    //当前选择的固件
    private String currentOtaBinPath = null;


    private String firmwarePath = "/storage/emulated/0/DiriFit Test/firmwares/";

    public int loadLayout() {
        return R.layout.activity_firmware_list;
    }


    private void downLoadFile() {
        showLoadingDialog("");
        setLoadingDialogCancelable(false);
        NetManager.getInstance().queryNetFirmwareInfo(getName(), new NpResponseListener<Data<FirmwareInfoEntity>>() {
            @Override
            public void onResponse(Call<Data<FirmwareInfoEntity>> call, Response<Data<FirmwareInfoEntity>> response) {
                if (response == null || response.body() == null || response.body().getData() == null || TextUtils.isEmpty(response.body().getData().getUrl())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingDialog();
                            ToastHelper.getToastHelper().show("没有找到相关固件，请重试");
                        }
                    });
                    return;
                }
                FirmwareInfoEntity firmwareInfoEntity = response.body().getData();
                File firmwareDir = new File(firmwarePath);
                if (!firmwareDir.exists()) {
                    firmwareDir.mkdirs();
                }
                File firmwareFile = new File(firmwareDir, firmwareInfoEntity.getFileName());
                NetDownloadManager.getInstance().download(firmwareInfoEntity.getUrl(), firmwareFile.getPath(), new DownloadListener() {
                    @Override
                    public void onStart() {
                        LogUtil.e("onStart");
                    }

                    @Override
                    public void onProgress(float progress) {
                        LogUtil.e("onProgress==>" + progress);

                    }

                    @Override
                    public void onFinish(String path) {
                        LogUtil.e("onFinish" + path);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                dismissLoadingDialog();
                                showToast("下载完成");
                                reLoadData();
                            }
                        });
                    }

                    @Override
                    public void onFail(String errorInfo) {
                        LogUtil.e("onFail" + errorInfo);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoadingDialog();
                                showToast("下载失败，请重试");
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * 加载固件列表
     */
    private void reLoadData() {
        files.clear();
        File firmwareDir = new File(firmwarePath);
        if (firmwareDir.exists()) {
            File listFiles[] = firmwareDir.listFiles();
            for (File file : listFiles) {
                files.add(file);
            }
            binListAdapter.notifyDataSetChanged();
        }
        if (TextUtils.isEmpty(currentOtaBinPath)) {
            bin_file_tv.setText("点击下面的item选择固件");
            sure_btn.setEnabled(false);
        } else {
            sure_btn.setEnabled(true);
            bin_file_tv.setText("当前选择的固件:" + currentOtaBinPath);
        }
    }

    private void startOta() {
//        OTAHelper.getInstance().startOTA(this, otaBinPath, mac, null, FirmType.TELINK, new OTACallback() {
//            public void onFailure(int paramAnonymousInt, String paramAnonymousString) {
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        ota_btn.setClickable(true);
//                        ota_btn.setText("OTA失败，点击重试");
//                    }
//                });
//            }
//
//            public void onProgress(final int paramAnonymousInt) {
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        Button localButton = ota_btn;
//                        StringBuilder localStringBuilder = new StringBuilder();
//                        localStringBuilder.append(paramAnonymousInt);
//                        localStringBuilder.append("%");
//                        localButton.setText(localStringBuilder.toString());
//                    }
//                });
//            }
//
//            public void onSuccess() {
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        ota_btn.setText("OTA成功");
//                    }
//                });
//            }
//        });
    }

    public String getName() {
        return bin_name.getText().toString().trim();
    }

    public void initView() {
        super.initView();
        titleBar.setTitle("固件列表");

        binListAdapter = new BinListAdapter(this, files) {
            protected void onItemConnClick(File file) {
                currentOtaBinPath = file.getPath();
                bin_file_tv.setText("当前选择的固件:" + currentOtaBinPath);
                sure_btn.setEnabled(true);
            }
        };
        binListView.setLayoutManager(new LinearLayoutManager(this));
        binListView.setAdapter(binListAdapter);
        reLoadData();


        bin_name.addTextChangedListener(new NpEditTextWatchImpl() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(getName())) {
                    download_btn.setEnabled(false);
                } else {
                    download_btn.setEnabled(true);
                }
            }
        });
//        bin_name.setText("OTA_M33_181");
    }


    @OnClick({R.id.download_btn, R.id.sure_btn})
    void click(View view) {
        switch (view.getId()) {
            //搜索下载固件
            case R.id.download_btn:
                if (TextUtils.isEmpty(getName())) {
                    showToast("请输入名称");
                    return;
                }
                downLoadFile();
                break;

            //确定按钮
            case R.id.sure_btn:
                if (TextUtils.isEmpty(currentOtaBinPath)) {
                    showToast("请先选择固件！！");
                    return;
                }
                setResult(RESULT_OK, new Intent().putExtra("binPath", currentOtaBinPath));
                finish();
                break;
        }
    }
}
