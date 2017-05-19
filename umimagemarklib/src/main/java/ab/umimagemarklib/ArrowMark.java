package ab.umimagemarklib;

import android.graphics.Canvas;
import android.graphics.Path;
import android.view.MotionEvent;

import ab.umimagemarklib.bean.LayerBean;
import ab.umimagemarklib.bean.ShapeBean;

/**
 * Created by AB051788 on 2017/4/18.
 */
public class ArrowMark extends Mark{

	public ArrowMark(Shape shape) {
		super(shape);

//		this.matrix = new Matrix();
//		realBounds = new RectF(100,400,600,60);
//		realBounds = new Rect(0, 0, getWidth(), getHeight());
	}

	@Override
	public void cancel() {

	}

	@Override
	public void touchDown(MotionEvent event) {

	}

	@Override
	public void touchMove(MotionEvent event) {

	}

	@Override
	public void touchUp(MotionEvent event) {

	}

	@Override
	public boolean contains(float x, float y) {
		if(realBounds == null){
			return false;
		}
//		Log.d("arrow",realBounds.toString());
		int left =  (int)realBounds.left;
		int right =  (int)realBounds.right;
		int bottom =  (int)realBounds.bottom;
		int top =  (int)realBounds.top;
		double lineLength = calculateDistance(left,top,right,bottom);
		double length = calculateDistance(left,top,(int)x,(int)y) + calculateDistance((int)x,(int)y,right,bottom);
		if(length < lineLength + 10){
			return true;
		}else {
			return false;
		}
//		return realBounds.contains(x, y);
	}

	@Override
	 public void draw(Canvas canvas) {
		if(realBounds == null)
			return;
		canvas.save();
//		canvas.concat(matrix);
//		mPaint = new Paint();
//		mPaint.setColor(paint_color);
//		mPaint.setAntiAlias(true);
//		mPaint.setStyle(Paint.Style.STROKE);
//		mPaint.setStrokeWidth(paint_width);
		canvas.drawLine(realBounds.left,realBounds.top,realBounds.right,realBounds.bottom,mPaint);
		if(shape == Shape.ARROW || shape == Shape.BOTHARROW){
			drawAL(canvas,realBounds.left,realBounds.top,realBounds.right,realBounds.bottom);
		}
		if(shape == Shape.BOTHARROW){
			drawAL(canvas,realBounds.right,realBounds.bottom,realBounds.left,realBounds.top);
		}

//		drawAL(canvas,realBounds.right,realBounds.bottom,realBounds.left,realBounds.top);
		canvas.restore();
	}

	/**
	 * 画箭头
	 * @param sx
	 * @param sy
	 * @param ex
	 * @param ey
	 */
	public void drawAL(Canvas canvas,float sx, float sy, float ex, float ey)
	{

		double H = 18; // 箭头高度
		double L = 6.5; // 底边的一半
		int x3 = 0;
		int y3 = 0;
		int x4 = 0;
		int y4 = 0;
		double awrad = Math.atan(L / H); // 箭头角度
		double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
		double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
		double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
		double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
		double y_3 = ey - arrXY_1[1];
		double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
		double y_4 = ey - arrXY_2[1];
		Double X3 = new Double(x_3);
		x3 = X3.intValue();
		Double Y3 = new Double(y_3);
		y3 = Y3.intValue();
		Double X4 = new Double(x_4);
		x4 = X4.intValue();
		Double Y4 = new Double(y_4);
		y4 = Y4.intValue();
		Path triangle = new Path();
		triangle.moveTo(ex, ey);
		triangle.lineTo(x3, y3);
		triangle.lineTo(x4, y4);
		triangle.close();
		canvas.drawPath(triangle,mPaint);
	}

	// 计算
	public double[] rotateVec(float px, float py, double ang, boolean isChLen, double newLen)
	{
		double mathstr[] = new double[2];
		// 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
		double vx = px * Math.cos(ang) - py * Math.sin(ang);
		double vy = px * Math.sin(ang) + py * Math.cos(ang);
		if (isChLen) {
			double d = Math.sqrt(vx * vx + vy * vy);
			vx = vx / d * newLen;
			vy = vy / d * newLen;
			mathstr[0] = vx;
			mathstr[1] = vy;
		}
		return mathstr;
	}


	@Override
	public LayerBean getLayerBean() {
		if (layerBean == null){
			layerBean = new LayerBean();
		}
		if(shape == Shape.LINE){
			layerBean.setShapeType("line");
		}else if (shape == Shape.ARROW){
			layerBean.setShapeType("arrow");
		}else if (shape == Shape.BOTHARROW){
			layerBean.setShapeType("doublearrow");
		}
		layerBean.setStrokeWidth((int)getPaint_width());
		layerBean.setStrokeColor(getStrColor());
		ShapeBean startPoint = new ShapeBean(getXPercent(realBounds.left),getYPercent(realBounds.top));
		ShapeBean endPoint = new ShapeBean(getXPercent(realBounds.right),getYPercent(realBounds.bottom));
		layerBean.setStartPoint(startPoint);
		layerBean.setEndPoint(endPoint);
//		private int borderRadius;
//		private int zOrder;
//		private String shapeType;// line,arrow,doublearrow,rect,roundrect,oval,text
//		private ShapeBean shapeBound;
//		private ShapeBean startPoint;
//		private ShapeBean endPoint;
//		private String text;

		return layerBean;
	}
}
