package org.concordia.bosses.hard;

import org.concordia.*;
import java.util.*;

// Hard boss: full Dijkstra + utility function identical to the reference
// implementation. Students need a meaningfully better strategy to beat this.
public class BossPlayer1Hard extends Player1 {

    public BossPlayer1Hard(int x, int y) {
        super(x, y);
    }

    @Override
    public Tile moveDecision(GameState state) {
        return computeMove(state, x, y, state.p2_x, state.p2_y);
    }

    static Tile computeMove(GameState state, int myX, int myY, int enemyX, int enemyY) {
        Tile start = state.tiles[myY][myX];
        Tile enemyStart = state.tiles[enemyY][enemyX];

        Map<Tile, Double> myDist    = dijkstraDist(start);
        Map<Tile, Double> enemyDist = dijkstraDist(enemyStart);
        Map<Tile, Tile>   prev      = dijkstraPrev(start);

        final double THREAT_WEIGHT = 2.0;
        Tile bestTarget  = null;
        double bestUtil  = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < state.tiles.length; i++) {
            for (int j = 0; j < state.tiles[0].length; j++) {
                Tile t = state.tiles[i][j];
                if (!t.treasurePresent) continue;
                double mine  = myDist.getOrDefault(t, Double.MAX_VALUE);
                double enemy = enemyDist.getOrDefault(t, Double.MAX_VALUE);
                if (mine == Double.MAX_VALUE) continue;
                double util = t.treasure.value + THREAT_WEIGHT * (enemy - mine);
                if (util > bestUtil) { bestUtil = util; bestTarget = t; }
            }
        }
        if (bestTarget == null) return start;
        return firstStep(start, bestTarget, prev);
    }

    private static Map<Tile, Double> dijkstraDist(Tile start) {
        double[] cost = {Math.sqrt(2),1,Math.sqrt(2),1,1,Math.sqrt(2),1,Math.sqrt(2)};
        Map<Tile, Double> dist = new HashMap<>();
        PriorityQueue<Tile> pq = new PriorityQueue<>(
            Comparator.comparingDouble(t -> dist.getOrDefault(t, Double.MAX_VALUE)));
        dist.put(start, 0.0); pq.add(start);
        while (!pq.isEmpty()) {
            Tile c = pq.poll(); double cd = dist.get(c);
            for (int d = 0; d < c.neighbours.length; d++) {
                Tile n = c.neighbours[d];
                if (n == null || n.collision) continue;
                double nd = cd + cost[d];
                if (nd < dist.getOrDefault(n, Double.MAX_VALUE)) { dist.put(n, nd); pq.add(n); }
            }
        }
        return dist;
    }

    private static Map<Tile, Tile> dijkstraPrev(Tile start) {
        double[] cost = {Math.sqrt(2),1,Math.sqrt(2),1,1,Math.sqrt(2),1,Math.sqrt(2)};
        Map<Tile, Double> dist = new HashMap<>();
        Map<Tile, Tile>   prev = new HashMap<>();
        PriorityQueue<Tile> pq = new PriorityQueue<>(
            Comparator.comparingDouble(t -> dist.getOrDefault(t, Double.MAX_VALUE)));
        dist.put(start, 0.0); pq.add(start);
        while (!pq.isEmpty()) {
            Tile c = pq.poll(); double cd = dist.get(c);
            for (int d = 0; d < c.neighbours.length; d++) {
                Tile n = c.neighbours[d];
                if (n == null || n.collision) continue;
                double nd = cd + cost[d];
                if (nd < dist.getOrDefault(n, Double.MAX_VALUE)) {
                    dist.put(n, nd); prev.put(n, c); pq.add(n);
                }
            }
        }
        return prev;
    }

    private static Tile firstStep(Tile start, Tile target, Map<Tile, Tile> prev) {
        Tile current = target;
        while (prev.containsKey(current) && prev.get(current) != start)
            current = prev.get(current);
        return prev.getOrDefault(current, start) == start ? current : start;
    }

    @Override
    public int getTeleport() { return 0; }
}
