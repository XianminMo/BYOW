package core;
import tileengine.TERenderer;
import tileengine.TETile;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // 初始化渲染器，设置世界大小
        TERenderer ter = new TERenderer();
        ter.initialize(80, 30);

        // 创建一个新的世界
        World world = new World();

        // 添加一个随机房间
        Random random = new Random(12346);
        world.generateRooms(random);

        // 渲染世界
        TETile[][] worldTiles = world.getWorld();
        ter.renderFrame(worldTiles);
    }
}
