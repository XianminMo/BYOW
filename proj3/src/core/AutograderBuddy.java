package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;
import core.Main;
import core.World;
import utils.FileUtils;

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
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty.");
        }
        if (input.startsWith("N") || input.startsWith("n")) {
            return handleNewGame(input);
        } else if (input.startsWith("L") || input.startsWith("l")) {
            return handleLoadGame(input);
        } else {
            throw new IllegalArgumentException("Invalid input format. Must start with 'N,n' or 'L, l'.");
        }
    }

    private static TETile[][] handleNewGame(String input) {
        // 提取种子
        int seedStartIndex = 1;
        int seedEndIndex = input.indexOf('s', seedStartIndex);
        long seed = Long.parseLong(input.substring(seedStartIndex, seedEndIndex));

        // 创建随机生成器
        Random random = new Random(seed);

        // 创建一个新世界
        World world = new World(random);

        // 提取移动命令
        int moveCommandsStartIndex = seedEndIndex + 1;
        int moveCommandsEndIndex = input.indexOf(':', moveCommandsStartIndex);
        String moveCommands = moveCommandsEndIndex == -1 ?
                input.substring(moveCommandsStartIndex) :
                input.substring(moveCommandsStartIndex, moveCommandsEndIndex);

        // 执行移动命令
        executeMoveCommands(world, moveCommands);

        // 如果有 :Q 则保存游戏
        if (moveCommandsEndIndex != -1) {
            saveGame(world, seed);
        }

        // 返回最终的世界状态
        return world.getWorld();
    }

    private static TETile[][] handleLoadGame(String input) {
        // 加载游戏
        World world = loadGame();

        // 提取移动命令
        int moveCommandsStartIndex = 1;
        int moveCommandsEndIndex = input.indexOf(':', moveCommandsStartIndex);
        String moveCommands = moveCommandsEndIndex == -1 ?
                input.substring(moveCommandsStartIndex) :
                input.substring(moveCommandsStartIndex, moveCommandsEndIndex);

        // 执行移动命令
        executeMoveCommands(world, moveCommands);

        // 如果有 :Q 则保存游戏
//        if (moveCommandsEndIndex != -1) {
//            saveGame(world, seed);
//        }

        // 返回最终的世界状态
        return world.getWorld();
    }

    private static void executeMoveCommands(World world, String moveCommands) {
        for (char command : moveCommands.toCharArray()) {
            world.moveAvatar(command);
        }
    }

    private static void saveGame(World world, long seedValue) {
        StringBuilder gameState = new StringBuilder();

        // 保存用于初始化随机数生成器的种子
        gameState.append(seedValue).append("\n");

        // 保存avatar的位置
        gameState.append(world.getAvatarX()).append(" ").append(world.getAvatarY()).append("\n");

        // 保存世界的TETile二维数组
        gameState.append(TETile.toString(world.getWorld()));

        // 使用FileUtils写入文件
        FileUtils.writeFile("savegame.txt", gameState.toString());
        System.out.println("游戏已保存到 savegame.txt");
    }

    private static World loadGame() {
        if (!FileUtils.fileExists("savegame.txt")) {
            System.out.println("没有找到保存的游戏文件。");
            System.exit(0);  // 如果没有找到保存文件，退出游戏
        }

        // 读取文件内容
        String content = FileUtils.readFile("savegame.txt");
        String[] lines = content.split("\n");

        // 恢复用于初始化随机数生成器的种子
        long seed = Long.parseLong(lines[0]);
        Random random = new Random(seed);

        // 获取原先avatar的位置
        String[] avatarPosition = lines[1].split(" ");
        int avatarX = Integer.parseInt(avatarPosition[0]);
        int avatarY = Integer.parseInt(avatarPosition[1]);

        // 恢复世界
        World world = new World(random);
        world.setAvatarPosition(avatarX, avatarY);

        return world;
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
