package com.moinapp.wuliao.modules.ipresource.network;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpPostProtocal;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.modules.ipresource.IPResourceModule;
import com.moinapp.wuliao.modules.ipresource.IPresListListener;
import com.moinapp.wuliao.modules.ipresource.model.IPResource;

import java.util.List;
import java.util.Map;

/**
 * Created by liujiancheng on 15/5/12.
 * 获取IP列表的服务器接口
 */
public class GetIPListProtocal extends HttpPostProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger(IPResourceModule.MODULE_NAME);
    private int mColumn;
    private String mLastId;

    // ===========================================================
    // Constructors
    // ===========================================================
    public GetIPListProtocal(String url, int column, String lastid, Map data, Object tag) {
        super(url, data);
        setTag(tag);
        mColumn = column;
        mLastId = lastid;
    }

    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    protected HttpObserver getCallback() {
        return this;
    }

    @Override
    public void onHttpResult(int resultCode, String respone) {
        MyLog.i("GetIPListProtocal.onHttpResult()... resultCode=" + resultCode + ", response=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new GetIPListSuccessEvent(respone, getTag(), mColumn, mLastId));
        } else {
            Object object = getTag();
            if (object != null) {
                if (resultCode == NetConstants.NONE_NETWORK) {
                    ((IPresListListener) object).onNoNetwork();
                } else {
                    ((IPresListListener) object).onErr(resultCode);
                }
            }
        }
    }

    // ===========================================================
    // inner class
    // ===========================================================
    public static class GetIPListSuccessEvent extends AbsProtocolEvent {
        private String mResponse;
        private int mColumn;
        private String mLastId;

        public GetIPListSuccessEvent(String resp, Object tag, int column, String lastid) {
            setTag(tag);
            mResponse = resp;
            mColumn = column;
            mLastId = lastid;
        }

        public String getResponse() {
            return mResponse;
        }

        public int getmColumn() {
            return mColumn;
        }

        public String getmLastId() {
            return mLastId;
        }
    }

    /**
     * Created by liujiancheng on 15/5/12.
     * IP资源的模型类，注意每个成员的命名保持和服务端数据库一致
     */
    public static class IPListResource {
        private int length;
        public List<IPResource> iplist;

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public List<IPResource> getIplist() {
            return iplist;
        }

        public void setIplist(List<IPResource> iplist) {
            this.iplist = iplist;
        }
    }

    // ===========================================================
    // interface
    // ===========================================================
    @Override
    protected void beforeCall() {

    }

    @Override
    protected void afterCall() {

    }

    @Override
    protected void onError() {

    }
}