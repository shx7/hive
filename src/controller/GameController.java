package controller;

import ui.GameUI;

// TODO: states
// TODO: proper ui loop
public class GameController {
    private final GameUI myGameUI = new GameUI();

    public static void main(String[] args) {
        GameController controller = new GameController();
        Unit dummyUnit = new DummyUnit();
        controller.myField.put(HexIndex.create(1, 1), dummyUnit);
        controller.myField.put(HexIndex.create(2, 1), dummyUnit);
        controller.myField.put(HexIndex.create(2, 0), dummyUnit);
        controller.myField.put(HexIndex.create(3, -1), dummyUnit);
    }
}
