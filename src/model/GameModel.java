package model;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import model.units.Unit;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameModel {
    private final Map<HexIndex, List<Unit>> myField = new HashMap<>();
    private final List<Player> myPlayers;

    private int myActivePlayerIndex;
    private HexIndex mySelectedHex;

    public GameModel(@NotNull List<Player> players) {
        this.myPlayers = players;
    }

    public void put(@NotNull HexIndex index, @NotNull Unit unit) {
        List<Unit> hex = getOrCreateHex(index);
        hex.add(unit);
        createNeighbourHexesIfNeeded(index);
    }

    private void createNeighbourHexesIfNeeded(@NotNull HexIndex index) {
        for (HexIndex neighbourIndex : getNeighboursIndices(index)) {
            getOrCreateHex(neighbourIndex);
        }
    }

    public void move(@NotNull HexIndex from, @NotNull HexIndex to) {
        List<Unit> hex = myField.get(from);
        if (hex == null || hex.isEmpty()) {
            throw new IllegalFieldStateException("No such field with index " + from.toString());
        }
        Unit unit = hex.remove(hex.size() - 1);
        cleanupDanglingHexes(from);
        put(to, unit);
    }

    private void cleanupDanglingHexes(@NotNull HexIndex hexIndex) {
        for (HexIndex neighbourHexIndex : getNeighboursIndices(hexIndex)) {
            deleteHexIfDangling(neighbourHexIndex);
        }
        deleteHexIfDangling(hexIndex);
    }

    private void deleteHexIfDangling(@NotNull HexIndex hexIndex) {
        Stream<List<Unit>> neighbourIndices = getNeighbours(hexIndex);
        if (getUnit(hexIndex) == null && neighbourIndices.allMatch(List::isEmpty)) {
            myField.remove(hexIndex);
        }
    }

    @NotNull
    private Stream<List<Unit>> getNeighbours(@NotNull HexIndex hexIndex) {
        return Arrays.stream(getNeighboursIndices(hexIndex))
                .map(myField::get)
                .filter(Objects::nonNull);
    }

    @NotNull
    private HexIndex[] getNeighboursIndices(@NotNull HexIndex hexIndex) {
        final int p = hexIndex.p;
        final int q = hexIndex.q;
        int pShift = hexIndex.q % 2 != 0 ? 0 : -1;
        return new HexIndex[]{
                HexIndex.create(p  + pShift, q - 1), HexIndex.create(p + pShift + 1, q - 1),
                HexIndex.create(p - 1, q), /*  hexIndex, */ HexIndex.create(p + 1, q),
                HexIndex.create(p + pShift, q + 1), HexIndex.create(p + pShift + 1, q + 1)
        };
    }

    @NotNull
    public Set<HexIndex> getHexIndices() {
        return myField.keySet();
    }

    @Nullable
    public Unit getUnit(@NotNull HexIndex index) {
        List<Unit> unitList = myField.get(index);
        return unitList != null && !unitList.isEmpty() ? unitList.get(unitList.size() - 1) : null;
    }

    private List<Unit> getOrCreateHex(@NotNull HexIndex index) {
        return myField.computeIfAbsent(index, idx -> new ArrayList<>());
    }

    @Nullable
    public HexIndex getSelectedHex() {
        return mySelectedHex;
    }

    public void setSelectedHex(@Nullable HexIndex hexIndex) {
        Unit unit = getUnit(hexIndex);
        if (unit == null || getActivePlayer().equals(unit.getPlayer())) {
            doSetSelectedHex(hexIndex);
        }
    }

    private void doSetSelectedHex(@Nullable HexIndex hexIndex) {
        this.mySelectedHex = hexIndex;
    }

    private boolean isEmptyHex(@NotNull HexIndex hexIndex) {
        return getUnit(hexIndex) == null;
    }

    @NotNull
    private Player getActivePlayer() {
        return myPlayers.get(myActivePlayerIndex);
    }

    public void changeActivePlayer() {
        ++myActivePlayerIndex;
        if (myActivePlayerIndex >= myPlayers.size()) {
            myActivePlayerIndex = 0;
        }
    }

    public boolean canMove(@NotNull HexIndex from, @NotNull HexIndex to) {
        return !isEmptyHex(from) && getPossibleMoves(from).contains(to);
    }

    @NotNull
    public Set<HexIndex> getPossibleMoves(@Nullable HexIndex hexIndex) {
        Unit unit = hexIndex != null ? getUnit(hexIndex) : null;
        return unit != null
                ? Arrays.stream(getNeighboursIndices(hexIndex)).filter(this::isEmptyHex).collect(Collectors.toSet())
                : Collections.emptySet();
    }
}
