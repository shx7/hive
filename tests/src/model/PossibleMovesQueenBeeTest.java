package src.model;

import model.FieldUtils;
import model.HexIndex;
import model.Player;
import model.units.BeeQueen;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import util.ContainerUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static model.Direction.*;

class PossibleMovesQueenBeeTest extends PossibleMovesTest<BeeQueen> {

    @Test
    void testSimple() {
        HexIndex startPosition = HexIndex.create(0, 0);
        BeeQueen unit = putUnit(startPosition);
        assertPossibleMoves(unit, ContainerUtil.setOf(FieldUtils.getNeighboursIndices(startPosition)));
    }

    @Test
    void testSeparationFromSwarm() {
        HexIndex startPosition = HexIndex.create(0, 0);
        BeeQueen unit = putUnit(startPosition, Arrays.asList(UP, RIGHT, RIGHT, DOWN, DOWN, LEFT, LEFT));
        assertPossibleMoves(unit,
                ContainerUtil.setOf(startPosition.go(UP).go(LEFT), startPosition.go(RIGHT),startPosition.go(DOWN)));
    }

    @Override
    @NotNull
    protected Set<HexIndex> expectedAllowedMovesForOneAdjacentNeighbour(@NotNull HexIndex[] neighbours, int i) {
        return getIndices(neighbours, i + 1, i - 1);
    }

    @Override
    @NotNull
    protected Set<HexIndex> expectedAllowedMovesForBlockedPassage(@NotNull HexIndex[] neighbours, int i) {
        return Collections.emptySet();
    }

    @Override
    @NotNull
    protected Set<HexIndex> expectedAllowedMovesForTwoJoinedNeighbours(@NotNull HexIndex[] neighbours, int i) {
        return getIndices(neighbours, i + 1, i - 2);
    }

    @Override
    @NotNull
    protected BeeQueen createUnit(@NotNull HexIndex index, @NotNull Player player) {
        return BeeQueen.create(player1, index);
    }
}