package com.moinapp.wuliao.modules.ipresource;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.modal.BaseVideo;
import com.moinapp.wuliao.modules.ipresource.model.MoinClip;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.DisplayUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by moying on 15/5/29.
 */
public class ClipAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private RelativeLayout.LayoutParams mLayoutParams;

    private List<List<MoinClip>> mClipList;
    private int screenWidth;
    private int firstHeight;
    private int minHeight;
    private int maxHeight;
    private ImageLoader imageLoader;
    private ILogger MyLog = LoggerFactory.getLogger(IPResourceModule.MODULE_NAME);

    public ClipAdapter(Context context, BaseVideo video, List<List<MoinClip>> cliplist) {
        mContext = context;
        if (mLayoutInflater == null) {
            mLayoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        try {
            MyLog.i("clip0(video): " + video.toString());
            MyLog.i("clipSize: " + cliplist.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mClipList = cliplist;
        MoinClip mc = null;
        if(video != null) {
            mc = new MoinClip(video.getUri(), video.getImageuri());
            mc.setMedia(2);
        }
        List<MoinClip> mList0 = new ArrayList<>();
        mList0.add(mc);
        mClipList.add(0, mList0);

        screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;// 获取分辨率宽度
        firstHeight = mContext.getResources().getDimensionPixelOffset(R.dimen.ip_hot_banner_height);
        minHeight = mContext.getResources().getDimensionPixelOffset(R.dimen.clip_item_hmin);
        maxHeight = mContext.getResources().getDimensionPixelOffset(R.dimen.clip_item_hmax);
        MyLog.i("screenWidth: " + screenWidth + ", firstHeight=" + firstHeight + ", minHeight=" + minHeight + ", maxHeight=" + maxHeight);

        imageLoader = ImageLoader.getInstance();

    }

    @Override
    public int getCount() {
        return mClipList.size();
    }

    @Override
    public Object getItem(int i) {
        return mClipList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.clip_list_item, null);
            viewHolder = getListViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setContentView(viewHolder, position);

        return convertView;
    }


    private void setContentView(ViewHolder viewHolder, int position) {
        List<MoinClip> mClip = null;
        if (mClipList != null && mClipList.size() > 0) {
            mClip = mClipList.get(position);
            MyLog.i("第" + (position + 1) + "行 有 " + mClip.size() + " 个数据");
//            int[] params = calculateHeight(position, mClip);
            int size = position == 0 ? 1 : mClip.size();
            for (int i = 0; i < size; i++) {
                if(mClip.get(i) == null) {
                    viewHolder.mIamge[i].setVisibility(View.GONE);
                    continue;
                }
                viewHolder.mIamge[i].setVisibility(View.VISIBLE);

//                mLayoutParams = new RelativeLayout.LayoutParams(params[2*i], params[2*i+1]);
                if(position == 0 && (mClip.get(i).getWidth() == 0 || mClip.get(i).getHeight() == 0)) {
                    // 312dp = R.dimen.ip_hot_banner_height
                    mLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(mContext, 312));
                } else {
                    mLayoutParams = new RelativeLayout.LayoutParams(mClip.get(i).getWidth(), mClip.get(i).getHeight());
                }
//                mLayoutParams = new RelativeLayout.LayoutParams(i==0?mClip.get(i).getWidth()-4:mClip.get(i).getWidth()-2, mClip.get(i).getHeight()-4);
                viewHolder.mIamge[i].setLayoutParams(mLayoutParams);
                int media = mClip.get(i).getMedia();
                viewHolder.mPlayIcon[i].setVisibility(media == 2 ? View.VISIBLE : View.GONE);
                try {
                    if (media == 1) {
                        imageLoader.displayImage(mClip.get(i).getUri(), viewHolder.mIamge[i], BitmapUtil.getImageLoaderOption());
                    } else if (media == 2) {
                        imageLoader.displayImage(mClip.get(i).getImageuri(), viewHolder.mIamge[i], BitmapUtil.getImageLoaderOption());
                    }
                } catch (OutOfMemoryError e) {
                    MyLog.e(e);
                } catch (Exception e) {
                    MyLog.e(e);
                }
                MoinClip finalMClip = mClip.get(i);
                MyLog.i("第" + (i + 1) + "个 类型是 " + (media == 1 ? "剧照" : "视频") + ", 名字叫 " + finalMClip.getName() + ", 地址是 " + (media == 1 ? finalMClip.getUri() : finalMClip.getImageuri()));
                MyLog.i("第" + (i + 1) + "个 " + finalMClip.getWidth() + "," + finalMClip.getHeight());
                MyLog.i("=====详情是===" + finalMClip.toString());

                viewHolder.mPlayIcon[i].setOnClickListener(new MyClickListener(finalMClip));
                viewHolder.mIamge[i].setOnClickListener(new MyClickListener(finalMClip));
            }
            for (int i = mClip.size(); i < 3; i++) {
                viewHolder.mIamge[i].setVisibility(View.GONE);
                viewHolder.mPlayIcon[i].setVisibility(View.GONE);
            }
        }
    }

    private int[] calculateHeight(int position, List<MoinClip> mClip) {
        if(mClip == null || mClip.size() < 1)
            return null;

        int len = mClip.size();
        int[] result = new int[len * 2];
        if(position == 0 || mClip.size() == 1) {
            result[0] = screenWidth;
            result[1] = position == 0 ? firstHeight :
                    mClip.get(0).getHeight() < minHeight ? minHeight :
                            mClip.get(0).getHeight() > maxHeight ? maxHeight : mClip.get(0).getHeight();
        } else {
            /**
             * minHeight < (h1 = h2 = h3) < maxHeight
             * w1/h1 = r1, w2/h2 = r2, w3/h3 = r3
             * w1 + w2 + w3 = screenWidth
             * */
            DecimalFormat df = new DecimalFormat("######0.0000");
//            BigDecimal b = new BigDecimal(f);
//            double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            double[] w = new double[len];
            double[] h = new double[len];
            double[] r = new double[len];
            double sum_r = 0d;
            MyLog.i("************************************************************************");
            for (int i = 0; i < len; i++) {
                w[i] = mClip.get(i).getWidth();
                h[i] = mClip.get(i).getHeight();
                r[i] = Double.parseDouble(df.format(w[i]/h[i]));
                sum_r += r[i];
                MyLog.i(i + "----: " + w[i] + "," + h[i] + "  w/h= " + r[i] + " , sum_r=" + sum_r);
            }
            MyLog.i("minHeight----: " + minHeight + ", maxHeight----: " + maxHeight + ", screenWidth----: " + screenWidth);
            double H = screenWidth / sum_r;
            boolean resize = H<minHeight || H>maxHeight;
            double H2 = H < minHeight ? minHeight :
                    H > maxHeight ? maxHeight : H;
            MyLog.i("H----: " + H + ", H2----: " + H2 + ", resize----: " + resize);

            for (int i = 0; i < len; i++) {
                result[2*i] = (int) ((resize ? H : H2) * w[i] / h[i]);
                result[2*i+1] = (int) H2;
                MyLog.i(">>>>>>>>" + i + "----: " + result[2 * i] + "," + result[2 * i + 1]);
            }
        }
        return result;
    }

    private void playScreen(String uri) {
        Intent intent = new Intent(mContext, StillsViewPagerActivity.class);
        intent.putExtra(StillsViewPagerActivity.KEY_CURRENT, uri);
        intent.putExtra(StillsViewPagerActivity.KEY_CLIPLIST, getClipArray());
        MyLog.i("current:" + uri);
        mContext.startActivity(intent);
    }


    private ViewHolder getListViewHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.mIamge[0] = (ImageView) convertView.findViewById(R.id.clip);
        holder.mPlayIcon[0] = (ImageView) convertView.findViewById(R.id.play);

        holder.mIamge[1] = (ImageView) convertView.findViewById(R.id.clip1);
        holder.mPlayIcon[1] = (ImageView) convertView.findViewById(R.id.play1);

        holder.mIamge[2] = (ImageView) convertView.findViewById(R.id.clip2);
        holder.mPlayIcon[2] = (ImageView) convertView.findViewById(R.id.play2);
        return holder;
    }


    class ViewHolder {
        public ImageView[] mIamge = new ImageView[3];
        public ImageView[] mPlayIcon = new ImageView[3];
    }

    private class MyClickListener implements View.OnClickListener {
        MoinClip mMoinClip;
        public MyClickListener(MoinClip finalMClip) {
            mMoinClip = finalMClip;
        }
        @Override
        public void onClick(View view) {
            if (mMoinClip.getMedia() == 1) {
                playScreen(mMoinClip.getUri());
            } else if (mMoinClip.getMedia() == 2) {
                IPResourceManager.getInstance().playWebVideo(mContext, mMoinClip.getUri());
            }
        }
    }

    private ArrayList<String> getClipArray() {
        ArrayList<String> list = null;
        int index = 0;
        if (mClipList != null && mClipList.size() > 0) {
            List<MoinClip> mClip = null;
            list = new ArrayList<String>();
            for (int i = 0; i < mClipList.size(); i++) {
                mClip = mClipList.get(i);
                if(mClip != null && mClip.size() > 0) {
                    for (int j = 0; j < mClip.size(); j++) {
                        MoinClip mc = mClip.get(j);
                        if(mc.getMedia() == 1) {
                            MyLog.i("getClipArray " + (++index) + mc.getUri());
                            list.add(mc.getUri());
                        }
                    }
                }
            }

        }
        return list;
    }
}