package com.moinapp.wuliao.modules.wowo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.AppConstants;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.login.LoginActivity;
import com.moinapp.wuliao.modules.login.LoginConstants;
import com.moinapp.wuliao.modules.login.LoginSuccJump;
import com.moinapp.wuliao.modules.wowo.model.WowoInfo;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liujiancheng on 15/6/13.
 * 窝首页的窝列表tab
 */
public class FragmentWoList extends Fragment {
    // ===========================================================
    // Constants
    // ===========================================================
    ILogger MyLog = LoggerFactory.getLogger(FragmentWoList.class.getSimpleName());

    // ===========================================================
    // Fields
    // ===========================================================
    /**
     * 已经关注的窝列表部分
     */
    private static List<WowoInfo> mAttentionGroup = new ArrayList<WowoInfo>();
    /**
     * 未关注（推荐）的窝列表部分
     */
    private List<WowoInfo> mSuggestGroup = new ArrayList<WowoInfo>();

    private ProgressDialog mWaitingDialog;
    private ExpandableListView mList;
    private MyListAdapter mAdapter;
    private List<String> mGroupName = new ArrayList<String>();
    private Handler mHandler = new Handler();
    protected Context mContext;
    private ImageLoader mImageLoader;

    /**
     * loading需要的控件
     */
    RelativeLayout rl_enter;
    LinearLayout lv_loading;
    LinearLayout lv_reload;

    // ===========================================================
    // Constructors
    // ===========================================================
    public FragmentWoList() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getApplicationContext();

        //初始化图片缓存
        mImageLoader = ImageLoader.getInstance();
        if(!mImageLoader.isInited()) {
            mImageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MyLog.i("FragmentWoList: onCreateView........................");
        View downloadView = inflater.inflate(R.layout.wo_list, container, false);
        mList = (ExpandableListView) downloadView.findViewById(R.id.wo_list);
        mList.setGroupIndicator(null);

        mGroupName.add("我的窝窝");
        mGroupName.add("推荐的窝窝");

        mAdapter = new MyListAdapter(mContext);
        mList.setAdapter(mAdapter);

        initLoadingView(downloadView);
        setLoadingMode(AppConstants.MODE_LOADING);
        getWoList(true);
        EventBus.getDefault().register(this);

        return downloadView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    private void initLoadingView(View root) {
        rl_enter = (RelativeLayout) root.findViewById(R.id.__enter);
        lv_loading = (LinearLayout) root.findViewById(R.id.__loading);
        lv_reload = (LinearLayout) root.findViewById(R.id.__reload);
        lv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadingMode(AppConstants.MODE_LOADING);
                getWoList(false);
            }
        });
    }

    private void setLoadingMode(int mode) {
        rl_enter.setVisibility(mode == AppConstants.MODE_OK ? View.GONE : View.VISIBLE);
        lv_loading.setVisibility(mode == AppConstants.MODE_LOADING ? View.VISIBLE : View.GONE);
        lv_reload.setVisibility(mode == AppConstants.MODE_RELOADING ? View.VISIBLE : View.GONE);
    }


    private Runnable hideLoadingRunnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    private void updateListView() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                if (mAttentionGroup.size() > 0) {
                    mList.expandGroup(0);
                }
                if (mSuggestGroup.size() > 0) {
                    mList.expandGroup(1);
                }
            }
        });


    }

    private void getWoList(boolean isFirst) {
        getMyWoList();
        getSuggestWoList(isFirst);
    }

    private void showReloading() {
        //要判断如果获取失败，是否已经从缓存里得到了数据，如果缓存里由数据则不显示重新加载的页面
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAttentionGroup.size() == 0) {
                    setLoadingMode(AppConstants.MODE_RELOADING);
                } else {
                    setLoadingMode(AppConstants.MODE_OK);
                }
            }
        });

    }

    /**
     * 联网获取我已经关注的窝列表
     */
    private void getMyWoList() {
        // 首先从本地缓存里获取我关注的窝列表
        List<WowoInfo> list = WowoManager.getInstance().getWoInfoFromCache(0);
        if (list != null && list.size() > 0) {
            mAttentionGroup.addAll(list);
            updateListView();
        }

        WowoManager.getInstance().getMyWoList(new IWoListener() {
            @Override
            public void onGetWowoSucc(List<WowoInfo> wowoInfoList) {
                MyLog.i("getMyWoList.onGetWowoSucc: wowInfolist.size =" + wowoInfoList.size());
                mAttentionGroup.clear();
                mAttentionGroup.addAll(wowoInfoList);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoadingMode(AppConstants.MODE_OK);
                    }
                });

                updateListView();
            }

            @Override
            public void onSubscribeResult(int result) {

            }

            @Override
            public void onNoNetwork() {
                MyLog.i("getMyWoList no network");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(getActivity(), R.string.no_network);
                    }
                });
                showReloading();
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("getMyWoList err " + object);
                showReloading();
            }
        });
    }

    /**
     * 联网获取未关注的窝列表
     * isFirst: 是否第一次获取的标记
     */
    private void getSuggestWoList(boolean isFirst) {
        // 首先从本地缓存里获取推荐的窝列表
        if (isFirst) {
            List<WowoInfo> list = WowoManager.getInstance().getWoInfoFromCache(1);
            if (list != null && list.size() > 0) {
                mSuggestGroup.addAll(list);
                updateListView();
            }
        }

        WowoManager.getInstance().getSuggestWoList(getLastWoid(), new IWoListener() {
            @Override
            public void onGetWowoSucc(List<WowoInfo> wowoInfoList) {
                MyLog.i("getSuggestWoList.onGetWowoSucc: wowInfolist.size =" + wowoInfoList.size());
                if (wowoInfoList == null || wowoInfoList.size() == 0) {
                    showNodata();
                    return;
                }

                //对获取到的wowo list去重
                List<WowoInfo> tempList = new ArrayList<WowoInfo>();
                for (WowoInfo wo : wowoInfoList) {
                    if (!isWoInList(wo, tempList)) {
                        tempList.add(wo);
                    }
                }
                if (tempList.size() > 0) {
                    mSuggestGroup.clear();
                    mSuggestGroup.addAll(tempList);
                    updateListView();
                }
            }

            @Override
            public void onSubscribeResult(int result) {

            }

            @Override
            public void onNoNetwork() {
                MyLog.i("getSuggestWoList no network");
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("getSuggestWoList err");
            }
        });
    }

    private void showNodata() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.toast(getActivity(), "窝窝已全部加载");
            }
        });
    }

    private boolean isWoInList(WowoInfo wo, List<WowoInfo> list) {
        if (list == null || list.size() == 0) {
            return false;
        }
        for (WowoInfo wowo:list) {
            if (wowo.getId() == wo.getId()){
                return true;
            }
        }
        return false;
    }

    private int getLastWoid() {
        if (mSuggestGroup.size() == 0) {
            return 0;
        }

        return mSuggestGroup.get(mSuggestGroup.size()-1).getId();
    }

    private class MyListAdapter extends BaseExpandableListAdapter {

        private LayoutInflater mInflater;
        private Context mContext;

        public MyListAdapter(Context context){
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            return getGroupSize();
        }

        @Override
        public int getChildrenCount(int i) {
            if (i == 0) {
                return mAttentionGroup.size();
            } else {
                return mSuggestGroup.size();
            }
        }

        private int getGroupSize(){
            return 2;
        }

        @Override
        public Object getGroup(int i) {
            return mGroupName.get(i);
        }

        @Override
        public Object getChild(int i, int i2) {
            if(i == 0){
                return mAttentionGroup.get(i2);
            }else{
                return mSuggestGroup.get(i2);
            }
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i2) {
            return i2;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            if (i == 1) {
                view = mInflater.inflate(R.layout.wo_list_group_view,null);
                TextView title = (TextView) view.findViewById(R.id.wo_list_group_title);
                Button switchWo = (Button) view.findViewById(R.id.switch_wo);
                title.setText(mGroupName.get(i));
                // 有时候滑动列表时会把换一换和groupview上的控件刷得不可见，暂时先强制设置可见，fix这个bug
                view.setVisibility(View.VISIBLE);
                switchWo.setVisibility(View.VISIBLE);

                switchWo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //联网获取新的未关注的窝列表
                        getSuggestWoList(false);
                    }
                });

                //加上这句listview就不能收起了
                view.setClickable(true);
            } else if (i == 0) {
                //如果是已关注窝的部分，不显示列表头，暂时没有好的方法，这里用一个空view代替
                view = mInflater.inflate(R.layout.empty_view, null);
            }

            return view;
        }

        private WowoInfo getmInstallAppItemByPosition(int group, int position) {
            List<WowoInfo> list = null;
            WowoInfo item = null;
            if (group == 0) {
                list = mAttentionGroup;
            } else {
                list = mSuggestGroup;
            }
            if (list.size() != 0 && position < list.size()) {
                item = list.get(position);
            }

            return item;
        }

        @Override
        public View getChildView(final int i, final int i2, boolean b, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if(view == null){
                holder = new ViewHolder();
                view = mInflater.inflate(R.layout.wo_info_item,null);
                holder.woCover = (ImageView) view.findViewById(R.id.wo_cover);
                holder.woName = (TextView) view.findViewById(R.id.wo_name);
                holder.commentCount = (TextView) view.findViewById(R.id.comment_count);
                holder.postNumber = (TextView) view.findViewById(R.id.post_number);
                holder.tags = (LinearLayout) view.findViewById(R.id.wo_tags_ll);
                holder.woDesc = (TextView) view.findViewById(R.id.wo_desc);
                holder.woRule = (ImageView) view.findViewById(R.id.wo_rule);
                holder.woAttention = (ImageView) view.findViewById(R.id.image_attention);

                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }

            final WowoInfo item = getmInstallAppItemByPosition(i,i2);
            if (item != null) {
                //给窝信息赋值
                if (item.getIcon() != null)
                    mImageLoader.displayImage(item.getIcon().getUri(), holder.woCover, BitmapUtil.getImageLoaderOption());
                holder.woName.setText(item.getName());
                holder.commentCount.setText("关注：" + String.valueOf(item.getUserCount()));
                holder.postNumber.setText("帖子：" + String.valueOf(item.getPostCount()));

                String tag = getTags(item.getTags());
                holder.tags.removeAllViews();
                if(!StringUtil.isNullOrEmpty(tag)) {
                    String[] tags = tag.split(",");
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER_VERTICAL;
                    params.rightMargin = getResources().getDimensionPixelSize(R.dimen.icon_margin);
                    for (int j = 0; j < tags.length; j++) {
                        MyLog.i("tag=="+tags[j]);
                        TextView tv_tag = new TextView(mContext);
                        tv_tag.setText(tags[j]);
                        tv_tag.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
                        tv_tag.setTextColor(mContext.getResources().getColor(R.color.common_text_grey));
                        tv_tag.setPadding(5, 1, 5, 1);
                        tv_tag.setBackground(mContext.getResources().getDrawable(R.drawable.tag_btn_grey_bg));
                        holder.tags.addView(tv_tag, params);
                    }
                }

                holder.woDesc.setText(item.getIntro());

                //窝规则的控件不可见
                holder.woRule.setVisibility(View.GONE);

                //如果是未关注窝列表，设置关注图标为可见
                if (i == 1) {
                    holder.woAttention.setVisibility(View.VISIBLE);
                    holder.woAttention.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //点击关注窝
                            subscribeWo(item);
                        }
                    });
                } else {
                    holder.woAttention.setVisibility(View.GONE);
                }

                //点击该控件进入窝窝详情
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, WoPostListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Bundle b = new Bundle();
                        b.putSerializable("wo_id", item.getId());
                        intent.putExtras(b);
                        mContext.startActivity(intent);
                    }
                });
            }
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i2) {
            return false;
        }

        private class ViewHolder{
            ImageView woCover;
            TextView woName;
            TextView commentCount;
            TextView postNumber;
            LinearLayout tags;
            TextView woDesc;
            ImageView woRule;
            ImageView woAttention;//关注窝的图标
        }
    }

    /**
     * 关注窝的处理
     * @param wo：窝对象
     */
    private void subscribeWo(final WowoInfo wo) {
        // 如果未登陆，提示登陆
        if (!ClientInfo.isUserLogin()) {
            ToastUtils.toast(mContext, "您还没有登陆，请登陆！");
            Bundle b = new Bundle();
            b.putBoolean(LoginActivity.KEY_NEED_RETURN, true);
            b.putInt(LoginActivity.KEY_FROM, LoginConstants.FRAGMENT_WOWO);
            AppTools.toIntent(mContext, b, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
            return;
        }
        WowoManager.getInstance().subscribeWo(wo.getId(), 1, new IWoListener() {
            @Override
            public void onGetWowoSucc(List<WowoInfo> wowoInfoList) {

            }

            @Override
            public void onSubscribeResult(int result) {
                MyLog.i("onSubscribeResult...result =" + result);
                if (result == 1) {
                    //关注成功，更新list
                    for (int j = 0; j < mSuggestGroup.size(); j++) {
                        if (mSuggestGroup.get(j).getId() == wo.getId()) {
                            mSuggestGroup.remove(j);
                            mAttentionGroup.add(wo);
                            break;
                        }
                    }
                    updateListView();
                }
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("subscribeWo no network");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(getActivity(), R.string.no_network);
                    }
                });
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("subscribeWo err " + object);
            }
        });
    }


    private String getTags(List<String> tags) {
        String result = null;
        StringBuilder woTag = new StringBuilder();
        if (tags != null && tags.size() > 0) {
            for (String tag : tags) {
                woTag.append(tag).append(",");
            }
            String tag = woTag.toString();
            if (!TextUtils.isEmpty(tag))
                result = tag.substring(0, tag.length() - 1);

        }
        return result;
    }

    public static List<WowoInfo> getAttentionWoList() {
        return mAttentionGroup;
    }

    public void onEvent(WowoRulesActivity.AttentionEvent event) {
        MyLog.i("FragmentWoList received onEvent:AttentionEvent!!");
        if (event != null) {
            if (event.getStatus() == 1) {
                //成功关注了一个窝，从推荐窝里面remove，我的窝里add
                for (int i = 0; i < mSuggestGroup.size(); i ++) {
                    if (mSuggestGroup.get(i).getId() == event.getWo().getId()) {
                        mSuggestGroup.remove(i);
                    }
                    if (!mAttentionGroup.contains(event.getWo())) {
                        mAttentionGroup.add(event.getWo());
                    }
                }
            } else if (event.getStatus() == 0) {
                // 成功取消关注了一个窝，我的窝里remove
                for (int i = 0; i < mAttentionGroup.size(); i ++) {
                    if (mAttentionGroup.get(i).getId() == event.getWo().getId()) {
                        mAttentionGroup.remove(i);
                    }
                }
            }
            updateListView();
        }
    }

    public void onEvent(LoginSuccJump.LoginSuccessEvent event) {
        MyLog.i("onEvent:LoginSuccessEvent");
        mAttentionGroup.clear();
        mSuggestGroup.clear();
        getWoList(false);
    }
}
