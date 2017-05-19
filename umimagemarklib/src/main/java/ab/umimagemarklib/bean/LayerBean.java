package ab.umimagemarklib.bean;

import java.io.Serializable;

/**
 * Created by AB051788 on 2017/5/17.
 */
public class LayerBean implements Serializable{
	private int strokeWidth;
	private String strokeColor;
	private int borderRadius;
	private int zOrder;
	private String shapeType;// line,arrow,doublearrow,rect,roundrect,oval,text
	private ShapeBean shapeBound;
	private ShapeBean startPoint;
	private ShapeBean endPoint;
	private String text;

	public LayerBean() {
	}

	public LayerBean(String shapeType) {
		this.shapeType = shapeType;
	}

	public LayerBean(int strokeWidth, String strokeColor, int borderRadius, int zOrder, String shapeType, ShapeBean shapeBound) {
		this.strokeWidth = strokeWidth;
		this.strokeColor = strokeColor;
		this.borderRadius = borderRadius;
		this.zOrder = zOrder;
		this.shapeType = shapeType;
		this.shapeBound = shapeBound;
	}

	public LayerBean(ShapeBean endPoint, ShapeBean startPoint, int strokeWidth, String strokeColor, int borderRadius, int zOrder, String shapeType) {
		this.endPoint = endPoint;
		this.startPoint = startPoint;
		this.strokeWidth = strokeWidth;
		this.strokeColor = strokeColor;
		this.borderRadius = borderRadius;
		this.zOrder = zOrder;
		this.shapeType = shapeType;
	}


	@Override
	public String toString() {
		return "LayerBean{" +
				"strokeWidth=" + strokeWidth +
				", strokeColor='" + strokeColor + '\'' +
				", borderRadius=" + borderRadius +
				", zOrder=" + zOrder +
				", shapeType='" + shapeType + '\'' +
				", shapeBound=" + shapeBound +
				", startPoint=" + startPoint +
				", endPoint=" + endPoint +
				", text='" + text + '\'' +
				'}';
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public String getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}

	public int getBorderRadius() {
		return borderRadius;
	}

	public void setBorderRadius(int borderRadius) {
		this.borderRadius = borderRadius;
	}

	public int getzOrder() {
		return zOrder;
	}

	public void setzOrder(int zOrder) {
		this.zOrder = zOrder;
	}

	public String getShapeType() {
		return shapeType;
	}

	public void setShapeType(String shapeType) {
		this.shapeType = shapeType;
	}

	public ShapeBean getShapeBound() {
		return shapeBound;
	}

	public void setShapeBound(ShapeBean shapeBound) {
		this.shapeBound = shapeBound;
	}

	public ShapeBean getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(ShapeBean startPoint) {
		this.startPoint = startPoint;
	}

	public ShapeBean getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(ShapeBean endPoint) {
		this.endPoint = endPoint;
	}
}
