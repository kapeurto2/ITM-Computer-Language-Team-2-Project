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

    // Working folder for this run (holds copies, never the originals).
    private static String saveDir;

    public static void main(String[] args) {
        System.out.println("===== Solo Adventure Maze =====");
        System.out.println("Move with w/a/s/d. (w=up, s=down, a=left, d=right)");
        System.out.println();

        // 1) Copy the original CSV files into a per-run folder.
        try {
            saveDir = prepareRunFolder();
        } catch (IOException e) {
            System.out.println("Failed to prepare the game files: " + e.getMessage());
            return;
        }

        // 2) Load the starting room.
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

        // Place the hero (placement rules live in Room.findHeroStart).
        int[] start = room.findHeroStart();
        Hero hero = new Hero(start[0], start[1]);
        Entity existing = room.getEntityAt(start[0], start[1]);
        if (existing instanceof Hero) {
            room.removeEntity(existing);   // drop the @ from file, keep our hero
        }
        room.addEntity(hero);

        // 3) Main loop.
        boolean running = true;
        while (running) {
            printStatus(hero, room);

            boolean killed = handleAdjacentCombat(hero, room);
            if (hero.getHealth() <= 0) {
                System.out.println("\nYou have died. Game over.");
                break;
            }
            if (killed) {
                printStatus(hero, room);   // redraw map after a kill
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

            // Wall / out-of-bounds check.
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
                    running = false;          // master door win
                } else {
                    room = result;            // moved through a door, or same room
                }

            } else if (target instanceof Item) {
                handleItem(hero, room, (Item) target, nr, nc);

            } else if (target instanceof Monster) {
                System.out.println("A " + target.getSymbol()
                        + " blocks the way. Attack it from an adjacent tile.");

            } else {
                hero.setLocation(nr, nc);
            }
        }

        System.out.println("\nThanks for playing!");
    }

    // =======================================================
    // Copy the original CSVs into a fresh per-run folder.
    //  - source: 'rooms' folder if present, otherwise the project root
    //  - copying uses Scanner(Paths) + PrintWriter, same as Room
    // =======================================================
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

    // Copy one file: read with Scanner, write with PrintWriter.
    private static void copyCsv(String from, String to) throws IOException {
        try (Scanner sc = new Scanner(Paths.get(from));
             PrintWriter pw = new PrintWriter(to)) {
            while (sc.hasNextLine()) {
                pw.println(sc.nextLine());
            }
        }
    }

    // =======================================================
    // Stats line + room map.
    // =======================================================
    private static void printStatus(Hero hero, Room room) {
        System.out.println();
        System.out.println(hero.toString());   // HP / Weapon / Key
        room.printRoom();
    }

    // =======================================================
    // Combat: handle every monster adjacent to the hero (up/down/left/right).
    // The actual damage math is done by Hero.attack / Monster.counterAttack.
    // =======================================================
    private static boolean handleAdjacentCombat(Hero hero, Room room) {
        boolean killedAny = false;
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] d : dirs) {
            int r = hero.getRow() + d[0];
            int c = hero.getCol() + d[1];
            Entity e = room.getEntityAt(r, c);
            if (e instanceof Monster) {
                boolean killed = fightMonster(hero, room, (Monster) e);
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

    // Action-menu loop for one monster. Returns true if it was killed.
    private static boolean fightMonster(Hero hero, Room room, Monster monster) {

        while (!monster.isDefeated() && hero.getHealth() > 0) {
            System.out.println();
            System.out.println("A " + monster.getSymbol() + " is adjacent! "
                    + "(HP: " + monster.getHealth() + ", Damage: " + monster.getDamage() + ")");

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
                // The simultaneous exchange is handled by Hero/Monster.
                hero.attack(monster);
                System.out.println("You strike the " + monster.getSymbol()
                        + "! (Monster HP: " + Math.max(monster.getHealth(), 0)
                        + ", Your HP: " + Math.max(hero.getHealth(), 0) + ")");

                if (monster.isDefeated()) {
                    System.out.println("The " + monster.getSymbol() + " is defeated!");
                    monster.onDelete(room);   // Troll drops the key here
                    return true;              // killed
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
    // Item handling (weapon / potion / key).
    // =======================================================
    private static void handleItem(Hero hero, Room room, Item item, int nr, int nc) {

        if (item instanceof Iweapon) {
            if (hero.getWeapon() == null) {
                // Unarmed -> auto-equip (onInteract equips + removes from room).
                item.onInteract(hero, room);
                hero.setLocation(nr, nc);
                System.out.println("You picked up the " + ((Iweapon) item).getName() + ".");
            } else {
                // Armed -> ask whether to switch.
                System.out.print("Switch to " + ((Iweapon) item).getName()
                        + "? (current: " + hero.getWeapon().getName() + ") (y/n): ");
                String ans = INPUT.hasNextLine() ? INPUT.nextLine().trim().toLowerCase() : "n";

                if (ans.equals("y")) {
                    Iweapon old = hero.getWeapon();
                    int fromR = hero.getRow();
                    int fromC = hero.getCol();
                    item.onInteract(hero, room);            // equip new weapon + clear its cell
                    hero.setLocation(nr, nc);               // move onto its cell
                    dropOldWeapon(room, old, fromR, fromC); // leave old weapon behind
                    System.out.println("Switched to the " + ((Iweapon) item).getName() + ".");
                } else {
                    System.out.println("You keep your " + hero.getWeapon().getName() + ".");
                }
            }

        } else if (item instanceof Ihealing) {
            int before = hero.getHealth();
            item.onInteract(hero, room);   // full HP -> left in room; otherwise heal + remove
            hero.setLocation(nr, nc);
            if (hero.getHealth() > before) {
                System.out.println("You drink the potion. (HP: " + hero.getHealth()
                        + "/" + hero.getMaxHealth() + ")");
            } else {
                System.out.println("HP already full. The potion stays here.");
            }

        } else {
            // Key
            item.onInteract(hero, room);   // setKey + remove
            hero.setLocation(nr, nc);
            System.out.println("You picked up the key!");
        }
    }

    // Drop the old weapon by recreating its concrete type.
    // findDropCell prevents stacking the dropped weapon on top of another floor object.
    private static void dropOldWeapon(Room room, Iweapon old, int r, int c) {
        int[] cell = room.findDropCell(r, c);
        int dr = cell[0];
        int dc = cell[1];

        Item dropped = null;
        if (old instanceof Stick) {
            dropped = new Stick(dr, dc);
        } else if (old instanceof WeakSword) {
            dropped = new WeakSword(dr, dc);
        } else if (old instanceof StrongSword) {
            dropped = new StrongSword(dr, dc);
        }
        if (dropped != null) {
            room.addEntity(dropped);
        }
    }

    // =======================================================
    // Door handling. Returns:
    //   - regular door: the new Room moved into
    //   - master door (win): null
    //   - blocked (no key / missing target): the same room
    // =======================================================
    private static Room handleDoor(Hero hero, Room room, Door door) {

        if (door.isMaster()) {
            if (hero.hasKey()) {
                System.out.println("\nYou unlock the master door with the key...");
                System.out.println("   YOU ESCAPED THE MAZE! YOU WIN!    ");
                return null;
            } else {
                System.out.println("The master door is locked. You need a key.");
                return room;
            }
        }

        // ---- regular door ----
        // Use only the file name, whether the cell is d:room2.csv or d:rooms/room2.csv.
        String targetFile = fileNameOnly(door.getTargetRoom());
        if (targetFile.isEmpty()) {
            System.out.println("This door leads nowhere.");
            return room;
        }

        // 1) Save the current room (remove hero first so no @ is written).
        try {
            room.removeEntity(hero);
            room.saveToCSV(saveDir + File.separator + fileNameOnly(room.getFileName()));
        } catch (IOException e) {
            System.out.println("Warning: could not save room state: " + e.getMessage());
        }

        // 2) Load the target room (from the per-run copy).
        Room newRoom;
        String targetPath = saveDir + File.separator + targetFile;
        try {
            newRoom = new Room(targetPath);
        } catch (IOException e) {
            System.out.println("The door references a missing room (" + targetFile + ").");
            room.addEntity(hero);   // move failed -> put hero back
            return room;
        } catch (RuntimeException e) {
            System.out.println("The target room is malformed (" + targetFile + ").");
            room.addEntity(hero);
            return room;
        }

        // 3) Place the hero next to the door that leads back to the room we came from.
        int[] spot = entrySpot(newRoom, fileNameOnly(room.getFileName()));
        hero.setLocation(spot[0], spot[1]);
        newRoom.addEntity(hero);

        System.out.println("You enter " + targetFile + ".");
        return newRoom;
    }

    // Find the door pointing back to the room we came from, return an empty cell
    // next to it. Falls back to findHeroStart if none is found.
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

    // Extract just the file name (rooms/room2.csv -> room2.csv).
    private static String fileNameOnly(String path) {
        if (path == null) {
            return "";
        }
        String p = path.trim().replace('\\', '/');
        int slash = p.lastIndexOf('/');
        return (slash >= 0) ? p.substring(slash + 1) : p;
    }
}
