//Your Name
//Student ID

public class Enemy {

    private int x;
    private int y;
    private int size; // 40 for mushrooms
    // movable x coordinates
    private int x1;
    private int x2;

    private int direction;

    private int speedX;

    private int type;
    private double speedY;
    private double gravity;
    private double floorY;

    // default type of enemy
    Enemy(int x,int y,int size,int direction,int x1,int x2){
        this.x=x;
        this.y=y;
        this.size=size;
        this.direction=direction; // 0=>left 1=>right
        // enemy moves between these positions
        this.x1=x1; // min position
        this.x2=x2; // max position
        type=0;
    }

    // second type of enemy
    Enemy(int x,int y,int size,int direction,int x1,int x2,int floorY){
        this(x,y,size,direction,x1,x2);
        this.floorY=floorY;
        speedY=17;
        gravity=1.0;
        type=1;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    public void move(){

        if (direction==0){
            speedX=-5;
        } else {
            speedX=5;
        }

        int nextX = x+speedX;
        // border checking (!!! x1 and x2 were initialized with halfsize of the enemy considered so there is no need to use halfsize in checking)
        if (nextX<=x1){
            x=x1; // snap the enemy to the left side to prevent returning early
            direction=1;
        } else if (nextX>=x2){
            x=x2; // snap the enemy to the right side to prevent returning early
            direction=0;
        } else {
            x=nextX;
        }

        if (type==1){
            speedY-=gravity;
            double nextY = y+speedY;
            if (nextY-size/2<=floorY){
                y= (int) floorY+size/2;
                speedY=17;
            } else {
                y=(int)nextY;
            }
        }

    }

    public void draw(){
        if (type==0){
            StdDraw.picture(x,y,"./assets/mushroom.png",size,size);
        } else {
            StdDraw.picture(x,y,"./assets/bouncingEnemy.png",size,size);
        }
    }

}
