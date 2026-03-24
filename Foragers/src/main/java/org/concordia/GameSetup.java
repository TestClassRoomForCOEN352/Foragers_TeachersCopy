package org.concordia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.concordia.MapLoader.MAP_HEIGHT;
import static org.concordia.MapLoader.MAP_WIDTH;

public class GameSetup {

    //STUDENT-MODIFIABLE PARAMETER----------
    public final int numberOfTreasures = 50; //Increase or decrease the amount of treasures on the map
    //--------------------------------------
    public boolean spawnEntities(Player1 p1, Player2 p2, Tile[][] tiles) {
        spawnPlayers(p1, p2, tiles);
        spawnTreasures(tiles);
        return true;
    }

    public void spawnTreasures(Tile[][] tiles) {
        List<Tile> eligible = new ArrayList<>();
        for (int i = 0; i < MAP_HEIGHT; i++)
            for (int j = 0; j < MAP_WIDTH; j++) {
                Tile t = tiles[i][j];
                if (!t.collision && !t.player1Present && !t.player2Present)
                    eligible.add(t);
            }

        Collections.shuffle(eligible);
        int count = Math.min(numberOfTreasures, eligible.size());
        for (int i = 0; i < count; i++) {
            eligible.get(i).treasurePresent = true;
            eligible.get(i).treasure = new Treasure();
        }
    }

    public void spawnPlayers(Player1 p1, Player2 p2, Tile[][] tiles) {
        tiles[p1.y][p1.x].player1Present = true;
        tiles[p2.y][p2.x].player2Present = true;
    }
}
