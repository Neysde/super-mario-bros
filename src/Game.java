//Your Name
//Student ID

import java.awt.*;
import java.awt.event.KeyEvent;
import java.security.Key;
import java.util.ArrayList;

public class Game {
    private ArrayList<Level> levels ;
    private Map map;
    private int width;
    private int height;

    Game(ArrayList<Level> levels, Map map,int width,int height){
        this.levels=levels;
        this.map=map;
        this.width=width;
        this.height=height;
    }

    public void run(Color skyColor, Color mainMenuColor0, Color mainMenuColor1, Color mainMenuColor2, Color marioDarkBlue, Color marioRed, Color marioGold, Color marioLightBlue){

        Mario mario = new Mario(width/2,140); //test mario

        int frameCount = 0;
        int gameState = 0; // 0=>main menu 1=> game 2=> endgame
        Level currentLevel = levels.get(0);
        int pauseDuration = 30;
        int currentFPS;
        int fpsKeyDelay=0; // delay is to prevent unwanted fps increase or decrease when adjustment
        long startingTime=0;
        int coinsCollected=0;
        boolean isLevelFinished=false;



        while (true){

            currentFPS=1000/pauseDuration;

            // main menu part
            if (gameState==0){

                boolean isGroundedMain = map.isOnGroundMainMenu(mario.x, mario.y, mario.size/2.0, mario.speedY,width,height);

                // processes the player's keyclicks
                int directionMain = mario.handleInputMainMenu(frameCount,isGroundedMain,map);

                // apply gravity to player
                applyGravityMainMenu(mario);

                // fps adjustment
                if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT) && fpsKeyDelay==0){
                    pauseDuration+=5;
                    fpsKeyDelay=10;
                } else if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT) && fpsKeyDelay==0){
                    if (pauseDuration>5){ //prevents division by 0 error
                        pauseDuration-=5;
                    } else {
                        pauseDuration=1;
                    }
                    fpsKeyDelay=10;
                }
                if (fpsKeyDelay>0){
                    fpsKeyDelay--;
                }

                if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)){
                    gameState=1;
                    startingTime=System.currentTimeMillis();
                    mario.respawn();
                }

                // drawing part
                StdDraw.clear(skyColor);

                startScreen(mario,currentFPS,mainMenuColor0,mainMenuColor1,mainMenuColor2);

                mario.draw(); // draws the player on the canvas

            } else if (gameState==1){
                // prevents back and forth teleporting
                if (!map.checkPortalCollision(mario.x, mario.y,mario.size/2.0,0,0)){ // this is supposed to check whether the hitbox of the player is away from the portal or not
                    mario.isTeleporting=false;
                }
                // it checks whether it is grounded so player can jump
                boolean isGrounded = map.isOnGround(mario.x,mario.y, mario.size/2.0, mario.speedY,width,height, mario.isTeleporting);

                // processes the player's keyclicks
                if (!mario.isDead()){
                    int direction = mario.handleInput(frameCount,isGrounded,map);
                }

                // Catch the teleportation jump before gravity has a chance to break that
                boolean isSPressed = StdDraw.isKeyPressed(KeyEvent.VK_S);
                map.handleTeleport(mario, isSPressed);

                if (currentLevel.getId()==3){
                    coinsCollected+=map.collectCoin(mario);
                }

                if (map.isOnLowerPipe(mario) && currentLevel.getId()==1){
                    isLevelFinished=true;
                } else if (map.isOnLowerPipe(mario) && currentLevel.getId()==2) {
                    isLevelFinished=true;
                } else if (map.isOnLowerPipe(mario) && currentLevel.getId()==3 && coinsCollected==3) {
                    isLevelFinished=true;
                }

                if (currentLevel.getId()==2 || currentLevel.getId()==3 || currentLevel.getId()==4){
                    ArrayList<Enemy> enemies = levels.get(currentLevel.getId()-1).getEnemies();

                    for (Enemy enemy : enemies){
                        enemy.move();

                        if (!mario.isDead()){
                            if (mario.checkEnemyCollision(enemy)){
                                mario.die();
                            }
                        }
                    }
                }

                // apply gravity to player
                applyGravity(mario);

                if (!isLevelFinished){
                    // drawing part
                    StdDraw.clear(skyColor);

                    if (mario.isDead()){ // !!! when player is dead they must fall in front of the obstacles
                        map.draw();
                        mario.draw();
                    } else { // !!! but when player is alive they must be drawn behind the pipes and portals
                        mario.draw();
                        map.draw();
                    }


                    if (currentLevel.getId()==2 || currentLevel.getId()==3 || currentLevel.getId()==4) {
                        // draws enemies in level 2 and 3
                        ArrayList<Enemy> enemies = levels.get(currentLevel.getId()-1).getEnemies();

                        for (Enemy enemy:enemies){
                            enemy.draw();
                        }

                        // draws coins in level 3
                        if (currentLevel.getId()==3){
                            map.drawCoin();
                        }
                    }

                    // draws hud
                    hud(getTime(startingTime),currentLevel.getId(),mario.getDeathCount(),marioDarkBlue,marioGold);
                } else {
                    long levelFinishPauseTime=levelBanner(currentLevel.getId(),pauseDuration,marioDarkBlue,marioGold);
                    startingTime+=levelFinishPauseTime;
                    System.out.println(startingTime);
                    if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)){
                        mario.respawn();
                        currentLevel=levels.get(currentLevel.getId());
                        isLevelFinished=false;
                    }
                }
            }

            StdDraw.show();
            StdDraw.pause(pauseDuration);
            frameCount++;
        }
    }

    private void applyGravity(Mario mario){ //applies gravity constantly in every frame because it is physics



        mario.speedY -= mario.gravity; // apply gravity to player's speed

        if (mario.isDead()){  // skip collision checks just apply gravity to make player fall off from the screen
            mario.y+=mario.speedY;
        } else {
            // checking floor collision
            if(map.isOnGround(mario.x,mario.y, mario.size/2.0,mario.speedY,width,height, mario.isTeleporting)){
                // because of how we check on isOnGround to prevent oscillation bug, player may still be hovering on the floor even though isOnGround returns true
                while (!map.isOnGround(mario.x, mario.y, mario.size/2.0, 0, width, height, mario.isTeleporting)) { // to prevent hovering, we need to decrease player's y position one by one independent of the speed to make sure player is on the block
                    mario.y -= 1;
                }
                mario.speedY=0;
            } else if (map.isOnCeiling(mario.x,mario.y, mario.size/2.0,mario.speedY,width,height, mario.isTeleporting)){
                while (!map.isOnCeiling(mario.x, mario.y, mario.size/2.0, 0, width, height, mario.isTeleporting)) {
                    mario.y += 1;
                }
                mario.speedY=0;
            }

            mario.y+=mario.speedY; // apply the speed to the player's position every frame
        }

    }

    private void applyGravityMainMenu(Mario mario){ //applies gravity constantly in every frame because it is physics

        mario.speedY -= mario.gravity; // apply gravity to player's speed
        // checking floor collision
        if(map.isOnGroundMainMenu(mario.x,mario.y, mario.size/2.0,mario.speedY,width,height)){
            // because of how we check on isOnGround to prevent oscillation bug, player may still be hovering on the floor even though isOnGround returns true
            while (!map.isOnGroundMainMenu(mario.x, mario.y, mario.size/2.0, 0, width, height)) { // to prevent hovering, we need to decrease player's y position one by one independent of the speed to make sure player is on the block
                mario.y -= 1;
            }
            mario.speedY=0;
        }
        // there is no way player can reach to ceiling in main menu
        mario.y+=mario.speedY;
    }

    // draws start screen
    private void startScreen(Mario mario, int currentFPS,Color mainMenuColor0, Color mainMenuColor1, Color mainMenuColor2){
        for (int i=0;i<3;i++){
            for (int k=0;k<20;k++){
                StdDraw.picture(20+k*40,20+40*i,"./assets/block.png",40,40);
            }
        }

        // main menu rectangle
        StdDraw.setPenColor(mainMenuColor0);
        StdDraw.filledRectangle(width/2,550,300,100);

        // border of the main menu rectangle
        StdDraw.setPenColor(mainMenuColor1);
        StdDraw.setPenRadius(0.006); // thickness of the border
        StdDraw.rectangle(width/2, 550, 300, 100);


        // draws title box, border and title texts to the main menu canvas
        StdDraw.setFont(new Font("SansSerif", Font.BOLD, 40));
        StdDraw.setPenColor(mainMenuColor2);
        StdDraw.text(width/2,600,"SUPER");
        StdDraw.setFont(new Font("SansSerif", Font.BOLD, 55));
        StdDraw.text(width/2,530,"MARIO BROS.");

        // draws fps counter and controls scheme to the main menu canvas
        StdDraw.setFont(new Font("Monospaced", Font.BOLD, 22));
        StdDraw.setPenColor(Color.white);
        StdDraw.text(width/2,420,"Press [Space] to start");
        StdDraw.setFont(new Font("Monospaced", Font.PLAIN, 22));
        StdDraw.text(width / 2.0, 380, "Move: [A] [D] [W]");
        StdDraw.text(width / 2.0, 350, "FPS: ~" + currentFPS + "     Adjust: [\u2190] [\u2192]");
    }

    private void hud(String timeElapsed, int currentLevel, int deaths, Color marioDarkBlue, Color marioGold){
        StdDraw.setPenColor(marioDarkBlue);
        StdDraw.filledRectangle(width/2,90,width/2,90);
        StdDraw.setPenColor(marioGold);
        StdDraw.setFont(new Font("SansSerif", Font.BOLD, 20));
        StdDraw.text(width/2,90,levels.get(currentLevel-1).getClue());

        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(125,125,"Move: [A] [D] [W]");
        StdDraw.text(125,90,"Restart: [R]");
        StdDraw.text(125,50,"Camera: [C]");

        StdDraw.text(700,125,"Level "+currentLevel);
        StdDraw.text(700,90,timeElapsed);
        StdDraw.setPenColor(marioGold);
        StdDraw.text(700,50,"Deaths: "+deaths);
    }

    // gets time elapsed and returns the displayed string
    private String getTime(long startingTime){
        long elapsedMillis = System.currentTimeMillis()-startingTime;

        long totalSeconds = elapsedMillis / 1000;
        long displayMillis = (elapsedMillis % 1000) / 10; // to display 2 digit ms, need to divide by 10 to eliminate the last digit
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", minutes, seconds,displayMillis); // to achieve 00:00:00 format
    }


    private long levelBanner(int id,int pauseDuration,Color marioDarkBlue,Color marioGold){
        long levelFinishPauseStart = System.currentTimeMillis(); // the exact time when level finishes

        // separate while loop to draw level finish banner because of the time calculations, If we do not include that loop, time calculations would went off because of the pause in the main loop.
        while(true){

            if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)){
                return System.currentTimeMillis()-levelFinishPauseStart; // elapsed time from the level finish to pressing space to proceed
            }

            // level banner drawings
            StdDraw.setPenColor(marioDarkBlue);
            StdDraw.filledRectangle(width/2,400,width/2,80);

            StdDraw.setPenColor(marioGold);
            StdDraw.setFont(new Font("SansSerif", Font.BOLD, 35));
            StdDraw.text(width/2,425,"Level "+id+" Complete!");

            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font("SansSerif", Font.BOLD, 25));
            StdDraw.text(width/2,375,"Press [Space] to Continue");
            StdDraw.show();
            StdDraw.pause(pauseDuration);
        }

    }
}
