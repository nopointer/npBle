package utils.dirifit.base.base.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by nopointer on 2018/1/5.
 * 基础的适配器
 */

public abstract class BaseRecycleAdapter<D, H extends BaseRecycleTag> extends RecyclerView.Adapter<H> {

    protected List<D> dataList = null;
    protected Context context = null;
    private LayoutInflater layoutInflater;

    protected DisplayMetrics dm = new DisplayMetrics();


    public BaseRecycleAdapter(Context context, List<D> dataList) {
        this.dataList = dataList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
    }


    @Override
    public H onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = layoutInflater.inflate(loadItemView(), parent, false);
        return instanceTag(convertView);
    }

    @Override
    public void onBindViewHolder(H holder, int position) {
        D data = dataList.get(position);
        handDataAndView(holder, data, position);
    }


    public abstract int loadItemView();

    public abstract H instanceTag(View convertView);

    public abstract void handDataAndView(H holder, D data, int position);

    @Override
    public int getItemCount() {
        return dataList.size();
    }


}
