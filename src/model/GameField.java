package model;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameField {
    private final Map<HexIndex, List<Unit>> myField = new HashMap<>();

    // TODO: check if move valid
    public void put(@NotNull HexIndex index, @NotNull Unit unit) {
        List<Unit> hex = getOrCreateHex(index);
        hex.add(unit);
    }

    // TODO: check if move valid
    public void move(@NotNull HexIndex from, @NotNull HexIndex to) {
        List<Unit> hex = myField.get(from);
        if (hex == null || hex.isEmpty()) {
            throw new IllegalFieldStateException("No such field with index " + from.toString());
        }
        Unit unit = hex.remove(hex.size() - 1);
        getOrCreateHex(to).add(unit);
        if (hex.isEmpty()) {
            myField.remove(from);
        }
    }

    @NotNull
    public List<HexIndex> getPossibleMoves(@NotNull Unit unit) {

        return null; //TODO:
    }

    private List<Unit> getOrCreateHex(@NotNull HexIndex index) {
        return myField.computeIfAbsent(index, idx -> new ArrayList<>());
    }
}
