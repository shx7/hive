package src.model;

import model.*;
import model.units.Unit;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import util.ContainerUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class PossibleMovesTest<T extends Unit> {
    private GameModel myModel;
    Player player1;

    @BeforeEach
    void setUp() {
        player1 = new Player("white", Color.white);
        myModel = new GameModel(Collections.singletonList(player1));
    }

    /**
     *  Test scheme
     *   |   | - |
     * | - | x |   |
     *   |   |   |
     */
    @TestFactory
    @NotNull
    Stream<DynamicTest> testBlockedPassageByNeighbours() {
        return generateTestsForOddAndEvenRow(index -> generateTests(index,
                (neighbours, i) -> {
                    HexIndex leftNeighbour = ContainerUtil.getCircular(neighbours, i + 1);
                    HexIndex rightNeighbour = ContainerUtil.getCircular(neighbours, i - 1);
                    putUnit(leftNeighbour);
                    putUnit(rightNeighbour);
                },
                this::rightLeftNeighbourTestName,
                this::expectedAllowedMovesForBlockedPassage));
    }

    @NotNull
    String rightLeftNeighbourTestName(@NotNull HexIndex startPosition, int index) {
        HexIndex[] neighbours = FieldUtils.getNeighboursIndices(startPosition);
        HexIndex leftNeighbour = ContainerUtil.getCircular(neighbours, index + 1);
        HexIndex rightNeighbour = ContainerUtil.getCircular(neighbours, index - 1);
        return "Move from: " + startPosition + " to: " + neighbours[index] + " obstacles: " + leftNeighbour + ", " + rightNeighbour;
    }

    @NotNull
    Stream<DynamicTest> generateTests(@NotNull HexIndex startPosition,
                                      @NotNull UnitsSetUpInTests setupUnits,
                                      @NotNull TestNameGenerator nameGenerator,
                                      @NotNull BiFunction<HexIndex[], Integer, Set<HexIndex>> allowedMovesGenerator) {
        HexIndex[] neighbours = FieldUtils.getNeighboursIndices(startPosition);
        return IntStream.range(0, 6)
                .mapToObj(i -> DynamicTest.dynamicTest(nameGenerator.generate(startPosition, i),
                        () -> {
                            setUp();
                            setupUnits.setUp(neighbours, i);
                            T unit = putUnit(startPosition);
                            assertPossibleMoves(unit, allowedMovesGenerator.apply(neighbours, i));
                        }));
    }

    @NotNull
    protected abstract Set<HexIndex> expectedAllowedMovesForBlockedPassage(@NotNull HexIndex[] neighbours, int i);

    /**
     *  Test scheme
     *   | - |   |
     * | - | x |   |
     *   |   |   |
     */
    @TestFactory
    @NotNull
    Stream<DynamicTest> testTwoJoinedNeighbours() {
        return generateTestsForOddAndEvenRow(this::generateJoinedNeighboursTests);
    }

    @NotNull
    private Stream<DynamicTest> generateJoinedNeighboursTests(@NotNull HexIndex startPosition) {
        HexIndex[] neighbours = FieldUtils.getNeighboursIndices(startPosition);
        return IntStream.range(0, 6)
                .mapToObj(i -> {
                    HexIndex center = neighbours[i];
                    HexIndex tail = ContainerUtil.getCircular(neighbours, i - 1);
                    return DynamicTest.dynamicTest("Start position: " + startPosition
                                    + " neighbours: " + center + ", " + tail,
                            () -> {
                                setUp();
                                T unit = putUnit(startPosition);
                                putUnit(center);
                                putUnit(tail);
                                assertPossibleMoves(unit, expectedAllowedMovesForTwoJoinedNeighbours(neighbours, i));
                            });
                });
    }

    @NotNull
    protected abstract Set<HexIndex> expectedAllowedMovesForTwoJoinedNeighbours(@NotNull HexIndex[] neighbours,
                                                                                int i);

    /**
     *  Test scheme
     *   |   |   |
     * | - | x |   |
     *   |   |   |
     */
    @TestFactory
    @NotNull
    Stream<DynamicTest> testOneAdjacentNeighbour() {
        return generateTestsForOddAndEvenRow(this::generateOneAdjacentNeighbourTests);
    }

    @NotNull
    private Stream<DynamicTest> generateOneAdjacentNeighbourTests(@NotNull HexIndex startPosition) {
        HexIndex[] neighbours = FieldUtils.getNeighboursIndices(startPosition);
        return IntStream.range(0, 6)
                .mapToObj(i -> DynamicTest.dynamicTest("Start position: " + startPosition + " neighbour: " + neighbours[i],
                        () -> {
                            setUp();
                            T unit = putUnit(startPosition);
                            putUnit(neighbours[i]);
                            assertPossibleMoves(unit, expectedAllowedMovesForOneAdjacentNeighbour(neighbours, i));
                        }));
    }

    @NotNull
    protected abstract Set<HexIndex> expectedAllowedMovesForOneAdjacentNeighbour(@NotNull HexIndex[] neighbours, int i);

    @NotNull
    protected abstract T createUnit(@NotNull HexIndex index, @NotNull Player player);

    void assertPossibleMoves(@NotNull Unit unit, @NotNull Set<HexIndex> expectedMoves) {
        assertEquals(expectedMoves, unit.getPossibleMoves(myModel));
    }

    @NotNull
    T putUnit(@NotNull HexIndex index) {
        T result = createUnit(index, player1);
        myModel.put(result);
        return result;
    }

    @NotNull
    T putUnit(@NotNull HexIndex index, @NotNull List<Direction> path) {
        T startUnit = putUnit(index);
        for (Direction d : path) {
            index = index.go(d);
            putUnit(index);
        }
        return startUnit;
    }

    @NotNull
    Set<HexIndex> getIndices(@NotNull HexIndex[] neighbours, int... circularIndices) {
        return Arrays.stream(circularIndices).mapToObj(idx -> ContainerUtil.getCircular(neighbours, idx))
                .collect(Collectors.toSet());
    }

    @NotNull
    static Stream<DynamicTest> generateTestsForOddAndEvenRow(@NotNull Function<HexIndex, Stream<DynamicTest>> testGenerator) {
        return Stream.concat(testGenerator.apply(HexIndex.create(0, 0)),
                testGenerator.apply(HexIndex.create(1, 0)));
    }
}
