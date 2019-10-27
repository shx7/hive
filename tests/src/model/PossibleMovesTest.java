package src.model;

import model.*;
import model.units.Unit;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import util.ContainerUtil;

import java.awt.*;
import java.util.List;
import java.util.*;
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
        return generateTestsForOddAndEvenRow(startPosition -> generateTests(
                new PossibleMovesTestDataGeneratorBase(startPosition) {
                    @Override
                    void setUp(int neighbourIndex) {
                        super.setUp(neighbourIndex);
                        HexIndex leftNeighbour = ContainerUtil.getCircular(getNeighbours(), neighbourIndex + 1);
                        HexIndex rightNeighbour = ContainerUtil.getCircular(getNeighbours(), neighbourIndex - 1);
                        putUnit(leftNeighbour);
                        putUnit(rightNeighbour);
                    }

                    @Override
                    @NotNull
                    protected Set<HexIndex> getObstacles(int neighbourIndex) {
                        HexIndex right = ContainerUtil.getCircular(getNeighbours(), neighbourIndex + 1);
                        HexIndex left = ContainerUtil.getCircular(getNeighbours(), neighbourIndex - 1);
                        return Set.of(left, right);
                    }

                    @Override
                    @NotNull Set<HexIndex> allowedMoves(int neighbourIndex) {
                        return expectedAllowedMovesForBlockedPassage(getNeighbours(), neighbourIndex);
                    }
                }));
    }

    @NotNull
    Stream<DynamicTest> generateTests(@NotNull PossibleMovesTestDataGenerator testDataGenerator) {
        return IntStream.range(0, 6)
                .mapToObj(i -> DynamicTest.dynamicTest(testDataGenerator.getTestName(i),
                        () -> {
                            testDataGenerator.setUp(i);
                            T unit = putUnit(testDataGenerator.getStartPosition());
                            assertPossibleMoves(unit, testDataGenerator.allowedMoves(i));
                        }));
    }

    @NotNull
    protected Set<HexIndex> expectedAllowedMovesForBlockedPassage(@NotNull HexIndex[] neighbours, int i) {
        return Collections.emptySet();
    }

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
        return generateTests(new PossibleMovesTestDataGeneratorBase(startPosition) {
            @Override
            void setUp(int neighbourIndex) {
                super.setUp(neighbourIndex);
                putUnit(getNeighbours()[neighbourIndex]); // center
                putUnit(ContainerUtil.getCircular(getNeighbours(), neighbourIndex - 1)); // tail
            }

            @Override
            protected @NotNull Set<HexIndex> getObstacles(int neighbourIndex) {
                HexIndex center = getNeighbours()[neighbourIndex];
                HexIndex tail = ContainerUtil.getCircular(getNeighbours(), neighbourIndex - 1);
                return Set.of(center, tail);
            }

            @Override
            @NotNull Set<HexIndex> allowedMoves(int neighbourIndex) {
                return expectedAllowedMovesForTwoJoinedNeighbours(getNeighbours(), neighbourIndex, getStartPosition());
            }
        });
    }

    @NotNull
    protected abstract Set<HexIndex> expectedAllowedMovesForTwoJoinedNeighbours(@NotNull HexIndex[] neighbours, int i,
                                                                                @NotNull HexIndex startPosition);

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
                            assertPossibleMoves(unit, expectedAllowedMovesForOneAdjacentNeighbour(neighbours, i, startPosition));
                        }));
    }

    @NotNull
    protected abstract Set<HexIndex> expectedAllowedMovesForOneAdjacentNeighbour(@NotNull HexIndex[] neighbours, int i,
                                                                                 @NotNull HexIndex startPosition);

    /**
     *  Test scheme
     *     | - |   |
     *   | - | x |   |
     *     | - |   |
     */
    @TestFactory
    Stream<DynamicTest> testThreeAdjacentNeighbours() {
        return generateTestsForOddAndEvenRow(startPosition ->
                generateTests(new PossibleMovesTestDataGeneratorBase(startPosition) {
                    @Override
                    void setUp(int neighbourIndex) {
                        super.setUp(neighbourIndex);
                        putUnit(getNeighbour(neighbourIndex));
                        putUnit(getNeighbour(neighbourIndex - 1));
                        putUnit(getNeighbour(neighbourIndex - 2));
                    }

                    @Override
                    protected @NotNull Set<HexIndex> getObstacles(int neighbourIndex) {
                        return Set.of(getNeighbour(neighbourIndex),
                                      getNeighbour(neighbourIndex - 1),
                                      getNeighbour(neighbourIndex - 2));
                    }

                    @Override
                    @NotNull Set<HexIndex> allowedMoves(int neighbourIndex) {
                        return allowedMovesThreeAdjacentNeighbours(getStartPosition(), getNeighbours(), neighbourIndex);
                    }
                })
        );
    }

    @NotNull
    abstract Set<HexIndex> allowedMovesThreeAdjacentNeighbours(@NotNull HexIndex startPosition,
                                                               @NotNull HexIndex[] neighbours,
                                                               int index);

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

    @NotNull
    Set<HexIndex> getSurroundingHexes(HexIndex[] neighbours, int... neighbourIndices) {
        Set<HexIndex> result = new HashSet<>();
        for (int idx : neighbourIndices) {
            Collections.addAll(result, FieldUtils.getNeighboursIndices(ContainerUtil.getCircular(neighbours, idx)));
        }
        for (int idx : neighbourIndices) {
            result.remove(ContainerUtil.getCircular(neighbours, idx));
        }
        return result;
    }

    protected abstract class PossibleMovesTestDataGeneratorBase extends PossibleMovesTestDataGenerator {
        PossibleMovesTestDataGeneratorBase(@NotNull HexIndex startPosition) {
            super(startPosition, PossibleMovesTest.this::setUp);
        }

        @NotNull
        protected Set<HexIndex> getObstacles(int neighbourIndex) {
            return Collections.emptySet();
        }

        @Override
        @NotNull String getTestName(int neighbourIndex) {
            String obstaclesStr = getObstacles(neighbourIndex)
                    .stream()
                    .map(HexIndex::toString)
                    .collect(Collectors.joining(", "));
            if (!obstaclesStr.isEmpty()) {
                obstaclesStr = " obstacles: " + obstaclesStr;
            }
            return super.getTestName(neighbourIndex) + obstaclesStr;
        }

        @NotNull
        HexIndex getNeighbour(int index) {
            return ContainerUtil.getCircular(getNeighbours(), index);
        }
    }
}
