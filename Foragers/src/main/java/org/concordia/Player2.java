package org.concordia;

import java.util.*;

public class Player2 {

    public char texture = '2';
    public int x;
    public int y;
    public int score;

    // STUDENT MAY PLACE ANY EXTRA FIELDS THEY WANT HERE -------------------

    // ----------------------------------------------------------------------

    public Player2(int x, int y) {
        this.x = x;
        this.y = y;
        this.score = 0;
    }

    // -----------------------------------------------------------------------
    // findPath — identical Dijkstra to Player1.
    // Returns a Map<Tile, Tile> predecessor map from this player's position.
    // -----------------------------------------------------------------------
    public Map<Tile, Tile> findPath(GameState state) {
        Tile start = state.tiles[y][x];

        Map<Tile, Double> dist = new HashMap<>();
        Map<Tile, Tile>   prev = new HashMap<>();
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
                    prev.put(nb, current);
                    pq.add(nb);
                }
            }
        }
        return prev;
    }

    // -----------------------------------------------------------------------
    // predictPath — same threat model as Player1 but from P2's perspective.
    // Returns Map<Tile, Double> of (treasure tile -> accessibility score).
    // -----------------------------------------------------------------------
    public Map<Tile, Double> predictPath(GameState state) {
        Map<Tile, Double> myDist    = dijkstraDist(state.tiles[y][x]);
        Map<Tile, Double> enemyDist = dijkstraDist(state.tiles[state.p1_y][state.p1_x]);

        Map<Tile, Double> scores = new HashMap<>();
        for (int i = 0; i < state.tiles.length; i++) {
            for (int j = 0; j < state.tiles[0].length; j++) {
                Tile t = state.tiles[i][j];
                if (!t.treasurePresent) continue;
                double mine  = myDist.getOrDefault(t, Double.MAX_VALUE);
                double enemy = enemyDist.getOrDefault(t, Double.MAX_VALUE);
                scores.put(t, enemy - mine);
            }
        }
        return scores;
    }

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
    // moveDecision — same utility function as Player1.
    // utility = treasure value + THREAT_WEIGHT * (enemy_dist - my_dist)
    // -----------------------------------------------------------------------
    public Tile moveDecision(GameState state) {
        final double THREAT_WEIGHT = 2.0;

        Map<Tile, Tile>   prev   = findPath(state);
        Map<Tile, Double> threat = predictPath(state);
        Map<Tile, Double> myDist = dijkstraDist(state.tiles[y][x]);

        Tile bestTarget = null;
        double bestUtility = Double.NEGATIVE_INFINITY;

        for (Map.Entry<Tile, Double> entry : threat.entrySet()) {
            Tile treasure = entry.getKey();
            double accessibility = entry.getValue();
            double myD = myDist.getOrDefault(treasure, Double.MAX_VALUE);
            if (myD == Double.MAX_VALUE) continue;

            double utility = treasure.treasure.value + THREAT_WEIGHT * accessibility;
            if (utility > bestUtility) {
                bestUtility = utility;
                bestTarget  = treasure;
            }
        }

        if (bestTarget == null) return state.tiles[y][x];

        return firstStep(state.tiles[y][x], bestTarget, prev);
    }

    private Tile firstStep(Tile start, Tile target, Map<Tile, Tile> prev) {
        Tile current = target;
        Tile next    = target;
        while (prev.containsKey(current) && prev.get(current) != start) {
            next    = current;
            current = prev.get(current);
        }
        return (prev.get(current) == start) ? current : start;
    }

    public void updatePlayer(GameState state) {
        this.x     = state.p2_x;
        this.y     = state.p2_y;
        this.score = state.p2_score;
    }
}
