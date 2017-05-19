package ab.umimagemarklib.bean;

import java.io.Serializable;

/**
 * Created by AB051788 on 2017/5/17.
 */
public class ShapeBean implements Serializable{
	private float x;
	private float y;
	private float w;
	private float h;

	public ShapeBean() {
	}


	public ShapeBean(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public ShapeBean(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	@Override
	public String toString() {
		return "ShapeBean{" +
				"x=" + x +
				", y=" + y +
				", w=" + w +
				", h=" + h +
				'}';
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getW() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}

	public float getH() {
		return h;
	}

	public void setH(float h) {
		this.h = h;
	}
}
