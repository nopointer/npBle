package npBase.BaseCommon.util.common;

import android.os.Handler;
import android.widget.TextView;

/**
 * 计时器工具
 */


public class CounterUtils {

    private Handler handler = new Handler();
    private int count = 60;
    private int tmp;
    private TextView textView = null;
    private String oldText = "";

    public CounterUtils(int count, TextView textView, String oldText) {
        this.count = count;
        this.textView = textView;
        this.oldText = oldText;
        handler = new Handler();
    }

    public void startCounter() {
        tmp = count;
        handler.post(new Runnable() {
            @Override
            public void run() {
                tmp--;
                if (tmp < 0) {
                    handler.removeCallbacks(this);
                    textView.setText(oldText);
                    textView.setClickable(true);
                } else {
                    handler.postDelayed(this, 1000);
                    textView.setText(tmp + "s");
                    textView.setClickable(false);
                }

            }
        });
    }

}
