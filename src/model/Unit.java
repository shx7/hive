package model;

import com.sun.istack.internal.NotNull;

public abstract class Unit {
    private final Player myPlayer;
    public final UnitType type;
    private final UnitMoveStrategy myMoveStrategy;


    public Unit(@NotNull Player myPlayer, @NotNull UnitType type, @NotNull UnitMoveStrategy myMoveStrategy) {
        this.myPlayer = myPlayer;
        this.type = type;
        this.myMoveStrategy = myMoveStrategy;
    }
}
