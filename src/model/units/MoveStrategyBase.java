package model.units;

import model.GameModel;
import model.HexIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ContainerUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static model.FieldUtils.getNeighboursIndices;

abstract class MoveStrategyBase implements MoveStrategy {
    /**
     *
     * @param from is null, if after move hex becomes empty, and not null,
     *             if there are more than 1 unit on the hex
     * @return true if swarm is connected after performing specified move
     *         without changing model and performing move
     */
    boolean swarmStaysConnectedIfMove(@NotNull HexIndex from,
                                      @NotNull HexIndex to,
                                      @NotNull GameModel model) {
        int expectedVisitedHexes = model.getNotEmptyHexIndices().size();
        if (model.isEmptyHex(to)) {
            ++expectedVisitedHexes;
        }

        if (model.getUnitsAmount(from) == 1) {
            --expectedVisitedHexes;
        }
        else { // we won't empty `from` hex, therefore shouldn't blacklist it from visiting
            from = null;
        }
        return countConnectedHexesIfMove(from, to, model) == expectedVisitedHexes;
    }

    /**
     * @param from is null when it's allowed to visit this hex
     *             is not null, when we blacklist it from visiting
     */
    private int countConnectedHexesIfMove(@Nullable HexIndex from,
                                          @NotNull HexIndex to,
                                          @NotNull GameModel model) {
        Set<HexIndex> visited = new HashSet<>();
        visited.add(to);
        List<HexIndex> queue = new ArrayList<>(ContainerUtil.filterNot(getNeighboursIndices(to), model::isEmptyHex));

        while (!queue.isEmpty()) {
            HexIndex index = queue.remove(queue.size() - 1);
            if (!index.equals(from) && !model.isEmptyHex(index) && visited.add(index)) {
                queue.addAll(ContainerUtil.filterNot(getNeighboursIndices(index), model::isEmptyHex));
            }
        }
        return visited.size();
    }
}
