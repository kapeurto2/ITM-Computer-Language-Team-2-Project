/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author Kapeu
 */

public abstract class Item extends Entity {

    public Item(int row, int col) {
        super(row, col);
    }

    // 픽업 동작은 각 서브클래스가 구현
    public abstract void onInteract(Hero hero, Room room);
}