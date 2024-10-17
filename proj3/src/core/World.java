package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;
import utils.Edge;
import utils.Room;
import utils.UnionFind;

import java.util.*;

public class World {
    /*
        世界显示的时候以左下角为坐标原点，横轴为x轴，纵轴为y轴，在数组中行代表横轴，列代表纵轴，原点在左上角，与显示画面有一定出入，需要注意！！！
    */
    private static final int WIDTH = 80;  // 定义世界宽度
    private static final int HEIGHT = 30; // 定义世界高度
    private final TETile[][] world;  // 世界的二维网格
    private final List<Room> rooms = new ArrayList<>(); // 房间
    private int avatarX; // 记录avatar的坐标
    private int avatarY;

    public World(Random random) {
        world = new TETile[WIDTH][HEIGHT];  // 创建网格
        initializeWorld(random);  // 初始化世界
    }

    // 初始化世界，将所有网格设置为空地，初始化avatar，生成房间
    private void initializeWorld(Random random) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;  // 将每个格子初始化
            }
        }

        // 生成房间
        generateRooms(random);

        // 初始化avatar
        placeAvatar(random);
    }

    // 初始化avatar
    private void placeAvatar(Random random) {
        boolean placed = false;
        while (!placed) {
            avatarX = RandomUtils.uniform(random, 0, WIDTH);
            avatarY = RandomUtils.uniform(random, 0, HEIGHT);
            if (world[avatarX][avatarY].equals(Tileset.FLOOR)) {
                world[avatarX][avatarY] = Tileset.AVATAR; // 将avatar放在初始位置
                placed = true;
            }
        }
    }

    // 设置avatar位置，在加载存档游戏时使用
    public void setAvatarPosition(int avatarX, int avatarY) {
        clearAvatar();
        this.avatarX = avatarX;
        this.avatarY = avatarY;
        world[avatarX][avatarY] = Tileset.AVATAR;
    }

    // 在设置avatar位置时，将随机生成的avatar先清除
    private void clearAvatar() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y].equals(Tileset.AVATAR)) {
                    world[x][y] = Tileset.FLOOR;
                }
            }
        }
    }

    // 根据键盘输入移动avatar
    public void moveAvatar(char direction) {
        int newX = avatarX;
        int newY = avatarY;

        // 不区分大小写，将输入字符统一转换为小写
        direction = Character.toLowerCase(direction);

        switch (direction) {
            case 'w': newY += 1; break;
            case 'a': newX -= 1; break;
            case 's': newY -= 1; break;
            case 'd': newX += 1; break;
        }

        // 检查是否为FLOOR
        if (world[newX][newY] == Tileset.FLOOR) {
            world[avatarX][avatarY] = Tileset.FLOOR; // 重置之前位置
            avatarX = newX;
            avatarY = newY;
            world[avatarX][avatarY] = Tileset.AVATAR; // 更新avatar位置
        }
    }


    // 生成多个房间并使用最小生成树连接
    private void generateRooms(Random random) {
        double fillRatio = 0.0;
        while (fillRatio < 0.5) {
            addRandomRoom(random);  // 随机生成房间
            fillRatio = calculateFillRatio();  // 计算填充率
        }
        connectRoomsUsingMST(random);  // 连接房间，使用最小生成树
    }

    // 计算当前世界的填充率
    private double calculateFillRatio() {
        int usedTiles = 0;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] != Tileset.NOTHING) {
                    usedTiles++;
                }
            }
        }
        return (double) usedTiles / (WIDTH * HEIGHT);
    }

    // 检查两个房间是否重叠
    private boolean isOverlapping(Room room1, Room room2) {
        int x1 = room1.getPosition()[0];
        int y1 = room1.getPosition()[1];
        int x2 = room2.getPosition()[0];
        int y2 = room2.getPosition()[1];
        int width1 = room1.getSize()[0];
        int height1 = room1.getSize()[1];
        int width2 = room2.getSize()[0];
        int height2 = room1.getSize()[1];

        return x1 < x2 + width2 &&
                x1 + width1 > x2 &&
                y1 < y2 + height2 &&
                y1 + height1 > y2;
    }

    // 随机生成房间并添加到世界
    private void addRandomRoom(Random random) {
        int x = 0;
        int y = 0;
        int roomWidth = 0;
        int roomHeight = 0;
        Room newRoom = null;

        boolean validRoom = false;
        while (!validRoom) {
            roomWidth = RandomUtils.uniform(random, 3, 15);  // 随机宽度
            roomHeight = RandomUtils.uniform(random, 3, 12); // 随机高度

            // 预留边界给墙
            x = RandomUtils.uniform(random, 1, WIDTH - roomWidth - 1); // 随机X位置
            y = RandomUtils.uniform(random, 1, HEIGHT - roomHeight - 1); // 随机Y位置

            // 创建房间类，添加到房间集合
            newRoom = new Room(x, y, roomWidth, roomHeight);
            validRoom = true;

            // 检查与已有房间是否重叠
            for (Room existingRoom : rooms) {
                if (isOverlapping(newRoom, existingRoom)) {
                    validRoom = false;  // 如果重叠，重新生成房间
                    break;
                }
            }
        }
        rooms.add(newRoom);

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
    private void connectRoomsUsingMST(Random random) {
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
                connectTwoRooms(room1, room2, random);
            }
        }
    }

    // 连接两个房间
    private void connectTwoRooms(Room room1, Room room2, Random random) {
        // 随机选择房间的连接点，可以是角点、中心或边界中点
        int[] room1Pos = chooseRandomPosition(room1, random);
        int[] room2Pos = chooseRandomPosition(room2, random);

        int startX = room1Pos[0];
        int startY = room1Pos[1];
        int endX = room2Pos[0];
        int endY = room2Pos[1];

        for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
            world[x][startY] = Tileset.FLOOR;
            addWallsAround(x, startY);
        }
        for (int y = Math.min(startY, endY); y <= Math.max(startY, endY); y++) {
            world[endX][y] = Tileset.FLOOR;
            addWallsAround(endX, y);
        }
    }

    // 在地板周围添加墙壁
    private void addWallsAround(int x, int y) {
        if (world[x][y - 1] == Tileset.NOTHING) world[x][y - 1] = Tileset.WALL;
        if (world[x][y + 1] == Tileset.NOTHING) world[x][y + 1] = Tileset.WALL;
        if (world[x - 1][y] == Tileset.NOTHING) world[x - 1][y] = Tileset.WALL;
        if (world[x + 1][y] == Tileset.NOTHING) world[x + 1][y] = Tileset.WALL;
    }

    private int[] chooseRandomPosition(Room room, Random random) {
        int x1 = room.getPosition()[0];
        int y1 = room.getPosition()[1];
        int x2 = x1 + room.getSize()[0] - 1;
        int y2 = y1 + room.getSize()[1] - 1;

        int[][] positions = {
                {x1, y1},                    // 左下角
                {x2, y1},                    // 右下角
                {x1, y2},                    // 左上角
                {x2, y2},                    // 右上角
                room.getCenter(),             // 中心
                {(x1 + x2) / 2, y1},          // 下边中点
                {(x1 + x2) / 2, y2},          // 上边中点
                {x1, (y1 + y2) / 2},          // 左边中点
                {x2, (y1 + y2) / 2}           // 右边中点
        };
        return positions[random.nextInt(positions.length)];
    }


    // 返回世界
    public TETile[][] getWorld() {
        return world;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public int getAvatarX() {
        return avatarX;
    }

    public int getAvatarY() {
        return avatarY;
    }
}
