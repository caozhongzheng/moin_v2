package com.keyboard.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keyboard.bean.EmoticonBean;
import com.keyboard.utils.imageloader.ImageBase;
import com.keyboard.utils.imageloader.ImageLoader;
import com.keyboard.view.I.IView;
import com.keyboard.view.R;

import java.io.IOException;
import java.util.List;

public class EmoticonsAdapter extends BaseAdapter{

    static final String TAG = "ea";
    private LayoutInflater inflater;
    private Context mContext;

    private List<EmoticonBean> data;
    private int mItemHeight = 0;
    private int mImgHeight = 0;

    public EmoticonsAdapter(Context context, List<EmoticonBean> list) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.data = list;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
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
            convertView = inflater.inflate(R.layout.item_emoticon, null);
            convertView.setLayoutParams(new AbsListView.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mItemHeight));
            viewHolder.iv_face = (ImageView) convertView.findViewById(R.id.item_iv_face);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.item_tv_name);
            viewHolder.rl_content = (LinearLayout) convertView.findViewById(R.id.rl_content);
            viewHolder.rl_parent = (RelativeLayout) convertView.findViewById(R.id.rl_parent);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mImgHeight, mImgHeight);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            viewHolder.rl_content.setLayoutParams(params);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final EmoticonBean emoticonBean = data.get(position);
//        Log.i(TAG, "显示第"+(position+1)+"个:");
        if (emoticonBean != null) {
//            Log.i(TAG, "1显示第"+(position+1)+"个的表情："+emoticonBean.toString());
            viewHolder.rl_parent.setBackgroundResource(R.drawable.iv_face);

            if(mOnItemListener != null){
                if(ImageBase.Scheme.ofUri(emoticonBean.getIconUri()) == ImageBase.Scheme.UNKNOWN){
                    if (mOnItemListener != null) {
                        mOnItemListener.onItemDisplay(emoticonBean);
                    }

                    viewHolder.rl_parent.setVisibility(View.GONE);
                }
                else{
                    try {
//                        Log.i(TAG, "2显示第"+(position+1)+"个的表情uri："+emoticonBean.getIconUri());
                        ImageLoader.getInstance(mContext).displayImage(emoticonBean.getIconUri(), viewHolder.iv_face);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(emoticonBean.getEventType()==2) {
//                        Log.i(TAG, "3显示第"+(position+1)+"个的表情的名字："+emoticonBean.getContent());
                        viewHolder.tv_name.setVisibility(View.VISIBLE);
                        viewHolder.tv_name.setText(emoticonBean.getContent());
                    } else {
//                        Log.i(TAG, "4 隐藏第"+(position+1)+"个的表情的名字：");
                        viewHolder.tv_name.setVisibility(View.GONE);
                    }
                }
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemListener != null) {
                        mOnItemListener.onItemClick(emoticonBean);
                    }
                }
            });

            final View finalConvertView = convertView;
            final int finalPosition = position;
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnItemListener != null) {
                        return mOnItemListener.onItemLongClick(finalPosition, finalConvertView, emoticonBean);
                    }
                    return false;
                }
            });
        } else {
//            Log.i(TAG, "9 隐藏第"+(position+1)+"个的表情：");
            viewHolder.rl_parent.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        public ImageView iv_face;
        public TextView tv_name;
        public RelativeLayout rl_parent;
        public LinearLayout rl_content;
    }

    public void setHeight(int height, int padding) {
        mItemHeight = height;
        mImgHeight = mItemHeight - padding;
        notifyDataSetChanged();
    }

    IView mOnItemListener;
    public void setOnItemListener(IView listener) {
        this.mOnItemListener = listener;
    }
}