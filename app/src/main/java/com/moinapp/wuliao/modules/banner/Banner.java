package com.moinapp.wuliao.modules.banner;

import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * 应用，包括普通应用和游戏。
 * @author changxiaofei
 * @time 2013-11-14 下午5:30:46
 */
public class Banner implements Serializable{
	private static final ILogger NqLog = LoggerFactory.getLogger(BannerModule.MODULE_NAME);
	
	private static final long serialVersionUID = -6988911941842961189L;
	/** plate 板块编号： 0：应用、游戏；1：主题；2：壁纸。（用于banner区分板块，banner表专用）  */
	private List<Integer> intPlate;

	/** strId */
	private String strId;
	/** banner和pop的广告图片 */
	private String imageUrl;
	/** 点击量 **/
	private int clickNum;
	/** plate 板块编号： 0：应用、游戏；1：主题；2：壁纸。3专题  4广告平台广告（用于banner区分板块，banner表专用）  */
	private int newPlate;
	/** plate 板块来源编号： 0：主题；1：锁屏；2：壁纸；3：应用、游戏。（用于banner区分板块，banner表专用）  */
	private int fromPlate;
	/** 发布时间 */
	private long lastPublishTime;
	
//	/** 应用 **/
//	private App app;
//	/** 主题 **/
//	private Theme theme;
//	/** 壁纸 **/
//	private Wallpaper wallpaper;
//	/** 专题 **/
//	private Topic topic;
	
	public Banner(){}
	

	/** plate 板块编号： 0：应用、游戏；1：主题；2：壁纸。3专题  4广告平台广告（用于banner区分板块，banner表专用）  */
	public int getNewPlate() {
		return newPlate;
	}
	public void setNewPlate(int newPlate) {
		this.newPlate = newPlate;
	}
	public int getClickNum() {
		return clickNum;
	}
	public void setClickNum(int clickNum) {
		this.clickNum = clickNum;
	}

	public long getLastPublishTime() {
		return lastPublishTime;
	}
	public void setLastPublishTime(long lastPublishTime) {
		this.lastPublishTime = lastPublishTime;
	}
	/**
	 * @return
	 */
	public List<Integer> getIntPlate() {
		return intPlate;
	}

	public void setIntPlate(List<Integer> intPlate) {
		this.intPlate = intPlate;
	}
	
	public int getFromPlate() {
		return fromPlate;
	}
	public void setFromPlate(int fromPlate) {
		this.fromPlate = fromPlate;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}
}
