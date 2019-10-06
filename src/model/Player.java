package model;

import com.sun.istack.internal.NotNull;

import java.awt.*;

public class Player {
    @NotNull public final String name;
    @NotNull public final Color color;

    public Player(@NotNull String name, Color color) {
        this.name = name;
        this.color = color;
    }
}
