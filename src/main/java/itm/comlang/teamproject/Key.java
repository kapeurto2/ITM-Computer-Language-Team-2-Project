/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author Kapeu
 */
public class Key extends Item{
    public Key(int row, int col) {
        super(row, col);
    }

    @Override
    public void onDelete(Room room) {
        room.removeEntity(this);
    }

    @Override
    public void onInteract(Hero hero, Room room) {
        hero.setKey();
        room.removeEntity(this);
    }
    
    @Override
    public String getSymbol() {
        return "*";
    }
    
}
