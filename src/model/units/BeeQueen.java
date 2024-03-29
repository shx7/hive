package model.units;

import model.GameModel;
import model.HexIndex;
import model.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class BeeQueen extends Unit {
    private BeeQueen(@NotNull Player player, @NotNull HexIndex position) {
        super(player, position, new Color(246, 141, 21));
    }

    @Override
    public @NotNull MoveStrategy createMoveStrategy() {
        return new MoveStrategyBase() {
            @NotNull
            @Override
            public Set<HexIndex> getPossibleMoves(@NotNull HexIndex fromIndex, @NotNull GameModel model) {
                final Set<HexIndex> result = new HashSet<>();
                for (HexIndex neighbourHex : model.getNeighboursWhereCanSlide(fromIndex)) {
                    if (swarmStaysConnectedIfMove(fromIndex, neighbourHex, model)) { // will touch the swarm
                        result.add(neighbourHex);
                    }
                }
                return result;
            }
        };
    }

    @NotNull
    public static BeeQueen create(@NotNull Player player, @NotNull HexIndex position) {
        return new BeeQueen(player, position);
    }
}
