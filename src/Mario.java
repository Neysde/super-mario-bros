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
    double speedX=10.0;
    double gravity=2.0;

    // direction
    boolean isRight;

    boolean isSPressed=false;
    int teleportMode = 0; //0=>normal gameplay, 1=>entering at the lower portal, 2=>entering at the upper portal

    // animation state of the player
    int animationState;

    private int deathCount;
    private boolean isDead;
    private boolean wasWPressed=false;

    Mario(int x,int y){ //normally takes x and y but for now no need
        this.x=x;
        this.y=y;
        isRight=true;
    }

    //draws mario
    public void draw(int animationState){
        if (isDead){
            StdDraw.picture(x,y,"./assets/dead.png",size,size);
            if (y+size<0){ // if player falls below to the screen fully
                respawn();
            }
        } else {
            if (animationState==3){ //jumping is the highest priority (except dead animation)
                if (isRight){
                    StdDraw.picture(x,y,"./assets/jumpRight.png",size,size);
                } else {
                    StdDraw.picture(x,y,"./assets/jumpLeft.png",size,size);
                }
            } else if (animationState==1) { //walking checks
                StdDraw.picture(x,y,"./assets/walkRight1.png",size,size);
            } else if (animationState==2) {
                StdDraw.picture(x,y,"./assets/walkRight2.png",size,size);
            } else if (animationState==-1) {
                StdDraw.picture(x,y,"./assets/walkLeft1.png",size,size);
            } else if (animationState==-2) {
                StdDraw.picture(x,y,"./assets/walkLeft2.png",size,size);
            } else if (animationState==4) {
                StdDraw.picture(x,y,"./assets/crouch.png",size,size);
            } else if (animationState==0) { // lastly stand checks as a fallback
                if (isRight){
                    StdDraw.picture(x,y,"./assets/standRight.png",size,size);
                } else {
                    StdDraw.picture(x,y,"./assets/standLeft.png",size,size);
                }
            }
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

    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    public int handleInput(int frame, boolean onGround, Map map){ //returning int to communicate with draw()
        boolean currentWState=false;
        animationState=0; //default
        if (StdDraw.isKeyPressed(KeyEvent.VK_W)){
            currentWState=true;
        }

        if (currentWState && onGround && !wasWPressed){
            jump();
            animationState=3;
        }

        if (StdDraw.isKeyPressed(KeyEvent.VK_D)){
            int walkingState=(frame/8)%2; //to create metronome-like state. in every 8 frames this value changes to 0 or 1 alternatively so we can draw 2 different walking picture alternatively
            
            int nextX=x + (int) speedX;
            if (map.isValidXMove(nextX,y,size/2,800,teleportMode)){
                x=nextX;
            }

            isRight=true;

            if (walkingState==0){
                animationState=1; // walkRight1
            } else {
                animationState=2; // walkRight2
            }

        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_A)){
            int walkingState=(frame/8)%2;
            int nextX = x - (int) speedX;
            if (map.isValidXMove(nextX,y,size/2,800,teleportMode)){
                x=nextX;
            }

            isRight=false; // player going to the left side

            if (walkingState==0){
                animationState=-1; // walkLeft1
            } else {
                animationState=-2; // walkLeft2
            }
            
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_S)){
            animationState=4; // crouch
            isSPressed=true;
        } else {
            isSPressed=false;
        }
        wasWPressed=currentWState;
        if (!onGround){
            animationState=3; // player must be jumping if player is not on the ground
        }
        return animationState;
    }

    public int handleInputMainMenu(int frame, boolean onGround, Map map){ //returning int to communicate with draw()
        boolean currentWState=false;
        animationState=0;
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
