package com.moinapp.wuliao.modules.ipresource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keyboard.utils.Utils;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.modal.TagInfo;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.ipresource.model.EmojiResource;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moying on 15/8/11.
 */
public class SearchActivity extends BaseActivity {
    ILogger MyLog = LoggerFactory.getLogger("ss");

    private static final int HISTORY_MAX = 9;//最多10个搜索历史
    private static final String SEPRATOR = "__";

    private Context mContext;
    private LayoutInflater mInflater;
    private Handler mHandler = new Handler();
    /**
     * 搜索历史
     */
    private static List<String> mHistoryGroup = new ArrayList<String>();
    /**
     * 热门标签
     */
    private List<String> mHotTagGroup = new ArrayList<String>();

    private ExpandableListView mList;
    private EditText searchEdit;
    private ImageView clear_iv;
    private TextView search_txt;
    private LinearLayout mLoadingLayout;

    private List<String> mGroupName = new ArrayList<String>();
    private MyListAdapter mAdapter;
    private String history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_layout);
        mContext = SearchActivity.this;
        mInflater = LayoutInflater.from(SearchActivity.this);

        EventBus.getDefault().register(this);

        history = IPResourcePreference.getInstance().getKeySearchEmjHistory();
        historyToView();

        initView();

        getHotTag();
    }

    private void getHotTag() {
        IPResourceManager.getInstance().getHotTag(2, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                List<TagInfo> list = (List<TagInfo>) obj;
                if (list != null && list.size() > 0) {
                    mHotTagGroup.clear();
                    for (int i = 0; i < list.size(); i++) {
                        mHotTagGroup.add(list.get(i).getName());
                    }
                    updateListView();
                }
            }

            @Override
            public void onNoNetwork() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(mContext, R.string.no_network);
                    }
                });
            }

            @Override
            public void onErr(Object object) {

            }
        });
    }

    private void initView() {
        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        searchEdit = (EditText) findViewById(R.id.search_et);
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                clear_iv.setVisibility(editable.toString().length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
        clear_iv = (ImageView) findViewById(R.id.clear_iv);
        clear_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEdit.setText(null);
            }
        });
        
        search_txt = (TextView) findViewById(R.id.search_txt);
        search_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndDoSearch(null);
            }
        });

        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_ll);
        mLoadingLayout.setOnClickListener(null);

        mList = (ExpandableListView) findViewById(R.id.hot_tag_list);
        mList.setGroupIndicator(null);

        mGroupName.add(getString(R.string.search_history));
        mGroupName.add(getString(R.string.hot_tag));

        mAdapter = new MyListAdapter(mContext);
        mList.setAdapter(mAdapter);

        updateListView();
    }

    private void updateListView() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                if (mHistoryGroup.size() > 0) {
                    mList.expandGroup(0);
                }
                if (mHotTagGroup.size() > 0) {
                    mList.expandGroup(1);
                }
            }
        });

    }

    private class MyListAdapter extends BaseExpandableListAdapter {

        private LayoutInflater mInflater;

        public MyListAdapter(Context context){
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            return getGroupSize();
        }

        @Override
        public int getChildrenCount(int i) {
            if (i == 0) {
                return mHistoryGroup.size();
            } else {
                return mHotTagGroup.size();
            }
        }

        private int getGroupSize(){
            return mGroupName.size();
        }

        @Override
        public Object getGroup(int i) {
            return mGroupName.get(i);
        }

        @Override
        public Object getChild(int i, int i2) {
            if(i == 0){
                return mHistoryGroup.get(i2);
            }else{
                return mHotTagGroup.get(i2);
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

            if (getChildrenCount(i) > 0) {
                view = mInflater.inflate(R.layout.search_list_group_view, null);
                TextView title = (TextView) view.findViewById(R.id.search_list_group_title);
                title.setText(mGroupName.get(i));
                // 有时候滑动列表时会把换一换和groupview上的控件刷得不可见，暂时先强制设置可见，fix这个bug
                view.setVisibility(View.VISIBLE);

                //加上这句listview就不能收起了
                view.setClickable(true);
            } else {
                // 如果是没有数据，不显示列表头，暂时没有好的方法，这里用一个空view代替
                view = mInflater.inflate(R.layout.empty_view, null);
            }
            return view;
        }

        @Override
        public View getChildView(final int group, final int position, boolean b, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if(view == null) {
                holder = new ViewHolder();
                view = mInflater.inflate(R.layout.search_list_item,null);
                holder.mNum = (TextView) view.findViewById(R.id.num);
                holder.mTitle = (TextView) view.findViewById(R.id.title);
                holder.mClear = (ImageView) view.findViewById(R.id.clear_history);
                holder.mDivider = (View) view.findViewById(R.id.divider);

                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }

            if (group == 0) {
                holder.mNum.setVisibility(View.GONE);
                holder.mTitle.setText(mHistoryGroup.get(position));
                holder.mClear.setVisibility(View.VISIBLE);
                holder.mClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mHistoryGroup.remove(position);
                        notifyDataSetChanged();
                        removeFromHistory(mHistoryGroup);
                    }
                });
                holder.mDivider.setVisibility(position == (mHistoryGroup.size() - 1) ? View.GONE : View.VISIBLE);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doSearch(mHistoryGroup.get(position));
                    }
                });
            } else if (group == 1) {
                holder.mNum.setVisibility(View.VISIBLE);
                holder.mNum.setText("" + (1 + position));
                holder.mTitle.setText(mHotTagGroup.get(position));
                holder.mClear.setVisibility(View.GONE);
                holder.mDivider.setVisibility(position == (mHotTagGroup.size() - 1) ? View.GONE : View.VISIBLE);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doSearch(mHotTagGroup.get(position));
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
            TextView mNum;
            TextView mTitle;
            ImageView mClear;
            View mDivider;
        }
    }


    private void historyToView() {
        if(!StringUtil.isNullOrEmpty(history)) {
            String[] arr = history.split(SEPRATOR);
            mHistoryGroup.clear();
            for (int i = 0; i < arr.length; i++) {
                mHistoryGroup.add(arr[i]);
            }
        }
    }

    /**如果txt是null，点击搜索*/
    private void checkAndDoSearch(String txt) {
        if(StringUtil.isNullOrEmpty(txt)) {
            String input = searchEdit.getText().toString().replaceAll(" ", "");
            if (StringUtil.isNullOrEmpty(input)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(SearchActivity.this, R.string.invalid_search);
                    }
                });
            } else {
                doSearch(input);
                boolean added = addToHistory(input);
                if(added) {
                    mHistoryGroup.add(0, input);
                    updateListView();
                }
            }
        } else {
            doSearch(txt);
        }
    }

    /**添加本次搜索内容到搜索历史*/
    private boolean addToHistory(String input) {
        boolean result = false;
        if(!StringUtil.isNullOrEmpty(history)) {
            boolean has = false;
            for (int i = 0; i < mHistoryGroup.size(); i++) {
                if(input.equals(mHistoryGroup.get(i))) {
                    has = true;
                    break;
                }
            }
            if(!has) {
                history = input + SEPRATOR + history;
                if(mHistoryGroup.size() > HISTORY_MAX) {
                    history = history.substring(0, history.lastIndexOf(SEPRATOR));
                    mHistoryGroup.remove(HISTORY_MAX);
                }
                IPResourcePreference.getInstance().setKeySearchEmjHistory(history);
                result = true;
            }
        } else {
            history = input;
            IPResourcePreference.getInstance().setKeySearchEmjHistory(history);
            result = true;
        }
        return result;
    }

    /**删除搜索历史*/
    private void removeFromHistory(List<String> input) {
        history = StringUtil.nullToEmpty(StringUtil.list2String(input, SEPRATOR));
        IPResourcePreference.getInstance().setKeySearchEmjHistory(history);
    }

    @Override
    public void onBackPressed() {
        if(isSearching) {
            mLoadingLayout.setVisibility(View.GONE);
        } else {
            finish();
        }
    }

    boolean isSearching = false;
    private void doSearch(final String keyWord) {
        Utils.closeSoftKeyboard(mContext);
        if(isSearching) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.toast(mContext, "正在搜索ing ");
                }
            });
            return;
        }
        isSearching = true;

        mLoadingLayout.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                IPResourceManager.getInstance().SearchtTag(2, keyWord, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        isSearching = false;
                        final List<EmojiResource> elist = (List<EmojiResource>) obj;

                        if(elist != null && elist.size() > 0) {
                            MyLog.i("获取了" + elist.size() + "条");
                        } else {
                            MyLog.i("获取0条");
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                ToastUtils.toast(mContext, "你获取了 " + elist.get(0).getName());
                                mLoadingLayout.setVisibility(View.GONE);
                            }
                        });
//                        EventBus.getDefault().post(new SearchEmojiEvent(elist));
                        gotoEmojiResource(keyWord, elist);
                        finish();
                    }

                    @Override
                    public void onNoNetwork() {
                        isSearching = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.toast(mContext, R.string.no_network);
                                mLoadingLayout.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onErr(Object object) {
                        isSearching = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.toast(mContext, R.string.search_failed);
                                mLoadingLayout.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void gotoEmojiResource(String keyword, List<EmojiResource> emojiList) {
        Bundle b = new Bundle();
        b.putSerializable("emoji_data", (ArrayList<EmojiResource>)emojiList);
        b.putString("ipname", keyword);
        AppTools.toIntent(ApplicationContext.getContext(), b, EmojiResourceActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public class SearchEmojiEvent {
        public SearchEmojiEvent(List<EmojiResource> elist) {
            mEmojiList = elist;
        }
        public List<EmojiResource> getmEmojiList() {
            return mEmojiList;
        }

        private List<EmojiResource> mEmojiList;

    }
}
