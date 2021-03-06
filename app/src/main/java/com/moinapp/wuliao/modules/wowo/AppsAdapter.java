package com.moinapp.wuliao.modules.wowo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.modules.wowo.model.AppBean;

import java.util.ArrayList;

/**
 * Created by moying on 15/6/8.
 * 图片，拍照
 */

public class AppsAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context mContext;
    private Handler mHandler;
    private ArrayList<AppBean> mDdata = new ArrayList<AppBean>();

    public AppsAdapter(Context context, ArrayList<AppBean> data) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        if (data != null) {
            this.mDdata = data;
        }
    }

    public AppsAdapter(Context context, ArrayList<AppBean> data, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        this.inflater = LayoutInflater.from(context);
        if (data != null) {
            this.mDdata = data;
        }
    }

    @Override
    public int getCount() {
        return mDdata.size();
    }

    @Override
    public Object getItem(int position) {
        return mDdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.xhs_item_app, null);

            viewHolder.rl_app = (RelativeLayout) convertView.findViewById(R.id.rl_app);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final AppBean appBean = mDdata.get(position);
        if (appBean != null) {
            viewHolder.rl_app.setVisibility(View.VISIBLE);
            int resID = mContext.getResources().getIdentifier(appBean.getIcon(), "drawable", mContext.getPackageName());
            viewHolder.iv_icon.setBackgroundResource(resID);
            viewHolder.tv_name.setText(appBean.getFuncName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mHandler != null) {
                        Message msg = new Message();
                        msg.what = 0x11;
                        msg.obj = appBean;
                        mHandler.sendMessage(msg);
                    }
                }
            });
        } else {
            viewHolder.rl_app.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        public RelativeLayout rl_app;
        public ImageView iv_icon;
        public TextView tv_name;
    }
}