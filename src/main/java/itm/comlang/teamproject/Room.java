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

    private void loadFromCSV(String fileName) throws IOException {
        try (Scanner scanner = new Scanner(Paths.get(fileName))) {

            String header = scanner.nextLine();
            String[] dimensions = header.trim().split(",");
            this.rows = Integer.parseInt(dimensions[0].trim());
            this.cols = Integer.parseInt(dimensions[1].trim());

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
        // Draw floor objects (items/monsters/doors) first.
        for (Entity e : entities) {
            if (!(e instanceof Hero)) {
                grid[e.getRow()][e.getCol()] = e.getSymbol();
            }
        }
        // Always draw the hero on top, so it is never hidden under a floor object
        // (e.g. standing on a full-HP potion that stays in the room).
        for (Entity e : entities) {
            if (e instanceof Hero) {
                grid[e.getRow()][e.getCol()] = e.getSymbol();
            }
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

    // Returns the "floor object" (excluding the hero) at this cell, or null.
    // A cell may hold both the hero and a floor object at once (e.g. the hero
    // standing on a full-HP potion), so use this when querying floor objects only.
    public Entity getFloorEntityAt(int row, int col) {
        for (Entity e : entities) {
            if (!(e instanceof Hero) && e.getRow() == row && e.getCol() == col) {
                return e;
            }
        }
        return null;
    }

    // Finds an empty cell to drop an item at (row,col).
    // If that cell already holds a floor object, returns the nearest empty cell
    // instead (prevents dropped items from stacking and hiding under each other).
    public int[] findDropCell(int row, int col) {
        if (getFloorEntityAt(row, col) == null) {
            return new int[]{row, col};
        }
        int maxRadius = Math.max(rows, cols);
        for (int radius = 1; radius < maxRadius; radius++) {
            for (int dr = -radius; dr <= radius; dr++) {
                for (int dc = -radius; dc <= radius; dc++) {
                    int r = row + dr;
                    int c = col + dc;
                    if (r >= 0 && r < rows && c >= 0 && c < cols
                            && getFloorEntityAt(r, c) == null) {
                        return new int[]{r, c};
                    }
                }
            }
        }
        return new int[]{row, col};   // no empty cell: fall back to the original
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

    // Getters
    public int getRows()        { return rows; }
    public int getCols()        { return cols; }
    public String getFileName() { return fileName; }
}