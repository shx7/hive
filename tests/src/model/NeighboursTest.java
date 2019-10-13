package src.model;

import model.FieldUtils;
import model.HexIndex;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NeighboursTest {
    @Test
    void testNeighbours() {
        assertNeighbours(HexIndex.create(0, 0),
                new HexIndex[]{
                        HexIndex.create(-1, 0),
                        HexIndex.create(-1, -1), HexIndex.create(0, -1),
                        HexIndex.create(1, 0),
                        HexIndex.create(-1, 1), HexIndex.create(0, 1)
                });

        assertNeighbours(HexIndex.create(0, 1),
                new HexIndex[]{
                        HexIndex.create(-1, 1),
                        HexIndex.create(0, 0), HexIndex.create(1, 0),
                        HexIndex.create(1, 1),
                        HexIndex.create(1, 2), HexIndex.create(0, 2)
                });
    }

    private static void assertNeighbours(@NotNull HexIndex index, @NotNull HexIndex[] expectedNeighbours) {
        Arrays.sort(expectedNeighbours, Comparator.comparing(HexIndex::toString));
        HexIndex[] actualNeighbours = FieldUtils.getNeighboursIndices(index);
        Arrays.sort(actualNeighbours, Comparator.comparing(HexIndex::toString));
        assertEquals(Arrays.toString(expectedNeighbours), Arrays.toString(actualNeighbours));
    }
}
