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
    public int Health;
    public int damage;
    public Iweapon weapon;
    
    
    public Hero(int row, int col) {
        super(row, col, "@");
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
    
    @Override
    public String getSymbol() {
        return "@";
    }
    @Override // 맵 만들어질때 구현
    public void onDelete() {
        
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
        return "HP: "+this.getHealth()+"/"+this.getMaxHealth()+" | Weapon: "+
                this.weapon+" | Key: "; //key 확인 여부 추가 
    }
    
    
    
    
}
