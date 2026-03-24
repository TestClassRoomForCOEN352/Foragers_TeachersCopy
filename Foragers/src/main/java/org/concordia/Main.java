package org.concordia;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        MapLoader loader = new MapLoader();

        //STUDENT-MODIFIABLE PARAMETER---------------------
        Tile[][] tiles = loader.load("Grotto.txt"); //Choose the map to be loaded
        //-------------------------------------------------

        PlayerLoader ploader = new PlayerLoader();
        Player1 p1 = ploader.p1;
        Player2 p2 = ploader.p2;

        GameConfig config = new GameConfig();
        GameSetup setup = new GameSetup();
        boolean check = setup.spawnEntities(p1, p2, tiles);
        if (!check) { System.out.println("Match spawn failed"); System.exit(1); }

        GameEngine engine = new GameEngine(tiles, p1, p2, config.rounds, config.gameTick);
        int victor = engine.playGame();

        if (victor == 1) System.out.println("Player 1 wins with a final score of: " + p1.score);
        else if (victor == 2) System.out.println("Player 2 wins with a final score of: " + p2.score);
        else if (victor == 3) System.out.println("Tie! Both scored: " + p1.score);
    }
}
