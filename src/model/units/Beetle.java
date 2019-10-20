package model.units;

import model.FieldUtils;
import model.GameModel;
import model.HexIndex;
import model.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Beetle extends Unit {
    private Beetle(@NotNull Player player, @NotNull HexIndex position) {
        super(player, position, new Color(194, 69, 180));
    }

    @Override
    protected @NotNull MoveStrategy createMoveStrategy() {
        return new MoveStrategyBase() {
            @Override
            @NotNull
            public Set<HexIndex> getPossibleMoves(@NotNull HexIndex fromIndex, @NotNull GameModel model) {
                int unitsAmount = model.getUnitsAmount(getPosition());
                final Set<HexIndex> result = new HashSet<>();
                Set<HexIndex> neighboursWhereCanSlide = model.getNeighboursWhereCanSlide(getPosition());

                for (HexIndex neighbourHex : FieldUtils.getNeighboursIndices(fromIndex)) {
                    if (swarmStaysConnectedIfMove(fromIndex, neighbourHex, model)
                            && (neighboursWhereCanSlide.contains(neighbourHex) // can squeeze through
                                || unitsAmount <= model.getUnitsAmount(neighbourHex))) { // can climb
                        result.add(neighbourHex);
                    }
                }
                return result;
            }
        };
    }

    @NotNull
    public static Beetle create(@NotNull Player player, @NotNull HexIndex position) {
        return new Beetle(player, position);
    }
}
