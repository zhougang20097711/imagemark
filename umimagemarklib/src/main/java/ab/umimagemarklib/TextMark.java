package ab.umimagemarklib;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import ab.umimagemarklib.bean.LayerBean;
import ab.umimagemarklib.bean.ShapeBean;

/**
 * Created by AB051788 on 2017/4/19.
 */
public class TextMark extends ShapeMark {
	private EditText editText;
	private ImageMark imageMark;

	public TextMark() {
		super(Shape.TEXT);
		setShape(Shape.TEXT);
//		this.mPaint.setColor(0xFF888888);
//		this.paint_color = 0xFF333333;
	}

	public void setEdit(final ImageMark layout, Context context) {
		setEdit(layout,context,"");
	}
	public void setEdit(final ImageMark layout, Context context,String msg) {
		if (editText == null) {
			editText = new EditText(context);
			imageMark = layout;
			editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						layout.changeHanding(TextMark.this);
					}
				}
			});
			editText.setBackground(null);
			if(!"".equals(msg)){
				checkRectF(realBounds);
			}
			editText.setLayoutParams(getLayoutParams());
			editText.setTextColor(this.paint_color);

			editText.setText(msg);
			layout.addView(editText);
		}
	}

	public FrameLayout.LayoutParams getLayoutParams() {
		if (editText == null || drawBounds == null) {
			return new FrameLayout.LayoutParams(0, 0);
		}
		RectF rectF = realBounds;
		if ((rectF.left >= rectF.right || rectF.top >= rectF.bottom) && drawBounds != null) {
			rectF = drawBounds;
		}
		int width = (int) (rectF.right - rectF.left) - 20;
		int height = (int) (rectF.bottom - rectF.top) - 40;
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
		params.setMargins((int) rectF.left + 10, (int) rectF.top + 20, 0, 0);

//		params.gravity = Gravity.TOP;
		return params;
	}

	@Override
	public void setRealBounds(RectF realBounds) {
		super.setRealBounds(realBounds);
		if (editText != null) {
			editText.setLayoutParams(getLayoutParams());
		}
	}


	public void clearFocus() {
		if (editText != null && editText.hasFocus()) {
			Log.d("focus", "clear");
			editText.clearFocus();
//	editText.c
		}
	}

	@Override
	public void cancel() {
		super.cancel();
		if(imageMark != null && editText != null){
			imageMark.removeView(editText);
		}
	}

	@Override
	public LayerBean getLayerBean() {
		if (layerBean == null){
			layerBean = new LayerBean();
		}

		if(shape == Shape.TEXT){
			layerBean.setShapeType("text");
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
if(editText != null){
	layerBean.setText(editText.getText().toString().trim());
}
//		private int borderRadius;
//		private int zOrder;
//		private String shapeType;// line,arrow,doublearrow,rect,roundrect,oval,text
//		private ShapeBean shapeBound;
//		private ShapeBean startPoint;
//		private ShapeBean endPoint;
//		private String text;

		return layerBean;
	}

	public EditText getEditText() {
		return editText;
	}

	public void setEditText(EditText editText) {
		this.editText = editText;
	}
}
