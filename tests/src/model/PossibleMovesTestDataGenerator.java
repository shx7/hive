package src.model;

import model.FieldUtils;
import model.HexIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

abstract class PossibleMovesTestDataGenerator {
    @NotNull private final HexIndex myStartPosition;
    @NotNull private final HexIndex[] myNeighbours;
    @NotNull private final Runnable myDefaultSetUp;

    PossibleMovesTestDataGenerator(@NotNull HexIndex startPosition, @NotNull Runnable myDefaultSetUp) {
        this.myStartPosition = startPosition;
        this.myNeighbours = FieldUtils.getNeighboursIndices(startPosition);
        this.myDefaultSetUp = myDefaultSetUp;
    }

    @NotNull
    HexIndex getStartPosition() {
        return myStartPosition;
    }

    @NotNull
    HexIndex[] getNeighbours() {
        return myNeighbours;
    }

    void setUp(int neighbourIndex) {
        myDefaultSetUp.run();
    }

    @NotNull
    String getTestName(int neighbourIndex) {
        return "Move from: " + getStartPosition() + " to: " + getNeighbours()[neighbourIndex];
    }

    @NotNull
    abstract Set<HexIndex> allowedMoves(int neighbourIndex);
}
