package model.units;

import model.GameModel;
import model.HexIndex;
import model.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ContainerUtil;

import java.util.*;

import static model.FieldUtils.getNeighboursIndices;

public class BeeQueen extends Unit {
    public BeeQueen(@NotNull Player player, @NotNull HexIndex position) {
        super(player, position);
    }

    @Override
    public @NotNull MoveStrategy createMoveStrategy() {
        return new MoveStrategy() {
            @NotNull
            @Override
            public Set<HexIndex> getPossibleMoves(@Nullable HexIndex hexIndex, @NotNull GameModel model) {
                List<Unit> unitList = hexIndex != null ? model.getUnitList(hexIndex) : null;
                if (unitList == null || unitList.isEmpty()) {
                    return Collections.emptySet();
                }
                Set<HexIndex> result = new HashSet<>();

                HexIndex[] neighboursIndices = getNeighboursIndices(hexIndex);
                int length = neighboursIndices.length;
                for (int i = 0; i < length; i++) {
                    HexIndex neighbourHex = neighboursIndices[i];
                    HexIndex leftNeighbourHex = ContainerUtil.getCircular(neighboursIndices, i - 1);
                    HexIndex rightNeighbourHex = ContainerUtil.getCircular(neighboursIndices, i + 1);
                    if (model.isEmptyHex(neighbourHex)
                            && (model.isEmptyHex(leftNeighbourHex) || model.isEmptyHex(rightNeighbourHex)) // can slide
                            && swarmStaysConnectedIfMove(unitList.size() == 1 ? hexIndex : null, neighbourHex, model) // will touch the swarm
                    ) {
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
            private boolean swarmStaysConnectedIfMove(@Nullable HexIndex from,
                                                      @NotNull HexIndex to,
                                                      @NotNull GameModel model) {
                return countConnectedHexesIfMove(from, to, model) >= model.getNotEmptyHexIndices().size();
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
        };
    }
}
