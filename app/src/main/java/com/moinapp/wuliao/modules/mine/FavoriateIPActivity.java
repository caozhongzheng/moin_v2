package com.moinapp.wuliao.modules.mine;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.ipresource.model.IPResource;
import com.moinapp.wuliao.modules.mine.listener.FavoriateIPListener;
import com.moinapp.wuliao.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moying on 15/6/30.
 */
public class FavoriateIPActivity extends BaseActivity {
    private ILogger MyLog = LoggerFactory.getLogger(MineModule.MODULE_NAME);

    /**
     * 所有的IP
     */
    private List<IPResource> mIps;

    /**
     * 已选择的IP
     */
    private ArrayList<IPResource> mSelectIps;
    private GridView mGirdView;
    private MyIPAdapter mAdapter;

    private RelativeLayout mBottomLy;
    private Button mDeleteBtn;

    private int mMode = MineConstants.MODE_NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoriate_ip);

        initLoadingView();
        setLoadingMode(MODE_LOADING);

        initTopBar(getString(R.string.focus_ip));
        initLeftBtn(true, R.drawable.back_gray);
        initRightBtn(true, getString(R.string.edit));

        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getIPs();
    }

    @Override
    protected void reloadHandle() {
        super.reloadHandle();
        getIPs();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mGirdView = (GridView) findViewById(R.id.id_gridView);
        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
        mDeleteBtn = (Button) findViewById(R.id.id_delete);
    }

    /**
     * 为底部的删除设置点击事件
     */
    private void initEvent() {
        mDeleteBtn.setOnClickListener(deleteIPListener);
    }
    /**
     * 获取该用户关注的所有IP
     */
    private void getIPs() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                MineManager.getInstance().getFavoriateIP(new FavoriateIPListener() {
                    @Override
                    public void getFavoriateIPSucc(List<IPResource> ips) {
                        mIps = ips;
                        // 通知Handler获取关注IP完成
                        mHandler.sendEmptyMessage(0x110);
                        if(ips == null || ips.size()==0) {
                            MyLog.i("getFavoriateIPSucc null or empty：null?  " + (ips==null));
                        } else {
                            MyLog.i("getFavoriateIPSucc ips.size=" + ips.size());
                            for (int i = 0; i < ips.size(); i++) {
                                MyLog.i("第"+(i+1)+"个:id="+ips.get(i).get_id()
                                        +", name="+ips.get(i).getName()
                                        +", type="+ips.get(i).getType()
                                        +", intro="+ips.get(i).getShortIntro());
                            }
                        }
                    }

                    @Override
                    public void delFavoriateIPSucc(int result) {

                    }

                    @Override
                    public void onNoNetwork() {
                        mHandler.sendEmptyMessage(0x119);
                    }

                    @Override
                    public void onErr(Object object) {
                        mHandler.sendEmptyMessage(0x120);
                    }
                });

            }
        }).start();
    }

    private Handler mHandler = createHandler();
    private Handler createHandler() {
        return new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 0x119:
                        setLoadingMode(MODE_RELOADING);
                        ToastUtils.toast(FavoriateIPActivity.this, R.string.no_network);
                        break;
                    case 0x120:
                        setLoadingMode(MODE_RELOADING);
                        ToastUtils.toast(FavoriateIPActivity.this, R.string.connection_failed);
                        break;

                    case 0x110:
                        setLoadingMode(MODE_OK);
                        // 为View绑定数据
                        data2View();
                        if(mAdapter != null) {
                            mAdapter.setIpList(mIps);
                            mAdapter.notifyDataSetChanged();
                        }
                        if (mIps == null || mIps.size() == 0) {
                            ToastUtils.toast(getApplicationContext(), R.string.no_favoriate_ip);
                        }
                        break;
                    case 0x130:
                        for (int i = 0; i < MyIPAdapter.getSelectedIP().size(); i++) {
                            String id0 = MyIPAdapter.getSelectedIP().get(i).get_id();

                            for (int j = mIps.size() - 1; j > -1 ; j--) {
                                String id1 = mIps.get(j).get_id();
                                if(id1.equals(id0)) {
                                    mIps.remove(j);
                                    break;
                                }
                            }
                        }
                        mMode = MineConstants.MODE_NONE;
                        initRightBtn(true, getString(R.string.edit));
                        mBottomLy.setVisibility(View.GONE);

                        mSelectIps.clear();
                        MyIPAdapter.setMode(mMode);
                        MyIPAdapter.setSelectedIP(mSelectIps);
                        mAdapter.setIpList(mIps);
                        mAdapter.notifyDataSetChanged();
                        mDeleteBtn.setText(R.string.delete);

                        break;
                    default:
                        break;
                }
            }
        };
    }

    private View.OnClickListener deleteIPListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(MyIPAdapter.getSelectedIP().size() > 0) {
                List<String> ipIDs = new ArrayList<>();
                for (int i = 0; i < MyIPAdapter.getSelectedIP().size(); i++) {
                    ipIDs.add(MyIPAdapter.getSelectedIP().get(i).get_id());
                }
                MineManager.getInstance().delFavoriateIP(ipIDs, new FavoriateIPListener() {
                    @Override
                    public void getFavoriateIPSucc(List<IPResource> ips) {

                    }

                    @Override
                    public void delFavoriateIPSucc(int result) {
                        // 通知handler 删除成功
                        if(mHandler == null) {
                            mHandler = createHandler();
                        }
                        mHandler.sendEmptyMessage(0x130);
                    }

                    @Override
                    public void onNoNetwork() {
                        mHandler.sendEmptyMessage(0x119);
                    }

                    @Override
                    public void onErr(Object object) {
                        mHandler.sendEmptyMessage(0x120);
                    }
                });
            }
        }
    };

    @Override
    protected void rightBtnHandle() {
        super.rightBtnHandle();
        if(!data2View() && mMode == MineConstants.MODE_NONE) {
            return;
        }

        if (mMode == MineConstants.MODE_NONE) {
            mMode = MineConstants.MODE_EDIT;
            initRightBtn(true, getString(android.R.string.cancel));
            mBottomLy.setVisibility(View.VISIBLE);
        } else if (mMode == MineConstants.MODE_EDIT) {
            mMode = MineConstants.MODE_NONE;
            initRightBtn(true, getString(R.string.edit));
            mBottomLy.setVisibility(View.GONE);
        }
        mSelectIps.clear();
        MyIPAdapter.setMode(mMode);
        MyIPAdapter.setSelectedIP(mSelectIps);
        mDeleteBtn.setText(getString(R.string.delete));
        mDeleteBtn.setClickable(false);
        mDeleteBtn.setBackground(getResources().getDrawable(R.drawable.tag_btn_solid_grey_bg));
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 为View绑定数据
     */
    private boolean data2View() {
        if (mIps == null || mIps.size() == 0) {
            return false;
        }

        buildAdapter();
        setAdapter();
        return true;
    }

    private void buildAdapter() {
        if(mIps == null) {
            mIps = new ArrayList<>();
        }
        if(mSelectIps == null) {
            mSelectIps = new ArrayList<>();
        }
    }

    private void setAdapter() {
        if(mAdapter == null) {
            mAdapter = new MyIPAdapter(getApplicationContext(), mIps, mHandler);
        }

        MyIPAdapter.setSelectedIP(mSelectIps);
        MyIPAdapter.setMode(mMode);
        mAdapter.setTextCallback(new MyIPAdapter.TextCallback() {
            @Override
            public void onListen(int count) {
                if (count > 0) {
                    mDeleteBtn.setText(getString(R.string.delete) + " (" + count + ")");
                    mDeleteBtn.setClickable(true);
                    mDeleteBtn.setBackground(getResources().getDrawable(R.drawable.tag_btn_solid_bg));
                } else {
                    mDeleteBtn.setText(getString(R.string.delete));
                    mDeleteBtn.setClickable(false);
                    mDeleteBtn.setBackground(getResources().getDrawable(R.drawable.tag_btn_solid_grey_bg));
                }
            }
        });
        mGirdView.setAdapter(mAdapter);
    }
}
