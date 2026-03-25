package org.concordia.bosses.hard;

import org.concordia.*;

public class BossPlayer2Hard extends Player2 {

    public BossPlayer2Hard(int x, int y) {
        super(x, y);
    }

    @Override
    public Tile moveDecision(GameState state) {
        // Reuses the same logic as BossPlayer1Hard but from P2's perspective
        return BossPlayer1Hard.computeMove(state, x, y, state.p1_x, state.p1_y);
    }

    @Override
    public int getTeleport() { return 0; }
}
