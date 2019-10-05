package controller;

import com.sun.istack.internal.NotNull;
import model.GameModel;
import model.HexIndex;
import model.units.DummyUnit;
import model.units.Unit;
import ui.GameUI;
import ui.PlayerActionListener;

public class GameController implements PlayerActionListener {
    private final GameModel myGameModel = new GameModel();
    private final GameUI myGameUI = new GameUI(myGameModel, this);

    @NotNull private GameState myState = GameState.SELECT_UNIT_TO_OPERATE;

    public static void main(String[] args) {
        GameController controller = new GameController();
        Unit dummyUnit = new DummyUnit();
        controller.myGameModel.put(HexIndex.create(0, 0), dummyUnit);
    }

    void setState(@NotNull GameState myState) {
        this.myState = myState;
    }

    @Override
    public void clickedHex(@NotNull HexIndex hexIndex) {
        myState.clickedHex(myGameModel, this, hexIndex);
        myGameUI.repaintGameField();
    }
}
