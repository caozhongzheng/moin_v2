package com.moinapp.wuliao.modules.ipresource;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.banner.Banner;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.ipresource.model.BannerInfo;
import com.moinapp.wuliao.modules.wowo.PostDetailActivity;
import com.moinapp.wuliao.modules.wowo.WoPostListActivity;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * IP首页最顶层的Hot Banner
 */
public class IPHotBannerAdapter extends PagerAdapter {
	private static final ILogger MyLog = LoggerFactory.getLogger(IPResourceModule.MODULE_NAME);

	private Context context;
	private List<BannerInfo> banners;
	private ImageLoader imageLoader;

	public IPHotBannerAdapter(Context context, List<BannerInfo> bannerInfos) {
		super();
		this.context = context;
		this.banners = bannerInfos;
		imageLoader = ImageLoader.getInstance();
		if(!imageLoader.isInited()) {
			imageLoader.init(BitmapUtil.getImageLoaderConfiguration());
		}
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}
	
	@Override
	public int getCount() {
		if (banners != null && banners.size() > 1) {
			return Integer.MAX_VALUE;
		} else {
			return 1;
		}
	}
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}
	
	@Override
	public Object instantiateItem(View view, int position) {
		// ViewPager设置Adapter时，自动会生成position 0和1的View
		if (banners.size() > 1 && position < 100){
			return null;
		}

		final int index = position % banners.size();
		final BannerInfo banner = banners.get(index);
		final View imageLayout = LayoutInflater.from(context).inflate(R.layout.banner_viewpager_item, null);
		final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
		final String imgUrl = banner.getImage().getUri();
		try {
			imageLoader.displayImage(imgUrl, imageView, BitmapUtil.getImageLoaderOption());
		} catch (OutOfMemoryError e) {
			MyLog.e(e);
		} catch (Exception e) {
			MyLog.e(e);
		}

		//banner的title和desc
		TextView title = (TextView) imageLayout.findViewById(R.id.banner_title);
		TextView desc = (TextView) imageLayout.findViewById(R.id.banner_desc);
		title.setText(banner.getTitle());
		desc.setText(banner.getDesc());

		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();

				switch (banner.getType()) {
					case BannerInfo.TYPE_IP:
						b.putString("ip_id", banner.getResource().getIpid());
						b.putInt("type", banner.getType());
						b.putBoolean("view_ip", true);
						AppTools.toIntent(ApplicationContext.getContext(), b, IPMoinClipActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
						break;
					case BannerInfo.TYPE_EMOJI:
						b.putString("single_emoji_id", banner.getResource().getEmojiid());
						b.putString("ipname", "");
						AppTools.toIntent(ApplicationContext.getContext(), b, EmojiResourceActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
						break;
					case BannerInfo.TYPE_COSPLAY:
						break;
					case BannerInfo.TYPE_POST:
						b.putSerializable(PostDetailActivity.KEY_WO_ID, banner.getResource().getWoid());
						b.putString(PostDetailActivity.KEY_POST_ID, banner.getResource().getPostid());
						AppTools.toIntent(ApplicationContext.getContext(), b, PostDetailActivity.class,Intent.FLAG_ACTIVITY_NEW_TASK);
						break;
					case BannerInfo.TYPE_WOWO:
						b.putSerializable(WoPostListActivity.WO_ID, banner.getResource().getWoid());
						AppTools.toIntent(ApplicationContext.getContext(), b, WoPostListActivity.class,Intent.FLAG_ACTIVITY_NEW_TASK);
						break;
					case BannerInfo.TYPE_EXTERANL_LINK:
						Intent intent = new Intent(Intent.ACTION_VIEW);
						MyLog.v("playWebVideo: URI:::::::::");
						intent.setData(Uri.parse(banner.getResource().getNetid()));
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
						break;
				}
			}
		});

		((ViewPager) view).addView(imageLayout, 0);

		return imageLayout;
	}
}