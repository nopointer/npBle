package npBase.BaseCommon.base.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public class NpBaseRecycleTag extends RecyclerView.ViewHolder {
    public View itemView;

    public NpBaseRecycleTag(View itemView) {
        super(itemView);
        this.itemView = itemView;
        ButterKnife.bind(this,itemView);
    }
}