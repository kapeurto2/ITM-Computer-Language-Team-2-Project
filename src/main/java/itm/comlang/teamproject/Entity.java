package itm.comlang.teamproject;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Kapeu
 */
public abstract class Entity {
    private String symbol;
    private int x;
    private int y;
    public Entity(int x, int y, String symbol) {
       this.x = x;
       this.y = y;
       this.symbol=symbol;
    }
    public abstract void onDeath();
    
    public String getSymbol(){
        return symbol;
    }
    public int getXlocation() {
        return x;
    }
    public int getYlocation() {
        return y;
    }
    public void setXlocation(int num) {
        this.x = num;
    }
    public void setYlocation(int num) {
        this.y = num;
    }
    public void setLocation(int num1, int num2) {
        this.x = num1;
        this.y = num2;
    }
}
