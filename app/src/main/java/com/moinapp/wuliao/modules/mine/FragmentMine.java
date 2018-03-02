package com.moinapp.wuliao.modules.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.ui.textdrawable.DataItem;
import com.moinapp.wuliao.commons.ui.textdrawable.DrawableProvider;
import com.moinapp.wuliao.modules.login.LoginActivity;
import com.moinapp.wuliao.modules.login.LoginConstants;
import com.moinapp.wuliao.modules.login.LoginManager;
import com.moinapp.wuliao.modules.login.PersonalInfoActivity;
import com.moinapp.wuliao.modules.login.model.UserInfo;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

/**
 * Created by moying on 15/5/13.
 */
public class FragmentMine extends Fragment {

    private static final ILogger MyLog = LoggerFactory.getLogger(MineModule.MODULE_NAME);
    private Context context;
    private ImageLoader imageLoader;
    private ImageView avatar, newEmjNum;
    private TextView mNickname;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mine_layout, container, false);

        avatar = (ImageView) rootView.findViewById(R.id.avatar);
        avatar.setOnClickListener(avatarListener);
        mNickname = (TextView) rootView.findViewById(R.id.nickname);
        mNickname.setOnClickListener(avatarListener);

        newEmjNum = (ImageView) rootView.findViewById(R.id.new_emj_iv);
        setDownloadEmjNum();


        rootView.findViewById(R.id.rl_emj).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO View/Edit my download emojiResource
                if(ClientInfo.isUserLogin()) {
                    AppTools.toIntent(getActivity().getApplicationContext(), MyEmojiActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                    MinePreference.getInstance().setEmjDownloadCount(0);
                    MinePreference.getInstance().setEmjDownloadID("");
                } else {
                    Bundle b = new Bundle();
                    b.putBoolean(LoginActivity.KEY_NEED_RETURN, true);
                    b.putInt(LoginActivity.KEY_FROM, LoginConstants.MINE_EMOJI);
                    AppTools.toIntent(getActivity().getApplicationContext(), b, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }
        });

        rootView.findViewById(R.id.rl_fip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClientInfo.isUserLogin()) {
                    AppTools.toIntent(getActivity().getApplicationContext(), FavoriateIPActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    Bundle b = new Bundle();
                    b.putBoolean(LoginActivity.KEY_NEED_RETURN, true);
                    b.putInt(LoginActivity.KEY_FROM, LoginConstants.MINE_IP);
                    AppTools.toIntent(getActivity().getApplicationContext(), b, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }
        });

        rootView.findViewById(R.id.rl_wowo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClientInfo.isUserLogin()) {
                    AppTools.toIntent(getActivity().getApplicationContext(), MyPostActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    Bundle b = new Bundle();
                    b.putBoolean(LoginActivity.KEY_NEED_RETURN, true);
                    b.putInt(LoginActivity.KEY_FROM, LoginConstants.MINE_WOWO);
                    AppTools.toIntent(getActivity().getApplicationContext(), b, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }
        });

        rootView.findViewById(R.id.rl_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClientInfo.isUserLogin()) {
                    AppTools.toIntent(getActivity().getApplicationContext(), SettingActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    Bundle b = new Bundle();
                    b.putBoolean(LoginActivity.KEY_NEED_RETURN, true);
                    b.putInt(LoginActivity.KEY_FROM, LoginConstants.MINE_SETTINGS);
                    AppTools.toIntent(getActivity().getApplicationContext(), b, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }
        });
        return rootView;
    }

    private void setDownloadEmjNum() {
        if(ClientInfo.isUserLogin()) {
            int num = MinePreference.getInstance().getEmjDownloadCount();
            if (num > 0) {
                newEmjNum.setVisibility(View.VISIBLE);
                DataItem drawable = DrawableProvider.getInstance().getDataItem("" + num, R.color.common_text_main, DrawableProvider.SAMPLE_ROUND);
                newEmjNum.setImageDrawable(drawable.getDrawable());
            } else {
                newEmjNum.setVisibility(View.GONE);
            }
        } else {
            newEmjNum.setVisibility(View.GONE);
        }
    }

    private String headimgurl_str;
    private String nick;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 4:
                    if(msg.obj != null) {
                        UserInfo us = (UserInfo) msg.obj;
                        try {
                            nick = us.getNickname();
                            MinePreference.getInstance().setNickname(nick);
                            MyLog.i("nickname is : " + nick);
                            if(us.getAvatar() == null) {
                                headimgurl_str = null;
                            } else {
                                headimgurl_str = us.getAvatar().getUri();
                                MyLog.i("headimgurl_str is : " + headimgurl_str);
                            }
                            handler.post(runnable_avatar);

                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }
                    }
                    break;
            }
        }
    };
    Runnable runnable_avatar = new Runnable() {
        @Override
        public void run() {
            MyLog.i("set nickname is : " + nick);
            mNickname.setText(StringUtil.isNullOrEmpty(nick) ? getString(R.string.no_nickname) : nick);
            if(headimgurl_str != null) {
                try {
                    imageLoader.displayImage(headimgurl_str, avatar, BitmapUtil.getImageLoaderOption(), new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            BitmapUtil.saveUserAvatar(getActivity().getApplicationContext(), bitmap);
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });
                } catch (NullPointerException e) {
                    MyLog.e(e);
                } catch (OutOfMemoryError e) {
                    MyLog.e(e);
                } catch (Exception e) {
                    MyLog.e(e);
                }
            } else {
                avatar.setImageResource(R.drawable.icon);
            }

        }
    };

    View.OnClickListener avatarListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(ClientInfo.isUserLogin()) {
                Bundle bundle = new Bundle();
                bundle.putString(PersonalInfoActivity.KEY_PLATFORM, PersonalInfoActivity.FROMMINE);
                AppTools.toIntent(getActivity().getApplicationContext(), bundle, PersonalInfoActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
            } else {
                Bundle b = new Bundle();
                b.putBoolean(LoginActivity.KEY_NEED_RETURN, true);
                b.putInt(LoginActivity.KEY_FROM, LoginConstants.MINE_AVATAR);
                AppTools.toIntent(getActivity().getApplicationContext(), LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }
    };
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showAvatar();
    }

    private void showAvatar() {
        if(ClientInfo.isUserLogin()) {
//            avatar.setClickable(true);
            File file = new File(BitmapUtil.getAvatarImagePath());
            if(file.exists()){
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(BitmapUtil.getAvatarImagePath(), options);
                // 源图片的宽度
//                int width = options.outWidth;
//                int height = options.outHeight;
                options.inJustDecodeBounds = false;
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(BitmapUtil.getAvatarImagePath(), options);
                    MyLog.i("showAvatar bitmap.path :[" + BitmapUtil.getAvatarImagePath() + "]");
                    MyLog.i("showAvatar bitmap.wh :[" + bitmap.getWidth() + "*" + bitmap.getHeight() + "]");
                    avatar.setImageBitmap(bitmap);
                } catch (OutOfMemoryError error) {
                    MyLog.e(error);
                }

//                com.moinapp.wuliao.modules.wowo.imageloader.ImageLoader.getInstance().loadImage(BitmapUtil.getAvatarImagePath(), avatar);
//                imageLoader.displayImage("file://" + BitmapUtil.getAvatarImagePath(), avatar, BitmapUtil.getImageLoaderOption());
            } else {
                refreshUserInfo();

                avatar.setImageResource(R.drawable.icon);
            }
            if(StringUtil.isNullOrEmpty(MinePreference.getInstance().getNickname())) {
                mNickname.setText(R.string.no_nickname);
                if(MinePreference.getInstance().isNeedFetchNickname()) {
                    MyLog.i("第一次进来时昵称没有或者是空的话就刷新下用户信息");
                    refreshUserInfo();
                    MinePreference.getInstance().setNeedFetchNickname(false);
                }
            } else {
                mNickname.setText(MinePreference.getInstance().getNickname());
            }
        } else {
            mNickname.setText(R.string.unlogin);
            avatar.setImageResource(R.drawable.icon);
        }
    }

    private void refreshUserInfo() {
        LoginManager.getInstance().getUserInfo(new IListener() {
            @Override
            public void onSuccess(Object obj) {
                Message msg = handler.obtainMessage();
                msg.what = 4;
                msg.obj = obj;
                handler.sendMessage(msg);
            }

            @Override
            public void onNoNetwork() {
                handler.sendEmptyMessage(2);
            }

            @Override
            public void onErr(Object object) {

            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        MyLog.i("onHiddenChanged hidden :" + hidden);
        if(!isHidden()) {
            showAvatar();
            setDownloadEmjNum();

            if(!ClientInfo.isUserLogin()) {
                Bundle b = new Bundle();
                b.putBoolean(LoginActivity.KEY_NEED_RETURN, true);
                b.putInt(LoginActivity.KEY_FROM, LoginConstants.FRAGMENT_MINE);
                AppTools.toIntent(getActivity().getApplicationContext(), b, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        MyLog.i("onResume");
        showAvatar();
        setDownloadEmjNum();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
