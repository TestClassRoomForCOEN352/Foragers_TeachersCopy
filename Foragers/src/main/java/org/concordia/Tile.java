package org.concordia;

public class Tile {

    public char texture;
    public final int x;
    public final int y;
    public boolean player1Present;
    public boolean player2Present;
    public boolean treasurePresent;
    public Treasure treasure;
    public boolean collision;
    public final Tile[] neighbours = new Tile[8];

    public Tile(char texture, int x, int y) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.collision = (texture != '.');
    }

    //Can use these to access individual neighbours if required. Otherwise the neighbours array will do fine.
    public Tile getNW() { return neighbours[0]; }
    public Tile getN()  { return neighbours[1]; }
    public Tile getNE() { return neighbours[2]; }
    public Tile getW()  { return neighbours[3]; }
    public Tile getE()  { return neighbours[4]; }
    public Tile getSW() { return neighbours[5]; }
    public Tile getS()  { return neighbours[6]; }
    public Tile getSE() { return neighbours[7]; }
}
