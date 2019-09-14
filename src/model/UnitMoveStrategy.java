package model;

import com.sun.istack.internal.NotNull;

import java.util.List;

public interface UnitMoveStrategy {
    @NotNull
    List<HexIndex> getPossibleMoves(@NotNull HexIndex unitIndex, @NotNull Player player);
}
