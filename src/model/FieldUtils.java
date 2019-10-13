package model;

import org.jetbrains.annotations.NotNull;

import static model.Direction.*;

public final class FieldUtils {
    /**
     * @param hexIndex hex which neighbours to enumerate
     * @return array of neighbours indices ordered clockwise, starting from leftmost
     * element in the same row. In other words spatially elements organised
     * like that (where x is hexIndex):
     *   [1] [2]
     * [0] [x] [3]
     *   [5] [4]
     */
    @NotNull
    public static HexIndex[] getNeighboursIndices(@NotNull HexIndex hexIndex) {
        HexIndex down = hexIndex.go(DOWN);
        HexIndex up = hexIndex.go(UP);
        return new HexIndex[]{
                hexIndex.go(LEFT), up.go(LEFT), up, hexIndex.go(RIGHT), down, down.go(LEFT)
        };
    }
}
