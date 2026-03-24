package org.concordia;

import java.util.*;

public class Player1 {

    public char texture = '1';
    public int x;
    public int y;
    public int score;

    // STUDENT MAY PLACE ANY EXTRA FIELDS THEY WANT HERE -------------------

    // ----------------------------------------------------------------------

    public Player1(int x, int y) {
        this.x = x;
        this.y = y;
        this.score = 0;
    }

    // -----------------------------------------------------------------------
    // findPath — Dijkstra from current position to every reachable tile.
    // Returns a Map<Tile, Tile> of (tile -> predecessor), which lets you
    // reconstruct the shortest path to any destination by walking back from it.
    // Cost is 1 per cardinal step and sqrt(2) per diagonal step.
    // -----------------------------------------------------------------------
    public Map<Tile, Tile> findPath(GameState state) {
        Tile start = state.tiles[y][x];

        // Distance from start to each tile
        Map<Tile, Double> dist = new HashMap<>();
        // predecessor map — reconstruct any path by walking back from target
        Map<Tile, Tile> prev = new HashMap<>();

        // Min-heap ordered by distance
        PriorityQueue<Tile> pq = new PriorityQueue<>(
            Comparator.comparingDouble(t -> dist.getOrDefault(t, Double.MAX_VALUE))
        );

        dist.put(start, 0.0);
        pq.add(start);

        // Direction costs: diagonal neighbours (0,2,5,7) cost sqrt(2), cardinal cost 1
        double[] stepCost = { Math.sqrt(2), 1, Math.sqrt(2),
                              1,                1,
                              Math.sqrt(2), 1, Math.sqrt(2) };

        while (!pq.isEmpty()) {
            Tile current = pq.poll();
            double currentDist = dist.getOrDefault(current, Double.MAX_VALUE);

            for (int dir = 0; dir < current.neighbours.length; dir++) {
                Tile neighbour = current.neighbours[dir];
                if (neighbour == null || neighbour.collision) continue;

                double newDist = currentDist + stepCost[dir];
                if (newDist < dist.getOrDefault(neighbour, Double.MAX_VALUE)) {
                    dist.put(neighbour, newDist);
                    prev.put(neighbour, current);
                    pq.add(neighbour);
                }
            }
        }
        return prev;
    }

    // -----------------------------------------------------------------------
    // predictPath — estimates the threat level each treasure poses.
    // For each treasure, computes how many steps closer the enemy is vs us.
    // A positive score means the enemy is farther — treasure is accessible.
    // A negative score means the enemy is closer — treasure is contested.
    // Returns a Map<Tile, Double> of (treasure tile -> accessibility score).
    // -----------------------------------------------------------------------
    public Map<Tile, Double> predictPath(GameState state) {
        // Run Dijkstra from both positions
        Map<Tile, Tile> myPrev     = findPath(state);
        Map<Tile, Double> myDist   = dijkstraDist(state.tiles[y][x]);
        Map<Tile, Double> enemyDist = dijkstraDist(state.tiles[state.p2_y][state.p2_x]);

        Map<Tile, Double> scores = new HashMap<>();
        for (int i = 0; i < state.tiles.length; i++) {
            for (int j = 0; j < state.tiles[0].length; j++) {
                Tile t = state.tiles[i][j];
                if (!t.treasurePresent) continue;
                double mine  = myDist.getOrDefault(t, Double.MAX_VALUE);
                double enemy = enemyDist.getOrDefault(t, Double.MAX_VALUE);
                // Positive = we are closer; negative = enemy is closer
                scores.put(t, enemy - mine);
            }
        }
        return scores;
    }

    // Helper: Dijkstra returning distances only (no predecessor map)
    private Map<Tile, Double> dijkstraDist(Tile start) {
        Map<Tile, Double> dist = new HashMap<>();
        PriorityQueue<Tile> pq = new PriorityQueue<>(
            Comparator.comparingDouble(t -> dist.getOrDefault(t, Double.MAX_VALUE))
        );
        double[] stepCost = { Math.sqrt(2), 1, Math.sqrt(2),
                              1,                1,
                              Math.sqrt(2), 1, Math.sqrt(2) };
        dist.put(start, 0.0);
        pq.add(start);
        while (!pq.isEmpty()) {
            Tile current = pq.poll();
            double cd = dist.getOrDefault(current, Double.MAX_VALUE);
            for (int dir = 0; dir < current.neighbours.length; dir++) {
                Tile nb = current.neighbours[dir];
                if (nb == null || nb.collision) continue;
                double nd = cd + stepCost[dir];
                if (nd < dist.getOrDefault(nb, Double.MAX_VALUE)) {
                    dist.put(nb, nd);
                    pq.add(nb);
                }
            }
        }
        return dist;
    }

    // -----------------------------------------------------------------------
    // moveDecision — utility function.
    // Scores every reachable treasure as:
    //   utility = (treasure value) + weight * (enemy dist - my dist)
    // Picks the highest-utility treasure, reconstructs the first step of the
    // shortest path toward it, and returns that tile.
    // Falls back to staying put if no reachable treasure exists.
    // -----------------------------------------------------------------------
    public Tile moveDecision(GameState state) {
        final double THREAT_WEIGHT = 2.0; // how much to penalise enemy-closer treasures

        Map<Tile, Tile>   prev       = findPath(state);
        Map<Tile, Double> threat     = predictPath(state);
        Map<Tile, Double> myDist     = dijkstraDist(state.tiles[y][x]);

        Tile bestTarget = null;
        double bestUtility = Double.NEGATIVE_INFINITY;

        for (Map.Entry<Tile, Double> entry : threat.entrySet()) {
            Tile treasure = entry.getKey();
            double accessibility = entry.getValue(); // enemy_dist - my_dist
            double myD = myDist.getOrDefault(treasure, Double.MAX_VALUE);
            if (myD == Double.MAX_VALUE) continue; // unreachable

            double utility = treasure.treasure.value + THREAT_WEIGHT * accessibility;
            if (utility > bestUtility) {
                bestUtility = utility;
                bestTarget  = treasure;
            }
        }

        if (bestTarget == null) return state.tiles[y][x]; // nowhere to go — stay put

        // Reconstruct first step toward bestTarget
        return firstStep(state.tiles[y][x], bestTarget, prev);
    }

    // Walk the predecessor map backwards from target to find the tile
    // that is one step away from start on the shortest path.
    private Tile firstStep(Tile start, Tile target, Map<Tile, Tile> prev) {
        Tile current = target;
        Tile next    = target;
        while (prev.containsKey(current) && prev.get(current) != start) {
            next    = current;
            current = prev.get(current);
        }
        // If prev.get(current) == start, then current is the first step
        return (prev.get(current) == start) ? current : start;
    }

    public void updatePlayer(GameState state) {
        this.x     = state.p1_x;
        this.y     = state.p1_y;
        this.score = state.p1_score;
    }
}
