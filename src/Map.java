//Your Name
//Student ID

import java.awt.*;

public class Map {
    private int[][] obstacles;
    private int[][] pipes;
    private int[][] portals;
    private int[][] coins;

    Map(int[][] obstacles,int[][] pipes,int[][] portals, int[][] coins){
        this.obstacles=obstacles;
        this.pipes=pipes;
        this.portals=portals;
        this.coins=coins;
    }

    public void draw(){
        // draws obstacles with their proper scaledWidth and scaledHeight
        for (int[] obstacle : obstacles){
            StdDraw.picture(obstacle[0],obstacle[1],"./assets/block.png",obstacle[2]*2,obstacle[3]*2);
        }

        // draws pipes
        StdDraw.setPenColor(StdDraw.YELLOW);
        for (int[] pipe : pipes){
            StdDraw.filledRectangle(pipe[0],pipe[1],pipe[2],pipe[3]);
        }

        // draws portals
        StdDraw.setPenColor(StdDraw.RED);
        for (int[] portal : portals){
            StdDraw.filledRectangle(portal[0],portal[1],portal[2],portal[3]);
        }
    }

    private boolean checkCollision(double nextX, double nextY, double playerHalfSize, int[][] obstacles){
        for (int[] obstacle : obstacles){
            if (Math.abs(nextX-obstacle[0])<(playerHalfSize+obstacle[2]) && Math.abs(nextY-obstacle[1])<(playerHalfSize+obstacle[3])){ // checks for x and y axis collisions (obstacle[0] is the x coordinate of obstacle and obstacle[2] is the half width)
                return true;
            }
        }
        return false;
    }

    public boolean isOnGround(int x, int y, double playerHalfSize, double speedY, int worldW, int worldH,boolean isGhost){
        double nextY=y+speedY-1.0; // it basically to check is there a ground below the player

        if (x-playerHalfSize<0 || x+playerHalfSize>worldW){ // if player is out of the canvas width, cannot be on ground
            return false;
        }
        if (speedY>0){ //it must be jumping
            return false;
        }

        if (!isGhost && checkCollision(x, nextY, playerHalfSize, portals)){
            return true;
        }

        return checkCollision(x,nextY,playerHalfSize,obstacles);
    }

    public boolean isOnGroundMainMenu(int x, int y, double playerHalfSize, double speedY, int worldH, int worldW){

        double nextY=y+speedY-1.0; // it basically checks is there a ground below the player

        if (nextY-playerHalfSize<120){
            return true;
        }

        return false;

    }

    public boolean isValidXMoveMainMenu(int x,int y, double playerHalfSize, double speedX, int worldW){
        double nextX = x+speedX;

        if (nextX-playerHalfSize<0 || nextX+playerHalfSize>worldW){
            return false;
        }

        return true;
    }

    public boolean isOnCeiling(int x, int y, double playerHalfSize, double speedY, int worldW, int worldH, boolean isGhost){
        double nextY=y+speedY+1.0; // it basically to check is there a ceiling above the player
        if (speedY < 0){ // it cannot be on ceiling if player's speed is less than 0
            return false;
        }

        if (x-playerHalfSize<0 || x+playerHalfSize>worldW){ // if player is out of the canvas width, cannot be on ceiling
            return false;
        }

        if (nextY+playerHalfSize>worldH){ // if player's next movement exceeds the height of the map
            return true;
        }

        if (!isGhost && checkCollision(x, nextY, playerHalfSize, portals)){
            return true;
        }

        return checkCollision(x,nextY,playerHalfSize,obstacles);
    }

    public boolean isValidXMove(int nextX,int y, double playerHalfSize, int worldW, boolean isGhost){
        if (nextX-playerHalfSize<0 || nextX+playerHalfSize>worldW){
            return false;
        }

        if (!isGhost && checkCollision(nextX, y, playerHalfSize, portals)){
            return false;
        }

        return !checkCollision(nextX,y,playerHalfSize,obstacles);
    }

    public boolean checkPortalCollision(int x, int y, double playerHalfSize, double speedX, double speedY){
        int nextX = x + (int) speedX;
        int nextY = y + (int) speedY;
        return checkCollision(nextX,nextY,playerHalfSize,portals);
    }

    public void handleTeleport(Mario mario, boolean isSPressed){
        int playerHalfSize = mario.size/2;
        int nextX = mario.x+(int)mario.speedX;
        int nextY = mario.y+(int)mario.speedY;

        int marioNextLeft = nextX-playerHalfSize;
        int marioNextRight = nextX+playerHalfSize;
        int marioNextTop = nextY+playerHalfSize;
        int marioNextBottom = nextY-playerHalfSize;

        int[][] upperPortal = {portals[0],portals[1]};
        int[][] lowerPortal = {portals[2],portals[3]};

        if (mario.speedY > 0 && !mario.isTeleporting){
            //Math.abs(nextX-upperPortal[1][0])<(playerHalfSize+upperPortal[1][2])
            if (marioNextTop>=upperPortal[1][1]-upperPortal[1][3] && nextX>=upperPortal[1][0]-upperPortal[1][2] && nextX<=upperPortal[1][0]+upperPortal[1][2]){ //instead of left and right sides of the player, use only center of the player to make portal entries easier without trying to pixel perfecting.
                mario.x=lowerPortal[0][0];
                mario.y=lowerPortal[0][1];
                mario.speedY = 23.0;
                mario.isTeleporting=true;
            }
        }

        if (mario.speedY <= 0 && !mario.isTeleporting){
            if (isSPressed && marioNextBottom<=lowerPortal[1][1]+lowerPortal[1][3] && nextX>=lowerPortal[1][0]-lowerPortal[1][2] && nextX<=lowerPortal[1][0]+lowerPortal[1][2]){
                mario.x=upperPortal[0][0];
                mario.y=upperPortal[0][1];
                mario.speedY=0;
                mario.isTeleporting=true;
            }
        }

    }

    public boolean isOnLowerPipe(Mario mario){
        int nextX = mario.x + (int) mario.speedX;
        int[][] lowerPipe = {pipes[2],pipes[3]};

        return checkCollision(nextX, mario.y,0,lowerPipe); // eliminate nextY because its mostly happening in x axis and eliminate player halfsize because this is not a regular collision check we want to give the effect of entering the pipe before displaying finish message
    }

    public int collectCoin(Mario mario) {
        for (int i=0;i<coins.length;i++){

            if (Math.abs(mario.x-coins[i][0])<=(mario.size/2.0+coins[i][2]) && Math.abs(mario.y-coins[i][1])<=(mario.size/2.0+coins[i][3])){ // checks for x and y axis collisions (obstacle[0] is the x coordinate of obstacle and obstacle[2] is the half width)
                System.out.println(i+" collected");
                coins[i][1]=-9999;
                return 1;
            }
        }
        return 0;
    }

    public void drawCoin(){
        // coin draw
        for (int i=0;i<coins.length;i++){
            StdDraw.picture(coins[i][0],coins[i][1],"./assets/coin.png",coins[i][2]*2,coins[i][3]*2);
        }
    }

}
