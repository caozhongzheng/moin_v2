package com.moinapp.wuliao.modules.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.MoinActivity;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.commons.ui.MyPopWindow;
import com.moinapp.wuliao.modules.login.model.Location;
import com.moinapp.wuliao.modules.login.model.UserInfo;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;

/**
 * Created by moying on 15/5/11.
 */
public class PersonalInfoActivity extends BaseActivity implements View.OnClickListener {

    private ILogger MyLog = LoggerFactory.getLogger(LoginModule.MODULE_NAME);
    private MyPopWindow alter_avatar_popupWindow, alter_nickname_popupWindow, alter_age_popupWindow,
            alter_gender_popupWindow, alter_signature_popupWindow;
    private RelativeLayout avatar_item, nickname_item, age_item, gender_item, zodiac_item, location_item, signature_item;
    private ImageView avatar_iv, alter_sex_male_selected, alter_sex_female_selected, clear_nickname, clear_signature;
    private TextView nickname_tv, age_tv, alter_age_cancel, gender_tv, zodiac_tv, address_tv, signature_tv,
            alter_avatar_album, alter_avatar_camera, alter_avatar_cancel, alter_nickname_cancel, alter_nickname_sure, alter_sex_male,
            alter_sex_female, alter_sex_cancel, alter_signature_cancel, alter_signature_sure, remaining_number;
    private TextView alter_age_70, alter_age_80, alter_age_85, alter_age_90, alter_age_95, alter_age_00;
    private ImageView alter_age_70_s, alter_age_80_s, alter_age_85_s, alter_age_90_s, alter_age_95_s, alter_age_00_s;
    private EditText alter_nickname_et, alter_signature_et;
    // --------------------------------------
    public static String KEY_NICKNAME = "nickname";
    public static String KEY_SEX = "sex";
    public static String KEY_HEADIMGURL = "headimgurl";
    public static String KEY_COUNTRY = "country";
    public static String KEY_CITY = "city";
    public static String KEY_PROVINCE = "province";
    public static String KEY_PLATFORM = "platform";
    public static String KEY_ZODIAC = "zodiac";
    public static final String FROMMINE = "mine";
    private String FROM = "";
    public static int ZODIAC_NONE = -1;
    // --------------------------------------
    public static final String AGE_NONE = "-1";
    // --------------------------------------
    public static final int NO_SDCARD = -1;
    public static final int NONE = 0;
    public static final int PHOTO_HRAPH = 1;// 拍照
    public static final int PHOTO_ZOOM = 2; // 缩放
    public static final int PHOTO_RESULT = 3;// 结果
    public static final int CROP_CAMERA = 6;// 截取拍照的头像
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final int ZODIAC_RESULT = 4;// 结果
    public static final int CITY_RESULT = 5;// 选择城市结果
    private int zodiac_i = ZODIAC_NONE;
    // --------------------------------------
    private int signature_max = 0;
    private String nickname_str, age_str, sex_param_str, headimgurl_str, country_str, city_str, province_str, signature_str;

    private SHARE_MEDIA platform;

    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.personalinformation_layout);

        if(!ClientInfo.isUserLogin()) {
            ToastUtils.toast(PersonalInfoActivity.this, R.string.login_first);
        }

        initLoadingView();
        setLoadingMode(MODE_LOADING);
        initView();
        initData(getIntent());
        initAvatarWindow();
        initNicknameWindow();
        initAgeWindow();
        initGenderWindow();
        initSignatureWindow();
    }

    private void initView() {
        avatar_iv = (ImageView) this.findViewById(R.id.user_avatar);
        nickname_tv = (TextView) this.findViewById(R.id.nickname);
        age_tv = (TextView) this.findViewById(R.id.age);
        gender_tv = (TextView) this.findViewById(R.id.gender);
        zodiac_tv = (TextView) this.findViewById(R.id.zodiac);
        address_tv = (TextView) this.findViewById(R.id.location);
        signature_tv = (TextView) this.findViewById(R.id.signature);

        avatar_item = (RelativeLayout) this.findViewById(R.id.avatar_item);
        nickname_item = (RelativeLayout) this.findViewById(R.id.nickname_item);
        age_item = (RelativeLayout) this.findViewById(R.id.age_item);
        gender_item = (RelativeLayout) this.findViewById(R.id.gender_item);
        zodiac_item = (RelativeLayout) this.findViewById(R.id.zodiac_item);
        location_item = (RelativeLayout) this.findViewById(R.id.location_item);
        signature_item = (RelativeLayout) this.findViewById(R.id.signature_item);
        avatar_item.setOnClickListener(this);
        nickname_item.setOnClickListener(this);
        age_item.setOnClickListener(this);
        gender_item.setOnClickListener(this);
        zodiac_item.setOnClickListener(this);
        location_item.setOnClickListener(this);
        signature_item.setOnClickListener(this);
    }

    /**
     * 在intent中如果获取了用户信息，那么就用intent中的
     * @param intent*/
    private void initData(Intent intent) {
        imageLoader = ImageLoader.getInstance();
        if(!imageLoader.isInited()) {
            imageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }
        age_str = AGE_NONE;
        signature_max = getResources().getInteger(R.integer.signature_max_len);
        if(intent != null) {
            String plat = intent.getStringExtra(KEY_PLATFORM);
            MyLog.i("from=" + plat);
            if(!StringUtil.isNullOrEmpty(plat)) {
                if(plat.equals(FROMMINE)) {
                    FROM = FROMMINE;
                } else {
                    platform = SHARE_MEDIA.convertToEmun(plat);
                    MyLog.i("platform.name=" + platform.name());
                    MyLog.i("platform=" + platform.toString());
                }
            }
            nickname_str = StringUtil.nullToEmpty(intent.getStringExtra(KEY_NICKNAME));
            nickname_tv.setText(nickname_str);
            MyLog.i("nick=" + nickname_str);
            sex_param_str = StringUtil.nullToEmpty(intent.getStringExtra(KEY_SEX));
            gender_tv.setText(StringUtil.getGender(PersonalInfoActivity.this, sex_param_str, true));
            MyLog.i("sex=" + sex_param_str);
            headimgurl_str = intent.getStringExtra(KEY_HEADIMGURL);
            MyLog.i("headimgurl=" + headimgurl_str);
            imageLoader.displayImage(headimgurl_str, avatar_iv, BitmapUtil.getImageLoaderOption());
            country_str = intent.getStringExtra(KEY_COUNTRY);
            city_str = intent.getStringExtra(KEY_CITY);
            province_str = intent.getStringExtra(KEY_PROVINCE);
            Location l = new Location();
            l.setProvince(province_str);
            l.setCity(city_str);
            l.setCountry(country_str);
            address_tv.setText(getLocation(l));
        }

        mHandler.sendEmptyMessageDelayed(0, platform == null ? 0 : 5000);
    }

    @Override
    protected void reloadHandle() {
        super.reloadHandle();
        refreshUserInfo();
    }

    private void refreshUserInfo() {
        LoginManager.getInstance().getUserInfo(new IListener() {
            @Override
            public void onSuccess(Object obj) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoadingMode(MODE_OK);
                    }
                });
                Message msg = mHandler.obtainMessage();
                msg.what = 2;
                msg.obj = obj;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onNoNetwork() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoadingMode(MODE_RELOADING);
                    }
                });
                mHandler.sendEmptyMessage(3);
            }

            @Override
            public void onErr(Object object) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoadingMode(MODE_RELOADING);
                    }
                });
            }
        });
    }

    /**
     *  修改头像
     */
    public void initAvatarWindow() {
        View popupWindow_view = this.getLayoutInflater().inflate(R.layout.alter_avatar, null);
        popupWindow_view.setPadding(0, 0, 0, CommonsPreference.getInstance().getVirtualKeyboardHeight());
        alter_avatar_album = (TextView) popupWindow_view.findViewById(R.id.alter_avatar_album);
        alter_avatar_camera = (TextView) popupWindow_view.findViewById(R.id.alter_avatar_camera);
        alter_avatar_cancel = (TextView) popupWindow_view.findViewById(R.id.alter_avatar_cancel);
        alter_avatar_album.setOnClickListener(this);
        alter_avatar_camera.setOnClickListener(this);
        alter_avatar_cancel.setOnClickListener(this);
        alter_avatar_popupWindow = new MyPopWindow(this, popupWindow_view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    // 拍照
    private void doCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(BitmapUtil.getTmpAvatarCameraPath())));
        startActivityForResult(intent, PHOTO_HRAPH);
    }

    /**
     *  剪切头像 此处用大图格式，由服务器生成不同分辨率对应的头像尺寸
     */
    public void cropAvatar(Uri uri) {
        MyLog.i("avatar cropImageUri:" + uri);
        /*
Exta Options Table for image crop:
SetExtra	DataType	Description
crop	String	Signals the crop feature
aspectX	int	Aspect Ratio
aspectY	int	Aspect Ratio
outputX	int	width of output created from this Intent
outputY	int	width of output created from this Intent
scale	boolean	should it scale
return-data	boolean	Return the bitmap with Action=inline-data by using the data
data	Parcelable	Bitmap to process, you may provide it a bitmap (not tested)
circleCrop	String	if this string is not null, it will provide some circular cr
MediaStore.EXTRA_OUTPUT ("output")	URI	Set this URi to a File:
*/

        if(uri == null) {
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 640);
            intent.putExtra("outputY", 640);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, BitmapUtil.getAvatarCropUri());
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true); // no face detection
        /*if this string is not null, it will provide some circular cr*/
            // TODO circleCrop目前还不能用
            intent.putExtra("circleCrop", true);
            startActivityForResult(intent, PHOTO_RESULT);
        } else {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 640);
            intent.putExtra("outputY", 640);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, BitmapUtil.getAvatarCropUri());
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true); // no face detection
            // TODO circleCrop目前还不能用
            intent.putExtra("circleCrop", true);
            startActivityForResult(intent, CROP_CAMERA);
        }
    }

    /**
     *  修改昵称对话框
     */
    private void initNicknameWindow() {
        View alter_nickname_view = this.getLayoutInflater().inflate(R.layout.alter_nickname, null);
        alter_nickname_et = (EditText) alter_nickname_view.findViewById(R.id.alter_nickname);

        alter_nickname_cancel = (TextView) alter_nickname_view.findViewById(R.id.alter_nickname_cancel);
        alter_nickname_sure = (TextView) alter_nickname_view.findViewById(R.id.alter_nickname_sure);
        clear_nickname = (ImageView) alter_nickname_view.findViewById(R.id.clear_nickname);
        alter_nickname_cancel.setOnClickListener(this);
        alter_nickname_sure.setOnClickListener(this);
        alter_nickname_sure.setClickable(false);
        clear_nickname.setOnClickListener(this);
        alter_nickname_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString() != null && !editable.toString().equals(nickname_str)) {
                    alter_nickname_sure.setClickable(true);
                } else {
                    alter_nickname_sure.setClickable(false);
                }
            }
        });
        alter_nickname_popupWindow = new MyPopWindow(this, alter_nickname_view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    private void alterNickName() {
        final String nickname = StringUtil.filter(alter_nickname_et.getText().toString());
        if(StringUtil.isNullOrEmpty(nickname.replaceAll(" ", ""))) {
            Message msg = Message.obtain();
            msg.what = -1;// invalid nickname
            mHandler.sendMessage(msg);
        } else {
            MyLog.i("new nick=" + nickname);

            UserInfo us = new UserInfo();
            us.setNickname(nickname);
            LoginManager.getInstance().userUpdate(us, new IListener() {
                @Override
                public void onSuccess(Object obj) {
                    MyLog.i("nickname update OK: " + nickname);
                    MinePreference.getInstance().setNickname(nickname);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            nickname_str = nickname;
                            nickname_tv.setText(nickname_str);
                        }
                    });

                }

                @Override
                public void onNoNetwork() {
                    MyLog.i("nickname update NG:" + getString(R.string.no_network));
                    mHandler.sendEmptyMessage(3);
                }

                @Override
                public void onErr(Object object) {
                    MyLog.i("nickname update NG: " + nickname_str);
                    mHandler.sendEmptyMessage(5);
                }
            });
        }
    }

    /**
     *  修改性别
     */
    public void initGenderWindow() {
        View popupWindow_view = this.getLayoutInflater().inflate(R.layout.alter_gender, null);
        popupWindow_view.setPadding(0,0,0, CommonsPreference.getInstance().getVirtualKeyboardHeight());
        alter_sex_male = (TextView) popupWindow_view.findViewById(R.id.alter_sex_male);
        alter_sex_male_selected = (ImageView) popupWindow_view.findViewById(R.id.alter_sex_male_selected);
        alter_sex_female = (TextView) popupWindow_view.findViewById(R.id.alter_sex_female);
        alter_sex_female_selected = (ImageView) popupWindow_view.findViewById(R.id.alter_sex_female_selected);
        alter_age_70.setTextColor(getResources().getColor(R.color.common_text_main));
        MyLog.i("initGenderWindow sex_param_str: " + sex_param_str);
        selectGender();

        alter_sex_cancel = (TextView) popupWindow_view.findViewById(R.id.alter_sex_cancel);
        alter_sex_male.setOnClickListener(this);
        alter_sex_female.setOnClickListener(this);
        alter_sex_cancel.setOnClickListener(this);
        alter_gender_popupWindow = new MyPopWindow(this, popupWindow_view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    private void selectGender() {
        if("1".equals(sex_param_str)){
            selectMale(true);
        } else if("2".equals(sex_param_str)) {
            selectMale(false);
        }
    }

    private void updateSex() {
        final String gender_str = StringUtil.getGender(PersonalInfoActivity.this, sex_param_str, true);

        UserInfo us = new UserInfo();
        us.setSex(StringUtil.getGender(PersonalInfoActivity.this, sex_param_str, false));
        LoginManager.getInstance().userUpdate(us, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                MyLog.i("update sex OK:" + gender_str);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        gender_tv.setText(gender_str);
                    }
                });
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("udpate sex NG:" + getString(R.string.no_network));
                mHandler.sendEmptyMessage(3);
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("update sex NG:" + gender_str);
                mHandler.sendEmptyMessage(5);
            }
        });
    }

    private void selectMale(boolean b) {
        if(b) {
            alter_sex_male.setTextColor(getResources().getColor(R.color.common_text_main));
            alter_sex_male_selected.setVisibility(View.VISIBLE);
            alter_sex_female.setTextColor(getResources().getColor(R.color.common_title_grey));
            alter_sex_female_selected.setVisibility(View.GONE);
        } else {
            alter_sex_male.setTextColor(getResources().getColor(R.color.common_title_grey));
            alter_sex_male_selected.setVisibility(View.GONE);
            alter_sex_female.setTextColor(getResources().getColor(R.color.common_text_main));
            alter_sex_female_selected.setVisibility(View.VISIBLE);
        }
    }

    /**
     *  修改年龄对话框
     */
    private void initAgeWindow() {
        View alter_age_view = this.getLayoutInflater().inflate(R.layout.alter_age, null);
        alter_age_view.setPadding(0,0,0, CommonsPreference.getInstance().getVirtualKeyboardHeight());
        alter_age_70 = (TextView) alter_age_view.findViewById(R.id.alter_age_70);
        alter_age_70_s = (ImageView) alter_age_view.findViewById(R.id.alter_age_70_selected);
        alter_age_80 = (TextView) alter_age_view.findViewById(R.id.alter_age_80);
        alter_age_80_s = (ImageView) alter_age_view.findViewById(R.id.alter_age_80_selected);
        alter_age_85 = (TextView) alter_age_view.findViewById(R.id.alter_age_85);
        alter_age_85_s = (ImageView) alter_age_view.findViewById(R.id.alter_age_85_selected);
        alter_age_90 = (TextView) alter_age_view.findViewById(R.id.alter_age_90);
        alter_age_90_s = (ImageView) alter_age_view.findViewById(R.id.alter_age_90_selected);
        alter_age_95 = (TextView) alter_age_view.findViewById(R.id.alter_age_95);
        alter_age_95_s = (ImageView) alter_age_view.findViewById(R.id.alter_age_95_selected);
        alter_age_00 = (TextView) alter_age_view.findViewById(R.id.alter_age_00);
        alter_age_00_s = (ImageView) alter_age_view.findViewById(R.id.alter_age_00_selected);

        selectAge();

        alter_age_cancel = (TextView) alter_age_view.findViewById(R.id.alter_age_cancel);
        alter_age_cancel.setOnClickListener(this);
        alter_age_70.setOnClickListener(this);
        alter_age_80.setOnClickListener(this);
        alter_age_85.setOnClickListener(this);
        alter_age_90.setOnClickListener(this);
        alter_age_95.setOnClickListener(this);
        alter_age_00.setOnClickListener(this);

        alter_age_popupWindow = new MyPopWindow(this, alter_age_view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    private void updateAge() {

        UserInfo us = new UserInfo();
        us.setAges(age_str.substring(0, 2));
        LoginManager.getInstance().userUpdate(us, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                MyLog.i("udpate age OK:" + age_str);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        age_tv.setText(age_str);
                    }
                });
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("udpate age NG:" + getString(R.string.no_network));
                mHandler.sendEmptyMessage(3);
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("udpate age NG:" + age_str);
                mHandler.sendEmptyMessage(5);
            }
        });
    }

    private boolean isAgeChanged(int age) {
        if(StringUtil.nullToEmpty(age_str).length() < 2) {
            return true;
        }
        int t = 0;
        try {
            t = Integer.parseInt(age_str.substring(0, 2));
        } catch (Exception e) {
            MyLog.e(e);
        }
        return t != age;
    }

    private void selectAge() {
        int age = 0;
        try {
            if(StringUtil.nullToEmpty(age_str).length() < 2) {
                age = -1;
            } else {
                age = Integer.parseInt(age_str.substring(0, 2));
            }
        } catch (NumberFormatException ne) {
            MyLog.e(ne);
        } catch (Exception e) {
            MyLog.e(e);
        }
        alter_age_70.setTextColor(getResources().getColor(R.color.title_color));
        alter_age_70_s.setVisibility(View.GONE);
        alter_age_80.setTextColor(getResources().getColor(R.color.title_color));
        alter_age_80_s.setVisibility(View.GONE);
        alter_age_85.setTextColor(getResources().getColor(R.color.title_color));
        alter_age_85_s.setVisibility(View.GONE);
        alter_age_90.setTextColor(getResources().getColor(R.color.title_color));
        alter_age_90_s.setVisibility(View.GONE);
        alter_age_95.setTextColor(getResources().getColor(R.color.title_color));
        alter_age_95_s.setVisibility(View.GONE);
        alter_age_00.setTextColor(getResources().getColor(R.color.title_color));
        alter_age_00_s.setVisibility(View.GONE);
        switch (age) {
            case 70:
                alter_age_70.setTextColor(getResources().getColor(R.color.common_text_main));
                alter_age_70_s.setVisibility(View.VISIBLE);
                break;
            case 80:
                alter_age_80.setTextColor(getResources().getColor(R.color.common_text_main));
                alter_age_80_s.setVisibility(View.VISIBLE);
                break;
            case 85:
                alter_age_85.setTextColor(getResources().getColor(R.color.common_text_main));
                alter_age_85_s.setVisibility(View.VISIBLE);
                break;
            case 90:
                alter_age_90.setTextColor(getResources().getColor(R.color.common_text_main));
                alter_age_90_s.setVisibility(View.VISIBLE);
                break;
            case 95:
                alter_age_95.setTextColor(getResources().getColor(R.color.common_text_main));
                alter_age_95_s.setVisibility(View.VISIBLE);
                break;
            case 0:
                alter_age_00.setTextColor(getResources().getColor(R.color.common_text_main));
                alter_age_00_s.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     *  修改个性签名对话框
     */
    private void initSignatureWindow() {
        View alter_signature_view = this.getLayoutInflater().inflate(R.layout.alter_signature, null);
        alter_signature_et = (EditText) alter_signature_view.findViewById(R.id.alter_signature);

        alter_signature_cancel = (TextView) alter_signature_view.findViewById(R.id.alter_signature_cancel);
        alter_signature_sure = (TextView) alter_signature_view.findViewById(R.id.alter_signature_sure);
        clear_signature = (ImageView) alter_signature_view.findViewById(R.id.clear_signature);
        remaining_number = (TextView) alter_signature_view.findViewById(R.id.remaining_number);
        alter_signature_cancel.setOnClickListener(this);
        alter_signature_sure.setOnClickListener(this);
        alter_signature_sure.setClickable(false);
        clear_signature.setOnClickListener(this);
        alter_signature_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                /**if (editable.toString() != null && !editable.toString().equals(signature_str)) {
                    alter_signature_sure.setClickable(true);
                } else {
                    alter_signature_sure.setClickable(false);
                }**/
                remaining_number.setText(editable.toString().length() + "/" + signature_max);
                alter_signature_sure.setClickable(true);
            }
        });
        alter_signature_popupWindow = new MyPopWindow(this, alter_signature_view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    private void alterSignature() {
        final String signature = StringUtil.filter(alter_signature_et.getText().toString());
        MyLog.i("new signature=[" + signature + "] is null: " + (signature == null));

        UserInfo us_sgn = new UserInfo();
        us_sgn.setSignature(signature);
        LoginManager.getInstance().userUpdate(us_sgn, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                MyLog.i("update signature OK:" + signature);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        signature_str = signature;
                        signature_tv.setText(signature_str);
                    }
                });
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("udpate signature NG:" + getString(R.string.no_network));
                mHandler.sendEmptyMessage(3);
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("update signature NG:[" + signature + "] is empty:" + (TextUtils.isEmpty(signature)));
                mHandler.sendEmptyMessage(5);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NO_SDCARD:
                ToastUtils.toast(this, R.string.label_no_sdcard);
                break;
            case PHOTO_HRAPH:
                switch (resultCode) {
                    case Activity.RESULT_OK://照相完成点击确定
                        if (!AppTools.existsSDCARD()) {
                            MyLog.v("SD card is not avaiable/writeable right now.");
                            return;
                        }
                        cropAvatar(Uri.fromFile(new File(BitmapUtil.getTmpAvatarCameraPath())));
//                        Message msg = new Message();
//                        msg.what = 1;
//                        msg.arg1 = 1;
//                        msg.obj = BitmapUtil.getAvatarCropPath();
//                        mHandler.sendMessage(msg);
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case PHOTO_RESULT:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Message msg = new Message();
                        msg.what = 1;
                        msg.arg1 = 2;
                        msg.obj = BitmapUtil.getAvatarCropPath();
                        mHandler.sendMessage(msg);
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case CROP_CAMERA:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Message message = new Message();
                        message.what = 1;
                        message.arg1 = 1;
                        message.obj = BitmapUtil.getAvatarCropPath();
                        mHandler.sendMessage(message);
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case ZODIAC_RESULT:
                int tmp = ZODIAC_NONE;
                if(data != null)
                    tmp = data.getIntExtra(KEY_ZODIAC, ZODIAC_NONE);
                if(zodiac_i != tmp && tmp >= 0 && tmp < 12) {

                    UserInfo us_z = new UserInfo();
                    us_z.setStars(tmp + 1);
                    final int z = tmp;
                    LoginManager.getInstance().userUpdate(us_z, new IListener() {
                        @Override
                        public void onSuccess(Object obj) {
                            MyLog.i("update stars OK:" + getString(AlterZodiacActivity.zodiac_name_arr[z]));
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    zodiac_i = z;
                                    zodiac_tv.setText(getString(AlterZodiacActivity.zodiac_name_arr[z]));
                                }
                            });
                        }

                        @Override
                        public void onNoNetwork() {
                            MyLog.i("udpate stars NG:" + getString(R.string.no_network));
                            mHandler.sendEmptyMessage(3);
                        }

                        @Override
                        public void onErr(Object object) {
                            MyLog.i("update stars NG:" + getString(AlterZodiacActivity.zodiac_name_arr[z]));
                            mHandler.sendEmptyMessage(5);
                        }
                    });
                }

                break;
            case CITY_RESULT:
                if(data != null && !StringUtil.isNullOrEmpty((data.getStringExtra(SelectCityActivity.KEY_CITY)))) {
                    UserInfo us = new UserInfo();
                    Location location = new Location();
                    location.setProvince(data.getStringExtra(SelectCityActivity.KEY_PROVINCE));
                    location.setCity(data.getStringExtra(SelectCityActivity.KEY_CITY));
                    location.setStreet(data.getStringExtra(SelectCityActivity.KEY_DISTRICT));
                    us.setLocation(location);
                    final String city_text = getLocation(location);

                    LoginManager.getInstance().userUpdate(us, new IListener() {
                        @Override
                        public void onSuccess(Object obj) {
                            MyLog.i("udpate location OK:" + city_text);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    city_str = city_text;
                                    address_tv.setText(city_str);
                                }
                            });
                        }

                        @Override
                        public void onNoNetwork() {
                            MyLog.i("udpate location NG:" + getString(R.string.no_network));
                            mHandler.sendEmptyMessage(3);
                        }

                        @Override
                        public void onErr(Object object) {
                            MyLog.i("udpate location NG:" + city_text);
                            mHandler.sendEmptyMessage(5);
                        }
                    });
                }
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    ToastUtils.toast(PersonalInfoActivity.this, R.string.invalid_nickname);
                    break;
                case 0:
                    refreshUserInfo();
                    break;
                // 拍照（arg1==1）or剪切头像(arg1==2)返回数据
                case 1:// upload avatar
                    if(msg.obj != null && !StringUtil.isNullOrEmpty(msg.obj.toString())) {
                        final String headimgpath = msg.obj.toString();
                        MyLog.i("update avatar in handler:" + headimgpath + ", arg1:" + msg.arg1);
                        final int arg = msg.arg1;

                        LoginManager.getInstance().userUploadImage(msg.obj.toString(), new IListener() {
                            @Override
                            public void onSuccess(Object obj) {
                                if(obj != null && !StringUtil.isNullOrEmpty(obj.toString())) {
                                    headimgurl_str = obj.toString();
                                    MyLog.i("arg=" + arg + " upload avatar OK:" + headimgurl_str);
//                                    if(arg == 1) {
                                        deleteTmpFile();

                                        saveAvatarByUrl(headimgurl_str);
                                        MyLog.i("saveAvatarByUrl:" + headimgurl_str);
//                                    }

                                }
                            }

                            @Override
                            public void onNoNetwork() {
                                MyLog.i("upload avatar NG:" + getString(R.string.no_network));
                                deleteTmpFile();
                                mHandler.sendEmptyMessage(3);
                            }

                            @Override
                            public void onErr(Object object) {
                                MyLog.i("upload avatar NG:" + headimgpath);
                                deleteTmpFile();
                                mHandler.sendEmptyMessage(4);
                            }
                        });
                    }
                    break;
                // 获取用户信息成功
                case 2:
                    if(msg.obj != null) {
                        UserInfo us = (UserInfo) msg.obj;
                        // 头像
                        try {
                            headimgurl_str = us.getAvatar().getUri();
                            if(headimgurl_str != null) {
                                saveAvatarByUrl(headimgurl_str);
                                MyLog.i("headimgurl="+headimgurl_str);
                                mHandler.post(runnable_avatar);
                            }
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }
                        // 昵称
                        try {
                            nickname_str = StringUtil.nullToEmpty(us.getNickname());
                            nickname_tv.setText(StringUtil.nullToEmpty(us.getNickname()));
                            MinePreference.getInstance().setNickname(StringUtil.nullToEmpty(us.getNickname()));
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }

                        // 性别
                        try {
                            String sex = StringUtil.nullToEmpty(us.getSex());
                            sex_param_str = sex.equals("female") ? "2" : (sex.equals("male") ? "1" :"");
                            gender_tv.setText(StringUtil.getGender(PersonalInfoActivity.this, sex, true));
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }
                        // 年代
                        try {
                            age_str = StringUtil.nullToEmpty(us.getAges());
                            if(age_str.length() > 1) {
                                age_tv.setText(age_str.substring(0, 2) + getString(R.string.i_age_extension));
                            }

                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }

                        // 星座
                        try {
                            zodiac_i = us.getStars() - 1;
                            if(zodiac_i < 0 || zodiac_i > 12) {
//                                zodiac_i = 0;
                                zodiac_tv.setText("");
                            } else {
                                zodiac_tv.setText(AlterZodiacActivity.zodiac_name_arr[zodiac_i]);
                            }
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }
                        // 地址
                        try {
                            address_tv.setText(getLocation(us.getLocation()));
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }
                        // 签名
                        try {
                            signature_str = StringUtil.nullToEmpty(us.getSignature());
                            signature_tv.setText(signature_str);
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }

                    }
                    break;
                case 3:
                    ToastUtils.toast(PersonalInfoActivity.this, R.string.no_network);
                    break;
                case 4:
                    ToastUtils.toast(PersonalInfoActivity.this, R.string.upload_icon_fail);
                    break;
                case 5:
                    ToastUtils.toast(PersonalInfoActivity.this, R.string.connection_failed);
                    break;
            }
        }
    };

    private void deleteTmpFile() {
        File tmp = new File(BitmapUtil.getTmpAvatarCameraPath());
        if(tmp.exists()) {
            tmp.delete();
        }
        File tmp2 = new File(BitmapUtil.getAvatarCropPath());
        if(tmp2.exists()) {
            tmp2.delete();
        }
    }

    private String getLocation(Location location) {
        if(location == null) return "";
        StringBuilder sb = new StringBuilder();
        if(!StringUtil.isNullOrEmpty(location.getCountry())) {
            sb.append(location.getCountry()).append(" ");
        }
        if(!StringUtil.isNullOrEmpty(location.getProvince())) {
            sb.append(location.getProvince()).append(" ");
        }
        if(!StringUtil.isNullOrEmpty(location.getCity()) && !isMunicipality(location.getProvince())) {
            sb.append(location.getCity()).append(" ");
        }

        return sb.toString();
    }

    /**是否是直辖市*/
    private boolean isMunicipality(String province) {
        if(StringUtil.isNullOrEmpty(province))
            return false;
        String[] municipality = getResources().getStringArray(R.array.municipality);
        if(municipality.length > 0) {
            for (int i = 0; i < municipality.length; i++) {
                if(province.startsWith(municipality[i]))
                    return true;
            }

        }
        return false;
    }

    private void saveAvatarByUrl(String headimgurl_str) {
        imageLoader.loadImage(headimgurl_str, BitmapUtil.getImageLoaderOption(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                BitmapUtil.saveUserAvatar(PersonalInfoActivity.this, bitmap);
                mHandler.post(runnable_avatar);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

    }

    Runnable runnable_avatar = new Runnable() {
        @Override
        public void run() {
            MyLog.i("runnable_avatar: " + headimgurl_str);
            imageLoader.displayImage(headimgurl_str, avatar_iv, BitmapUtil.getImageLoaderOption());
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // avatar
            case R.id.avatar_item:
                alter_avatar_popupWindow.showButtom();
                break;
            case R.id.alter_avatar_album:
                alter_avatar_popupWindow.dismiss();
                cropAvatar(null);
                break;
            case R.id.alter_avatar_camera:
                alter_avatar_popupWindow.dismiss();
                doCamera();
                break;
            case R.id.alter_avatar_cancel:
                alter_avatar_popupWindow.dismiss();
                break;
            // nickname
            case R.id.nickname_item:
                alter_nickname_et.setText(nickname_str);
                alter_nickname_et.setSelection(nickname_str != null ? nickname_str.length() : 0);
                alter_nickname_popupWindow.showButtom();
                break;
            case R.id.alter_nickname_cancel:
                alter_nickname_popupWindow.dismiss();
                break;
            case R.id.clear_nickname:
                alter_nickname_et.setText("");
                break;
            case R.id.alter_nickname_sure:
                alter_nickname_popupWindow.dismiss();
                alterNickName();
                break;
            // age
            case R.id.age_item:
                selectAge();
                alter_age_popupWindow.showButtom();
                break;
            case R.id.alter_age_cancel:
                alter_age_popupWindow.dismiss();
                break;
            case R.id.alter_age_70:
                if(isAgeChanged(70)) {
                    age_str = getString(R.string.i_age_70);
                    selectAge();
                    updateAge();
                }
                alter_age_popupWindow.dismiss();
                break;
            case R.id.alter_age_80:
                if(isAgeChanged(80)) {
                    age_str = getString(R.string.i_age_80);
                    selectAge();
                    updateAge();
                }
                alter_age_popupWindow.dismiss();
                break;
            case R.id.alter_age_85:
                if(isAgeChanged(85)) {
                    age_str = getString(R.string.i_age_85);
                    selectAge();
                    updateAge();
                }
                alter_age_popupWindow.dismiss();
                break;
            case R.id.alter_age_90:
                if(isAgeChanged(90)) {
                    age_str = getString(R.string.i_age_90);
                    selectAge();
                    updateAge();
                }
                alter_age_popupWindow.dismiss();
                break;
            case R.id.alter_age_95:
                if(isAgeChanged(95)) {
                    age_str = getString(R.string.i_age_95);
                    selectAge();
                    updateAge();
                }
                alter_age_popupWindow.dismiss();
                break;
            case R.id.alter_age_00:
                if(isAgeChanged(0)) {
                    age_str = getString(R.string.i_age_00);
                    selectAge();
                    updateAge();
                }
                alter_age_popupWindow.dismiss();
                break;
            // gender
            case R.id.gender_item:
                if(!StringUtil.isNullOrEmpty(sex_param_str)) {
                    MyLog.i("show" + sex_param_str);
                    selectGender();
                }
                alter_gender_popupWindow.showButtom();
                break;
            case R.id.alter_sex_male:
                selectMale(true);
                sex_param_str = StringUtil.nullToEmpty("1");
                updateSex();
                alter_gender_popupWindow.dismiss();
                break;
            case R.id.alter_sex_female:
                selectMale(false);
                sex_param_str = StringUtil.nullToEmpty("2");
                updateSex();
                alter_gender_popupWindow.dismiss();
                break;
            case R.id.alter_sex_cancel:
                alter_gender_popupWindow.dismiss();
                break;
            // zodiac
            case R.id.zodiac_item:
                Intent zodiac = new Intent(PersonalInfoActivity.this, AlterZodiacActivity.class);
                zodiac.putExtra(KEY_ZODIAC, zodiac_i);
                startActivityForResult(zodiac, ZODIAC_RESULT);
                break;

            // city
            case R.id.location_item:
                Intent location = new Intent(PersonalInfoActivity.this, SelectCityActivity.class);
                location.putExtra(SelectCityActivity.KEY_CITY, address_tv.getText().toString());
                startActivityForResult(location, CITY_RESULT);
                break;
            // signatrue
            case R.id.signature_item:
                alter_signature_et.setText(signature_str);
                int sign_len = signature_str != null ? signature_str.length() : 0;//getStrLen(signature_str);
                alter_signature_et.setSelection(sign_len);
                remaining_number.setText(sign_len + "/" + signature_max);
                alter_signature_popupWindow.showButtom();
                break;
            case R.id.alter_signature_cancel:
                alter_signature_popupWindow.dismiss();
                break;
            case R.id.clear_signature:
                alter_signature_et.setText("");
                remaining_number.setText("0/" + getResources().getInteger(R.integer.signature_max_len));
                break;
            case R.id.alter_signature_sure:
                alter_signature_popupWindow.dismiss();
                alterSignature();
                break;
            case R.id.back:
                gotoBack();
                break;

            default:
                break;
        }
    }

    private void gotoBack() {
        if(!FROM.equals(FROMMINE)) {
            AppTools.toIntent(PersonalInfoActivity.this, MoinActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            gotoBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
