/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author 오갱
 */
public class Hero extends Entity implements Fightable {

    private int maxHealth;
    private int Health;
    private int damage;
    private boolean key;
    private Iweapon weapon;

    public Hero(int row, int col) {
        super(row, col);
        this.maxHealth = 25;
        this.Health = 25;
        this.weapon = null;
        this.damage = 0;
    }

    public void setHealth(int health) {
        this.Health = health;
    }

    public void setWeapon(Iweapon weapon) {
        this.weapon = weapon;
        this.damage = weapon.getDamage();
    }

    public void setKey() {
        this.key = true;
    }

    public Iweapon getWeapon() {
        return this.weapon;
    }

    public boolean hasKey() {
        return this.key;
    }

    public int[] nextPosition(String dir) {
        int r = this.getRow();
        int c = this.getCol();
        switch (dir) {
            case "w": r--; break;   // up
            case "s": r++; break;   // down
            case "a": c--; break;   // left
            case "d": c++; break;   // right
        }
        return new int[]{r, c};
    }

    // =======================================================
    // Combat (hero side)
    // The hero attacks an adjacent monster. Combat is a simultaneous
    // exchange: the hero deals weapon damage and the monster strikes
    // back immediately (the counter happens even on the killing blow).
    // =======================================================
    public void attack(Monster target) {
        target.takeDamage(this.damage);   // hero's hit (weapon damage)
        target.counterAttack(this);       // monster's simultaneous counter
    }

    @Override
    public String getSymbol() {
        return "@";
    }

    @Override
    public void onDelete(Room room) {
        room.removeEntity(this);
    }

    @Override // current health
    public int getHealth() {
        return this.Health;
    }

    @Override // max health
    public int getMaxHealth() {
        return this.maxHealth;
    }

    @Override // apply damage
    public void takeDamage(int amount) {
        this.Health -= amount;
    }

    @Override
    public int getDamage() {
        return this.damage;
    }

    @Override
    public String toString() {
        String w = (this.weapon == null) ? "None" : this.weapon.getName();
        String k = this.key ? "Yes" : "No";
        return "HP: " + this.Health + "/" + this.maxHealth + " | Weapon: " + w + " | Key: " + k;
    }
}