package ab.umimagemarklib;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by AB051788 on 2017/4/12.
 */
public class IconMark{
	public static final int LEFT_TOP = 0;
	public static final int RIGHT_TOP = 1;
	public static final int LEFT_BOTTOM = 2;
	public static final int RIGHT_BOTOM = 3;
    private float x;
	private float y;
	private int mPosition = LEFT_TOP;

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

	public IconMark(int postion){
		mPosition = postion;
	}

	public int getmPosition() {
		return mPosition;
	}

	public void setmPosition(int mPosition) {
		this.mPosition = mPosition;
	}

	public void draw(Canvas canvas) {
		canvas.save();
		 Paint mPaint = new Paint();
		mPaint.setColor(Color.BLUE);
		Paint paintbg = new Paint();
		paintbg.setColor(0xfff4f4f4);

		mPaint.setAntiAlias(true);
		canvas.drawRect(x-24,y-24,x+24,y+24,paintbg);
		canvas.drawRect(x-20,y-20,x+20,y+20,mPaint);
		canvas.restore();
	}
}
