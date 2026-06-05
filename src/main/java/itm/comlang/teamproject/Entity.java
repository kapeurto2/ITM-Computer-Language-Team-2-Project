package itm.comlang.teamproject;

/**
 *
 * @author Kapeu
 */

public class Entity {

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

    /**
     * 출력용 한 글자 심볼.
     * "d:room2.csv" -> "d", "G:goblin" -> "G", "@" -> "@"
     */
    public String getSymbol() {
        if (this.type == null || this.type.isEmpty()) {
            return " ";
        }
        return String.valueOf(this.type.charAt(0));
    }

    @Override
    public String toString() {
        return getSymbol();
    }
}