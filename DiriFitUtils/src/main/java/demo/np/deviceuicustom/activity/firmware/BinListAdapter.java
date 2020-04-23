package demo.np.deviceuicustom.activity.firmware;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import demo.np.deviceuicustom.R;
import demo.np.deviceuicustom.base.adapter.BaseRecycleAdapter;
import demo.np.deviceuicustom.base.adapter.BaseRecycleTag;

/**
 * 固件列表
 */
public abstract class BinListAdapter extends BaseRecycleAdapter<File, BinListAdapter.ViewHolder> {

    /**
     * 当前选中的固件路径
     */
    private String currentSelectPath = "";

    public String getCurrentSelectPath() {
        return currentSelectPath;
    }

    public void setCurrentSelectPath(String currentSelectPath) {
        this.currentSelectPath = currentSelectPath;
        notifyDataSetChanged();
    }

    public BinListAdapter(Context paramContext, List<File> paramList) {
        super(paramContext, paramList);
    }

    public void handDataAndView(ViewHolder viewHolder, final File file, int position) {
        viewHolder.bin_name_tv.setText(file.getPath());

        if (currentSelectPath.equalsIgnoreCase(file.getPath())) {
            viewHolder.itemView.setBackgroundColor(0x80CCCCCC);
        } else {
            viewHolder.itemView.setBackgroundColor(0xFFFFFF);
        }

        viewHolder.itemView.setOnClickListener(new OnClickListener() {
            public void onClick(View paramAnonymousView) {
                currentSelectPath = file.getPath();
                notifyDataSetChanged();
                onItemConnClick(file);
            }
        });

    }

    public ViewHolder instanceTag(View paramView) {
        return new ViewHolder(paramView);
    }

    public int loadItemView() {
        return R.layout.item_bin_info;
    }

    protected abstract void onItemConnClick(File paramFile);

    public static class ViewHolder extends BaseRecycleTag {
        @BindView(R.id.bin_name_tv)
        TextView bin_name_tv;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
