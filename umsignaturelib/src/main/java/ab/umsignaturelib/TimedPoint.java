package ab.umsignaturelib;

/**
 * 记录位置点
 */
public class TimedPoint {
    public final float x;            //x轴
    public final float y;           //y轴
    public final long timestamp;   //时间戳

    public TimedPoint(float x, float y) {
        this.x = x;
        this.y = y;
        this.timestamp = System.currentTimeMillis();
    }

    //计算速度
    public float velocityFrom(TimedPoint start) {
        float velocity = distanceTo(start) / (this.timestamp - start.timestamp);
        if (velocity != velocity) return 0f;
        return velocity;
    }

    //计算距离
    public float distanceTo(TimedPoint point) {
        return (float) Math.sqrt(Math.pow(point.x - this.x, 2) + Math.pow(point.y - this.y, 2));
    }
}