package org.concordia;

import static org.concordia.MapLoader.MAP_HEIGHT;
import static org.concordia.MapLoader.MAP_WIDTH;

public class PlayerLoader {
    public Player1 p1;
    public Player2 p2;

    public PlayerLoader() {
        int midX = MAP_WIDTH  / 2;
        int p1Y  = MAP_HEIGHT / 4;
        int p2Y  = (MAP_HEIGHT * 3) / 4;

        this.p1 = new Player1(midX, p1Y);
        this.p2 = new Player2(midX, p2Y);
    }
}
