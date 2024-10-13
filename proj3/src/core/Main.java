package core;
import edu.princeton.cs.algs4.StdDraw;
import org.apache.bcel.generic.IF_ACMPEQ;
import org.apache.bcel.generic.NEW;
import tileengine.TERenderer;
import tileengine.TETile;

import javax.management.relation.RelationNotification;
import java.awt.*;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Main game = new Main();
        game.showMainMenu();
    }

    // 显示主菜单
    public void showMainMenu() {
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, 80);
        StdDraw.setYscale(0, 80);

        drawMainMenu();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 'N' || key == 'n') {
                    enterSeed();  // 开始新游戏
                } else if (key == 'L' || key == 'l') {
                    loadGame();  // 加载游戏
                } else if (key == 'Q' || key == 'q') {
                    System.exit(0);  // 退出游戏
                }
            }
        }
    }

    // 绘制主菜单
    private void drawMainMenu() {
        StdDraw.clear(StdDraw.BLACK);
        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(StdDraw.WHITE);

        // 保留主菜单文本
        StdDraw.text(40, 60, "CS61B: THE GAME");
        StdDraw.text(40, 40, "New Game (N)");
        StdDraw.text(40, 35, "Load Game (L)");
        StdDraw.text(40, 30, "Quit (Q)");
        StdDraw.show();
    }

    private void saveGame(World world, Random random) {

    }

    private void loadGame() {
    }

    // 处理种子输入并在菜单上动态显示
    private void enterSeed() {
        StringBuilder seed = new StringBuilder();  // 存储输入的种子
        boolean seedEntered = false;

        // 保持原有的菜单显示并更新种子输入提示
        drawMainMenu();

        Font smallFont = new Font("Arial", Font.BOLD, 20);
        StdDraw.setFont(smallFont);
        StdDraw.text(40, 15, "Press S to start");
        StdDraw.text(40, 20, "Enter Seed:");
        StdDraw.show();

        while (!seedEntered) {
            // 等待用户输入数字或按下S键
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();

                if (Character.isDigit(key)) {
                    // 如果输入是数字，则将数字加入种子
                    seed.append(key);

                    // 清除旧的种子和提示信息
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.filledRectangle(40, 20, 30, 2);  // 覆盖掉之前的显示区域

                    // 动态显示 "Enter Seed" 和当前输入的种子
                    StdDraw.setPenColor(StdDraw.WHITE);
                    StdDraw.text(40, 20, "Enter Seed: " + seed.toString());  // 显示新的种子值
                    StdDraw.show();
                } else if (key == 'S' || key == 's') {
                    // 按下S开始游戏
                    if (!seed.isEmpty()) {  // 确保种子不为空
                        seedEntered = true;
                        long seedValue = Long.parseLong(seed.toString());
                        startNewGame(seedValue);  // 使用输入的种子启动游戏
                    }
                }
            }
        }
    }

    private void startNewGame(long seedValue) {
        // 初始化渲染器，设置世界大小
        TERenderer ter = new TERenderer();
        ter.initialize(80, 30);

        // 创建一个新的世界
        Random random = new Random(seedValue);
        World world = new World(random);

        // 渲染世界
        TETile[][] worldTiles = world.getWorld();
        ter.renderFrame(worldTiles);

        // 操作avatar
        processInput(world, ter, random);
    }

    // 处理输入，显示tile类型
    private void processInput(World world, TERenderer ter, Random random) {
        StringBuilder command = new StringBuilder();  // 用于存储所有用户的输入

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                command.append(key); // 将每个输入的字符追加到命令字符串中
//                System.out.println(command.toString());

                // 处理命令输入
                // 检查输入的最后两个字符是否是 ":Q" 或 ":q"
                if (command.length() >= 2 && (command.substring(command.length() - 2).equals(":q") || command.substring(command.length() - 2).equals(":Q"))) {
                    saveGame(world, random);
                    System.exit(0);
                } else {
                    world.moveAvatar(key);
                }
            }

            ter.renderFrame(world.getWorld());

            // 获取鼠标所在位置
            int mouseX = (int) StdDraw.mouseX();
            int mouseY = (int) StdDraw.mouseY();

            if (mouseX >= 0 && mouseX < world.getWidth() && mouseY >=0 && mouseY < world.getHeight()) {
                // 获取鼠标所在tile的类型
                String tileType = world.getWorld()[mouseX][mouseY].description();

                // 在左上角显示鼠标所在的Tile类型
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.textLeft(0, world.getHeight() - 1, "Tile: " + tileType);
            }

            // 显示更新
            StdDraw.show();

            // 控制帧率
            StdDraw.pause(1000 / 60); // 每秒60帧
        }
    }
}
