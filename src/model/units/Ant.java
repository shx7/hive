package model.units;

import model.HexIndex;
import model.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Ant extends Unit {
    private Ant(@NotNull Player player, @NotNull HexIndex position) {
        super(player, position, new Color(60, 232, 222));
    }

    @Override
    @NotNull
    protected MoveStrategy createMoveStrategy() {
        return new MoveAroundHiveStrategy(null);
    }

    @NotNull
    public static Ant create(@NotNull Player player, @NotNull HexIndex position) {
        return new Ant(player, position);
    }
}
