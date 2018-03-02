package com.moinapp.wuliao.modules.ipresource;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moinsocialcenter.MoinSocialConstants;
import com.moinapp.wuliao.commons.moinsocialcenter.MoinSocialFactory;
import com.moinapp.wuliao.commons.moinsocialcenter.UmengSocialCenter;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.commons.ui.BackInterpolator;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.commons.ui.EasingType;
import com.moinapp.wuliao.commons.ui.ExpandableTextView;
import com.moinapp.wuliao.commons.ui.MyGridView;
import com.moinapp.wuliao.commons.ui.MyListView;
import com.moinapp.wuliao.commons.ui.MyScrollView2;
import com.moinapp.wuliao.commons.ui.UiUtils;
import com.moinapp.wuliao.commons.ui.menu.Panel;
import com.moinapp.wuliao.modules.ipresource.model.IPDetails;
import com.moinapp.wuliao.modules.login.LoginActivity;
import com.moinapp.wuliao.modules.login.LoginConstants;
import com.moinapp.wuliao.modules.login.LoginSuccJump;
import com.moinapp.wuliao.modules.wowo.IPostListener;
import com.moinapp.wuliao.modules.wowo.PostDetailActivity;
import com.moinapp.wuliao.modules.wowo.WoPostListActivity;
import com.moinapp.wuliao.modules.wowo.WowoContants;
import com.moinapp.wuliao.modules.wowo.WowoManager;
import com.moinapp.wuliao.modules.wowo.model.CommentInfo;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;
import com.moinapp.wuliao.modules.wowo.model.WowoInfo;
import com.moinapp.wuliao.utils.AnimationUtil;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.DisplayUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by moying on 15/5/27.
 */
public class IPMoinClipActivity extends BaseActivity implements Panel.OnPanelListener, MyScrollView2.OnScrollListener {

    private ILogger MyLog = LoggerFactory.getLogger(IPResourceModule.MODULE_NAME);
    private ImageLoader imageLoader;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private final Object lock = new Object();
    private static final int MSG_TIME_OUT = 1;
    private static final int MSG_FAVORIATE = 2;
    private static final int MSG_FAVORIATE_CANCEL = 3;
    private static final int MSG_UPDATE_FAVORIATE = 4;
    private static final int MSG_UPDATE_STAR = 5;

    private ListView lv_clip;
    private Panel panel;
    private MyScrollView2 myScrollView2;
    private ImageView haibao, collapse, ivCursor, coll_img, clip_coll_img;
    private TextView ip_name, ip_type, iptag, iptag2, iplength, iplocation, iplanguage, ipbanquan, ipshowdate, t_emoji,
            t_detail, t_wowo, emoji_in_production, emoji_title, emoji_more, director, actor, actor2, coll_num;
    private LinearLayout emoji_view,detail_view,wowo_view,collection, share;
    private RelativeLayout emoji_detail;
    private MyGridView emoji_grid;
    private MyListView words_lv;

    // wowo相关的内容
    //private RelativeLayout mWowoHead;
    private TextView mWoName;
    private TextView mMoreWo;
    private LinearLayout mWoPostBody;
    private ImageView mPostCover; //帖子封面
    private ImageView mAuthorAvatar; //帖子作者头像
    private TextView mAuthorName; //帖子作者
    private TextView mAuthorLevel; //帖子作者身份，版主等等
    private TextView mPostUpdateAt; //帖子发布时间
    private ImageView mSuggestImage; //帖子是精品贴的图标
    private ImageView mPicImage; //帖子包含图片的图标
    private ImageView mEmojiImage; //帖子包含表情的图标
    private TextView mPostTitle; //帖子标题
    private TextView mPostDesc; //帖子描述
    private LinearLayout mCommentLayout;//推荐的区域，在ip首页中窝贴列表是需要的，这里不需要，隐藏
    private ImageView mShare; //分享
    private LinearLayout mCommentView; //评论的view
    private TextView mComment; //帖子评论的个数
    private ImageView mCommentImage; //帖子评论的图标
    private LinearLayout mPostCotentArea;//帖子内容的点击区域，用户跳转帖子详情
    private RelativeLayout mPostAuthorArea;//帖子作者的点击区域，用户跳转帖子详情
//    private LinearLayout mCosplayEntry;//详情页中大咖秀的入口

    private LinearLayout mRootView;

    private Button allWords;
    private boolean more;
    private SimpleAdapter wordlistAdapter;
    private ArrayList<HashMap<String, Object>> wordlistItems;
    private int wordsCount;

    private int haibaoTop;
    private int haibaoHeight;
    private int screenWidth;
    private int cursorWidth;
    private int cursorLastX;

    private int cursorPadingPx;
    private RelativeLayout.LayoutParams lpCursor;
    private String ip_id;
    /**true: clip;    false:ip detail*/
    private boolean view_ip;
    private boolean favoriate_ip;
    private int favoriate_stat = -2;

    private ExpandableTextView expandableTextView;
    private IPDetails ipd;
    private LinearLayout.LayoutParams params;
    private int eHeight, dHeight, wHeight, initHeight;
    Dialog dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moin_clip_layout);

        imageLoader = ImageLoader.getInstance();
        if(!imageLoader.isInited()) {
            imageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }
        EventBus.getDefault().register(this);
        getData();
        initLoadingView();
        setLoadingMode(MODE_LOADING);
        initView();
        setListener();
    }

    private void showHelp() {
        boolean isFirst = IPResourcePreference.getInstance().isFirstEnterIPMoin();
        if(isFirst) {
            try {
                Context context = IPMoinClipActivity.this;
                dia = new Dialog(context, R.style.edit_AlertDialog_style);
                dia.setContentView(R.layout.moin_clip_help_dialog);

                OnClickListener dismiss = new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IPResourcePreference.getInstance().setFirstEnterIPMoin(false);
                        dia.dismiss();
                    }
                };
                dia.findViewById(R.id.dialog_rl).setOnClickListener(dismiss);
                ImageView imageView = (ImageView) dia.findViewById(R.id.help_ip_img);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                params.topMargin = getResources().getDimensionPixelSize(R.dimen.tutorial_moin_clip_mt);
                MyLog.i("params.topMargin = " + params.topMargin);
                if (Build.VERSION.SDK_INT < 19) {
                    params.topMargin += DisplayUtil.dip2px(IPMoinClipActivity.this, 26);
                    MyLog.i("params.topMargin [19] = " + params.topMargin);
                }
                imageView.setLayoutParams(params);
                imageView.setOnClickListener(dismiss);

                dia.show();

                dia.setCanceledOnTouchOutside(true); // Sets whether this dialog is
                // canceled when touched outside
                // the window's bounds.
                dia.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        IPResourcePreference.getInstance().setFirstEnterIPMoin(false);
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

    @Override
    protected void reloadHandle() {
        super.reloadHandle();

        getIPdetails();

        //获取官方窝的信息
        getWoPost();
    }

    private void getData() {
        Intent intent = getIntent();
        if(intent == null)
            return;
        ip_id = intent.getStringExtra("ip_id");
        if(TextUtils.isEmpty(ip_id))
            return;

        int type = intent.getIntExtra("type", 1);
        MyLog.i("ip_id = " + ip_id);
        view_ip = getIntent().getBooleanExtra("view_ip", true);
        MyLog.i("view_ip = " + view_ip);

        cursorPadingPx = DisplayUtil.dip2px(IPMoinClipActivity.this, 6);

        getIPdetails();

        //获取官方窝的信息
        getWoPost();

        initHeight = DisplayUtil.dip2px(IPMoinClipActivity.this, 320);

        mHandlerThread = new HandlerThread("LocateHandlerThread");
        mHandlerThread.start();
        mHandler = new MyHandler(mHandlerThread.getLooper());
    }

    private void getIPdetails() {
        IPResourceManager.getInstance().getIPDetails(ip_id, new IListener() {
            @Override
            public void onSuccess(final Object obj) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoadingMode(MODE_OK);
                        ipd = (IPDetails) obj;
                        updateIpinfo();
                    }
                });

            }

            @Override
            public void onNoNetwork() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(IPMoinClipActivity.this, R.string.no_network);
                        setLoadingMode(MODE_RELOADING);
                    }
                });

            }

            @Override
            public void onErr(Object object) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(IPMoinClipActivity.this, R.string.connection_failed);
                        setLoadingMode(MODE_RELOADING);
                    }
                });
            }
        });
    }

    private void initView() {
        mRootView = (LinearLayout) findViewById(R.id.root);
        mRootView.setPadding(0, 0, 0, CommonsPreference.getInstance().getVirtualKeyboardHeight());

        clip_coll_img = (ImageView) findViewById(R.id.back);

        lv_clip = (ListView) findViewById(R.id.lv_clip);

        // 初始化IP详情内容
        panel = (Panel) findViewById(R.id.bottomPanel);
        panel.setOnPanelListener(this);
        panel.setInterpolator(new BackInterpolator(EasingType.OUT, -2));
        panel.setOpen(view_ip, false);

        FrameLayout ipdetail = (FrameLayout) findViewById(R.id.panelContent);
        haibao = (ImageView) ipdetail.findViewById(R.id.haibao);
        ipdetail.findViewById(R.id.haibao_frame).setBackgroundResource(R.drawable.bgd_haibao_line);
        ip_name = (TextView) ipdetail.findViewById(R.id.header_name);
        ip_type = (TextView) ipdetail.findViewById(R.id.header_type);
        iptag = (TextView) ipdetail.findViewById(R.id.iptag);
        iptag2 = (TextView) ipdetail.findViewById(R.id.iptag2);

        iplength = (TextView) ipdetail.findViewById(R.id.iplength);
        //iplocation = (TextView) ipdetail.findViewById(R.id.iplocation);
        //iplanguage = (TextView) ipdetail.findViewById(R.id.iplanguage);
        ipbanquan = (TextView) ipdetail.findViewById(R.id.ipbanquan);
        ipshowdate = (TextView) ipdetail.findViewById(R.id.ipshowdate);

        expandableTextView = (ExpandableTextView) ipdetail.findViewById(R.id.shortcut);

        t_emoji = (TextView) ipdetail.findViewById(R.id.t_emoji);
        t_detail = (TextView) ipdetail.findViewById(R.id.t_detail);
        t_wowo = (TextView) ipdetail.findViewById(R.id.t_wowo);
        // 初始化栏目指示器
        ivCursor = (ImageView) ipdetail.findViewById(R.id.r_cursor).findViewById(R.id.iv_cursor);
        screenWidth = getResources().getDisplayMetrics().widthPixels;// 获取分辨率宽度
        cursorWidth = (int) (screenWidth / 3 + 0.5f);
        lpCursor = new RelativeLayout.LayoutParams(cursorWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        ivCursor.setPadding(2 * cursorPadingPx, 0, cursorPadingPx, 0);
        ivCursor.setLayoutParams(lpCursor);

        emoji_view = (LinearLayout) ipdetail.findViewById(R.id.emoji_view);
        emoji_in_production = (TextView) ipdetail.findViewById(R.id.emoji_in_production);
        emoji_detail = (RelativeLayout) ipdetail.findViewById(R.id.emoji_detail);
        detail_view = (LinearLayout) ipdetail.findViewById(R.id.detail_view);

        //设置大咖秀
//        mCosplayEntry = (LinearLayout) ipdetail.findViewById(R.id.dakaXiu);

        //设置窝窝相关的view
        wowo_view = (LinearLayout) ipdetail.findViewById(R.id.wowo_view);
        View mWowoHead = wowo_view.findViewById(R.id.wowo_header);
        mWoName = (TextView) mWowoHead.findViewById(R.id.desc);
        mMoreWo = (TextView) mWowoHead.findViewById(R.id.more);
        mWoPostBody = (LinearLayout) wowo_view.findViewById(R.id.wowo_tz);
        mPostCover = (ImageView) mWoPostBody.findViewById(R.id.post_cover);
        mAuthorAvatar = (ImageView) mWoPostBody.findViewById(R.id.author_avatar);
        mAuthorName = (TextView) mWoPostBody.findViewById(R.id.author_name);
        mAuthorLevel = (TextView) mWoPostBody.findViewById(R.id.author_level);
        mPostUpdateAt = (TextView) mWoPostBody.findViewById(R.id.post_time);
        mSuggestImage = (ImageView) mWoPostBody.findViewById(R.id.post_suggest_image);
        mPicImage = (ImageView) mWoPostBody.findViewById(R.id.post_pic_image);
        mEmojiImage = (ImageView) mWoPostBody.findViewById(R.id.post_emoji_image);
        mPostTitle = (TextView) mWoPostBody.findViewById(R.id.post_title);
        mPostDesc = (TextView) mWoPostBody.findViewById(R.id.post_desc);
        mCommentLayout = (LinearLayout) mWoPostBody.findViewById(R.id.recommend);
        mCommentLayout.setVisibility(View.GONE);
        mShare = (ImageView) mWoPostBody.findViewById(R.id.wo_post_share);
        mCommentImage = (ImageView) mWoPostBody.findViewById(R.id.wo_post_comment_image);
        mComment = (TextView) mWoPostBody.findViewById(R.id.wo_post_comment);
        mCommentView = (LinearLayout) mWoPostBody.findViewById(R.id.comment);
        mPostCotentArea = (LinearLayout) mWoPostBody.findViewById(R.id.post_content_area);
        mPostAuthorArea = (RelativeLayout) mWoPostBody.findViewById(R.id.post_author_area);

        emoji_title = (TextView) ipdetail.findViewById(R.id.emoji_title);
        emoji_more = (TextView) ipdetail.findViewById(R.id.emoji_more);
        emoji_grid = (MyGridView) ipdetail.findViewById(R.id.emoji_grid);

        director = (TextView) ipdetail.findViewById(R.id.director);
        actor = (TextView) ipdetail.findViewById(R.id.actor);
        actor2 = (TextView) ipdetail.findViewById(R.id.actor2);

        coll_img = (ImageView) ipdetail.findViewById(R.id.coll_img);
        coll_num = (TextView) ipdetail.findViewById(R.id.coll_num);
        // 初始化台词listview
        words_lv = (MyListView) ipdetail.findViewById(R.id.words_lv);
        allWords = (Button) ipdetail.findViewById(R.id.show_all_words);

        myScrollView2 = (MyScrollView2) findViewById(R.id.scrollView);

        collapse = (ImageView) findViewById(R.id.collapse);

        collection = (LinearLayout) ipdetail.findViewById(R.id.collection);
        share = (LinearLayout) ipdetail.findViewById(R.id.l_share);


        if(params == null) {
            params = (LinearLayout.LayoutParams) emoji_view.getLayoutParams();
        }
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = initHeight;
    }

    private void setListener() {
        myScrollView2.setOnScrollListener(this);
        findViewById(R.id.fade_header).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ipd.getClip() != null && !StringUtil.isNullOrEmpty(ipd.getClip().getUri())) {
                    IPResourceManager.getInstance().playWebVideo(IPMoinClipActivity.this, ipd.getClip().getUri());
                }
            }
        });
        collapse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                panel.setOpen(false, true);
            }
        });
        clip_coll_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                panel.setOpen(true, true);
            }
        });

        t_emoji.setOnClickListener(new TitleClickListener(0));
        t_detail.setOnClickListener(new TitleClickListener(1));
        t_wowo.setOnClickListener(new TitleClickListener(2));

        collection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClientInfo.isUserLogin()) {
                    favoriate_ip = !favoriate_ip;
                    // 本地处理收藏状态和个数
                    if (favoriate_ip) {
                        favoriate_stat++;
                    } else {
                        favoriate_stat--;
                    }

                    mHandler.sendEmptyMessage(MSG_UPDATE_FAVORIATE);
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_STAR, 100);

                    mHandler.removeMessages(MSG_FAVORIATE_CANCEL);
                    mHandler.removeMessages(MSG_FAVORIATE);
                    if (favoriate_ip) {
                        mHandler.sendEmptyMessageDelayed(MSG_FAVORIATE, 2000);
                    } else {
                        mHandler.sendEmptyMessageDelayed(MSG_FAVORIATE_CANCEL, 2000);
                    }
                } else {
                    Bundle b = new Bundle();
                    b.putBoolean(LoginActivity.KEY_NEED_RETURN, true);
                    b.putInt(LoginActivity.KEY_FROM, LoginConstants.IP_DETAIL);
                    AppTools.toIntent(IPMoinClipActivity.this, b, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }
        });


        mMoreWo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转窝窝主页
                MyLog.i("more wo click.....");
            }
        });

        // 大咖秀入口
//        mCosplayEntry.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CosplayManager.getInstance().getCosplayResource(ip_id, null, new GetCosplayResListener() {
//                    @Override
//                    public void getCosplaySucc(List<CosplayResource> cosplayResourceList) {
//                        if (cosplayResourceList == null) {
//                            MyLog.i("getCosplaySucc...cosplayResourceList = null");
//                            ToastUtils.toast(IPMoinClipActivity.this, R.string.no_network);
//                            return;
//                        }
//
//                        MyLog.i("getCosplaySucc...cosplayResourceList.size =" + cosplayResourceList.size());
//                        if (cosplayResourceList.size() > 0) {
//                            CosplayResource cosplay = cosplayResourceList.get(0);
//                            if (cosplay != null) {
//                                //检查状态
//                                int status = CosplayManager.getInstance().getStatus(cosplay).statusCode;
//                                if (status == MyDownloadManager.STATUS_SUCCESSFUL) {
//                                    CosplayManager.getInstance().initCosplay(cosplay);
//                                } else {
//                                    MyLog.i("getCosplaySucc...cosplayResource need downloading....");
//                                    Bundle b = new Bundle();
//                                    b.putInt(DownloadCosplyResActivity.FROM, 1);
//                                    b.putSerializable(DownloadCosplyResActivity.COSPLAY_RES_LIST, (ArrayList<CosplayResource>) cosplayResourceList);
//                                    AppTools.toIntent(IPMoinClipActivity.this, b,DownloadCosplyResActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
//                                }
//                            }
//                        } else {
//                            CosplayManager.getInstance().getips();
//                        }
//                    }
//
//                    @Override
//                    public void onNoNetwork() {
//
//                    }
//
//                    @Override
//                    public void onErr(Object object) {
//
//                    }
//                });
//            }
//        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if(!panel.isOpen()) {
                panel.setOpen(true, true);
            } else {
                super.leftBtnHandle();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void updateIpinfo() {
        if(ipd == null)
            return;

        showHelp();

        lv_clip.setAdapter(new ClipAdapter(IPMoinClipActivity.this, ipd.getClip(), ipd.getCliplist()));

        // haibao
        if(ipd.getPosters() != null && ipd.getPosters().get(0) != null) {
            try {
                imageLoader.displayImage(ipd.getPosters().get(0).getUri(), haibao, BitmapUtil.getImageLoaderOption());
            } catch (OutOfMemoryError e) {
                MyLog.e(e);
            } catch (Exception e) {
                MyLog.e(e);
            }
            final String haibaoUri = ipd.getPosters().get(0).getUri();
            haibao.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(haibaoUri);
                    Intent intent = new Intent(IPMoinClipActivity.this, StillsViewPagerActivity.class);
                    intent.putExtra(StillsViewPagerActivity.KEY_CURRENT, haibaoUri);
                    intent.putExtra(StillsViewPagerActivity.KEY_CLIPLIST, list);
                    MyLog.i("click post cover: current=" + haibaoUri);
                    IPMoinClipActivity.this.startActivity(intent);
                }
            });
        }
        ip_name.setText(ipd.getName());
        ip_type.setText(IPResourceManager.getIpTypeName(IPMoinClipActivity.this, ipd.getType()) + "/"
                + StringUtil.formatDate(ipd.getReleaseDate(), IPResourceConstants.IP_RELEASE_DATE_FORMAT));
        iptag.setText(getTag(ipd.getTags()));
        iptag2.setText(getTag(ipd.getTags()));

        iplength.setText(ipd.getType() == IPResourceConstants.TYPE_TV ? ipd.getTypeInfo().getEpscount() : ipd.getTypeInfo().getDuration());
        //iplocation.setText(ipd.getLocation());
        //iplanguage.setText(ipd.getLanguage());
        ipbanquan.setText(ipd.getOwner());
        ipshowdate.setText(StringUtil.formatDate(ipd.getReleaseDate(), IPResourceConstants.IP_RELEASE_DATE_FORMAT));
        expandableTextView.setText(ipd.getIntro());

        if(ipd.getEmojis() != null && ipd.getEmojis().size() > 0) {
            emoji_in_production.setVisibility(View.GONE);
            emoji_detail.setVisibility(View.VISIBLE);

            emoji_title.setText(ipd.getName());
            emoji_more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle b = new Bundle();
                    b.putString("ipid", ip_id);
                    b.putString("ipname", ipd.getName());
                    AppTools.toIntent(ApplicationContext.getContext(), b, EmojiResourceActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            });
            IpDetailEmjGridAdapter grdiAdapter = new IpDetailEmjGridAdapter(IPMoinClipActivity.this, ipd.getEmojis());
            emoji_grid.setAdapter(grdiAdapter);
            UiUtils.fixGridViewHeight(emoji_grid, getResources().getDisplayMetrics().widthPixels);

            emoji_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    MyLog.i("you click " + i);
                }
            });
            // 重新计算下表情tab的高度
            observerEmojiViewHeight();
        } else {
            emoji_in_production.setVisibility(View.VISIBLE);
            emoji_detail.setVisibility(View.GONE);
        }
        director.setText(ipd.getDirector());
        actor.setText(ipd.getActor());
        actor2.setText(ipd.getActor());

        allWords.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                more = !more;
                buildListForSimpleAdapter();
                wordlistAdapter.notifyDataSetChanged();
                allWords.setText(getString(R.string.show_all_words) + (more ? "" : "(" + (ipd.getWords().size() - 3) + ")"));
//                UiUtils.fixListViewHeight(words_lv);
                MyLog.i("all words click: words_lv.param.Height:" + words_lv.getLayoutParams().height);
//                params.height = initHeight > words_lv.getLayoutParams().height ? initHeight : words_lv.getLayoutParams().height;
//                detail_view.setLayoutParams(params);
            }
        });

        buildListForSimpleAdapter();
        wordlistAdapter = new SimpleAdapter(this, wordlistItems, R.layout.ip_words_item,
                new String[]{"owner", "content"},
                new int[]{R.id.owner, R.id.content});
        words_lv.setAdapter(wordlistAdapter);
        words_lv.setCacheColorHint(0x00000000);
        words_lv.setBackgroundDrawable(null);
        words_lv.setBackgroundColor(Color.TRANSPARENT);
        words_lv.setParentScrollView(myScrollView2);
        // 140dp是详情tab到最底部的高度，也就是收藏按钮的高度+导演和演员的高度+展开更多台词的高度
        words_lv.setMaxHeight(params.height - DisplayUtil.dip2px(IPMoinClipActivity.this, 210));

//        words_lv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(IPMoinClipActivity.this, 120)));
//        UiUtils.fixListViewHeight(words_lv);

        if (ipd.getWords().size() > 3) {
            allWords.setVisibility(View.VISIBLE);
            allWords.setText(getString(R.string.show_all_words) + "(" + (ipd.getWords().size() - 3) + ")");
        } else {
            allWords.setVisibility(View.GONE);
        }


        {
            MyLog.i("ipd.getIsFavorite()="+ipd.getIsFavorite());
            favoriate_stat = ipd.getFavoriteStat();
            MyLog.i(ipd.getName()+(ipd.getIsFavorite() ? "已收藏:"+favoriate_stat:"未收藏:"+favoriate_stat));
            if (ipd.getIsFavorite()) {
                favoriate_ip = true;
                collection.setSelected(true);
                coll_img.setImageResource(R.drawable.icon_fav_on);
                coll_num.setText(getString(R.string.ip_collectioned) + (favoriate_stat > 0 ? " " + favoriate_stat : ""));
            } else {
                favoriate_ip = false;
                collection.setSelected(false);
                coll_img.setImageResource(R.drawable.icon_fav);
                coll_num.setText(getString(R.string.ip_collection) + (favoriate_stat > 0 ? " " + favoriate_stat : ""));
            }
            mHandler.sendEmptyMessage(MSG_UPDATE_FAVORIATE);
        }

        share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                String shareUrl = IPResourceConstants.IP_SHARE_URL + ip_id;
                UmengSocialCenter shareCenter = new UmengSocialCenter(IPMoinClipActivity.this);
                shareCenter.setShareContent(ipd.getName(), ipd.getIntro(),
                        shareUrl, ipd.getPosters().get(0).getUri());
                shareCenter.openShare(IPMoinClipActivity.this);
            }
        });

    }

    private void buildListForSimpleAdapter() {
        if(wordlistItems == null) {
            wordlistItems = new ArrayList<>();
        }

        wordsCount = more ? ipd.getWords().size() : Math.min(ipd.getWords().size(), 3);
        if(wordsCount <= 0)
            return;

        wordlistItems.clear();

        for (int i = 0; i < wordsCount; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("owner", ipd.getWords().get(i).getOwner() + ":");
            map.put("content", ipd.getWords().get(i).getContent());
            MyLog.i((i + 1) + ":" + map.get("owner")+"/"+map.get("content"));
            wordlistItems.add(map);
        }
    }

    private String getTag(List<String> tags) {
        StringBuilder sb = new StringBuilder();
        if(tags == null || tags.isEmpty())
            return "";
        for (int i = 0; i < tags.size(); i++) {
            sb.append(tags.get(i)).append(" ");
        }
        return sb.toString();
    }

    /**
     * 窗口有焦点的时候，即所有的布局绘制完毕的时候，我们来获取海报的高度和myScrollView2距离父类布局的顶部位置
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            haibaoHeight = haibao.getHeight();
            haibaoTop = haibao.getTop();
        }
    }
    @Override
    public void onPanelClosed(Panel mPanel) {
        clip_coll_img.setVisibility(View.VISIBLE);
        mPanel.findViewById(R.id.panelHandle).setVisibility(View.VISIBLE);
    }

    @Override
    public void onPanelOpened(Panel panel) {
        clip_coll_img.setVisibility(View.GONE);
        panel.findViewById(R.id.panelHandle).setVisibility(View.GONE);
        lv_clip.smoothScrollToPositionFromTop(0,0,200);
//        lv_clip.setSelection(0);
        if(myScrollView2 != null) {
//            myScrollView2.fullScroll(MyScrollView2.FOCUS_UP);
            myScrollView2.scrollTo(0,0);
        }
    }

    /**
     * 滑动ip详情页中的ScrollView时计算海报的高度是不是超过了底部第一个大片花的底部，如果超过了，那就让详情页收起来
     * */
    @Override
    public void onScroll(int scrollY) {
//        if(scrollY >= haibaoTop){
//
//        }else if(scrollY <= haibaoTop + haibaoHeight){
//            // TODO 应该是详情页中海报小图右边的buttonHandler(控制电机后panel手机的view),等UI更新好后，判断。
//            panel.setOpen(false, true);
//        }
    }

    int woMaxHeight = 0;
    private class TitleClickListener implements OnClickListener {
        private int pos;
        public TitleClickListener(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(View view) {
            t_emoji.setTextColor(pos == 0 ? getColor(R.color.common_text_main) : getColor(R.color.common_title_grey));
            t_detail.setTextColor(pos == 1 ? getColor(R.color.common_text_main) : getColor(R.color.common_title_grey));
            t_wowo.setTextColor(pos == 2 ? getColor(R.color.common_text_main) : getColor(R.color.common_title_grey));

            AnimationUtil.moveTo(ivCursor, 300, cursorLastX, cursorWidth * pos, 0, 0);
            ivCursor.setPadding((2 - pos) * cursorPadingPx, 0, (pos / 2 + 1) * cursorPadingPx, 0);
            cursorLastX = cursorWidth * pos;

            emoji_view.setVisibility(pos == 0 ? View.VISIBLE : View.GONE);
            detail_view.setVisibility(pos == 1 ? View.VISIBLE : View.GONE);
            wowo_view.setVisibility(pos == 2 ? View.VISIBLE : View.GONE);
            if(woMaxHeight == 0) {
                woMaxHeight = getResources().getDimensionPixelSize(R.dimen.ip_wowo_view_h);
            }
            MyLog.i("wowo_view.getLayoutParams().height = " + woMaxHeight);

            final int y = myScrollView2.getScrollY();
            MyLog.i("new params params.height = " + params.height);
            MyLog.i("new params y = " + y);
            switch (pos) {
                case 0:
                    observerEmojiViewHeight();
                    break;
                case 1:
                    detail_view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            detail_view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            dHeight = detail_view.getHeight();
                            MyLog.i("new params detail_view.h = " + dHeight);
                            if(params.height < dHeight) {
                                params.height =dHeight;
                            }
                            setTabHeight();
                        }
                    });
                    break;
                case 2:
                    wowo_view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            wowo_view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            wHeight = wowo_view.getHeight();
                            wHeight = woMaxHeight > wHeight ? woMaxHeight : wHeight;
                            MyLog.i("new params wowo_view.h = " + wHeight);
                            if(params.height < wHeight) {
                                params.height =wHeight;
                            }
                            setTabHeight();
                        }
                    });
                    break;

            }
            MyLog.i("new params params.height 2 = " + params.height);

//            handler.postDelayed(run_scrlTpEnd, 400);

        }
    }

    private void observerEmojiViewHeight() {
        emoji_view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                emoji_view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                eHeight = emoji_view.getHeight();
                MyLog.i("new params emoji_view.h = " + eHeight);
                if(params.height < eHeight) {
                    params.height =eHeight;
                }
                setTabHeight();
            }
        });
    }

    private void setTabHeight() {
        emoji_view.setLayoutParams(params);
        detail_view.setLayoutParams(params);
        wowo_view.setLayoutParams(params);
    }

    private int getColor(int id) {
        return getResources().getColor(id);
    }

    private class MyHandler extends Handler {

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try{
                switch (msg.what) {
                    case MSG_FAVORIATE_CANCEL:
                        MyLog.i("start Cancel favoriate ip: " + ip_id + " @ " + System.currentTimeMillis());
                        IPResourceManager.getInstance().favoriateIP(ip_id, 0, new IListener() {
                            @Override
                            public void onSuccess(Object obj) {
                                MyLog.i("cancel favoriate OK ip: " + ip_id);
//                                favoriate_ip = false;
//                                favoriate_stat--;
//                                mHandler.sendEmptyMessage(MSG_UPDATE_FAVORIATE);
                            }

                            @Override
                            public void onNoNetwork() {
                                MyLog.i("cancel favoriate failed nonetwork ip: " + ip_id);
//                                favoriate_ip = true;
                            }

                            @Override
                            public void onErr(Object object) {
                                MyLog.i("cancel favoriate failed ip: " + ip_id);
//                                favoriate_ip = true;
//                                mHandler.sendEmptyMessage(MSG_UPDATE_FAVORIATE);
                            }
                        });
//
//                        List<String> ips = new ArrayList<>();
//                        ips.add(ip_id);
//                        MineManager.getInstance().delFavoriateIP(ips, new FavoriateIPListener() {
//                            @Override
//                            public void getFavoriateIPSucc(List<IPResource> ips) {
//
//                            }
//
//                            @Override
//                            public void delFavoriateIPSucc(int result) {
//                                if(result > 0) {
//                                    MyLog.i("cancel favoriate OK ip: " + ip_id);
//                                    favoriate_stat--;
//                                    favoriate_ip = false;
//                                } else {
//                                    MyLog.i("cancel favoriate failed ip: " + ip_id);
//                                    favoriate_ip = true;
//                                }
//                                mHandler.sendEmptyMessage(MSG_UPDATE_FAVORIATE);
//                            }
//
//                            @Override
//                            public void onNoNetwork() {
//                                MyLog.i("cancel favoriate failed nonetwork ip: " + ip_id);
//                                favoriate_ip = true;
//                            }
//
//                            @Override
//                            public void onErr(Object object) {
//                                MyLog.i("cancel favoriate failed ip: " + ip_id);
//                                favoriate_ip = true;
//                                mHandler.sendEmptyMessage(MSG_UPDATE_FAVORIATE);
//                            }
//                        });
                        break;
                    case MSG_FAVORIATE:
                        MyLog.i("start favoriate ip: " + ip_id + " @ " + System.currentTimeMillis());
                        IPResourceManager.getInstance().favoriateIP(ip_id, 1, new IListener() {
                            @Override
                            public void onSuccess(Object obj) {
                                MyLog.i("favoriate OK ip: " + ip_id);
//                                favoriate_ip = true;
//                                favoriate_stat++;
//                                mHandler.sendEmptyMessage(MSG_UPDATE_FAVORIATE);
                            }

                            @Override
                            public void onNoNetwork() {
                                MyLog.i("favoriate failed nonetwork ip: " + ip_id);
//                                favoriate_ip = false;
                            }

                            @Override
                            public void onErr(Object object) {
                                MyLog.i("favoriate failed ip: " + ip_id);
//                                favoriate_ip = false;
//                                mHandler.sendEmptyMessage(MSG_UPDATE_FAVORIATE);
                            }
                        });
                        break;
                    case MSG_UPDATE_FAVORIATE:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                udpateCollection();
                            }
                        });
                        break;
                    case MSG_UPDATE_STAR:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                coll_img.startAnimation(AnimationUtils.loadAnimation(IPMoinClipActivity.this, R.anim.favorate_star));
                            }
                        });
                        break;
                }
            } catch(Exception e){
                MyLog.e(e);
            }
        }
    }

    private void udpateCollection() {
        if(favoriate_ip) {
            coll_num.setTextColor(getResources().getColor(R.color.common_text_main));
            coll_num.setText(getString(R.string.ip_collection) + (favoriate_stat > 0 ? " " + favoriate_stat : ""));
            coll_img.setImageResource(R.drawable.icon_fav_on);
        } else {
            coll_num.setTextColor(getResources().getColor(R.color.common_title_grey));
            coll_num.setText(getString(R.string.ip_collection) + (favoriate_stat > 0 ? " " + favoriate_stat : ""));
            coll_img.setImageResource(R.drawable.icon_fav);
        }
    }

    Runnable run_scrlTpEnd = new Runnable() {
        @Override
        public void run() {
            myScrollView2.fullScroll(MyScrollView2.FOCUS_DOWN);
        }
    };

    private void getWoPost() {
        WowoManager.getInstance().getIPPostList(ip_id, new IPostListener() {
            @Override
            public void onGetPostListSucc(List<PostInfo> postInfoList, int column) {

            }

            @Override
            public void onGetWoPostListSucc(List<PostInfo> postInfoList, WowoInfo woinfo, int column) {

            }

            @Override
            public void onGetIPPostSucc(List<PostInfo> postInfoList) {
                MyLog.i("onGetIPPostSucc");
                if (postInfoList != null && postInfoList.size() > 0) {
                    final PostInfo postInfo = postInfoList.get(0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updatePostInfo(postInfo);
                        }
                    });
                }
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

            }

            @Override
            public void onErr(Object object) {

            }
        });
    }

    private void updatePostInfo(final PostInfo postInfo) {
        if(postInfo == null) {
            return;
        }
        mWoName.setText(postInfo.getWoname());
        mMoreWo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转窝频道
                Bundle b = new Bundle();
                b.putSerializable(WoPostListActivity.WO_ID, postInfo.getWoid());
                b.putInt(WoPostListActivity.FROM, 1);
                AppTools.toIntentWithRequestCode(IPMoinClipActivity.this, b, WoPostListActivity.class, 1234);
            }
        });
        if (postInfo.getCover() != null) {
            try {
                imageLoader.displayImage(postInfo.getCover().getUri(), mPostCover, BitmapUtil.getImageLoaderOption());
            } catch (OutOfMemoryError e) {
                MyLog.e(e);
            } catch (Exception e) {
                MyLog.e(e);
            }
            //帖子封面点击
            mPostCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(postInfo.getCover().getUri());
                    Intent intent = new Intent(IPMoinClipActivity.this, StillsViewPagerActivity.class);
                    intent.putExtra("current", postInfo.getCover().getUri());
                    intent.putExtra("cliplist", list);
                    MyLog.i("click post cover: current=" + postInfo.getCover().getUri());
                    IPMoinClipActivity.this.startActivity(intent);**/
                    gotoPostDetail(postInfo);
                }
            });
        }
        if (postInfo.getAuthor().getAvatar() != null) {
            try {
                imageLoader.displayImage(postInfo.getAuthor().getAvatar().getUri(), mAuthorAvatar, BitmapUtil.getImageLoaderOption());
            } catch (OutOfMemoryError e) {
                MyLog.e(e);
            } catch (Exception e) {
                MyLog.e(e);
            }
        }
        mAuthorAvatar.setOnClickListener(null);
        mAuthorName.setText(postInfo.getAuthor().getNickname());
        mAuthorName.setOnClickListener(null);


        if (postInfo.getAuthor().getLevel() != 10000) {
            mAuthorLevel.setVisibility(View.INVISIBLE);
        }
        mPostUpdateAt.setText(StringUtil.humanDate(postInfo.getUpdatedAt(), WowoContants.COMMENT_TIMESTAMP_PATTERN));
        if (postInfo.getStatus() != 3) { //3是精华贴
            mSuggestImage.setVisibility(View.GONE);
        }
        if (!postInfo.isHasImage()) {
            mPicImage.setVisibility(View.GONE);
        }
        if (!postInfo.isHasEmoji()) {
            mEmojiImage.setVisibility(View.GONE);
        }

        mPostTitle.setText(postInfo.getTitle());
        mPostDesc.setText(postInfo.getContent());

         // 设置分享
        final PostInfo post = postInfo;
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = WowoContants.POST_SHARE_URL + post.getWoid() + "/" + post.getPostid();
                UmengSocialCenter shareCenter = new UmengSocialCenter(IPMoinClipActivity.this);
                shareCenter.setShareContent(post.getTitle(), post.getContent(), url, post.getCover().getUri());
                shareCenter.openShare(IPMoinClipActivity.this);
            }
        });

        // 设置评论action
        mComment.setText("" +postInfo.getCommentCount());
        mCommentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPostDetail(post);
            }
        });
        mPostAuthorArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPostDetail(post);
            }
        });

        mPostCotentArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPostDetail(post);
            }
        });
    }

    private void gotoPostDetail(final PostInfo post) {
        Bundle b = new Bundle();
        b.putSerializable(PostDetailActivity.KEY_WO_ID, post.getWoid());
        b.putString(PostDetailActivity.KEY_POST_ID, post.getPostid());
        AppTools.toIntent(IPMoinClipActivity.this, b, PostDetailActivity.class);
    }

    int touchSlop = 10;
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        float lastY = -1f;
        float currentY = -1f;
        int lastDirection = 0;
        int currentDirection = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            MyLog.i("collapse ontouch@ " + System.currentTimeMillis());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastY = event.getY();
                    currentY = event.getY();
                    currentDirection = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (lastY < 0) {
                        lastY = event.getY();
                    }
//                    if (listView.getFirstVisiblePosition() > 0) {
                        float tmpCurrentY = event.getY();
                        if (Math.abs(tmpCurrentY - lastY) > touchSlop) {
                            currentY = tmpCurrentY;
                            currentDirection = (int) (currentY - lastY);
                            if (lastDirection != currentDirection) {
                                // 向下滑动
                                if (currentDirection > 0) {
                                    panel.setOpen(false, true);
                                }
                            }
                            lastY = currentY;
                        }
//                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    currentDirection = 0;
                    currentY = -1f;
                    lastY = -1f;
                    break;
            }
            return false;
        }
    };

    public void onEvent(LoginSuccJump.JumpIPDetailEvent event) {
        MyLog.i("onEvent:JumpIPDetailEvent");
        IPResourceManager.getInstance().getIPDetails(ip_id, new IListener() {
            @Override
            public void onSuccess(final Object obj) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ipd = (IPDetails) obj;
                        updateIpinfo();
                    }
                });

            }

            @Override
            public void onNoNetwork() {
                ToastUtils.toast(IPMoinClipActivity.this, R.string.no_network);
            }

            @Override
            public void onErr(Object object) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1234 && resultCode == RESULT_OK) {
            // do nothing
        } else {
            UMSocialService mController = UMServiceFactory.getUMSocialService(MoinSocialConstants.NAME_UMENG);
            UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
            if (ssoHandler != null) {
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }
}
