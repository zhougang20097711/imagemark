package ab.umimagemarklib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.Math.round;

/**
 * Created by AB051788 on 2017/4/12.
 */
public class MarkUtils {
	private static final String TAG = "ImageMark";


	public static String saveImageToGallery(File file, Bitmap bmp) {
		if (bmp == null) {
			Log.e(TAG, "saveImageToGallery: the bitmap is null");
			return "";
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.e(TAG, "saveImageToGallery: the path of bmp is " + file.getAbsolutePath());
		return file.getAbsolutePath();
	}

	// 把文件插入到系统图库
	public static void notifySystemGallery(Context context, File file) {
		if (file == null || !file.exists()) {
			Log.e(TAG, "notifySystemGallery: the file do not exist.");
			return;
		}

		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(),
					file.getName(), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 最后通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
	}

//创建文件
	public static File getNewFile(Context context, String folderName) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);

		String timeStamp = simpleDateFormat.format(new Date());

		String path;
		if (isSDAvailable()) {
			path = getFolderName(folderName) + File.separator + timeStamp + ".jpg";
		} else {
			path = context.getFilesDir().getPath() + File.separator + timeStamp + ".jpg";
		}

		if (TextUtils.isEmpty(path)) {
			return null;
		}

		return new File(path);
	}

	public static String getFolderName(String name) {
		File mediaStorageDir =
				new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
						name);

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return "";
			}
		}
		return mediaStorageDir.getAbsolutePath();
	}

	/**
	 * 判断sd卡是否可以用
	 */
	private static boolean isSDAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public static RectF trapToRect(float[] array) {
		RectF r = new RectF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY,
				Float.NEGATIVE_INFINITY);
		for (int i = 1; i < array.length; i += 2) {
			float x = round(array[i - 1] * 10) / 10.f;
			float y = round(array[i] * 10) / 10.f;
			r.left = (x < r.left) ? x : r.left;
			r.top = (y < r.top) ? y : r.top;
			r.right = (x > r.right) ? x : r.right;
			r.bottom = (y > r.bottom) ? y : r.bottom;
		}
		r.sort();
		return r;
	}



}
