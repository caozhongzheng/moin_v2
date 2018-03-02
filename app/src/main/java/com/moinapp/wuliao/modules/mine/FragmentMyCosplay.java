package com.moinapp.wuliao.modules.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.AppConstants;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.modal.BaseImage;
import com.moinapp.wuliao.modules.cosplay.CosplayManager;
import com.moinapp.wuliao.modules.cosplay.ui.CosplayConstants;
import com.moinapp.wuliao.modules.ipresource.EmojiShowActivity;
import com.moinapp.wuliao.modules.mine.listener.CosplayListener;
import com.moinapp.wuliao.modules.mine.model.EmojiCosPlay;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujiancheng on 15/7/6.
 */
public class FragmentMyCosplay  extends Fragment {
    // ===========================================================
    // Constants
    // ===========================================================
    ILogger MyLog = LoggerFactory.getLogger(FragmentMyEmoji.class.getSimpleName());


    protected Context mContext;
    private ImageLoader mImageLoader;

    private GridView mGirdView;
    private static RelativeLayout mBottomLy;
    private static Button mDeleteBtn;
    private static MyCosplayAdapter mAdapter;
    /**
     * 所有的表情
     */
    private List<EmojiCosPlay> mCosplayList;

//    private EmojiCosPlay mDefaultCosplay = new EmojiCosPlay();//默认的大咖秀入口
    /**
     * 用户选择的表情
     */
    public static ArrayList<EmojiCosPlay> mSelectedCosplay = new ArrayList<EmojiCosPlay>();

    static int count;

    public static int mMode;

    /**
     * loading需要的控件
     */
    RelativeLayout rl_enter;
    LinearLayout lv_loading;
    LinearLayout lv_reload;
    // ===========================================================
    // Constructors
    // ===========================================================
    public FragmentMyCosplay() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getApplicationContext();

        //初始化图片缓存
        mImageLoader = ImageLoader.getInstance();
        if (!mImageLoader.isInited()) {
            mImageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MyLog.i("FragmentWoList: onCreateView........................");
        View view = inflater.inflate(R.layout.my_emoji_cosplay, container, false);
        mGirdView = (GridView) view.findViewById(R.id.id_gridView);
        mBottomLy = (RelativeLayout) view.findViewById(R.id.id_bottom_ly);
        mDeleteBtn = (Button) view.findViewById(R.id.id_delete);
        mDeleteBtn.setOnClickListener(deleteListener);

        mCosplayList = new ArrayList<EmojiCosPlay>();
        mAdapter = new MyCosplayAdapter();
        mGirdView.setAdapter(mAdapter);
        mMode = MineConstants.MODE_NONE;

        initLoadingView(view);
        setLoadingMode(AppConstants.MODE_LOADING);
        getMyCosplayList();

        return view;
    }

    private void initLoadingView(View root) {
        rl_enter = (RelativeLayout) root.findViewById(R.id.__enter);
        lv_loading = (LinearLayout) root.findViewById(R.id.__loading);
        lv_reload = (LinearLayout) root.findViewById(R.id.__reload);
        lv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyCosplayList();
            }
        });
    }

    private void setLoadingMode(int mode) {
        rl_enter.setVisibility(mode == AppConstants.MODE_OK ? View.GONE : View.VISIBLE);
        lv_loading.setVisibility(mode == AppConstants.MODE_LOADING ? View.VISIBLE : View.GONE);
        lv_reload.setVisibility(mode == AppConstants.MODE_RELOADING ? View.VISIBLE : View.GONE);
    }

    private void showReloading() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoadingMode(AppConstants.MODE_RELOADING);
                ToastUtils.toast(getActivity(), R.string.no_network);
            }
        });

    }

    private void getMyCosplayList() {
        MineManager.getInstance().getMyCosplay(new CosplayListener() {
            @Override
            public void createCosplaySucc(int result, String id) {

            }

            @Override
            public void getCosplaySucc(final List<EmojiCosPlay> cosPlayList) {
                if (cosPlayList == null || cosPlayList.size() == 0) {
                    MyLog.i("getCosplaySucc: cosplay list is empty");
                } else {
                    MyLog.i("getCosplaySucc: cosplay list.size =" + cosPlayList.size());
                }
                mCosplayList.addAll(cosPlayList);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoadingMode(AppConstants.MODE_OK);
                        mAdapter.notifyDataSetChanged();
                        // 入本地数据库
                        CosplayManager.getInstance().addCosplayIntoDB(cosPlayList);
                    }
                });

            }

            @Override
            public void delCosplaySucc(int result) {

            }

            @Override
            public void onNoNetwork() {
                showReloading();
            }

            @Override
            public void onErr(Object object) {
                showReloading();
            }
        });
    }

    private class MyCosplayAdapter extends BaseAdapter {
        protected LayoutInflater mInflater;

        public MyCosplayAdapter() {
            this.mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mCosplayList.size();
        }

        @Override
        public Object getItem(int position) {
            return mCosplayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.my_emoji_cosplay_item, null);
                viewHolder = getViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //加上默认的大咖秀入口item
//            if (!mCosplayList.contains(mDefaultCosplay)) {
//                mCosplayList.add(mDefaultCosplay);
//            }

            setContentView(viewHolder, position);
            return convertView;
        }

        private void setContentView(final ViewHolder viewHolder, final int position) {
            if(mCosplayList == null || mCosplayList.size() <= position) {
                return;
            }

            final EmojiCosPlay cosPlay = mCosplayList.get(position);
            if (cosPlay != null && cosPlay.getIcon() != null) {
                mImageLoader.displayImage(cosPlay.getIcon().getUri(), viewHolder.mImage, BitmapUtil.getImageLoaderOption());
            } else {
                viewHolder.mImage.setBackgroundResource(R.drawable.cosplay_dakaxiu);
            }

            if(mMode == MineConstants.MODE_EDIT) {
                viewHolder.mSelectIcon.setVisibility(View.VISIBLE);
                if(mSelectedCosplay.contains(mCosplayList.get(position))) {
                    viewHolder.mSelectIcon.setImageResource(R.drawable.icon_select);
                } else {
                    viewHolder.mSelectIcon.setImageResource(R.drawable.icon_unselect);
                }

                viewHolder.mImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mSelectedCosplay.contains(mCosplayList.get(position))) {
                            viewHolder.mSelectIcon.setImageResource(R.drawable.icon_unselect);
                            mSelectedCosplay.remove(mCosplayList.get(position));
                            count--;
                        } else {
                            viewHolder.mSelectIcon.setImageResource(R.drawable.icon_select);
                            mSelectedCosplay.add(mCosplayList.get(position));
                            count++;
                        }
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
            } else if(mMode == MineConstants.MODE_NONE) {
                viewHolder.mSelectIcon.setVisibility(View.GONE);
                viewHolder.mImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mMode == MineConstants.MODE_EDIT)
                            return;
                        onClickCosplay(cosPlay);
                    }
                });
            }
        }

        private ViewHolder getViewHolder(View convertView) {
            ViewHolder holder = new ViewHolder();
            holder.mImage = (ImageView) convertView.findViewById(R.id.image_cosplay);
            holder.mSelectIcon = (ImageView) convertView.findViewById(R.id.id_item_select);

            return holder;
        }

        class ViewHolder {
            public ImageView mImage;
            public ImageView mSelectIcon;
        }

    }

    private View.OnClickListener deleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mSelectedCosplay != null && mSelectedCosplay.size() > 0) {
                List<String> cosplayIDs = new ArrayList<>();
                for (int i = 0; i < mSelectedCosplay.size(); i++) {
                    cosplayIDs.add(mSelectedCosplay.get(i).getId());
                }
                MineManager.getInstance().delMyCosplay(cosplayIDs, new CosplayListener() {
                    @Override
                    public void createCosplaySucc(int result, String id) {

                    }

                    @Override
                    public void getCosplaySucc(List<EmojiCosPlay> cosPlayList) {

                    }

                    @Override
                    public void delCosplaySucc(int result) {
                        for (int i = 0; i < mSelectedCosplay.size(); i++) {
                            mCosplayList.remove(mSelectedCosplay.get(i));
                        }
                        mSelectedCosplay.clear();
                        count = 0;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                mDeleteBtn.setText(R.string.delete);
                            }
                        });
                    }

                    @Override
                    public void onNoNetwork() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.toast(mContext, R.string.no_network);
                            }
                        });
                    }

                    @Override
                    public void onErr(Object object) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.toast(mContext, R.string.connection_failed);
                            }
                        });
                    }
                });
            }
        }
    };


    public ArrayList<EmojiCosPlay> getSelectedIP() {
        return mSelectedCosplay;
    }

    public void setSelectedCosplay(ArrayList<EmojiCosPlay> selectedCosplay) {
        mSelectedCosplay = selectedCosplay;
        count = mSelectedCosplay.size();
    }

    public void setMode(int mode) {
        mMode = mode;
        mBottomLy.setVisibility(mMode == MineConstants.MODE_EDIT ? View.VISIBLE : View.GONE);
        if (mMode == MineConstants.MODE_NONE) {
            mSelectedCosplay.clear();
            mDeleteBtn.setText(R.string.delete);
        } else {
            if (count == 0) {
                mDeleteBtn.setClickable(false);
                mDeleteBtn.setBackground(getResources().getDrawable(R.drawable.tag_btn_solid_grey_bg));
            }
        }
        setSelectedCosplay(new ArrayList<EmojiCosPlay>());
        mAdapter.notifyDataSetChanged();
    }

    private void onClickCosplay(EmojiCosPlay cosPlay) {
        String cosplayPath = CosplayConstants.COSPLAY_EMOJI_FOLDER + cosPlay.getId() + ".gif";
        Intent i = new Intent(mContext, EmojiShowActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );
        Bundle b = new Bundle();
        b.putSerializable(EmojiShowActivity.EMOJI_PATH, cosplayPath);
        i.putExtras(b);
        mContext.startActivity(i);
    }
}