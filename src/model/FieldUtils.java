package model;

import org.jetbrains.annotations.NotNull;

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
        final int p = hexIndex.p;
        final int q = hexIndex.q;
        int pShift = hexIndex.q % 2 != 0 ? 0 : -1;
        return new HexIndex[]{
                HexIndex.create(p - 1, q), HexIndex.create(p  + pShift, q - 1),
                HexIndex.create(p + pShift + 1, q - 1), HexIndex.create(p + 1, q),
                HexIndex.create(p + pShift + 1, q + 1), HexIndex.create(p + pShift, q + 1)
        };
    }
}
