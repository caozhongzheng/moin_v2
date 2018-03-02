package com.moinapp.wuliao.modules.wowo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.db.DataProvider;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moduleframework.AbsManager;
import com.moinapp.wuliao.modules.ipresource.tables.IPListCacheTable;
import com.moinapp.wuliao.modules.wowo.model.CommentInfo;
import com.moinapp.wuliao.modules.wowo.model.Emoji;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;
import com.moinapp.wuliao.modules.wowo.model.WowoInfo;
import com.moinapp.wuliao.modules.wowo.network.GetIPPostProtocal;
import com.moinapp.wuliao.modules.wowo.network.GetMyWoListProtocal;
import com.moinapp.wuliao.modules.wowo.network.GetPostDetailProtocal;
import com.moinapp.wuliao.modules.wowo.network.GetSuggestPostProtocal;
import com.moinapp.wuliao.modules.wowo.network.GetSuggestWoListProtocal;
import com.moinapp.wuliao.modules.wowo.network.GetWoPostProtocal;
import com.moinapp.wuliao.modules.wowo.network.GetWoTagProtocal;
import com.moinapp.wuliao.modules.wowo.network.NewPostProtocal;
import com.moinapp.wuliao.modules.wowo.network.ReplyPostProtocal;
import com.moinapp.wuliao.modules.wowo.network.SubscribeWoProtocal;
import com.moinapp.wuliao.modules.wowo.network.UploadImageProtocal;
import com.moinapp.wuliao.modules.wowo.network.WowoNetService;
import com.moinapp.wuliao.modules.wowo.tables.WoListCacheTable;
import com.moinapp.wuliao.modules.wowo.tables.WoSuggestPostTable;
import com.moinapp.wuliao.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liujiancheng on 15/6/8.
 * 窝窝相关的manager业务逻辑处理类
 */
public class WowoManager  extends AbsManager {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final long CACHE_MAX_TIME = 24L * 60 * 60 * 1000;


    // ===========================================================
    // Fields
    // ===========================================================
    private static ILogger MyLog = LoggerFactory.getLogger(WowoModule.MODULE_NAME);
    private static WowoManager mInstance;
    private Context mContext;
    private Gson mGson = new Gson();

    // ===========================================================
    // Constructors
    // ===========================================================
    private WowoManager() {
        mContext = ApplicationContext.getContext();
        init();
    }

    public static synchronized WowoManager getInstance() {
        if (mInstance == null) {
            mInstance = new WowoManager();
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
     * 获取已经关注的窝列表的接口方法
     * @param listener： callback
     */
    public void getMyWoList(IWoListener listener) {
        new WowoNetService().getMyWoListService(WowoContants.GET_MY_WO_URL, listener);
    }

    /**
     * 获取未关注的窝列表的接口方法
     * @param lastid: 上一次获取到的窝的id
     * @param listener： callback
     */
    public void getSuggestWoList(int lastid, IWoListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        if (lastid == 0) {
            map.put(WowoContants.LAST_ID, null);
        } else {
            map.put(WowoContants.LAST_ID, String.valueOf(lastid));
        }
        new WowoNetService().getSuggestWoListService(WowoContants.GET_SUGGEST_WO_URL, map, listener);
    }

    /**
     * 获取关注一个窝的接口方法
     * @param woid: 窝的id
     * @param action: 关注或者取消关注 0 取消关注 1 关注
     * @param listener： callback
     */
    public void subscribeWo(int woid, int action, IWoListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(WowoContants.WO_ID, String.valueOf(woid));
        map.put(WowoContants.WO_ACTION, String.valueOf(action));
        new WowoNetService().subScribeWoService(WowoContants.SUBSCRIBE_WO_URL, map, listener);
    }

    /**
     * 获取推荐的帖子列表的接口方法
     * @param woid: 最后获取到的帖子所在窝的id
     * @param postid: 最后获取到的帖子的id
     * @param from 0 IP首页，返回内容需要包括ip对象和emoji对象， 1 窝窝页面，不返回ip对象和emoji对象
     * @param listener： callback
     */
    public void getSuggestPostList(int woid, String postid, int from, IPostListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        if (woid == 0 || postid == null) {
            map.put(WowoContants.LAST_ID, null);
        } else {
            LastId lastId = new LastId();
            lastId.woid = woid;
            lastId.postid = postid;
            map.put(WowoContants.LAST_ID, mGson.toJson(lastId));
        }
        map.put(WowoContants.FROM, String.valueOf(from));
        new WowoNetService().getSuggestPostService(WowoContants.GET_SUGGEST_POST_URL, map, listener);
    }

    /**
     * 获取窝里面帖子列表的接口方法
     * @param woid: 窝的id
     * @param lastid: 最后获取到的帖子的id, 用于分页
     * @param filter: filter 过滤条件， 0 全部贴， 1 精华贴， 2 表情贴
     * @param listener： callback
     */
    public void getWoPostList(int woid, String lastid, int filter, IPostListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(WowoContants.WO_ID, String.valueOf(woid));
        map.put(WowoContants.LAST_ID, lastid);
        map.put(WowoContants.POST_FILTER, String.valueOf(filter));
        new WowoNetService().getWoPostService(WowoContants.GET_WO_POST_URL, map, filter, listener);
    }

    /**
     * 获取指定IP帖子的接口方法
     * @param ipid
     * @param listener： callback
     */
    public void getIPPostList(String ipid, IPostListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(WowoContants.IPID, ipid);
        new WowoNetService().getIPPostService(WowoContants.GET_IP_POST_URL, map, listener);
    }

    /**
     * 发表新帖子的接口方法
     * @param woid: 发帖所在窝的id
     * @param title: 帖子标题
     * @param content: 帖子内容
     * @param tag: 帖子tag
     * @param pics: 帖子图片的id，注意必须要先调用uploadpic接口上传图片后得到图片id，注意最多5张图片
     * @param emoji: 帖子中的表情对象，最多一个表情
     * @param listener： callback
     */
    public void newPost(int woid, String title, String content, String tag, List<String>pics, Emoji emoji, IPostListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(WowoContants.WO_ID, String.valueOf(woid));
        map.put(WowoContants.POST_TITLE, title);
        map.put(WowoContants.POST_CONTENT, content);
        map.put(WowoContants.POST_TAG, tag);
        map.put(WowoContants.POST_PICS, StringUtil.list2String(pics));
        if (emoji != null) {
            map.put(WowoContants.POST_EMOJI, mGson.toJson(emoji));
        }
        new WowoNetService().newPostService(WowoContants.NEW_POST_URL, map, listener);
    }

    /**
     * 上传发帖图片的接口方法
     * @param filePath: 图片文件的路径
     * @param listener： callback
     */
    public void uploadImage(String filePath, IPostListener listener) {
        new WowoNetService().UploadFileService(WowoContants.UPLOAD_IMAGE_URL, filePath, listener);
    }

    /**
     * 回复帖子的接口方法
     * @param woid: 发帖所在窝的id
     * @param postid: 帖子id
     * @param content: 回复内容
     * @param pics: 帖子图片的id，注意必须要先调用uploadpic接口上传图片后得到图片id
     * @param emoji: 帖子中的表情对象
     * @param commentInfo: 引用的CommentInfo
     * @param listener： callback
     */
    public void replyPost(int woid, String postid, String content, String pics, Emoji emoji, CommentInfo commentInfo, IPostListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(WowoContants.WO_ID, String.valueOf(woid));
        map.put(WowoContants.POST_ID, postid);
        map.put(WowoContants.POST_CONTENT, content);
        map.put(WowoContants.POST_PICS, pics);
        if (emoji != null) {
            map.put(WowoContants.POST_EMOJI, mGson.toJson(emoji));
        }
        if(commentInfo != null) {
            map.put(WowoContants.POST_REFERRAL, String.valueOf(commentInfo.getCid()));
            map.put(WowoContants.POST_META, commentInfo.getContent());
        }
        new WowoNetService().ReplyPostService(WowoContants.REPLY_POST_URL, map, listener);
    }

    /**
     * 获取帖子详情的接口方法
     * @param woid: 发帖所在窝的id
     * @param postid: 帖子id
     * @param lastid: 引用的楼号,当前已经获取到的最后一个回复楼号，用于分页显示
     * @param listener： callback
     */
    public void getPostDetail(int woid, String postid, int lastid, IPostListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(WowoContants.WO_ID, String.valueOf(woid));
        map.put(WowoContants.POST_ID, postid);
        if (lastid == 0) {
            map.put(WowoContants.LAST_ID, null);
        } else {
            map.put(WowoContants.LAST_ID, String.valueOf(lastid));
        }
        new WowoNetService().getPostDetailService(WowoContants.GET_POST_URL, map, listener);
    }

    /**
     * 获取帖子详情的接口方法
     * @param woid: 发帖所在窝的id
     * @param listener： callback
     */
    public void getWoTag(int woid, WoTagListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(WowoContants.WO_ID, String.valueOf(woid));
        new WowoNetService().getWoTagService(WowoContants.GET_WO_TAG, map, listener);
    }
    // ===========================================================
    // 获取服务器接口成功后的回调
    // ===========================================================
    /**
     * 接收到服务器返回的已经关注的窝列表后进行json解析
     * @param event
     */
    public void onEvent(GetMyWoListProtocal.GetMyWoListSuccessEvent event) {
        String response = event.getResponse();
        GetMyWoListProtocal.GetMyWoListResponse res = mGson.fromJson(response, GetMyWoListProtocal.GetMyWoListResponse.class);
        if (res != null) {
            processWowoList(res.getWowos(), 0, (IWoListener) event.getTag());
        } else {
            ((IWoListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的未关注的窝列表后进行json解析
     * @param event
     */
    public void onEvent(GetSuggestWoListProtocal.GetSuggestWoListSuccessEvent event) {
        String response = event.getResponse();
        GetSuggestWoListProtocal.GetMyWoListResponse res = mGson.fromJson(response, GetSuggestWoListProtocal.GetMyWoListResponse.class);
        if (res != null) {
            processWowoList(res.getWowos(), 1, (IWoListener) event.getTag());
        } else {
            ((IWoListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的关注窝的结果进行json解析
     * @param event
     */
    public void onEvent(SubscribeWoProtocal.SubscribeWoSuccessEvent event) {
        String response = event.getResponse();
        SubscribeWoProtocal.SubscribeWoResponse res = mGson.fromJson(response, SubscribeWoProtocal.SubscribeWoResponse.class);
        if (res != null) {
            int result = res.getResult();
            if (result > 0) {
                ((IWoListener) event.getTag()).onSubscribeResult(result);
            } else  {
                ((IWoListener) event.getTag()).onErr(res.getError());
            }
        } else {
            ((IWoListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的推荐帖子列表后进行json解析
     * @param event
     */
    public void onEvent(GetSuggestPostProtocal.GetSuggestPostSuccessEvent event) {
        String response = event.getResponse();
        GetSuggestPostProtocal.GetPostListResponse res = mGson.fromJson(response, GetSuggestPostProtocal.GetPostListResponse.class);
        if (res != null) {
            processSuggestPostList(res.getPostInfos(), 0, (IPostListener) event.getTag());
        } else {
            ((IPostListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的窝中帖子列表后进行json解析
     * @param event
     */
    public void onEvent(GetWoPostProtocal.GetWoPostSuccessEvent event) {
        String response = event.getResponse();
        GetWoPostProtocal.GetWoPostListResponse res = mGson.fromJson(response, GetWoPostProtocal.GetWoPostListResponse.class);
        if (res != null) {
            processWoPostList(res.getPostInfos(), res.getWowo(), event.getFilter(), (IPostListener) event.getTag());
        } else {
            ((IPostListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的指定IP的热门帖子后进行json解析
     * @param event
     */
    public void onEvent(GetIPPostProtocal.GetIPPostSuccessEvent event) {
        String response = event.getResponse();
        GetIPPostProtocal.GetIPPostResponse res = mGson.fromJson(response, GetIPPostProtocal.GetIPPostResponse.class);
        if (res != null) {
            ((IPostListener)event.getTag()).onGetIPPostSucc(res.getPosts());
        } else {
            ((IPostListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的发帖结果后进行json解析
     * 成功后回调窝id和帖子id
     * @param event
     */
    public void onEvent(NewPostProtocal.NewPostSuccessEvent event) {
        String response = event.getResponse();
        NewPostProtocal.NewPostResponse res = mGson.fromJson(response, NewPostProtocal.NewPostResponse.class);
        if (res != null) {
            if (res.getResult() > 0) {
                // 回调包含窝id，帖子id
                ((IPostListener)event.getTag()).onNewPostSucc(res.getWoid(), res.getPostid());
            } else {
                ((IPostListener)event.getTag()).onErr(res.getError());
            }

        } else {
            ((IPostListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的上传文件结果后进行json解析
     * @param event
     */
    public void onEvent(UploadImageProtocal.GetUploadImageSuccessEvent event) {
        String response = event.getResponse();
        UploadImageProtocal.UploadImageResult res = mGson.fromJson(response, UploadImageProtocal.UploadImageResult.class);
        if (res != null) {
            if (res.getResult() > 0) {
                // 回调包含图片上传成功后生成的图片id，这个id要用于发帖或回帖
                ((IPostListener)event.getTag()).onUploadImageSucc(res.getPicid());
            } else {
                ((IPostListener)event.getTag()).onErr(res.getError());
            }

        } else {
            ((IPostListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的回复帖子结果后进行json解析
     * 成功后回调窝id和帖子id
     * @param event
     */
    public void onEvent(ReplyPostProtocal.ReplyPostSuccessEvent event) {
        String response = event.getResponse();
        ReplyPostProtocal.ReplyPostResponse res = mGson.fromJson(response, ReplyPostProtocal.ReplyPostResponse.class);
        if (res != null) {
            if (res.getResult() > 0) {
                // 回调包含回复成功后生成的楼号
                ((IPostListener)event.getTag()).onReplyPostSucc(res.getCommentid());
            } else {
                ((IPostListener)event.getTag()).onErr(res.getError());
            }

        } else {
            ((IPostListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的帖子详情结果后进行json解析
     * 成功后回调窝id和帖子id
     * @param event
     */
    public void onEvent(GetPostDetailProtocal.GetPostDetailSuccessEvent event) {
        String response = event.getResponse();
        GetPostDetailProtocal.GetPostDetailResponse res = mGson.fromJson(response, GetPostDetailProtocal.GetPostDetailResponse.class);
        if (res != null) {
            // 回调包含主贴信息和回复信息
            ((IPostListener) event.getTag()).onGetPostSucc(res.getPost(), res.getComments());
        } else {
            ((IPostListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 接收到服务器返回的窝的标签列表
     * 成功后回调标签列表
     * @param event
     */
    public void onEvent(GetWoTagProtocal.GetWoTagSuccessEvent event) {
        String response = event.getResponse();
        GetWoTagProtocal.GetWoTagResponse res = mGson.fromJson(response, GetWoTagProtocal.GetWoTagResponse.class);
        if (res != null) {
            ((WoTagListener) event.getTag()).onGetWoTagSucc(res.getTags());
        } else {
            ((WoTagListener)event.getTag()).onErr(null);
        }
    }
    
    // ===========================================================
    // private methods
    // ===========================================================
    /**
     * 处理获取到窝列表后的回调
     * @param wowoInfoList
     * @param group: 窝的分组： 0是我的窝窝 1是推荐的窝窝
     * @param listener
     */
    private void processWowoList(List<WowoInfo> wowoInfoList, int group, IWoListener listener) {
        if (wowoInfoList != null) {
            MyLog.i("GetWoList succeed: size = " + wowoInfoList.size());
            for (WowoInfo wo:wowoInfoList) {
//                MyLog.i("Wo: = " + wo.getName() + "," + wo.getIntro() + "," + wo.getIcon().getUri());
            }
            listener.onGetWowoSucc(wowoInfoList);
            saveWoList(wowoInfoList, group);
        } else {
            listener.onErr(null);
        }

    }

    /**
     * 处理获取到推荐帖子列表后的回调
     * @param postInfos
     * @param listener
     */
    private void processSuggestPostList(List<PostInfo> postInfos, int column, IPostListener listener) {
        if (postInfos != null) {
            MyLog.i("GetPostList succeed: size = " + postInfos.size());
            for (PostInfo post:postInfos) {
                MyLog.i("Wo: = " + post.getWoname() + ",woid:" + post.getWoid() + ",postid:" + post.getPostid() + ", userCount:" + post.getUserCount());
            }
            listener.onGetPostListSucc(postInfos, column);
        } else {
            listener.onErr(null);
        }

    }

    /**
     * 处理获取到窝帖子列表后的回调
     * @param postInfos
     * @param listener
     */
    private void processWoPostList(List<PostInfo> postInfos, WowoInfo woinfo, int column, IPostListener listener) {
        if (postInfos != null || woinfo != null) {
            MyLog.i("GetPostList succeed: size = " + postInfos.size());
            for (PostInfo post:postInfos) {
                MyLog.i("Wo: = " + post.getWoname() + ",woid:" + post.getWoid() + ",postid:" + post.getPostid() + ", userCount:" + post.getUserCount());
            }
            listener.onGetWoPostListSucc(postInfos, woinfo, column);
        } else {
            listener.onErr(null);
        }

    }

    /**
     * 窝的id和最后一个帖子的id组成的lastid对象
     */
    private class LastId {
        public int woid;
        public String postid;
    }

    /**
     * @param from: 获取推荐帖子的来源： 0是ip首页，1是窝窝频道的推荐帖子页面
     * @return
     */
    public List<PostInfo> getSuggestPostFromCache(int from) {
        List<PostInfo> postList= new ArrayList<PostInfo>();
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            cursor = contentResolver.query(WoSuggestPostTable.WOSUGGEST_POST_CACHE_URI, null,
                    WoSuggestPostTable.FROM + " = " + from, null, null);

            if (cursor == null ) {
                return null;
            }

            while (cursor.moveToNext()) {
                String json = cursor.getString(cursor.getColumnIndex(WoSuggestPostTable.POST_JSON));
                if (!TextUtils.isEmpty(json)) {
                    PostInfo post = mGson.fromJson(json, PostInfo.class);
                    if (post != null) {
                        postList.add(post);
                    }
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return postList;
    }

    private ContentValues postToContentValues(PostInfo post, long now, int from) {
        ContentValues values = null;
        if (post != null) {
            values = new ContentValues();
            values.put(WoSuggestPostTable.POST_ID, post.getPostid());
            values.put(WoSuggestPostTable.WO_ID, post.getWoid());
            values.put(WoSuggestPostTable.POST_JSON, mGson.toJson(post));
            values.put(WoSuggestPostTable.UPDATE_TIME, now);
            values.put(WoSuggestPostTable.FROM, from);
        }
        return values;
    }

    private boolean isPostExist(String postid, int from) {
        boolean result = false;
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            cursor = contentResolver.query(WoSuggestPostTable.WOSUGGEST_POST_CACHE_URI, null,
                    WoSuggestPostTable.POST_ID + " =? AND " + WoSuggestPostTable.FROM +  " = " + from,
                    new String[]{postid}, null);

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
     * @param list
     * @param from: 获取推荐帖子的来源： 0是ip首页，1是窝窝频道的推荐帖子页面
     */
    public void saveSuggestPostList(List<PostInfo> list, int from) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder b = null;
            long now = System.currentTimeMillis();

            for (PostInfo post: list) {
                if (!isPostExist(post.getPostid(), from)) {
                    ContentValues values = postToContentValues(post, now, from);
                    b = ContentProviderOperation.newInsert(WoSuggestPostTable.WOSUGGEST_POST_CACHE_URI).withValues(
                            values);
                    ops.add(b.build());
                }
            }
            ApplicationContext.getContext().getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, ops);
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    /**
     * 帖子是否已经在列表中存在的判断
     */
    public boolean isPostInList(PostInfo post, List<PostInfo> list){
        if (list == null || list.size() == 0) {
            return false;
        }
        for (PostInfo item:list) {
            if (item.getPostid().equalsIgnoreCase(post.getPostid())) {
                return true;
            }
        }
        return false;
    }

    private ContentValues woToContentValues(WowoInfo wo, long now, int group) {
        ContentValues values = null;
        if (wo != null) {
            values = new ContentValues();
            values.put(WoListCacheTable.WO_ID, wo.getId());
            values.put(WoListCacheTable.UID, ClientInfo.getUID());
            values.put(WoListCacheTable.WO_JSON, mGson.toJson(wo));
            values.put(WoListCacheTable.UPDATE_TIME, now);
            values.put(WoListCacheTable.GROUP, group);
        }
        return values;
    }

    /**
     * @param group: 窝分组信息，0是我的窝，1是推荐的窝
     * @return
     */
    public List<WowoInfo> getWoInfoFromCache(int group) {
        List<WowoInfo> woList= new ArrayList<WowoInfo>();
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            cursor = contentResolver.query(WoListCacheTable.WO_LIST_CACHE_URI, null,
                    WoListCacheTable.UID + " =? AND " + WoListCacheTable.GROUP + " =" + group,
                    new String[]{ClientInfo.getUID()}, null, null);

            if (cursor == null ) {
                return null;
            }

            while (cursor.moveToNext()) {
                String json = cursor.getString(cursor.getColumnIndex(WoListCacheTable.WO_JSON));
                if (!TextUtils.isEmpty(json)) {
                    WowoInfo wo = mGson.fromJson(json, WowoInfo.class);
                    if (wo != null) {
                        woList.add(wo);
                    }
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return woList;
    }

    private boolean isWoExist(int woid, int group) {
        boolean result = false;
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            cursor = contentResolver.query(WoListCacheTable.WO_LIST_CACHE_URI, null,
                    WoListCacheTable.WO_ID + " = " + woid +  " AND " + WoListCacheTable.GROUP +  " = " + group
                    + " AND " + WoListCacheTable.UID + " =? " ,
                    new String[]{ClientInfo.getUID()}, null);

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
     * @param list
     * @param group: 窝分组信息，0是我的窝，1是推荐的窝
     */
    public void saveWoList(List<WowoInfo> list, int group) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder b = null;
            long now = System.currentTimeMillis();

            for (WowoInfo wo: list) {
                if (!isWoExist(wo.getId(), group)) {
                    ContentValues values = woToContentValues(wo, now, group);
                    b = ContentProviderOperation.newInsert(WoListCacheTable.WO_LIST_CACHE_URI).withValues(
                            values);
                    ops.add(b.build());
                }
            }
            ApplicationContext.getContext().getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, ops);
        } catch (Exception e) {
            MyLog.e(e);
        }
    }
}
