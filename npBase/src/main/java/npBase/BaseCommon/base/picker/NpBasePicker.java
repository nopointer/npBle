package npBase.BaseCommon.base.picker;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import butterknife.ButterKnife;
import npBase.BaseCommon.R;


/**
 * Created by nopointer on 2017/12/26.
 * 基础的选择view
 */

public abstract class NpBasePicker {

    /**
     * Context
     */
    protected Context context;
    /**
     * 布局文件
     */
    protected View contentView;
    /**
     * 弹出窗
     */
    protected PopupWindow popupWindow;

    protected abstract int loadLayout();

    protected abstract void initView();

    public NpBasePicker(Context context) {
        this.context = context;
        contentView = LayoutInflater.from(context).inflate(loadLayout(), null);
        ButterKnife.bind(this, contentView);

        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setAlpha(128);
        popupWindow.setBackgroundDrawable(colorDrawable);

        initView();
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    protected void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((FragmentActivity) context).getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        ((FragmentActivity) context).getWindow().setAttributes(lp);
    }

    public void dismiss() {
        popupWindow.dismiss();
        backgroundAlpha(1f);
    }


}
