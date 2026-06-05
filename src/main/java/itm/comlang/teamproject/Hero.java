/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author 오갱
 */
public class Hero extends Entity implements Fightable{
    
    private int maxHealth;
    private int Health;
    private int damage;
    private boolean key;
    private Iweapon weapon;
    
    
    public Hero(int row, int col) {
        super(row, col);
        this.maxHealth=25;
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
        this.key = !(this.key);
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
            case "w": r--; break;   // 위
            case "s": r++; break;   // 아래
            case "a": c--; break;   // 왼쪽
            case "d": c++; break;   // 오른쪽
    }
    return new int[]{r, c};
}

    @Override
    public String getSymbol() {
        return "@";
    }
    @Override
    public void onDelete(Room room) {
        room.removeEntity(this);
    }

    @Override
    public void attack(Fightable target) {
        int damage = target.getDamage();
        this.takeDamage(damage);
        target.takeDamage(this.damage);
        
    }
    @Override // 현재 체력 확인
    public int getHealth() {
        return this.Health;
        
    }
    @Override // 최대 체력 확인 
    public int getMaxHealth() {
        return this.maxHealth;
    } 
    
    @Override // 데미지 처리
    public void takeDamage(int amount) {
        this.Health -= amount;
        
    }
    @Override
    public int getDamage() {
        return this.damage;
    }
    
    
    @Override // 
    public String toString() {
        String w = (this.weapon == null) ? "None" : this.weapon.getName();
        String k = this.key ? "Yes" : "No";
        return "HP: " +this.Health + "/" + this.maxHealth + " | Weapon: " + w + " | Key: " + k;
    }
}
    
    
    
    

