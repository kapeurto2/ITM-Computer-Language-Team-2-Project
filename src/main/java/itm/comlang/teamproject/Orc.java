/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itm.comlang.teamproject;

/**
 *
 * @author 오갱
 */
public class Orc extends Monster {
 
    public Orc(int row, int col) {
        super(row, col, 8, 3);
    }
 
    @Override
    public String getSymbol() {
        return "O";
    }
}