package com.moinapp.wuliao.modules.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.ipresource.IPMoinClipActivity;
import com.moinapp.wuliao.modules.ipresource.IPResourceConstants;
import com.moinapp.wuliao.modules.ipresource.IPResourceManager;
import com.moinapp.wuliao.modules.ipresource.model.IPResource;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moying on 15/6/30.
 */
public class MyIPAdapter extends BaseAdapter {
    private ILogger MyLog = LoggerFactory.getLogger(MineModule.MODULE_NAME);
    private final ImageLoader imageLoader;
    private Context mContext;
    protected LayoutInflater mInflater;
    private Handler mHandler;
    private List<IPResource> mIPList;
    /**
     * 用户选择的IP
     */
    public static ArrayList<IPResource> mSelectedIP = new ArrayList<IPResource>();

    static int count;
    
    public static int mMode;
    
    public MyIPAdapter(Context context, List<IPResource> iplist, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        this.mInflater = LayoutInflater.from(mContext);
        this.mIPList = iplist;

        imageLoader = ImageLoader.getInstance();
        if(!imageLoader.isInited())
            imageLoader.init(BitmapUtil.getImageLoaderConfiguration());
    }

    public void setIpList(List<IPResource> iplist) {
        this.mIPList = iplist;
    }

    @Override
    public int getCount() {
        return mIPList.size();
    }

    @Override
    public Object getItem(int position) {
        return mIPList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ip_homepg_row_item, null);
            viewHolder = getViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setContentView(viewHolder, position);
        return convertView;
    }

    private void setContentView(final ViewHolder viewHolder, final int position) {
        if(mIPList == null || mIPList.size() <= position) {
            return;
        }
        try {
            imageLoader.displayImage(mIPList.get(position).getIcon().getUri(), viewHolder.mIamge, BitmapUtil.getImageLoaderOption());
        } catch (OutOfMemoryError e) {
            MyLog.e(e);
        } catch (Exception e) {
            MyLog.e(e);
        }

        if(mMode == MineConstants.MODE_EDIT) {
            viewHolder.mSelectIcon.setVisibility(View.VISIBLE);
            if(mSelectedIP.contains(mIPList.get(position))) {
                viewHolder.mSelectIcon.setImageResource(R.drawable.icon_select);
            } else {
                viewHolder.mSelectIcon.setImageResource(R.drawable.icon_unselect);
            }

            viewHolder.mIamge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mSelectedIP.contains(mIPList.get(position))) {
                        viewHolder.mSelectIcon.setImageResource(R.drawable.icon_unselect);
                        mSelectedIP.remove(mIPList.get(position));
                        count--;
                        if (textcallback != null)
                            textcallback.onListen(count);
                    } else {
                        viewHolder.mSelectIcon.setImageResource(R.drawable.icon_select);
                        mSelectedIP.add(mIPList.get(position));
                        count++;
                        if (textcallback != null)
                            textcallback.onListen(count);
                    }
                }
            });
        } else if(mMode == MineConstants.MODE_NONE) {
            viewHolder.mSelectIcon.setVisibility(View.GONE);
            viewHolder.mIamge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle b = new Bundle();
                    b.putString("ip_id", mIPList.get(position).get_id());
                    b.putInt("type", mIPList.get(position).getType());
                    b.putBoolean("view_ip", true);
                    MyLog.i("toIPMoin: " + b);
                    AppTools.toIntent(mContext.getApplicationContext(), b, IPMoinClipActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            });
        }
        viewHolder.mTitle.setText(mIPList.get(position).getName());
        viewHolder.mDesc.setText(IPResourceManager.getIpTypeName(mContext, mIPList.get(position).getType()) + "/"
                + StringUtil.formatDate(mIPList.get(position).getReleaseDate(), IPResourceConstants.IP_RELEASE_DATE_FORMAT));
    }

    private ViewHolder getViewHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.mIamge = (ImageView) convertView.findViewById(R.id.iv);
        holder.mSelectIcon = (ImageView) convertView.findViewById(R.id.id_item_select);
        holder.mTitle = (TextView) convertView.findViewById(R.id.title);
        holder.mDesc = (TextView) convertView.findViewById(R.id.desc);

        return holder;
    }

    class ViewHolder {
        public ImageView mIamge;
        public ImageView mSelectIcon;
        public TextView mTitle;
        public TextView mDesc;
    }

    public static ArrayList<IPResource> getSelectedIP() {
        return mSelectedIP;
    }

    public static void setSelectedIP(ArrayList<IPResource> mSelectedIP) {
        MyIPAdapter.mSelectedIP = mSelectedIP;
        count = mSelectedIP.size();
    }
    
    public static void setMode(int mode) {
        MyIPAdapter.mMode = mode;
    }

    private TextCallback textcallback = null;
    public interface TextCallback {
        void onListen(int count);
    }

    public void setTextCallback(TextCallback listener) {
        textcallback = listener;
    }

}
