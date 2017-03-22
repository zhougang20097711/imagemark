package ab.umsignaturelib;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 签字控件
 * 默认画笔颜色black,最大宽度：4dp,最小宽度：2dp
 */
public class SignaturePad extends View {
	private List<TimedPoint> mPoints;              //存放点集
	private boolean mIsEmpty;
	private float mLastTouchX;                     //最后x
	private float mLastTouchY;                    //最后Y
	private float mLastVelocity;                 //最后速度
	private float mLastWidth;                    //宽度
	private RectF mDirtyRect;                    //签字矩阵

	// Configurable parameters
	private int mMinWidth;                      //画笔最小宽度
	private int mMaxWidth;                      //画笔最大宽度
	private float mVelocityFilterWeight;        //速度的权重
	private OnSignedListener mOnSignedListener;  //监听事件

	private Paint mPaint = new Paint();          //画笔
	private Path mPath = new Path();             //路径
	private Bitmap mSignatureBitmap = null;      //签字影像
	private Canvas mSignatureBitmapCanvas = null;  //画布

	private Context context;
	public SignaturePad(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SignaturePad, 0, 0);
		try {
			//配置画笔的一些属性，最大小宽度，颜色
			mMinWidth = a.getDimensionPixelSize(R.styleable.SignaturePad_minWidth, convertDpToPx(2));
			mMaxWidth = a.getDimensionPixelSize(R.styleable.SignaturePad_maxWidth, convertDpToPx(4));
			mVelocityFilterWeight = a.getFloat(R.styleable.SignaturePad_velocityFilterWeight, 0.9f);
			mPaint.setColor(a.getColor(R.styleable.SignaturePad_penColor, Color.BLACK));
		} finally {
			a.recycle();
		}
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mDirtyRect = new RectF();
		clear();
	}

	/**
	 * 设置画笔颜色
	 * @param colorRes color res
	 */
	public void setPenColorRes(int colorRes) {
		try {
			setPenColor(getResources().getColor(colorRes));
		} catch (Resources.NotFoundException ex) {
			setPenColor(0x000000);
		}
	}

	/**
	 * 设置画笔颜色
	 * @param color
	 */
	public void setPenColor(int color) {
		mPaint.setColor(color);
	}

	/**
	 * 设置画笔最小宽度
	 * @param minWidth 最小宽度
	 */
	public void setMinWidth(float minWidth) {
		mMinWidth = convertDpToPx(minWidth);
	}

	/**
	 * 设置画笔最大宽度
	 * @param maxWidth 最大宽度
	 */
	public void setMaxWidth(float maxWidth) {
		mMaxWidth = convertDpToPx(maxWidth);
	}

	/**
	 * 设置速度权重
	 * @param velocityFilterWeight 权重
	 */
	public void setVelocityFilterWeight(float velocityFilterWeight) {
		mVelocityFilterWeight = velocityFilterWeight;
	}

	/**
	 * 清除全部
	 */
	public void clear() {
		mPoints = new ArrayList<TimedPoint>();
		mLastVelocity = 0;
		mLastWidth = (mMinWidth + mMaxWidth) / 2;
		mPath.reset();

		if (mSignatureBitmap != null) {
			mSignatureBitmap = null;
			ensureSignatureBitmap();
		}

		setIsEmpty(true);

		invalidate();
	}

	/**
	 * 处理触碰事件
	 * @param event 事件
	 * @return
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled())
			return false;

		float eventX = event.getX();
		float eventY = event.getY();

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN:
			Log.i("event", "ACTION_POINTER_DOWN");
			break;
		case MotionEvent.ACTION_POINTER_UP:
			Log.i("event", "ACTION_POINTER_UP");
			mPoints.clear();//清空点集
			break;
		case MotionEvent.ACTION_DOWN:
			Log.i("event", "ACTION_DOWN");
			getParent().requestDisallowInterceptTouchEvent(true);
			mPoints.clear();
			mPath.moveTo(eventX, eventY);
			mLastTouchX = eventX;
			mLastTouchY = eventY;
//			addPoint(new TimedPoint(eventX, eventY));
			break;
		case MotionEvent.ACTION_MOVE:
			Log.i("event", "ACTION_MOVE");
			resetDirtyRect(eventX, eventY);
			addPoint(new TimedPoint(eventX, eventY));//加入点
			break;

		case MotionEvent.ACTION_UP:
			Log.i("event", "ACTION_UP");
			resetDirtyRect(eventX, eventY);
//			addPoint(new TimedPoint(eventX, eventY));
			mPoints.clear();//清空点集
			mPath.moveTo(eventX, eventY);
			getParent().requestDisallowInterceptTouchEvent(true);
			setIsEmpty(false);
			break;

		default:
			return false;
		}

		/**
		 * 重绘指定区域
		 */
		invalidate((int) (mDirtyRect.left - mMaxWidth), (int) (mDirtyRect.top - mMaxWidth), (int) (mDirtyRect.right + mMaxWidth), (int) (mDirtyRect.bottom + mMaxWidth));

		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mSignatureBitmap != null) {
			canvas.drawBitmap(mSignatureBitmap, 0, 0, mPaint);
		}
	}

	/**
	 * 设置监听
	 * @param listener
	 */
	public void setOnSignedListener(OnSignedListener listener) {
		mOnSignedListener = listener;
	}

	/**
	 * 获取当前是否有签字
	 * @return
	 */
	public boolean isEmpty() {
		return mIsEmpty;
	}

	/**
	 * 获取当前签字影像的Bitmap 白底
	 * @return 
	 */
	public Bitmap getSignatureBitmap() {
		Bitmap originalBitmap = getTransparentSignatureBitmap();
		Bitmap whiteBgBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(whiteBgBitmap);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(originalBitmap, 0, 0, null);
		return whiteBgBitmap;
	}

	/**
	 * 给画布设置Bitmap
	 * @param signature
	 */
	public void setSignatureBitmap(Bitmap signature) {
		clear();
		ensureSignatureBitmap();

		RectF tempSrc = new RectF();
		RectF tempDst = new RectF();

		int dWidth = signature.getWidth();
		int dHeight = signature.getHeight();
		int vWidth = getWidth();
		int vHeight = getHeight();

		// Generate the required transform.
		tempSrc.set(0, 0, dWidth, dHeight);
		tempDst.set(0, 0, vWidth, vHeight);

		Matrix drawMatrix = new Matrix();
		drawMatrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER);

		Canvas canvas = new Canvas(mSignatureBitmap);
		canvas.drawBitmap(signature, drawMatrix, null);
		setIsEmpty(false);
		invalidate();
	}

	/**
	 * 根据当前画布大小获取透明的签字bitmap
	 * @return
	 */
	public Bitmap getTransparentSignatureBitmap() {
		ensureSignatureBitmap();
		return mSignatureBitmap;
	}

	/**
	 *去掉白边的透明的签字bitmap
	 * @param trimBlankSpace 是否去掉白边
	 * @return
	 */
	public Bitmap getTransparentSignatureBitmap(boolean trimBlankSpace) {

		if (!trimBlankSpace) {
			return getTransparentSignatureBitmap();
		}

		ensureSignatureBitmap();

		int imgHeight = mSignatureBitmap.getHeight();
		int imgWidth = mSignatureBitmap.getWidth();

		int backgroundColor = Color.TRANSPARENT;

		int xMin = Integer.MAX_VALUE, xMax = Integer.MIN_VALUE, yMin = Integer.MAX_VALUE, yMax = Integer.MIN_VALUE;

		boolean foundPixel = false;
//找到不是透明的区域
		for (int x = 0; x < imgWidth; x++) {
			boolean stop = false;
			for (int y = 0; y < imgHeight; y++) {
				if (mSignatureBitmap.getPixel(x, y) != backgroundColor) {
					xMin = x;
					stop = true;
					foundPixel = true;
					break;
				}
			}
			if (stop)
				break;
		}

		if (!foundPixel)
			return null;
		for (int y = 0; y < imgHeight; y++) {
			boolean stop = false;
			for (int x = xMin; x < imgWidth; x++) {
				if (mSignatureBitmap.getPixel(x, y) != backgroundColor) {
					yMin = y;
					stop = true;
					break;
				}
			}
			if (stop)
				break;
		}

		for (int x = imgWidth - 1; x >= xMin; x--) {
			boolean stop = false;
			for (int y = yMin; y < imgHeight; y++) {
				if (mSignatureBitmap.getPixel(x, y) != backgroundColor) {
					xMax = x;
					stop = true;
					break;
				}
			}
			if (stop)
				break;
		}

		for (int y = imgHeight - 1; y >= yMin; y--) {
			boolean stop = false;
			for (int x = xMin; x <= xMax; x++) {
				if (mSignatureBitmap.getPixel(x, y) != backgroundColor) {
					yMax = y;
					stop = true;
					break;
				}
			}
			if (stop)
				break;
		}

		return Bitmap.createBitmap(mSignatureBitmap, xMin, yMin, xMax - xMin, yMax - yMin);
	}

	/**
	 * 添加新点
	 * @param newPoint
	 */
	private void addPoint(TimedPoint newPoint) {
		mPoints.add(newPoint);
		if (mPoints.size() > 2) {
			// To reduce the initial lag make it work with 3 mPoints
			// by copying the first point to the beginning.
			if (mPoints.size() == 3)
				mPoints.add(0, mPoints.get(0));

			ControlTimedPoints tmp = calculateCurveControlPoints(mPoints.get(0), mPoints.get(1), mPoints.get(2));
			TimedPoint c2 = tmp.c2;
			tmp = calculateCurveControlPoints(mPoints.get(1), mPoints.get(2), mPoints.get(3));
			TimedPoint c3 = tmp.c1;
			Bezier curve = new Bezier(mPoints.get(1), c2, c3, mPoints.get(2));

			TimedPoint startPoint = curve.startPoint;
			TimedPoint endPoint = curve.endPoint;

			//计算速度
			float velocity = endPoint.velocityFrom(startPoint);
			velocity = Float.isNaN(velocity) ? 0.0f : velocity;

			velocity = mVelocityFilterWeight * velocity + (1 - mVelocityFilterWeight) * mLastVelocity;

	//计算当前宽度
			float newWidth = strokeWidth(velocity);


			addBezier(curve, mLastWidth, newWidth);

			mLastVelocity = velocity;
			mLastWidth = newWidth;


			mPoints.remove(0);
		}
	}

	/**
	 *绘制曲线
	 * @param curve 曲线
	 * @param startWidth 上一点宽度
	 * @param endWidth 当前宽度
	 */
	private void addBezier(Bezier curve, float startWidth, float endWidth) {
		ensureSignatureBitmap();
		float originalWidth = mPaint.getStrokeWidth();
		float widthDelta = endWidth - startWidth;
		float drawSteps = (float) Math.floor(curve.length());

		for (int i = 0; i < drawSteps; i++) {
			// Calculate the Bezier (x, y) coordinate for this step.
			float t = ((float) i) / drawSteps;
			float tt = t * t;
			float ttt = tt * t;
			float u = 1 - t;
			float uu = u * u;
			float uuu = uu * u;

			float x = uuu * curve.startPoint.x;
			x += 3 * uu * t * curve.control1.x;
			x += 3 * u * tt * curve.control2.x;
			x += ttt * curve.endPoint.x;

			float y = uuu * curve.startPoint.y;
			y += 3 * uu * t * curve.control1.y;
			y += 3 * u * tt * curve.control2.y;
			y += ttt * curve.endPoint.y;

			// Set the incremental stroke width and draw.
			mPaint.setStrokeWidth(startWidth + ttt * widthDelta);
			mSignatureBitmapCanvas.drawPoint(x, y, mPaint);
			expandDirtyRect(x, y);
		}

		mPaint.setStrokeWidth(originalWidth);
	}

	/**
	 * 通过连续三个点计算控制点位置
	 * @param s1
	 * @param s2
	 * @param s3
	 * @return
	 */
	private ControlTimedPoints calculateCurveControlPoints(TimedPoint s1, TimedPoint s2, TimedPoint s3) {
		float dx1 = s1.x - s2.x;
		float dy1 = s1.y - s2.y;
		float dx2 = s2.x - s3.x;
		float dy2 = s2.y - s3.y;

		TimedPoint m1 = new TimedPoint((s1.x + s2.x) / 2.0f, (s1.y + s2.y) / 2.0f);
		TimedPoint m2 = new TimedPoint((s2.x + s3.x) / 2.0f, (s2.y + s3.y) / 2.0f);

		float l1 = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1);
		float l2 = (float) Math.sqrt(dx2 * dx2 + dy2 * dy2);

		float dxm = (m1.x - m2.x);
		float dym = (m1.y - m2.y);
		float k = l2 / (l1 + l2);
		TimedPoint cm = new TimedPoint(m2.x + dxm * k, m2.y + dym * k);

		float tx = s2.x - cm.x;
		float ty = s2.y - cm.y;

		return new ControlTimedPoints(new TimedPoint(m1.x + tx, m1.y + ty), new TimedPoint(m2.x + tx, m2.y + ty));
	}

	/**
	 * 根据速度计算画笔宽度
	 * @param velocity 速度
	 * @return
	 */
	private float strokeWidth(float velocity) {
		return Math.max(mMaxWidth / (velocity + 1), mMinWidth);
	}

	/**
	 *确保mDirtyRect足够大
	 * @param historicalX x
	 * @param historicalY y
	 */
	private void expandDirtyRect(float historicalX, float historicalY) {
		if (historicalX < mDirtyRect.left) {
			mDirtyRect.left = historicalX;
		} else if (historicalX > mDirtyRect.right) {
			mDirtyRect.right = historicalX;
		}
		if (historicalY < mDirtyRect.top) {
			mDirtyRect.top = historicalY;
		} else if (historicalY > mDirtyRect.bottom) {
			mDirtyRect.bottom = historicalY;
		}
	}

	/**
	 *重新设置mDirtyRect 区域
	 * @param eventX
	 * @param eventY
	 */
	private void resetDirtyRect(float eventX, float eventY) {
		mDirtyRect.left = Math.min(mLastTouchX, eventX);
		mDirtyRect.right = Math.max(mLastTouchX, eventX);
		mDirtyRect.top = Math.min(mLastTouchY, eventY);
		mDirtyRect.bottom = Math.max(mLastTouchY, eventY);
	}

	/**
	 * 设置清空
	 * @param newValue
	 */
	private void setIsEmpty(boolean newValue) {
		mIsEmpty = newValue;
		if (mOnSignedListener != null) {
			if (mIsEmpty) {
				mOnSignedListener.onClear();
			} else {
				mOnSignedListener.onSigned();
			}
		}
	}

	/**
	 * 生成签字的bitmap
	 */
	private void ensureSignatureBitmap() {
		if (mSignatureBitmap == null) {
			mSignatureBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
			mSignatureBitmapCanvas = new Canvas(mSignatureBitmap);
		}
	}

	/**
	 * dp转px
	 * @param dp
	 * @return
	 */
	private int convertDpToPx(float dp) {
		return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
	}

	/**
	 * 监听接口
	 */
	public interface OnSignedListener {
		public void onSigned();

		public void onClear();
	}


	/**
	 *保存图片为JPG
	 * @param bitmap bitmap
	 * @param photo 目标文件
	 * @throws IOException
	 */
    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
		saveBitmap(bitmap,photo,Bitmap.CompressFormat.JPEG);
    }

	/**
	 *保存图片为PNG
	 * @param bitmap bitmap
	 * @param photo 目标文件
	 * @throws IOException
	 */
	public void saveBitmapToPNG(Bitmap bitmap, File photo) throws IOException {
		saveBitmap(bitmap,photo,Bitmap.CompressFormat.PNG);
	}

	/**
	 * 保存图片
	 * @param bitmap bitmap
	 * @param photo  目标文件
	 */
	public void saveBitmap(Bitmap bitmap, File photo,Bitmap.CompressFormat format)throws IOException{
		Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawColor(Color.TRANSPARENT);
		canvas.drawBitmap(bitmap, 0, 0, null);
		OutputStream stream = new FileOutputStream(photo);
		newBitmap.compress(format, 80, stream);
		stream.close();
	}
}
