package com.moinapp.wuliao.modules.banner;

import com.moinapp.wuliao.commons.net.NetworkingListener;

import java.util.List;

/**
 * banner详情获取监听
 * @time 2013-12-5 下午5:28:56
 */
public interface BannerListListener extends NetworkingListener {
	public void onGetBannerListSucc(List<Banner> banners);
}
