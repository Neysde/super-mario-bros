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

    Enemy(int x,int y,int size,int direction,int x1,int x2){
        this.x=x;
        this.y=y;
        this.size=size;
        this.direction=direction; // 0=>left 1=>right
        // enemy moves between these positions
        this.x1=x1; // min position
        this.x2=x2; // max position
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
    }

    public void draw(){
        StdDraw.picture(x,y,"./assets/mushroom.png",size,size);
    }

}
