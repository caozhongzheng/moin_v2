package com.moinapp.wuliao.modules.ipresource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.ui.AsyncImageView;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.commons.ui.XListView;
import com.moinapp.wuliao.modules.ipresource.model.EmojiInfo;
import com.moinapp.wuliao.modules.ipresource.model.IPResource;
import com.moinapp.wuliao.utils.AnimationUtil;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.DisplayUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujiancheng on 15/5/26.
 * ip列表页：分两个tab 最moin ip和最热 ip
 */
public class IPResourceListActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final ILogger MyLog = LoggerFactory.getLogger("IPResourceListActivity");
    private static final String COLUMN = "column";

    private static final int COLUMN_COUNT = 2; //2个栏目
    private static final int CURSOR_ANIMATION_MILLISECONDS = 300;
    private static final int ITEMS_PER_ROW = 3; //每行3个ip
    private static final int ITEMS_PER_PAGE = 10; //每页10个ip
    private Context mContext;

    private RelativeLayout mTitle;
    //
    private ViewPager viewPager;
    private TextView tvColumnNew, tvColumnTop;
    private ImageView ivCursor;
    private int screenWidth;
    private int cursorWidth;
    private int cursorLastX;
    private int cursorPadingPx;
    private RelativeLayout.LayoutParams lpCursor;
    private ImageView mBack;

    // 数据
    private XListView[] mListView;
    private IPArrListAdapter[] mIPArrListAdapters;
    private int currIndex = 0;// 主题列表ViewPager当前页卡编号
    private int scrollBy;

    private ImageLoader mImageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = IPResourceListActivity.this;

        cursorPadingPx = DisplayUtil.dip2px(mContext, 6);
        scrollBy = DisplayUtil.dip2px(mContext, 10);
        setContentView(R.layout.ipres_list);

        //初始化图片缓存
        mImageLoader = ImageLoader.getInstance();
        if(!mImageLoader.isInited()) {
            mImageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }

        initLoadingView();
        setLoadingMode(MODE_LOADING);
        int column = getColumn();
        findViews(column);
    }

    @Override
    protected void reloadHandle() {
        super.reloadHandle();
        getIPList(0, null);
        getIPList(1, null);
    }

    private int getColumn() {
        int column = 0;
        Intent intent = getIntent();
        if (intent != null) {
            column = intent.getIntExtra(COLUMN, 0);
        }
        return column;
    }

    private void findViews(int column) {
        // 初始化栏目指示器
        ivCursor = (ImageView) findViewById(R.id.iv_cursor);
        screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;// 获取分辨率宽度
        cursorWidth = (int) (screenWidth / 2 + 0.5f);
        lpCursor = new RelativeLayout.LayoutParams(cursorWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        ivCursor.setPadding(2 * cursorPadingPx, 0, cursorPadingPx, 0);
        ivCursor.setLayoutParams(lpCursor);
        // 初始化栏目
        tvColumnNew = (TextView) findViewById(R.id.tv_most_moin_ip);
        tvColumnNew.setTextColor(getResources().getColor(R.color.tab_indicator_text_selected));
        tvColumnTop = (TextView) findViewById(R.id.tv_most_hot_ip);
        // 初始化栏目列表
        viewPager = (ViewPager) findViewById(R.id.vp_list);
        viewPager.setAdapter(new IPResourcePagerAdapter());
        viewPager.setCurrentItem(column);
        viewPager.setOnPageChangeListener(new IPOnPageChangeListener());

        //初始化指示器指向column
        setIndicator(column);

        mIPArrListAdapters = new IPArrListAdapter[COLUMN_COUNT];
        for(int i=0; i<COLUMN_COUNT; i++){
            mIPArrListAdapters[i] = new IPArrListAdapter();
        }
        mListView = new XListView[COLUMN_COUNT];
        tvColumnNew.setOnClickListener(this);
        tvColumnTop.setOnClickListener(this);

        mBack = (ImageView) findViewById(R.id.imageView);
        mBack.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * 主题模块，栏目切换PagerAdapter
     */
    public class IPResourcePagerAdapter extends PagerAdapter {

        public IPResourcePagerAdapter() {
        }

        @Override
        public int getCount() {
            return COLUMN_COUNT;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final LinearLayout ipListLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.moin_ip_list, null);

            final int column = position;
            View.OnClickListener clickListener = new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    getIPList(column, null);
                }

            };

            final XListView listView = (XListView) ipListLayout.findViewById(R.id.lv_list);
            mListView[position] = listView;
            listView.setAdapter(mIPArrListAdapters[position]);
            listView.setPullLoadEnable(true);
            listView.setPullRefreshEnable(false);
            listView.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {

                }

                @Override
                public void onLoadMore() {
                    loadMoreData();
                }
            });

            container.addView(ipListLayout, 0);

//            int offset = mIPArrListAdapters[position].getIPList().size();
            if (viewPager.getCurrentItem() == position) {//只加载当前栏目
                getIPList(position, null);
            }
            return ipListLayout;
        }
    }

    /**
     * ip列表viewPager，栏目切换监听。
     */
    public class IPOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            MyLog.i("IPOnPageChangeListener.MyOnPageChangeListener.onPageSelected() currIndex=" + position);
            setIndicator(position);
            int offset = mIPArrListAdapters[currIndex].getIPList().size();
            if(offset == 0) {//ListView为空，需要重新加载
                getIPList(currIndex, null);
            }
        }
    }

    private void setIndicator(int position) {
       currIndex = position;

       tvColumnNew.setTextColor(getResources().getColor(R.color.common_title_grey));
       tvColumnTop.setTextColor(getResources().getColor(R.color.common_title_grey));
       switch (currIndex) {
           case 0:
               tvColumnNew.setTextColor(getResources().getColor(R.color.common_text_main));
               AnimationUtil.moveTo(ivCursor, CURSOR_ANIMATION_MILLISECONDS, cursorLastX, 0, 0, 0);
               ivCursor.setPadding(2 * cursorPadingPx, 0, cursorPadingPx, 0);
               cursorLastX = 0;
               break;
           case 1:
               tvColumnTop.setTextColor(getResources().getColor(R.color.common_text_main));
               AnimationUtil.moveTo(ivCursor, CURSOR_ANIMATION_MILLISECONDS, cursorLastX, cursorWidth, 0, 0);
               ivCursor.setPadding(cursorPadingPx, 0, 2 * cursorPadingPx, 0);
               cursorLastX = cursorWidth;
               break;
           default:
               break;
       }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_most_moin_ip) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.tv_most_hot_ip) {
            viewPager.setCurrentItem(1);
        } else if (id == R.id.imageView) {
            finish();
        }
    }

    /**
     * 主题列表Adapter
     */
    private class IPArrListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<IPResource[]> mIPArrList;
        private int mLast = -1;

        public IPArrListAdapter() {
            this.mInflater = LayoutInflater.from(mContext);
            this.mIPArrList = new ArrayList<IPResource[]>();
        }

        public List<IPResource[]> getIPList(){
            return mIPArrList;
        }

        @Override
        public int getCount() {
            int count = 0;
            if (mIPArrList != null) {
                return mIPArrList.size();
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.moin_ip_list_item3, null);
                for (int i = 0; i < 3; i++) {
                    switch (i) {
                        case 0:
                            holder.rl[i] = (LinearLayout) convertView.findViewById(R.id.moin_ip_list_item0);
                            break;
                        case 1:
                            holder.rl[i] = (LinearLayout) convertView.findViewById(R.id.moin_ip_list_item1);
                            break;
                        case 2:
                            holder.rl[i] = (LinearLayout) convertView.findViewById(R.id.moin_ip_list_item2);
                            break;
                    }
                    holder.ivPreview[i] = (AsyncImageView) holder.rl[i].findViewById(R.id.iv_preview);
                    holder.tvName[i] = (TextView) holder.rl[i].findViewById(R.id.tv_name);
                    holder.tvDesc[i] = (TextView) holder.rl[i].findViewById(R.id.desc);

                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // 赋值
            final IPResource[] ips = mIPArrList.get(position);
            if(mLast != position){
                mLast = position;
            }
            for (int i = 0; i < 3; i++) {
                if(ips[i] != null && !TextUtils.isEmpty(ips[i].get_id())){
                    holder.rl[i].setVisibility(View.VISIBLE);
                    holder.tvName[i].setText(ips[i].getName());
                    MyLog.i("mContext==" + mContext);
                    MyLog.i("ips[i]==" + ips[i]);
                    MyLog.i("holder.tvDesc[i]==" + holder.tvDesc[i]);
                    holder.tvDesc[i].setText(IPResourceManager.getIpTypeName(mContext, ips[i].getType()) + "/"
                            + StringUtil.formatDate(ips[i].getReleaseDate(), IPResourceConstants.IP_RELEASE_DATE_FORMAT));
                    holder.ivPreview[i].setTag(ips[i].get_id());
                    String url = null;
                    if (ips[i].getIcon() != null) {
                        url = ips[i].getIcon().getUri();
                    }
                    try {
                        mImageLoader.displayImage(url, holder.ivPreview[i], BitmapUtil.getImageLoaderOption());
                    } catch (OutOfMemoryError e) {
                        MyLog.e(e);
                    } catch (Exception e) {
                        MyLog.e(e);
                    }
//                    holder.ivPreview[i].loadImage(url, null, R.drawable.default_img);
                    final int index = i;
                    /**主题列表item点击事件*/
                    holder.ivPreview[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            Bundle b = new Bundle();
                            b.putString("ip_id", ips[index].get_id());
                            b.putBoolean("view_ip", true);
                            AppTools.toIntent(mContext, b, IPMoinClipActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                    });
                } else {
                    holder.rl[i].setVisibility(View.INVISIBLE);
                }
            }

            return convertView;
        }

    }

    private static class ViewHolder {
        LinearLayout[] rl = new LinearLayout[3];
        AsyncImageView[] ivPreview = new AsyncImageView[3];
        TextView[] tvName = new TextView[3];
        TextView[] tvDesc = new TextView[3];
    }

    /**获取主题列表*/
    private void getIPList(int column, String offset){
        MyLog.i("getIPListFromCache: column=" + column + " offset=" + offset);
        IPResourceManager ipMgr = IPResourceManager.getInstance();

        boolean bStarted = false;
        // 缓存过期，或者初始进入时网络是wifi情况下，需要联网获取
        if (offset == null) {
            bStarted = true;
            ipMgr.getIPList(null, column, new BasicIPListListener(column, offset, false));
        }
        // 下拉刷新
        if (offset != null && !bStarted) {
            //todo: 计算lastid
            ipMgr.getIPList(offset, column, new BasicIPListListener(column, offset,true));
        }

    }

    /** 刷新列表视图*/
    private void updateIPList(int column, String offset, List<IPResource> ips) {
        MyLog.d("updateIPList: column=" + column + " offset=" + offset);

        if (ips == null || ips.size() == 0){
            return;
        }

        if (offset == null) {
            mIPArrListAdapters[column].getIPList().clear();
        }

        //看最后一行是否是有空余位置（不足3个）
        int size = mIPArrListAdapters[viewPager.getCurrentItem()].getIPList().size();
        if (size > 0) {
            IPResource[] tmp = mIPArrListAdapters[viewPager.getCurrentItem()].getIPList().get(size - 1);
            int count = 0;
            for (int i = 0; i < tmp.length; i++) {
                if (ips.size() == count) {
                    break;
                }
                //看最后一行是否有空位，有的话把ips的前几个填满现有列表的最后一行
                if (tmp[i] == null || TextUtils.isEmpty(tmp[i].get_id())) {
                    tmp[i] = ips.get(count);
                    count++;
                    MyLog.i("ljc:i=" + i);
                }
            }

            //把ips的前几个已经填满现有列表最后一行的items去掉
            MyLog.i("ljc:count=" + count);
            for (int j = 0; j < count; j++) {
                if (ips.size() > 0 && ips.get(0) != null) {
                    ips.remove(0);
                }
            }
        }
        List<IPResource[]> ipList = convertIPList(ips);
        if (ipList != null || ipList.size() > 0) {
            mIPArrListAdapters[column].getIPList().addAll(ipList);
            mIPArrListAdapters[column].notifyDataSetChanged();
        }

//        if(offset != null) {
//            mListView[column].scrollBy(0, scrollBy);
//        }
    }

    /**
     *
     */
    private List<IPResource[]> convertIPList(List<IPResource> ipResourceList) {
        if (ipResourceList == null || ipResourceList.size() == 0) {
            return null;
        }
        // 把ip列表分成每行3个的适用于ui的列表
        List<IPResource[]> listIps = new ArrayList<IPResource[]>();
        int index = 0;
        IPResource[] ips = null;
        for (IPResource ip: ipResourceList) {
            if (index == 0) {
                ips = new IPResource[ITEMS_PER_ROW];
            }
            ips[index] = ip;
            if (index == ITEMS_PER_ROW -1) {
                listIps.add(ips);
                index = 0;
            } else {
                index++;
            }
        }
        if (ipResourceList.size() % ITEMS_PER_ROW != 0){
            listIps.add(ips);
        }

        return listIps;
    }

    /**从网络获取主题列表监听*/
    private class BasicIPListListener implements IPresListListener {
        private int mColumn;
        private String mOffset;
        private boolean mPullrefresh;
        public BasicIPListListener(int column, String offset,boolean pullrefresh) {
            mColumn = column;
            mOffset = offset;
            mPullrefresh = pullrefresh;
        }

        @Override
        public void onErr(Object object) {
            MyLog.d("onErr");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MyLog.d("onErr.runOnUiThread");

                    setLoadingMode(MODE_OK);
                    if (mOffset != null) {
                        ToastUtils.toast(mContext, R.string.list_end);
                    }
                    mListView[mColumn].setLoadMoreOver();
                }
            });
        }

        @Override
        public void onNoNetwork() {
            MyLog.d("onNoNetwork");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MyLog.d("onNoNetwork.runOnUiThread");

                    if (mIPArrListAdapters[mColumn].getIPList().size() > 0) {
                        setLoadingMode(MODE_OK);
                    } else {
                        setLoadingMode(MODE_RELOADING);
                    }
                    ToastUtils.toast(mContext, R.string.no_network);
                }
            });
        }


		@Override
        public void onGetIPListSucc(final int column, final String offset, final List<IPResource> ipResourceList) {
			MyLog.d("onGetIPListSucc: column=" + column + "iplist" + ipResourceList);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLoadingMode(MODE_OK);
                    MyLog.d("onGetIPListSucc.runOnUiThread: column=" + column + " offset=" + offset + " iplist=" + ipResourceList);

                    if (ipResourceList != null) {
                        if (ipResourceList.size() == 0 && mIPArrListAdapters[column].getIPList().size() == 0) {
                            onErr(null);
                        } else {
                            //视为末页
                            if (ipResourceList.size() < ITEMS_PER_PAGE) {
                                mListView[mColumn].setLoadMoreOver();
                                ToastUtils.toast(mContext, R.string.list_end);
                            }
                            ;
                            updateIPList(column, offset, ipResourceList);
                        }
                    }
                }
            });
		}

        @Override
        public void onGetHotIPSucc(IPResource ipResourceList) {

        }
    }

    void toast(String text){
        ToastUtils.toast(mContext, text);
    }


    @Override
    public void onResume() {
        MyLog.d("IPResourceListActivity.onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        MyLog.d("IPResourceListActivity.onPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        MyLog.d("IPResourceListActivity.onDestroy");
        super.onDestroy();
    }

    public void loadMoreData() {
        //如果是自动加载,可以在这里放置异步加载数据的代码
        getIPList(currIndex, getLastIpid());
    }

    private String getLastIpid() {
        String lastid = null;
        int size = mIPArrListAdapters[viewPager.getCurrentItem()].getIPList().size();
        if(size <= 0) {
            return "";
        }
        IPResource[] tmp = mIPArrListAdapters[viewPager.getCurrentItem()].getIPList().get(size - 1);
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i] != null && !TextUtils.isEmpty(tmp[i].get_id()))
                lastid = tmp[i].get_id();
        }

        MyLog.i("getLastIpid="+lastid);
        return lastid;
    }
}
