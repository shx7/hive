package model;

import model.units.Unit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ContainerUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static model.FieldUtils.getNeighboursIndices;

public class GameModel {
    private final Map<HexIndex, List<Unit>> myField = new HashMap<>();
    private final List<Player> myPlayers;

    private int myActivePlayerIndex;
    @Nullable private HexIndex mySelectedHex;
    @NotNull private Set<HexIndex> myPossibleMovesFromSelectedHex;

    public GameModel(@NotNull List<Player> players) {
        myPlayers = players;
        myPossibleMovesFromSelectedHex = Collections.emptySet();
    }

    public void put(@NotNull Unit unit) {
        HexIndex index = unit.getPosition();
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
        unit.setPosition(to);
        cleanupDanglingHexes(from);
        put(unit);
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
    public Set<HexIndex> getNeighboursWhereCanSlide(@NotNull HexIndex from) {
        final Set<HexIndex> result = new HashSet<>();
        HexIndex[] neighboursIndices = getNeighboursIndices(from);
        final int unitListSize = getUnitsAmount(from);
        int length = neighboursIndices.length;
        for (int i = 0; i < length; i++) {
            HexIndex neighbourHex = neighboursIndices[i];
            HexIndex leftNeighbourHex = ContainerUtil.getCircular(neighboursIndices, i - 1);
            HexIndex rightNeighbourHex = ContainerUtil.getCircular(neighboursIndices, i + 1);
            if (unitListSize > getUnitsAmount(neighbourHex) // can slide on level down or move to
                    && (unitListSize > getUnitsAmount(leftNeighbourHex)
                        || unitListSize > getUnitsAmount(rightNeighbourHex))) // passage not blocked
            {
                result.add(neighbourHex);
            }
        }
        return result;
    }

    public int getUnitsAmount(@NotNull HexIndex rightNeighbourHex) {
        return getUnitList(rightNeighbourHex).size();
    }


    @NotNull
    public Set<HexIndex> getHexIndices() {
        return myField.keySet();
    }

    @Nullable
    @Contract("null -> null")
    public Unit getUnit(@Nullable HexIndex index) {
        List<Unit> unitList = index != null ? getUnitList(index) : null;
        return unitList != null && !unitList.isEmpty() ? unitList.get(unitList.size() - 1) : null;
    }

    @Nullable
    @Contract("null -> null; !null -> !null") // TODO: nasty, refactor
    private List<Unit> getUnitList(@Nullable HexIndex index) {
        List<Unit> result = null;
        if (index != null) {
            result = myField.get(index);
        }
        return result;
    }

    @NotNull
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
        mySelectedHex = hexIndex;
        myPossibleMovesFromSelectedHex = getPossibleMoves(mySelectedHex);
    }

    public boolean isEmptyHex(@NotNull HexIndex hexIndex) {
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

    public boolean canMoveFromSelectedHexTo(@NotNull HexIndex hexIndex) {
        return myPossibleMovesFromSelectedHex.contains(hexIndex);
    }

    @NotNull
    public List<HexIndex> getNotEmptyHexIndices() { // TODO: cache value and update only on model change
        return myField.keySet().stream().filter(hexIndex -> !isEmptyHex(hexIndex)).collect(Collectors.toList());
    }

    @NotNull
    private Set<HexIndex> getPossibleMoves(@Nullable HexIndex from) {
        Unit unit = getUnit(from);
        return unit != null ? unit.getPossibleMoves(this) : Collections.emptySet();
    }
}
