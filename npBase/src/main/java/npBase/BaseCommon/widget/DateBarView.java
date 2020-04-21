package npBase.BaseCommon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

import npBase.BaseCommon.R;
import npBase.BaseCommon.util.DateUtils;

/**
 * 自定义日期（日/周/月/年）选择组件
 */

public class DateBarView extends RelativeLayout implements View.OnClickListener {
    //父组件
    private View rootView;
    //左右按钮
    private ImageView leftImgView, rightImgView;
    //显示内容
    private TextView titleTxtView;
    //类型
    private int dateType = 0;
    //当前对象
    private DateBarBean dateBarBean;
    //回调
    private OnDateBarSelectedListener onDateBarSelectedListener;

    //天 索引
    private int dayIndex = 0;
    //周 索引
    private int weekIndex = 0;
    //月 索引
    private int monthIndex = 0;
    //年 索引
    private int yearIndex = 0;

    public DateBarView(Context context) {
        super(context);
        initView();
    }

    public DateBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DateBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.date_bar_layout, this, true);
        //按钮
        leftImgView = rootView.findViewById(R.id.date_bar_left_imgView);
        rightImgView = rootView.findViewById(R.id.date_bar_right_imgView);
        leftImgView.setOnClickListener(this);
        rightImgView.setOnClickListener(this);
        //标题
        titleTxtView = rootView.findViewById(R.id.date_bar_title_txtView);
    }

    /**
     * 加载数据
     */
    private void loadDateToView() {
        switch (dateType) {
            case DateBarBean.TYPE_DAY:
                dateBarBean = DateUtils.getDayDateBarBean(new Date(System.currentTimeMillis()), dayIndex);
                break;
            case DateBarBean.TYPE_WEEK:
                dateBarBean = DateUtils.getWeekDateBarBean(new Date(System.currentTimeMillis()), weekIndex);
                break;
            case DateBarBean.TYPE_MONTH:
                dateBarBean = DateUtils.getMonthDateBarBean(new Date(System.currentTimeMillis()), monthIndex);
                break;
            case DateBarBean.TYPE_YEAR:
                dateBarBean = DateUtils.getYearDateBarBean(new Date(System.currentTimeMillis()), yearIndex);
                break;
        }
        setTitle();

        if (onDateBarSelectedListener != null) {
            onDateBarSelectedListener.onSelected(dateBarBean);
        }
    }

    private void setTitle() {
        if (titleTxtView != null && dateBarBean != null) {
            titleTxtView.setText(dateBarBean.getTitle());
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.date_bar_left_imgView) {
            switch (dateType) {
                case 0:
                    dayIndex--;
                    break;
                case 1:
                    weekIndex--;
                    break;
                case 2:
                    monthIndex--;
                    break;
                case 3:
                    yearIndex--;
                    break;
            }

            loadDateToView();
        } else if (v.getId() == R.id.date_bar_right_imgView) {
            switch (dateType) {
                case 0:
                    if (dayIndex < 0) {
                        dayIndex++;
                        loadDateToView();
                    }
                    break;
                case 1:
                    if (weekIndex < 0) {
                        weekIndex++;
                        loadDateToView();
                    }
                    break;
                case 2:
                    if (monthIndex < 0) {
                        monthIndex++;
                        loadDateToView();
                    }
                    break;
                case 3:
                    if (yearIndex < 0) {
                        yearIndex++;
                        loadDateToView();
                    }
                    break;
            }
        }
    }

    /**
     * 设置日期类型(日、周、月)
     *
     * @param dateType
     */
    public void setDateType(int dateType) {
        this.dateType = dateType;
        loadDateToView();
    }

    /**
     * 设置回调
     *
     * @param onDateBarSelectedListener
     */
    public void setOnDateBarSelectedListener(OnDateBarSelectedListener onDateBarSelectedListener) {
        this.onDateBarSelectedListener = onDateBarSelectedListener;
    }

    /**
     * 选择回调
     */
    public interface OnDateBarSelectedListener {
        void onSelected(DateBarBean argDateBarBean);
    }
}
