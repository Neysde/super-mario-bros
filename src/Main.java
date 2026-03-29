//Your Name
//Student ID

import java.awt.*;

public class Main {
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
    }
}
