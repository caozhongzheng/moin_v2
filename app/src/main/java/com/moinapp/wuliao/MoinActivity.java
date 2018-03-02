package com.moinapp.wuliao;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.commons.info.MobileInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moinsocialcenter.MoinSocialConstants;
import com.moinapp.wuliao.commons.net.UpdateListener;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.commons.ui.BaseFragmentActivity;
import com.moinapp.wuliao.modules.cosplay.CosplayManager;
import com.moinapp.wuliao.modules.update.UpdateManager;
import com.moinapp.wuliao.modules.update.model.App;
import com.moinapp.wuliao.utils.ToastUtils;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

/**
 * Created by moying on 15/5/13.
 */
public class MoinActivity extends BaseFragmentActivity implements View.OnClickListener {

    private static final ILogger MyLog = LoggerFactory.getLogger("");
    /** bundle参数key，要显示的fragment */
    public static final String KEY_FRAGMENT_INDEX_TO_SHOW = "fragment_index";
    private Fragment[] fragments;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private int fragmentToShow;

    private RelativeLayout mMoinLayout;
    private RelativeLayout mWasaiLayout;
    private RelativeLayout mWowoLayout;
    private RelativeLayout mMineLayout;

    /*add by wufan cosplay*/
    private RelativeLayout mCosplayLayout;

    private RelativeLayout[] tabs = new RelativeLayout[TAB_NUM];


    private ImageView mMoin;
    private ImageView mWasai;
    private ImageView mCamera;
    private ImageView mWowo;
    private ImageView mMine;

    
    private TextView mTextCosplay;/*add by wufan,cosplay*/

    private TextView mTextMoin;
    private TextView mTextWasai;
    private TextView mTextWowo;
    private TextView mTextMine;

    private static final int TAB_MOIN = 0;
    private static final int TAB_WASAI = 1;
    private static final int TAB_WOWO = 2;
    private static final int TAB_MINE = 3;
    private static final int TAB_COSPLAY = 4;/*add by wufan,cosplay*/

    /*add by wufan,cosplay*/
    private static final int TAB_NUM = 5;
    int vh;
    TextView info;
    StringBuilder sb = new StringBuilder();

    private Context mContext;
    Dialog mUpdateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("MoinActivity onCreste");
        vh = super.getVirtualKeyHeight();
        MyLog.i("MoinActivity onCreste vh = " + vh);

//        ImageView imageView = new ImageView(MoinActivity.this);
//        imageView.setImageResource(R.drawable.splash);
//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
//        setContentView(imageView, params);
        mContext = this;
        setContentView(R.layout.moin_layout);

        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        MyLog.i("MoinActivity onNewIntent");
        super.onNewIntent(intent);
        this.setIntent(intent);
        setView();
    }
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        MyLog.i("arg0:" + arg0 + ";arg1:" +arg1);
        if (arg0 == 11) {
            finish();
        }

        //分享到微博需要的activity回调
        UMSocialService mController = UMServiceFactory.getUMSocialService(MoinSocialConstants.NAME_UMENG);
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(arg0);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(arg0, arg1, arg2);
        }
    }

    View root;
    private void initView() {
        root = findViewById(R.id.root);
//        root.setPadding(0,0,0,vh);
        info = (TextView) findViewById(R.id.info);

        splash = (ImageView) findViewById(R.id.splash);
        mMoinLayout = (RelativeLayout) findViewById(R.id.tab_moin);
        mWasaiLayout = (RelativeLayout) findViewById(R.id.tab_wasai);
        mWowoLayout = (RelativeLayout) findViewById(R.id.tab_wowo);
        mMineLayout = (RelativeLayout) findViewById(R.id.tab_mine);
        mCosplayLayout = (RelativeLayout)findViewById(R.id.tab_camera);

        mMoin = (ImageView) findViewById(R.id.image_moin);
        mWasai = (ImageView) findViewById(R.id.image_wasai);
        mCamera = (ImageView) findViewById(R.id.image_camera);
        mWowo = (ImageView) findViewById(R.id.image_wowo);
        mMine = (ImageView) findViewById(R.id.image_mine);

        mTextMoin = (TextView) findViewById(R.id.text_moin);
        mTextWasai = (TextView) findViewById(R.id.text_wasai);
        mTextWowo = (TextView) findViewById(R.id.text_wowo);
        mTextMine = (TextView) findViewById(R.id.text_mine);
        mTextCosplay = (TextView)findViewById(R.id.text_cosplay);

//        animatinoGone = AnimationUtils.loadAnimation(this,R.anim.alpha_gone); //动画效果
//        myAnimation_Alpha = AnimationUtils.loadAnimation(this,R.anim.alpha_action); //动画效果
//
//        splash = (LinearLayout) findViewById(R.id.splashscreen);
//        tv = (TextView) findViewById(R.id.info);
//        tv.setText("正在建立数据连接");
//        splash.startAnimation(myAnimation_Alpha);

//        Message msg = new Message();
//        msg.what = STOPSPLASH;
//        splashHandler.sendMessageDelayed(msg, SPLASHTIME);

        setView();


        //联网获取新版本信息
        UpdateManager.getInstance().checkUpdate(new UpdateListener() {
            @Override
            public void getUpdateSucc(final App app) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showUpdateDialog(app);
                    }
                });
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("MoinActivity: onError");
            }
        });
    }

    ImageView splash;
    int SPLASHTIME = 1500;
    int STOPSPLASH = 0x15;
    Handler splashHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == STOPSPLASH) {
                splash.setVisibility(View.GONE);
            }
        }
    };
    int mheightDifference = 0;
    int mresourceId = 0;
    int mRectHeihgt = 0;
    int mscreenHeight = 0;
    int count = 0;
    private void setVirtualKeyHeight() {
        if(!MobileInfo.isMeizu()) {
            root.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {
                            Rect r = new Rect();
                            root.getWindowVisibleDisplayFrame(r);

                            int screenHeight = root.getRootView()
                                    .getHeight();
                            int heightDifference = screenHeight
                                    - (r.bottom - r.top);
                            int resourceId = getResources()
                                    .getIdentifier("status_bar_height", "dimen", "android");
                            if (resourceId > 0) {
                                heightDifference -= getResources()
                                        .getDimensionPixelSize(resourceId);
                            }

                            if (heightDifference < 0) {
                                heightDifference = 0;
                            }

//                            MyLog.i("Virtual Keyboard Size: " + heightDifference);
                            mheightDifference = heightDifference;
                            mresourceId = resourceId;
                            mscreenHeight = screenHeight;
                            mRectHeihgt = r.bottom - r.top;

//                        sb.append("VK: " + heightDifference + " resourceId: " + resourceId + "\n");
//                        sb.append("screenHeight: " + screenHeight + " rect: " + (r.bottom - r.top) + "\n");
                            root.setPadding(0, 0, 0, heightDifference);
                            CommonsPreference.getInstance().setVirtualKeyboardHeight(heightDifference);
                            // boolean visible = heightDiff > screenHeight / 3;
//                            if(count > 2) {
//                                root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                            } else {
//                                count++;
//                            }
                        }
                    });
        }
    }

    private void setView() {
        if(getIntent() != null) {
            fragmentToShow = getIntent().getIntExtra(KEY_FRAGMENT_INDEX_TO_SHOW, 0);
        }

        tabs[0] = mMoinLayout;
        tabs[1] = mWasaiLayout;
        tabs[2] = mWowoLayout;
        tabs[3] = mMineLayout;
        tabs[4] = mCosplayLayout;
        for (int i = 0; i < tabs.length; i++) {
            tabs[i].setOnClickListener(this);
        }

        fragments = new Fragment[TAB_NUM];
        fragmentManager = getSupportFragmentManager();
        fragments[0] = fragmentManager.findFragmentById(R.id.fragement_moin);
        fragments[1] = fragmentManager.findFragmentById(R.id.fragement_wasai);
        fragments[2] = fragmentManager.findFragmentById(R.id.fragement_wowo);
        fragments[3] = fragmentManager.findFragmentById(R.id.fragement_mine);
        //fragments[4] =  fragmentManager.findFragmentById(R.id.fragement_cosplay);

        showTab(fragmentToShow);
    }

    private void showTab(int index) {


        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.hide(fragments[0]);
        fragmentTransaction.hide(fragments[1]);
        fragmentTransaction.hide(fragments[2]);
        fragmentTransaction.hide(fragments[3]);
        //cosplay是直接一个 activity//

        mMoin.setImageResource(R.drawable.dockbar_moin);
        mWasai.setImageResource(R.drawable.dockbar_wasai);
        mWowo.setImageResource(R.drawable.dockbar_wo);
        mMine.setImageResource(R.drawable.dockbar_me);
        //mCamera.setImageResource(R.drawable.dockbar_cosplay);

        mTextMoin.setTextColor(getResources().getColor(R.color.common_title_grey));
        mTextWasai.setTextColor(getResources().getColor(R.color.common_title_grey));
        mTextWowo.setTextColor(getResources().getColor(R.color.common_title_grey));
        mTextMine.setTextColor(getResources().getColor(R.color.common_title_grey));
        //mTextCosplay.setTextColor(getResources().getColor(R.color.common_title_grey));

        switch (index) {
            case TAB_MOIN:
                mMoin.setImageResource(R.drawable.dockbar_moin_on);
                mTextMoin.setTextColor(getResources().getColor(R.color.common_text_main));
                break;
            case TAB_WASAI:
                mWasai.setImageResource(R.drawable.dockbar_wasai_on);
                mTextWasai.setTextColor(getResources().getColor(R.color.common_text_main));
                break;
            case TAB_WOWO:
                mWowo.setImageResource(R.drawable.dockbar_wo_on);
                mTextWowo.setTextColor(getResources().getColor(R.color.common_text_main));
                break;

            case TAB_MINE:
                mMine.setImageResource(R.drawable.dockbar_me_on);
                mTextMine.setTextColor(getResources().getColor(R.color.common_text_main));
                break;

//            case TAB_COSPLAY:
//                mCamera.setImageResource(R.drawable.dockbar_cosplay_on);
//                mTextCosplay.setTextColor(getResources().getColor(R.color.common_text_main));
//                break;

        }

        try {
            fragmentTransaction.show(fragments[index]);
        } catch (ArrayIndexOutOfBoundsException ae) {
            MyLog.d("showTab " + System.currentTimeMillis()
                    + ae);
        } catch (Exception e) {
            MyLog.d("showTab " + System.currentTimeMillis()
                    + e);
        } finally {
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_moin:
                showTab(TAB_MOIN);
                break;
            case R.id.tab_wasai:
                showTab(TAB_WASAI);
                break;

            case R.id.tab_wowo:
                showTab(TAB_WOWO);
                break;
            case R.id.tab_mine:
                showTab(TAB_MINE);
                break;

            case R.id.tab_camera:

               // if(kkk%2 == 0) {
                    CosplayManager.getInstance().startCosplay();
                //}
//                else
//                {
//                    CosplayManager.getInstance().startCosplay("gson启动");
//                }
                //kkk++;

                //showTab(TAB_COSPLAY);
                break;
        }
    }
    static  int kkk = 0;

    @Override
    protected void onResume() {
        super.onResume();

        mTextMoin.setText(R.string.tab_moin);
        mTextWasai.setText(R.string.tab_wasai);
        mTextWowo.setText(R.string.tab_wowo);
        mTextMine.setText(R.string.tab_mine);
        mTextCosplay.setText(R.string.tab_cosplay);

        setVirtualKeyHeight();

        root.setPadding(0,0,0,CommonsPreference.getInstance().getVirtualKeyboardHeight());
//        root.setPadding(0,0,0,CommonsPreference.getInstance().getVirtualKeyboardHeight());
        StringBuilder sb = new StringBuilder();
        sb.append("BaseFragmentActivity.vh=" + vh + "\n");
        sb.append("VK: " + mheightDifference + " resourceId: " + mresourceId + "\n");
        sb.append("screenHeight: " + mscreenHeight + " rect: " + mRectHeihgt + "\n");
        sb.append("DeviceName: " + MobileInfo.getDeviceName() + "\n");
        sb.append("DeviceName2: " + MobileInfo.getDeviceName().startsWith("Meizu__Meizu") + "\n");
        sb.append("density: " + getResources().getDisplayMetrics().density + "\n");
        sb.append("bottom: " + CommonsPreference.getInstance().getVirtualKeyboardHeight() + "\n");
//        sb.append("meizu: " + MobileInfo.hasSmBar() + "\n");
        info.setText(sb.toString());
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                ToastUtils.toast(MoinActivity.this, R.string.exit_tip);
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
//
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                // 注意,这个地方必须加FLAG_ACTIVITY_NEW_TASK
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addCategory(Intent.CATEGORY_HOME);
//                this.startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showUpdateDialog(App b) {
        final String version = b.getVersionCode();
        final String url = b.getApkFile().getUrl();

        if (mUpdateDialog == null) {
            mUpdateDialog = new Dialog(mContext, R.style.Translucent_NoTitle);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.version_update_layout, null);
        int titleId = R.id.nq_update_dialog_title;
        TextView title = (TextView) view.findViewById(titleId);
        title.setText(b.getTitle());

        int contentId = R.id.updatedialog_content;
        TextView content = (TextView) view.findViewById(contentId);
        content.setText(b.getDesc());

        int btnCancelId = R.id.no_update;
        Button btnCancel = (Button) view.findViewById(btnCancelId);
        btnCancel.setText(getString(R.string.update_no));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUpdateDialog.dismiss();
            }
        });

        int btnOkId = R.id.ok_update;
        Button btnOk = (Button) view.findViewById(btnOkId);
        btnOk.setText(getString(R.string.update_yes));
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                //处理升级
                try {
                    mUpdateDialog.dismiss();
                    UpdateManager.getInstance().downloadApp(url, version);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mUpdateDialog.setContentView(view);
        mUpdateDialog.setCanceledOnTouchOutside(true);

        if (!mUpdateDialog.isShowing()) {
            mUpdateDialog.show();
        }
    }

}
