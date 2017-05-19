package ab.umimagemarklib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ab.umimagemarklib.bean.LayerBean;
import ab.umimagemarklib.bean.MarkBean;

/**
 * Created by AB051788 on 2017/4/11.
 */
public class ImageMark extends FrameLayout {
	private List<Mark> shapeMarks = new ArrayList<>();
	private List<IconMark> iconMarks = new ArrayList<>();
	private List<TextMark> textMarks = new ArrayList<>();
	private Mark handlingMark;
	private IconMark currentIcon;
	private ActionMode currentMode = ActionMode.NONE;

	private float downX;
	private float downY;

	private Context context;

	private ImageView imageview;
	private int x0;
	private int y0;

	private enum ActionMode {
		NONE,   //nothing
		DRAG,   //drag the sticker with your finger
		NEWMODE,
		ZOOM_WITH_TWO_FINGER,   //zoom in or zoom out the sticker and rotate the sticker with two finger
		ICON,    //touch in icon
		CLICK    //Click the Sticker
	}

	public ImageMark(Context context) {
		this(context, null);
	}

	public ImageMark(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImageMark(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		View view = new View(context);
		view.setFocusableInTouchMode(true);
		this.addView(view);
		iconMarks.clear();
		iconMarks.add(new IconMark(IconMark.LEFT_TOP));
		iconMarks.add(new IconMark(IconMark.LEFT_BOTTOM));
		iconMarks.add(new IconMark(IconMark.RIGHT_BOTOM));
		iconMarks.add(new IconMark(IconMark.RIGHT_TOP));
	}

	public ImageView getImageview() {
		return imageview;
	}


	public void setImageview(ImageView imageview) {
		this.imageview = imageview;
		x0 = (getWidth() - imageview.getWidth()) / 2;
		y0 = (getHeight() - imageview.getHeight()) / 2;
		Log.d("x0", x0 + "");
		Log.d("y0", y0 + "");
	}


	//重绘要修改的标记
	public void modifyMarkBean(MarkBean markBean) {
		if (markBean == null) return;
		List<LayerBean> layerBeanList = markBean.getLayers();
		for (int i = 0, size = layerBeanList.size(); i < size; i++) {
			LayerBean layerBean = layerBeanList.get(i);
			String shapeType = layerBean.getShapeType();
			Mark mark = null;
			if ( "line".equals(shapeType)) {
				mark = new ArrowMark(Mark.Shape.LINE);
			} else if ("arrow".equals(shapeType)) {
				mark = new ArrowMark(Mark.Shape.ARROW);
			} else if ("doublearrow".equals(shapeType)) {
				mark = new ArrowMark(Mark.Shape.BOTHARROW);
			} else if ("rect".equals(shapeType)) {
				mark = new ShapeMark(Mark.Shape.RECT);
			} else if ("roundrect".equals(shapeType)) {
				mark = new ShapeMark(Mark.Shape.ROUNDRECT);
			} else if ("oval".equals(shapeType)) {
				mark = new ShapeMark(Mark.Shape.OVAL);
			} else if ("text".equals(shapeType)) {
				mark = new TextMark();
			}
			addMark(mark,layerBean);
		}
	}

	public void addMark(Mark mark) {
		addMark(mark, null);
	}


	public void addMark(Mark mark, LayerBean layerBean) {
		if (mark == null) return;
		Log.e("img", imageview.getHeight() + "");
		Log.e("img", imageview.getWidth() + "");
		if (imageview != null) {
			int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
			imageview.measure(w, h);
			mark.setImgHeight(imageview.getMeasuredHeight());
			mark.setImgWidth(imageview.getMeasuredWidth());
			mark.setX0(x0);
			mark.setY0(y0);
		}
		if (layerBean != null) {
			mark.setLayerBean(layerBean);
		}
		shapeMarks.add(mark);
		if (mark.getShape() == Mark.Shape.TEXT) {
			textMarks.add((TextMark) mark);
			if(layerBean != null){
				TextMark textMark = (TextMark) mark;
				textMark.setEdit(ImageMark.this, context,layerBean.getText());
			}
		}
		handlingMark = mark;
		currentMode = ActionMode.NEWMODE;
		invalidate();
	}

	public void deleteMark() {
		if (handlingMark != null) {
			if (handlingMark.getShape() == Mark.Shape.TEXT) {
				handlingMark.cancel();
				if (textMarks.contains(handlingMark)) {
					textMarks.remove(handlingMark);
				}
			}
			if (shapeMarks.contains(handlingMark)) {
				shapeMarks.remove(handlingMark);
			}
			handlingMark = null;
			currentMode = ActionMode.NONE;
			invalidate();
		}
	}

	//	public void addArrowMark(ArrowMark mark){
//		if (mark == null) return;
//		arrowMarks.add(mark);
////		handlingMark = mark;
////		currentMode = ActionMode.NEWMODE;
//		invalidate();
//	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
//			stickerRect.left = left;
//			stickerRect.top = top;
//			stickerRect.right = right;
//			stickerRect.bottom = bottom;
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		drawMarks(canvas);
	}

	private void drawMarks(Canvas canvas) {
		for (int i = 0, size = shapeMarks.size(); i < size; i++) {
			Mark sticker = shapeMarks.get(i);
			if (sticker != null) {
				if (sticker.getShape() == Mark.Shape.TEXT) {
					if (handlingMark == sticker) {
						sticker.draw(canvas);
					}
				} else {
					sticker.draw(canvas);
				}
			}
		}
		if (handlingMark != null && handlingMark.getRealBounds() != null) {
			RectF rectF = handlingMark.getRealBounds();
			Mark.Shape shape = handlingMark.getShape();
			for (int i = 0, size = iconMarks.size(); i < size; i++) {
				IconMark iconMark = iconMarks.get(i);
				switch (iconMark.getmPosition()) {
					case IconMark.LEFT_TOP:
						drawIcon(iconMark, canvas, rectF.left, rectF.top);
						break;
					case IconMark.LEFT_BOTTOM:
						if (shape != Mark.Shape.LINE && shape != Mark.Shape.ARROW && shape != Mark.Shape.BOTHARROW) {
							drawIcon(iconMark, canvas, rectF.left, rectF.bottom);
						}
						break;
					case IconMark.RIGHT_BOTOM:
						drawIcon(iconMark, canvas, rectF.right, rectF.bottom);
						break;
					case IconMark.RIGHT_TOP:
						if (shape != Mark.Shape.LINE && shape != Mark.Shape.ARROW && shape != Mark.Shape.BOTHARROW) {
							drawIcon(iconMark, canvas, rectF.right, rectF.top);
						}
						break;
				}
			}
		}
	}

	private void drawIcon(IconMark iconMark, Canvas canvas, float x, float y) {
		iconMark.setX(x);
		iconMark.setY(y);
		iconMark.draw(canvas);
	}

	public void changeHanding(Mark mark) {
		if (mark == null) {
			return;
		}
		this.handlingMark = mark;
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int action = MotionEventCompat.getActionMasked(event);

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				currentMode = ActionMode.NONE;
				downX = event.getX();
				downY = event.getY();
				lastX = event.getX();
				lastY = event.getY();
				clearFocus();
//				midPoint = calculateMidPoint();
//				oldDistance = calculateDistance(midPoint.x, midPoint.y, downX, downY);
//				oldRotation = calculateRotation(midPoint.x, midPoint.y, downX, downY);
				if (handlingMark != null && handlingMark.getRealBounds() == null) {
					currentMode = ActionMode.NEWMODE;
					break;
				}

				currentIcon = findCurrentIconTouched(downX, downY);
				if (currentIcon != null) {
					currentMode = ActionMode.ICON;
//					currentIcon.onActionDown(this, event);
				} else {
					handlingMark = findHandlingMark();
					if (handlingMark != null) {
						currentMode = ActionMode.DRAG;
						handlingMark.touchDown(event);
					} else {
						currentMode = ActionMode.NONE;
					}
				}

//				if (handlingSticker != null) {
//					downMatrix.set(handlingSticker.getMatrix());
//				}
//
//				if (bringToFrontCurrentSticker) {
//					stickers.remove(handlingSticker);
//					stickers.add(handlingSticker);
//				}

				invalidate();
				break;


			case MotionEvent.ACTION_MOVE:
				if (currentMode == ActionMode.ICON) {
					zoomShapeMark(event);
				} else if (currentMode == ActionMode.NEWMODE) {
					drawNewShapeMark(event);
//					break;
				} else if (currentMode == ActionMode.DRAG) {
					translate(event);

				}

				invalidate();
				break;

			case MotionEvent.ACTION_UP:

//
				if (handlingMark != null) {
					handlingMark.touchUp(event);
				}
				currentMode = ActionMode.NONE;
//				lastClickTime = currentTime;
				break;


		}//end of switch(action)

		return true;
	}


	private IconMark findCurrentIconTouched(float downX, float downY) {
		for (IconMark icon : iconMarks) {
			float x = icon.getX() - downX;
			float y = icon.getY() - downY;
			float distance_pow_2 = x * x + y * y;
			if (distance_pow_2 <= Math.pow(30, 2)) {
				return icon;
			}
		}
		return null;
	}

	private void changeCurrentIconTouched(MotionEvent event) {
		for (IconMark icon : iconMarks) {
			float x = icon.getX() - event.getX();
			float y = icon.getY() - event.getX();
			float distance_pow_2 = x * x + y * y;
			if (distance_pow_2 <= Math.pow(30, 2)) {
				currentIcon = icon;
			}
		}
//		return null;
	}

	/**
	 * find the touched Sticker
	 **/
	private Mark findHandlingMark() {
		for (int i = shapeMarks.size() - 1; i >= 0; i--) {
			if (isInStickerArea(shapeMarks.get(i), downX, downY)) {
				return shapeMarks.get(i);
			}
		}
		return null;
	}

	private boolean isInStickerArea(Mark mark, float downX, float downY) {
		return mark.contains(downX, downY);
	}

	public void drawNewShapeMark(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
//		Log.d("distance","downX:"+downX);
//		Log.d("distance","downX:"+downX);
//		Log.d("distance",Math.sqrt(Math.pow((x - downX), 2) + Math.pow((y - downY), 2))+"");
		if (Math.sqrt(Math.pow((x - downX), 2) + Math.pow((y - downY), 2)) > 10.0f) {
			RectF drawBounds = new RectF();
			Mark.Shape shape = handlingMark.getShape();
			if (shape == Mark.Shape.LINE || shape == Mark.Shape.ARROW || shape == Mark.Shape.BOTHARROW) {
				drawBounds.set(downX, downY, x, y);
			} else {
				if (downX >= x && downY >= y) {
					drawBounds.set(x, y, downX, downY);
				} else if (downX >= x) {
					drawBounds.set(x, downY, downX, y);
				} else if (downY >= y) {
					drawBounds.set(downX, y, x, downY);
				} else {
					drawBounds.set(x, y, downX, downY);
				}
			}
			handlingMark.setRealBounds(drawBounds);
			invalidate();
			if (handlingMark.getShape() == Mark.Shape.TEXT) {
				TextMark shapeMark = (TextMark) handlingMark;
				shapeMark.setEdit(this, context);
			}
			currentIcon = findCurrentIconTouched(x, y);
			if (currentIcon != null) {
				currentMode = ActionMode.ICON;
			}
		}
	}

	public void zoomShapeMark(MotionEvent event) {
		if (handlingMark != null && currentMode == ActionMode.ICON && currentIcon != null) {
			RectF rect = handlingMark.getRealBounds();
			switch (currentIcon.getmPosition()) {
				case IconMark.LEFT_TOP:
					changeHandingRect(event.getX(), event.getY(), rect.right, rect.bottom, event);
					break;
				case IconMark.LEFT_BOTTOM:
					changeHandingRect(event.getX(), rect.top, rect.right, event.getY(), event);
					break;
				case IconMark.RIGHT_TOP:
					changeHandingRect(rect.left, event.getY(), event.getX(), rect.bottom, event);
					break;
				case IconMark.RIGHT_BOTOM:
					changeHandingRect(rect.left, rect.top, event.getX(), event.getY(), event);
					break;
			}
		}
	}


	private float lastX;
	private float lastY;

	public void translate(MotionEvent event) {
		if (handlingMark != null && currentMode == ActionMode.DRAG) {
			float sx = event.getX() - lastX;
			float sy = event.getY() - lastY;
			RectF rect = handlingMark.getRealBounds();
			float left = rect.left + sx;
			float right = rect.right + sx;
			float top = rect.top + sy;
			float bottom = rect.bottom + sy;
			if (left < 5 || right > getWidth() - 5) {
				left = rect.left;
				right = rect.right;
				if (left < 5) {
					left = 5;
				}
				if (right > getWidth() - 5) {
					right = getWidth() - 5;
				}
			}
			if (top < 5 || bottom > getHeight() - 5) {
				top = rect.top;
				bottom = rect.bottom;
				if (top < 5) {
					top = 5;
				}
				if (bottom > getHeight() - 5) {
					bottom = getHeight() - 5;
				}
			}

			Mark.Shape shape = handlingMark.getShape();
			if (shape == Mark.Shape.LINE || shape == Mark.Shape.ARROW || shape == Mark.Shape.BOTHARROW) {
				if (right < 5 || left > getWidth() - 5) {
					left = rect.left;
					right = rect.right;
					if (right < 5) {
						right = 5;
					}
					if (left > getWidth() - 5) {
						left = getWidth() - 5;
					}
				}
				if (bottom < 5 || top > getHeight() - 5) {
					top = rect.top;
					bottom = rect.bottom;
					if (bottom < 5) {
						bottom = 5;
					}
					if (top > getHeight() - 5) {
						top = getHeight() - 5;
					}
				}
			}
			rect.set(left, top, right, bottom);
			handlingMark.setRealBounds(rect);
			lastX = event.getX();
			lastY = event.getY();

		}
	}

	public void changeHandingRect(float left, float top, float right, float bottom, MotionEvent event) {
//		if(left > right || top > bottom){
//			Log.d("TAG", "changeHandingRect: ");
//			changeCurrentIconTouched(event);
//		}
		RectF rect = handlingMark.getRealBounds();
//		if(left > right){
//			rect.set(right,top,left,bottom);
////			return new RectF(rectF.right,rectF.top,rectF.left,rectF.bottom);
//		}else{
//			rect.set(left,top,right,bottom);
//		}
		rect.set(left, top, right, bottom);
//		rect.set(left<right?left:right,top < bottom?top:bottom,left<right?right:left,top < bottom?bottom:top);
		handlingMark.setRealBounds(rect);
	}

	public void clearFocus() {
		for (int i = 0, size = textMarks.size(); i < size; i++) {
			TextMark textMark = textMarks.get(i);
			if (textMark != null) {
				textMark.clearFocus();
			}
		}
	}


	public MarkBean save(File file) {
		MarkUtils.saveImageToGallery(file, createBitmap());
		MarkUtils.notifySystemGallery(getContext(), file);
		return getMarkBean(file);
	}

	public Bitmap createBitmap() {
		handlingMark = null;
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		this.draw(canvas);
		return bitmap;
	}

	public MarkBean getMarkBean(File file) {
		MarkBean markBean = new MarkBean();
		markBean.setImageURL(file.getAbsolutePath());
		List<LayerBean> layerBeanList = new ArrayList<>();
		for (int i = 0, size = shapeMarks.size(); i < size; i++) {
			layerBeanList.add(shapeMarks.get(i).getLayerBean());
		}
		markBean.setLayers(layerBeanList);
		return markBean;
	}
}
