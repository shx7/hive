package controller;

import model.GameModel;
import model.HexIndex;
import model.Player;
import model.units.DummyUnit;
import org.jetbrains.annotations.NotNull;
import ui.GameUI;
import ui.PlayerActionListener;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class GameController implements PlayerActionListener {
    @NotNull private final GameModel myGameModel;
    @NotNull private final GameUI myGameUI;
    @NotNull private GameState myState;

    private GameController(@NotNull List<Player> playerList) {
        myGameModel = new GameModel(playerList);
        myGameUI = new GameUI(myGameModel, this);
        myState = GameState.SELECT_UNIT_TO_OPERATE;
    }

    public static void main(String[] args) {
        // TODO: get rid of Players initialisation, they must be initialised inside model
        Player whitePlayer = new Player("White", Color.WHITE);
        Player blackPlayer = new Player("Black", Color.BLACK);
        List<Player> playerList =
                Arrays.asList(blackPlayer, whitePlayer);
        GameController controller = new GameController(playerList);
        controller.myGameModel.put(HexIndex.create(0, 0), new DummyUnit(whitePlayer));
        controller.myGameModel.put(HexIndex.create(1, 0), new DummyUnit(blackPlayer));
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
