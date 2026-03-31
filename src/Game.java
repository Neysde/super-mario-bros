//Your Name
//Student ID

import java.awt.*;
import java.awt.event.KeyEvent;
import java.security.Key;
import java.util.ArrayList;

public class Game {
    private ArrayList<Level> levels ; // levels' data held
    private Map map; // map data
    private int width; // width of the canvas
    private int height; // height of the canvas
    private int cameraModeW; // player camera width
    private int cameraModeH; // player camera height

    Game(ArrayList<Level> levels, Map map,int width,int height,int cameraModeW,int cameraModeH){
        this.levels=levels;
        this.map=map;
        this.width=width;
        this.height=height;
        this.cameraModeW=cameraModeW;
        this.cameraModeH=cameraModeH;
    }

    // game initialization function, drawings calculations take place here actually
    public void run(Color skyColor, Color mainMenuColor0, Color mainMenuColor1, Color mainMenuColor2, Color marioDarkBlue, Color marioRed, Color marioGold, Color marioLightBlue){

        Mario mario = new Mario(width/2,140); // initializes player

        int frameCount = 0; // counts frames
        int gameState = 0; // 0=>main menu 1=> game 2=> endgame
        Level currentLevel = levels.get(0); // holds the current level object (level1,2,3,4)
        int pauseDuration = 30; // default pause duration is 30
        int currentFPS; // 1000/pauseDuration

        // hold the value whether the left or right arrow was pressed in previous frame to prevent unwanted clicks
        boolean wasLeftArrowPressed=false;
        boolean wasRightArrowPressed=false;

        long startingTime=0; // holds the starting time of the game
        int coinsCollected=0;
        boolean isLevelFinished=false; // whether the level is finished

        // camera mode check
        boolean isFollowingCamera=false;
        boolean wasCPressed=false;

        // player animation state
        int animationState=0;

        // end game menu option check
        boolean isRestart=false;

        // hold the value whether the up or down arrow was pressed in previous frame to prevent unwanted clicks
        boolean wasUpArrowPressed=false;
        boolean wasDownArrowPressed=false;

        String victoryTime="00:00:00"; // holds the time displayed on endgame menu

        while (true){

            currentFPS=1000/pauseDuration; // current fps value (displayed in main menu)

            // main menu part
            if (gameState==0){

                // checks whether the player is on the ground in main menu
                boolean isGroundedMain = map.isOnGroundMainMenu(mario.x, mario.y, mario.size/2.0, mario.speedY);

                // processes the player's keyclicks
                animationState = mario.handleInputMainMenu(frameCount,isGroundedMain,map);

                // applies gravity to player
                applyGravityMainMenu(mario);

                // fps adjustment (used was pressed logic to prevent missclicks)
                if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT) && !wasLeftArrowPressed){
                    pauseDuration+=5;
                } else if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT) && !wasRightArrowPressed){
                    if (pauseDuration>5){ //prevents division by 0 error
                        pauseDuration-=5;
                    } else {
                        pauseDuration=1; // fps can be 1000 maximum
                    }
                }

                // starts the game
                if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)){
                    gameState=1;
                    startingTime=System.currentTimeMillis(); // assigns the starting time
                    mario.respawn(); // to start the player in spawn position
                }

                // drawing part for main menu
                StdDraw.clear(skyColor); // draws the background

                startScreen(mario,currentFPS,mainMenuColor0,mainMenuColor1,mainMenuColor2); // draws the start screen elements (menu, ground)

                mario.draw(animationState); // draws the player on the canvas

                // checks whether the left arrow is being pressed
                if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)){
                    wasLeftArrowPressed=true;
                } else {
                    wasLeftArrowPressed=false;
                }

                // checks whether the right arrow is being pressed
                if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)){
                    wasRightArrowPressed=true;
                } else {
                    wasRightArrowPressed=false;
                }

            } else if (gameState==1){ // gameplay part

                // game reset logic
                if (StdDraw.isKeyPressed(KeyEvent.VK_R)){
                    levels=Main.initializeLevels(); // initializes the level from scratch like in the first opening
                    currentLevel=levels.get(0); // resets the current level to level1
                    mario.respawn();
                    mario.setDeathCount(0); // resets the death count
                    isLevelFinished=false;
                    coinsCollected=0; // resets the collected coin count
                    startingTime=System.currentTimeMillis(); // resets the starting time
                }

                // camera mode toggle logic
                if (StdDraw.isKeyPressed(KeyEvent.VK_C) && !wasCPressed){
                    isFollowingCamera=!isFollowingCamera;
                }

                // camera mode scale login
                if (isFollowingCamera){
                    // scalings' min and max values
                    int minX = mario.x-cameraModeW/2;
                    int maxX = mario.x+cameraModeW/2;
                    int minY = mario.y-cameraModeH/2;
                    int maxY= mario.y+cameraModeH/2;

                    // to prevent showing outside the map, original map borders need to be considered
                    if (minX<0){
                        minX=0;
                        maxX=width/2;
                    } else if (maxX>width){
                        minX=width/2;
                        maxX=width;
                    }
                    if (minY < 0) {
                        minY=0;
                        maxY=height/2;
                    } else if (maxY>height){
                        maxY=height;
                        minY=height/2;
                    }
                    // creates a scaled 400x400 window following player
                    StdDraw.setXscale(minX,maxX);
                    StdDraw.setYscale(minY,maxY);
                } else {
                    // default fullscreen scale
                    StdDraw.setXscale(0, width);
                    StdDraw.setYscale(0, height);
                }


                // it checks whether it is grounded so player can jump
                boolean isGrounded = map.isOnGround(mario.x, mario.y, mario.size/2.0, mario.speedY, width, mario.teleportMode);

                // processes the player's keyclicks and returns an integer to be used to select which animation will be used on player drawing
                if (!mario.isDead() && mario.teleportMode==0){ // player must be alive and not teleporting to accept movement inputs
                    animationState = mario.handleInput(frameCount,isGrounded,map);
                }

                // Catch the teleportation jump before gravity has a chance to break that
                boolean isSPressed = StdDraw.isKeyPressed(KeyEvent.VK_S);
                map.handleTeleport(mario, isSPressed);

                // check coin collision (only on level 3 and 4) with player and collect coins and increment the coinsCollected by 1
                if (currentLevel.getId()==3 || currentLevel.getId()==4){
                    coinsCollected+=map.collectCoin(mario,currentLevel);
                }

                // level finish logics
                if (map.isOnLowerPipe(mario) && currentLevel.getId()==1){
                    isLevelFinished=true;
                } else if (map.isOnLowerPipe(mario) && currentLevel.getId()==2) {
                    isLevelFinished=true;
                } else if (map.isOnLowerPipe(mario) && currentLevel.getId()==3 && coinsCollected==3) {
                    coinsCollected=0; // must be resetted for the new level to count from the start
                    isLevelFinished=true;
                } else if (map.isOnLowerPipe(mario) && currentLevel.getId()==4 && coinsCollected==3) {
                    victoryTime = getTime(startingTime); // gets the time elapsed as string to display in victory screen
                    coinsCollected=0;
                    isLevelFinished=true;
                }

                // enemy calculations for level 2,3,4
                if (currentLevel.getId()==2 || currentLevel.getId()==3 || currentLevel.getId()==4){
                    ArrayList<Enemy> enemies = levels.get(currentLevel.getId()-1).getEnemies(); // gets the enemies arraylist for current level

                    // moves each enemy and checks the collision with player if player is alive
                    for (Enemy enemy : enemies){
                        enemy.move();

                        if (!mario.isDead()){
                            if (mario.checkEnemyCollision(enemy)){
                                mario.die(); // if player collides with enemy, make player dead
                            }
                        }
                    }
                }

                // applies gravity to player
                if (mario.teleportMode==0){
                    applyGravity(mario); // only applies gravity if player is not teleporting
                } else {
                    int[][] portals = map.getPortals();
                    int[][] upperPortal = {portals[0],portals[1]};
                    int[][] lowerPortal = {portals[2],portals[3]};

                    if (mario.teleportMode==1){ // teleporting from lower portal to upper portal

                        mario.y-=3; // make player fall with constant speed
                        mario.animationState=4; // hold crouch animation while teleporting

                        // if player's topside is below than the lower portal's entrance
                        if (mario.y+mario.size/2<lowerPortal[1][1]+lowerPortal[1][3]){
                            // teleport player to the upper portal
                            mario.x=upperPortal[0][0];
                            mario.y=upperPortal[0][1];
                            mario.speedY=0; // make player's speed 0 to make player fall freely with gravity
                            mario.teleportMode=0; // exit from the teleport mode
                        }

                    } else if (mario.teleportMode==2){ // teleporting from upper portal to lower portal
                        mario.y+=3; // make player rise with constant speed
                        mario.animationState=3; // hold jump animation while teleporting

                        // if player's bottom side is above than the upper portal's entrance
                        if (mario.y-mario.size/2>upperPortal[1][1]-upperPortal[1][3]){
                            // teleport player to the lower portal
                            mario.x=lowerPortal[0][0];
                            mario.y=lowerPortal[0][1];

                            mario.speedY=28.0; // make player jump from the lower portal
                            mario.teleportMode=0; // exit from the teleport mode
                        }
                    }
                }


                if (!isLevelFinished){ // drawings when level is not finished
                    // drawing part
                    StdDraw.clear(skyColor);

                    if (mario.isDead()){ // !!! when player is dead they must fall in front of the obstacles
                        map.draw();
                        mario.draw(animationState);
                    } else { // !!! but when player is alive they must be drawn behind the pipes and portals
                        mario.draw(animationState);
                        map.draw();
                    }


                    if (currentLevel.getId()==2 || currentLevel.getId()==3 || currentLevel.getId()==4) {
                        // draws enemies in level 2,3,4
                        ArrayList<Enemy> enemies = levels.get(currentLevel.getId()-1).getEnemies();

                        for (Enemy enemy:enemies){
                            enemy.draw();
                        }

                        // draws coins in level 3 and 4
                        if (currentLevel.getId()==3 || currentLevel.getId()==4){
                            map.drawCoin(currentLevel);
                        }
                    }

                    // draws hud
                    hud(getTime(startingTime),currentLevel.getId(),mario.getDeathCount(),marioDarkBlue,marioGold);
                } else if (currentLevel.getId()==4){ // when level 4 is finished
                    gameState=2; // change the state to endgame
                } else { // when level 1,2,3 is finished
                    long levelFinishPauseTime=levelBanner(currentLevel.getId(),pauseDuration,marioDarkBlue,marioGold); // holds the time passed when level finished in level finish banner
                    startingTime+=levelFinishPauseTime; // adds this time to starting time to make sure the time in level end banner passed not counted when starting the next level
                    if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)){ // go to the next level when pressed space
                        mario.respawn(); // start the
                        currentLevel=levels.get(currentLevel.getId()); // increases the current level as level id = level index in the list + 1
                        isLevelFinished=false;
                    }
                }

                // to check whether c held down to prevent unwanted changes in camera modes.
                if (StdDraw.isKeyPressed(KeyEvent.VK_C)){
                    wasCPressed=true;
                } else {
                    wasCPressed=false;
                }
            } else if (gameState==2){ // endgame menu

                // switch between exit and restart options
                if (StdDraw.isKeyPressed(KeyEvent.VK_UP) && !wasUpArrowPressed){
                    isRestart=!isRestart;
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN) && !wasDownArrowPressed){
                    isRestart=!isRestart;
                }


                if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)){
                    if (isRestart){ // if restart selected (same logic with restart button)
                        levels=Main.initializeLevels();
                        currentLevel=levels.get(0);
                        mario.respawn();
                        mario.setDeathCount(0);
                        isLevelFinished=false;
                        coinsCollected=0;
                        startingTime=System.currentTimeMillis();
                        gameState=1;
                    } else { // exits from the game when exit selected
                        System.exit(0);
                    }
                }

                // endgame screen drawing part

                // draw endgame screen considering fullscreen scale
                StdDraw.setXscale(0,width);
                StdDraw.setXscale(0,height);

                StdDraw.clear(marioDarkBlue);
                StdDraw.setPenColor(marioGold);
                StdDraw.setFont(new Font("SansSerif", Font.BOLD, 45));
                StdDraw.text(width/2,650,"You Won!");

                StdDraw.setPenColor(marioGold);
                StdDraw.setFont(new Font("SansSerif", Font.BOLD, 28));

                // switch the drawings according to the selections with up and down arrows
                if (!isRestart){
                    StdDraw.text(width/2,height/2,"> Exit <");

                    StdDraw.setPenColor(marioLightBlue);
                    StdDraw.text(width/2,height/2-50,"Restart");
                } else {
                    StdDraw.text(width/2,height/2-50,"> Restart <");

                    StdDraw.setPenColor(marioLightBlue);
                    StdDraw.text(width/2,height/2,"Exit");
                }



                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.setFont(new Font("SansSerif", Font.BOLD, 20));
                StdDraw.text(width/2,185,"Time: "+victoryTime); // displays time elapsed in levels
                StdDraw.text(width/2,160,"Deaths: "+mario.getDeathCount()); // displays death count


                // to check whether arrow keys held down to prevent unwanted option changes
                if (StdDraw.isKeyPressed(KeyEvent.VK_UP)){
                    wasUpArrowPressed=true;
                } else {
                    wasUpArrowPressed=false;
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)){
                    wasDownArrowPressed=true;
                } else {
                    wasDownArrowPressed=false;
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
            if(map.isOnGround(mario.x,mario.y, mario.size/2.0,mario.speedY,width, mario.teleportMode)){
                // because of how we check on isOnGround to prevent oscillation bug, player may still be hovering on the floor even though isOnGround returns true
                while (!map.isOnGround(mario.x, mario.y, mario.size/2.0, 0, width, mario.teleportMode)) { // to prevent hovering, we need to decrease player's y position one by one independent of the speed to make sure player is on the block
                    mario.y -= 1;
                }
                mario.speedY=0;
            } else if (map.isOnCeiling(mario.x,mario.y, mario.size/2.0,mario.speedY,width, height)){
                while (!map.isOnCeiling(mario.x, mario.y, mario.size/2.0, 0, width, mario.teleportMode)) {
                    mario.y += 1;
                }
                mario.speedY=0;
            }

            mario.y+=mario.speedY; // apply the speed to the player's position every frame
        }

    }

    // similar logic but its not checking the obstacles in levels
    private void applyGravityMainMenu(Mario mario) { //applies gravity constantly in every frame because it is physics

        mario.speedY -= mario.gravity; // apply gravity to player's speed
        // checking floor collision
        if(map.isOnGroundMainMenu(mario.x,mario.y, mario.size/2.0,mario.speedY)){
            // because of how we check on isOnGround to prevent oscillation bug, player may still be hovering on the floor even though isOnGround returns true
            while (!map.isOnGroundMainMenu(mario.x, mario.y, mario.size/2.0, 0)) { // to prevent hovering, we need to decrease player's y position one by one independent of the speed to make sure player is on the block
                mario.y -= 1;
            }
            mario.speedY=0;
        }
        // there is no way player can reach to ceiling in main menu

        mario.y+=mario.speedY;
    }

    // draws start screen
    private void startScreen(Mario mario, int currentFPS,Color mainMenuColor0, Color mainMenuColor1, Color mainMenuColor2){
        // draws the bottom blocks
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

        //always draw hud like in full screen view
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.setPenColor(marioDarkBlue);
        StdDraw.filledRectangle(width/2,90,width/2,90);
        StdDraw.setPenColor(marioGold);
        StdDraw.setFont(new Font("SansSerif", Font.BOLD, 20));
        StdDraw.text(width/2+25,90,levels.get(currentLevel-1).getClue()); // clue text displayed in middle

        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(125,125,"Move: [A] [D] [W]");
        StdDraw.text(125,90,"Restart: [R]");
        StdDraw.text(125,50,"Camera: [C]");

        StdDraw.text(720,125,"Level "+currentLevel); // level indicator
        StdDraw.text(720,90,timeElapsed); // draws passed time
        StdDraw.setPenColor(marioGold);
        StdDraw.text(720,50,"Deaths: "+deaths); // death counter
    }

    // gets time elapsed and returns the displayed string
    private String getTime(long startingTime){
        long elapsedMillis = System.currentTimeMillis()-startingTime; // elapsed time in each frame between starting time

        // conversions
        long totalSeconds = elapsedMillis / 1000;
        long displayMillis = (elapsedMillis % 1000) / 10; // to display 2 digit ms, need to divide by 10 to eliminate the last digit
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", minutes, seconds,displayMillis); // to achieve 00:00:00 format
    }

    // level ending banner
    private long levelBanner(int id,int pauseDuration,Color marioDarkBlue,Color marioGold){
        long levelFinishPauseStart = System.currentTimeMillis(); // the exact time when level finishes

        // level banner should be drawn like in fullscreen mode independent of the camera mode
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

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
