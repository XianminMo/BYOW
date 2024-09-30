package utils;

public class Room {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // 左下角位置
    public int[] getPosition() {
        return new int[] {x, y};
    }

    // Size(Width, Height)
    public int[] getSize() {
        return new int[] {width, height};
    }

    // 中心点
    public int[] getCenter() {
        return new int[]{x + width / 2, y + height / 2};
    }
}

