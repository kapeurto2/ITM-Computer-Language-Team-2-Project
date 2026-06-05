/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author 오갱
 */
public class Stick extends Item implements Iweapon{

    private int damage;
    private String name;
    
    public Stick(int row, int col) {
        super(row, col);
        this.damage = 1;
        this.name = "Stick";
   
    }
    //성민애몽
    @Override
    public void onDelete(Room room) {
        room.removeEntity(this);
    }
    @Override
    public void onInteract(Hero hero, Room room) {
        hero.setWeapon(this);
        room.removeEntity(this);  
    }
    @Override
    public int getDamage() {
        return this.damage;
    }
    @Override
    public String getName() {
        return this.name;
    }
    @Override
    public String getSymbol() {
        return "S";
    }
    
}
