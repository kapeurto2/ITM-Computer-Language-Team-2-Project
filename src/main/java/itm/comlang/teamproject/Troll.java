/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author 오갱
 */
public class Troll extends Entity implements Fightable{
    private int maxHealth;
    private int Health;
    private int damage;
    
    public Troll(int row, int col) {
        super(row, col, "T");
        this.maxHealth = 15;
        this.Health = 15;
        this.damage = 4;
    }
    
    @Override
    public void onDelete() {
        
    }
    @Override
    public int getMaxHealth() {
        return this.maxHealth;
    }
    @Override
    public int getHealth() {
        return this.Health;
    }
    
    public void setHealth(int health) {
        this.Health = health;
    }
    @Override
    public void attack(Fightable target) {
        int damage = target.getDamage();
        this.takeDamage(damage);
        target.takeDamage(this.damage);
        
    }
    @Override
    public void takeDamage(int amount) {
        this.Health -= amount;
    }
    
    
    @Override
    public String getSymbol() {
        return "T";
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
    @Override
    public int getDamage() {
        return this.damage;
    }
    //성민 애몽..
    public void onDrop() {
        
    }
    
    
  
    
}
