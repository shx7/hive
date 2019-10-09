package model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class HexIndex { // TODO: reevaluate selected corrdinate system
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
        return "HexIndex{" +
                "p=" + p +
                ", q=" + q +
                '}';
    }
}
