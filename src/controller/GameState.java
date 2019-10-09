package controller;

import model.GameModel;
import model.HexIndex;
import org.jetbrains.annotations.NotNull;

public enum GameState {
    SELECT_UNIT_TO_OPERATE {
        @Override
        void clickedHex(@NotNull GameModel model, @NotNull GameController controller, @NotNull HexIndex hexIndex) {
            model.setSelectedHex(hexIndex);
            controller.setState(SELECT_UNIT_OPERATION_DESTINATION);
        }
    },
    SELECT_UNIT_OPERATION_DESTINATION {
        @Override
        void clickedHex(@NotNull GameModel model, @NotNull GameController controller, @NotNull HexIndex hexIndex) {
            HexIndex selectedHex = model.getSelectedHex();
            if (selectedHex != null && model.canMove(selectedHex, hexIndex)) {
                model.move(selectedHex, hexIndex);
                model.setSelectedHex(null);
                controller.setState(SELECT_UNIT_TO_OPERATE);
                model.changeActivePlayer();
            }
            else {
                model.setSelectedHex(hexIndex);
            }
        }
    };

    // Invoked when player selects hex on the board with or without unit on it
    abstract void clickedHex(@NotNull GameModel model, @NotNull GameController controller, @NotNull HexIndex hexIndex);
}
