package com.moinapp.wuliao.modules.ipresource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.ipresource.model.EmojiInfo;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moying on 15/5/28.
 */
public class IpDetailEmjGridAdapter extends BaseAdapter {
    private ILogger MyLog = LoggerFactory.getLogger("emj");

    private Context mContext;
    private ArrayList<EmojiInfo> mEmjInfos;
    private ArrayList<String> mUrlList;
    private LayoutInflater mInflater;
    private int screenWidth;
    public static final int GRIVIEW_COLUMN_HEIGHT = 3;

    public IpDetailEmjGridAdapter(Context context, List<EmojiInfo> emjInfos) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mEmjInfos = (ArrayList<EmojiInfo>) emjInfos;

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mUrlList = new ArrayList<String>();

        if (emjInfos != null) {
//            MyLog.i("size = " + emjInfos.size());
            for (int i = 0; i < emjInfos.size(); i++) {
                mUrlList.add(emjInfos.get(i).getIcon().getUri());
            }
//
//            for (int i = 0; i < 3; i++) {
//                mUrlList.add("http://dev.mo-image.com/image/2015/05/OZ/e5ba3a07e41a9247d9f2b32152892be2.png");
//            }
//            for (int i = 0; i < 3; i++) {
//                mUrlList.add("http://dev.mo-image.com/image/2015/05/Y4/89a79199122f5d38ff2a45c5f6b7d95e.jpg");
//            }
        } else {
//            MyLog.i("emjInfos is nul;l = ");
        }
//        MyLog.i("size22 = " + mUrlList.size());
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

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ip_emoji_grid_item, null);
            GridView.LayoutParams gl = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screenWidth/3);

            convertView.setLayoutParams(gl);

//            initKeyView(convertView);

            // construct an item tag
            viewTag = new ItemViewTag((ImageView) convertView.findViewById(R.id.emoji_icon));
            convertView.setTag(viewTag);
        } else {
            viewTag = (ItemViewTag) convertView.getTag();
        }
//        MyLog.i(i + "  getView = " + viewTag.mIcon);

        // set icon
        try {
            ImageLoader.getInstance().displayImage(mUrlList.get(i), viewTag.mIcon, BitmapUtil.getImageLoaderOption());
        } catch (OutOfMemoryError e) {
            MyLog.e(e);
        } catch (Exception e) {
            MyLog.e(e);
        }
        //TODO view emoji detail ，
        final int position = i;
        viewTag.mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, EmojiShowActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                );
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(EmojiShowActivity.EMOJI_OBJECT, mEmjInfos.get(position));
                i.putExtras(mBundle);
                mContext.startActivity(i);
            }
        });

        return convertView;
    }
//
//    public void initKeyView(final View ll) {
//        ViewTreeObserver vto2 = ll.getViewTreeObserver();
//        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                ll.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//
//                setHeight(ll, ll.getHeight());
//            }
//        });
//    }
//
//    public void setHeight(View ll, int height) {
//        ll.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height, Gravity.CENTER));
//    }
    class ItemViewTag {
        protected ImageView mIcon;

        /**
         * The constructor to construct a navigation view tag
         *
         * @param icon the icon view of the item
         */
        public ItemViewTag(ImageView icon) {
            this.mIcon = icon;
        }
    }
}
