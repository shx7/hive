# Hive
Hive boardgame Java implementation

![Demo Animation](../master/resources/hive.gif?raw=true)

## Rules: 
- There are two players playing over a hex grid where you can put different units.
- Unit types available: Bee Queen, Beetle, Ant, Spider and Grasshopper. 
- Player can either move a unit, or draw a new one into the grid during his/her round. 
- Game ends when one's Bee Queen is surrounded by units (ownership doesn't matter) and can't move anywhere.
- You can draw Bee Queen onto the grid no later than yours 4th round.

## Units:
Every unit has it's own movement pattern, but there are common rules for all units:
- You can move unit, if it can physically slide to position selected.
- Hive swarm should always be connected. At no time connectivity of swarm can be broken.

### Bee Queen:
- Moves 1 hex per turn in every direction.

### Beetle:
- Same as Queen, but it can also climb over other units, effectively blocking their movement by doing so.

### Ant:
- Can move along outer border of the hive (minding sliding rule above).

### Spider:
- Moves in the same fashion as Ant does, but by 3 hexes only, nor more nor less.

### Grasshopper:
- Can jump along straight row to the first free spot available.
