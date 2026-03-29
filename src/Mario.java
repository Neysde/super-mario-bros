//Your Name
//Student ID

import java.awt.event.KeyEvent;
import java.security.Key;

public class Mario {

    final int spawnX = 60;
    final int spawnY = 770;
    final int size = 40;

    int x;
    int y;
    double speedY;
    double speedX=7.0;
    double gravity=2.0;

    boolean isSPressed=false;
    boolean isTeleporting=false;

    private int deathCount;
    private boolean isDead;
    private boolean wasWPressed=false;

    Mario(int x,int y){ //normally takes x and y but for now no need
        this.x=x;
        this.y=y;
    }

    //draws mario
    public void draw(){
        if (isDead){
            StdDraw.picture(x,y,"./assets/dead.png",size,size);
            if (y+size<0){ // if player falls below to the screen fully
                respawn();
            }
        } else {
            StdDraw.picture(x,y,"./assets/standRight.png",size,size);
        }

    }

    public void respawn(){
        x=spawnX;
        y=spawnY;
        speedY=0;
        isDead=false;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public int handleInput(int frame, boolean onGround, Map map){ //returning int to communicate with draw()
        boolean currentWState=false;
        int animationState=0; //default
        if (StdDraw.isKeyPressed(KeyEvent.VK_W)){
            currentWState=true;
        }

        if (currentWState && onGround && !wasWPressed){
            jump();
            animationState=2;
        }

        if (StdDraw.isKeyPressed(KeyEvent.VK_D)){
            int nextX=x + (int) speedX;
            if (map.isValidXMove(nextX,y,size/2,800,isTeleporting)){
                x=nextX;
            }
            animationState=1; //for turning right
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_A)){
            int nextX = x - (int) speedX;
            if (map.isValidXMove(nextX,y,size/2,800,isTeleporting)){
                x=nextX;
            }
            animationState=-1; //for turning left
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_S)){
            isSPressed=true;
        } else {
            isSPressed=false;
        }
        wasWPressed=currentWState;
        return animationState; //for standing still
    }

    public int handleInputMainMenu(int frame, boolean onGround, Map map){ //returning int to communicate with draw()
        boolean currentWState=false;
        int animationState=0; //default
        if (StdDraw.isKeyPressed(KeyEvent.VK_W)){
            currentWState=true;
        }

        if (currentWState && onGround && !wasWPressed){
            jump();
            animationState=2;
        }

        if (StdDraw.isKeyPressed(KeyEvent.VK_D)){
            int nextX=x + (int) speedX;
            if (map.isValidXMoveMainMenu(nextX,y,size/2.0,speedX,800)){
                x=nextX;
            }
            animationState=1; //for turning right
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_A)){
            int nextX = x - (int) speedX;
            if (map.isValidXMoveMainMenu(nextX,y,size/2.0,speedX,800)){
                x=nextX;
            }
            animationState=-1; //for turning left
        }
        wasWPressed=currentWState;
        return animationState; //for standing still
    }

    public void jump(){
        speedY=23.0; //sets speed y to a large value
    }

    public boolean checkEnemyCollision(Enemy enemy){
        int playerLeft = x-size/2;
        int playerRight = x+size/2;
        int playerTop = y+size/2;
        int playerBottom = y-size/2;

        int enemyLeft = enemy.getX()-enemy.getSize()/2;
        int enemyRight = enemy.getX()+enemy.getSize()/2;
        int enemyTop = enemy.getY()+enemy.getSize()/2;
        int enemyBottom = enemy.getY()-enemy.getSize()/2;

        if (playerLeft<enemyRight && playerRight>enemyLeft && playerTop>enemyBottom && playerBottom<enemyTop){
            return true;
        }

        return false;
    }

    public void die(){
        jump();
        deathCount++;
        isDead=true; //use when drawing death animation
    }

    public boolean isDead() {
        return isDead;
    }


}
