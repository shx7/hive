package src.model;

import model.*;
import model.units.BeeQueen;
import model.units.Unit;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import util.ContainerUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static model.Direction.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PossibleMovesQueenBeeTest {
    private GameModel myModel;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        player1 = new Player("white", Color.white);
        player2 = new Player("white", Color.black);
        myModel = new GameModel(Arrays.asList(player1, player2));
    }

    @Test
    void testSimple() {
        HexIndex startPosition = HexIndex.create(0, 0);
        BeeQueen unit = putQueen(startPosition);
        assertEquals(ContainerUtil.setOf(FieldUtils.getNeighboursIndices(startPosition)), unit.getPossibleMoves(myModel));
    }

    @TestFactory
    @NotNull
    Stream<DynamicTest> testOneAdjacentNeighbour() {
        return generateTestsForOddAndEvenRow(this::generateOneAdjacentNeighbourTests);
    }

    @NotNull
    private Stream<DynamicTest> generateOneAdjacentNeighbourTests(HexIndex startPosition) {
        HexIndex[] neighbours = FieldUtils.getNeighboursIndices(startPosition);
        return IntStream.range(0, 6)
                .mapToObj(i -> DynamicTest.dynamicTest("Start position: " + startPosition + " neighbour: " + neighbours[i],
                        () -> {
                            setUp();
                            BeeQueen unit = putQueen(startPosition);
                            putQueen(neighbours[i]);
                            assertEquals(ContainerUtil.setOf(ContainerUtil.getCircular(neighbours, i + 1),
                                    ContainerUtil.getCircular(neighbours, i - 1)),
                                    unit.getPossibleMoves(myModel));
                        }));
    }

    @TestFactory
    @NotNull
    Stream<DynamicTest> testBlockedPassageByNeighbours() {
        return generateTestsForOddAndEvenRow(this::generateBlockedPassageByNeighbours);
    }

    private Stream<DynamicTest> generateBlockedPassageByNeighbours(@NotNull HexIndex startPosition) {
        HexIndex[] neighbours = FieldUtils.getNeighboursIndices(startPosition);
        return IntStream.range(0, 6)
                .mapToObj(i -> {
                    HexIndex leftNeighbour = ContainerUtil.getCircular(neighbours, i + 1);
                    HexIndex rightNeighbour = ContainerUtil.getCircular(neighbours, i - 1);
                    return DynamicTest.dynamicTest("Start position: " + startPosition + " neighbours: "
                                    + leftNeighbour + ", " + rightNeighbour,
                            () -> {
                                setUp();
                                BeeQueen unit = putQueen(startPosition);
                                putQueen(leftNeighbour);
                                putQueen(rightNeighbour);
                                assertEquals(Collections.emptySet(), unit.getPossibleMoves(myModel));
                            });
                });
    }

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
                    HexIndex headMove = ContainerUtil.getCircular(neighbours, i + 1);
                    HexIndex center = neighbours[i];
                    HexIndex tail = ContainerUtil.getCircular(neighbours, i - 1);
                    HexIndex tailMove = ContainerUtil.getCircular(neighbours, i - 2);
                    return DynamicTest.dynamicTest("Start position: " + startPosition
                                    + " neighbours: " + center + ", " + tail,
                            () -> {
                                setUp();
                                BeeQueen unit = putQueen(startPosition);
                                putQueen(center);
                                putQueen(tail);
                                assertEquals(ContainerUtil.setOf(headMove, tailMove), unit.getPossibleMoves(myModel));
                            });
                });
    }

    @Test
    void testSeparationFromSwarm() {
        HexIndex startPosition = HexIndex.create(0, 0);
        BeeQueen unit = putQueens(startPosition, Arrays.asList(UP, RIGHT, RIGHT, DOWN, DOWN, LEFT, LEFT));
        assertPossibleMoves(unit,
                ContainerUtil.setOf(startPosition.go(UP).go(LEFT), startPosition.go(RIGHT),startPosition.go(DOWN)));
    }

    private void assertPossibleMoves(@NotNull Unit unit, @NotNull Set<Object> expectedMoves) {
        assertEquals(expectedMoves, unit.getPossibleMoves(myModel));
    }

    @NotNull
    private BeeQueen putQueens(@NotNull HexIndex index, @NotNull List<Direction> path) {
        BeeQueen startUnit = putQueen(index);
        for (Direction d : path) {
            index = index.go(d);
            putQueen(index);
        }
        return startUnit;
    }

    @NotNull
    private static Stream<DynamicTest> generateTestsForOddAndEvenRow(@NotNull Function<HexIndex, Stream<DynamicTest>> testGenerator) {
        return Stream.concat(testGenerator.apply(HexIndex.create(0, 0)),
                testGenerator.apply(HexIndex.create(1, 0)));
    }

    @NotNull
    private BeeQueen putQueen(@NotNull HexIndex index) {
        BeeQueen result = BeeQueen.create(player1, index);
        myModel.put(result);
        return result;
    }
}