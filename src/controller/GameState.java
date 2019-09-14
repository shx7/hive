package controller;

import com.sun.istack.internal.NotNull;
import model.HexIndex;

public interface GameState {
    void selectedIndex(@NotNull HexIndex index);

    void deselected();
}
