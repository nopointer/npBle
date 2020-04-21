package npBase.BaseCommon.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by nopointer on 2017/4/8.
 */

public abstract class NpBaseListAdapter<D, T extends NpBaseListTag> extends BaseAdapter {

    protected Context context;
    protected List<D> datas = null;

    public NpBaseListAdapter(Context context, List<D> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public D getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final D d = datas.get(position);
        T t;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(loadItemView(), parent, false);
            t = instanceTag(convertView);
        } else {
            t = (T) convertView.getTag();
        }
        handDataAndView(t, d, position);
        return convertView;
    }

    public abstract int loadItemView();

    public abstract T instanceTag(View convertView);

    public abstract void handDataAndView(T t, D d, int position);


}
