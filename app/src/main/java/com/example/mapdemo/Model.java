package com.example.mapdemo;

/**
 * Created by DC0708 on 01-Feb-16.
 */
public class Model {

    public double boundaryWidth;
    public double boundaryHeight;
    public int sizeJunkBubbles;

    public String boundarytype;

    public int sizeSnackBubble;
    public int sizeRepellerBubbles;

    public final int sizeWormHoles=3;

    public final int wormholecount = 3; //the wormhole count is fixed to 3 //

    int[] xCoordinate = new int[wormholecount];
    int[] yCoordinate = new int[wormholecount];

    public  Boolean[][] isPlacedBubble = new Boolean[10][10];

    public Model(String boundarytype){

        for(int i=0;i<10;i++){

            for(int j=0;j<10;j++){
                this.isPlacedBubble[i][j]=false;
            }

        }

        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;

        if(boundarytype.equals("Small")){

            /** setting up the model if small boundary **/
            this.boundarytype="Small";
            this.boundaryHeight = 2*0.00010;
            this.boundaryWidth = 2*0.00010;
            this.sizeJunkBubbles = 3;
            this.sizeRepellerBubbles = 1;
            this.sizeSnackBubble = 7;

        }

        else if(boundarytype.equals("Medium")){

            this.boundarytype= "Medium";
            this.boundaryHeight = 2*0.00020;
            this.boundaryWidth = 2*0.00020;
            this.sizeJunkBubbles = 5;
            this.sizeRepellerBubbles = 1;
            this.sizeSnackBubble = 11;

        }
        else{

            this.boundarytype = "Large";
            this.boundaryHeight = 2*0.00030;
            this.boundaryWidth = 2*0.00030;
            this.sizeJunkBubbles = 7;
            this.sizeRepellerBubbles = 1;
            this.sizeSnackBubble = 14;

        }
    }
}
