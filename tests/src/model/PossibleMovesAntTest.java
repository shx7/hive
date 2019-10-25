package src.model;

import model.FieldUtils;
import model.HexIndex;
import model.Player;
import model.units.Ant;
import org.jetbrains.annotations.NotNull;
import util.ContainerUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PossibleMovesAntTest extends PossibleMovesTest<Ant> {
    @Override
    protected @NotNull Set<HexIndex> expectedAllowedMovesForTwoJoinedNeighbours(@NotNull HexIndex[] neighbours, int i,
                                                                                @NotNull HexIndex startPosition) {
        Set<HexIndex> result = new HashSet<>();
        HexIndex head = neighbours[i];
        HexIndex tail = ContainerUtil.getCircular(neighbours, i - 1);
        Collections.addAll(result, FieldUtils.getNeighboursIndices(head));
        Collections.addAll(result, FieldUtils.getNeighboursIndices(tail));

        result.remove(startPosition);
        result.remove(head);
        result.remove(tail);
        return result;
    }

    @Override
    protected @NotNull Set<HexIndex> expectedAllowedMovesForOneAdjacentNeighbour(@NotNull HexIndex[] neighbours, int i,
                                                                                 @NotNull HexIndex startPosition) {
        Set<HexIndex> result = new HashSet<>();
        Collections.addAll(result, FieldUtils.getNeighboursIndices(neighbours[i]));
        result.remove(startPosition);
        return result;
    }

    @NotNull
    @Override
    protected Ant createUnit(@NotNull HexIndex index, @NotNull Player player) {
        return Ant.create(player, index);
    }
}
