package itm.comlang.teamproject;

/**
 *
 * @author Kapeu
 */

public abstract class Entity {

    private int row;
    private int col;
    private String type;   // 원본 셀 값 (예: "@", "d:room2.csv", "G:goblin")

    public Entity(int row, int col, String type) {
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public int getRow() { return this.row; }
    public int getCol() { return this.col; }

    public void setLocation(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public String getType() { return this.type; }
    public void setType(String type) { this.type = type; }
    public abstract String getSymbol();
    public abstract void onDelete();
    @Override
    public String toString() {
        return getSymbol();
    }
}