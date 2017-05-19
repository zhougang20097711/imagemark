package ab.umimagemarklib;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import java.math.BigInteger;

import ab.umimagemarklib.bean.LayerBean;
import ab.umimagemarklib.bean.ShapeBean;

/**
 * Created by AB051788 on 2017/4/11.
 */
public abstract class Mark {
	protected static final String TAG = "Mark";
	public Shape shape = Shape.RECT;
	protected RectF realBounds;

	protected Paint mPaint;
	protected int paint_color = 0xFFFF0000;
	protected float paint_width = 6.0f;

	protected LayerBean layerBean;
	protected int imgWidth=1;
	protected int imgHeight=1;
	protected int x0;
	protected int y0;

	public enum Shape {
		RECT,
		ROUNDRECT,
		OVAL,
		LINE,
		ARROW,
		BOTHARROW,
		TEXT,
	}

	public Mark(Shape shape) {
		this.shape = shape;
		this.mPaint = new Paint();
		mPaint.setColor(paint_color);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(paint_width);
	}


	public int getImgWidth() {
		return imgWidth;
	}

	public void setImgWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}

	public int getX0() {
		return x0;
	}

	public void setX0(int x0) {
		this.x0 = x0;
	}

	public int getY0() {
		return y0;
	}

	public void setY0(int y0) {
		this.y0 = y0;
	}

	public int getImgHeight() {
		return imgHeight;
	}

	public void setImgHeight(int imgHeight) {
		this.imgHeight = imgHeight;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public RectF getRealBounds() {
		return realBounds;
	}

	public void setRealBounds(RectF realBounds) {
		this.realBounds = realBounds;
	}

	public Paint getmPaint() {
		return mPaint;
	}

	public void setmPaint(Paint mPaint) {
		this.mPaint = mPaint;
	}

	public int getPaint_color() {
		return paint_color;
	}

	public String getStrColor(){
		String temp = Integer.toHexString(paint_color);
		return "#"+temp;
	}
	public int getIntColor(String color){
		BigInteger bi = new BigInteger(color.replace("#",""), 16);
		return bi.intValue();
	}

	public void setPaint_color(int paint_color) {
		if(shape != Shape.TEXT){
			this.mPaint.setColor(paint_color);
		}
		this.paint_color = paint_color;
	}

	public float getPaint_width() {
		return paint_width;
	}

	public void setPaint_width(float paint_width) {
		this.paint_width = paint_width;
	}


	public double calculateDistance(int x1, int y1, int x2, int y2){
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
	}

	public float getYPercent(float y){
		Log.d("xx","imgHeight:"+imgHeight);
		float result = (y-y0)/imgHeight;
		Log.d("xx","y:"+y);
		Log.d("xx","result:"+result);
		return result;
	}

	public float getXPercent(float x){
		float result = (x-x0)/imgWidth;
		Log.d("xx","imgWidth:"+imgWidth);
		Log.d("xx","x:"+x);
		Log.d("xx","result:"+result);
		return result;
	}
	public float getXPix(float xPercent){
float x = xPercent*imgWidth + x0;
		Log.d("xx","imgWidth:"+imgWidth);
		Log.d("xx","x:"+x);
		return  x;
	}
	public float getYPix(float yPercent){
		float y = yPercent*imgHeight + y0;
		Log.d("xx","imgHeight:"+imgHeight);
		Log.d("xx","y:"+y);
		return  y;
	}

	public void setLayerBean(LayerBean layerBean){
if(layerBean == null){
	return;
}

		this.layerBean = layerBean;
		RectF drawBounds = new RectF();
		if(layerBean.getStartPoint() != null){
			ShapeBean start = layerBean.getStartPoint();
			ShapeBean end = layerBean.getEndPoint();
			drawBounds.set(getXPix(start.getX()), getYPix(start.getY()), getXPix(end.getX()), getYPix(end.getY()));
		}
		if(layerBean.getShapeBound() != null){

		}
this.realBounds = drawBounds;
		setPaint_color(getIntColor(layerBean.getStrokeColor()));

	}
	public abstract LayerBean getLayerBean();

	public abstract void draw(Canvas canvas);

	public abstract boolean contains(float x, float y);

	public abstract void cancel();

	public abstract void touchDown(MotionEvent event);

	public abstract void touchMove(MotionEvent event);

	public abstract void touchUp(MotionEvent event);



}
