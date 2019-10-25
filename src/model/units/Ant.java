package model.units;

import model.GameModel;
import model.HexIndex;
import model.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Ant extends Unit {
    private Ant(@NotNull Player player, @NotNull HexIndex position) {
        super(player, position, new Color(60, 232, 222));
    }

    @Override
    protected @NotNull MoveStrategy createMoveStrategy() {
        return new MoveStrategyBase() {
            @Override
            @NotNull
            public Set<HexIndex> getPossibleMoves(@NotNull HexIndex fromIndex, @NotNull GameModel model) {
                Set<HexIndex> visited = new HashSet<>();
                List<HexIndex> queue = new ArrayList<>();
                queue.add(fromIndex);

                while (!queue.isEmpty()) {
                    HexIndex currentIndex = queue.remove(queue.size() - 1);
                    if (visited.add(currentIndex)) {
                        queue.addAll(model.getNeighboursWhereCanSlide(currentIndex, currentIndex.equals(fromIndex))
                                .stream()
                                .filter(indexTo -> swarmStaysConnectedIfMove(fromIndex, indexTo, model)
                                        && !visited.contains(indexTo))
                                .collect(Collectors.toList()));
                    }
                }
                visited.remove(getPosition());
                return visited;
            }
        };
    }

    @NotNull
    public static Ant create(@NotNull Player player, @NotNull HexIndex position) {
        return new Ant(player, position);
    }
}
