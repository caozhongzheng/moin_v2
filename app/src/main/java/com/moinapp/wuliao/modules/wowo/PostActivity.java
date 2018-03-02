package com.moinapp.wuliao.modules.wowo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keyboard.XhsEmoticonsKeyBoardBar;
import com.keyboard.bean.EmoticonBean;
import com.keyboard.view.I.IView;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.commons.ui.BaseEmojActivity;
import com.moinapp.wuliao.commons.ui.MyPopWindow;
import com.moinapp.wuliao.modules.ipresource.EmojiResourceActivity;
import com.moinapp.wuliao.modules.wowo.imageloader.SelectPhotoActivity;
import com.moinapp.wuliao.modules.wowo.model.CommentInfo;
import com.moinapp.wuliao.modules.wowo.model.Emoji;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;
import com.moinapp.wuliao.modules.wowo.model.WowoInfo;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.DisplayUtil;
import com.moinapp.wuliao.utils.EmoticonsUtils;
import com.moinapp.wuliao.utils.FileUtil;
import com.moinapp.wuliao.utils.NetworkUtils;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.moinapp.wuliao.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by moying on 15/6/10.
 * 发帖
 */
public class PostActivity extends BaseEmojActivity implements View.OnClickListener {
    private ILogger MyLog = LoggerFactory.getLogger("pa");
    public static final String KEY_WO_ID = "wowoid";

    private LayoutInflater inflater;
    private XhsEmoticonsKeyBoardBar kv_bar;
    private EditText mTitle, mContent;
    private TextView mTv_tag, tv_camera, tv_gallery, tv_cancel;
    private LinearLayout mLy_photo;
    /** 发帖时添加图片的layoutParams*/
    private LinearLayout.LayoutParams params;
    private GridView mGridTag;
    private RelativeLayout mAdd_photo, mTagLy;
    private ImageView mIv_add;
    private MyPopWindow selectphoto_popupWindow, select_tag_popupwindow;
    private Button btn_tag_cancel, btn_tag_ok;

    private ProgressDialog mProgressDialog;

    public static final int NO_SDCARD = -1;
    public static final int CAMERA_PHOTO_WITH_DATA = 1;// 拍照
    public static final int GALLERY_PHOTO_WITH_DATA = 2;// 图库
    public static final int REMOVE_FACE = 4;// 去掉大表情

    private ImageLoader imageLoader;

    private String mFaceIconUri;
    private ArrayList<String> tagsList;
    private ArrayList<String> mPhotoList = new ArrayList<>();
    private HashMap<String, String> map = new HashMap<>();//图片上传结果用
    private HashMap<String, String> failed_map = new HashMap<>();//图片上传结果用

    private int wowoId, oldCount = 0;

    private String title_str;
    private final int  NULL = 0, ERROR = -1, CORRECT = 1;
    private int title_state = NULL;
    private boolean IS_POSTING = false;
    /**发帖时用到的表情对象*/
    private Emoji emoji;
    Dialog dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_post_layout);

        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        initTopBar(getString(R.string.wowo_post));
        initLeftBtn(true, R.drawable.back_gray);
        initRightBtn(true, getString(R.string.wowo_post));

        imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited())
            imageLoader.init(BitmapUtil.getImageLoaderConfiguration());

        if(getIntent() != null) {
            wowoId = getIntent().getIntExtra(KEY_WO_ID, 0);
//            wowoId = 46;
            if(wowoId <= 0)
                finish();
        }

        MyLog.i("wowoid= " + wowoId);
        Tools.clearFastSend();
        initView();
        buildListForTagAdapter();
    }



    private void initView() {
        kv_bar = (XhsEmoticonsKeyBoardBar) findViewById(R.id.kv_bar);

        mTitle = (EditText) findViewById(R.id.title);
        mContent = (EditText) findViewById(R.id.content);
        mTagLy = (RelativeLayout) findViewById(R.id.rl_tag);
        mTv_tag = (TextView) findViewById(R.id.tv_tag);

        mLy_photo = (LinearLayout) findViewById(R.id.ly_photo);

        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                title_str = editable.toString();
                if (title_str.length() == 0) {
                    title_state = NULL;
                } else if (StringUtil.isNullOrEmpty(title_str.replaceAll(" ", " ").replaceAll(" ", "").replaceAll("\n", ""))) {
                    title_state = ERROR;
                } else {
                    title_state = CORRECT;
                }
            }
        });

        mTagLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupTagWindow();
            }
        });

        initPhotoWindow();
        initTagWindow();

        mAdd_photo = (RelativeLayout) inflater.inflate(R.layout.post_photo_add, null);
        mIv_add = (ImageView) mAdd_photo.findViewById(R.id.iv_add);
        mIv_add.setOnClickListener(addListener);

        params = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.photo_add_w),getResources().getDimensionPixelSize(R.dimen.photo_add_h));
        params.rightMargin = 10;

        mLy_photo.addView(mAdd_photo, params);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                kv_bar.setBuilder(EmoticonsUtils.getBuilder(PostActivity.this));
            }
        });

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

        // 添加大咖秀入口按钮
        /*
        View dkxBtnView = inflater.inflate(R.layout.xhs_view_toolbtn_left_simple,null);
        dkxBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kv_bar.show(1);// show dakaxiu
            }
        });
        kv_bar.addFixedView(dkxBtnView, false);

        View dakaxiu = inflater.inflate(R.layout.dakaxiu_layout, null);
        kv_bar.add(dakaxiu);

        // 加号中图片和拍照用的
        GridView gv_dkxs = (GridView)dakaxiu.findViewById(R.id.gv_dkxs);
        ArrayList<AppBean> mDkxBeanList = new ArrayList<AppBean>();
        AppBean appBean = new AppBean();
        appBean.setId(1);
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
        kv_bar.setMultimediaVisibility(false);
        kv_bar.setBtnSoftKeyVisibility(View.VISIBLE);
        kv_bar.setSentVisibility(View.GONE);
        kv_bar.setInputVisibility(View.INVISIBLE);

//        kv_bar.getEmoticonsToolBarView().addOnToolBarItemClickListener(new EmoticonsToolBarView.OnToolBarItemClickListener() {
//            @Override
//            public void onToolBarItemClick(int position) {
//                if (position == kv_bar.getTooBtnSize() - 1) {
//                    // 点击下载表情按钮
//                    handler.sendEmptyMessage(0x77);
//                }
//            }
//        });

        // 初始设置发送按钮不可用
        kv_bar.setSendBtnSendable(false);

        kv_bar.getEmoticonsPageView().addIViewListener(new IView() {
            @Override
            public void onItemClick(EmoticonBean bean) {
                if (bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
                    Bitmap bmm = imageLoader.loadImageSync(bean.getIconUri(), BitmapUtil.getImageLoaderOption());
                    if (bmm == null) {
                        bmm = BitmapFactory.decodeFile(bean.getIconUri());
                        if (bmm == null) {
                        } else {
                            mFaceIconUri = bean.getGifUri();
                            kv_bar.getBtn_emj().setImageBitmap(bmm);
                            kv_bar.setFaceVisibility(false);
                        }
                    } else {
                        mFaceIconUri = bean.getGifUri();
                        kv_bar.getBtn_emj().setImageBitmap(bmm);
                        kv_bar.setFaceVisibility(false);
                    }

                    emoji = new Emoji();
                    emoji.setId(Integer.parseInt(bean.getId()));
                    emoji.setParentid(bean.getParentId());
                    emoji.setType(0);
                }
            }

            @Override
            public boolean onItemLongClick(int position, View converView, EmoticonBean bean) {
                int[] location = new int[2];
                // 获取当前view在屏幕中的绝对位置,location[0]表示view的x坐标值,location[1]表示view的坐标值
                converView.getLocationOnScreen(location);

                GifDialog myDialog = new GifDialog(PostActivity.this, R.style.MyGifDialogStyle, bean.getGifUri());
                WindowManager.LayoutParams params = myDialog.getWindow().getAttributes();
                params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
//                params.x = location[0];
                params.y = DisplayUtil.getDisplayHeight(PostActivity.this) - location[1];
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
            }

            @Override
            public void OnEmjBtnClick() {
                if (!StringUtil.isNullOrEmpty(mFaceIconUri)) {
                    Intent intent = new Intent(PostActivity.this, DeleteFaceActivity.class);
                    intent.putExtra(DeleteFaceActivity.KEY_PATH, mFaceIconUri);
                    startActivityForResult(intent, REMOVE_FACE);
                }
            }

            @Override
            public void OnPhotoBtnClick() {
            }

            @Override
            public void OnSendBtnClick(String msg) {
            }

            @Override
            public void OnVoiceBtnClick() {
            }

            @Override
            public void OnMultimediaBtnClick() {
            }
        });

        showHelp();
    }

    private void showHelp() {
        boolean isFirst = WowoPreference.getInstance().isFirstPost();
        if(isFirst) {
            try {
                Context context = PostActivity.this;
                dia = new Dialog(context, R.style.edit_AlertDialog_style);
                dia.setContentView(R.layout.new_post_help_dialog);

                View.OnClickListener dismiss = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WowoPreference.getInstance().setFirstPost(false);
                        dia.dismiss();
                    }
                };
                dia.findViewById(R.id.dialog_rl).setOnClickListener(dismiss);
                dia.findViewById(R.id.help_post_img1).setOnClickListener(dismiss);
                dia.findViewById(R.id.help_post_img2).setOnClickListener(dismiss);
                dia.findViewById(R.id.help_post_img3).setOnClickListener(dismiss);
                dia.findViewById(R.id.help_post_img4).setOnClickListener(dismiss);

                dia.show();

                dia.setCanceledOnTouchOutside(true); // Sets whether this dialog is
                // canceled when touched outside
                // the window's bounds.
                dia.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        WowoPreference.getInstance().setFirstPost(false);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case NO_SDCARD:
                ToastUtils.toast(this, R.string.label_no_sdcard);
                break;
            case REMOVE_FACE:
                if (data != null) {
                    boolean deleted = data.getBooleanExtra(DeleteFaceActivity.KEY_DELETE, false);
                    if (deleted) {
                        kv_bar.getBtn_emj().setImageResource(com.keyboard.view.R.drawable.btn_emoj_grey);
                        kv_bar.setFaceVisibility(true);
                        kv_bar.setSendBtnSendable(false);
                        mFaceIconUri = "";
                        emoji = null;
                    }
                }
                break;
            case CAMERA_PHOTO_WITH_DATA:
                switch (resultCode) {
                    case Activity.RESULT_OK://照相完成点击确定
                        if (!AppTools.existsSDCARD()) {
                            MyLog.v("SD card is not avaiable/writeable right now.");
                            return;
                        }
//                        Bundle bundle = data.getExtras();
//                        Bitmap bitmap = (Bitmap) bundle.get("data");
//                        Bitmap photo = data.getParcelableExtra("data");
//                如果不需要做剪切处理就可以直接使用图片了，比如输出到ImageView上
//                        kv_bar.getBtn_photo().setImageBitmap(photo);
//                        kv_bar.setMultimediaVisibility(false);
                        kv_bar.setSendBtnSendable(true);

                        String fileName = capturePath;
                        if(!StringUtil.isNullOrEmpty(fileName)) {
                            if (mPhotoList == null) {
                                mPhotoList = new ArrayList<>();
                            }
                            mPhotoList.add(fileName);
                            for (int i = 0; i < mPhotoList.size(); i++) {
                                MyLog.i(i + " 拍照返回 : " + mPhotoList.get(i));
                            }
                            data2View(1);
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case GALLERY_PHOTO_WITH_DATA:
                if(data != null) {
                    boolean isChange = data.getBooleanExtra(SelectPhotoActivity.KEY_IS_CHANGED, false);
                    if(isChange) {
                        mPhotoList = data.getStringArrayListExtra(SelectPhotoActivity.KEY_SELECTED);
                        if(mPhotoList == null) {
                            mPhotoList = new ArrayList<>();
                        }
                        for (int i = 0; i < mPhotoList.size(); i++) {
                            MyLog.i((i+1)+" :选择照片 " + mPhotoList.get(i));
                        }
                        data2View(2);
                    }
                }
                break;
        }
    }

    /**
     * @param from 1:Camera  2:Gallery*/
    private void data2View(int from) {
        if(from == 1) {
            // 如果达到最大张数,需要先删除加号View
            if(mPhotoList.size()==WowoContants.WO_POST_MAX_SIZE) {
                mLy_photo.removeViewAt(oldCount);
            }
            View v = inflater.inflate(R.layout.post_photo_item, null);
            mLy_photo.addView(v, oldCount, params);

            View c = mLy_photo.getChildAt(oldCount);
            ImageView iv = (ImageView) c.findViewById(R.id.iv_photo);
            ImageView ivd = (ImageView) c.findViewById(R.id.iv_del);
            ivd.setOnClickListener(new ItemDelClickListener(mPhotoList.get(oldCount)));
//            com.moinapp.wuliao.modules.wowo.imageloader.ImageLoader.getInstance(3, com.moinapp.wuliao.modules.wowo.imageloader.ImageLoader.Type.LIFO)
//                    .loadImage("file://" + mPhotoList.get(oldCount), iv);
            imageLoader.displayImage("file://" + mPhotoList.get(oldCount), iv, BitmapUtil.getImageLoaderOption());

        } else if(from ==2) {
            // 多选了几张图片
            if (oldCount < mPhotoList.size()) {
                // 如果达到最大张数,需要先删除加号View
                if (mPhotoList.size() == WowoContants.WO_POST_MAX_SIZE) {
                    mLy_photo.removeViewAt(oldCount);
                }
                for (int i = oldCount; i < mPhotoList.size(); i++) {
                    View v = inflater.inflate(R.layout.post_photo_item, null);
                    mLy_photo.addView(v, i, params);
                }
            }
            // 少选了几张图片
            else if (oldCount > mPhotoList.size()) {
                mLy_photo.removeViews(mPhotoList.size(), oldCount - mPhotoList.size());

                // 如果以前达到最大张数,需要添加加号View
                if (oldCount == WowoContants.WO_POST_MAX_SIZE) {
                    mLy_photo.addView(mAdd_photo, params);
                }
            }

            for (int i = 0; i < mPhotoList.size(); i++) {
                View c = mLy_photo.getChildAt(i);
                ImageView iv = (ImageView) c.findViewById(R.id.iv_photo);
                ImageView ivd = (ImageView) c.findViewById(R.id.iv_del);
                ivd.setOnClickListener(new ItemDelClickListener(mPhotoList.get(i)));
                imageLoader.displayImage("file://" + mPhotoList.get(i), iv, BitmapUtil.getImageLoaderOption());
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x119:
                    mProgressDialog.dismiss();
                    IS_POSTING = false;
                    ToastUtils.toast(PostActivity.this, R.string.no_network);
                    break;
                case 0x120:
                    mProgressDialog.dismiss();
                    IS_POSTING = false;
                    ToastUtils.toast(PostActivity.this, R.string.connection_failed);
                    break;
                // 选择删除图片
                case 0x800:
                    int pos = -1;
                    for (int i = 0; i < mPhotoList.size(); i++) {
                        if(msg.obj.toString().equals(mPhotoList.get(i))) {
                            pos = i;
                            break;
                        }
                    }
                    if(pos >= 0) {
                        mLy_photo.removeViewAt(pos);
                        map.remove(mPhotoList.get(pos));
                        mPhotoList.remove(pos);
                        deleteTmpCaptures(msg.obj.toString());

                        // 如果是删除了最后一张图，那么要将加号加回来
                        if(mPhotoList.size()+1 == WowoContants.WO_POST_MAX_SIZE) {
                            mLy_photo.addView(mAdd_photo, params);
                        }
                    }

                    break;
                // 图片上传结果
                case 0x55:
                    if(msg.getData() != null) {
                        String url = msg.getData().getString("url");
                        String picid = msg.getData().getString("picid");
                        if(StringUtil.isNullOrEmpty(picid)) {
                            MyLog.w("图片上传失败啦啦啦 " + url);
                            failed_map.put(url, picid);
                        } else {
                            MyLog.i("图片上传成功 " + url + ", picid="+picid);
                            map.put(url, picid);
                        }

                        int size = map.keySet().size() + failed_map.keySet().size();

                        MyLog.i("图片上传成功个数 " + map.keySet().size() + ", 图片上传失败个数="+failed_map.keySet().size());
                        if(size < mPhotoList.size()) {
                            MyLog.i("还没到总的个数 " + mPhotoList.size());
                            return;
                        }
                        if(failed_map.keySet().size() > 0) {
                            retry();
                        } else if(map.keySet().size() == mPhotoList.size()) {
                            MyLog.i("图片全部全部全部全部全部全部 都上传成功");
                            ArrayList<String> picIdList = new ArrayList<>();

                            for (int i = 0; i < mPhotoList.size(); i++) {
                                MyLog.i((i+1)+" : " + mPhotoList.get(i) + " / " + map.get(mPhotoList.get(i)));
                                picIdList.add(map.get(mPhotoList.get(i)));
                            }

                            WowoManager.getInstance().newPost(wowoId, StringUtil.filter(title_str), StringUtil.filter(mContent.getText().toString()), mTv_tag.getText().toString(),
                                    picIdList, emoji, new IPostListener() {
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
                                            MyLog.i("发帖成功 postid= " + postid);
                                            handler.sendEmptyMessage(0x66);

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
                                            MyLog.i("发帖失败 无网络");
                                        }

                                        @Override
                                        public void onErr(Object object) {
                                            MyLog.i("发帖失败");
                                        }
                                    });
                        }
                    }
                    break;
                // 发帖成功
                case 0x66:
                    mProgressDialog.dismiss();
                    isRetry = false;
                    ToastUtils.toast(PostActivity.this, R.string.post_success);
                    for (int i = 0; i < mPhotoList.size(); i++) {
                        deleteTmpCaptures(mPhotoList.get(i));
                    }

                    MyLog.i("跳到窝窝帖子列表去 wowoId=" + wowoId);
                    Bundle bundle = new Bundle();
                    bundle.putInt(WoPostListActivity.WO_ID, wowoId);
                    bundle.putInt("column",0);
                    AppTools.toIntent(PostActivity.this, bundle, WoPostListActivity.class);
                    break;
                // 去下载表情
                case 0x77:
                    // if first time click download moinEmj, please dialog to notice user only once.
                    if(!WowoPreference.getInstance().isVisitEmojiList()) {
                        WowoPreference.getInstance().setVisitEmojiList(true);
                        ToastUtils.toast(PostActivity.this, "下载新的大表情去咯~");
                        final AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                        builder.setMessage("下载新的表情");
                        builder.setTitle("提示");
                        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                AppTools.toIntent(PostActivity.this, EmojiResourceActivity.class);
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
                        AppTools.toIntent(PostActivity.this, EmojiResourceActivity.class);
                    }
                    break;
                default:
                    break;
            }

        }
    };

    boolean isRetry = false;
    private void retry() {
        MyLog.i("retry ");
        if (!NetworkUtils.isConnected(PostActivity.this)) {
            handler.sendEmptyMessage(0x119);
            return;
        }
        if(isRetry) {
            handler.sendEmptyMessage(0x120);
            return;
        }
        isRetry = true;
        int size = failed_map.keySet().size();
        MyLog.i("retry GOGOGO  size："+size);
        ArrayList<String> list = new ArrayList<>(size);
        for(Map.Entry<String, String> entry:failed_map.entrySet()) {
            list.add(entry.getKey());
        }
        failed_map.clear();
        ExecutorService limitedTaskExecutor = Executors.newFixedThreadPool(list.size());
        for (int i = 0; i < size; i++) {
            MyLog.i("重试  上传图片任务 " + list.get(i));
            UploadImageTask task = new UploadImageTask();
            task.executeOnExecutor(limitedTaskExecutor, list.get(i));
        }

    }

    private void deleteTmpCaptures(String path) {
        if(StringUtil.isNullOrEmpty(path))
            return;

        if (path.contains(BitmapUtil.POST_CAPTURE_PREFIX)) {
            new File(path).delete();
            MyLog.i("删除临时图片 success =" + path);
        }
    }


    /**
     *  选择图片
     */
    public void initPhotoWindow() {
        View popupWindow_view = this.getLayoutInflater().inflate(R.layout.alter_avatar, null);
        popupWindow_view.setPadding(0, 0, 0, CommonsPreference.getInstance().getVirtualKeyboardHeight());
        ((TextView) popupWindow_view.findViewById(R.id.title)).setText(R.string.select_photo);
        popupWindow_view.findViewById(R.id.emoji_ly).setVisibility(View.GONE);
        tv_camera = (TextView) popupWindow_view.findViewById(R.id.alter_avatar_camera);
        tv_gallery = (TextView) popupWindow_view.findViewById(R.id.alter_avatar_album);
        tv_cancel = (TextView) popupWindow_view.findViewById(R.id.alter_avatar_cancel);
        tv_camera.setOnClickListener(this);
        tv_gallery.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        selectphoto_popupWindow = new MyPopWindow(this, popupWindow_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    /**
     *  选择标签
     */
    public void initTagWindow() {
        View popupWindow_view = this.getLayoutInflater().inflate(R.layout.select_tag_popwindow, null);
        btn_tag_ok = (Button) popupWindow_view.findViewById(R.id.btn_tag_ok);
        btn_tag_cancel = (Button) popupWindow_view.findViewById(R.id.btn_tag_no);
        mGridTag = (GridView) popupWindow_view.findViewById(R.id.grid_tag);
        btn_tag_ok.setOnClickListener(this);
        btn_tag_cancel.setOnClickListener(this);
        select_tag_popupwindow = new MyPopWindow(this, popupWindow_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // photo
            case R.id.alter_avatar_camera:
                selectphoto_popupWindow.dismiss();
                doCamera();
                break;
            case R.id.alter_avatar_album:
                selectphoto_popupWindow.dismiss();
                SelectPhotos();
                break;
            case R.id.alter_avatar_cancel:
                selectphoto_popupWindow.dismiss();
                break;
            // tag
            case R.id.btn_tag_ok:
                select_tag_popupwindow.dismiss();
                break;
            case R.id.btn_tag_no:
                select_tag_popupwindow.dismiss();
                break;
        }
    }

    private void popupTagWindow() {
        TagAdapter adapter = new TagAdapter();
        mGridTag.setAdapter(adapter);

        int selection = -1;
        if(!TextUtils.isEmpty(mTv_tag.getText().toString())) {
            for (int i = 0; i < tagsList.size(); i++) {
                if (mTv_tag.getText().toString().equals(tagsList.get(i))) {
                    selection = i;
                    break;
                }
            }
        }
        if(selection >= 0) {
            MyLog.i("select tag:" + tagsList.get(selection));
            adapter.setSelectItem(selection);
            adapter.notifyDataSetInvalidated();
        }

        mGridTag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mTv_tag.setVisibility(View.VISIBLE);
                mTv_tag.setText(tagsList.get(i));
                select_tag_popupwindow.dismiss();
            }
        });
        select_tag_popupwindow.showButtom();
    }

    private void SelectPhotos() {
        Intent intent = new Intent(PostActivity.this, SelectPhotoActivity.class);
        intent.putStringArrayListExtra(SelectPhotoActivity.KEY_SELECTED, mPhotoList);
        setOldCount();
        startActivityForResult(intent, GALLERY_PHOTO_WITH_DATA);
    }

    private void setOldCount() {
        oldCount = mPhotoList.size();
    }

    // 拍照
    String capturePath;
    private void doCamera() {
        setOldCount();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        capturePath = BitmapUtil.getPostCapturePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(capturePath)));
        startActivityForResult(intent, CAMERA_PHOTO_WITH_DATA);
    }

    private View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectphoto_popupWindow.showButtom();
        }
    };

    @Override
    protected void rightBtnHandle() {
        super.rightBtnHandle();

        if (IS_POSTING) {
            MyLog.w(StringUtil.formatDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss S") + " 正在发帖ing");
            ToastUtils.toast(PostActivity.this, R.string.posting_toast);
            return;
        }

        if(Tools.isFastDoubleSend()) {
            MyLog.w(StringUtil.formatDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss S") + getString(R.string.click_too_fast));
            ToastUtils.toast(PostActivity.this, R.string.click_too_fast);
            return;
        } else {
            if(title_state != CORRECT) {
                ToastUtils.toast(PostActivity.this, R.string.post_title_hint);
                return;
            }
            if(TextUtils.isEmpty(mTv_tag.getText().toString())) {
                ToastUtils.toast(PostActivity.this, R.string.post_tag_hint);
                return;
            }
            if(StringUtil.isNullOrEmpty(mContent.getText().toString().replaceAll(" ", " ").replaceAll(" ", "").replaceAll("\n", "")) &&
                    mPhotoList.size() == 0 &&
                    emoji == null) {
                ToastUtils.toast(PostActivity.this, R.string.post_content_hint);
                return;
            }

            IS_POSTING = true;

            mProgressDialog = ProgressDialog.show(this, null, "正在发帖...");
            mProgressDialog.setCanceledOnTouchOutside(true);


            // TODO 上传图片时要压缩图片到5MB以内
            if(mPhotoList.size() > 0) {
                StringBuffer sb = new StringBuffer();
                boolean has = false;
                for (int i = 0; i < mPhotoList.size(); i++) {
                    File file = new File(mPhotoList.get(i));
                    if(/*file.exists() && file.isFile() && */file.length() >= WowoContants.WO_IMAGE_MAX_MB ||
                            FileUtil.getExtensionName(mPhotoList.get(i)).equals("webp")) {
                        MyLog.i(mPhotoList.get(i) + " 's size(KB)=" + file.length()/1024);
                        if(!has) {
                            sb.append(i+1);
                            has = true;
                        } else {
                            sb.append("," + (i + 1));
                        }
                    }
                }

                if(has) {
                    ToastUtils.toast(PostActivity.this, String.format(getResources().getString(R.string.picture_too_big), sb.toString()));

                    String[] bigger = sb.toString().split(",");
                    if (bigger.length > 0) {
                        // TODO 用最大宽度 1080*1920 这样在大小分辨率手机上都能保证一定的清晰度   720*1280
                        // 80 是压缩质量
                        int quality = 80;
                        for (int i = 0; i < bigger.length; i++) {
                            int position = i;
                            try {
                                position = Integer.parseInt(bigger[i]) - 1;
                            } catch (NumberFormatException e) {
                                break;
                            }
                            String path = mPhotoList.get(position);
                            String path_new = BitmapUtil.getPostCompressPath(position);
                            Bitmap bitmap =
                                    com.moinapp.wuliao.modules.wowo.imageloader.ImageLoader.getInstance()
                                            .decodeSampledBitmapFromResource(path, WowoContants.WO_IMAGE_MAX_WIDTH, WowoContants.WO_IMAGE_MAX_HEIGHT);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                            MyLog.i("按宽度缩小后图片是" + baos.toByteArray().length / 1024 + " KB, 宽高：" + bitmap.getWidth() + "*" + bitmap.getHeight());
                            if (baos.toByteArray().length < WowoContants.WO_IMAGE_MAX_MB) {
                                boolean ret = BitmapUtil.saveBitmap2file(bitmap, path_new, Bitmap.CompressFormat.JPEG, quality);
                                if (ret) {
                                    mPhotoList.set(position, path_new);
                                    MyLog.i(" decode图片OK: " + path_new);
                                }
                            } else {
                                MyLog.i(" 压缩图片 ");
                                Bitmap compress = BitmapUtil.compressImage(bitmap, 90, WowoContants.WO_IMAGE_MAX_KB);
                                boolean ret = BitmapUtil.saveBitmap2file(compress, path_new, Bitmap.CompressFormat.JPEG, quality);
                                if (ret) {
                                    mPhotoList.set(position, path_new);
                                    MyLog.i(" 压缩图片OK: " + path_new);
                                }
                            }
                        }
                    }
                }
            }

            if(mPhotoList.size() > 0) {
                /** 每次执行限定个数大小为5个任务的线程池 */
                ExecutorService limitedTaskExecutor = Executors.newFixedThreadPool(5);

                map.clear();
                failed_map.clear();
                for (int i = 0; i < mPhotoList.size(); i++) {
//                    MyLog.i("执行上传图片任务 " + i);
                    if(!NetworkUtils.isConnected(PostActivity.this)) {
                        ToastUtils.toast(PostActivity.this, R.string.no_network);
                    } else {
                        UploadImageTask task = new UploadImageTask();
                        task.executeOnExecutor(limitedTaskExecutor, mPhotoList.get(i));
                    }
                }
            } else {
                WowoManager.getInstance().newPost(wowoId, StringUtil.filter(title_str), StringUtil.filter(mContent.getText().toString()), mTv_tag.getText().toString(),
                        null, emoji, new IPostListener() {
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
                                MyLog.i("无自定义图片发帖成功 postid="+postid);
                                IS_POSTING = false;
                                handler.sendEmptyMessage(0x66);
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
                                IS_POSTING = false;
                                MyLog.i("无自定义图片发帖失败 无网络");
                                handler.sendEmptyMessage(0x119);
                            }

                            @Override
                            public void onErr(Object object) {
                                IS_POSTING = false;
                                MyLog.i("无自定义图片发帖失败 " + object);
                                handler.sendEmptyMessage(0x120);
                            }
                        });
            }
        }
    }


    private class ItemDelClickListener implements View.OnClickListener {
        private String path;

        public ItemDelClickListener(String i) {
            this.path = i;
        }

        @Override
        public void onClick(View view) {
            Message message = Message.obtain(handler, 0x800);
            message.obj = path;
            message.sendToTarget();
        }
    }

    /**
     * 获取标签list
     * */
    private void buildListForTagAdapter() {
        if(tagsList == null) {
            tagsList = new ArrayList<>();
        }
        tagsList.clear();

        WowoManager.getInstance().getWoTag(wowoId, new WoTagListener() {
            @Override
            public void onGetWoTagSucc(List<String> tags) {
                tagsList.addAll(tags);
            }

            @Override
            public void onNoNetwork() {
                handler.sendEmptyMessage(0x119);
            }

            @Override
            public void onErr(Object object) {

            }
        });
    }

    private class UploadImageTask extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(final String... strings) {
            try {
                WowoManager.getInstance().uploadImage(strings[0], new IPostListener() {
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
                        onUploadCallback(strings[0], 1, picid);
                    }

                    @Override
                    public void onNoNetwork() {
                        MyLog.i("doInBackground onNoNetwork" + ", " + strings[0]);
                        onUploadCallback(strings[0], -1, "");
                    }

                    @Override
                    public void onErr(Object object) {
                        MyLog.i("doInBackground onErr " + object + ", " + strings[0]);
                        onUploadCallback(strings[0], 0, "");
                    }
                });
            } catch (Exception e) {
                MyLog.e(e);
                e.printStackTrace();
                onUploadCallback(strings[0], 0, "");
            } finally {
                return null;
            }

        }

        private void onUploadCallback(String url, int type, String picId) {
            Message msg = new Message();
            msg.what = 0x55;

            Bundle b = new Bundle();
            b.putString("url", url);
            b.putString("picid", picId);
            b.putInt("type", type);
            msg.setData(b);
            handler.sendMessage(msg);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private class TagAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return tagsList.size();
        }

        @Override
        public Object getItem(int i) {
            return tagsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(PostActivity.this);
            view = inflater.inflate(R.layout.tag_item, null).findViewById(R.id.tag);
            TextView tv = (TextView) view.findViewById(R.id.tag);
            tv.setText(tagsList.get(i));
            if(selectItem == i) {
                tv.setTextColor(getResources().getColor(R.color.common_text_main));
                tv.setBackground(getResources().getDrawable(R.drawable.tag_btn_bg));
            }
            return view;
        }

        public void setSelectItem(int selectItem) {
            this.selectItem = selectItem;
        }

        private int selectItem = -1;
    }
}
