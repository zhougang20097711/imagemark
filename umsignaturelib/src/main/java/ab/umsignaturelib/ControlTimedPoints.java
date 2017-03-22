package ab.umsignaturelib;

/**
 *存放计算曲线的两控制点
 */
public class ControlTimedPoints {

    public TimedPoint c1;
    public TimedPoint c2;

    public ControlTimedPoints(TimedPoint c1, TimedPoint c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

}
