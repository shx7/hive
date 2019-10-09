package math;

import org.jetbrains.annotations.NotNull;

public class Vector {
    private final Point p1;
    private final Point p2;
    private final double dx;
    private final double dy;

    private Vector(@NotNull Point p1, @NotNull Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.dx = p2.x - p1.x;
        this.dy = p2.y - p1.y;
    }

    @NotNull
    public static Vector create(@NotNull Point p1, @NotNull Point p2) {
        return new Vector(p1, p2);
    }

    // a `skew product` b = |a| * |b| * sin(Alpha)
    public double pseudoscalarProduct(@NotNull Vector v) {
        return dx * v.dy - dy * v.dx;
    }
}
