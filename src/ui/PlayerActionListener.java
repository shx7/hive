package ui;

import com.sun.istack.internal.NotNull;
import model.HexIndex;

public interface PlayerActionListener {
    void clickedHex(@NotNull HexIndex hexIndex);
}
