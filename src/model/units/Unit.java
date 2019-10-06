package model.units;

import com.sun.istack.internal.NotNull;
import model.Player;

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
