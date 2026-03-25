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

    public void updatePlayer(GameState state) {
        this.x     = state.p2_x;
        this.y     = state.p2_y;
        this.score = state.p2_score;
    }

    public int getTeleport() {
    return 0;
    }
}
