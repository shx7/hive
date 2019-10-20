package src.model;

import model.HexIndex;
import model.Player;
import model.units.Beetle;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import util.ContainerUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PossibleMovesBeetleTest extends PossibleMovesTest<Beetle> {
    @Override
    @NotNull
    protected Set<HexIndex> expectedAllowedMovesForBlockedPassage(@NotNull HexIndex[] neighbours, int i) {
        return Collections.emptySet();
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

    @TestFactory
    @NotNull
    Stream<DynamicTest> testBlockedPassageOnEvenlyFilledLevel() {
        return generateTestsForOddAndEvenRow(startIndex ->
                generateTests(startIndex,
                        (neighbours, i) -> {
                            Arrays.stream(neighbours).forEach(this::putUnit);
                            putUnit(startIndex);
                            putUnit(ContainerUtil.getCircular(neighbours, i - 1));
                            putUnit(ContainerUtil.getCircular(neighbours, i + 1));
                        },
                        this::rightLeftNeighbourTestName,
                        (neighbours, i) -> IntStream.range(0, neighbours.length)
                                .filter(k -> k != i)
                                .mapToObj(k -> neighbours[k])
                                .collect(Collectors.toSet())
                )
        );
    }

    @Override
    @NotNull
    protected Beetle createUnit(@NotNull HexIndex index, @NotNull Player player) {
        return Beetle.create(player, index);
    }
}
