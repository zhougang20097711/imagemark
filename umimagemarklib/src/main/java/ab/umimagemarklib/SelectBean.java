package ab.umimagemarklib;

import android.util.Log;

/**
 * Created by AB051788 on 2017/5/4.
 */
public class SelectBean {

	private int iconsrc;
	private Mark.Shape tag;
	private int color = 0xFF4a90e2;
	private float paint_width;
//	public enum BeanTag {
//		COLOR,
//		RECT,
//		ROUNDRECT,
//		OVAL,
//		LINE,
//		ARROW,
//		BOTHARROW,
//		TEXT,
//	}


	public SelectBean(int iconsrc, Mark.Shape tag) {
		this.iconsrc = iconsrc;
		this.tag = tag;
	}


	public Mark getMark(){
		Mark mark = null;
		switch (tag){
			case RECT:
				mark = new ShapeMark(ShapeMark.Shape.RECT);
				break;
			case ROUNDRECT:
				mark = new ShapeMark(ShapeMark.Shape.ROUNDRECT);
				break;
			case OVAL:
				mark = new ShapeMark(ShapeMark.Shape.OVAL);
				break;
			case LINE:
				mark = new ArrowMark(ArrowMark.Shape.LINE);
				break;
			case ARROW:
				mark = new ArrowMark(ArrowMark.Shape.ARROW);
				break;
			case BOTHARROW:
				mark = new ArrowMark(ArrowMark.Shape.BOTHARROW);
				break;
			case TEXT:
				mark = new TextMark();
				break;
			default:
				mark = new ShapeMark(ShapeMark.Shape.RECT);
				break;
		}
		mark.setPaint_color(getColor());
		Log.d("color",getColor()+"");
		return mark;
	}


	public Mark.Shape getTag() {
		return tag;
	}

	public void setTag(Mark.Shape tag) {
		this.tag = tag;
	}

	public int getIconsrc() {
		return iconsrc;
	}

	public void setIconsrc(int iconsrc) {
		this.iconsrc = iconsrc;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}



	public float getPaint_width() {
		return paint_width;
	}

	public void setPaint_width(float paint_width) {
		this.paint_width = paint_width;
	}
}
