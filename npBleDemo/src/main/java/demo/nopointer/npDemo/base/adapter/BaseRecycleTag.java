package demo.nopointer.npDemo.base.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public class BaseRecycleTag extends RecyclerView.ViewHolder {
    public View view;

    public BaseRecycleTag(View itemView) {
        super(itemView);
        this.view = itemView;
        ButterKnife.bind(this,itemView);
    }
}