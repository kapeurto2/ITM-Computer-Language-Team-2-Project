/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author 오갱
 */
public class Troll extends Monster {
 
    public Troll(int row, int col) {
        super(row, col, 15, 4);
    }
 
    // 처치 시 자기 자리에 키를 떨어뜨린 뒤 방에서 제거된다
    @Override
    public void onDelete(Room room) {
        onDrop(room);
        super.onDelete(room);   // Monster 의 기본 제거 동작 재사용
    }
 
    public void onDrop(Room room) {
        room.addEntity(new Key(this.getRow(), this.getCol()));
    }
 
    @Override
    public String getSymbol() {
        return "T";
    }
}
