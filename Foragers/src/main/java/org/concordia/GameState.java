package org.concordia;

public class GameState {

    //This is all the information at your disposal for coming to a final decision
    //You should be accessing these and not anywhere that isn't tied to the current state of the game.
    public Tile[][] tiles;
    public Player1 p1;
    public Player2 p2;
    public int p1_x;
    public int p1_y;
    public int p2_x;
    public int p2_y;
    public int p1_score;
    public int p2_score;
    public int rounds_left;

    public GameState(Tile[][] tiles, Player1 p1, Player2 p2, int rounds_left){
        this.tiles = tiles;
        this.p1 = p1;
        this.p2 = p2;
        p1_x = p1.x;
        p1_y = p1.y;
        p1_score = p1.score;
        p2_x = p2.x;
        p2_y = p2.y;
        p2_score = p2.score;
        this.rounds_left = rounds_left;
    }

    public void updateGameState(Tile[][] tiles, Player1 p1, Player2 p2, int rounds_left){
        this.tiles = tiles;
        this.p1 = p1;
        this.p2 = p2;
        p1_x = p1.x;
        p1_y = p1.y;
        p1_score = p1.score;
        p2_x = p2.x;
        p2_y = p2.y;
        p2_score = p2.score;
        this.rounds_left = rounds_left;
    }
}
