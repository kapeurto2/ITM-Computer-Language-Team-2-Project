/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 * 
 *
 * @author Kapeu
 */
public abstract class Monster extends Entity implements Fightable {

    private int maxHealth;
    private int health;
    private int damage;

    public Monster(int row, int col, int maxHealth, int damage) {
        super(row, col);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.damage = damage;
    }

    @Override
    public int getMaxHealth() {
        return this.maxHealth;
    }

    @Override
    public int getHealth() {
        return this.health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public int getDamage() {
        return this.damage;
    }

    @Override
    public void takeDamage(int amount) {
        this.health -= amount;
    }

    // =======================================================
    // Combat (monster side)
    // Simultaneous counter when attacked by the hero: deals the
    // monster's own damage to the hero.
    // =======================================================
    public void counterAttack(Hero hero) {
        hero.takeDamage(this.damage);
    }

    // Defeated once HP drops to zero or below.
    public boolean isDefeated() {
        return this.health <= 0;
    }

    @Override
    public void onDelete(Room room) {
        room.removeEntity(this);
    }

    @Override
    public abstract String getSymbol();
}