/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author 오갱
 */
public class Orc extends Entity implements Fightable{
    private int maxHealth;
    private int Health;
    private int damage;
    
    public Orc(int row, int col) {
        super(row, col);
        this.maxHealth = 8;
        this.Health = 8;
        this.damage = 3;
    }
    
    @Override
    public void onDelete(Room room) {
        room.removeEntity(this);
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
        return "O";
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
