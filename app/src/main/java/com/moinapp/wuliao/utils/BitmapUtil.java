package com.moinapp.wuliao.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.ipresource.IPResourceConstants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 图形操作类
 * 
 * @author Administrator
 * 
 */
public class BitmapUtil {

	private static ILogger MyLog = LoggerFactory.getLogger("BitmapUtil");

	public static String IMAGELOAD_CACHE = "Moin/Cache/";
	public static String BITMAP_CACHE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + IMAGELOAD_CACHE;

	public static String IMAGELOAD_CAMERA = "Moin/Camera/";
	public static String BITMAP_CAMERA = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + IMAGELOAD_CAMERA;

	public static String IMAGE_FOLDER = "Moin/MOIN/";
	public static String BITMAP_DOWNLOAD = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + IMAGE_FOLDER;

	public static String EMOJI_FOLDER = "Moin/Emoji/";
	public static String BITMAP_EMOJI = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + EMOJI_FOLDER;

	public static ImageLoaderConfiguration options;

	static {
		if (AppTools.existsSDCARD()) {
			FileUtil.createFolder(BITMAP_CACHE);
			FileUtil.createFolder(BITMAP_CAMERA);
			FileUtil.createFolder(BITMAP_DOWNLOAD);
			FileUtil.createFolder(BITMAP_EMOJI);
		}
		options = new ImageLoaderConfiguration.Builder(ApplicationContext.getContext())
//				.writeDebugLogs()
				.build();
	}
	public static ImageLoaderConfiguration getImageLoaderConfiguration() {
		return options;
	}

	public static DisplayImageOptions getImageLoaderOptionWithDefaultIcon() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.default_img) //设置图片在下载期间显示的图片
		.showImageForEmptyUri(R.drawable.default_icon)
		.cacheInMemory(true)// 是否緩存都內存中
        .cacheOnDisk(true)// 是否緩存到sd卡上
		.build();//设置图片Uri为空或是错误的时候显示的图片
		return options;
	}
	
	public static DisplayImageOptions getImageLoaderOption() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)// 是否緩存都內存中
        .cacheOnDisk(true)// 是否緩存到sd卡上
		.build();//设置图片Uri为空或是错误的时候显示的图片  
		return getImageLoaderOptionWithDefaultIcon();
	}

	/**
	 * Bitmap圆角转化
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap GetRoundedCornerBitmap(Bitmap bitmap, int roundPx) {

		try {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(Color.BLACK);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

			final Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

			canvas.drawBitmap(bitmap, src, rect, paint);
			return output;
		} catch (Exception e) {
			return bitmap;
		}
	}

	private Bitmap decodeUriAsBitmap(Context context, Uri uri) {
		if(uri == null) {
			return null;
		}
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	/**
	 * 通过Url获取Bitmap:还不能用，因为HttpUtil.httpGetInputStream方法被删除，用HttpUtil.download
	 * 
	 * @param context
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static Bitmap getBitmapByUrl(Context context, String url) throws Exception {
		Bitmap bitmap = null;
		InputStream inputstream = null;
		try {
//			inputstream = HttpUtil.httpGetInputStream(context, url);
			bitmap = BitmapFactory.decodeStream(inputstream);
		}catch(Exception e){
			
		}

		try {
			if (null != inputstream) {
				inputstream.close();
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 通过绝对地址获取Bitmap
	 * use ImageLoader.getInstance().decodeSampledBitmapFromResource();
	 * 
	 * @param filePath
	 * @return
	 */

	public static String saveBitmapToSDCardString(Context context, Bitmap bitmap, String filePath, int quality) throws IOException {
		if (bitmap != null) {
			String md5_path = MD5.MD5Encode(filePath.getBytes()) + ".jpg";
			File file = new File(BITMAP_DOWNLOAD);
			if (!file.exists())
				file.mkdirs();
			FileOutputStream fos = new FileOutputStream(BITMAP_DOWNLOAD + md5_path);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
			byte[] bytes = stream.toByteArray();
			fos.write(bytes);
			fos.close();

			return BITMAP_DOWNLOAD + md5_path;
		} else {
			return "";
		}

	}

	public static boolean saveUserAvatar(Context context, Bitmap bitmap) {
		boolean result = false;
		if (bitmap != null) {
			if (!AppTools.existsSDCARD()) {
				MyLog.v("SD card is not avaiable/writeable right now.");
				return false;
			}
			FileOutputStream b = null;
			String fileName = BitmapUtil.getAvatarImagePath();
			try {
				b = new FileOutputStream(fileName);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
				result = true;
			} catch (FileNotFoundException e) {
				MyLog.e(e);
				e.printStackTrace();
			} finally {
				try {
					b.flush();
					b.close();
				} catch (IOException e) {
					MyLog.e(e);
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * 放大缩小图片
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		Bitmap newbmp = null;
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidht = ((float) w / width);
			float scaleHeight = ((float) h / height);
			matrix.postScale(scaleWidht, scaleHeight);
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
			bitmap.recycle();
		}
		return newbmp;
	}

	/**
	 * Drawable转Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {

		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * Bitma转תDrawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		Drawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	/**
	 * Bitma转InputStream
	 *
	 * @param bitmap
	 * @return
	 */
	public static InputStream bitmapToInputStream(Bitmap bitmap) {
		return bitmapToInputStream(bitmap, Bitmap.CompressFormat.JPEG, 100);
	}
	public static InputStream bitmapToInputStream(Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(format, quality, baos);
		InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
		return sbs;
	}

	public static Uri getAvatarImageUri() {
		Uri uri = Uri.parse("file://" + getAvatarImagePath());
		return uri;
	}

	public static String getAvatarImagePath() {
		String path = BITMAP_CACHE + ClientInfo.getUID() + "_avatar.j";
		return path;
	}

	public static Uri getAvatarCropUri() {
		Uri uri = Uri.parse("file://" + getAvatarCropPath());
		return uri;
	}

	public static Uri getCosplayCropUri() {
		Uri uri = Uri.parse("file://" + getCosplayCropPath());
		return uri;
	}

	public static String getAvatarCropPath() {
		return BitmapUtil.BITMAP_CAMERA + ClientInfo.getUID() + "_avatar.j";
	}

	public static String getTmpAvatarCameraPath() {
		return BitmapUtil.BITMAP_CAMERA + ClientInfo.getUID() + "_avatar_tmp.j";
	}

	/**addby wufan 得到大咖秀临时图片**/
	public static String getCosplayImagePath() {
		String path = BITMAP_CAMERA + ClientInfo.getUID() + "_cosplayBackGround.jpg";
		return path;
	}

	public static String getCosplayCropPath() {
		return BitmapUtil.BITMAP_CAMERA + ClientInfo.getUID() + "_cosplay_crop.jpg";
	}

	public static String POST_CAPTURE_PREFIX = "Moin.Post.";
	public static String getPostCapturePath() {
		String str = null;
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssS");
		date = new Date();
		str = format.format(date);
		String fileName = BitmapUtil.BITMAP_CAMERA + POST_CAPTURE_PREFIX + str + IPResourceConstants.JPG_EXTENSION;
		return fileName;
	}

	public static String REPLY_CAPTURE_PREFIX = "Moin.Reply.";
	public static String getReplyCapturePath() {
		String str = null;
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssS");
		date = new Date();
		str = format.format(date);
		String fileName = BitmapUtil.BITMAP_CAMERA + REPLY_CAPTURE_PREFIX + str + IPResourceConstants.JPG_EXTENSION;
		return fileName;
	}

	//========================以下为bitmap的操作===============================
	/**
	 * 从exif信息获取图片旋转角度
	 *
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 对图片进行压缩选择处理
	 *
	 * @param picPath
	 * @return
	 */
	public static Bitmap compressRotateBitmap(String picPath) {
		Bitmap bitmap = null;
		int degree = readPictureDegree(picPath);
		if (degree == 90) {
			bitmap = featBitmapToSuitable(picPath, 500, 1.8f);
			bitmap = rotate(bitmap, 90);
		} else {
			bitmap = featBitmapToSuitable(picPath, 500, 1.8f);
		}
		return bitmap;
	}

	/**
	 * 转换bitmap为字节数组
	 *
	 * @param bitmap
	 * @return
	 */
	public static byte[] bitmapToBytes(Bitmap bitmap) {
		final int size = bitmap.getWidth() * bitmap.getHeight() * 4;
		final ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		byte[] image = out.toByteArray();
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image;

	}

	/**
	 * 获取合适尺寸的图片 图片的长或高中较大的值要 < suitableSize*factor
	 *
	 * @param path
	 * @param suitableSize
	 * @return
	 */
	public static Bitmap featBitmapToSuitable(String path, int suitableSize,
											  float factor) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			options.inJustDecodeBounds = false;
			options.inSampleSize = 1;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			int bitmap_w = options.outWidth;
			int bitmap_h = options.outHeight;
			int max_edge = bitmap_w > bitmap_h ? bitmap_w : bitmap_h;
			while (max_edge / (float) suitableSize > factor) {
				options.inSampleSize <<= 1;
				max_edge >>= 1;
			}
			return BitmapFactory.decodeFile(path, options);
		} catch (Exception e) {
		}
		return bitmap;
	}

	public static Bitmap featBitmap(String path, int width) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			options.inJustDecodeBounds = false;
			options.inSampleSize = 1;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			int bitmap_w = options.outWidth;
			while (bitmap_w / (float) width > 2) {
				options.inSampleSize <<= 1;
				bitmap_w >>= 1;
			}
			return BitmapFactory.decodeFile(path, options);
		} catch (Exception e) {
		}
		return bitmap;
	}

	public static Bitmap loadBitmap(String path, int maxSideLen) {
		if (null == path) {
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false;
		options.inSampleSize = Math.max(options.outWidth / maxSideLen, options.outHeight / maxSideLen);
		if (options.inSampleSize < 1) {
			options.inSampleSize = 1;
		}
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			if (bitmap != bitmap) {
				bitmap.recycle();
			}
			return bitmap;
		} catch (OutOfMemoryError e) {
			MyLog.e(e);
		}
		return null;
	}

	public static Bitmap loadBitmap(String path) {
		if (null == path) {
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		//不对图进行压缩
		options.inSampleSize = 1;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			return bitmap;
		} catch (OutOfMemoryError e) {
			MyLog.e(e);
		}
		return null;
	}

	public static Bitmap loadFromAssets(Activity activity, String name, int sampleSize,Bitmap.Config config) {
		AssetManager asm = activity.getAssets();
		try {
			InputStream is = asm.open(name);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = sampleSize;
			options.inPreferredConfig = config;
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				is.close();
				return bitmap;
			} catch (OutOfMemoryError e) {
				MyLog.e(e);
			}
		} catch (IOException e) {
			MyLog.e(e);
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap decodeByteArrayUnthrow(byte[] data, BitmapFactory.Options opts) {
		try {
			return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		} catch (Throwable e) {
			MyLog.e(e);
		}

		return null;
	}

	public static Bitmap rotateAndScale(Bitmap b, int degrees, float maxSideLen) {

		return rotateAndScale(b, degrees, maxSideLen, true);
	}

	// Rotates the bitmap by the specified degree.
	// If a new bitmap is created, the original bitmap is recycled.
	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
				if (null != b2 && b != b2) {
					b.recycle();
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
			}
		}
		return b;
	}

	public static Bitmap rotateNotRecycleOriginal(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees);
			try {
				return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
			}
		}
		return b;
	}

	public static Bitmap rotateAndScale(Bitmap b, int degrees, float maxSideLen, boolean recycle) {
		if (null == b || degrees == 0 && b.getWidth() <= maxSideLen + 10 && b.getHeight() <= maxSideLen + 10) {
			return b;
		}

		Matrix m = new Matrix();
		if (degrees != 0) {
			m.setRotate(degrees);
		}

		float scale = Math.min(maxSideLen / b.getWidth(), maxSideLen / b.getHeight());
		if (scale < 1) {
			m.postScale(scale, scale);
		}

		try {
			Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
			if (null != b2 && b != b2) {
				if (recycle) {
					b.recycle();
				}
				b = b2;
			}
		} catch (OutOfMemoryError e) {
		}

		return b;
	}

	public static boolean saveBitmap2file(Bitmap bmp, File file, Bitmap.CompressFormat format, int quality) {
		if (file.isFile())
			file.delete();
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			MyLog.e(e);
			return false;
		}

		return bmp.compress(format, quality, stream);
	}

	public static boolean saveBitmap2file(Bitmap bmp, String filename, Bitmap.CompressFormat format, int quality) {
		File file = new File(filename);
		if (file == null) {
			return false;
		}
		if (file.exists())
			file.delete();
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			MyLog.e(e);
			return false;
		}

		return bmp.compress(format, quality, stream);
	}


	/**压缩图片
	 * @param accept: 初始的压缩质量，最大是100
	 * @param accept: 可接受的最大大小（单位KB）
	 */
	public static Bitmap compressImage(Bitmap image, int max_quality, int accept) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, max_quality, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = max_quality;
		MyLog.i(baos.toByteArray().length /1024 + " KB 开始压缩图片 目标是（" + accept +  " KB）");
		while (baos.toByteArray().length /1024 > accept) {    //循环判断如果压缩后图片是否大于32kb,大于继续压缩
			MyLog.i(baos.toByteArray().length /1024 + " KB 压缩图片还达不到要求（" + accept +  " KB）");
			baos.reset();//重置baos即清空baos
			options -= 10;//每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public static String getPostCompressPath(int i) {
		return BITMAP_CAMERA + POST_CAPTURE_PREFIX + "compress_" + i + IPResourceConstants.COMPRESS_EXTENSION;
	}
	public static String getReplyCompressPath(int i) {
		return BITMAP_CAMERA + REPLY_CAPTURE_PREFIX + "compress_" + i + IPResourceConstants.COMPRESS_EXTENSION;
	}
}
