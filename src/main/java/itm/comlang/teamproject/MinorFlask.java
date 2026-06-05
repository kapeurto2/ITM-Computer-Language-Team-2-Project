/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author 오갱
 */
public class MinorFlask extends Item implements Ihealing{

    private int recover;

    
    public MinorFlask(int row, int col) {
        super(row, col, "m");
        this.recover = 6;
    }
    //성민애몽
    public void onDelete() {
        
    }
    // 성민애몽
    @Override
    public void interact(Hero hero) {
        
    }
    
    @Override
    public void onRecover(Hero hero) {
        int heal = hero.getHealth() + this.recover;
        if(heal > hero.getMaxHealth()) {
            hero.setHealth(hero.getMaxHealth());
        } else {
            hero.setHealth(heal);
        }
        
    }
    @Override
    public String getSymbol() {
        return "m";
    }
    
    
    
    
    
    
}
