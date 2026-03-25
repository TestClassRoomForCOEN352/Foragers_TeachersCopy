package org.concordia.bosses.easy;

import org.concordia.*;
import java.util.*;

public class BossPlayer2Easy extends Player2 {

    private final Random rng = new Random();

    public BossPlayer2Easy(int x, int y) {
        super(x, y);
    }

    @Override
    public Tile moveDecision(GameState state) {
        Tile current = state.tiles[y][x];
        List<Tile> options = new ArrayList<>();
        for (Tile n : current.neighbours)
            if (n != null && !n.collision) options.add(n);
        if (options.isEmpty()) return current;
        return options.get(rng.nextInt(options.size()));
    }

    @Override
    public int getTeleport() { return 0; }
}
