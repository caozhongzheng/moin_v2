package com.moinapp.wuliao.modules.ipresource;

import android.os.Handler;
import android.view.View;

import com.moinapp.wuliao.commons.banner.BannerUI;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.modules.ipresource.model.BannerInfo;

import java.util.List;

/**
 * Created by moying on 15/5/15.
 */
public class IPHotBannerUI extends BannerUI {

    private ILogger MyLog = LoggerFactory.getLogger(IPResourceModule.MODULE_NAME);
    IPResourceManager manager = IPResourceManager.getInstance();

    List<BannerInfo> mBanners;
//    IPResource hotIp;

    public IPHotBannerUI(View view, Handler handler) {
        super(view, handler);
    }

//    public void setHotIp(IPResource hotIp) {
//        this.hotIp = hotIp;
//    }
    public void setBanners(List<BannerInfo> banners) {
        this.mBanners = banners;
    }
    @Override
    public void loadBanners() {
        if(mBanners != null) {
            showBanner();
        }
        onRefreshing();

        manager.getBanner(new IListener() {
            @Override
            public void onSuccess(Object obj) {
                mBanners = (List<BannerInfo>) obj;
                showBanner();
            }

            @Override
            public void onNoNetwork() {
                runOnGetFailed();
            }

            @Override
            public void onErr(Object object) {
                runOnGetFailed();
            }
        });

    }

    private void showBanner() {
        if (mBanners == null || mBanners.size() == 0) {
            runOnGetFailed();
        } else {
            setBanners(mBanners);
            setAdapter(new IPHotBannerAdapter(getContext(), mBanners));
            runOnGetSuccess(mBanners);
//            displayBanners(banners);
        }
    }

    @Override
    public void runOnGetFailed() {
        List<BannerInfo> banners = IPResourceManager.getInstance().getBannersFromCache();
        if (banners != null && banners.size() > 0) {
            setBanners(banners);
            showBanner();
        } else {
            super.runOnGetFailed();
        }
    }
}
