package ab.umimagemarklib;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;

import ab.umimagemarklib.bean.LayerBean;
import ab.umimagemarklib.bean.ShapeBean;

/**
 * Created by AB051788 on 2017/4/11.
 */
public class ShapeMark extends Mark {
	protected RectF drawBounds;

	public ShapeMark(Shape shape) {
		super(shape);
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
		if(realBounds == null){
			return;
		}
		if(realBounds.left>realBounds.right || realBounds.top > realBounds.bottom){
			if(drawBounds != null){
				realBounds = new RectF(drawBounds.left,drawBounds.top,drawBounds.right,drawBounds.bottom);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		if(realBounds == null)
			return;
		canvas.save();
//		mPaint = new Paint();
//		mPaint.setColor(paint_color);
//		mPaint.setAntiAlias(true);
//		mPaint.setStyle(Paint.Style.STROKE);
//		mPaint.setStrokeWidth(paint_width);
		if(shape == Shape.RECT || shape == Shape.TEXT){
			canvas.drawRect(checkRectF(realBounds),mPaint);
		}else if(shape == Shape.ROUNDRECT){
			canvas.drawRoundRect(checkRectF(realBounds),20,20,mPaint);
		}else if(shape == Shape.OVAL){
			canvas.drawOval(checkRectF(realBounds),mPaint);
		}
		canvas.restore();
	}



public RectF checkRectF(RectF rectF){
	if (drawBounds == null){
		drawBounds = new RectF();
	}
	if(rectF.left >= rectF.right ||rectF.top >= rectF.bottom){
//		Log.d("aa", "checkRectF: ");
		if(rectF.left >= rectF.right){
			drawBounds.set(rectF.right,rectF.top,rectF.left,rectF.bottom);
		}
		if(rectF.top >= rectF.bottom){
			drawBounds.set(rectF.left,rectF.bottom,rectF.right,rectF.top);
		}
		if(rectF.left >= rectF.right && rectF.top >= rectF.bottom){
			drawBounds.set(rectF.right,rectF.bottom,rectF.left,rectF.top);
		}
//		drawBounds.set(rectF.left>=rectF.right?rectF.right:rectF.left,rectF.top>=rectF.bottom?rectF.top:rectF.bottom,rectF.left>=rectF.right?rectF.left:rectF.right,rectF.top>=rectF.bottom?rectF.bottom:rectF.top);
		return drawBounds;
	}

	return rectF;
}

	@Override
	public boolean contains(float x, float y) {
		if(realBounds == null){
			return false;
		}
		return realBounds.contains(x, y);
	}

	@Override
	public LayerBean getLayerBean() {
		if (layerBean == null){
			layerBean = new LayerBean();
		}

		if(shape == Shape.RECT){
			layerBean.setShapeType("rect");
		}else if (shape == Shape.ROUNDRECT){
			layerBean.setShapeType("roundrect");
		}else if (shape == Shape.OVAL){
			layerBean.setShapeType("oval");
		}
		layerBean.setStrokeWidth((int)getPaint_width());
		layerBean.setStrokeColor(getStrColor());

		if(realBounds == null){
			return layerBean;
		}
		ShapeBean startPoint = new ShapeBean(getXPercent(realBounds.left),getYPercent(realBounds.top));
		ShapeBean endPoint = new ShapeBean(getXPercent(realBounds.right),getYPercent(realBounds.bottom));
		layerBean.setStartPoint(startPoint);
		layerBean.setEndPoint(endPoint);

		ShapeBean shapeBound = new ShapeBean(getXPercent(realBounds.left),getYPercent(realBounds.top));
		shapeBound.setW(realBounds.right-realBounds.left);
		shapeBound.setH(realBounds.bottom - realBounds.top);
		layerBean.setShapeBound(shapeBound);
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
