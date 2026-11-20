# Beacon Fold

Open `index.html` in a modern browser. Tip the two-cell lantern onto the diamond so that it stands upright. Arrow keys, WASD, and the on-screen arrows all work. Undo rewinds one move. Hint suggests the next step along a shortest route. Par is the fewest moves from the starting position. The game remembers completed islands and personal bests when the browser permits local storage.

## Deploy

Copy the included `index.html` to the public directory of a static web host. It includes all five islands, the renderer, and the artwork. No runtime dependencies, server process, network assets, or API keys are required.

## Rebuild or change the islands

With JDK 17 or later installed, open a terminal in the folder containing the source and run:

```
java BeaconFold.java
```

Open or deploy the resulting `dist/index.html`. Java defines the puzzle rules, enumerates legal moves, and uses breadth-first search to check each island and calculate shortest paths. The HTML template contains the browser input, animation, and Canvas renderer. In the Java island maps, `#` is stone, `.` is open water, `S` is the start, and `G` is the goal.
