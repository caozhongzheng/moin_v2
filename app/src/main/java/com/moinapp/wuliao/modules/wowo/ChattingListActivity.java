package com.moinapp.wuliao.modules.wowo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;

import com.keyboard.XhsEmoticonsKeyBoardBar;
import com.keyboard.bean.EmoticonBean;
import com.keyboard.view.EmoticonsToolBarView;
import com.keyboard.view.I.IView;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.wowo.model.AppBean;
import com.moinapp.wuliao.utils.EmoticonsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moying on 15/6/8.
 */

public class ChattingListActivity extends Activity {

    ILogger MyLog = LoggerFactory.getLogger("chat");
    XhsEmoticonsKeyBoardBar kv_bar;
    ListView lv_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        EmoticonsUtils.initEmoticonsDB(getApplicationContext());

//        initTopBar(getString(R.string.chat));
//        initLeftBtn(true, R.drawable.back_gray);

        findView();
        initEvent();
    }

    public void findView(){
        kv_bar = (XhsEmoticonsKeyBoardBar) findViewById(R.id.kv_bar);
        // db中已收藏和下载的表情
        kv_bar.setBuilder(EmoticonsUtils.getBuilder(this));
        // 别的表情组
        kv_bar.addToolView(com.keyboard.view.R.drawable.btn_setup);
        kv_bar.addToolView(com.keyboard.view.R.drawable.btn_setup);
        kv_bar.addToolView(com.keyboard.view.R.drawable.btn_setup);
        kv_bar.addToolView(com.keyboard.view.R.drawable.btn_setup);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toolBtnView = inflater.inflate(R.layout.xhs_view_toolbtn_right_simple,null);
        toolBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kv_bar.del();
            }
        });
        kv_bar.addFixedView(toolBtnView, true);

        View view =  inflater.inflate(R.layout.xhs_view_apps, null);
        kv_bar.add(view);

        // 此处对应某个表情专辑的介绍等，view2就对应kv_bar.setBuilder，以及kv_bar.addToolView的数组下表2的介绍view。
//        TextView view2 =  (TextView)inflater.inflate(R.layout.xhs_view_test, null);
//        view2.setText("PAGE 3");
//        kv_bar.add(view2);
//        TextView view3 =  (TextView)inflater.inflate(R.layout.xhs_view_test, null);
//        view3.setText("PAGE 4");
//        kv_bar.add(view3);
//        TextView view4 =  (TextView)inflater.inflate(R.layout.xhs_view_test, null);
//        view4.setText("PAGE 5");
//        kv_bar.add(view4);

        // 加号中图片和拍照用的
        GridView gv_apps = (GridView)view.findViewById(R.id.gv_apps);
        ArrayList<AppBean> mAppBeanList = new ArrayList<AppBean>();
        String[] funcArray = getResources().getStringArray(com.keyboard.view.R.array.apps_func);
        String[] funcIconArray = getResources().getStringArray(com.keyboard.view.R.array.apps_func_icon);
        for (int i = 0; i < funcArray.length; i++) {
            AppBean bean = new AppBean();
            bean.setId(i);
            bean.setIcon(funcIconArray[i % 2]);
            bean.setFuncName(funcArray[i % 2]);
            mAppBeanList.add(bean);
//            MyLog.i("gv_apps : " + bean.toString());
        }

        AppsAdapter adapter = new AppsAdapter(this, mAppBeanList);
        gv_apps.setAdapter(adapter);

        kv_bar.getEmoticonsToolBarView().addOnToolBarItemClickListener(new EmoticonsToolBarView.OnToolBarItemClickListener() {
            @Override
            public void onToolBarItemClick(int position) {
//                ToastUtils.toast(ChattingListActivity.this, "EmoticonsToolBarView Click : " + position);
                if (position == 3) {
                    kv_bar.show(3);
                } else if (position == 4) {
                    kv_bar.show(4);
                } else if (position == 5) {
                    kv_bar.show(5);
                }
            }
        });

        // 点击下载或收藏的大表情响应事件，自动发送出去（聊天）
        kv_bar.getEmoticonsPageView().addIViewListener(new IView() {
            @Override
            public void onItemClick(EmoticonBean bean) {
                if(bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF){
                    ImMsgBean imMsgBean = new ImMsgBean();
                    imMsgBean.setContent(bean.getIconUri());
                    imMsgBean.setMsgType(ImMsgBean.CHAT_MSGTYPE_IMG);
                    mChattingListAdapter.addData(imMsgBean, true, false);
                    // listview 滚到最后一句
                    lv_chat.setSelection(lv_chat.getBottom());
                    MyLog.i("getEmoticonsPageView.onItemClick: " + bean.toString());
                }
            }

            @Override
            public boolean onItemLongClick(int position, View converView, EmoticonBean bean) {
                MyLog.i("getEmoticonsPageView.onItemLongClick: " + bean.toString());
                return false;
            }

            @Override
            public void onItemDisplay(EmoticonBean bean) {
                MyLog.i("getEmoticonsPageView.onItemDisplay: " + bean.toString());
            }

            @Override
            public void onPageChangeTo(int position) {
                MyLog.i("getEmoticonsPageView.onPageChangeTo: " + position);
            }
        });

        kv_bar.setOnKeyBoardBarViewListener(new XhsEmoticonsKeyBoardBar.KeyBoardBarViewListener() {
            // 可以添加一个长按的工作了吧
            @Override
            public void OnKeyBoardStateChange(int state, int height) {
                lv_chat.post(new Runnable() {
                    @Override
                    public void run() {
                        lv_chat.setSelection(lv_chat.getAdapter().getCount() - 1);
                    }
                });
            }

            @Override
            public void OnEmjBtnClick() {

            }

            @Override
            public void OnPhotoBtnClick() {

            }

            @Override
            public void OnSendBtnClick(String msg) {
                if(!TextUtils.isEmpty(msg)){
                    ImMsgBean bean = new ImMsgBean();
                    bean.setContent(msg);
                    mChattingListAdapter.addData(bean, true, false);
                    lv_chat.post(new Runnable() {
                        @Override
                        public void run() {
                            lv_chat.setSelection(lv_chat.getBottom());
                        }
                    });


                    kv_bar.clearEditText();
                }
//                ToastUtils.toast(ChattingListActivity.this, "SendBtn Click");
            }

            @Override
            public void OnVoiceBtnClick() {
//                ToastUtils.toast(ChattingListActivity.this, "VideoBtn Click");
            }

            @Override
            public void OnMultimediaBtnClick() {
                // 没看到多媒体表情啊
//                ToastUtils.toast(ChattingListActivity.this, "MultimediaBtn Click");
            }
        });

        lv_chat = (ListView) findViewById(R.id.lv_chat);

        lv_chat.setOnScrollListener(new AbsListView.OnScrollListener() {
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
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { }
        });

        mChattingListAdapter = new ChattingListAdapter(this);
        List<ImMsgBean> beanList = new ArrayList<ImMsgBean>();
        for(int i = 0 ; i <20 ; i++){
            ImMsgBean bean = new ImMsgBean();
            bean.setContent("Chat:" + i);
            beanList.add(bean);
        }
        mChattingListAdapter.addData(beanList);
        lv_chat.setAdapter(mChattingListAdapter);
    }
    ChattingListAdapter mChattingListAdapter;
    public void initEvent(){
    }
}
