package com.moinapp.wuliao.modules.ipresource;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.MoinActivity;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.ui.MoveBackView;
import com.moinapp.wuliao.commons.ui.XListView;
import com.moinapp.wuliao.modules.ipresource.model.BannerInfo;
import com.moinapp.wuliao.modules.ipresource.model.IPResource;
import com.moinapp.wuliao.modules.ipresource.model.Information;
import com.moinapp.wuliao.modules.wowo.IPostListener;
import com.moinapp.wuliao.modules.wowo.WowoManager;
import com.moinapp.wuliao.modules.wowo.model.CommentInfo;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;
import com.moinapp.wuliao.modules.wowo.model.WowoInfo;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.DisplayUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moying on 15/5/13.
 */
public class FragmentIPres extends Fragment implements AdapterView.OnItemClickListener, XListView.IXListViewListener {

    private static final ILogger MyLog = LoggerFactory.getLogger(IPResourceModule.MODULE_NAME);
    private Context context;
    private ImageLoader imageLoader;

    /*banner*/
    private FrameLayout mBannerLayout;
    private IPHotBannerUI mBannerUI;
    /*iplist*/
    private LinearLayout mLoadFailedLayout;
    private MoveBackView mLoadingView;
    private ImageView mNoNetworkView;
    private ImageView mEmptyView;
    /*XListView*/
    private XListView mXListView;
    private View mHeaderView;
    private WoPostAdaptor mAdapter;
    private List<Information> mInformationList;
    private List<PostInfo> mPostInfoList = new ArrayList<PostInfo>();;
    private Handler mHandler;
    private int start = 0;
    private static int refreshCnt = 0;
    Dialog dia;

    /**最MOIN, 最热*/
    private LinearLayout zmoin6, zhot3;//ip_zmoin_homepg
    private RelativeLayout zmoin6_r1_i1, zmoin6_r1_i2, zmoin6_r1_i3,
            zmoin6_r2_i1, zmoin6_r2_i2, zmoin6_r2_i3,
            zhot3_r1_i1, zhot3_r1_i2, zhot3_r1_i3;

    private ImageView zmoin6_r1_i1_iv, zmoin6_r1_i2_iv, zmoin6_r1_i3_iv,
            zmoin6_r2_i1_iv, zmoin6_r2_i2_iv, zmoin6_r2_i3_iv,
            zhot3_r1_i1_iv, zhot3_r1_i2_iv, zhot3_r1_i3_iv;

    private TextView zmoin6_r1_i1_desc, zmoin6_r1_i2_desc, zmoin6_r1_i3_desc,
            zmoin6_r2_i1_desc, zmoin6_r2_i2_desc, zmoin6_r2_i3_desc,
            zhot3_r1_i1_desc, zhot3_r1_i2_desc, zhot3_r1_i3_desc;

    private TextView zmoin6_r1_i1_title, zmoin6_r1_i2_title, zmoin6_r1_i3_title,
            zmoin6_r2_i1_title, zmoin6_r2_i2_title, zmoin6_r2_i3_title,
            zhot3_r1_i1_title, zhot3_r1_i2_title, zhot3_r1_i3_title;

    private RelativeLayout[] zitem;
    private ImageView[] ziv;
    private TextView[] ztitle, zdesc;

    private RelativeLayout wowo_header;
    private TextView wowo_header_name, wowo_header_all, mBannerTitle, mBannerType;
    // 数据
//    private IPresListAdapter mIPresListAdapter;
    private ArrayList<IPResource> ip_data_list;
    private List<IPResource> list1;
    private List<IPResource> list2;
    private static final int MOIN_LEN = 6;
    private static final int ZHOT_LEN = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();
        if(ApplicationContext.getContext() == null) {
            ApplicationContext.setContext(context);
        }

        imageLoader = ImageLoader.getInstance();
        if(!imageLoader.isInited()) {
            imageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }

        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ip_view = inflater.inflate(R.layout.ipres_layout, container, false);

        mXListView = (XListView) ip_view.findViewById(R.id.lv);
        mXListView.setPullLoadEnable(true);
        mXListView.setPullRefreshEnable(false);
        mXListView.freezeRefresh();
        mHeaderView = inflater.inflate(R.layout.ipres_header, container, false);

        // 初始化Banner控件
        mBannerLayout = (FrameLayout) mHeaderView.findViewById(R.id.bannerLayout);
        mBannerUI = new IPHotBannerUI(mBannerLayout, new Handler());
        mBannerUI.enableAutoSlide();

        showTutorial();

//        mBannerTitle = (TextView) mHeaderView.findViewById(R.id.ip_name);
//        mBannerType = (TextView) mHeaderView.findViewById(R.id.ip_type);

        zitem = new RelativeLayout[MOIN_LEN + ZHOT_LEN];
        ziv = new ImageView[MOIN_LEN + ZHOT_LEN];
        ztitle = new TextView[MOIN_LEN + ZHOT_LEN];
        zdesc = new TextView[MOIN_LEN + ZHOT_LEN];
        // 初始化Moin+Hot
        initZmoinView(mHeaderView);
        initZhotView(mHeaderView);
        setListener();

        // 初始化wowo

        View wolist = mHeaderView.findViewById(R.id.ip_wolist);
        TextView desc = (TextView) wolist.findViewById(R.id.desc);
        desc.setText(R.string.ip_homep_wowotop);
        wolist.findViewById(R.id.more).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO view wowo top channel
                MyLog.v("view wowo top");
                Bundle b = new Bundle();
                //todo 跳转到窝窝频道的所在页面，column具体是几可能会会变
                b.putInt(MoinActivity.KEY_FRAGMENT_INDEX_TO_SHOW, 2);
                AppTools.toIntent(getActivity(), b, MoinActivity.class);
            }
        });

/**
        wowo_header = (RelativeLayout) mHeaderView.findViewById(R.id.wowo_header);
        wowo_header_name = (TextView) wowo_header.findViewById(R.id.desc);
        wowo_header_name.setText(R.string.ip_homep_wowotop);
        wowo_header_all = (TextView) wowo_header.findViewById(R.id.more);
        wowo_header_all.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO view wowo top channel
                MyLog.v("view wowo top");
            }
        });
**/
        mXListView.addHeaderView(mHeaderView);
//        mIPresListAdapter = new IPresListAdapter(ip_data_list);
        mAdapter = new WoPostAdaptor(this.getActivity(), mPostInfoList);
        mXListView.setAdapter(mAdapter);
        mXListView.setOnItemClickListener(FragmentIPres.this);
        mXListView.setXListViewListener(this);
        mHandler = new Handler();

        // 初始化loadFailed
        mLoadFailedLayout = (LinearLayout) ip_view.findViewById(R.id.ll_load_failed);
        mLoadingView = (MoveBackView) ip_view.findViewById(R.id.move_back);
        mNoNetworkView = (ImageView) ip_view.findViewById(R.id.iv_nonetwork);
        mEmptyView = (ImageView) ip_view.findViewById(R.id.iv_empty);
        OnClickListener clickListener = new OnClickListener(){
            @Override
            public void onClick(View v) {
                MyLog.i("click to get ip list:0");
//                getIPList(0);
            }
        };
        mLoadFailedLayout.setOnClickListener(clickListener);
        mNoNetworkView.setOnClickListener(clickListener);
        mEmptyView.setOnClickListener(clickListener);

        // 开始联网取帖子数据
        getWoSuggestPost(true);
        return ip_view;
    }

    private void showTutorial() {
        if(!isHidden() && IPResourcePreference.getInstance().isFirstBanner()) {
            List<BannerInfo> bannerList = IPResourceManager.getInstance().getBannersFromCache();
            if (bannerList != null) {
                mBannerUI.setBanners(bannerList);
                showHelp();
            }
        }
    }

    private void showHelp() {
        if(dia != null) {
            return;
        }

        boolean isFirst = IPResourcePreference.getInstance().isFirstBanner();
        if(isFirst) {
            try {
                Context context = getActivity();
                dia = new Dialog(context, R.style.edit_AlertDialog_style);
                dia.setContentView(R.layout.banner_help_dialog);

                OnClickListener dismissListener = new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IPResourcePreference.getInstance().setFirstEnterBanner(false);
                        dia.dismiss();
                    }
                };

                dia.findViewById(R.id.dialog_rl).setOnClickListener(dismissListener);
                ImageView imageView = (ImageView) dia.findViewById(R.id.help_banner_img);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                params.topMargin = getResources().getDimensionPixelSize(R.dimen.tutorial_hot_banner_mt);
                MyLog.i("params.topMargin = " + params.topMargin);
                if(Build.VERSION.SDK_INT < 19) {
                    params.topMargin += DisplayUtil.dip2px(context, 27);
                    MyLog.i("params.topMargin [19] = " + params.topMargin);
                }
                imageView.setLayoutParams(params);
                imageView.setOnClickListener(dismissListener);
                dia.show();

                dia.setCanceledOnTouchOutside(true); // Sets whether this dialog is
                // canceled when touched outside
                // the window's bounds.
                dia.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        IPResourcePreference.getInstance().setFirstEnterBanner(false);
                    }
                });
                Window w = dia.getWindow();
                WindowManager.LayoutParams lp = w.getAttributes();
                lp.x = 0;
                lp.y = 0;
                lp.height = DisplayUtil.getDisplayHeight(context);
                dia.onWindowAttributesChanged(lp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initZmoinView(View ip_view) {
        zmoin6 = (LinearLayout) ip_view.findViewById(R.id.ip_zmoin_homepg);
        ((TextView) zmoin6.findViewById(R.id.header).findViewById(R.id.desc)).setText(R.string.ip_homep_zmoin);
        zmoin6.findViewById(R.id.header).findViewById(R.id.more).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), IPResourceListActivity.class);
                intent.putExtra("column", 0);
                getActivity().startActivity(intent);
            }
        });
        // row1.i1
        zitem[0] = zmoin6_r1_i1 = (RelativeLayout) zmoin6.findViewById(R.id.row1).findViewById(R.id.item1);
        ziv[0] = zmoin6_r1_i1_iv = (ImageView) zmoin6_r1_i1.findViewById(R.id.iv);
        zdesc[0] = zmoin6_r1_i1_desc = (TextView) zmoin6_r1_i1.findViewById(R.id.desc);
        ztitle[0] = zmoin6_r1_i1_title = (TextView) zmoin6_r1_i1.findViewById(R.id.title);
        // row1.i2
        zitem[1] = zmoin6_r1_i2 = (RelativeLayout) zmoin6.findViewById(R.id.row1).findViewById(R.id.item2);
        ziv[1] = zmoin6_r1_i2_iv = (ImageView) zmoin6_r1_i2.findViewById(R.id.iv);
        zdesc[1] = zmoin6_r1_i2_desc = (TextView) zmoin6_r1_i2.findViewById(R.id.desc);
        ztitle[1] = zmoin6_r1_i2_title = (TextView) zmoin6_r1_i2.findViewById(R.id.title);
        // row1.i3
        zitem[2] = zmoin6_r1_i3 = (RelativeLayout) zmoin6.findViewById(R.id.row1).findViewById(R.id.item3);
        ziv[2] = zmoin6_r1_i3_iv = (ImageView) zmoin6_r1_i3.findViewById(R.id.iv);
        zdesc[2] = zmoin6_r1_i3_desc = (TextView) zmoin6_r1_i3.findViewById(R.id.desc);
        ztitle[2] = zmoin6_r1_i3_title = (TextView) zmoin6_r1_i3.findViewById(R.id.title);

        // row2.i1
        zitem[3] = zmoin6_r2_i1 = (RelativeLayout) zmoin6.findViewById(R.id.row2).findViewById(R.id.item1);
        ziv[3] = zmoin6_r2_i1_iv = (ImageView) zmoin6_r2_i1.findViewById(R.id.iv);
        zdesc[3] = zmoin6_r2_i1_desc = (TextView) zmoin6_r2_i1.findViewById(R.id.desc);
        ztitle[3] = zmoin6_r2_i1_title = (TextView) zmoin6_r2_i1.findViewById(R.id.title);
        // row2.i2
        zitem[4] = zmoin6_r2_i2 = (RelativeLayout) zmoin6.findViewById(R.id.row2).findViewById(R.id.item2);
        ziv[4] = zmoin6_r2_i2_iv = (ImageView) zmoin6_r2_i2.findViewById(R.id.iv);
        zdesc[4] = zmoin6_r2_i2_desc = (TextView) zmoin6_r2_i2.findViewById(R.id.desc);
        ztitle[4] = zmoin6_r2_i2_title = (TextView) zmoin6_r2_i2.findViewById(R.id.title);
        // row2.i3
        zitem[5] = zmoin6_r2_i3 = (RelativeLayout) zmoin6.findViewById(R.id.row2).findViewById(R.id.item3);
        ziv[5] = zmoin6_r2_i3_iv = (ImageView) zmoin6_r2_i3.findViewById(R.id.iv);
        zdesc[5] = zmoin6_r2_i3_desc = (TextView) zmoin6_r2_i3.findViewById(R.id.desc);
        ztitle[5] = zmoin6_r2_i3_title = (TextView) zmoin6_r2_i3.findViewById(R.id.title);

        zmoin6_r1_i1_iv.setOnClickListener(new IPItemClickListener(0));
        zmoin6_r1_i2_iv.setOnClickListener(new IPItemClickListener(1));
        zmoin6_r1_i3_iv.setOnClickListener(new IPItemClickListener(2));
        zmoin6_r2_i1_iv.setOnClickListener(new IPItemClickListener(3));
        zmoin6_r2_i2_iv.setOnClickListener(new IPItemClickListener(4));
        zmoin6_r2_i3_iv.setOnClickListener(new IPItemClickListener(5));
    }

    private void initZhotView(View ip_view) {
        zhot3 = (LinearLayout) ip_view.findViewById(R.id.ip_zhotest_homepg);
        ((TextView) zhot3.findViewById(R.id.header).findViewById(R.id.desc)).setText(R.string.ip_homep_zhotest);
        zhot3.findViewById(R.id.header).findViewById(R.id.more).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), IPResourceListActivity.class);
                intent.putExtra("column", 1);
                getActivity().startActivity(intent);
            }
        });

        // row.i1
        zitem[MOIN_LEN] = zhot3_r1_i1 = (RelativeLayout) zhot3.findViewById(R.id.content).findViewById(R.id.item1);
        ziv[MOIN_LEN] = zhot3_r1_i1_iv = (ImageView) zhot3_r1_i1.findViewById(R.id.iv);
        zdesc[MOIN_LEN] = zhot3_r1_i1_desc = (TextView) zhot3_r1_i1.findViewById(R.id.desc);
        ztitle[MOIN_LEN] = zhot3_r1_i1_title = (TextView) zhot3_r1_i1.findViewById(R.id.title);
        // row.i2
        zitem[MOIN_LEN+1] = zhot3_r1_i2 = (RelativeLayout) zhot3.findViewById(R.id.content).findViewById(R.id.item2);
        ziv[MOIN_LEN+1] = zhot3_r1_i2_iv = (ImageView) zhot3_r1_i2.findViewById(R.id.iv);
        zdesc[MOIN_LEN+1] = zhot3_r1_i2_desc = (TextView) zhot3_r1_i2.findViewById(R.id.desc);
        ztitle[MOIN_LEN+1] = zhot3_r1_i2_title = (TextView) zhot3_r1_i2.findViewById(R.id.title);
        // row.i3
        zitem[MOIN_LEN+2] = zhot3_r1_i3 = (RelativeLayout) zhot3.findViewById(R.id.content).findViewById(R.id.item3);
        ziv[MOIN_LEN+2] = zhot3_r1_i3_iv = (ImageView) zhot3_r1_i3.findViewById(R.id.iv);
        zdesc[MOIN_LEN+2] = zhot3_r1_i3_desc = (TextView) zhot3_r1_i3.findViewById(R.id.desc);
        ztitle[MOIN_LEN+2] = zhot3_r1_i3_title = (TextView) zhot3_r1_i3.findViewById(R.id.title);

        zhot3_r1_i1_iv.setOnClickListener(new IPItemClickListener(MOIN_LEN));
        zhot3_r1_i2_iv.setOnClickListener(new IPItemClickListener(MOIN_LEN+1));
        zhot3_r1_i3_iv.setOnClickListener(new IPItemClickListener(MOIN_LEN+2));
    }

    private void setListener() {
        for (int i = 0; i < ziv.length; i++) {
            ziv[i].setOnClickListener(new IPItemClickListener(i));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO get zmoin, zhot etc
        getZmoin();
        getZhot();
//        getIPList(0);
        // TODO 此处是处理banner等的显示和隐藏，需要重新构建下view结构
        hideAppListLoading(0, true);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (isHidden()){
            MyLog.i("mBannerUI.onHide");
            mBannerUI.onHide();
            System.gc();
        } else {
            MyLog.i("mBannerUI.onShow");
            mBannerUI.onShow();
            showTutorial();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden()) {
            mBannerUI.startAutoSlide();
            mAdapter.notifyDataSetChanged();
//            mIPresListAdapter.notifyDataSetInvalidated();
        }
    }

    @Override
    public void onPause() {
        if (!isHidden()) {
            mBannerUI.stopAutoSlide();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mBannerUI.disableAutoSlide();
        super.onDestroyView();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        List<IPResource> ipResourceList = mIPresListAdapter.getIPresList();
        //由于加了header，position会有偏差，因此需要先getAdapter获取内部包装过的Adapter
//        int pos = (int) adapterView.getAdapter().getItemId(i);
//        if (ipResourceList != null && pos >= 0 && pos < ipResourceList.size()) {
//            IPResource ipResource = ipResourceList.get(pos);
            // TODO view ip detail in manager
//            MyLog.i("on list item click: " + ipResource.getName());
//            IPResourceManager.getInstance().viewIpDetail();
//        }

    }
    private void onLoad() {
        mXListView.stopRefresh();
//        mXListView.stopLoadMore();
        mXListView.setRefreshTime(System.currentTimeMillis() + "");
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                start = ++refreshCnt;
                mInformationList.clear();
                getData2();
                // mAdapter.notifyDataSetChanged();
//                mAdapter = new ListViewAdapter(context, mInformationList);
//                mXListView.setAdapter(mAdapter);
                onLoad();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                getData2();
//                mAdapter.notifyDataSetChanged();
                getWoSuggestPost(false);
                onLoad();
            }
        }, 0);
    }

    private void hideAppListLoading(int type, boolean isErr) {
        MoveBackView loadingView = mLoadingView;
        MyLog.d("hideAppListLoading: type:" + type + ", isErr=" + isErr);
        if (loadingView == null){
            return;
        }
        loadingView.stopAnim();

//        if(isErr)
//            mListView.onLoadErr(mIPresListAdapter.getCount() > 0);
//        else {
//            mListView.onLoadMoreComplete();
//        }
        // TODO judge hadAvailableIPListCashe
        int newType = IPResourceManager.getInstance().hadAvailableIPListCashe(2) ? 0 : type;
        mLoadFailedLayout.setVisibility(newType == 0 ? View.GONE : View.VISIBLE);
        switch(newType){
            case 0:
                mEmptyView.setVisibility(View.GONE);
                mNoNetworkView.setVisibility(View.GONE);
                mXListView.setVisibility(View.VISIBLE);
                break;
            case 1:
                mEmptyView.setVisibility(View.VISIBLE);
                mNoNetworkView.setVisibility(View.GONE);
                mXListView.setVisibility(View.GONE);
                break;
            case 2:
                mEmptyView.setVisibility(View.GONE);
                mNoNetworkView.setVisibility(View.VISIBLE);
                mXListView.setVisibility(View.GONE);
                break;
            default:
                break;

        }
    }

    private void showAppListLoading() {
        MoveBackView loadingView = mLoadingView;
        if (loadingView == null){
            return;
        }
        boolean isMore = mAdapter.getCount() > 0;
//        boolean isMore = mIPresListAdapter.getCount() > 0;
        loadingView.startAnim(isMore);
    }

    private void getZmoin() {
        List<IPResource> ipResourceList = IPResourceManager.getInstance().getIPListFromCache(0);
//        MyLog.i(" ipResourceList.size " + ipResourceList.size());
//        for (int i = 0; i < ipResourceList.size(); i++) {
//            MyLog.i(" ipResourceList ."+i+" :" + ipResourceList.get(i).getName());
//        }
        if(ipResourceList != null && ipResourceList.size() > 0) {
            list1.clear();
            list1.addAll(ipResourceList.subList(0, ipResourceList.size() >= MOIN_LEN ? MOIN_LEN : ipResourceList.size()));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateZmoin();
                }
            });
        }
        IPResourceManager.getInstance().getIPList(null, 0, new IPresListListener() {
            @Override
            public void onGetIPListSucc(int column, String lastid, List<IPResource> ipResourceList) {
                list1.clear();
                list1.addAll(ipResourceList.subList(0, ipResourceList.size() >= MOIN_LEN ? MOIN_LEN : ipResourceList.size()));
                for (int r = 0; r < list1.size(); r++) {
                    MyLog.i(r + " moi " + list1.get(r).getName());
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateZmoin();
                    }
                });

            }

            @Override
            public void onGetHotIPSucc(IPResource ipResource) {

            }

            @Override
            public void onNoNetwork() {
                toastNoNetwork();
            }

            @Override
            public void onErr(Object object) {

            }
        });
    }

    private void updateZmoin() {
        for (int i = 0; i < list1.size(); i++) {
            zitem[i].setVisibility(View.VISIBLE);
            try {
                imageLoader.displayImage(list1.get(i).getIcon().getUri(), ziv[i], BitmapUtil.getImageLoaderOption());
            } catch (OutOfMemoryError e) {
                MyLog.e(e);
            } catch (Exception e) {
                MyLog.e(e);
            }
            ztitle[i].setText(list1.get(i).getName());
            zdesc[i].setText(IPResourceManager.getIpTypeName(getActivity(), list1.get(i).getType()) + "/"
                    + StringUtil.formatDate(list1.get(i).getReleaseDate(), IPResourceConstants.IP_RELEASE_DATE_FORMAT));
        }
        if(list1.size() < MOIN_LEN) {
            for(int i=list1.size(); i<MOIN_LEN; i++) {
                zitem[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    private void getZhot() {
        List<IPResource> ipResourceList = IPResourceManager.getInstance().getIPListFromCache(1);

        if(ipResourceList != null && ipResourceList.size() > 0) {
            list2.clear();
            list2.addAll(ipResourceList.subList(0, ipResourceList.size() >= ZHOT_LEN ? ZHOT_LEN : ipResourceList.size()));

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateZhot();
                }
            });
        }
        IPResourceManager.getInstance().getIPList(null, 1, new IPresListListener() {
            @Override
            public void onGetIPListSucc(int column, String lastid, List<IPResource> ipResourceList) {
                list2.clear();
                list2.addAll(ipResourceList.subList(0, ipResourceList.size() >= ZHOT_LEN ? ZHOT_LEN : ipResourceList.size()));
                for (int r = 0; r < list2.size(); r++) {
                    MyLog.i(r+" hot " + list2.get(r).getName());
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateZhot();
                    }
                });

            }

            @Override
            public void onGetHotIPSucc(IPResource ipResource) {

            }

            @Override
            public void onNoNetwork() {
                toastNoNetwork();
            }

            @Override
            public void onErr(Object object) {

            }
        });
    }

    private void updateZhot() {
        for (int i = 0; i < list2.size(); i++) {
            zitem[MOIN_LEN+i].setVisibility(View.VISIBLE);
            try {
                imageLoader.displayImage(list2.get(i).getIcon().getUri(), ziv[MOIN_LEN + i], BitmapUtil.getImageLoaderOption());
            } catch (OutOfMemoryError e) {
                MyLog.e(e);
            } catch (Exception e) {
                MyLog.e(e);
            }
            ztitle[MOIN_LEN+i].setText(list2.get(i).getName());
            zdesc[MOIN_LEN+i].setText(IPResourceManager.getIpTypeName(getActivity(), list2.get(i).getType()) + "/"
                    + StringUtil.formatDate(list2.get(i).getReleaseDate(), IPResourceConstants.IP_RELEASE_DATE_FORMAT));
        }
        if(list2.size() < ZHOT_LEN) {
            for(int i=list2.size(); i<ZHOT_LEN; i++) {
                zitem[MOIN_LEN+i].setVisibility(View.INVISIBLE);
            }
        }
    }
    /**
     * 获取Banner的热门IP
     */
    public void getData() {
        ip_data_list = new ArrayList<IPResource>();
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();

        mInformationList = new ArrayList<Information>();
        for (int i = 0; i < 10; i++) {
            Information information = new Information();
            information.setDesc("Desc " + i);
            information.setTitle("Title " + i);
            mInformationList.add(information);
        }

        MyLog.i("getBanner start:");
        IPResourceManager.getInstance().getBanner(new IListener() {
            @Override
            public void onSuccess(final Object obj) {
                MyLog.i("getBanner onSuccess:");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<BannerInfo> list = (List<BannerInfo>) obj;
                        mBannerUI.setBanners(list);
                        MyLog.i("FragmentIPres:get banner size=" + list.size());
//                        mBannerTitle.setText(hotIp.getName());
//                        mBannerType.setText(IPResourceManager.getIpTypeName(getActivity(), hotIp.getType()) + "/"
//                                + StringUtil.formatDate(hotIp.getReleaseDate(), IPResourceConstants.IP_RELEASE_DATE_FORMAT));
                        showTutorial();
                    }
                });
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("getBanner onNoNetwork:");
                List<BannerInfo> banners = IPResourceManager.getInstance().getBannersFromCache();
                if (banners != null && banners.size() > 0) {
                    return;
                }
                mBannerUI.runOnGetFailed();
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("getBanner onErr:");
                List<BannerInfo> banners = IPResourceManager.getInstance().getBannersFromCache();
                if (banners != null && banners.size() > 0) {
                    return;
                }
                mBannerUI.runOnGetFailed();
            }
        });

        MyLog.i("getData:size= " + mInformationList.size());

//        // 开始联网取帖子数据
//        mPostInfoList = new ArrayList<PostInfo>();
//        getWoSuggestPost(true);
    }

    public void getData2() {
        mInformationList = new ArrayList<Information>();
        for (int i = 0; i < 10; i++) {
            Information information = new Information();
            information.setDesc("Desc " + i + i);
            information.setTitle("Title " + i + i);
            mInformationList.add(information);
        }
        MyLog.i("getData222:size= " + mInformationList.size());
    }

    private void toastNoNetwork() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.toast(context, R.string.no_network);
            }
        });
    }

    private class IPItemClickListener implements OnClickListener {
        private int pos;
        public IPItemClickListener(int i) {
            this.pos = i;
        }

        public int getPos() {
            return pos;
        }

        @Override
        public void onClick(View view) {
            // view ip detail, if pos>6, view hot detail
            try {

                Bundle b = new Bundle();
                if(getPos() < MOIN_LEN) {
                    b.putString("ip_id", list1.get(getPos()).get_id());
                    b.putBoolean("view_ip", true);
                    b.putInt("type", list1.get(getPos()).getType());
                    AppTools.toIntent(context, b, IPMoinClipActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    b.putString("ip_id", list2.get(getPos() - MOIN_LEN).get_id());
                    b.putBoolean("view_ip", true);
                    b.putInt("type", list2.get(getPos() - MOIN_LEN).getType());
                    AppTools.toIntent(context, b, IPMoinClipActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            } catch (Exception e) {
                e.printStackTrace();
                MyLog.e(e);
            }
        }
    }

    private void getWoSuggestPost(final boolean isFirst) {
        int woid = 0;
        String postid = null;

        //首先从缓存取数据
        final List<PostInfo> postLocal = WowoManager.getInstance().getSuggestPostFromCache(0);
        if (isFirst && postLocal != null && postLocal.size() > 0) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (PostInfo post : postLocal) {
                        mPostInfoList.add(post);
                        if (mPostInfoList.size() >= 2) {
                            return;
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        if (!isFirst && mPostInfoList.size() > 0) {
            woid = mPostInfoList.get(mPostInfoList.size()-1).getWoid();
            postid = mPostInfoList.get(mPostInfoList.size()-1).getPostid();
        }
        MyLog.i("woid="+woid+",postid="+postid);
        WowoManager.getInstance().getSuggestPostList(woid, postid, 0, new IPostListener() {
            @Override
            public void onGetPostListSucc(List<PostInfo> postInfoList, int column) {
                final List<PostInfo> postinfo = postInfoList;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mXListView.stopLoadMore();
                        //首次只加载2个帖子
                        if (isFirst) {
                            mPostInfoList.clear();
                            for (PostInfo post : postinfo) {
                                mPostInfoList.add(post);
                                if (mPostInfoList.size() >= 2) {
                                    break;
                                }
                            }
                        } else {
                            MyLog.i("getWoSuggestPost.onGetPostListSucc.. postinfolist.size=" + postinfo.size());
                            if (postinfo == null || postinfo.size() < 5) {
                                mXListView.setLoadMoreOver();
                                ToastUtils.toast(getActivity(), R.string.list_end);
                            }
                            mPostInfoList.addAll(postinfo);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });

                //缓存到本地数据库
                WowoManager.getInstance().saveSuggestPostList(postInfoList, 0);

            }

            @Override
            public void onGetWoPostListSucc(List<PostInfo> postInfoList, WowoInfo woinfo, int column) {

            }

            @Override
            public void onGetIPPostSucc(List<PostInfo> postInfoList) {

            }

            @Override
            public void onNewPostSucc(int woid, String postid) {

            }

            @Override
            public void onReplyPostSucc(int commentid) {

            }

            @Override
            public void onGetPostSucc(PostInfo postInfo, List<CommentInfo> commentInfos) {

            }

            @Override
            public void onUploadImageSucc(String picid) {

            }

            @Override
            public void onNoNetwork() {
                MyLog.i("onNoNetwork");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mXListView.stopLoadMore();
                        ToastUtils.toast(getActivity(), R.string.no_network);
                    }
                });
            }

            @Override
            public void onErr(Object object) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mXListView.stopLoadMore();
                        // 不是第一次进来而且list有数据则不让再加载了
                        if (!isFirst && mPostInfoList.size() > 0) {
                            mXListView.setLoadMoreOver();
                            ToastUtils.toast(getActivity(), R.string.list_end);
                        }

                    }
                });
            }
        });
    }

}
