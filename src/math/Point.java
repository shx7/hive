package math;

import com.sun.istack.internal.NotNull;

public class Point {
    public final double x;
    public final double y;

    private Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @NotNull
    public static Point create(double x, double y) {
        return new Point(x, y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
