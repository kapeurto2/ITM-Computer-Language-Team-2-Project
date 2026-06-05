/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package itm.comlang.teamproject;
import java.util.*;
import java.io.*;
/**
 *
 * @author Kapeu
 */
public class Teamproject {
    public static void main(String[] args) throws IOException {
        Room room = new Room("start.csv");  // 방 읽기
        room.printRoom();                    // 화면 출력
    }
}
