package src.model;

import model.HexIndex;
import model.Player;
import model.units.Beetle;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PossibleMovesBeetleTest extends PossibleMovesTest<Beetle> {
    @Override
    @NotNull
    protected Set<HexIndex> expectedAllowedMovesForBlockedPassage(@NotNull HexIndex[] neighbours, int i) {
        return getIndices(neighbours, i - 1, i + 1);
    }

    @Override
    @NotNull
    protected Set<HexIndex> expectedAllowedMovesForTwoJoinedNeighbours(@NotNull HexIndex[] neighbours, int i) {
        return getIndices(neighbours, i + 1, i, i - 1, i - 2);
    }

    @Override
    @NotNull
    protected Set<HexIndex> expectedAllowedMovesForOneAdjacentNeighbour(@NotNull HexIndex[] neighbours, int i) {
        return getIndices(neighbours, i + 1, i, i - 1);
    }

    @Override
    @NotNull
    protected Beetle createUnit(@NotNull HexIndex index, @NotNull Player player) {
        return Beetle.create(player, index);
    }
}
