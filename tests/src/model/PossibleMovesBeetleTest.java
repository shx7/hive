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
                generateTests(new PossibleMovesTestDataGeneratorBase(startIndex) {
                                  @Override
                                  void setUp(int neighbourIndex) {
                                      super.setUp(neighbourIndex);
                                      Arrays.stream(getNeighbours()).forEach(PossibleMovesBeetleTest.this::putUnit);
                                      putUnit(startIndex);
                                      putUnit(ContainerUtil.getCircular(getNeighbours(), neighbourIndex - 1));
                                      putUnit(ContainerUtil.getCircular(getNeighbours(), neighbourIndex + 1));
                                  }

                                  @Override
                                  @NotNull Set<HexIndex> allowedMoves(int neighbourIndex) {
                                      return IntStream.range(0, getNeighbours().length)
                                              .filter(k -> k != neighbourIndex)
                                              .mapToObj(k -> getNeighbours()[k])
                                              .collect(Collectors.toSet());
                                  }
                              }
                )
        );
    }

    @Override
    @NotNull
    protected Beetle createUnit(@NotNull HexIndex index, @NotNull Player player) {
        return Beetle.create(player, index);
    }
}
