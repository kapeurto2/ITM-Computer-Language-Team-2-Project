/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author Kapeu
 */
public class Door {
 
    private int row;
    private int col;
    private String targetFile;  // 연결된 방 CSV 파일명 (마스터 도어면 null)
    private boolean isMaster;   // 마스터 도어 여부
 
    public Door(int row, int col, String targetFile, boolean isMaster) {
        this.row = row;
        this.col = col;
        this.targetFile = targetFile;
        this.isMaster = isMaster;
    }
 
    // 히어로가 이 도어 위치에 있는지 확인
    public boolean isHeroHere(int heroRow, int heroCol) {
        return this.row == heroRow && this.col == heroCol;
    }
 
    // 도어 통과 시도
    // 반환값: 이동할 방 파일명 / 마스터 도어 승리 시 "WIN" / 잠겨있으면 null
    public String enter(boolean heroHasKey) {
        if (isMaster) {
            if (heroHasKey) {
                return "WIN";
            } else {
                return null;
            }
        }
        return targetFile;
    }
 
    // 새 방 입장 시 위치 결정 (나간 벽 반대편에서 등장)
    public static int[] getEntryPosition(Room newRoom, Room oldRoom, Door usedDoor) {
        int oldRow = usedDoor.getRow();
        int oldCol = usedDoor.getCol();
        int newRows = newRoom.getRows();
        int newCols = newRoom.getCols();
 
        if (oldRow == 0)                     return new int[]{newRows - 1, newCols / 2};
        if (oldRow == oldRoom.getRows() - 1) return new int[]{0, newCols / 2};
        if (oldCol == 0)                     return new int[]{newRows / 2, newCols - 1};
        if (oldCol == oldRoom.getCols() - 1) return new int[]{newRows / 2, 0};
 
        return new int[]{1, 1};
    }
 
    public int getRow()           { return row; }
    public int getCol()           { return col; }
    public String getTargetFile() { return targetFile; }
    public boolean isMaster()     { return isMaster; }
}
