package model;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import model.units.Unit;
import util.ContainerUtil;

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

    /**
     * @param hexIndex hex which neighbours to enumerate
     * @return array of neighbours indices ordered clockwise, starting from leftmost
     * element in the same row. In other words spatially elements organised
     * like that (where x is hexIndex):
     *   [1] [2]
     * [0] [x] [3]
     *   [5] [4]
     */
    @NotNull
    private HexIndex[] getNeighboursIndices(@NotNull HexIndex hexIndex) {
        final int p = hexIndex.p;
        final int q = hexIndex.q;
        int pShift = hexIndex.q % 2 != 0 ? 0 : -1;
        return new HexIndex[]{
                HexIndex.create(p - 1, q), HexIndex.create(p  + pShift, q - 1),
                HexIndex.create(p + pShift + 1, q - 1), HexIndex.create(p + 1, q),
                HexIndex.create(p + pShift + 1, q + 1), HexIndex.create(p + pShift, q + 1)
        };
    }

    @NotNull
    public Set<HexIndex> getHexIndices() {
        return myField.keySet();
    }

    @Nullable
    public Unit getUnit(@NotNull HexIndex index) {
        List<Unit> unitList = getUnitList(index);
        return unitList != null && !unitList.isEmpty() ? unitList.get(unitList.size() - 1) : null;
    }

    @Nullable
    private List<Unit> getUnitList(@NotNull HexIndex index) {
        return myField.get(index);
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
        List<Unit> unitList = hexIndex != null ? getUnitList(hexIndex) : null;
        if (unitList == null || unitList.isEmpty()
                || unitList.size() == 1 && !isOneSwarmIfRemoveHex(hexIndex)) {
            return Collections.emptySet();
        }
        Set<HexIndex> result = new HashSet<>();

        HexIndex[] neighboursIndices = getNeighboursIndices(hexIndex);
        int length = neighboursIndices.length;
        for (int i = 0; i < length; i++) {
            HexIndex neighbourHex = neighboursIndices[i];
            HexIndex leftNeighbourHex = ContainerUtil.getCircular(neighboursIndices, i - 1);
            HexIndex rightNeighbourHex = ContainerUtil.getCircular(neighboursIndices, i + 1);
            if (isEmptyHex(neighbourHex) && (isEmptyHex(leftNeighbourHex) || isEmptyHex(rightNeighbourHex))) {
                result.add(neighbourHex);
            }
        }
        return result;
    }

    @NotNull
    private boolean isOneSwarmIfRemoveHex(@NotNull HexIndex hexIndexToRemove) {
        Set<HexIndex> connectedHexesIfHexRemoved = new HashSet<>();
        List<HexIndex> queue = new ArrayList<>();
        HexIndex firstNeighbour =
                ContainerUtil.findNot(getNeighboursIndices(hexIndexToRemove), this::isEmptyHex);
        if (firstNeighbour != null) {
            queue.add(firstNeighbour);
        }

        while (!queue.isEmpty()) {
            HexIndex index = queue.remove(queue.size() - 1);
            if (!hexIndexToRemove.equals(index)
                    && !isEmptyHex(index) && connectedHexesIfHexRemoved.add(index)) {
                Collections.addAll(queue, getNeighboursIndices(index));
            }
        }
        // Expect to get all non empty hexes visited but hex to be removed
        return connectedHexesIfHexRemoved.size() == getNotEmptyHexIndices().size() - 1;
    }

    @NotNull
    private List<HexIndex> getNotEmptyHexIndices() {
        return myField.keySet().stream().filter(hexIndex -> !isEmptyHex(hexIndex)).collect(Collectors.toList());
    }
}
