/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package itm.comlang.teamproject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Kapeu
 */
public class Room {

    private String[][] grid;
    private int rows;
    private int cols;
    private String fileName;

    // -------------------------------------------------------
    // 생성자: CSV 파일 읽어서 그리드 생성
    // -------------------------------------------------------
    public Room(String fileName) throws IOException {
        this.fileName = fileName;
        loadFromCSV(fileName);
    }

    private void loadFromCSV(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        // 첫 줄: 행, 열 수 (헤더)
        String header = reader.readLine();
        if (header == null) {
            throw new IOException("Missing header line in: " + fileName);
        }

        String[] dimensions = header.trim().split(",");
        this.rows = Integer.parseInt(dimensions[0].trim());
        this.cols = Integer.parseInt(dimensions[1].trim());
        this.grid = new String[rows][cols];

        // 나머지 줄: 셀 내용
        for (int r = 0; r < rows; r++) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Missing row " + r + " in: " + fileName);
            }
            String[] cells = line.split(",", -1);
            for (int c = 0; c < cols; c++) {
                grid[r][c] = (c < cells.length) ? cells[c].trim() : " ";
            }
        }

        reader.close();
    }

    // -------------------------------------------------------
    // 방 상태를 파일에 저장
    // -------------------------------------------------------
    public void saveToCSV(String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(rows + "," + cols);
        writer.newLine();
        for (int r = 0; r < rows; r++) {
            StringBuilder line = new StringBuilder();
            for (int c = 0; c < cols; c++) {
                line.append(grid[r][c]);
                if (c < cols - 1) line.append(",");
            }
            writer.write(line.toString());
            writer.newLine();
        }
        writer.close();
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

        // 빈 공간이면 이동
        if (grid[newRow][newCol].equals("") || grid[newRow][newCol].equals(" ")) {
            grid[oldRow][oldCol] = " ";
            grid[newRow][newCol] = "@";
        } else {
            // 빈 공간 아니면 되돌리기 (도어/아이템/몬스터는 main에서 처리)
            hero.setLocation(oldRow, oldCol);
        }
    }

    // -------------------------------------------------------
    // 방 안의 도어 목록 반환
    // -------------------------------------------------------
    public ArrayList<Door> getDoors() {
        ArrayList<Door> doors = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c].startsWith("d:")) {
                    doors.add(new Door(r, c, grid[r][c].substring(2), false));
                } else if (grid[r][c].equals("D")) {
                    doors.add(new Door(r, c, null, true));
                }
            }
        }
        return doors;
    }

    // -------------------------------------------------------
    // 히어로 시작 위치 찾기
    // 1순위: @ / 2순위: [1][1] / 3순위: 랜덤 빈 공간
    // -------------------------------------------------------
    public int[] findHeroStart() {
        // 1순위: CSV에 @ 있으면 그 위치
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (grid[r][c].equals("@")) return new int[]{r, c};

        // 2순위: [1][1]이 비어있으면
        if (rows > 1 && cols > 1 && grid[1][1].equals(" "))
            return new int[]{1, 1};

        // 3순위: 랜덤 빈 공간
        ArrayList<int[]> empty = new ArrayList<>();
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (grid[r][c].equals(" ")) empty.add(new int[]{r, c});

        if (!empty.isEmpty()) {
            java.util.Random rand = new java.util.Random();
            return empty.get(rand.nextInt(empty.size()));
        }

        return new int[]{1, 1};
    }

    // -------------------------------------------------------
    // 방 출력 (ASCII 벽 + 그리드)
    // -------------------------------------------------------
    public void printRoom() {
        // 상단 벽
        System.out.print("+");
        for (int c = 0; c < cols; c++) System.out.print("-");
        System.out.println("+");

        // 그리드
        for (int r = 0; r < rows; r++) {
            System.out.print("|");
            for (int c = 0; c < cols; c++) {
                System.out.print(getCellDisplay(grid[r][c]));
            }
            System.out.println("|");
        }

        // 하단 벽
        System.out.print("+");
        for (int c = 0; c < cols; c++) System.out.print("-");
        System.out.println("+");
    }

    // 셀 표시 문자 변환 (d:room2.csv → d 등)
    private String getCellDisplay(String cell) {
        if (cell.startsWith("d:"))      return "d";
        if (cell.startsWith("G:"))      return "G";
        if (cell.startsWith("O:"))      return "O";
        if (cell.startsWith("T:"))      return "T";
        if (cell.isEmpty())             return " ";
        return String.valueOf(cell.charAt(0));
    }

    // -------------------------------------------------------
    // 셀 값 읽기 / 쓰기
    // -------------------------------------------------------
    public String getCell(int row, int col) { return grid[row][col]; }
    public void setCell(int row, int col, String value) { grid[row][col] = value; }

    // -------------------------------------------------------
    // Getters
    // -------------------------------------------------------
    public int getRows()        { return rows; }
    public int getCols()        { return cols; }
    public String getFileName() { return fileName; }
}