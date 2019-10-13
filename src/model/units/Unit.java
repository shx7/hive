package model.units;

import model.GameModel;
import model.HexIndex;
import model.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Set;

public abstract class Unit {
    @NotNull private final Player myPlayer;
    @NotNull private final MoveStrategy myMoveStrategy;
    @NotNull private HexIndex myPosition;
    @NotNull private Color myColor; // TODO: replace with icon

    Unit(@NotNull Player player, @NotNull HexIndex position, @NotNull Color color) {
        myPlayer = player;
        myPosition = position;
        myColor = color;
        myMoveStrategy = createMoveStrategy();
    }

    @NotNull
    public Player getPlayer() {
        return myPlayer;
    }

    public void setPosition(@NotNull HexIndex position) {
        myPosition = position;
    }

    @NotNull
    public HexIndex getPosition() {
        return myPosition;
    }

    @NotNull
    public Set<HexIndex> getPossibleMoves(@NotNull GameModel model) {
        return myMoveStrategy.getPossibleMoves(myPosition, model);
    }

    @NotNull
    public Color getColor() {
        return myColor;
    }

    @NotNull
    protected abstract MoveStrategy createMoveStrategy();
}
