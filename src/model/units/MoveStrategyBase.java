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

public abstract class MoveStrategyBase implements MoveStrategy {
    public List<HexIndex> getNeighboursWhereCanSlide(@NotNull HexIndex from,
                                                     @NotNull GameModel model) {
        final List<HexIndex> result = new ArrayList<>();
        HexIndex[] neighboursIndices = getNeighboursIndices(from);
        int length = neighboursIndices.length;
        for (int i = 0; i < length; i++) {
            HexIndex neighbourHex = neighboursIndices[i];
            HexIndex leftNeighbourHex = ContainerUtil.getCircular(neighboursIndices, i - 1);
            HexIndex rightNeighbourHex = ContainerUtil.getCircular(neighboursIndices, i + 1);
            if (model.isEmptyHex(neighbourHex)
                    && (model.isEmptyHex(leftNeighbourHex) || model.isEmptyHex(rightNeighbourHex))) {
                result.add(neighbourHex);
            }
        }
        return result;
    }

    /**
     *
     * @param from is null, if after move hex becomes empty, and not null,
     *             if there are more than 1 unit on the hex
     * @return true if swarm is connected after performing specified move
     *         without changing model and performing move
     */
    protected boolean swarmStaysConnectedIfMove(@Nullable HexIndex from,
                                                @NotNull HexIndex to,
                                                @NotNull GameModel model) {
        return countConnectedHexesIfMove(from, to, model) >= model.getNotEmptyHexIndices().size(); // TODO: fix condition for stack movement
    }

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
