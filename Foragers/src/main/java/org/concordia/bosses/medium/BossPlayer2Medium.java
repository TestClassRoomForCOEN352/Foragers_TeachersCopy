package org.concordia.bosses.medium;

import org.concordia.*;
import java.util.*;

public class BossPlayer2Medium extends Player2 {

    public BossPlayer2Medium(int x, int y) {
        super(x, y);
    }

    @Override
    public Tile moveDecision(GameState state) {
        Tile current = state.tiles[y][x];

        Tile bestTreasure = null;
        int bestDist = Integer.MAX_VALUE;
        for (int i = 0; i < state.tiles.length; i++) {
            for (int j = 0; j < state.tiles[0].length; j++) {
                Tile t = state.tiles[i][j];
                if (!t.treasurePresent) continue;
                int dist = Math.abs(t.x - x) + Math.abs(t.y - y);
                if (dist < bestDist) { bestDist = dist; bestTreasure = t; }
            }
        }
        if (bestTreasure == null) return current;

        Tile bestMove = current;
        int bestMoveDist = Integer.MAX_VALUE;
        for (Tile n : current.neighbours) {
            if (n == null || n.collision) continue;
            int dist = Math.abs(n.x - bestTreasure.x) + Math.abs(n.y - bestTreasure.y);
            if (dist < bestMoveDist) { bestMoveDist = dist; bestMove = n; }
        }
        return bestMove;
    }

    @Override
    public int getTeleport() { return 0; }
}
