package utils.dirifit.base.base.adapter;

import android.view.View;

import butterknife.ButterKnife;

public class BaseListTag {

    public View itemView;

    public BaseListTag(View view) {
        this.itemView = view;
        ButterKnife.bind(this, view);
        view.setTag(this);
    }

}
