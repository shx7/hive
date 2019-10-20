package src.model;

import model.HexIndex;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface UnitsSetUpInTests {
    void setUp(@NotNull HexIndex[] neighbours, int neighbourIndex);
}
