package com.moinapp.wuliao.modules.ipresource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.modules.ipresource.model.EmojiInfo;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moying on 15/5/28.
 */
public class GridAdapter extends BaseAdapter {
    private ArrayList<String> mUrlList;
    private Context mContext;
    private LayoutInflater mInflater;

    public GridAdapter(Context context, List<EmojiInfo> emjInfos) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mUrlList = new ArrayList<String>();
        if(emjInfos != null) {
            for (int i = 0; i < emjInfos.size(); i++) {
                mUrlList.add(emjInfos.get(i).getIcon().getUri());
            }
        }
    }

    @Override
    public int getCount() {
        return mUrlList.size();
    }

    @Override
    public Object getItem(int i) {
        return mUrlList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ItemViewTag viewTag;

        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.ip_emoji_grid_item, null);

            // construct an item tag
            viewTag = new ItemViewTag((ImageView) convertView.findViewById(R.id.emoji_icon));
            convertView.setTag(viewTag);
        } else
        {
            viewTag = (ItemViewTag) convertView.getTag();
        }

        // set icon
        ImageLoader.getInstance().displayImage(mUrlList.get(i), viewTag.mIcon, BitmapUtil.getImageLoaderOption());
        return convertView;
    }

    class ItemViewTag
    {
        protected ImageView mIcon;

        /**
         * The constructor to construct a navigation view tag
         *
         * @param icon
         *            the icon view of the item
         */
        public ItemViewTag(ImageView icon)
        {
            this.mIcon = icon;
        }
    }
}
