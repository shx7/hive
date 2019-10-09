package model;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Player {
    @NotNull private final String name;
    @NotNull public final Color color;

    public Player(@NotNull String name, @NotNull Color color) {
        this.name = name;
        this.color = color;
    }
}
