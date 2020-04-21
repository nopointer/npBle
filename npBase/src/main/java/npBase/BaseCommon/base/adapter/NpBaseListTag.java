package npBase.BaseCommon.base.adapter;

import android.view.View;

import butterknife.ButterKnife;

public class NpBaseListTag {

    public View itemView;

    public NpBaseListTag(View view) {
        this.itemView = view;
        ButterKnife.bind(this, view);
        view.setTag(this);
    }

}
