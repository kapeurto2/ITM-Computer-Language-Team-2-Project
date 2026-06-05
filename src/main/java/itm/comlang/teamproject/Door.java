/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author Kapeu
 */
public class Door extends Entity {

    private String targetRoom;   
    private boolean master;      
    public Door(int row, int col, String targetRoom, boolean master) {
        super(row, col, master ? "D" : "d");   
        this.targetRoom = targetRoom;
        this.master = master;
    }

    public String getTargetRoom() { return targetRoom; }
    public boolean isMaster()     { return master; }

    @Override
    public String getSymbol() {
        return master ? "D" : "d";
    }

    @Override
    public void onDelete() {
        // 문은 삭제 시 특별한 동작 없음 (비워둠)
    }
}