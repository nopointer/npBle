package demo.nopointer.ble.activity;

import android.view.View;

import demo.nopointer.ble.R;
import demo.nopointer.ble.bleModule.NpBleManager;

import android.os.Handler;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;

import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import demo.nopointer.ble.activity.ble.HexCommandBean;
import demo.nopointer.ble.activity.ble.adapter.HexCommandAdapter;
import demo.nopointer.ble.base.activity.TitleActivity;
import demo.nopointer.ble.bleModule.bean.CharaBean;
import demo.nopointer.ble.database.deviceuuid.DeviceUuidServiceImpl;
import demo.nopointer.ble.database.deviceuuid.DeviceUuidTable;
import demo.nopointer.ble.database.hexcommand.HexCommandServiceImpl;
import demo.nopointer.ble.database.hexcommand.HexCommandTable;
import demo.nopointer.ble.dialog.LogCfgDialog;
import demo.nopointer.ble.dialog.bleservice.BleServiceListDialog;
import demo.nopointer.ble.sharedpreferences.SharedPrefereceCycle;
import demo.nopointer.ble.utils.BleLogUtils;
import demo.nopointer.ble.utils.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import npBase.BaseCommon.widget.TitleBar;
import npLog.nopointer.core.NpLog;
import npble.nopointer.ble.conn.NpBleConnCallback;
import npble.nopointer.ble.conn.NpBleConnState;
import npble.nopointer.device.BleDevice;
import npble.nopointer.util.BleUtil;

public class BleActivity extends TitleActivity implements NpBleConnCallback, NpBleManager.BleDataReceiveListener {
    private BleDevice bleDevice = null;
    private BleServiceListDialog bleServiceListDialog = null;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View paramAnonymousView) {
            if (npBleManager.isConnected()) {
                sureExit(false);
                return;
            }
            showLoadingDialog("连接中...");
            npBleManager.connDevice(bleDevice.getMac());
        }
    };
    private int currentLine = 0;
    private int cycleIndex = 0;
    @BindView(R.id.cycle_time_ed)
    EditText cycle_time_ed;
    private DeviceUuidTable deviceUuidTable = null;
    @BindView(R.id.device_mac_tv)
    TextView device_mac_tv;
    private Handler handler = new Handler();
    private HexCommandAdapter hexCommandAdapter = null;
    private List<HexCommandBean> hexCommandBeanList = new ArrayList();
    @BindView(R.id.hexCommandListView)
    RecyclerView hexCommandListView;
    @BindView(R.id.hex_text)
    AppCompatTextView hex_text;
    private boolean isStartRecordLog = false;
    private String lastReadUUid = null;
    private String lastWriteUUid = null;
    private LogCfgDialog logCfgDialog = null;
    private int maxLine = 100;
    @BindView(R.id.read_notify_uuid_btn)
    Button read_notify_uuid_btn;
    private int selectCharaType = 0;
    @BindView(R.id.start_notify_btn)
    Button start_notify_btn;
    @BindView(R.id.stop_notify_btn)
    Button stop_notify_btn;
    private StringBuilder stringBuilder = new StringBuilder();
    QMUIDialog sureClearDialog = null;
    QMUIDialog sureExitDialog = null;
    private float textSize = 14.0F;

    private void cycleSendCommand() {
        if (hexCommandAdapter == null) {
            return;
        }
        final List localList = hexCommandAdapter.getSelectCommand();
        if (localList != null) {
            if (localList.size() < 1) {
                return;
            }
            final int i = localList.size();
            cycleIndex = 0;
            String str = cycle_time_ed.getText().toString();
            if (TextUtils.isEmpty(str)) {
                ToastHelper.getToastHelper().show("请输入发送间隔（单位毫秒）");
                return;
            }
            final int j = Integer.valueOf(str).intValue();
            SharedPrefereceCycle.save(str);
            showLoadingDialog("批量发送指令中...");
            setCancelable(false);
            handler.post(new Runnable() {
                public void run() {
                    if (cycleIndex < i) {
                        HexCommandBean localHexCommandBean = (HexCommandBean) localList.get(cycleIndex);
                        npBleManager.writeData(BleUtil.hexStr2Byte(localHexCommandBean.getHex()));
//                        access$2008(this);
                        handler.postDelayed(this, j);
                        return;
                    }
                    handler.removeCallbacksAndMessages(null);
                    dismissLoadingDialog();
                }
            });
            return;
        }
    }

    private boolean isConn() {
        return npBleManager.isConnected();
    }


    /**
     * 加载设备
     */
    private void loadDeviceHexCommand() {
        List<HexCommandTable> hexCommandTableList = HexCommandServiceImpl.getInstance().findByName(bleDevice.getName());
        if (hexCommandTableList != null && hexCommandTableList.size() >= 1) {
            for (HexCommandTable hexCommandTable : hexCommandTableList) {
                HexCommandBean hexCommandBean = new HexCommandBean(hexCommandTable.getHexString());
                hexCommandBean.setTime(hexCommandTable.getDataId());
                hexCommandBean.setSelect(hexCommandTable.isSelect());
                hexCommandBeanList.add(hexCommandBean);
            }
        }
        hexCommandAdapter.notifyDataSetChanged();
    }

    private void loadDeviceUUid() {
        DeviceUuidTable localDeviceUuidTable = DeviceUuidServiceImpl.getInstance().find(bleDevice.getName());
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("加载上次选择的uuid");
        localStringBuilder.append(new Gson().toJson(localDeviceUuidTable));
        NpLog.eAndSave(localStringBuilder.toString());
        if (localDeviceUuidTable == null) {
            NpLog.e("本地设备记录uuid为空");
            return;
        }
        if ((!TextUtils.isEmpty(localDeviceUuidTable.getWriteServiceUUid())) && (!TextUtils.isEmpty(localDeviceUuidTable.getWriteCharaUUid()))) {
            npBleManager.setWriteUuid(new CharaBean(localDeviceUuidTable.getWriteServiceUUid(), localDeviceUuidTable.getWriteCharaUUid()));
            lastWriteUUid = localDeviceUuidTable.getWriteCharaUUid();
        }
        if ((!TextUtils.isEmpty(localDeviceUuidTable.getReadOrNotifyServiceUUid())) && (!TextUtils.isEmpty(localDeviceUuidTable.getReadOrNotifyCharaUUid()))) {
            npBleManager.setReadNotifyUuid(new CharaBean(localDeviceUuidTable.getReadOrNotifyServiceUUid(), localDeviceUuidTable.getReadOrNotifyCharaUUid()));
            npBleManager.startReadNotifyUuid();
            lastReadUUid = localDeviceUuidTable.getReadOrNotifyCharaUUid();
        }
    }

    private void saveHexCommand(HexCommandBean paramHexCommandBean) {
        HexCommandTable localHexCommandTable = new HexCommandTable(paramHexCommandBean.getTime());
        localHexCommandTable.setName(bleDevice.getName());
        localHexCommandTable.setHexString(paramHexCommandBean.getHex());
        localHexCommandTable.setSelect(paramHexCommandBean.isSelect());
        HexCommandServiceImpl.getInstance().save(localHexCommandTable);
    }

    private void sureCLear() {
        if (sureClearDialog == null) {
            sureClearDialog = new QMUIDialog.MessageDialogBuilder(this).setTitle("确定要清除数据吗").setMessage("清除的数据将无法恢复")
                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                        public void onClick(QMUIDialog paramAnonymousQMUIDialog, int paramAnonymousInt) {
                            sureClearDialog.dismiss();
                        }
                    })
                    .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                        public void onClick(QMUIDialog paramAnonymousQMUIDialog, int paramAnonymousInt) {
//                    access$1202(this, new StringBuilder());
                            hex_text.setText(stringBuilder.toString());
                            sureClearDialog.dismiss();
                        }
                    }).create(com.qmuiteam.qmui.R.style.QMUI_Dialog);
        }
        sureClearDialog.setCancelable(false);
        sureClearDialog.setCanceledOnTouchOutside(false);
        sureClearDialog.show();
    }

    private void sureExit(final boolean paramBoolean) {
        if (sureExitDialog == null) {
            String str;
            if (paramBoolean) {
                str = "确定要退出并且断开蓝牙吗";
            } else {
                str = "确定要断开蓝牙吗";
            }
            sureExitDialog = new QMUIDialog.MessageDialogBuilder(this).setMessage(str).addAction("取消", new QMUIDialogAction.ActionListener() {
                public void onClick(QMUIDialog paramAnonymousQMUIDialog, int paramAnonymousInt) {
                    sureExitDialog.dismiss();
                }
            }).addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                public void onClick(QMUIDialog paramAnonymousQMUIDialog, int paramAnonymousInt) {
                    sureExitDialog.dismiss();
                    npBleManager.disConnectDevice();
                    if (paramBoolean) {
                        finish();
                    }
                }
            }).create(com.qmuiteam.qmui.R.style.QMUI_Dialog);
        }
        sureExitDialog.setCancelable(false);
        sureExitDialog.setCanceledOnTouchOutside(false);
        sureExitDialog.show();
    }

    private void updateConnState(NpBleConnState npBleConnState) {
        String connStateText = "";
        if ((npBleManager.isConnected()) && (npBleConnState == NpBleConnState.CONNECTED)) {
            connStateText = "已连接";
            dismissLoadingDialog();
        } else if (npBleConnState == NpBleConnState.CONNECTING) {
            connStateText = "连接中";
        } else {
            connStateText = "未连接";
            dismissLoadingDialog();
        }
        TitleBar localTitleBar = titleBar;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(bleDevice.getName());
        localStringBuilder.append("(");
        localStringBuilder.append(connStateText);
        localStringBuilder.append(")");
        localTitleBar.setTitle(localStringBuilder.toString());
    }

    @OnClick({R.id.read_notify_uuid_btn, R.id.write_uuid_btn, R.id.clear_data_btn, R.id.command_line_add, R.id.command_line_cut, R.id.cycle_send_btn, R.id.start_notify_btn, R.id.stop_notify_btn, R.id.read_data, R.id.text_size_add, R.id.text_size_cut})
    void click(View view) {
        switch (view.getId()) {
            case R.id.write_uuid_btn:
                if (!isConn()) {
                    ToastHelper.getToastHelper().show("设备未连接");
                    return;
                }
                selectCharaType = 0;
                bleServiceListDialog.showWithChoiceData(npBleManager.getBleServiceBeanList(), 0, lastWriteUUid);
                return;
            //字体减
            case R.id.text_size_cut:
                textSize -= 0.5F;
                if (textSize <= 8.0F) {
                    textSize = 8.0F;
                }
                hex_text.setTextSize(textSize);
                return;

            //字体加
            case R.id.text_size_add:
                textSize += 0.5F;
                if (textSize >= 20.0F) {
                    textSize = 20.0F;
                }
                hex_text.setTextSize(textSize);
                return;
            case R.id.stop_notify_btn:
                if (isStartRecordLog) {
                    isStartRecordLog = false;
                    start_notify_btn.setEnabled(true);
                    stop_notify_btn.setEnabled(false);
                    read_notify_uuid_btn.setEnabled(true);
                    String text = "保存至:" + BleLogUtils.getBleLogFileDir().getAbsolutePath();
                    ToastHelper.getToastHelper().show(text);
                    return;
                }
                break;
            case R.id.start_notify_btn:
                if (!isConn()) {
                    ToastHelper.getToastHelper().show("设备未连接");
                    return;
                }
                if (!npBleManager.isSelectReadOrNotifyUUID()) {
                    return;
                }
                if (isStartRecordLog) {
                    return;
                }
                logCfgDialog.show();
                return;
            case R.id.read_notify_uuid_btn:
                if (!isConn()) {
                    ToastHelper.getToastHelper().show("设备未连接");
                    return;
                }
                selectCharaType = 1;
                bleServiceListDialog.showWithChoiceData(npBleManager.getBleServiceBeanList(), 1, lastReadUUid);
                return;
            case R.id.read_data:
                if (!isConn()) {
                    ToastHelper.getToastHelper().show("设备未连接");
                    return;
                }
                npBleManager.readCharaData();
                return;
            case R.id.cycle_send_btn:
                if (!isConn()) {
                    ToastHelper.getToastHelper().show("设备未连接");
                    return;
                }
                cycleSendCommand();
                return;
            case R.id.command_line_cut:
                if ((hexCommandBeanList != null) && (hexCommandBeanList.size() > 0)) {
                    HexCommandBean hexCommandBean = hexCommandBeanList.get(hexCommandBeanList.size() - 1);
                    HexCommandServiceImpl.getInstance().delete(hexCommandBean.getTime());
                    hexCommandBeanList.remove(hexCommandBean);

                    StringBuilder localStringBuilder = new StringBuilder();
                    for (HexCommandBean hexCommand : hexCommandBeanList) {
                        localStringBuilder.append("当前所有的数据：");
                        localStringBuilder.append(((HexCommandBean) hexCommand).toString());
                    }
                    NpLog.eAndSave(localStringBuilder.toString());

                    hexCommandAdapter.notifyDataSetChanged();
                    return;
                }
                break;
            //增加发送区
            case R.id.command_line_add:
                if (hexCommandBeanList != null) {
                    HexCommandBean hexCommandBean = new HexCommandBean();
                    hexCommandBean.setTime(System.currentTimeMillis());
                    hexCommandBeanList.add(hexCommandBean);
                    saveHexCommand(hexCommandBean);
                    hexCommandAdapter.notifyDataSetChanged();
                    return;
                }
                break;
            case R.id.clear_data_btn:
                sureCLear();
        }
    }

    public void initView() {
        super.initView();
        npBleManager.setDataReceiveListener(this);
        bleDevice = ((BleDevice) getIntent().getSerializableExtra("bleDevice"));
        if (deviceUuidTable == null) {
            deviceUuidTable = new DeviceUuidTable(bleDevice.getName());
        }
        device_mac_tv.setText(bleDevice.getMac());
        titleBar.setRightText("");
        titleBar.setLeftViewOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                sureExit(true);
            }
        });
        device_mac_tv.setOnClickListener(clickListener);
        titleBar.setTitleClickListener(clickListener);
        npBleManager.registerConnCallback(this);
        onConnState(npBleManager.getBleConnState());
        hex_text.setText("");
        hexCommandListView.setLayoutManager(new LinearLayoutManager(this));
        hexCommandAdapter = new HexCommandAdapter(this, hexCommandBeanList) {
            protected void onHexCommandChange(HexCommandBean paramAnonymousHexCommandBean, int paramAnonymousInt) {
                saveHexCommand(paramAnonymousHexCommandBean);
            }

            protected void onSendCommand(HexCommandBean paramAnonymousHexCommandBean, int paramAnonymousInt) {
                if (paramAnonymousHexCommandBean != null) {
                    if (TextUtils.isEmpty(paramAnonymousHexCommandBean.getHex())) {
                        return;
                    }
                    if (!isConn()) {
                        ToastHelper.getToastHelper().show("设备未连接");
                        return;
                    }
                    npBleManager.writeData(BleUtil.hexStr2Byte(paramAnonymousHexCommandBean.getHex()));
                    saveHexCommand(paramAnonymousHexCommandBean);
                    return;
                }
            }
        };
        hexCommandListView.setAdapter(hexCommandAdapter);
        bleServiceListDialog = new BleServiceListDialog(this) {
            protected void onCharaSelect(String paramAnonymousString1, String paramAnonymousString2) {
                if (selectCharaType == 0) {
                    npBleManager.setWriteUuid(new CharaBean(paramAnonymousString1, paramAnonymousString2));
                    deviceUuidTable.setWriteServiceUUid(paramAnonymousString1);
                    deviceUuidTable.setWriteCharaUUid(paramAnonymousString2);
                    DeviceUuidServiceImpl.getInstance().save(deviceUuidTable);
//                    access$702(this, paramAnonymousString2);
                    return;
                }
                npBleManager.setReadNotifyUuid(new CharaBean(paramAnonymousString1, paramAnonymousString2));
                deviceUuidTable.setReadOrNotifyServiceUUid(paramAnonymousString1);
                deviceUuidTable.setReadOrNotifyCharaUUid(paramAnonymousString2);
                DeviceUuidServiceImpl.getInstance().save(deviceUuidTable);
//                access$902(this, paramAnonymousString2);
                npBleManager.startReadNotifyUuid();
            }
        };
        logCfgDialog = new LogCfgDialog(this) {
            protected void onSure(String paramAnonymousString, boolean paramAnonymousBoolean) {
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append("初始化监听文件夹名称");
                localStringBuilder.append(paramAnonymousString);
                NpLog.e(localStringBuilder.toString());
                BleLogUtils.initLog("npBle/Logs", paramAnonymousString);
                BleLogUtils.setIsShowTime(paramAnonymousBoolean);
                BleLogUtils.clearLogFile();
//                access$1102(this, true);
//                access$1202(this, new StringBuilder());
                hex_text.setText(stringBuilder.toString());
                start_notify_btn.setEnabled(false);
                stop_notify_btn.setEnabled(true);
                read_notify_uuid_btn.setEnabled(false);
            }
        };
        loadDeviceHexCommand();
        loadDeviceUUid();
        String str = SharedPrefereceCycle.read();
        if (!TextUtils.isEmpty(str)) {
            cycle_time_ed.setText(str);
        }
    }

    public int loadLayout() {
        return R.layout.activity_ble;
    }

    public void onConnState(final NpBleConnState paramNpBleConnState) {
        runOnUiThread(new Runnable() {
            public void run() {
                updateConnState(paramNpBleConnState);
            }
        });
    }

    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
        if (paramInt == 4) {
            sureExit(true);
        }
        return super.onKeyDown(paramInt, paramKeyEvent);
    }

    public void onReceiveData(String paramString, final byte[] paramArrayOfByte) {
        if (!paramString.equalsIgnoreCase(lastReadUUid)) {
            NpLog.eAndSave("上报数据的uuid 与监听的不一致！！！");
        }
        runOnUiThread(new Runnable() {
            public void run() {
//                access$1708(this);
                String str = BleUtil.byte2HexStr(paramArrayOfByte);
                if (currentLine >= maxLine) {
//                    access$1202(this, new StringBuilder());
//                    access$1702(this, 0);
                }
                StringBuilder localStringBuilder = stringBuilder;
                localStringBuilder.append(str);
                localStringBuilder.append("\n");
                hex_text.setText(stringBuilder.toString());
                if (isStartRecordLog) {
                    BleLogUtils.save(str);
                }
            }
        });
    }
}
