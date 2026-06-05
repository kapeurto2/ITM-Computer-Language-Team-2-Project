package itm.comlang.teamproject;

/**
 *
 * @author Kapeu
 */

public abstract class Entity {


    private int row;
    private int col;
    
    public Entity(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return this.row; }
    public int getCol() { return this.col; }

    public void setLocation(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public abstract String getSymbol();
    public abstract void onDelete(Room room);
    @Override
    public String toString() {
        return getSymbol();
    }
}