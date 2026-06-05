/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author 오갱
 */
public class WeakSword extends Item implements Iweapon{
    private int damage;

    
    public WeakSword(int row, int col) {
        super(row, col, "W");
        this.damage = 2;
   
    }
    //성민애몽
    public void onDelete() {
        
    }
    //성민애몽
    @Override
    public void interact(Hero hero) {
        
    }
    
    @Override
    public void setWeapon(Hero hero) {
        hero.setWeapon(this);
        
        
    }
    @Override
    public int getDamage() {
        return this.damage;
    }
    
    @Override
    public String getSymbol() {
        return "W";
    }
}
