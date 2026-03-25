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

    public void updatePlayer(GameState state) {
        this.x     = state.p1_x;
        this.y     = state.p1_y;
        this.score = state.p1_score;
    }

    public int getTeleport() {
    return 0;
    }
}
