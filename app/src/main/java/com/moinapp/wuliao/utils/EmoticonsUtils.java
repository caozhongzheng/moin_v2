package com.moinapp.wuliao.utils;

import android.content.Context;

import com.keyboard.bean.EmoticonBean;
import com.keyboard.bean.EmoticonSetBean;
import com.keyboard.db.DBHelper;
import com.keyboard.utils.DefEmoticons;
import com.keyboard.utils.EmoticonsKeyboardBuilder;
import com.keyboard.utils.Utils;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.modules.ipresource.EmojiUtils;
import com.moinapp.wuliao.modules.wowo.WowoPreference;
import com.moinapp.wuliao.modules.wowo.model.AppBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EmoticonsUtils {

    public static final ILogger MyLog = LoggerFactory.getLogger("emj");
    /**
     * 初始化表情数据库
     * @param context
     */
    public static void initEmoticonsDB(final Context context) {
        if (!Utils.isInitDb(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final DBHelper dbHelper = new DBHelper(context);

                    /**
                     * FROM DRAWABLE
                     */
//                    ArrayList<EmoticonBean> emojiArray = ParseData(DefEmoticons.emojiArray, EmoticonBean.FACE_TYPE_NOMAL, ImageBase.Scheme.DRAWABLE);
//                    EmoticonSetBean emojiEmoticonSetBean = new EmoticonSetBean("emoji", 3, 7);
//                    emojiEmoticonSetBean.setIconUri("drawable://icon_emoji");
//                    emojiEmoticonSetBean.setItemPadding(20);
//                    emojiEmoticonSetBean.setVerticalSpacing(10);
//                    emojiEmoticonSetBean.setShowDelBtn(true);
//                    emojiEmoticonSetBean.setEmoticonList(emojiArray);
//                    long emojic = dbHelper.insertEmoticonSet(emojiEmoticonSetBean);
//                    MyLog.i("from drawable emoji count: " + emojic + ", listSize=" + emojiArray.size());

                    /**
                     * FROM ASSETS
                     */
//                    ArrayList<EmoticonBean> xhsfaceArray = ParseData(xhsemojiArray, EmoticonBean.FACE_TYPE_NOMAL, ImageBase.Scheme.ASSETS);
//                    EmoticonSetBean xhsEmoticonSetBean = new EmoticonSetBean("xhs", 3, 7);
//                    xhsEmoticonSetBean.setIconUri("assets://xhsemoji_19.png");
//                    xhsEmoticonSetBean.setItemPadding(20);
//                    xhsEmoticonSetBean.setVerticalSpacing(10);
//                    xhsEmoticonSetBean.setShowDelBtn(true);
//                    xhsEmoticonSetBean.setEmoticonList(xhsfaceArray);
//                    long xhs = dbHelper.insertEmoticonSet(xhsEmoticonSetBean);
//                    MyLog.i("from assets xhs count: " + xhs + ", listSize=" + xhsfaceArray.size());
//
//                    ArrayList<EmoticonBean> dkxfaceArray = ParseData(dkxemojiArray, EmoticonBean.FACE_TYPE_NOMAL, ImageBase.Scheme.ASSETS);
//                    EmoticonSetBean dkxEmoticonSetBean = new EmoticonSetBean("dakaxiu", 2, 4);
//                    dkxEmoticonSetBean.setIconUri("assets://xhsemoji_19.png");
//                    dkxEmoticonSetBean.setItemPadding(20);
//                    dkxEmoticonSetBean.setVerticalSpacing(10);
//                    dkxEmoticonSetBean.setShowDelBtn(true);
//                    dkxEmoticonSetBean.setEmoticonList(dkxfaceArray);
//                    long dkx = dbHelper.insertEmoticonSet(dkxEmoticonSetBean);
//                    MyLog.i("from assets 大咖秀 count: " + dkx + ", listSize=" + dkxfaceArray.size());

                    /**
                     * FROM FILE
                     */
//                    String filePath = Environment.getExternalStorageDirectory() + "/55826b9e0cf2de8602c56d8e";
                    final String filePath = BitmapUtil.BITMAP_EMOJI + "55826b9e0cf2de8602c56d8e";//55826b9e0cf2de8602c56d8e 是zip的名字
                    MyLog.i("filePath=" + filePath);
                    try{
                        FileUtil.unzip(
                                context.getAssets().open("55826b9e0cf2de8602c56d8e.zip"),
                                filePath,
                                new IListener() {
                                    @Override
                                    public void onSuccess(Object obj) {
                                        MyLog.i("unzip success=" + filePath);
                                        XmlUtil xmlUtil = new XmlUtil(context);
                                        EmoticonSetBean bean =  xmlUtil.ParserXml(
                                                xmlUtil.getXmlFromSD(filePath + "/55826b9e0cf2de8602c56d8e.xml"), filePath);
                                        // 也可以在xml中配置,15比较合适
                                        bean.setUid(DefEmoticons.DEFAULT_EMOJISET_UID);
                                        bean.setOrder(10);
                                        bean.setItemPadding(15);
                                        bean.setVerticalSpacing(5);
                                        bean.setIconUri("file://" + filePath + "/55826b9e0cf2de8602c56d8e.j");
                                        int dwx = dbHelper.deleteEmoticonSet(bean.getId());
                                        FileUtil.delAllFilesInFolder(EmojiUtils.getEmjSetFolder(bean.getId()));
                                        long lwx = dbHelper.insertEmoticonSet(bean);
                                        WowoPreference.getInstance().setDefaultEmojisetId("55826b9e0cf2de8602c56d8e");
                                        MyLog.i("from file 55826b9e0cf2de8602c56d8e.zip: " + bean.toString());
                                        MyLog.i("delete count: " + dwx);
                                        MyLog.i("from file 55826b9e0cf2de8602c56d8e.zip count: " + lwx + ", listSize=" + bean.getEmoticonList().size());
                                    }

                                    @Override
                                    public void onNoNetwork() {

                                    }

                                    @Override
                                    public void onErr(Object object) {
                                        MyLog.i("unzip onErr=" + filePath);
                                    }
                                });
                    }catch(IOException e){
                        e.printStackTrace();
                        MyLog.e(e);
                    }


                    /**
                     * FROM HTTP/HTTPS
                     */


                    /**
                     * FROM CONTENT
                     */

                    /**
                     * FROM USER_DEFINED
                     */

                    dbHelper.cleanup();
                    Utils.setIsInitDb(context, true);
                }
            }).start();
        }
    }

    public static EmoticonsKeyboardBuilder getSimpleBuilder(Context context) {

        DBHelper dbHelper = new DBHelper(context);
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = dbHelper.queryEmoticonSet(ClientInfo.getUID()/*"emoji","xhs"*/);//这两个预制表情不要了
        dbHelper.cleanup();

        ArrayList<AppBean> mAppBeanList = new ArrayList<AppBean>();
        String[] funcArray = context.getResources().getStringArray(com.keyboard.view.R.array.apps_func);
        String[] funcIconArray = context.getResources().getStringArray(com.keyboard.view.R.array.apps_func_icon);
        for (int i = 0; i < funcArray.length; i++) {
            AppBean bean = new AppBean();
            bean.setId(i);
            bean.setIcon(funcIconArray[i]);
            bean.setFuncName(funcArray[i]);
            mAppBeanList.add(bean);
        }

        return new EmoticonsKeyboardBuilder.Builder()
                .setEmoticonSetBeanList(mEmoticonSetBeanList)
                .build();
    }

    public static EmoticonsKeyboardBuilder getBuilder(Context context) {

        checkEmojiStatus(context);

        DBHelper dbHelper = new DBHelper(context);
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = dbHelper.queryAllEmoticonSet(ClientInfo.getUID());
        dbHelper.cleanup();

        ArrayList<AppBean> mAppBeanList = new ArrayList<AppBean>();
        String[] funcArray = context.getResources().getStringArray(com.keyboard.view.R.array.apps_func);
        String[] funcIconArray = context.getResources().getStringArray(com.keyboard.view.R.array.apps_func_icon);
        for (int i = 0; i < funcArray.length; i++) {
            AppBean bean = new AppBean();
            bean.setId(i);
            bean.setIcon(funcIconArray[i]);
            bean.setFuncName(funcArray[i]);
            mAppBeanList.add(bean);
        }

        return new EmoticonsKeyboardBuilder.Builder()
                .setEmoticonSetBeanList(mEmoticonSetBeanList)
                .build();
    }

    /**防止用户在手动删除sd卡中的表情图片后，发帖和回复时找不到图*/
    public static void checkEmojiStatus(Context context) {
        DBHelper dbHelper = new DBHelper(context);

        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = dbHelper.queryAllEmoticonSet(ClientInfo.getUID());
        if(mEmoticonSetBeanList == null || mEmoticonSetBeanList.size() < 1) {
            return;
        }
        for (EmoticonSetBean set:mEmoticonSetBeanList) {
            MyLog.i("防止用户在手动删除sd卡中的表情图片后，发帖和回复时找不到图:"+set.getName());
            if(set != null) {
                final String url = set.getIconUrl();
                final String parent = set.getIconUri().replaceFirst("file://", "");
                if(!StringUtil.isNullOrEmpty(set.getId())) {
                    FileUtil.createFolder(parent);
                    File file = new File(parent);
                    if(!file.exists()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MyLog.i("download emojiResource icon: "+parent);
                                MyLog.i("download emojiResource icon from: "+url);
                                HttpUtil.download(url, parent);
                            }
                        }).start();
                    }
                }
                ArrayList<EmoticonBean> beanList = set.getEmoticonList();
                if (beanList != null && beanList.size() > 0) {
                    for (EmoticonBean bean : beanList) {
                        if (bean != null) {
                            final String iconurl = bean.getIconUrl();
                            final String iconuri = bean.getIconUri().replaceFirst("file://", "");
                            final String gifurl = bean.getGifUrl();
                            final String gifuri = bean.getGifUri().replaceFirst("file://", "");

                            File icon = new File(iconuri);
                            if (!icon.exists()) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyLog.i("download emoji icon: " + iconuri);
                                        MyLog.i("download emoji icon from: " + iconurl);
                                        HttpUtil.download(iconurl, iconuri);
                                    }
                                }).start();
                            }
                            File gif = new File(iconuri);
                            if (!gif.exists()) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyLog.i("download emoji gif: " + gifuri);
                                        MyLog.i("download emoji gif from: " + gifurl);
                                        HttpUtil.download(gifurl, gifuri);
                                    }
                                }).start();
                            }
                        }
                    }
                }
            }
        }
        dbHelper.cleanup();
    }
//
//    public static ArrayList<EmoticonBean> ParseData(String[] arry, long eventType, ImageBase.Scheme scheme) {
//        try {
//            ArrayList<EmoticonBean> emojis = new ArrayList<EmoticonBean>();
//            for (int i = 0; i < arry.length; i++) {
//                if (!TextUtils.isEmpty(arry[i])) {
//                    String temp = arry[i].trim().toString();
//                    String[] text = temp.split(",");
//                    if (text != null && text.length == 2) {
//                        String fileName = null;
//                        if (scheme == ImageBase.Scheme.DRAWABLE) {
//                            if(text[0].contains(".")){
//                                fileName = scheme.toUri(text[0].substring(0, text[0].lastIndexOf(".")));
//                            }
//                            else {
//                                fileName = scheme.toUri(text[0]);
//                            }
//                        } else {
//                            fileName = scheme.toUri(text[0]);
//                        }
//                        String content = text[1];
//                        EmoticonBean bean = new EmoticonBean(eventType, fileName, content);
//                        emojis.add(bean);
//                    }
//                }
//            }
//            return emojis;
//        } catch (
//                Exception e
//                )
//
//        {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    /**
    ASSETS表情
     */
//    public static String[] xhsemojiArray = {
//            "xhsemoji_1.png,[无语]",
//            "xhsemoji_2.png,[汗]",
//            "xhsemoji_3.png,[瞎]",
//            "xhsemoji_4.png,[口水]",
//            "xhsemoji_5.png,[酷]",
//            "xhsemoji_6.png,[哭] ",
//            "xhsemoji_7.png,[萌]",
//            "xhsemoji_8.png,[挖鼻孔]",
//            "xhsemoji_9.png,[好冷]",
//            "xhsemoji_10.png,[白眼]",
//            "xhsemoji_11.png,[晕]",
//            "xhsemoji_12.png,[么么哒]",
//            "xhsemoji_13.png,[哈哈]",
//            "xhsemoji_14.png,[好雷]",
//            "xhsemoji_15.png,[啊]",
//            "xhsemoji_16.png,[嘘]",
//            "xhsemoji_17.png,[震惊]",
//            "xhsemoji_18.png,[刺瞎]",
//            "xhsemoji_19.png,[害羞]",
//            "xhsemoji_20.png,[嘿嘿]",
//            "xhsemoji_21.png,[嘻嘻]"
//    };
    public static String[] dkxemojiArray = {
            "dkx_1.png,[制作一个]",
    };

}
