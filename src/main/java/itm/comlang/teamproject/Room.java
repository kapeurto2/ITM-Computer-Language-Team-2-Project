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

    private ArrayList<Entity> entities;  // 핵심 자료구조 (source of truth)
    private String[][] grid;             // entities 로부터 갱신되는 맵 (출력/저장용)
    private int rows;
    private int cols;
    private String fileName;

    // -------------------------------------------------------
    // 생성자: CSV 파일을 읽어 엔티티 목록과 맵을 만든다
    // -------------------------------------------------------
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

    // -------------------------------------------------------
    // 셀 문자열 -> 알맞은 Entity 구체 클래스 생성 
    // -------------------------------------------------------
    private Entity createEntity(int row, int col, String value) {
        if (value == null || value.isEmpty() || value.equals(" ")) {
            return null;                                  
        }

        if (value.equals("@")) return new Hero(row, col);
        //if (value.equals("S")) return new Stick(col, row);        
        //if (value.equals("W")) return new WeakSword(col, row);    
        //if (value.equals("X")) return new StrongSword(col, row);  
        //if (value.equals("m")) return new MinorFlask(col, row, "m");  
        //if (value.equals("B")) return new BigFlask(col, row, "B");    
        if (value.equals("D")) return new Door(row, col, null, true);
        //if (value.equals("*")) return new Key(row, col);

        if (value.contains(":")) {
            String[] p = value.split(":", 2);
            String head = p[0];
            String data = p[1];

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

        return null;  // 알 수 없는 심볼은 무시
    }

    // -------------------------------------------------------
    // entities -> grid 갱신 (리스트가 바뀔 때마다 호출)
    // -------------------------------------------------------
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
        refreshMap();
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println(rows + "," + cols);
            for (int r = 0; r < rows; r++) {
                StringBuilder line = new StringBuilder();
                for (int c = 0; c < cols; c++) {
                    line.append(grid[r][c]);
                    if (c < cols - 1) {
                        line.append(",");
                    }
                }
                writer.println(line.toString());
            }
        }
    }

    // -------------------------------------------------------
    // 엔티티 조회 / 추가 / 삭제 (변경 시 맵 갱신)
    // -------------------------------------------------------
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

    // -------------------------------------------------------
    // 방 안의 도어 목록 반환 (instanceof 로 깔끔하게)
    // -------------------------------------------------------
    public ArrayList<Door> getDoors() {
        ArrayList<Door> doors = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof Door) {
                doors.add((Door) e);
            }
        }
        return doors;
    }

    // -------------------------------------------------------
    // 히어로 시작 위치 찾기
    // 1순위: @(Hero) / 2순위: [1][1] / 3순위: 랜덤 빈 공간
    // -------------------------------------------------------
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

    // -------------------------------------------------------
    // 방 출력 (ASCII 벽 + grid 직접 읽기: 칸당 O(1))
    // -------------------------------------------------------
    public void printRoom() {
        refreshMap();  // grid 최신화

        // 상단 벽
        System.out.print("+");
        for (int c = 0; c < cols; c++) System.out.print("-");
        System.out.println("+");

        // 그리드
        for (int r = 0; r < rows; r++) {
            System.out.print("|");
            for (int c = 0; c < cols; c++) {
                System.out.print(grid[r][c]);
            }
            System.out.println("|");
        }

        // 하단 벽
        System.out.print("+");
        for (int c = 0; c < cols; c++) System.out.print("-");
        System.out.println("+");
    }

    // -------------------------------------------------------
    // 셀 값 읽기 / 쓰기 (쓰기 시 리스트 변경 -> 맵 갱신)
    // -------------------------------------------------------
    public String getCell(int row, int col) {
        Entity e = getEntityAt(row, col);
        return (e == null) ? " " : e.getSymbol();
    }

    public void setCell(int row, int col, String value) {
        Entity existing = getEntityAt(row, col);
        if (existing != null) {
            entities.remove(existing);            // 기존 엔티티 제거
        }
        Entity e = createEntity(row, col, value); // 새 값이 있으면 팩토리로 생성
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
 
