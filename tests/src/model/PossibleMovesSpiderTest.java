package src.model;

import model.FieldUtils;
import model.HexIndex;
import model.Player;
import model.units.Spider;
import org.jetbrains.annotations.NotNull;
import util.ContainerUtil;

import java.util.Set;

public class PossibleMovesSpiderTest extends PossibleMovesTest<Spider> {
    @Override
    @NotNull
    protected Set<HexIndex> expectedAllowedMovesForTwoJoinedNeighbours(@NotNull HexIndex[] neighbours, int i,
                                                                       @NotNull HexIndex startPosition) {
        return Set.of(getThreeStepsNeighbour(neighbours[i], startPosition),
                getThreeStepsNeighbour(ContainerUtil.getCircular(neighbours, i - 1), startPosition));
    }

    @Override
    @NotNull
    protected Set<HexIndex> expectedAllowedMovesForOneAdjacentNeighbour(@NotNull HexIndex[] neighbours, int i,
                                                                        @NotNull HexIndex startPosition) {
        return Set.of(getThreeStepsNeighbour(neighbours[i], startPosition));
    }

    @Override
    @NotNull
    Set<HexIndex> allowedMovesThreeAdjacentNeighbours(@NotNull HexIndex startPosition,
                                                               @NotNull HexIndex[] neighbours,
                                                               int i) {
        return Set.of(getThreeStepsNeighbour(neighbours[i], startPosition),
                getThreeStepsNeighbour(ContainerUtil.getCircular(neighbours, i - 2), startPosition));
    }

    @NotNull
    @Override
    protected Spider createUnit(@NotNull HexIndex index, @NotNull Player player) {
        return Spider.create(player, index);
    }

    @NotNull
    private HexIndex getThreeStepsNeighbour(@NotNull HexIndex neighbour,
                                            @NotNull HexIndex startPosition) {
        HexIndex[] neighbourNeighbours = FieldUtils.getNeighboursIndices(neighbour);
        int idx = findIndex(neighbourNeighbours, startPosition);
        return ContainerUtil.getCircular(neighbourNeighbours, idx + 3);
    }

    private int findIndex(@NotNull HexIndex[] indices,
                          @NotNull HexIndex index) {
        for (int i = 0; i < indices.length; i++) {
            if (indices[i].equals(index)) {
                return i;
            }
        }
        return -1;
    }
}

