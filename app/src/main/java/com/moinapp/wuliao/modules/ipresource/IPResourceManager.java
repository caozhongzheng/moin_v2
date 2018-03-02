package com.moinapp.wuliao.modules.ipresource;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.keyboard.bean.EmoticonBean;
import com.keyboard.bean.EmoticonSetBean;
import com.keyboard.db.DBHelper;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.banner.Banner;
import com.moinapp.wuliao.commons.db.DataProvider;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.modal.TagInfo;
import com.moinapp.wuliao.commons.moduleframework.AbsManager;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.net.Listener;
import com.moinapp.wuliao.modules.ipresource.model.BannerInfo;
import com.moinapp.wuliao.modules.ipresource.model.EmojiInfo;
import com.moinapp.wuliao.modules.ipresource.model.EmojiResource;
import com.moinapp.wuliao.modules.ipresource.model.IPDetails;
import com.moinapp.wuliao.modules.ipresource.model.IPResource;
import com.moinapp.wuliao.modules.ipresource.network.DownloadEmojiProtocal;
import com.moinapp.wuliao.modules.ipresource.network.FavoriateIPProtocal;
import com.moinapp.wuliao.modules.ipresource.network.GetBannerProtocal;
import com.moinapp.wuliao.modules.ipresource.network.GetEmojiListProtocal;
import com.moinapp.wuliao.modules.ipresource.network.GetHotIPProtocal;
import com.moinapp.wuliao.modules.ipresource.network.GetHotTagProtocal;
import com.moinapp.wuliao.modules.ipresource.network.GetIPDetailProtocal;
import com.moinapp.wuliao.modules.ipresource.network.GetIPListProtocal;
import com.moinapp.wuliao.modules.ipresource.network.GetIPResourceService;
import com.moinapp.wuliao.modules.ipresource.network.LikeResourceProtocal;
import com.moinapp.wuliao.modules.ipresource.network.SearchTagProtocal;
import com.moinapp.wuliao.modules.ipresource.tables.BannerListCache;
import com.moinapp.wuliao.modules.ipresource.tables.EmojiListCacheTable;
import com.moinapp.wuliao.modules.ipresource.tables.IPListCacheTable;
import com.moinapp.wuliao.utils.HttpUtil;
import com.moinapp.wuliao.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liujiancheng on 15/5/12.
 */
public class IPResourceManager extends AbsManager {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final long CACHE_MAX_TIME = 24L * 60 * 60 * 1000;


    // ===========================================================
    // Fields
    // ===========================================================
    private static ILogger MyLog = LoggerFactory.getLogger(IPResourceModule.MODULE_NAME);
    private static IPResourceManager mInstance;
    private Context mContext;
    private Gson mGson = new Gson();

    // ===========================================================
    // Constructors
    // ===========================================================
    private IPResourceManager() {
        mContext = ApplicationContext.getContext();
        init();
    }

    public static synchronized IPResourceManager getInstance() {
        if (mInstance == null) {
            mInstance = new IPResourceManager();
        }

        return mInstance;
    }

    // ===========================================================
    // public methods or interface
    // ===========================================================
    @Override
    public void init() {
        EventBus.getDefault().register(this);
    }

    /**
     * 获取首页banner的接口方法
     * @param listener： callback
     */
    public void getBanner(Listener listener) {
        new GetIPResourceService().GetBannerService(IPResourceConstants.GET_BANNER_URL, listener);
    }

    /**
     * 获取热门标签的接口方法
     * @param type： 资源类型 0 全部； 1 IP； 2 表情专辑 emoji； 3 大咖秀 cosplay； 4 帖子 poster； 5 窝窝 wowo
     * @param listener： callback
     */
    public void getHotTag(int type, Listener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IPResourceConstants.TAG_TYPE, String.valueOf(type));
        new GetIPResourceService().GetHotTagService(IPResourceConstants.GET_HOT_TAG_URL, map, listener);
    }

    /**
     * 搜索标签的接口方法
     * @param type： 资源类型 0 全部； 1 IP； 2 表情专辑 emoji； 3 大咖秀 cosplay； 4 帖子 poster； 5 窝窝 wowo
     * @param tags： 搜索的内容字符串
     * @param listener： callback
     */
    public void SearchtTag(int type, String tags, Listener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IPResourceConstants.TAG_TYPE, String.valueOf(type));
        map.put(IPResourceConstants.SEARCH_CONTENT, tags);
        new GetIPResourceService().SearchTagService(IPResourceConstants.SEARCH_TAG_URL, map, type, listener);
    }

    /**
     * 获取IP列表的接口方法
     * @param lastid: 上次请求的最后一个资源id
     * @param column: 0:获取最moin列表 1:获取最热列表
     * @param listener： callback
     */
    public void getIPList(String lastid, int column, IPresListListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IPResourceConstants.LAST_ID, lastid);
        int sortBy = column == 0 ? 1:2;
        map.put(IPResourceConstants.SORT_BY, String.valueOf(sortBy));
        new GetIPResourceService().getIPListService(IPResourceConstants.GET_IPLIST_URL, column, lastid, map, listener);
    }

    /**
     * 获取IP详情的接口
     *
     * @param id： IP的id
     */
    public void getIPDetails(String id, Listener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IPResourceConstants.IP_ID, id);
        new GetIPResourceService().getIPDetailService(IPResourceConstants.GET_IP_DETAIL, map, listener);
    }

    /**
     * 获热门IP的接口
     * @param listener： callback
     */
    public void getHotIP(Listener listener) {
        new GetIPResourceService().getHotIPService(IPResourceConstants.GET_HOT_IP, listener);
    }

    /**
     * 获取IP详情的接口
     * @param ipid： IP的id*
     * @param emojiid： emoji的id
     */
    public void getEmojiList(String ipid, String emojiid, Listener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IPResourceConstants.IP_ID, ipid);
        map.put(IPResourceConstants.SINGLE_EMOJI_ID, emojiid);
        new GetIPResourceService().getemojiService(IPResourceConstants.GET_EMOJI, map, listener);
    }

    /**
     * 下载表情专辑的接口
     *
     * @param id： 表情的id
     */
    public void downloadEmoji(String id, Listener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IPResourceConstants.EMOJI_ID, id);
        new GetIPResourceService().downloadEmojiService(IPResourceConstants.DOWNLOAD_IP_EMOJI, map, listener);
    }
    /**
     * 开一个线程下载获取到的所有表情列表
     * @param emojiList
     */
    public void downloadAllEmojis(final List<EmojiResource> emojiList) {
        if (emojiList == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (emojiList) {
                    for (EmojiResource emojiResource : emojiList) {
                        try {
                            //下载表情专辑的icon
                            String url = emojiResource.getIcon().getUri();
                            String path = EmojiUtils.getEmjSetPath(emojiResource.getId());
                            HttpUtil.download(url, path);
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }
                        for (EmojiInfo emoji : emojiResource.getEmojis()) {
                            try {
                                //下载gif
                                String url = emoji.getPicture().getUri();
                                String path = EmojiUtils.getEmjPath(emoji);
                                HttpUtil.download(url, path);

                                //下载缩略图
                                url = emoji.getIcon().getUri();
                                path = EmojiUtils.getThumbPath(emoji);
                                HttpUtil.download(url, path);
                            } catch (NullPointerException e) {
                                MyLog.e(e);
                            }
                        }

                    }

                }
            }
        }).start();
    }

    public void addEmojiIntoDB(final String uid, List<EmojiResource> emojiList) {
        for(EmojiResource emojiResource: emojiList) {
            addEmojiIntoDB(uid, emojiResource);
        }
    }
    public void addEmojiIntoDB(final String uid, final EmojiResource emojiResource) {
        // TODO
        MyLog.i("addEmojiIntoDB");
        if(emojiResource == null || emojiResource.getEmojis()==null || emojiResource.getEmojis().size() == 0) {
            MyLog.i("addEmojiIntoDB NG");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper dbHelper = new DBHelper(mContext);
                ArrayList<EmoticonSetBean> q = dbHelper.queryEmoticonSetByID(uid, emojiResource.getId());
                MyLog.i("query q =" + q);
                int count = q == null ? 0 : q.size();
                if(count > 0) {
                    return;
                }
                int delcnt = dbHelper.deleteEmoticonSet(emojiResource.getId());
                if(delcnt > 0) {
                    MyLog.i(emojiResource.getName() + " is deleted before insert: " + delcnt);
                }
                EmoticonSetBean emoticonSetBean = new EmoticonSetBean();
                emoticonSetBean.setId(emojiResource.getId());
                emoticonSetBean.setUid(uid);
                emoticonSetBean.setName(emojiResource.getName());
                emoticonSetBean.setLine(2);
                emoticonSetBean.setRow(4);
                try {
                    emoticonSetBean.setIconUri("file://" + EmojiUtils.getEmjSetPath(emojiResource.getId()));
                    emoticonSetBean.setIconUrl(emojiResource.getIcon().getUri());
                } catch (NullPointerException e) {
                    MyLog.e(e);
                }

                ArrayList<EmoticonBean> emoticonList = new ArrayList<EmoticonBean>();
                EmoticonBean tmp = null;
                for (EmojiInfo emoji : emojiResource.getEmojis()) {
                    tmp = new EmoticonBean();
                    tmp.setParentId(emoticonSetBean.getId());
                    tmp.setEventType(2);
                    tmp.setId(String.valueOf(emoji.getId()));
                    tmp.setTags(StringUtil.list2String(emoji.getTags()));
                    //缩略图
                    tmp.setIconUri("file://" + EmojiUtils.getThumbPath(emoji));
                    try {
                        tmp.setIconUrl(emoji.getIcon().getUri());
                    } catch (Exception e) {
                        MyLog.e(e);
                    }
                    //gif
                    tmp.setGifUri("file://" + EmojiUtils.getEmjPath(emoji));
                    try {
                        tmp.setGifUrl(emoji.getPicture().getUri());
                    } catch (Exception e) {
                        MyLog.e(e);
                    }
                    tmp.setContent("");
                    tmp.setUseStat(0);

                    emoticonList.add(tmp);
                }
                emoticonSetBean.setEmoticonList(emoticonList);

                long cnt = dbHelper.insertEmoticonSet(emoticonSetBean);
                dbHelper.cleanup();
                if(cnt > 0) {
                    MyLog.i(emojiResource.getName() + " is inserted into DB OK");
                } else {
                    MyLog.i(emojiResource.getName() + " is inserted into DB NG");
                }
            }
        }).start();

    }
    /**
     * 收藏IP的接口
     * @param id：ip的id
     * @param action：0，取消收藏 1，收藏
     */
    public void favoriateIP(String id,int action, Listener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IPResourceConstants.FAVORIATE_IP_ID, id);
        map.put(IPResourceConstants.ACTION, String.valueOf(action));
        new GetIPResourceService().favoriateIPService(IPResourceConstants.FAVORIATE_IP, map, listener);
    }

    /**
     * 点赞的接口
     * @param type : 资源类型 1 IPResource 2 表情专辑 EmojiResource 3 大咖秀 EmojiCosplay 4 帖子 Poster
     * @param id ：相应资源的ID，如果type是4，则ID为一个对象，包括woid 和 postid 两个keys
     * @param action ：0，取消点赞 1，点赞
     */
    public void likeResource(int type, String id,int action, Listener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IPResourceConstants.LIKE_TYPE, String.valueOf(type));
        map.put(IPResourceConstants.LIKE_ID, id);
        map.put(IPResourceConstants.ACTION, String.valueOf(action));
        new GetIPResourceService().likeResourceService(IPResourceConstants.LIKE_RESOURCE, map, listener);
    }

    /**
     * 接收到服务器返回的banner列表后进行json解析
     * @param event
     */
    public void onEvent(GetBannerProtocal.GetBannerSuccessEvent event) {
        String response = event.getResponse();
        GetBannerProtocal.GetBannerResponse listBanner  = mGson.fromJson(response, GetBannerProtocal.GetBannerResponse.class);
        if (listBanner!= null) {
            List<BannerInfo> list = listBanner.getBanner();
            if (list != null && list.size() > 0) {
                for ( BannerInfo banner: list) {
                    MyLog.i("banner title:"+ banner.getTitle());
                }
                IListener listener = ((IListener)event.getTag());
                listener.onSuccess(list);

                //save banner到本地数据库
                saveBannerList(list);
            } else {
                ((IListener)event.getTag()).onErr(null);
            }
        } else {
            ((IListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的热门标签列表后进行json解析
     * @param event
     */
    public void onEvent(GetHotTagProtocal.GetHotTagSuccessEvent event) {
        String response = event.getResponse();
        GetHotTagProtocal.GetHotTagResponse listTags  = mGson.fromJson(response, GetHotTagProtocal.GetHotTagResponse.class);
        if (listTags!= null) {
            List<TagInfo> list = listTags.getTaglist();
            if (list != null && list.size() > 0) {
                for ( TagInfo tag: list) {
                    MyLog.i("hot tag item:"+ tag.getName());
                }
                IListener listener = ((IListener)event.getTag());
                listener.onSuccess(list);
            } else {
                ((IListener)event.getTag()).onErr(null);
            }
        } else {
            ((IListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的搜索标签结果后进行json解析
     * @param event
     */
    public void onEvent(SearchTagProtocal.SearchTagSuccessEvent event) {
        String response = event.getResponse();
        SearchTagProtocal.SearchTagResponse resp  = mGson.fromJson(response, SearchTagProtocal.SearchTagResponse.class);
        if (resp!= null) {
            if (resp.getResult() > 0) {
                MyLog.i("search tag result ="+ resp.getResult() + ",type=" + event.getType());
                int type = event.getType();
                IListener listener = ((IListener)event.getTag());
                switch (type) {
                    case 1:
                        listener.onSuccess(resp.getIplist());
                        break;
                    case 2:
                        List<EmojiResource> list = resp.getEmojilist();
                        if (list != null && list.size() > 0) {
                            for (EmojiResource emoji : resp.getEmojilist()) {
                                MyLog.i("emoji:" + emoji.getName());
                            }
                            listener.onSuccess(resp.getEmojilist());
                        } else {
                            listener.onErr(null);
                        }
                        break;
                    case 3:
                        listener.onSuccess(resp.getCosplaylist());
                        break;
                    case 4:
                        listener.onSuccess(resp.getPosterlist());
                        break;
                    case 5:
                        listener.onSuccess(resp.getWowolist());
                        break;
                }
            } else {
                ((IListener)event.getTag()).onErr(null);
            }
        } else {
            ((IListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的ip列表后进行json解析
     * @param event
     */
    public void onEvent(GetIPListProtocal.GetIPListSuccessEvent event) {
        String response = event.getResponse();
        GetIPListProtocal.IPListResource listResource  = mGson.fromJson(response, GetIPListProtocal.IPListResource.class);
        if (listResource!= null) {
            MyLog.i("GetIPlist response: listResource.size = " + listResource.getLength());
            List<IPResource> list = listResource.getIplist();
            if (list != null && list.size() > 0) {
                MyLog.i("get list: real list.size=" + list.size());
                for ( IPResource ip: list) {
                    MyLog.i("get list: id=" + ip.get_id());
                    MyLog.i("get list: name=" + ip.getName());
                }
                IPresListListener listener = ((IPresListListener)event.getTag());
                listener.onGetIPListSucc(event.getmColumn(), event.getmLastId(), list);
                saveIPList(list, event.getmColumn());
            } else {
                ((IPresListListener)event.getTag()).onErr(null);
            }
        } else {
            ((IPresListListener)event.getTag()).onErr(null);
        }

    }

    /**
     * 接收到服务器返回的ip列表后进行json解析
     * @param event
     */
    public void onEvent(GetHotIPProtocal.GetHotIPSuccessEvent event) {
        String response = event.getResponse();
        GetHotIPProtocal.HotIPResponse hotIPResponse  = mGson.fromJson(response, GetHotIPProtocal.HotIPResponse.class);
        if (hotIPResponse!= null) {
            IPResource hotip = hotIPResponse.getHotip();
            if (hotip != null ) {
                hotip.setIsHot(1);
                MyLog.i("get hot ip: id=" + hotip.getName());
                MyLog.i("get hot ip: pic=" + hotip.getPics().getClip().getUri());
                IPresListListener listener = ((IPresListListener)event.getTag());
                listener.onGetHotIPSucc(hotip);
                List<IPResource> list = new ArrayList<IPResource>();
                list.add(hotip);
                saveIPList(list, 2);
            } else {
                ((IPresListListener)event.getTag()).onErr(null);
            }
        } else {
            ((IPresListListener)event.getTag()).onErr(null);
        }

    }

    /**
     * 接收到服务器返回的ip列表后进行json解析
     * @param event
     */
    public void onEvent(GetIPDetailProtocal.GetIPDetailSuccessEvent event) {
        String response = event.getResponse();
        GetIPDetailProtocal.IPDetailResponse res = mGson.fromJson(response, GetIPDetailProtocal.IPDetailResponse.class);
        if (res != null) {
            IPDetails details = res.getIpresource();
            if (details != null) {
                MyLog.i("GetIPDetail succeed: detail.isFavorite = " + details.getIsFavorite());
                updateIPListCache(details);
                ((IListener)event.getTag()).onSuccess(details);
            } else {
                ((IListener)event.getTag()).onErr(null);
            }
        } else {
            ((IListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的表情列表后进行json解析
     * @param event
     */
    public void onEvent(GetEmojiListProtocal.GetEmojiListSuccessEvent event) {
        String response = event.getResponse();
        GetEmojiListProtocal.GetEmojiListResponse res = mGson.fromJson(response, GetEmojiListProtocal.GetEmojiListResponse.class);
        if (res != null) {
            List<EmojiResource> list = res.getEmojilist();
            List<EmojiResource> suggest = res.getSuggest();
            if (list != null && list.size() > 0) {
                for (EmojiResource emoji: list) {
                    MyLog.i("GetEmojiList succeed: emoji.emojiInfo size = " + emoji.getEmojis().size());
                    if (emoji.getIcon() != null)
                        MyLog.i("GetEmojiList succeed: emoji icon = " + emoji.getIcon().getUri());
                }
                ((IPEmojiListListener) event.getTag()).onGetEmojiListSucc(list, suggest);

                //保存到本地数据库
                saveEmojiList(list);
            } else {
                ((IPEmojiListListener)event.getTag()).onErr(null);
            }
        } else {
            ((IPEmojiListListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的下载表情列表的结果
     * @param event
     */
    public void onEvent(DownloadEmojiProtocal.DownloadEmojiSuccessEvent event) {
        String response = event.getResponse();
        DownloadEmojiProtocal.DownloadEmojiResponse res = mGson.fromJson(response, DownloadEmojiProtocal.DownloadEmojiResponse.class);
        if (res != null) {
            ((IListener)event.getTag()).onSuccess(res.getResult());
        } else {
            ((IListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的ip收藏的结果
     * @param event
     */
    public void onEvent(FavoriateIPProtocal.FavoriateIPSuccessEvent event) {
        String response = event.getResponse();
        FavoriateIPProtocal.FavoriateIPResponse res = mGson.fromJson(response, FavoriateIPProtocal.FavoriateIPResponse.class);
        if (res != null) {
            ((IListener)event.getTag()).onSuccess(res.getResult());
        } else {
            ((IListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的点赞的结果
     * @param event
     */
    public void onEvent(LikeResourceProtocal.LikeResourceSuccessEvent event) {
        String response = event.getResponse();
        LikeResourceProtocal.LikeResourceResponse res = mGson.fromJson(response, LikeResourceProtocal.LikeResourceResponse.class);
        if (res != null) {
            ((IListener)event.getTag()).onSuccess(res.getResult());
        } else {
            ((IListener)event.getTag()).onErr(null);
        }
    }


    /**
     * 从本地数据库缓存中获取ip列表
     * @return
     */
    public List<IPResource> getIPListFromCache(int column) {
        List<IPResource> ipList= new ArrayList<IPResource>();
        Cursor cursor = null;
        int COL = column == 0 ? 0x1 : 0x2;
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            cursor = contentResolver.query(IPListCacheTable.IPLIST_CACHE_URI, null,
                    null, null, null);

            if (cursor == null ) {
                return null;
            }

            while (cursor.moveToNext()) {
                int flag = cursor.getInt(cursor.getColumnIndex(IPListCacheTable.IP_FLAG));
                if ((flag & COL) > 0) {
                    String ipJson = cursor.getString(cursor.getColumnIndex(IPListCacheTable.IP_JSON));
                    if (!TextUtils.isEmpty(ipJson)) {
                        IPResource ip = mGson.fromJson(ipJson, IPResource.class);
                        ip.setIsHot(0);
                        if (ip != null) {
                            ipList.add(ip);
                        }
                    }
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return ipList;
    }

    /**
     * 从本地数据库缓存中获取热门ip
     * @return
     */
    public IPResource getHotIPFromCache() {
        Cursor cursor = null;
        IPResource ip = null;
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            cursor = contentResolver.query(IPListCacheTable.IPLIST_CACHE_URI, null,
                    null, null, null);

            if (cursor == null ) {
                return null;
            }

            if (cursor.moveToNext()) {
                int flag = cursor.getInt(cursor.getColumnIndex(IPListCacheTable.IP_FLAG));
                if ((flag & 0x4) > 0) {
                    String ipJson = cursor.getString(cursor.getColumnIndex(IPListCacheTable.IP_JSON));
                    if (!TextUtils.isEmpty(ipJson)) {
                        ip = mGson.fromJson(ipJson, IPResource.class);
                        if (ip != null) {
                            ip.setIsHot(1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return ip;
    }

    /**
     * 从本地数据库缓存中获取banner列表的信息
     */
    public List<BannerInfo> getBannersFromCache() {
        List<BannerInfo> bannerList= new ArrayList<BannerInfo>();
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            cursor = contentResolver.query(BannerListCache.BANNER_CACHE_URI, null,
                    null, null, null);

            if (cursor == null ) {
                return null;
            }

            while (cursor.moveToNext()) {
                String json = cursor.getString(cursor.getColumnIndex(BannerListCache.BANNER_JSON));
                if (!TextUtils.isEmpty(json)) {
                    BannerInfo bannerInfo = mGson.fromJson(json, BannerInfo.class);
                    if (bannerInfo != null) {
                        bannerList.add(bannerInfo);
                    }
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return bannerList;
    }

    /**
     * 判断IP缓存是否过期
     * @param flag： 0表示最moin ip列表 1表示最热门ip列表 2表示首页banner上的热门ip
     * @return
     */
    public boolean isCacheExpired(int flag) {
        Long cacheTime = 0l;
        IPResourcePreference preference = IPResourcePreference.getInstance();

        switch (flag) {
            case 0:
                cacheTime = preference.getLastGetMoinIPTime();
                break;
            case 1:
                cacheTime = preference.getLastGetHotIPTime();
                break;
            case 2:
                cacheTime = preference.getLastGetPrimaryHotIPtIME();
                break;
        }
        Long currentTime = System.currentTimeMillis();
        return currentTime - cacheTime > CACHE_MAX_TIME ;
    }

    /***
     *
     * @param column 0:最moin列表 1:最热门列表 2: 首页最热的一个ip
     * @return
     */
    public boolean hadAvailableIPListCashe(int column) {
        boolean result = false;
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            cursor = contentResolver.query(IPListCacheTable.IPLIST_CACHE_URI, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                result = true;
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return result;
    }

    /**
     * 从本地数据库缓存中获取表情专题列表
     * @return
     */
    public List<EmojiResource> getEmojiListFromCache() {
        List<EmojiResource> emojiList= new ArrayList<EmojiResource>();
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            cursor = contentResolver.query(EmojiListCacheTable.EMOJILIST_CACHE_URI, null,
                    EmojiListCacheTable.UID + " =? ", new String[]{ClientInfo.getUID()}, null);

            if (cursor == null ) {
                return null;
            }

            while (cursor.moveToNext()) {
                String json = cursor.getString(cursor.getColumnIndex(EmojiListCacheTable.EMOJI_JSON));
                if (!TextUtils.isEmpty(json)) {
                    EmojiResource emoji = mGson.fromJson(json, EmojiResource.class);
                    if (emoji != null) {
                        emojiList.add(emoji);
                    }
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return emojiList;
    }

    // ===========================================================
    // private methods
    // ===========================================================
    /**
     * 保存从服务器获取到的banner列表到本地缓存
     * @param list
     */
    private void saveBannerList(List<BannerInfo> list) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder b = null;
            long now = System.currentTimeMillis();

            // 清除banner缓存
            b = ContentProviderOperation.newDelete(
                    BannerListCache.BANNER_CACHE_URI).withSelection(
                    null, null);
            ops.add(b.build());

            for (BannerInfo banner: list) {
                ContentValues values = BannerToContentValues(banner, now);
                b = ContentProviderOperation.newInsert(BannerListCache.BANNER_CACHE_URI).withValues(
                        values);
                ops.add(b.build());
            }
            ApplicationContext.getContext().getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, ops);
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    private ContentValues BannerToContentValues(BannerInfo banner, long now) {
        ContentValues values = null;
        if (banner != null) {
            values = new ContentValues();
            values.put(BannerListCache.BANNER_TYPE, banner.getType());
            values.put(BannerListCache.BANNER_TITLE, banner.getTitle());
            values.put(BannerListCache.BANNER_JSON, mGson.toJson(banner));
            values.put(BannerListCache.BANNER_UPDATE_TIME, now);
        }
        return values;
    }

    /**
     * @param list
     * @param column 0:最moin列表 1:最热门列表 2: 首页最热的一个ip
     */
    private void saveIPList(List<IPResource> list, int column) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder b = null;
            long now = System.currentTimeMillis();
            int COL = getCOL(column);
            switch (column) {
                case 0:
                    IPResourcePreference.getInstance().setKeyLastGetMoinIpTime(now);
                    COL = 1;
                    break;
                case 1:
                    IPResourcePreference.getInstance().setLastGetHotIPTime(now);
                    COL = 2;
                    break;
                case 2:
                    IPResourcePreference.getInstance().setKeyLastGetPrimaryHotIpTime(now);
                    COL = 4;
                    break;
            }

            for (IPResource ip: list) {
                int flag = getIPFlag(ip.get_id());
                if (flag > 0) {
                    ContentValues values = new ContentValues();
                    values.put(IPListCacheTable.IP_FLAG, flag | COL);
                    b = ContentProviderOperation.newUpdate(IPListCacheTable.IPLIST_CACHE_URI)
                            .withValues(values)
                            .withSelection(
                                    IPListCacheTable.IP_ID + " =?", new String[]{ip.get_id()});
                    ops.add(b.build());
                } else {
                    ContentValues values = ipToContentValues(ip, now, column);
                    b = ContentProviderOperation.newInsert(IPListCacheTable.IPLIST_CACHE_URI).withValues(
                            values);
                    ops.add(b.build());
                }
            }
            ApplicationContext.getContext().getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, ops);
        } catch (Exception e) {
            MyLog.e(e);
        }

    }

    private int getCOL(int column) {
        return column == 0 ? 1 : (column == 1 ? 2 : 4);
    }

    private ContentValues ipToContentValues(IPResource ip, long now, int column) {
        ContentValues values = null;
        if (ip != null) {
            values = new ContentValues();
            values.put(IPListCacheTable.IP_ID, ip.get_id());
            values.put(IPListCacheTable.IP_JSON, mGson.toJson(ip));
            values.put(IPListCacheTable.IP_UPDATE_TIME, now);
            values.put(IPListCacheTable.IP_FLAG, getCOL(column));
        }
        return values;
    }

    /**
     * 如果用户获取了某个ip的详情信息，把从服务器获取到的详情更新之数据库中
     * @param detail
     */
    private void updateIPListCache(IPDetails detail) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder b = null;

            ContentValues values = new ContentValues();
            values.put(IPListCacheTable.IP_DETAIL_JSON, mGson.toJson(detail));
            b = ContentProviderOperation.newUpdate(IPListCacheTable.IPLIST_CACHE_URI)
                    .withValues(values)
                    .withSelection(IPListCacheTable.IP_ID + " =? ", new String[]{detail.getId()});
            ops.add(b.build());
            ApplicationContext.getContext().getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, ops);
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    private int getIPFlag(String id) {
        int result = -1;
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            cursor = contentResolver.query(IPListCacheTable.IPLIST_CACHE_URI, null,
                    IPListCacheTable.IP_ID + " =? ", new String[]{id}, null);

            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getInt(cursor.getColumnIndex(IPListCacheTable.IP_FLAG));
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return result;
    }

    /**
     * 保存从服务器获取到的表情列表到本地缓存
     * @param list
     */
    private void saveEmojiList(List<EmojiResource> list) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder b = null;
            long now = System.currentTimeMillis();

            //先复制一个list，防止出现同步问题
            List<EmojiResource> listSave = new ArrayList<EmojiResource>();
            listSave.addAll(list);
            synchronized (listSave) {
                for (EmojiResource emoji : listSave) {
                    boolean exist = isEmojiExist(emoji);
                    MyLog.i("emoji:" + emoji.getName() + " exist =" + exist);
                    if (exist) {
                        ContentValues values = new ContentValues();
                        values.put(EmojiListCacheTable.EMOJI_JSON, mGson.toJson(emoji));
                        values.put(EmojiListCacheTable.EMOJI_UPDATE_TIME, now);
                        b = ContentProviderOperation.newUpdate(EmojiListCacheTable.EMOJILIST_CACHE_URI)
                                .withValues(values)
                                .withSelection(EmojiListCacheTable.EMOJI_ID + " =? AND " +
                                                EmojiListCacheTable.UID + " =? ",
                                        new String[]{emoji.getId(), ClientInfo.getUID()});
                        ops.add(b.build());
                    } else {
                        ContentValues values = emojiToContentValues(emoji, now);
                        b = ContentProviderOperation.newInsert(EmojiListCacheTable.EMOJILIST_CACHE_URI).withValues(
                                values);
                        ops.add(b.build());
                    }
                }
            }
            ApplicationContext.getContext().getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, ops);
        } catch (Exception e) {
            MyLog.e(e);
        }

    }

    private ContentValues emojiToContentValues(EmojiResource emoji, long now) {
        ContentValues values = null;
        if (emoji != null) {
            values = new ContentValues();
            values.put(EmojiListCacheTable.EMOJI_ID, emoji.getId());
            values.put(EmojiListCacheTable.UID, ClientInfo.getUID());
            values.put(EmojiListCacheTable.EMOJI_JSON, mGson.toJson(emoji));
            values.put(EmojiListCacheTable.EMOJI_UPDATE_TIME, now);
        }
        return values;
    }

    /**
     * 查询表情是否已经在数据库中存在
     * @param emoji
     */
    private boolean isEmojiExist(EmojiResource emoji) {
        boolean result = false;
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            cursor = contentResolver.query(EmojiListCacheTable.EMOJILIST_CACHE_URI, null,
                    EmojiListCacheTable.EMOJI_ID + " =?  AND " +
                            EmojiListCacheTable.UID + " =? ",
                    new String[]{emoji.getId(), ClientInfo.getUID()}, null);

            if (cursor != null && cursor.moveToFirst()) {
                result = true;
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return result;
    }


    public List<Banner> ipToBanner(IPResource ip) {
        if(ip == null) return null;
        List<Banner> banners = new ArrayList<Banner>();
        Map<Integer, Banner> map = new HashMap<Integer, Banner>();
        if(ip.getPics() != null) {
            Banner banner = null;
            if(ip.getPics().getCover() != null) {
                banner = new Banner();
                banner.setType(Banner.TYPE_COVER);
                banner.setStrId(ip.get_id());
                banner.setStrName(ip.getName());
                banner.setImageUrl(ip.getPics().getCover().getUri());
                map.put(Banner.TYPE_COVER, banner);
            }
            if(ip.getPics().getClip() != null) {
                banner = new Banner();
                banner.setType(Banner.TYPE_CLIP);
                banner.setStrId(ip.get_id());
                banner.setStrName(ip.getName());
                banner.setImageUrl(ip.getPics().getClip().getUri());
                map.put(Banner.TYPE_CLIP, banner);
            }
            if(ip.getPics().getPoster() != null) {
                banner = new Banner();
                banner.setType(Banner.TYPE_POSTER);
                banner.setStrId(ip.get_id());
                banner.setStrName(ip.getName());
                banner.setImageUrl(ip.getPics().getPoster().getUri());
                map.put(Banner.TYPE_POSTER, banner);
            }
            if(ip.getPics().getScreen() != null) {
                banner = new Banner();
                banner.setType(Banner.TYPE_SCREEN);
                banner.setStrId(ip.get_id());
                banner.setStrName(ip.getName());
                banner.setImageUrl(ip.getPics().getScreen().getUri());
                map.put(Banner.TYPE_SCREEN, banner);
            }
            if(ip.getPics().getEmoji() != null) {
                banner = new Banner();
                banner.setType(Banner.TYPE_EMOJI);
                banner.setStrId(ip.get_id());
                banner.setStrName(ip.getName());
                banner.setImageUrl(ip.getPics().getEmoji().getUri());
                map.put(Banner.TYPE_EMOJI, banner);
            }
            //排序
            List list = new ArrayList(map.keySet());
            Object[] ary = list.toArray();
            Arrays.sort(ary);
            list = Arrays.asList(ary);
            for (Object o : list) {
                banners.add(map.get(o));
                MyLog.i(o.toString() + "/" + map.get(o).toString());
            }
        }

        return banners;
    }

    public static String getIpTypeName(Context context, int type) {
        String result;
        switch (type) {
            case IPResourceConstants.TYPE_TV:
                result = context.getString(R.string.ip_type_tv);
                break;
            case IPResourceConstants.TYPE_ZY:
                result = context.getString(R.string.ip_type_variety_show);
                break;
            default:
            case IPResourceConstants.TYPE_MV:
                result = context.getString(R.string.ip_type_movie);
                break;
        }
        return result;
    }

    public void playWebVideo(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        MyLog.v("playWebVideo: URI:::::::::" + url);
        intent.setData(Uri.parse(url));
//        intent.setDataAndType(Uri.parse(url), "video/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
