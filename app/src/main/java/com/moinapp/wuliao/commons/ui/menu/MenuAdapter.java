package com.moinapp.wuliao.commons.ui.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ApplicationContext;

import java.util.List;

public class MenuAdapter extends BaseAdapter {

    private List<String> mItems;
    private LayoutInflater mInflater;

    public MenuAdapter(List<String> items){
        mItems = items;
        mInflater = LayoutInflater.from(ApplicationContext.getContext());
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = mInflater.inflate(R.layout.menu_row,null);
        }

        TextView title = (TextView) view.findViewById(R.id.row_title);
        title.setText(mItems.get(i));

        return view;
    }
}
