package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] getWorldFromInput(String input) {
        if (input == null || !input.toUpperCase().startsWith("N") || !input.toUpperCase().endsWith("S")) {
            throw new IllegalArgumentException("Invalid input format");
        }

        // 提取种子
        String seedString = input.substring(1, input.length() - 1);
        long seed = Long.parseLong(seedString);

        // 使用种子创建随机生成器
        Random random = new Random(seed);

        // 生成随机世界
        World world = new World();
        world.generateRooms(random); // 随机生成5-15个房间

        // 返回生成的世界
        return world.getWorld();

    }


    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}
