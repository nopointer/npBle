//package npBase.BaseCommon.view.picker;
//
//import android.content.Context;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.PopupWindow;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import cn.carbswang.android.numberpickerview.library.NumberPickerView;
//import npBase.BaseCommon.R;
//import npBase.BaseCommon.base.picker.NpBasePicker;
//
//
///**
// * 从底部弹出来供选择的滚动列表对话框
// */
//
//public class SinglePickerView extends NpBasePicker {
//
//
//    //选择器
//    @BindView(R.id.stepGoalPickerView)
//    NumberPickerView stepGoalPickerView;
//
//    private List<String> selectPickerList = null;
//
//    public SinglePickerView(Context context) {
//        super(context);
//        if (selectPickerList == null) {
//            selectPickerList = new ArrayList<>();
//        }
//    }
//
//    public List<String> getSelectPickerList() {
//        return selectPickerList;
//    }
//
//    public SinglePickerView setSelectPickerList(List<String> selectPickerList) {
//        this.selectPickerList = selectPickerList;
//        return this;
//    }
//
//    @Override
//    protected int loadLayout() {
//        return R.layout.pop_bottom_float_single_picker_layout;
//    }
//
//    @Override
//    protected void initView() {
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                if (onBottomFloatPickCallback != null) onBottomFloatPickCallback.onDismiss();
//                backgroundAlpha(1f);
//            }
//        });
//
//
////        stepGoalPickerView.setValue(getIndex(strValue));
////        stepGoalPickerView.setOnValueChangedListener(this);
//
//    }
//
//    public OnBottomFloatPickCallback onBottomFloatPickCallback = null;
//
//    public SinglePickerView showPicker(View refLayour) {
//        backgroundAlpha(0.5f);
//        handDataAndView();
//        popupWindow.showAtLocation(refLayour, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//        return this;
//    }
//
//
//    private void handDataAndView() {
//        selectPickerList.add("11");
//        selectPickerList.add("11");
//        selectPickerList.add("11");
//        selectPickerList.add("11");
//        selectPickerList.add("11");
//        stepGoalPickerView.setDisplayedValues(selectPickerList.toArray(new String[]{}));
//        stepGoalPickerView.setMinValue(0);
//        stepGoalPickerView.setWrapSelectorWheel(false);
//        stepGoalPickerView.setMaxValue(selectPickerList.size() - 1);
//    }
//
//    public SinglePickerView appendCallback(OnBottomFloatPickCallback callback) {
//        this.onBottomFloatPickCallback = callback;
//        return this;
//    }
//
//    public static abstract class OnBottomFloatPickCallback {
//        public abstract void onClick(SinglePickerView picker, int index);
//
//        public void onDismiss() {
//        }
//    }
//
//}
