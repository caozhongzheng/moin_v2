package com.moinapp.wuliao.modules.wowo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keyboard.XhsEmoticonsKeyBoardBar;
import com.keyboard.bean.EmoticonBean;
import com.keyboard.utils.Utils;
import com.keyboard.utils.imageloader.ImageBase;
import com.keyboard.view.EmoticonsEditText;
import com.keyboard.view.I.IView;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.gif.GifUtils;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.modal.BaseImage;
import com.moinapp.wuliao.commons.moinsocialcenter.MoinSocialConstants;
import com.moinapp.wuliao.commons.moinsocialcenter.MoinSocialFactory;
import com.moinapp.wuliao.commons.moinsocialcenter.UmengSocialCenter;
import com.moinapp.wuliao.commons.ui.AutoHideUtil;
import com.moinapp.wuliao.commons.ui.BaseEmojActivity;
import com.moinapp.wuliao.commons.ui.XListView;
import com.moinapp.wuliao.modules.ipresource.EmojiResourceActivity;
import com.moinapp.wuliao.modules.ipresource.EmojiUtils;
import com.moinapp.wuliao.modules.ipresource.StillsViewPagerActivity;
import com.moinapp.wuliao.modules.login.LoginActivity;
import com.moinapp.wuliao.modules.wowo.model.AppBean;
import com.moinapp.wuliao.modules.wowo.model.CommentInfo;
import com.moinapp.wuliao.modules.wowo.model.Emoji;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;
import com.moinapp.wuliao.modules.wowo.model.WowoInfo;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.DisplayUtil;
import com.moinapp.wuliao.utils.EmoticonsUtils;
import com.moinapp.wuliao.utils.FileUtil;
import com.moinapp.wuliao.utils.HttpUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.moinapp.wuliao.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by moying on 15/6/8.
 * 帖子详情
 */
public class PostDetailActivity extends BaseEmojActivity {
    private ILogger MyLog = LoggerFactory.getLogger("pd");

    public static final String KEY_WO_ID = "wowoid";
    public static final String KEY_POST_ID = "postid";

    XhsEmoticonsKeyBoardBar kv_bar;
    XListView mXListView;
    View mHeaderView;
    View footer;
    ImageView avatar, emj_iv, iv_type_essence, iv_type_emj, iv_type_image;
    LinearLayout images_ly, reply, tag;
    TextView author, level, time, pinglun_num, title, desc;

    CommentsAdapter adapter;
    private ImageLoader imageLoader;
    public static final int NO_SDCARD = -1;
    public static final int CAMERA_PHOTO_WITH_DATA = 1;// 拍照
    public static final int GALLERY_PHOTO_WITH_DATA = 2;// 图库
    public static final int REMOVE_PHOTO = 3;// 去掉大图
    public static final int REMOVE_FACE = 4;// 去掉大表情

    public static final int MODE_ALL = 1;//:刚开始时获取全部的信息
    public static final int MODE_LOAD_MORE = 2;//:只是加载更多
    public static final int MODE_REPLY_OK = 3; //:刷新最新回复的帖子

    /** 记载拍照或图片的path*/
    String mImagPath;
    int wowoId, lastId = 0;
    boolean loadFull;
    String postId;
    private ArrayList<String> coverList;
    private ArrayList<String> emojiList;
    private ArrayList<Integer> cidList;
    private ArrayList<String> emojiPathList;

    boolean isReplyUseFace = false;
    boolean isReplyUsePhoto = false;
    boolean isReplyWithText = false;
    /**回复的第几楼信息*/
    CommentInfo replyCommentInfo = null;
    /**评论或回复时使用到的表情对象*/
    Emoji replyEmoji = null;
    private int mPicMaxWidth, mEmjMinWidth;
    private PostInfo mPostInfo;//帖子的基本信息

    //帖子分享的变量
    private String mShareUrl;
    private String mShareTitle;
    private String mShareContent;
    private String mShareImage;
    private Dialog dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork() // 这里可以替换为detectAll() 就包括了磁盘读写和网络I/O
                    .penaltyLog() //打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
                    .build());
        }
        super.onCreate(savedInstanceState);

        if(getIntent()!=null) {
            wowoId = getIntent().getIntExtra(KEY_WO_ID, 0);
            postId = getIntent().getStringExtra(KEY_POST_ID);
        }
        if(wowoId <= 0 || StringUtil.isNullOrEmpty(postId)) {
            finish();
            return;
        }

        setContentView(R.layout.post_detail_layout);

        initLoadingView();
        setLoadingMode(MODE_LOADING);
        initTopBar(getString(R.string.wowo_tz_detail));
        initLeftBtn(true, R.drawable.back_gray);
        initRightBtn(true, R.drawable.icon_share);

        EventBus.getDefault().register(this);
        MyLog.i("wowoId="+wowoId+ ", postId="+postId);

        imageLoader = ImageLoader.getInstance();
        if(!imageLoader.isInited())
            imageLoader.init(BitmapUtil.getImageLoaderConfiguration());

        mPicMaxWidth = DisplayUtil.getDisplayWidth(PostDetailActivity.this) - getResources().getDimensionPixelSize(R.dimen.margin_normal)*2;
        mEmjMinWidth = getResources().getDimensionPixelSize(R.dimen.emj_min_width);

        Tools.clearFastSend();

        initView();

        adapter = new CommentsAdapter(PostDetailActivity.this, handler);
        mXListView.setAdapter(adapter);
        mXListView.setOnScrollListener(new XListView.OnXScrollListener() {
            @Override
            public void onXScrolling(View view) {
                kv_bar.hideAutoView();
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        break;
                    case SCROLL_STATE_IDLE:
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        kv_bar.hideAutoView();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        AutoHideUtil.applyListViewAutoHide(PostDetailActivity.this, mXListView, null, footer, 80);

        getPostDetail(MODE_ALL);
    }

    private void initView() {
        mXListView = (XListView) findViewById(R.id.list_view);
        mXListView.setPullLoadEnable(true);
        mXListView.setPullRefreshEnable(false);

        mHeaderView = LayoutInflater.from(PostDetailActivity.this).inflate(R.layout.post_detail_header, null, false);
        avatar = (ImageView) mHeaderView.findViewById(R.id.author_avatar);
        author = (TextView) mHeaderView.findViewById(R.id.author_name);
        level = (TextView) mHeaderView.findViewById(R.id.author_level);

        pinglun_num = (TextView) mHeaderView.findViewById(R.id.comment_count);
        time = (TextView) mHeaderView.findViewById(R.id.post_time);
        title = (TextView) mHeaderView.findViewById(R.id.post_title);

        iv_type_image = (ImageView) mHeaderView.findViewById(R.id.post_pic_image);
        iv_type_emj = (ImageView) mHeaderView.findViewById(R.id.post_emoji_image);
        iv_type_essence= (ImageView) mHeaderView.findViewById(R.id.post_suggest_image);


        tag = (LinearLayout) mHeaderView.findViewById(R.id.post_tag);
        reply = (LinearLayout) mHeaderView.findViewById(R.id.post_reply);
        desc = (TextView) mHeaderView.findViewById(R.id.post_desc);
        emj_iv = (ImageView) mHeaderView.findViewById(R.id.post_emj);
        images_ly = (LinearLayout) mHeaderView.findViewById(R.id.ly_images);
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goReplyComment(null);
            }
        });

        mXListView.addHeaderView(mHeaderView);
        mXListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onLoad();
                        getPostDetail(MODE_LOAD_MORE);
                    }
                }, 0/*2000*/);
            }
        });
        footer = findViewById(R.id.totop);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mXListView.setSelection(0);
            }
        });

        kv_bar = (XhsEmoticonsKeyBoardBar) findViewById(R.id.kv_bar);
        // 大咖秀，大咖秀表情的最后一个是点击加号进入大咖秀,DB中要插入才可以，或者在addOnToolBarItemClickListener 中设置
        // db中已收藏和下载的表情 position==0~builderList.size()-1
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                kv_bar.setBuilder(EmoticonsUtils.getBuilder(PostDetailActivity.this));
            }
        });
        // 别的表情组 position==size()~size()++
//        kv_bar.addToolView(com.keyboard.view.R.drawable.icon_face_pop);

        // 最后一个是点击添加表情按钮去下载表情，只提示一次。
//        kv_bar.addToolView(com.keyboard.view.R.drawable.btn_setup);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toolBtnView = inflater.inflate(R.layout.xhs_view_toolbtn_right_simple, null);
        toolBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击下载表情按钮
                handler.sendEmptyMessage(0x77);
//                kv_bar.del();
            }
        });
        kv_bar.addFixedView(toolBtnView, true);

//        View dkxBtnView = inflater.inflate(R.layout.xhs_view_toolbtn_left_simple,null);
//        dkxBtnView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                kv_bar.show(2);// show dakaxiu
//            }
//        });
//        kv_bar.addFixedView(dkxBtnView, false);
        /**==================================华丽的分割线===================================*/
        // childPosition==1(setBuilder/addToolView),show == 1(add)
        View view =  inflater.inflate(R.layout.xhs_view_apps, null);
        // ly_foot_func.getChildCount(); 这个时候add是加到了ly_foot_func内，add完后childCount是2了，
        // 所以keyborad.show(1) 会显示这个gridView，如果再加view的话，触发view可以挪动到别的地方
        kv_bar.add(view);

        // 加号中图片和拍照用的
        GridView gv_apps = (GridView)view.findViewById(R.id.gv_apps);
        ArrayList<AppBean> mAppBeanList = new ArrayList<AppBean>();
        String[] funcArray = getResources().getStringArray(com.keyboard.view.R.array.apps_func);
        String[] funcIconArray = getResources().getStringArray(com.keyboard.view.R.array.apps_func_icon);
        for (int i = 0; i < funcArray.length; i++) {
            AppBean bean = new AppBean();
            bean.setId(i);
            bean.setIcon(funcIconArray[i]);
            bean.setFuncName(funcArray[i]);
            mAppBeanList.add(bean);
        }
        for (int i = 0; i < 8-funcArray.length; i++) {
            mAppBeanList.add(null);
        }

        final AppsAdapter adapter = new AppsAdapter(this, mAppBeanList, handler);
        gv_apps.setAdapter(adapter);
        /**==================================华丽的分割线===================================*/
        // childPosition==2(addToolView),show == 2(add)
        /*
        View dakaxiu = inflater.inflate(R.layout.dakaxiu_layout, null);
        kv_bar.add(dakaxiu);

        GridView gv_dkxs = (GridView)dakaxiu.findViewById(R.id.gv_dkxs);
        ArrayList<AppBean> mDkxBeanList = new ArrayList<AppBean>();
        AppBean appBean = new AppBean();
        appBean.setId(funcArray.length+20);
        appBean.setIcon("btn_new_cosplay");
        appBean.setFuncName(getString(R.string.cosplay_make));
        mDkxBeanList.add(appBean);
        for (int i = 0; i < 7; i++) {
            mDkxBeanList.add(null);
        }

        // 从数据库或者SDK中获取大咖秀的表情
//        for (int i = 0; i < funcArray.length; i++) {
//            AppBean bean = new AppBean();
//            bean.setId(i+20);
//            bean.setIcon(funcIconArray[i]);
//            bean.setFuncName(funcArray[i]);
//            mDkxBeanList.add(bean);
////            MyLog.i("gv_apps : " + bean.toString());
//        }

        final AppsAdapter adapter_dkx = new AppsAdapter(this, mDkxBeanList, handler);
        gv_dkxs.setAdapter(adapter_dkx);
        */
//
//        kv_bar.getEmoticonsToolBarView().addOnToolBarItemClickListener(new EmoticonsToolBarView.OnToolBarItemClickListener() {
//            @Override
//            public void onToolBarItemClick(int position) {
//                if (position == kv_bar.getTooBtnSize() - 1) {
//                    // 点击下载表情按钮
//                    handler.sendEmptyMessage(0x77);
//                }
//            }
//        });
        // 可以默认选中第几组表情
//        kv_bar.getEmoticonsToolBarView().setToolBtnSelect(0);

        kv_bar.getEt_chat().setHint(R.string.reply_post_author);
        // 初始设置发送按钮不可用
        kv_bar.setSendBtnSendable(false);

        kv_bar.getEmoticonsPageView().addIViewListener(new IView() {
            @Override
            public void onItemClick(EmoticonBean bean) {
                MyLog.i("getEmoticonsPageView.onItemClick: " + bean.toString());
                if (bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
                    //点击下载或收藏的大表情响应事件，自动发送出去（聊天）
//                    ImMsgBean imMsgBean = new ImMsgBean();
//                    imMsgBean.setContent(bean.getIconUri());
//                    imMsgBean.setMsgType(ImMsgBean.CHAT_MSGTYPE_IMG);
//                    adapter.addData(imMsgBean, true, false);
//                    // listview 滚到最后一句
//                    mXListView.setSelection(mXListView.getBottom());

                    // 点击下载或收藏的大表情，使笑脸变成我们的大表情缩略图，加号变为相册拍照或者图片的缩略图
                    Bitmap bmm = imageLoader.loadImageSync(bean.getIconUri(), BitmapUtil.getImageLoaderOption());
                    if (bmm == null) {
                        bmm = BitmapFactory.decodeFile(bean.getIconUri());
                        if (bmm == null) {
                            isReplyUseFace = false;
                        } else {
                            kv_bar.getBtn_emj().setImageBitmap(bmm);
                            kv_bar.setFaceVisibility(false);
                            isReplyUseFace = true;
                        }
                    } else {
                        kv_bar.getBtn_emj().setImageBitmap(bmm);
                        kv_bar.setFaceVisibility(false);
                        isReplyUseFace = true;
                    }
                    kv_bar.setSendBtnSendable(isReplyUseFace || isReplyUsePhoto || isReplyWithText);

                    replyEmoji = new Emoji();
                    replyEmoji.setId(Integer.parseInt(bean.getId()));
                    replyEmoji.setParentid(bean.getParentId());
                    replyEmoji.setType(0);
                    BaseImage emojiIcon = new BaseImage();
                    emojiIcon.setUri(bean.getGifUri());
                    replyEmoji.setImage(emojiIcon);
                }
            }

            @Override
            public boolean onItemLongClick(int position, View converView, EmoticonBean bean) {
                int[] location = new int[2];
                // 获取当前view在屏幕中的绝对位置
                converView.getLocationOnScreen(location);

                GifDialog myDialog = new GifDialog(PostDetailActivity.this, R.style.MyGifDialogStyle, bean.getGifUri());
                WindowManager.LayoutParams params = myDialog.getWindow().getAttributes();
                params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
//                params.x = location[0];
                params.y = DisplayUtil.getDisplayHeight(PostDetailActivity.this) - location[1];
                myDialog.getWindow().setAttributes(params);
                myDialog.setCanceledOnTouchOutside(true);
                myDialog.show();
                return true;
            }

            @Override
            public void onItemDisplay(EmoticonBean bean) {
            }

            @Override
            public void onPageChangeTo(int position) {
            }
        });

        kv_bar.setOnKeyBoardBarViewListener(new XhsEmoticonsKeyBoardBar.KeyBoardBarViewListener() {
            @Override
            public void OnKeyBoardStateChange(int state, int height) {
                mXListView.post(new Runnable() {
                    @Override
                    public void run() {
                        mXListView.setSelection(mXListView.getAdapter().getCount() - 1);
                    }
                });
            }

            @Override
            public void OnEmjBtnClick() {
                if (replyEmoji != null && replyEmoji.getImage() != null && !StringUtil.isNullOrEmpty(replyEmoji.getImage().getUri())) {
                    Intent intent = new Intent(PostDetailActivity.this, DeleteFaceActivity.class);
                    intent.putExtra(DeleteFaceActivity.KEY_PATH, replyEmoji.getImage().getUri());
                    startActivityForResult(intent, REMOVE_FACE);
                }
            }

            @Override
            public void OnPhotoBtnClick() {
                if (!StringUtil.isNullOrEmpty(mImagPath)) {
                    Intent intent = new Intent(PostDetailActivity.this, DeleteImageActivity.class);
                    intent.putExtra(DeleteImageActivity.KEY_PATH, mImagPath);
                    startActivityForResult(intent, REMOVE_PHOTO);
                } else {
                    kv_bar.show(1);
                }
            }

            @Override
            public void OnSendBtnClick(final String msg) {
                if (Tools.isFastDoubleSend()) {
                    ToastUtils.toast(PostDetailActivity.this, R.string.click_too_fast);

                    MyLog.w(StringUtil.formatDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss S") + getString(R.string.click_too_fast));
                    return;
                }
                if (!ClientInfo.isUserLogin()) {
                    ToastUtils.toast(PostDetailActivity.this, R.string.login_first);
                    Bundle b = new Bundle();
                    b.putBoolean(LoginActivity.KEY_NEED_RETURN, true);
                    AppTools.toIntent(PostDetailActivity.this, b, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                    return;
                }
                kv_bar.getSendBtn().setClickable(false);
                if (!StringUtil.isNullOrEmpty(mImagPath)) {
                    File file = new File(mImagPath);
                    if(file.length() >= WowoContants.WO_IMAGE_MAX_MB ||
                            FileUtil.getExtensionName(mImagPath).equals("webp")) {
                        // 质量压缩
                        int quality = 80;
                        MyLog.i(mImagPath + " 's size(KB)=" + file.length()/1024);
                        String path_new = BitmapUtil.getReplyCompressPath(8);
                        Bitmap bitmap =
                                com.moinapp.wuliao.modules.wowo.imageloader.ImageLoader.getInstance()
                                        .decodeSampledBitmapFromResource(mImagPath, WowoContants.WO_IMAGE_MAX_WIDTH, WowoContants.WO_IMAGE_MAX_HEIGHT);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                        MyLog.i("按宽度缩小后图片是" + baos.toByteArray().length / 1024 + " KB, 宽高：" + bitmap.getWidth() + "*" + bitmap.getHeight());
                        if(baos.toByteArray().length  < WowoContants.WO_IMAGE_MAX_MB) {
                            boolean ret = BitmapUtil.saveBitmap2file(bitmap, path_new, Bitmap.CompressFormat.JPEG, quality);
                            if (ret) {
                                mImagPath = path_new;
                                MyLog.i(" decode图片OK: " + path_new);
                            }
                        } else {
                            MyLog.i(" 压缩图片 ");
                            Bitmap compress = BitmapUtil.compressImage(bitmap, 90, WowoContants.WO_IMAGE_MAX_KB);
                            boolean ret = BitmapUtil.saveBitmap2file(compress, path_new, Bitmap.CompressFormat.JPEG, quality);
                            if (ret) {
                                mImagPath = path_new;
                                MyLog.i(" 压缩图片OK: " + path_new);
                            }
                        }
                    }
                    WowoManager.getInstance().uploadImage(mImagPath, new IPostListener() {
                        @Override
                        public void onGetPostListSucc(List<PostInfo> postInfoList, int column) {

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
                            MyLog.i("图片上传成功 picid：" + picid);
                            doReplyPost(msg, picid);
                        }

                        @Override
                        public void onNoNetwork() {
                            handler.sendEmptyMessage(0x99);
                            MyLog.i("图片上传失败 无网络：");
                        }

                        @Override
                        public void onErr(Object object) {
                            handler.sendEmptyMessage(0x98);
                            MyLog.i("图片上传失败 ：" + object);
                        }
                    });
                } else {
                    doReplyPost(msg, null);
                }
            }

            @Override
            public void OnVoiceBtnClick() {
            }

            @Override
            public void OnMultimediaBtnClick() {
            }
        });

        if (!ClientInfo.isUserLogin()) {
//            kv_bar.getEt_chat().setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    ToastUtils.toast(PostDetailActivity.this, R.string.login_first);
//                    Bundle b = new Bundle();
//                    b.putBoolean(LoginActivity.KEY_NEED_RETURN, true);
//                    b.putInt(LoginActivity.KEY_FROM, LoginConstants.POST_DETAIL_REPLY);
//                    AppTools.toIntent(PostDetailActivity.this, b, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
//                    return true;
//                }
//            });
        } else {
            kv_bar.getEt_chat().setFocusable(true);
            kv_bar.getEt_chat().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }

        kv_bar.getEt_chat().setOnTextChangedInterface(new EmoticonsEditText.OnTextChangedInterface() {
            @Override
            public void onTextChanged(CharSequence arg0) {
                isReplyWithText = !TextUtils.isEmpty(arg0.toString());
                kv_bar.setSendBtnSendable(isReplyUseFace || isReplyUsePhoto || isReplyWithText);
            }
        });
    }

    // 执行回复任务
    private void doReplyPost(final String content, String picid) {
        if(StringUtil.isNullOrEmpty(picid) && replyEmoji==null) {
            // TODO 发帖标题禁止使用回车键
            if (StringUtil.isNullOrEmpty(content.replaceAll(" ", " ").replaceAll(" ", "").replace("\n", ""))) {
                ToastUtils.toast(PostDetailActivity.this, R.string.reply_hint);
                return;
            }
        }
//        MyLog.i("执行回复：content" + content + ", picid=" + picid + ", replyCommentInfo " + replyCommentInfo);
        MyLog.i("执行回复：content" + content);
        MyLog.i("执行回复：picid=" + picid);
        MyLog.i("执行回复：replyCommentInfo " + replyCommentInfo);
        MyLog.i("执行回复：replyEmoji" + replyEmoji);

        WowoManager.getInstance().replyPost(wowoId, postId, StringUtil.filter(content), picid, replyEmoji, replyCommentInfo,
                new IPostListener() {
                    @Override
                    public void onGetPostListSucc(List<PostInfo> postInfoList, int column) {

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
                        MyLog.i("回帖成功 commentid：" + commentid);
                        handler.sendEmptyMessage(0x44);
                    }

                    @Override
                    public void onGetPostSucc(PostInfo postInfo, List<CommentInfo> commentInfos) {

                    }

                    @Override
                    public void onUploadImageSucc(String picid) {

                    }

                    @Override
                    public void onNoNetwork() {
                        handler.sendEmptyMessage(0x99);
                        kv_bar.getSendBtn().setClickable(true);
                        MyLog.i("回帖失败 无网络：");
                    }

                    @Override
                    public void onErr(Object object) {
                        kv_bar.getSendBtn().setClickable(true);
                        handler.sendEmptyMessage(0x98);
                        MyLog.i("回帖失败 ：" + object);
                    }
                });
    }

    @Override
    protected void rightBtnHandle() {
        super.rightBtnHandle();
        Utils.closeSoftKeyboard(PostDetailActivity.this);
        MyLog.i("imageUrl= " + mShareImage + ",title=" + mShareTitle + ",content="+mShareContent);
        UmengSocialCenter shareCenter = new UmengSocialCenter(PostDetailActivity.this);
        shareCenter.setShareContent(mShareTitle, mShareContent, mShareUrl, mShareImage);
        shareCenter.openShare(PostDetailActivity.this);
    }

    @Override
    protected void reloadHandle() {
        super.reloadHandle();
        getPostDetail(MODE_ALL);
    }

    private void updateShareInfo() {
        if(mPostInfo == null)
            return;
        mShareUrl = WowoContants.POST_SHARE_URL + wowoId + "/" + postId;
        //分享的缩略图

        if (mPostInfo.isHasImage() &&
                mPostInfo.getImages() != null &&
                mPostInfo.getImages().get(0) != null &&
                mPostInfo.getImages().get(0).getUri() != null) {
            mShareImage = mPostInfo.getImages().get(0).getUri();
        } else {
            mShareImage = null;//"http://1.jpg";//假的数据，umeng需要提供一个http开头的url
        }
        mShareTitle = StringUtil.nullToEmpty(mPostInfo.getTitle());
        mShareContent = TextUtils.isEmpty(mPostInfo.getContent()) ? " ": mPostInfo.getContent();
    }


    /**
     * @param type  */
    private void getPostDetail(final int type) {

        if (loadFull) {
            mXListView.stopLoadMore();
            return;
        }
        int commentsCount = adapter.getCommentList().size();
        lastId = commentsCount == 0 ? 0 : adapter.getCommentList().get(commentsCount - 1).getCid();

        MyLog.i("getPostDetail lastId= " + lastId);

        WowoManager.getInstance().getPostDetail(wowoId, postId, lastId, new IPostListener() {

            @Override
            public void onGetPostListSucc(List<PostInfo> postInfoList, int column) {

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
            public void onGetPostSucc(final PostInfo postInfo, final List<CommentInfo> commentInfos) {
                MyLog.i("getPostDetail onGetPostSucc");
                mPostInfo = postInfo;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoadingMode(MODE_OK);
                        mXListView.stopLoadMore();
                        if (lastId == 0 && type == MODE_ALL) {
                            updatePostInfo(postInfo);
                        }
                        updateComments(commentInfos, type);
                        if (type == MODE_ALL) {
                            updateShareInfo();
                        }
                    }
                });

            }

            @Override
            public void onUploadImageSucc(String picid) {

            }

            @Override
            public void onNoNetwork() {
                Message nonet = new Message();
                nonet.what = 0x109;
                nonet.arg1 = type;
                handler.sendMessage(nonet);
                MyLog.i("getPostDetail nonetwork");
            }

            @Override
            public void onErr(Object object) {
                Message err = new Message();
                err.what = 0x110;
                err.arg1 = type;
                handler.sendMessage(err);
                MyLog.i("getPostDetail failed " + object);
            }
        });
    }

    private void onLoad() {
        mXListView.stopRefresh();
//        mXListView.stopLoadMore();
        mXListView.setRefreshTime(StringUtil.humanDate(System.currentTimeMillis(), WowoContants.COMMENT_TIMESTAMP_PATTERN));
    }

    private void updatePostInfo(final PostInfo postInfo) {
        if(postInfo == null)
            return;

        MyLog.i("updatePostInfo:" + postInfo);
        if(postInfo.getImages() != null) {
            MyLog.i("postInfo.getImages():" + postInfo.getImages().size());
            for (int i = 0; i < postInfo.getImages().size(); i++) {
                MyLog.i((i+1)+":" + postInfo.getImages().get(i).getUri());
            }
        }
        if (postInfo.getAuthor().getAvatar() != null) {
            try {
                imageLoader.displayImage(postInfo.getAuthor().getAvatar().getUri(), avatar, BitmapUtil.getImageLoaderOption());
            } catch (OutOfMemoryError e) {
                MyLog.e(e);
            } catch (Exception e) {
                MyLog.e(e);
            }
        }
        author.setText(postInfo.getAuthor().getNickname());
        level.setText(getLevel(postInfo.getAuthor().getLevel()));
        pinglun_num.setText(postInfo.getCommentCount()+"");
        time.setText(StringUtil.humanDate(postInfo.getCreatedAt(), WowoContants.COMMENT_TIMESTAMP_PATTERN));

        title.setText(postInfo.getTitle());
        tag.removeAllViews();
        if(!StringUtil.isNullOrEmpty(postInfo.getTag())) {
            String[] tagarr = postInfo.getTag().split(",");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            params.rightMargin = getResources().getDimensionPixelSize(R.dimen.icon_margin);
            int one_dp = DisplayUtil.dip2px(PostDetailActivity.this, 1);
            for (int j = 0; j < tagarr.length; j++) {
                MyLog.i("tag:" + tagarr[j]);
                if(!StringUtil.isNullOrEmpty(tagarr[j])) {
                    TextView tv_tag = new TextView(PostDetailActivity.this);
                    tv_tag.setText(tagarr[j]);
                    tv_tag.setTextColor(getResources().getColor(R.color.common_text_main));
                    tv_tag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    tv_tag.setBackground(getResources().getDrawable(R.drawable.tag_btn_bg));
                    tv_tag.setPadding(one_dp * 5, one_dp, one_dp * 5, one_dp);
                    tag.addView(tv_tag, params);
                }
            }
        }
        if(StringUtil.isNullOrEmpty(postInfo.getContent())){
            desc.setVisibility(View.GONE);
        }else{
            desc.setText(postInfo.getContent());
        }


        if(postInfo.getImages() != null && postInfo.getImages().size() > 0) {
            if(coverList == null) {
                coverList = new ArrayList<>();
            }

            coverList.clear();
            for (int i = 0; i < postInfo.getImages().size(); i++) {
                coverList.add(postInfo.getImages().get(i).getUri());
            }

            images_ly.setVisibility(View.VISIBLE);
            MyLog.i("updatePostInfo mPicMaxWidth" + mPicMaxWidth);
            for (int i = 0; i < postInfo.getImages().size(); i++) {
                BaseImage pic = postInfo.getImages().get(i);
                MyLog.i("updatePostInfo img" + i + " = " + pic.getUri());

                ImageView imageView = new ImageView(PostDetailActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_START);
                imageLoader.displayImage(pic.getUri(), imageView, BitmapUtil.getImageLoaderOptionWithDefaultIcon());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_HORIZONTAL;

                int[] newwh = EmojiUtils.getScaleHeight(pic.getWidth(), pic.getHeight(), mPicMaxWidth);
                params.width = newwh[0];
                params.height = newwh[1];
                if(newwh[1] <= 0) {
                    Bitmap bmp = imageLoader.loadImageSync(pic.getUri());
                    if(bmp != null) {
                        int[] params2 = EmojiUtils.getScaleHeight(bmp.getWidth(), bmp.getHeight(), mPicMaxWidth);
//                        MyLog.i("loadImageSync w*h" + bmp.getWidth() + " * " + bmp.getHeight() + " , new height=[" + params2[1] + "]");
                        params.width = params2[0];
                        params.height = params2[1];
                    }
                }

                MyLog.i("updatePostInfo img.W*H = [" + pic.getWidth() + "*" + pic.getHeight() + "]" + ", new height=[" + params.height + "]");
                params.topMargin = getResources().getDimensionPixelSize(R.dimen.margin_normal);

                images_ly.addView(imageView, params);
                imageView.setOnClickListener(new CoverItemClickListener(pic.getUri()));
            }

            iv_type_image.setVisibility(View.VISIBLE);
        } else {
            images_ly.setVisibility(View.GONE);
            iv_type_image.setVisibility(View.GONE);
        }

        if(postInfo.getEmoji() != null) {

            iv_type_emj.setVisibility(View.VISIBLE);
            new Thread() {
                @Override
                public void run() {
                    Message message = Message.obtain(handler, 0x33);
                    message.obj = postInfo;
                    message.sendToTarget();
                }
            }.start();

        } else {
            emj_iv.setVisibility(View.GONE);
            iv_type_emj.setVisibility(View.GONE);
        }

        iv_type_essence.setVisibility(postInfo.getStatus() == 3 ? View.VISIBLE : View.GONE);

        showHelp();
    }

    private void showHelp() {
        if(dia != null)
            return;
        boolean isFirst = WowoPreference.getInstance().isFirstPostDetail();
        if(isFirst) {
            try {
                Context context = PostDetailActivity.this;
                dia = new Dialog(context, R.style.edit_AlertDialog_style);
                dia.setContentView(R.layout.post_detail_help_dialog);

                View.OnClickListener dismissListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WowoPreference.getInstance().setFirstPostDetail(false);
                        dia.dismiss();
                    }
                };

                dia.findViewById(R.id.dialog_rl).setOnClickListener(dismissListener);
                dia.findViewById(R.id.help_post_detail_img).setOnClickListener(dismissListener);

                dia.show();

                dia.setCanceledOnTouchOutside(true); // Sets whether this dialog is
                // canceled when touched outside
                // the window's bounds.
                dia.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        WowoPreference.getInstance().setFirstPostDetail(false);
                    }
                });
                Window w = dia.getWindow();
                WindowManager.LayoutParams lp = w.getAttributes();
                lp.x = 0;
                lp.y = 0;
                lp.width = DisplayUtil.getDisplayWidth(context);
                lp.height = DisplayUtil.getDisplayHeight(context);
                dia.onWindowAttributesChanged(lp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getLevel(int level) {
        if (level == 10000) {
            return getString(R.string.banzhu);
        }
        return "";
    }

    private void updateComments(List<CommentInfo> commentInfos, int type) {
        mXListView.stopLoadMore();

        if(commentInfos == null || commentInfos.size() <= 0) {
            loadFull = true;
            mXListView.setLoadMoreOver();
            MyLog.i("updateComments load full");
            if(adapter.getCommentList().size() > 5) {
                ToastUtils.toast(PostDetailActivity.this, R.string.list_end);
            }
            return;
        }
        MyLog.i("updateComments lastid = " + lastId);
        /**取到最后一页了*/
        if(commentInfos.size() < 5) {
            loadFull = true;
            mXListView.setLoadMoreOver();
            MyLog.i("updateComments load full");
            if(adapter.getCommentList().size() > 5 && type != MODE_REPLY_OK) {
                ToastUtils.toast(PostDetailActivity.this, R.string.list_end);
            }
        }
        for (int i = 0; i < commentInfos.size(); i++) {
            MyLog.i("第" + commentInfos.get(i).getCid() + "楼:" + commentInfos.get(i).toString());
            CommentInfo tmp = commentInfos.get(i);
            if(tmp.getImages() != null && tmp.getImages().size() > 0) {
                MyLog.i("第" + commentInfos.get(i).getCid() + "楼有图:" + tmp.getImages().get(0).getUri());
                if(coverList == null) {
                    coverList = new ArrayList<>();
                }

                coverList.add(tmp.getImages().get(0).getUri());
            }
            if(tmp.getEmoji() != null && tmp.getEmoji().getPicture() != null &&
                    !StringUtil.isNullOrEmpty(tmp.getEmoji().getPicture().getUri())) {
                String emjUrl = tmp.getEmoji().getPicture().getUri();
                String emjPath = EmojiUtils.getEmjPath(tmp.getEmoji());
                MyLog.i("第" + commentInfos.get(i).getCid() + "楼有表情url:" + emjUrl);
                MyLog.i("第" + commentInfos.get(i).getCid() + "楼有表情path:" + emjPath);
                if(emojiList == null) {
                    emojiList = new ArrayList<>();
                }
                if(emojiPathList == null) {
                    emojiPathList = new ArrayList<>();
                }
                if(cidList == null) {
                    cidList = new ArrayList<>();
                }
                cidList.add(commentInfos.get(i).getCid());
                emojiList.add(emjUrl);
                emojiPathList.add(emjPath);
            }
        }

        if(lastId == 0) {
            adapter.getCommentList().clear();
        }

        MyLog.i("回复前有" + adapter.getCommentList().size() + "楼");
        adapter.getCommentList().addAll(commentInfos);
        MyLog.i("回复后有" + adapter.getCommentList().size() + "楼");

        for (int i = 0; i < adapter.getCommentList().size(); i++) {
            CommentInfo tmp = adapter.getCommentList().get(i);
            if(tmp.getImages() != null && tmp.getImages().size() > 0) {
                MyLog.i("第" + tmp.getCid() + "楼有图:" + tmp.getImages().get(0).getUri());
            }
            if(tmp.getEmoji() != null && tmp.getEmoji().getPicture() != null &&
                    !StringUtil.isNullOrEmpty(tmp.getEmoji().getPicture().getUri())) {
                String emjUrl = tmp.getEmoji().getPicture().getUri();
                String emjPath = EmojiUtils.getEmjPath(tmp.getEmoji());
                MyLog.i("第" + tmp.getCid() + "楼有表情url:" + emjUrl);
                MyLog.i("第" + tmp.getCid() + "楼有表情path:" + emjPath);
            }
        }

        adapter.notifyDataSetChanged();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 选择图片
                case 0x11:
                    AppBean bean = (AppBean) msg.obj;
                    if(bean.getId() == 0) {
                        doGallery();
                    } else if(bean.getId() == 1) {
                        doCamera();
                    }
                    break;
                // 去回复某楼
                case 0x22:
                    CommentInfo commentInfo = (CommentInfo) msg.obj;
                    goReplyComment(commentInfo);
                    break;
                // 点击某楼的图片
                case 0x24:
                    String photoUri = (String) msg.obj;
                    viewAllPhotos(photoUri);
                    break;
                // 显示帖子主贴中的表情大图
                case 0x33:
                    final PostInfo postInfo = (PostInfo) msg.obj;
                    if (postInfo.getEmoji().getType() == 0) {
                        final String emjUrl = postInfo.getEmoji().getPicture().getUri();
//                        String emjPath = BitmapUtil.BITMAP_DOWNLOAD +emjUrl.substring(emjUrl.lastIndexOf("/")+1);
                        final String emjPath = EmojiUtils.getEmjPath(postInfo.getEmoji());
                        if(emojiList == null) {
                            emojiList = new ArrayList<>();
                        }
                        emojiList.add(0, emjUrl);

                        if(emojiPathList == null) {
                            emojiPathList = new ArrayList<>();
                        }
                        emojiPathList.add(0, emjPath);

                        if(cidList == null) {
                            cidList = new ArrayList<>();
                        }
                        cidList.add(0, 0);
                        MyLog.i("主楼添加了表情Url:" + emojiList.get(0));
                        MyLog.i("主楼添加了表情Path:" + emojiPathList.get(0));

                        this.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean down = HttpUtil.download(emjUrl, emjPath);
                                if (down) {
                                    GifUtils.displayGif(emj_iv, new File(emjPath));
                                    emj_iv.setVisibility(View.VISIBLE);

                                    MyLog.i("updatePostInfo mEmjMinWidth" + mEmjMinWidth);
                                    try {
                                        ViewGroup.LayoutParams params = emj_iv.getLayoutParams();
                                        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
//                                final BitmapFactory.Options options = new BitmapFactory.Options();
//                                options.inJustDecodeBounds = true;
//                                Bitmap bmp = BitmapFactory.decodeFile(emjPath, options);
                                        int gifWidth = postInfo.getEmoji().getPicture().getWidth();
                                        int gifHeight = postInfo.getEmoji().getPicture().getHeight();

                                        params.height = EmojiUtils.getScaleEmjHeight(gifWidth, gifHeight, mEmjMinWidth)[1];
                                        MyLog.i("updatePostInfo img.W*H = [" + gifWidth + "*" + gifHeight + "]" + ", new height=[" + params.height + "]");
                                        emj_iv.setLayoutParams(params);
                                    } catch (Exception e) {
                                        MyLog.e(e);
                                    }

                                    emj_iv.setOnClickListener(new EmojiItemClickListener(0, emjUrl));
                                } else {
                                    emj_iv.setVisibility(View.GONE);
                                }
                            }
                        });

                    } else if (postInfo.getEmoji().getType() == 1) {
                        // TODO 根据大咖秀表情的id，url(服务器接收到用户上传的自制的大咖秀表情后，应该是按url惯例的)
                    }
                    break;
                case 0x36:
                    String emjPath = (String) msg.obj;
                    viewAllEmojis(msg.arg1, emjPath);

                    break;
                // 回帖成功
                case 0x44:
                    ToastUtils.toast(PostDetailActivity.this, R.string.reply_success);

                    kv_bar.setFaceVisibility(true);
                    kv_bar.toggleMultiMediaVisibility(true);
                    kv_bar.clearEditText();
                    kv_bar.getEt_chat().setHint(R.string.reply_post_author);
                    isReplyUsePhoto = false;
                    isReplyUseFace = false;
                    isReplyWithText = false;
                    replyCommentInfo = null;
                    replyEmoji = null;
                    if(!StringUtil.isNullOrEmpty(mImagPath) && mImagPath.contains(BitmapUtil.REPLY_CAPTURE_PREFIX)) {
                        new File(mImagPath).delete();
                    }
                    mImagPath = "";
                    kv_bar.setSendBtnSendable(isReplyUseFace || isReplyUsePhoto || isReplyWithText);
                    kv_bar.hideAutoView();

//                    if(msg.obj != null) {
//                        adapter.getCommentList().add((CommentInfo) msg.obj);
//                        adapter.notifyDataSetChanged();
//                    }
                    loadFull = false;
                    getPostDetail(MODE_REPLY_OK);
                    break;
                // 去下载表情
                case 0x77:
                    // if first time click download moinEmj, please dialog to notice user only once.
                    if(!WowoPreference.getInstance().isVisitEmojiList()) {
                        WowoPreference.getInstance().setVisitEmojiList(true);
                        ToastUtils.toast(PostDetailActivity.this, "下载新的大表情去咯~");
                        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
                        builder.setMessage("下载新的表情");
                        builder.setTitle("提示");
                        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                AppTools.toIntent(PostDetailActivity.this, EmojiResourceActivity.class);
                            }

                        });
                        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }

                        });
                        builder.create().show();
                    } else {
                        AppTools.toIntent(PostDetailActivity.this, EmojiResourceActivity.class);
                    }
                    break;
                case 0x99:
                    ToastUtils.toast(PostDetailActivity.this, R.string.no_network);
                    break;
                case 0x98:
                    ToastUtils.toast(PostDetailActivity.this, R.string.reply_failed_toast);
                    break;
                case 0x109:
                    if(msg.arg1 == MODE_ALL) {
                        setLoadingMode(MODE_RELOADING);
                    }
                    mXListView.stopLoadMore();
                    ToastUtils.toast(PostDetailActivity.this, R.string.no_network);
                    break;
                case 0x110:
                    mXListView.stopLoadMore();
                    if(msg.arg1 == MODE_ALL) {
                        setLoadingMode(MODE_RELOADING);
                        ToastUtils.toast(PostDetailActivity.this, R.string.connection_failed);
                    } else if(msg.arg1 == MODE_LOAD_MORE) {
                        ToastUtils.toast(PostDetailActivity.this, R.string.load_error);
                    } else if(msg.arg1 == MODE_REPLY_OK) {
                        ToastUtils.toast(PostDetailActivity.this, R.string.load_error);
                    } else
                    break;
            }

        }
    };

    /**
     * 回复某条评论
     * @param commentInfo
     */
    private void goReplyComment(CommentInfo commentInfo) {

        kv_bar.getEt_chat().requestFocus();
        kv_bar.showAutoView();

        kv_bar.getEt_chat().setText("");
        if(commentInfo != null) {
            kv_bar.getEt_chat().setHint(String.format(getResources().getString(R.string.reply_referral_nickname), commentInfo.getAuthor().getNickname()));
        } else {
            kv_bar.getEt_chat().setHint(R.string.reply_post_author);
        }

        replyCommentInfo = commentInfo;
    }


    /** 去拍照*/
    private String capturePath;
    private void doCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        capturePath = BitmapUtil.getReplyCapturePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(capturePath)));
        startActivityForResult(intent, CAMERA_PHOTO_WITH_DATA);
    }
    /**去选择图片*/
    private void doGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_PHOTO_WITH_DATA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        UMSocialService mController = UMServiceFactory.getUMSocialService(MoinSocialConstants.NAME_UMENG);
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }


        switch (requestCode) {
            case NO_SDCARD:
                ToastUtils.toast(this, R.string.label_no_sdcard);
                break;
            case REMOVE_FACE:
                if(data != null) {
                    boolean deleted = data.getBooleanExtra(DeleteFaceActivity.KEY_DELETE, false);
                    if(deleted) {
                        kv_bar.getBtn_emj().setImageResource(com.keyboard.view.R.drawable.btn_emoj_grey);
                        kv_bar.setFaceVisibility(true);
                        isReplyUseFace = false;
                        kv_bar.setSendBtnSendable(isReplyUseFace || isReplyUsePhoto || isReplyWithText);
                        replyEmoji = null;
                    }
                }
                break;
            case REMOVE_PHOTO:
                if(data != null) {
                    boolean deleted = data.getBooleanExtra(DeleteImageActivity.KEY_DELETE, false);
                    if(deleted) {
                        kv_bar.getBtn_photo().setImageResource(com.keyboard.view.R.drawable.btn_multi_bg);
                        kv_bar.toggleMultiMediaVisibility(true);
                        if(mImagPath.contains(BitmapUtil.REPLY_CAPTURE_PREFIX)) {
                            new File(mImagPath).delete();
                            MyLog.i("删除临时图片 success =" + mImagPath);
                        }
                        mImagPath = "";
                        isReplyUsePhoto = false;
                        kv_bar.setSendBtnSendable(isReplyUseFace || isReplyUsePhoto || isReplyWithText);
                    }
                }
                break;
            case CAMERA_PHOTO_WITH_DATA:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (!AppTools.existsSDCARD()) {
                            MyLog.v("SD card is not avaiable/writeable right now.");
                            return;
                        }
                        mImagPath = capturePath;
                        kv_bar.getBtn_photo().setImageURI(Uri.fromFile(new File(mImagPath)));
                        kv_bar.toggleMultiMediaVisibility(false);
                        isReplyUsePhoto = true;
                        kv_bar.setSendBtnSendable(isReplyUseFace || isReplyUsePhoto || isReplyWithText);

                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case GALLERY_PHOTO_WITH_DATA:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Uri uri = data.getData();

                        MyLog.i("你选择了图片uri:" + uri);

                        switch(ImageBase.Scheme.ofUri(uri.toString())) {
                            case FILE:
                                mImagPath = ImageBase.Scheme.FILE.crop(uri.toString());

                                selectPhotoOver();
                                break;
                            case CONTENT:
                                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                                if(cursor == null) {
                                    MyLog.w("抱歉，没找到图片:"+uri);
                                } else {
                                    cursor.moveToFirst();
                                    /**index 0:图片编号, 1:图片文件路径, 2:图片大小, 3:图片文件名*/
                                    mImagPath = cursor.getString(1); //
                                    cursor.close();

                                    selectPhotoOver();
                                }
                                break;
                        }

                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;

        }
    }

    private void selectPhotoOver() {
        // OutOfMemoryError
        Bitmap gallery =
                com.moinapp.wuliao.modules.wowo.imageloader.ImageLoader.getInstance()
                        .decodeSampledBitmapFromResource(mImagPath, 300, 300);

        kv_bar.getBtn_photo().setImageBitmap(gallery);
        kv_bar.toggleMultiMediaVisibility(false);
        isReplyUsePhoto = true;
        kv_bar.setSendBtnSendable(isReplyUseFace || isReplyUsePhoto || isReplyWithText);
    }

    private class CoverItemClickListener implements View.OnClickListener {
        private String url;

        public CoverItemClickListener(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View view) {
            viewAllPhotos(url);
        }
    }

    private void viewAllPhotos(String currentUrl) {
        Intent intent = new Intent(PostDetailActivity.this, StillsViewPagerActivity.class);
        intent.putExtra(StillsViewPagerActivity.KEY_CURRENT, currentUrl);
        intent.putExtra(StillsViewPagerActivity.KEY_CLIPLIST, coverList);
        startActivity(intent);
    }

    private class EmojiItemClickListener implements View.OnClickListener {
        private int mCid;
        private String mUrl;

        public EmojiItemClickListener(int cid, String url) {
            this.mCid = cid;
            this.mUrl = url;
        }

        @Override
        public void onClick(View view) {
            viewAllEmojis(mCid, mUrl);
        }
    }

    private void viewAllEmojis(int cid, String currentUrl) {
        Intent intent = new Intent(PostDetailActivity.this, EmojisViewPagerActivity.class);
        intent.putExtra(EmojisViewPagerActivity.KEY_CID, cid);
        intent.putExtra(EmojisViewPagerActivity.KEY_CURRENT_URL, currentUrl);
        intent.putExtra(EmojisViewPagerActivity.KEY_EMJURL_LIST, emojiList);
        intent.putExtra(EmojisViewPagerActivity.KEY_CID_LIST, cidList);
        intent.putExtra(EmojisViewPagerActivity.KEY_EMJPATHLIST, emojiPathList);
        startActivity(intent);
    }

//    public void onEvent(LoginSuccJump.JumpPostReplyEvent event) {
//        MyLog.i("onEvent:JumpPostReplyEvent");
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                MyLog.i("onEvent:JClientInfo.isUserLogin()="+ClientInfo.isUserLogin());
//                kv_bar.getEt_chat().setOnTouchListener(null);
//                kv_bar.getEt_chat().setFocusable(true);
//                kv_bar.getEt_chat().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
//            }
//        });
//    }
}
