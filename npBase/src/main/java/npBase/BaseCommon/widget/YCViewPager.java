package npBase.BaseCommon.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Viewpage不能左右滑动
 */
public class YCViewPager extends ViewPager {
    private boolean isCanScroll = false;

    public YCViewPager(Context context) {
        super(context);
    }

    public YCViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置是否滑动
     * @param isCanScroll
     */
    public void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }


    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (isCanScroll) {
            return super.onTouchEvent(arg0);
        } else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isCanScroll == false) {
            return false;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item,false);
    }
}
