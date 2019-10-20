package src.model;

import model.HexIndex;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface TestNameGenerator {
    String generate(@NotNull HexIndex startPosition, int neighbourIndex);
}
