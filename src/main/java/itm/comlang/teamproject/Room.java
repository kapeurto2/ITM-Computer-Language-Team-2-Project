package itm.comlang.teamproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Kapeu
 */

public class Room {

    private ArrayList<Entity> entities; 
    private String[][] grid;             
    private int rows;
    private int cols;
    private String fileName;

    public Room(String fileName) throws IOException {
        this.fileName = fileName;
        this.entities = new ArrayList<>();
        loadFromCSV(fileName);
        refreshMap();
    }

    // -------------------------------------------------------
    // 파일 읽기 (Scanner + Paths)
    // -------------------------------------------------------
    private void loadFromCSV(String fileName) throws IOException {
        try (Scanner scanner = new Scanner(Paths.get(fileName))) {

            // 첫 줄: 행, 열 수 (헤더)
            String header = scanner.nextLine();
            String[] dimensions = header.trim().split(",");
            this.rows = Integer.parseInt(dimensions[0].trim());
            this.cols = Integer.parseInt(dimensions[1].trim());

            // 나머지 줄: 셀을 읽어 알맞은 Entity 로 만들어 리스트에 추가
            for (int r = 0; r < rows; r++) {
                if (!scanner.hasNextLine()) {
                    break;
                }
                String[] cells = scanner.nextLine().split(",", -1);
                for (int c = 0; c < cols; c++) {
                    String value = (c < cells.length) ? cells[c].trim() : "";
                    Entity e = createEntity(r, c, value);
                    if (e != null) {
                        entities.add(e);
                    }
                }
            }
        }
    }
    private Entity createEntity(int row, int col, String value) {
        if (value == null || value.isEmpty() || value.equals(" ")) {
            return null;
        }
        if (value.equals("@")) return new Hero(row, col);
        if (value.equals("S")) return new Stick(row, col);
        if (value.equals("W")) return new WeakSword(row, col);
        if (value.equals("X")) return new StrongSword(row, col);
        if (value.equals("m")) return new MinorFlask(row, col);
        if (value.equals("B")) return new BigFlask(row, col);
        if (value.equals("*")) return new Key(row, col);          
        if (value.equals("D")) return new Door(row, col, null, true);

        if (value.equals("G")) return new Goblin(row, col);
        if (value.equals("O")) return new Orc(row, col);
        if (value.equals("T")) return new Troll(row, col);

        if (value.contains(":")) {
            String[] p = value.split(":", 2);
            String head = p[0].trim();
            String data = p[1].trim();

            switch (head) {
                case "d":
                    return new Door(row, col, data, false);
                case "G": {
                    Goblin g = new Goblin(row, col);
                    g.setHealth(Integer.parseInt(data));
                    return g;
                }
                case "O": {
                    Orc o = new Orc(row, col);
                    o.setHealth(Integer.parseInt(data));
                    return o;
                }
                case "T": {
                    Troll t = new Troll(row, col);
                    t.setHealth(Integer.parseInt(data));
                    return t;
                }
            }
        }

        return null;
    }
        

    public void refreshMap() {
        this.grid = new String[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = " ";
            }
        }
        for (Entity e : entities) {
            grid[e.getRow()][e.getCol()] = e.getSymbol();
        }
    }

    public void saveToCSV(String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println(rows + "," + cols);
            for (int r = 0; r < rows; r++) {
                StringBuilder line = new StringBuilder();
                for (int c = 0; c < cols; c++) {
                    line.append(cellToCsv(getEntityAt(r, c)));
                    if (c < cols - 1) {
                        line.append(",");
                    }
                }
                writer.println(line.toString());
            }
        }
    }

    private String cellToCsv(Entity e) {
        if (e == null) {
            return " ";
        }
        if (e instanceof Hero) {
            return " ";
        }
        if (e instanceof Door) {
            Door d = (Door) e;
            return d.isMaster() ? "D" : "d:" + d.getTargetRoom();
        }
        if (e instanceof Goblin) {
            return "G:" + ((Goblin) e).getHealth();
        }
        if (e instanceof Orc) {
            return "O:" + ((Orc) e).getHealth();
        }
        if (e instanceof Troll) {
            return "T:" + ((Troll) e).getHealth();
        }
        return e.getSymbol();   // 무기 S/W/X, 포션 m/B, 키 *
    }

    public Entity getEntityAt(int row, int col) {
        for (Entity e : entities) {
            if (e.getRow() == row && e.getCol() == col) {
                return e;
            }
        }
        return null;
    }

    public void addEntity(Entity e) {
        entities.add(e);
        refreshMap();
    }

    public void removeEntity(Entity e) {
        entities.remove(e);
        refreshMap();
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public ArrayList<Door> getDoors() {
        ArrayList<Door> doors = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof Door) {
                doors.add((Door) e);
            }
        }
        return doors;
    }

    public int[] findHeroStart() {
        for (Entity e : entities) {
            if (e instanceof Hero) {
                return new int[]{e.getRow(), e.getCol()};
            }
        }

        if (rows > 1 && cols > 1 && getEntityAt(1, 1) == null) {
            return new int[]{1, 1};
        }

        ArrayList<int[]> empty = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (getEntityAt(r, c) == null) {
                    empty.add(new int[]{r, c});
                }
            }
        }

        if (!empty.isEmpty()) {
            Random rand = new Random();
            return empty.get(rand.nextInt(empty.size()));
        }

        return new int[]{1, 1};
    }

    public void printRoom() {
        refreshMap();  
        System.out.print("+");
        for (int c = 0; c < cols; c++) System.out.print("-");
        System.out.println("+");

        for (int r = 0; r < rows; r++) {
            System.out.print("|");
            for (int c = 0; c < cols; c++) {
                System.out.print(grid[r][c]);
            }
            System.out.println("|");
        }

        System.out.print("+");
        for (int c = 0; c < cols; c++) System.out.print("-");
        System.out.println("+");
    }

    public String getCell(int row, int col) {
        Entity e = getEntityAt(row, col);
        return (e == null) ? " " : e.getSymbol();
    }

    public void setCell(int row, int col, String value) {
        Entity existing = getEntityAt(row, col);
        if (existing != null) {
            entities.remove(existing);            
        }
        Entity e = createEntity(row, col, value); 
        if (e != null) {
            entities.add(e);
        }
        refreshMap();
    }

    // -------------------------------------------------------
    // Getters
    // -------------------------------------------------------
    public int getRows()        { return rows; }
    public int getCols()        { return cols; }
    public String getFileName() { return fileName; }
}