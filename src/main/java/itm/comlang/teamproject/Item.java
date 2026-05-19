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
    public Item(int x, int y, String symbol) {
        super(x, y,symbol);
    }

    // 아이템은 제거될 때 그리드에서 사라짐
    @Override
    public void onDeath() {
    }

    // 픽업 동작은 각 서브클래스가 구현
    public abstract void interact(Hero hero, Room room);
}