package org.concordia;

public class Treasure {
    public int value;
    public char texture;

    //STUDENT MODIFIABLE PARAMETER---------
    public double treasureValueRatio = 0.8; //Change this between 0.0-1.0 to change how many big treasures spawn
    //-------------------------------------

    Treasure(){
        this.value = Math.random() < treasureValueRatio ? 10 : 25;
        this.texture = this.value == 10 ? '¢' : '$';
    }
}
