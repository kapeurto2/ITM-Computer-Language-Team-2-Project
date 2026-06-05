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

            // 나머지 줄: 빈 칸이 아닌 셀만 Entity 로 만들어 리스트에 추가
            for (int r = 0; r < rows; r++) {
                if (!scanner.hasNextLine()) {
                    break;
                }
                String[] cells = scanner.nextLine().split(",", -1);
                for (int c = 0; c < cols; c++) {
                    String value = (c < cells.length) ? cells[c].trim() : "";
                    if (!value.isEmpty()) {
                        entities.add(new Entity(r, c, value));
                    }
                }
            }
        }
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

    // -------------------------------------------------------
    // 방 상태를 파일에 저장 (PrintWriter)
    // -------------------------------------------------------
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
    // 히어로 이동 처리
    // -------------------------------------------------------
    public void moveObject(Hero hero, String moving) {
        int oldRow = hero.getXLocation();
        int oldCol = hero.getYLocation();

        hero.move(moving);

        int newRow = hero.getXLocation();
        int newCol = hero.getYLocation();

        // 범위 밖이면 되돌리기
        if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) {
            hero.setLocation(oldRow, oldCol);
            return;
        }

        Entity target = getEntityAt(newRow, newCol);

        // 빈 공간이면 이동 (리스트의 히어로 엔티티 위치를 바꾸고 맵 갱신)
        if (target == null) {
            Entity heroEntity = getEntityAt(oldRow, oldCol);
            if (heroEntity != null) {
                heroEntity.setLocation(newRow, newCol);
            } else {
                entities.add(new Entity(newRow, newCol, "@"));
            }
            refreshMap();
        } else {
            // 빈 공간이 아니면 되돌리기 (도어/아이템/몬스터는 main 에서 처리)
            hero.setLocation(oldRow, oldCol);
        }
    }

    // -------------------------------------------------------
    // 방 안의 도어 목록 반환
    // -------------------------------------------------------
    public ArrayList<Door> getDoors() {
        ArrayList<Door> doors = new ArrayList<>();
        for (Entity e : entities) {
            String type = e.getType();
            if (type.startsWith("d:")) {
                doors.add(new Door(e.getRow(), e.getCol(), type.substring(2), false));
            } else if (type.equals("D")) {
                doors.add(new Door(e.getRow(), e.getCol(), null, true));
            }
        }
        return doors;
    }

    // -------------------------------------------------------
    // 히어로 시작 위치 찾기
    // 1순위: @ / 2순위: [1][1] / 3순위: 랜덤 빈 공간
    // -------------------------------------------------------
    public int[] findHeroStart() {
        for (Entity e : entities) {
            if (e.getType().equals("@")) {
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
    // 방 출력 (ASCII 벽 + Entity 타입만 출력)
    // -------------------------------------------------------
    public void printRoom() {
        // 상단 벽
        System.out.print("+");
        for (int c = 0; c < cols; c++) System.out.print("-");
        System.out.println("+");

        // 그리드: 각 칸의 Entity 타입(심볼)만 출력
        for (int r = 0; r < rows; r++) {
            System.out.print("|");
            for (int c = 0; c < cols; c++) {
                Entity e = getEntityAt(r, c);
                System.out.print(e == null ? " " : e.getSymbol());
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
        return (e == null) ? " " : e.getType();
    }

    public void setCell(int row, int col, String value) {
        Entity e = getEntityAt(row, col);
        if (value == null || value.trim().isEmpty()) {
            if (e != null) {
                entities.remove(e);   // 빈 값으로 설정하면 엔티티 제거
            }
        } else if (e != null) {
            e.setType(value);         // 이미 있으면 타입만 변경
        } else {
            entities.add(new Entity(row, col, value));  // 없으면 새로 추가
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