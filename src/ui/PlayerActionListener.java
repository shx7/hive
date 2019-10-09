package ui;

import model.HexIndex;
import org.jetbrains.annotations.NotNull;

public interface PlayerActionListener {
    void clickedHex(@NotNull HexIndex hexIndex);
}
