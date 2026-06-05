/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author Kapeu
 */
public interface Fightable {
    void attack(Fightable target);
    int getHealth();      // 현재 체력 확인
    int getMaxHealth();          // 최대 체력 확인 
    void takeDamage(int amount); // 데미지 처리
    int getDamage(); //hero/ monster로 부터 damage 불러오기
    
}