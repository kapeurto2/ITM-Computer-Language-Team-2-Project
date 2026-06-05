/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author 오갱
 */
public class BigFlask implements Ihealing{
    private int row;
    private int col;
    private int recover;
    private String symbol;
    
    public BigFlask(int col, int row, String symbol) {
        this.col = col;
        this.row = row;
        this.symbol = symbol;
        this.recover = 12;
    }
    //성민애몽
    public void onDelete() {
        
    }
    // 성민애몽
    public void onInteract() {
        
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
    
}
