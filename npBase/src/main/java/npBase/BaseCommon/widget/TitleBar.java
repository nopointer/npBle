package npBase.BaseCommon.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import npBase.BaseCommon.R;


/**
 * 常用标题栏
 */

public class TitleBar extends RelativeLayout {
    //根组件
    private View rootView;
    //左右图标
    private ImageView leftImageView, rightImageView;
    //左右文字和标题
    private TextView leftTxtView, titleTxtView, rightTxtView;

    public TitleBar(Context context) {
        super(context);
        initView();
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        //加载根布局
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.title_bar_layout, this, true);
        //初始化各组件
        leftImageView = rootView.findViewById(R.id.left_icon_view);
        rightImageView = rootView.findViewById(R.id.right_icon_view);

        leftTxtView = rootView.findViewById(R.id.leftText);
        titleTxtView = rootView.findViewById(R.id.title_txtView);
        rightTxtView = rootView.findViewById(R.id.rightText);

    }

    /**
     * 设置背景颜色
     *
     * @param argColor
     */
    public void setBackgroundColor(int argColor) {
        View view = (View) getParent();
        view.setBackgroundColor(argColor);
    }

    public void setTitleClickListener(OnClickListener clickListener){
        titleTxtView.setOnClickListener(clickListener);
    }

    /**
     * 设置背景图片
     *
     * @param argBgId
     */
    public void setBackgroundResource(int argBgId) {
        View view = (View) getParent();
        view.setBackgroundResource(argBgId);
    }

    /**
     * 设置标题
     *
     * @param argTitle
     */
    public void setTitle(String argTitle) {
        titleTxtView.setText(argTitle);
    }

    /**
     * 设置标题
     *
     * @param argTitleId
     */
    public void setTitle(int argTitleId) {
        titleTxtView.setText(argTitleId);
    }

    /**
     * 设置标题文字颜色
     *
     * @param argColor
     */
    public void setTitleColor(int argColor) {
        titleTxtView.setTextColor(argColor);
    }

    /**
     * 设置标题文字颜色
     *
     * @param argColor
     */
    public void setTitleColor(String argColor) {
        titleTxtView.setTextColor(Color.parseColor(argColor));
    }


    /**
     * 设置左边图标
     *
     * @param argRID
     */
    public void setLeftImage(int argRID) {
        if (argRID == -1) {
            leftImageView.setVisibility(GONE);
        } else {
            leftImageView.setVisibility(View.VISIBLE);
            leftImageView.setImageResource(argRID);
        }
    }

    /**
     * 设置左边图标
     *
     * @param argRID
     */
    public void setRightImage(int argRID) {
        rightImageView.setVisibility(View.VISIBLE);
        rightImageView.setImageResource(argRID);
    }

    /**
     * 设置左边文字
     *
     * @param argText
     */
    public void setLeftText(String argText) {
        leftImageView.setVisibility(GONE);
        leftTxtView.setVisibility(View.VISIBLE);
        leftTxtView.setText(argText);
    }


    /**
     * 设置左边文字
     *
     * @param argRID
     */
    public void setLeftText(int argRID) {
        leftTxtView.setVisibility(View.VISIBLE);
        leftTxtView.setText(argRID);
    }


    /**
     * 设置左边文字颜色
     *
     * @param argColor
     */
    public void setLeftTextColor(int argColor) {
        leftTxtView.setVisibility(View.VISIBLE);
        leftTxtView.setTextColor(argColor);
    }

    /**
     * 设置左边文字
     *
     * @param argText
     */
    public void setRightText(String argText) {
        rightTxtView.setVisibility(View.VISIBLE);
        rightTxtView.setText(argText);
    }

    /**
     * 设置左边文字大小
     *
     * @param argSize
     */
    public void setRightTextSize(float argSize) {
        rightTxtView.setTextSize(argSize);
    }

    /**
     * 设置左边文字
     *
     * @param argRID
     */
    public void setRightText(int argRID) {
        rightTxtView.setVisibility(View.VISIBLE);
        rightTxtView.setText(argRID);
    }

    /**
     * 设置右边文字颜色
     *
     * @param argColor
     */
    public void setRightTextColor(int argColor) {
        rightTxtView.setTextColor(argColor);
    }

    /**
     * 设置左边区域点击事件
     *
     * @param argOnClickListener
     */
    public void setLeftViewOnClickListener(OnClickListener argOnClickListener) {
        findViewById(R.id.left_icon_view).setOnClickListener(argOnClickListener);
        findViewById(R.id.leftText).setOnClickListener(argOnClickListener);
    }


    /**
     * 设置左边区域点击事件
     *
     * @param argOnClickListener
     */
    public void setRightViewOnClickListener(OnClickListener argOnClickListener) {
        findViewById(R.id.right_icon_view).setOnClickListener(argOnClickListener);
        findViewById(R.id.rightText).setOnClickListener(argOnClickListener);
    }
}

