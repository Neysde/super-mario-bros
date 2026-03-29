//Your Name
//Student ID

import java.util.ArrayList;

public class Level {
    private int id;
    private ArrayList<Enemy> enemies;
    private String clue;
    private int[][] coins;

    // the main constructor for level 3 (contains all fields)
    public Level(int id, ArrayList<Enemy> enemies, String clue, int[][] coins) {
        this.id = id;
        this.enemies = enemies;
        this.clue = clue;
        this.coins = coins;
    }

    // for level 2 without coins
    public Level(int id, ArrayList<Enemy> enemies,String clue) {
        this(id, enemies, clue, new int[0][0]);
    }

    // for level 1 without enemies and without coins
    public Level(int id, String clue) {
        this(id, new ArrayList<Enemy>(), clue, new int[0][0]);
    }

    public int getId() {
        return id;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public String getClue() {
        return clue;
    }

    public int[][] getCoins() {
        return coins;
    }


}
