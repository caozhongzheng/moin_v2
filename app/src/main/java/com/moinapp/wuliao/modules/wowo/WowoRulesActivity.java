package com.moinapp.wuliao.modules.wowo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.login.LoginActivity;
import com.moinapp.wuliao.modules.login.LoginConstants;
import com.moinapp.wuliao.modules.login.LoginSuccJump;
import com.moinapp.wuliao.modules.wowo.model.CommentInfo;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;
import com.moinapp.wuliao.modules.wowo.model.WowoInfo;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by liujiancheng on 15/6/18.
 */
public class WowoRulesActivity extends BaseActivity implements View.OnClickListener {
    private static ILogger MyLog = LoggerFactory.getLogger(WowoRulesActivity.class.getSimpleName());
    private static final String WOWO_INFO = "wowo_info";
    private static final String WOWO_STATUS = "wowo_satus";

    private ImageView woCover;
    private TextView woName;
    private TextView commentCount;
    private TextView postNumber;
    private LinearLayout tags;
    private TextView woOldDesc;
    private TextView woIntro;
    private TextView woRule;
    private ImageView woAttention;//关注窝的图标

    private WowoInfo mWoIno;
    private boolean mWoStatus; //0,未关注的窝 1，已经关注的窝
    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wowo_rule_intro);
        mWoIno = getWoInfo();
        if (mWoIno == null) {
            finish();
            return;
        }
        mWoStatus = mWoIno.isFavorite();

        EventBus.getDefault().register(this);
        //初始化图片缓存
        mImageLoader = ImageLoader.getInstance();
        if(!mImageLoader.isInited()) {
            mImageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }

        findViews();
    }

    private void findViews() {
        initTopBar(getString(R.string.wo_intro));
        initLeftBtn(true, R.drawable.back_gray);

        woCover = (ImageView) findViewById(R.id.wo_cover);
        woName = (TextView) findViewById(R.id.wo_name);
        commentCount = (TextView) findViewById(R.id.comment_count);
        postNumber = (TextView) findViewById(R.id.post_number);
        tags = (LinearLayout) findViewById(R.id.wo_tags_ll);
        woOldDesc = (TextView) findViewById(R.id.wo_desc);
        woOldDesc.setVisibility(View.GONE);
        woIntro = (TextView) findViewById(R.id.wo_intro);
        woRule = (TextView) findViewById(R.id.wo_rules);
        woAttention = (ImageView) findViewById(R.id.image_attention);
        woAttention.setVisibility(View.VISIBLE);

        if (mWoIno.getIcon() != null)
            mImageLoader.displayImage(mWoIno.getIcon().getUri(), woCover, BitmapUtil.getImageLoaderOption());
        woName.setText(mWoIno.getName());
        commentCount.setText("关注：" + String.valueOf(mWoIno.getUserCount()));
        postNumber.setText("帖子：" + String.valueOf(mWoIno.getPostCount()));

        String tag = getTags(mWoIno.getTags());
        tags.removeAllViews();
        if(!StringUtil.isNullOrEmpty(tag)) {
            String[] tagarr = tag.split(",");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            params.rightMargin = getResources().getDimensionPixelSize(R.dimen.icon_margin);
            for (int j = 0; j < tagarr.length; j++) {
                if(!StringUtil.isNullOrEmpty(tagarr[j])) {
                    TextView tv_tag = new TextView(WowoRulesActivity.this);
                    tv_tag.setText(tagarr[j]);
                    tv_tag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    tv_tag.setTextColor(getResources().getColor(R.color.common_text_grey));
                    tv_tag.setBackground(getResources().getDrawable(R.drawable.tag_btn_grey_bg));
                    tv_tag.setPadding(5, 1, 5, 1);
                    tags.addView(tv_tag, params);
                }
            }
        }

        woIntro.setText(mWoIno.getIntro());
        woRule.setText(mWoIno.getRuleIntro());

        if (mWoStatus) {
            woAttention.setImageDrawable(getResources().getDrawable(R.drawable.btn_remove));
        } else {
            woAttention.setImageDrawable(getResources().getDrawable(R.drawable.btn_add));
        }

        woAttention.setOnClickListener(this);
    }

    private void updateViews() {
        if (mWoStatus) {
            mWoStatus = false;
            woAttention.setImageDrawable(getResources().getDrawable(R.drawable.btn_add));
        } else {
            mWoStatus = true;
            woAttention.setImageDrawable(getResources().getDrawable(R.drawable.btn_remove));
        }
    }

    private WowoInfo getWoInfo() {
        Intent intent = getIntent();
        if (intent == null)
            return null;

        return (WowoInfo)intent.getSerializableExtra(WOWO_INFO);
    }

//    private int getWoStatus() {
//        return getIntent().getIntExtra(WOWO_STATUS, 0);
//    }

    private void attentionWo() {
        final int action = mWoStatus ? 0:1;
        WowoManager.getInstance().subscribeWo(mWoIno.getId(), action, new IWoListener() {
            @Override
            public void onErr(Object object) {

            }

            @Override
            public void onNoNetwork() {

            }

            @Override
            public void onGetWowoSucc(List<WowoInfo> wowoInfoList) {

            }

            @Override
            public void onSubscribeResult(int result) {
                MyLog.i("onSubscribeResult succ, result" + result);
                if (result == 1) {
                    EventBus.getDefault().post(new AttentionEvent(mWoIno, action));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateViews();
                        }
                    });
                }
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

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.image_attention:
                if(!ClientInfo.isUserLogin()) {
                    if(!ClientInfo.isUserLogin()) {
                        //进入登录界面
                        Bundle b = new Bundle();
                        b.putBoolean(LoginActivity.KEY_NEED_RETURN, true);
                        b.putInt(LoginActivity.KEY_FROM, LoginConstants.WOWO_RULE);
                        AppTools.toIntent(WowoRulesActivity.this, b, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                } else {
                    attentionWo();
                }
                break;
        }
    }

    private void getWowoInfo() {
        WowoManager.getInstance().getWoPostList(mWoIno.getId(), null, 0, new IPostListener() {
            @Override
            public void onGetPostListSucc(List<PostInfo> postInfoList, int column) {

            }

            @Override
            public void onGetWoPostListSucc(List<PostInfo> postInfoList, WowoInfo woinfo, int column) {
                MyLog.i("WowoRulesActivity:onGetWoPostListSucc....");
                if (woinfo != null) {
                    mWoStatus = woinfo.isFavorite();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mWoStatus) {
                                woAttention.setImageDrawable(getResources().getDrawable(R.drawable.btn_remove));
                            } else {
                                woAttention.setImageDrawable(getResources().getDrawable(R.drawable.btn_add));
                            }
                        }
                    });
                }
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

            }

            @Override
            public void onErr(Object object) {

            }
        });
    }

    public void onEvent(LoginSuccJump.LoginSuccessEvent event) {
        MyLog.i("onEvent:LoginSuccessEvent");
        getWowoInfo();
    }

    @Override
    public void onBackPressed() {
            finish();
    }

    public static class AttentionEvent {
        private WowoInfo wowoInfo;
        private int status;//0,未关注 1，已关注
        AttentionEvent(WowoInfo wowoInfo, int status) {
            this.wowoInfo = wowoInfo;
            this.status = status;
        }

        public WowoInfo getWo() {
            return wowoInfo;
        }

        public void setWo(WowoInfo wowoInfo) {
            this.wowoInfo = wowoInfo;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}