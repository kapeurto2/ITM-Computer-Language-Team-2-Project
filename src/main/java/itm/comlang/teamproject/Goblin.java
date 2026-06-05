/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author 오갱
 */
public class Goblin extends Entity implements Fightable{
        private int maxHealth;
    private int Health;
    private int damage;
    
    public Goblin(int row, int col) {
        super(row, col, "G");
        this.maxHealth = 3;
        this.Health = 3;
        this.damage = 1;
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
        return "G";
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
    @Override
    public int getDamage() {
        return this.damage;
    }
    
}
