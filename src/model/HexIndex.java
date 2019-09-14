package model;

import java.util.Objects;

public final class HexIndex {
    public final int p;
    public final int q;

    public HexIndex(int p, int q) {
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
}
