package core;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Main game = new Main();
        game.startGame();
    }

    public void startGame() {
        // 初始化渲染器，设置世界大小
        TERenderer ter = new TERenderer();
        ter.initialize(80, 30);

        // 创建一个新的世界
        Random random = new Random(12347);
        World world = new World(random);

        // 渲染世界
        TETile[][] worldTiles = world.getWorld();
        ter.renderFrame(worldTiles);

        processInput(world, ter);
    }

    // 处理输入
    public void processInput(World world, TERenderer ter) {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                world.moveAvatar(key);
                ter.renderFrame(world.getWorld());
            }
        }
    }
}
