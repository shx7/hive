package model.units;

import model.GameModel;
import model.HexIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class MoveAroundHiveStrategy extends MoveStrategyBase {
    private final Integer myStepsRestriction;

    /**
     * @param stepsRestriction exact number of steps allowed.
     *                         If `null` no restriction imposed
     */
    MoveAroundHiveStrategy(@Nullable Integer stepsRestriction) {
        this.myStepsRestriction = stepsRestriction;
    }

    @Override
    @NotNull
    public Set<HexIndex> getPossibleMoves(@NotNull HexIndex unitHexIndex, @NotNull GameModel model) {
        Set<HexIndex> visited = new HashSet<>();
        Map<HexIndex, Integer> distanceToHexes = new HashMap<>();
        List<HexIndex> queue = new ArrayList<>();

        queue.add(unitHexIndex);
        distanceToHexes.put(unitHexIndex, 0);

        while (!queue.isEmpty()) {
            HexIndex currentIndex = queue.remove(queue.size() - 1);
            int distanceToCurrent = distanceToHexes.get(currentIndex);
            if (visited.add(currentIndex)
                    && (myStepsRestriction == null || distanceToCurrent <= myStepsRestriction)) {
                List<HexIndex> neighboursWhereCanSlide =
                        model.getNeighboursWhereCanSlide(currentIndex, currentIndex.equals(unitHexIndex))
                                .stream()
                                .filter(indexTo -> swarmStaysConnectedIfMove(unitHexIndex, indexTo, model)
                                        && !visited.contains(indexTo))
                                .collect(Collectors.toList());
                queue.addAll(neighboursWhereCanSlide);
                neighboursWhereCanSlide.forEach(index -> distanceToHexes.put(index, distanceToCurrent + 1));
            }
        }

        visited.remove(unitHexIndex);

        if (myStepsRestriction != null) {
            return visited
                    .stream()
                    .filter(index -> distanceToHexes.get(index).equals(myStepsRestriction))
                    .collect(Collectors.toSet());
        }
        return visited;
    }
}
