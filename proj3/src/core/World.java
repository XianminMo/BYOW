package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;
import utils.Edge;
import utils.Room;
import utils.UnionFind;

import java.util.*;

public class World {
    private static final int WIDTH = 80;  // 定义世界宽度
    private static final int HEIGHT = 30; // 定义世界高度
    private final TETile[][] world;  // 世界的二维网格
    private List<Room> rooms = new ArrayList<>(); // 房间

    public World() {
        world = new TETile[WIDTH][HEIGHT];  // 创建网格
        initializeWorld();  // 初始化世界
    }

    // 初始化世界，将所有网格设置为空地
    private void initializeWorld() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;  // 将每个格子初始化
            }
        }
    }

    // 生成多个房间
    public void generateRooms(Random random, int roomCount) {
        for (int i = 0; i < roomCount; i++) {
            addRandomRoom(random); // 随机生成房间
        }
        connectRoomsUsingMST();  // 连接房间
    }

    // 随机生成房间并添加到世界
    public void addRandomRoom(Random random) {
        int roomWidth = RandomUtils.uniform(random, 4, 10);  // 随机宽度
        int roomHeight = RandomUtils.uniform(random, 4, 10); // 随机高度

        // 预留边界给墙
        int x = RandomUtils.uniform(random, 1, WIDTH - roomWidth - 1); // 随机X位置
        int y = RandomUtils.uniform(random, 1, HEIGHT - roomHeight - 1); // 随机Y位置

        // 创建房间类，添加到房间集合
        Room room = new Room(x, y, roomWidth, roomHeight);
        rooms.add(room);

        // 用地板填充房间
        for (int i = x; i < x + roomWidth; i++) {
            for (int j = y; j < y + roomHeight; j++) {
                world[i][j] = Tileset.FLOOR;
            }
        }

        // 添加墙壁
        for (int i = x - 1; i <= x + roomWidth; i++) {
            world[i][y - 1] = Tileset.WALL;
            world[i][y + roomHeight] = Tileset.WALL;
        }
        for (int j = y - 1; j <= y + roomHeight; j++) {
            world[x - 1][j] = Tileset.WALL;
            world[x + roomWidth][j] = Tileset.WALL;
        }
    }


    // 使用最小生成树算法连接房间 -- Kruskal
    private void connectRoomsUsingMST() {
        PriorityQueue<Edge> edges = new PriorityQueue<>(Comparator.comparingInt(e -> e.distance));
        for (int i = 0; i < rooms.size(); i++) {
            for (int j = i + 1; j < rooms.size(); j++) {
                int[] room1 = rooms.get(i).getCenter();
                int[] room2 = rooms.get(j).getCenter();
                int distance = Math.abs(room1[0] - room2[0]) + Math.abs(room1[1] - room2[1]);
                edges.add(new Edge(i, j, distance));
            }
        }
        UnionFind uf = new UnionFind(rooms.size());
        while (!edges.isEmpty() && uf.getCount() > 1) {
            Edge edge = edges.poll();

            // Edge中的room1和room2仅为int标识，并非真正的room类
            // union返回false说明room1和room2已经在同一集合中，若不在，则连接并返回true
            if (uf.union(edge.room1, edge.room2)) {
                Room room1 = rooms.get(edge.room1);
                Room room2 = rooms.get(edge.room2);
                connectTwoRooms(room1, room2);
            }
        }
    }

    // 连接两个房间的随机边缘
    private void connectTwoRooms(Room room1, Room room2) {
        int startX = room1.getCenter()[0];
        int startY = room1.getCenter()[1];
        int endX = room2.getCenter()[0];
        int endY = room2.getCenter()[1];

        // 连接X轴
        for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
            world[x][startY] = Tileset.FLOOR;
            if (world[x][startY - 1] == Tileset.NOTHING) world[x][startY - 1] = Tileset.WALL;
            if (world[x][startY + 1] == Tileset.NOTHING) world[x][startY + 1] = Tileset.WALL;
        }

        // 连接Y轴
        for (int y = Math.min(startY, endY); y <= Math.max(startY, endY); y++) {
            world[endX][y] = Tileset.FLOOR;
            if (world[endX - 1][y] == Tileset.NOTHING) world[endX - 1][y] = Tileset.WALL;
            if (world[endX + 1][y] == Tileset.NOTHING) world[endX + 1][y] = Tileset.WALL;
        }
    }


    // 返回世界
    public TETile[][] getWorld() {
        return world;
    }
}
