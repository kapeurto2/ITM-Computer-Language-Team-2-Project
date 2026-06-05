/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author 오갱
 */
public class BigFlask extends Item implements Ihealing {

    private int recover;

    public BigFlask(int row, int col) {
        super(row, col);
        this.recover = 12;
    }

    @Override
    public void onDelete(Room room) {
        room.removeEntity(this);
    }

    // 성민애몽
    @Override
    public void onInteract(Hero hero, Room room) {
        if (onRecover(hero)) {
            room.removeEntity(this);
        }
    }

    @Override
    public boolean onRecover(Hero hero) {
        if (hero.getHealth() >= hero.getMaxHealth()) {
            return false;
        }
        int heal = hero.getHealth() + this.recover;
        if (heal > hero.getMaxHealth()) {
            hero.setHealth(hero.getMaxHealth());
        } else {
            hero.setHealth(heal);
        }
        return true;
    }

    
    @Override
    public String getSymbol() {
        return "B";
    }
    

}
