package model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *   Hex field layout is "odd-r" horizontal layout:
 *
 *   | 0,0 | 1,0 | 2,0 | 3,0 |
 *      | 0,1 | 1,1 | 2,1 | 3,1 |
 *   | 0,2 | 1,2 | 2,2 | 3,2 |
 *
 *  See <a href=https://www.redblobgames.com/grids/hexagons/#coordinates>hex grids article</a>
 */
public final class HexIndex {
    public final int p;
    public final int q;

    private HexIndex(int p, int q) {
        this.p = p;
        this.q = q;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HexIndex hexIndex = (HexIndex) o;
        return p == hexIndex.p && q == hexIndex.q;
    }

    @Override
    public int hashCode() {
        return Objects.hash(p, q);
    }

    @NotNull
    public static HexIndex create(int p, int q) {
        return new HexIndex(p, q);
    }

    @Override
    public String toString() {
        return "{" +
                "p=" + p +
                ", q=" + q +
                '}';
    }

    /**
     * Hex grid layout:
     *      | - | U |
     *   | L | x | R |
     *     | - | D |
     */
    @NotNull
    public HexIndex go(@NotNull Direction direction) {
        int newP = p;
        int newQ = q;
        int pShift = q % 2 != 0 ? 0 : -1;

        switch (direction) {
            case LEFT:
                newP = p - 1;
                break;

            case RIGHT:
                newP = p + 1;
                break;

            case UP:
                newP += pShift + 1;
                newQ--;
                break;

            case DOWN:
                newP += pShift + 1;
                newQ++;
                break;
        }
        return HexIndex.create(newP, newQ);
    }
}
