//Your Name
//Student ID

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static void readFile(int[][] positions, String file){
        try{
            FileInputStream fis = new FileInputStream(file);
            Scanner scanner = new Scanner(fis);
            int lineCount=0;
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                String[] lineSplit = line.split(",");
                int x = Integer.parseInt(lineSplit[0]);
                int y = Integer.parseInt(lineSplit[1]);
                int halfWidth = Integer.parseInt(lineSplit[2]);
                int halfHeight = Integer.parseInt(lineSplit[3]);
                positions[lineCount][0]=x;
                positions[lineCount][1]=y;
                positions[lineCount][2]=halfWidth;
                positions[lineCount][3]=halfHeight;
                lineCount++;
            }
            scanner.close();
            fis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final int width = 800;
        final int height = 800;

        final int cameraModeW = 400;
        final int cameraModeH = 400;

        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.setTitle("Super Mario Bros.");
        StdDraw.enableDoubleBuffering();

        Enemy enemy0 = new Enemy(340, 580, 40, 0, 260, 420); //new Enemy(x, y, size, direction, x1, x2)
        Enemy enemy1 = new Enemy(440, 300, 40, 1, 380, 500); //new Enemy(x, y, size, direction, x1, x2)

        String clue0 = "Go to the exit pipe!";
        String clue1 = "Do not mess with mushrooms!";
        String clue2 = "Do not forget to collect all the coins!";

        Color skyColor = new Color(146, 144, 255);
        Color mainMenuColor0 = new Color(153, 78, 0);
        Color mainMenuColor1 = new Color(100, 50, 0);
        Color mainMenuColor2 = new Color(255, 204, 197);
        Color marioDarkBlue = new Color(32, 56, 136);
        Color marioRed = new Color(228, 0, 18);
        Color marioGold = new Color(255, 183, 0);
        Color marioLightBlue = new Color(107, 140, 255);
<<<<<<< HEAD
=======

        // reads map element location files and saves their locations into their arrays
        int[][] obstacles = new int[191][4];
        int[][] pipes = new int[4][4];
        int[][] portals = new int[4][4];
        int[][] coins = new int[3][4];
        readFile(obstacles,"./data/obstacles.txt");
        readFile(pipes,"./data/pipes.txt"); //check location it seems makes 10 to 30 in 2. line
        readFile(portals,"./data/portals.txt");
        readFile(coins,"./data/coins.txt");


        Map map = new Map(obstacles,pipes,portals); // map object

        ArrayList<Enemy> enemies = new ArrayList<>(); // enemies

        enemies.add(enemy0);
        enemies.add(enemy1);

        ArrayList<Level> levels = new ArrayList<>(); //test levels

        levels.add(new Level(1,clue0));
        levels.add(new Level(2,enemies,clue1)); //lvl2 test

        Game game = new Game(levels,map,width,height);

        game.run(skyColor,mainMenuColor0,mainMenuColor1,mainMenuColor2,marioDarkBlue,marioRed,marioGold,marioLightBlue);

>>>>>>> 9f7d43b (second commit)
    }
}
