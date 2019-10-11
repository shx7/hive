package model.units;

import model.GameModel;
import model.HexIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface MoveStrategy {
    @NotNull
    Set<HexIndex> getPossibleMoves(@NotNull HexIndex hexIndex, @NotNull GameModel model);
}
