package model.units;

import model.HexIndex;
import model.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Spider extends Unit {
    private static final int STEPS_RESTRICTION = 3;

    private Spider(@NotNull Player player, @NotNull HexIndex position) {
        super(player, position, new Color(93, 16, 75));
    }

    @Override
    @NotNull
    protected MoveStrategy createMoveStrategy() {
        return new MoveAroundHiveStrategy(STEPS_RESTRICTION);
    }

    @NotNull
    public static Spider create(@NotNull Player player, @NotNull HexIndex position) {
        return new Spider(player, position);
    }
}
