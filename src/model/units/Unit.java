package model.units;

import model.Player;
import org.jetbrains.annotations.NotNull;

public abstract class Unit {
    @NotNull private final Player player;

    Unit(@NotNull Player player) {
        this.player = player;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }
}
