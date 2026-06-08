/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;
 
/**
 *  *
 * 
 * @author Kapeu
 */
public interface Fightable {
    int getHealth();             // current health
    int getMaxHealth();          // max health
    void takeDamage(int amount); // apply damage
    int getDamage();             // damage this target deals
}
