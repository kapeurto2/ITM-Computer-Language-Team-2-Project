/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package itm.comlang.teamproject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * 
 *
 * @author Kapeu
 */
public class Teamproject {

    private static final Scanner INPUT = new Scanner(System.in);

    // 이번 실행에서 읽고/쓰는 작업 폴더 이름 (원본이 아니라 복사본들이 들어있음)
    private static String saveDir;

    public static void main(String[] args) {
        System.out.println("===== Solo Adventure Maze =====");
        System.out.println("Move with w/a/s/d. (w=up, s=down, a=left, d=right)");
        System.out.println();

        // 1) 원본 CSV 들을 per-run 폴더로 복사
        try {
            saveDir = prepareRunFolder();
        } catch (IOException e) {
            System.out.println("Failed to prepare the game files: " + e.getMessage());
            return;
        }

        // 2) 시작 방 로드
        Room room;
        try {
            room = new Room(saveDir + File.separator + "start.csv");
        } catch (IOException e) {
            System.out.println("Could not load start.csv: " + e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.out.println("start.csv is malformed: " + e.getMessage());
            return;
        }

        // 히어로 배치 (배치 규칙은 Room.findHeroStart 담당)
        int[] start = room.findHeroStart();
        Hero hero = new Hero(start[0], start[1]);
        Entity existing = room.getEntityAt(start[0], start[1]);
        if (existing instanceof Hero) {
            room.removeEntity(existing);   // 파일에 @ 가 있었으면 우리 hero 로 통일
        }
        room.addEntity(hero);

        // 3) 메인 루프
        boolean running = true;
        while (running) {
            printStatus(hero, room);

            boolean killed = handleAdjacentCombat(hero, room);
            if (hero.getHealth() <= 0) {
                System.out.println("\nYou have died. Game over.");
                break;
            }
            if (killed) {
                printStatus(hero, room);   // 몬스터를 처치했으면 갱신된 맵 다시 표시
            }

            System.out.print("\nYour move (w/a/s/d, q=quit): ");
            if (!INPUT.hasNextLine()) {
                break;
            }
            String cmd = INPUT.nextLine().trim().toLowerCase();

            if (cmd.equals("q")) {
                System.out.println("You quit the game.");
                break;
            }
            if (!cmd.equals("w") && !cmd.equals("a") && !cmd.equals("s") && !cmd.equals("d")) {
                System.out.println("Invalid command. Use w/a/s/d (or q to quit).");
                continue;
            }

            int[] next = hero.nextPosition(cmd);
            int nr = next[0];
            int nc = next[1];

            // 범위(벽) 체크
            if (nr < 0 || nr >= room.getRows() || nc < 0 || nc >= room.getCols()) {
                System.out.println("A wall blocks your way.");
                continue;
            }

            Entity target = room.getEntityAt(nr, nc);

            if (target == null) {
                hero.setLocation(nr, nc);

            } else if (target instanceof Door) {
                Room result = handleDoor(hero, room, (Door) target);
                if (result == null) {
                    running = false;          // 마스터문 승리
                } else {
                    room = result;            // 일반문 이동 또는 동일 방 유지
                }

            } else if (target instanceof Item) {
                handleItem(hero, room, (Item) target, nr, nc);

            } else if (target instanceof Fightable) {
                System.out.println("A " + target.getSymbol()
                        + " blocks the way. Attack it from an adjacent tile.");

            } else {
                hero.setLocation(nr, nc);
            }
        }

        System.out.println("\nThanks for playing!");
    }

    private static String prepareRunFolder() throws IOException {
        File originDir = new File("rooms");
        if (!originDir.isDirectory()) {
            originDir = new File(".");
        }

        String dir = "run_" + System.currentTimeMillis();
        new File(dir).mkdirs();

        File[] files = originDir.listFiles();
        int copied = 0;
        if (files != null) {
            for (File f : files) {
                if (f.isFile() && f.getName().toLowerCase().endsWith(".csv")) {
                    copyCsv(f.getPath(), dir + File.separator + f.getName());
                    copied++;
                }
            }
        }

        if (copied == 0) {
            throw new IOException("no .csv files found (looked in 'rooms' and project root)");
        }
        System.out.println("Working folder: " + dir + " (" + copied + " files copied)\n");
        return dir;
    }

    // 파일 한 개 복사: Scanner 로 읽어 PrintWriter 로 그대로 쓴다
    private static void copyCsv(String from, String to) throws IOException {
        try (Scanner sc = new Scanner(Paths.get(from));
             PrintWriter pw = new PrintWriter(to)) {
            while (sc.hasNextLine()) {
                pw.println(sc.nextLine());
            }
        }
    }

    private static void printStatus(Hero hero, Room room) {
        System.out.println();
        System.out.println(hero.toString());   // HP / Weapon / Key
        room.printRoom();
    }

    private static boolean handleAdjacentCombat(Hero hero, Room room) {
        boolean killedAny = false;
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] d : dirs) {
            int r = hero.getRow() + d[0];
            int c = hero.getCol() + d[1];
            Entity e = room.getEntityAt(r, c);
            if (e instanceof Fightable && !(e instanceof Hero)) {
                boolean killed = fightMonster(hero, room, e);
                if (killed) {
                    killedAny = true;
                }
                if (hero.getHealth() <= 0) {
                    return killedAny;
                }
            }
        }
        return killedAny;
    }

    // 한 몬스터에 대한 액션 메뉴 루프
    private static boolean fightMonster(Hero hero, Room room, Entity monster) {
        Fightable mon = (Fightable) monster;

        while (mon.getHealth() > 0 && hero.getHealth() > 0) {
            System.out.println();
            System.out.println("A " + monster.getSymbol() + " is adjacent! "
                    + "(HP: " + mon.getHealth() + ", Damage: " + mon.getDamage() + ")");

            if (hero.getWeapon() == null) {
                System.out.println("You are unarmed and cannot attack. You must flee.");
                return false;
            }

            System.out.print("Action - (a)ttack or (s)kip: ");
            if (!INPUT.hasNextLine()) {
                return false;
            }
            String choice = INPUT.nextLine().trim().toLowerCase();

            if (choice.equals("a")) {
                // 동시 타격: hero.attack 한 번이 양쪽 데미지를 모두 처리
                hero.attack(mon);
                System.out.println("You strike the " + monster.getSymbol()
                        + "! (Monster HP: " + Math.max(mon.getHealth(), 0)
                        + ", Your HP: " + Math.max(hero.getHealth(), 0) + ")");

                if (mon.getHealth() <= 0) {
                    System.out.println("The " + monster.getSymbol() + " is defeated!");
                    monster.onDelete(room);   // 트롤이면 여기서 키 드롭
                    return true;              // 처치함
                }
                if (hero.getHealth() <= 0) {
                    return false;
                }
            } else if (choice.equals("s")) {
                System.out.println("You skip the fight.");
                return false;
            } else {
                System.out.println("Invalid action. Use 'a' or 's'.");
            }
        }
        return false;
    }

    // =======================================================
    // 아이템 처리 (무기 / 포션 / 키)
    // =======================================================
    private static void handleItem(Hero hero, Room room, Item item, int nr, int nc) {

        if (item instanceof Iweapon) {
            if (hero.getWeapon() == null) {
                // 맨손 -> 자동 장착 (onInteract 가 장착 + 방에서 제거)
                item.onInteract(hero, room);
                hero.setLocation(nr, nc);
                System.out.println("You picked up the " + ((Iweapon) item).getName() + ".");
            } else {
                // 무장 중 -> 교체 여부 질문
                System.out.print("Switch to " + ((Iweapon) item).getName()
                        + "? (current: " + hero.getWeapon().getName() + ") (y/n): ");
                String ans = INPUT.hasNextLine() ? INPUT.nextLine().trim().toLowerCase() : "n";

                if (ans.equals("y")) {
                    Iweapon old = hero.getWeapon();
                    int fromR = hero.getRow();
                    int fromC = hero.getCol();
                    item.onInteract(hero, room);         
                    hero.setLocation(nr, nc);             
                    dropOldWeapon(room, old, fromR, fromC); 
                    System.out.println("Switched to the " + ((Iweapon) item).getName() + ".");
                } else {
                    System.out.println("You keep your " + hero.getWeapon().getName() + ".");
                }
            }

        } else if (item instanceof Ihealing) {
            int before = hero.getHealth();
            item.onInteract(hero, room);   
            hero.setLocation(nr, nc);
            if (hero.getHealth() > before) {
                System.out.println("You drink the potion. (HP: " + hero.getHealth()
                        + "/" + hero.getMaxHealth() + ")");
            } else {
                System.out.println("HP already full. The potion stays here.");
            }

        } else {
            // 키
            item.onInteract(hero, room);   // setKey + 제거
            hero.setLocation(nr, nc);
            System.out.println("You picked up the key!");
        }
    }

    private static void dropOldWeapon(Room room, Iweapon old, int r, int c) {
        Item dropped = null;
        if (old instanceof Stick) {
            dropped = new Stick(r, c);
        } else if (old instanceof WeakSword) {
            dropped = new WeakSword(r, c);
        } else if (old instanceof StrongSword) {
            dropped = new StrongSword(r, c);
        }
        if (dropped != null) {
            room.addEntity(dropped);
        }
    }
    private static Room handleDoor(Hero hero, Room room, Door door) {

        if (door.isMaster()) {
            if (hero.hasKey()) {
                System.out.println("\nYou unlock the master door with the key...");
                System.out.println("*** YOU ESCAPED THE MAZE! YOU WIN! ***");
                return null;
            } else {
                System.out.println("The master door is locked. You need a key.");
                return room;
            }
        }

         String targetFile = fileNameOnly(door.getTargetRoom());
        if (targetFile.isEmpty()) {
            System.out.println("This door leads nowhere.");
            return room;
        }

        try {
            room.removeEntity(hero);
            room.saveToCSV(saveDir + File.separator + fileNameOnly(room.getFileName()));
        } catch (IOException e) {
            System.out.println("Warning: could not save room state: " + e.getMessage());
        }

        // 2) 대상 방 로드 (per-run 폴더의 복사본에서)
        Room newRoom;
        String targetPath = saveDir + File.separator + targetFile;
        try {
            newRoom = new Room(targetPath);
        } catch (IOException e) {
            System.out.println("The door references a missing room (" + targetFile + ").");
            room.addEntity(hero);   // 이동 실패 -> 원래 방 복귀
            return room;
        } catch (RuntimeException e) {
            System.out.println("The target room is malformed (" + targetFile + ").");
            room.addEntity(hero);
            return room;
        }

        // 3) 새 방에서 히어로 위치: 떠나온 방을 가리키는 문 옆 빈칸
        int[] spot = entrySpot(newRoom, fileNameOnly(room.getFileName()));
        hero.setLocation(spot[0], spot[1]);
        newRoom.addEntity(hero);

        System.out.println("You enter " + targetFile + ".");
        return newRoom;
    }

    // 새 방에서, '돌아온 방'을 가리키는 문을 찾아 그 옆 빈칸 좌표 반환.
    // 못 찾으면 findHeroStart 폴백.
    private static int[] entrySpot(Room newRoom, String cameFromFile) {
        for (Door d : newRoom.getDoors()) {
            String t = d.getTargetRoom();
            if (t != null && fileNameOnly(t).equals(cameFromFile)) {
                int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                for (int[] off : dirs) {
                    int r = d.getRow() + off[0];
                    int c = d.getCol() + off[1];
                    if (r >= 0 && r < newRoom.getRows()
                            && c >= 0 && c < newRoom.getCols()
                            && newRoom.getEntityAt(r, c) == null) {
                        return new int[]{r, c};
                    }
                }
            }
        }
        return newRoom.findHeroStart();
    }

    private static String fileNameOnly(String path) {
        if (path == null) {
            return "";
        }
        String p = path.trim().replace('\\', '/');
        int slash = p.lastIndexOf('/');
        return (slash >= 0) ? p.substring(slash + 1) : p;
    }
}