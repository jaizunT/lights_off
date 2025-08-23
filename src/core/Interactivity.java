package core;
import core.World.Position;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.lang.reflect.Array;
import java.util.*;

public class Interactivity {
    World bigWorld;
    TETile[][] bigWorldLights;
    TETile[][] bonusRoom;
    TETile[][] entityPath;
    Player player;
    Entity entity;
    static Out log;
    boolean lightsOn;
    boolean entityPathOn;
    public boolean bright;
    public final int windowWidth = 106;
    public final int windowHeight = 58;
    public final int gameWidth = 106;
    public final int gameHeight = 53;
    public final int keys = 3;
    public final int minTeleports = 40;
    public final int maxTeleports = 80;
    public final int lights = 8;
    public boolean inBonusRoom = false;
    public boolean victory = false;
    public Clip menuClip;
    public Clip worldClip;



    public void load () {
        File file = new File("save.txt");
        In in = new In(file);
        String seed = in.readLine();
        String text = in.readLine();
        if (text != null) {
            StringBuilder untilS = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) != 'v') {
                    untilS.append(text.charAt(i));
                } else {
                    untilS.append('v');
                    break;
                }
            }
            text = untilS.toString();
            log = new Out("save.txt");
            log.println(seed);
            log.print(text);
            runStates(text, Integer.parseInt(seed));
        } else {
            restart(Integer.parseInt(seed));
        }
        player.updateWorld();
        entity.updateWorld();
    }
    public void resetFile () {
        log = new Out("save.txt");
        log.println(Long.toString(bigWorld.seed));
        log.print('v');
    }

    public void rewriteSave () {
        File file = new File("save.txt");
        In in = new In(file);
        String seed = in.readLine();
        String text = in.readLine();
        StringBuilder untilS = new StringBuilder();
        int v = 0;
        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == 'v') {
                    v++;
                }
            }
            int countvs = 0;
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) != 'v' || countvs == v - 1) {
                    untilS.append(text.charAt(i));
                } else {
                    countvs += 1;
                }
            }
            text = untilS.toString();
            System.out.println(text);
            log.close();
            log = new Out("save.txt");
            log.println(seed);
            log.print(text);
        }

    }

    public class Player {
        Position pos;
        TETile currentTile;
        TETile avatar;
        int orbs;
        int coins;
        int keysPlaced;
        int directionFacing; //0 = up, 1 = down, 2 = left, 3 = right

        public Player(Position pos, TETile currentTile) {
            this.pos = pos;
            this.currentTile = currentTile;
            this.avatar = Tileset.AVATAR;
            this.orbs = 20; //OP START, change to 0
            this.keysPlaced = 0;
            this.directionFacing = 0;
            int coins = 0;
        }
        public void w() {
            if (canW()) {
                moveW();
            }
            directionFacing = 0;
        }
        public void a() {
            if (canA()) {
                moveA();
            }
            directionFacing = 2;

        }
        public void s() {
            if (canS()) {
                moveS();
            }
            directionFacing = 1;

        }
        public void d() {
            if (canD()) {
                moveD();
            }
            directionFacing = 3;

        }
        public boolean canW() {
            if (!inBonusRoom) {
                return !(bigWorld.getTile(bigWorld.getNorthPos(pos)) == Tileset.WALL);
            } else {
                return !(bonusRoom[bigWorld.getNorthPos(pos).x][bigWorld.getNorthPos(pos).y] == Tileset.WALL);
            }
        }
        public boolean canA() {
            if (!inBonusRoom) {
                return !(bigWorld.getTile(bigWorld.getWestPos(pos)) == Tileset.WALL);

            } else {
                return !(bonusRoom[bigWorld.getWestPos(pos).x][bigWorld.getWestPos(pos).y] == Tileset.WALL);
            }
        }
        public boolean canS() {
            if (!inBonusRoom) {
                return !(bigWorld.getTile(bigWorld.getSouthPos(pos)) == Tileset.WALL);
            } else {
                return !(bonusRoom[bigWorld.getSouthPos(pos).x][bigWorld.getSouthPos(pos).y] == Tileset.WALL);
            }
        }
        public boolean canD() {
            if (!inBonusRoom) {
                return !(bigWorld.getTile(bigWorld.getEastPos(pos)) == Tileset.WALL);
            }
            return !(bonusRoom[bigWorld.getEastPos(pos).x][bigWorld.getEastPos(pos).y] == Tileset.WALL);
        }

        public void moveW() {
            if (!inBonusRoom) {
                //get new tile
                TETile newTile = bigWorld.getTile(bigWorld.getNorthPos(pos));
                //set current tile to original tile
                bigWorld.world[pos.x][pos.y] = currentTile;
                //set new current tile to newTile
                currentTile = newTile;
                //update pos
                pos = bigWorld.getNorthPos(pos);
            } else {
                //get new tile
                TETile newTile = bonusRoom[bigWorld.getNorthPos(pos).x][bigWorld.getNorthPos(pos).y];
                //set current tile to original tile
                bonusRoom[pos.x][pos.y] = currentTile;
                //set new current tile to newTile
                currentTile = newTile;
                //update pos
                pos = bigWorld.getNorthPos(pos);
            }
        }
        public void moveA() {
            if (!inBonusRoom) {
                //get new tile
                TETile newTile = bigWorld.getTile(bigWorld.getWestPos(pos));
                //set current tile to original tile
                bigWorld.world[pos.x][pos.y] = currentTile;
                //set new current tile to newTile
                currentTile = newTile;
                //update pos
                pos = bigWorld.getWestPos(pos);
            } else {
                //get new tile
                TETile newTile = bonusRoom[bigWorld.getWestPos(pos).x][bigWorld.getWestPos(pos).y];
                //set current tile to original tile
                bonusRoom[pos.x][pos.y] = currentTile;
                //set new current tile to newTile
                currentTile = newTile;
                //update pos
                pos = bigWorld.getWestPos(pos);
            }
        }
        public void moveS() {
            if (!inBonusRoom) {
                //get new tile
                TETile newTile = bigWorld.getTile(bigWorld.getSouthPos(pos));
                //set current tile to original tile
                bigWorld.world[pos.x][pos.y] = currentTile;
                //set new current tile to newTile
                currentTile = newTile;
                //update pos
                pos = bigWorld.getSouthPos(pos);
            } else {
                //get new tile
                TETile newTile = bonusRoom[bigWorld.getSouthPos(pos).x][bigWorld.getSouthPos(pos).y];
                //set current tile to original tile
                bonusRoom[pos.x][pos.y] = currentTile;
                //set new current tile to newTile
                currentTile = newTile;
                //update pos
                pos = bigWorld.getSouthPos(pos);
            }

        }
        public void moveD() {
            if (!inBonusRoom) {
                //get new tile
                TETile newTile = bigWorld.getTile(bigWorld.getEastPos(pos));
                //set current tile to original tile
                bigWorld.world[pos.x][pos.y] = currentTile;
                //set new current tile to newTile
                currentTile = newTile;
                //update pos
                pos = bigWorld.getEastPos(pos);
            } else {
                //get new tile
                TETile newTile = bonusRoom[bigWorld.getEastPos(pos).x][bigWorld.getEastPos(pos).y];
                //set current tile to original tile
                bonusRoom[pos.x][pos.y] = currentTile;
                //set new current tile to newTile
                currentTile = newTile;
                //update pos
                pos = bigWorld.getEastPos(pos);
            }

        }

        public void updateWorld() {
            if (inBonusRoom) {
                bonusRoom[pos.x][pos.y] = avatar;
                if (currentTile == Tileset.COIN) {
                    coins += 1;
                    currentTile = Tileset.FLOOR;
                    playAudioCollectOrb();
                }
            }
            bigWorld.world[pos.x][pos.y] = avatar;
            if (currentTile == Tileset.ORB) {
                orbs += 1;
                currentTile = Tileset.FLOOR;
                playAudioCollectOrb();

            }
        }

        public void interactKey() {
            if (avatar == Tileset.AVATAR) {
                pickUpKey();
            } else {
                placeKey();
            }
        }
        public void pickUpKey() {
            if (currentTile == Tileset.KEY && avatar != Tileset.AVATARWITHKEY) {
                currentTile = Tileset.FLOOR;
                avatar = Tileset.AVATARWITHKEY;
                playAudioPickUpKey();
            }
        }
        public void placeKey() {
            if (currentTile == Tileset.FLOOR) {
                currentTile = Tileset.KEY;
                avatar = Tileset.AVATAR;
                playAudioPlaceKey();
            } else if (currentTile == Tileset.ZONE) {
                avatar = Tileset.AVATAR;
                keysPlaced++;
                playAudioPlaceKeyInZone();

            } if (keysPlaced == keys) {
                spawnTeleporter();
                playAudioSpawnTeleporter();
            }
        }
        public boolean canTeleport() {
            if (inBonusRoom) {
                // look up to two tiles ahead in bonusRoom
                if (directionFacing == 0) {            // up
                    Position one = bigWorld.getNorthPos(pos);
                    Position two = bigWorld.getNorthPos(one);
                    return bonusRoom[one.x][one.y] == Tileset.WALL
                            || bonusRoom[two.x][two.y] == Tileset.WALL;
                } else if (directionFacing == 1) {     // down
                    Position one = bigWorld.getSouthPos(pos);
                    Position two = bigWorld.getSouthPos(one);
                    return bonusRoom[one.x][one.y] == Tileset.WALL
                            || bonusRoom[two.x][two.y] == Tileset.WALL;
                } else if (directionFacing == 2) {     // left
                    Position one = bigWorld.getWestPos(pos);
                    Position two = bigWorld.getWestPos(one);
                    return bonusRoom[one.x][one.y] == Tileset.WALL
                            || bonusRoom[two.x][two.y] == Tileset.WALL;
                } else {                               // right
                    Position one = bigWorld.getEastPos(pos);
                    Position two = bigWorld.getEastPos(one);
                    return bonusRoom[one.x][one.y] == Tileset.WALL
                            || bonusRoom[two.x][two.y] == Tileset.WALL;
                }
            } else {
                if (directionFacing == 0) {
                    return bigWorld.getTile(bigWorld.getNorthPos(pos)) == Tileset.WALL
                            || bigWorld.getTile(bigWorld.getNorthPos(bigWorld.getNorthPos(pos))) == Tileset.WALL;
                } else if (directionFacing == 1) {
                    return bigWorld.getTile(bigWorld.getSouthPos(pos)) == Tileset.WALL
                            || bigWorld.getTile(bigWorld.getSouthPos(bigWorld.getSouthPos(pos))) == Tileset.WALL;
                } else if (directionFacing == 2) {
                    return bigWorld.getTile(bigWorld.getWestPos(pos)) == Tileset.WALL
                            || bigWorld.getTile(bigWorld.getWestPos(bigWorld.getWestPos(pos))) == Tileset.WALL;
                } else {
                    return bigWorld.getTile(bigWorld.getEastPos(pos)) == Tileset.WALL
                            || bigWorld.getTile(bigWorld.getEastPos(bigWorld.getEastPos(pos))) == Tileset.WALL;
                }
            }
        }
        public void teleport() {
            if (orbs >= 5 && canTeleport()) {
                Position newPos = bigWorld.getNextFloorAfterWall(directionFacing, pos, inBonusRoom, bonusRoom);
                if (bigWorld.inBounds(newPos)) {
                    bigWorld.world[pos.x][pos.y] = currentTile;
                    pos = newPos;
                    currentTile = bigWorld.getTile(pos);
                    orbs -= 5;
                    playAudioTeleport();
                }
            }
        }
        public void moveUpUntilWall() {
            if (orbs >= 2) {
                int maxTP = 8;
                for (int i = 0; i < maxTP; i++) {
                    if (directionFacing == 0) {
                        w();
                    } else if (directionFacing == 1) {
                        s();
                    } else if (directionFacing == 2) {
                        a();
                    } else if (directionFacing == 3) {
                        d();
                    }
                }
                orbs -= 2;
                playAudioDash();
            }

        }
    }

    public class Entity {
        Position pos;
        Position prev;
        TETile currentTile;
        TETile avatar;
        boolean caught;
        boolean visualize;
        ArrayList<Tile> currPath;
        ArrayList<Tile> prevPath;
        int directionFacing; //0 = up, 1 = down, 2 = left, 3 = right

        public Entity(Position pos, TETile currentTile) {
            this.pos = pos;
            this.currentTile = currentTile;
            this.avatar = Tileset.FLOWER;
            this.directionFacing = 0;
            this.caught = false;
            this.visualize = false;
            currPath = new ArrayList<>();
            prevPath = new ArrayList<>();
            this.prev = pos;
        }

        public void updateWorld() {
            bigWorld.world[pos.x][pos.y] = avatar;
            prevPath = currPath;
            if (currPath.size() == 1) {
                caught = true;
            }

        }

        public void whereToGo(Player p) {
            Tile enemy = new Tile(this.pos, this.currentTile);
            Tile player = new Tile(p.pos, p.currentTile);

            PriorityQueue<Tile> fringe = new PriorityQueue<>();
            ArrayList<Tile> open = new ArrayList<>();
            ArrayList<Tile> visited = new ArrayList<>();

            enemy.g = 0;
            enemy.h = distance(enemy, player);
            enemy.f = enemy.g + enemy.h;
            open.add(enemy);
            fringe.add(enemy);

            while (!fringe.isEmpty()) {
                Tile curr = fringe.poll();
                visited.add(curr);

                if (curr.x == player.x && curr.y == player.y) {
                    pathGen(enemy, curr);
                    return;
                }

                for (Tile t: next(curr)) {
                    if (visited.contains(t)) {
                        continue;
                    }
                    int d = distance(t, curr);
                    int temp = curr.g + d;
                    int nextG = t.g;
                    int i = open.indexOf(t);

                    t.prev = curr;
                    t.g = temp;
                    t.h = distance(t, player);
                    t.f = t.g + t.h;

                    if (open.contains(t)) {
                        if (temp < nextG) {
                            open.set(i, t);
                        }
                    } else {
                        fringe.add(t);
                        open.add(t);
                    }
                }
            }
            System.out.println("uh oh");
        }

        private ArrayList<Tile> next(Tile t) {
            ArrayList<Tile> bob = new ArrayList<>();
            int x = t.x;
            int y = t.y;

            Position w = new Position (x,y+1);
            if (bigWorld.inBounds(w) && bigWorld.getTile(w) != Tileset.WALL) {
                bob.add(new Tile(w, bigWorld.getTile(w)));
            }

            Position s = new Position (x,y-1);
            if (bigWorld.inBounds(s) && bigWorld.getTile(s) != Tileset.WALL) {
                bob.add(new Tile(s, bigWorld.getTile(s)));
            }

            Position a = new Position (x-1,y);
            if (bigWorld.inBounds(a) && bigWorld.getTile(a) != Tileset.WALL) {
                bob.add(new Tile(a, bigWorld.getTile(a)));
            }

            Position d = new Position (x+1,y);
            if (bigWorld.inBounds(d) && bigWorld.getTile(d) != Tileset.WALL) {
                bob.add(new Tile(d, bigWorld.getTile(d)));
            }

//            System.out.println(bob);
            return bob;
        }

        public int distance(Tile a, Tile b) {
            return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
        }

        public ArrayList<Tile> pathGen(Tile e, Tile p) {
            ArrayList<Tile> path = new ArrayList<>();
            Tile curr = p;

            while (curr != null && !(curr.x == e.x && curr.y == e.y)) {
                path.add(curr);
                curr = curr.prev;
            }
            Collections.reverse(path);
            System.out.println("Path found with " + path.size() + " steps:");

            currPath = path;
            return path;
        }

        public void drawPath(ArrayList<Tile> prev, ArrayList<Tile> curr) {
            for (Tile t : prev) {
                if (t.equals(curr.getLast())) {
                    continue;
                }
                bigWorld.world[t.x][t.y] = Tileset.FLOOR;
            }

            for (Tile t : curr) {
                if (t.equals(curr.getLast())) {
                    continue;
                }
                if (visualize) {
                    bigWorld.world[t.x][t.y] = Tileset.TREE;
                } else {
                    bigWorld.world[t.x][t.y] = Tileset.FLOOR;
                }
            }
        }

        public void move() {
            prev = pos;
            int x = currPath.getFirst().x;
            int y = currPath.getFirst().y;
            Position update = new Position(x,y);
            TETile newTile = bigWorld.getTile(update);

            bigWorld.world[pos.x][pos.y] = currentTile;
            currentTile = newTile;

            pos = update;
            prevPath = currPath;
            currPath.removeFirst();
        }
    }

    public static class Tile implements Comparable<Tile> {
        int x;
        int y;
        int f;
        int g;
        int h;
        Tile prev;
        public final TETile currentTile;

        public Tile(Position pos, TETile currentTile){
            x = pos.x;
            y = pos.y;
            f = Integer.MAX_VALUE;
            g = Integer.MAX_VALUE;
            h = 0;
            this.currentTile = currentTile;
            prev = null;
        }

        @Override
        public int compareTo(Tile o) {
            return this.f - o.f;
        }

        @Override
        public boolean equals(Object o) {
            Tile tile = (Tile) o;
            return this.x == tile.x && this.y == tile.y;
        }
    }

    public Interactivity() {
        this.bigWorld = null;
        this.player = null;
        this.entity = null;
        try {
            // pre‐load the menu music once
            AudioInputStream ais = AudioSystem
                    .getAudioInputStream(new File("./src/core/audio/menu_music.wav"));
            menuClip = AudioSystem.getClip();
            menuClip.open(ais);
            // prepare it to loop forever
            menuClip.loop(Clip.LOOP_CONTINUOUSLY);
            if (menuClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) menuClip.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(-5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            AudioInputStream bgStream =
                    AudioSystem.getAudioInputStream(new File("./src/core/audio/background.wav"));
            worldClip = AudioSystem.getClip();
            worldClip.open(bgStream);
            if (worldClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) worldClip.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(-10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mainMenu() {

        if (menuClip != null) {
            menuClip.setFramePosition(0);
            menuClip.start();
        }

        TERenderer ter = new TERenderer();
        ter.initialize(106, 58);
        StdDraw.setPenColor(Color.WHITE);
        int middle = 106 / 2;
        String text1 = "Lights Off";
        String text2 = "New Game (N)";
        String text3 = "Load Game (L)";
        String text4 = "Quit (Q)";
        Font font = new Font("Sans Serif", Font.BOLD, 40);
        StdDraw.setFont(font);
        StdDraw.text(middle, 4./5 * 58, text1);
        font = new Font("Sans Serif", Font.PLAIN, 20);
        StdDraw.setFont(font);
        StdDraw.text(middle, 3./5 * 58, text2);
        StdDraw.text(middle, 2.5/5 * 58, text3);
        StdDraw.text(middle, 2./5 * 58, text4);

        StdDraw.show();

        askInputMenu();
    }
    public void askInputMenu() {
        char c;
        while (true) {
            while (StdDraw.hasNextKeyTyped()) {
                c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);
                switch (c) {
                    case 'q':System.exit(0);
                        break;
                    case 'n':
                        askInputSeed();
                        break;
                    case 'l':
                        load();
                        gameLoopInteractivity();
                        break;
                    default:
                        break;
                }
            }
        }
    }
    public void seedScreen() {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.WHITE);
        int middle = 106 / 2;
        String text1 = "Enter Seed:";
        Font font = new Font("Sans Serif", Font.PLAIN, 24);
        StdDraw.setFont(font);
        StdDraw.text(middle, 3./5 * 58, text1);
        StdDraw.show();
    }
    public void askInputSeed() {
        if (menuClip != null && !menuClip.isRunning()) {
            menuClip.start();
        }
        seedScreen();
        StringBuilder seed = new StringBuilder();
        String str;
        char c;
        while (true) {
            while (StdDraw.hasNextKeyTyped()) {
                seedScreen();
                Font font = new Font("Sans Serif", Font.PLAIN, 24);
                StdDraw.setFont(font);
                c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);
                if (c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == 's' || c == 'q') {
                    seed.append(c);
                }
                str = seed.toString();

                StdDraw.text(106 / 2, 58 / 2, str);
                StdDraw.show();
                if (c == 's') {
                    String result = str.substring(0,seed.length() - 1);
                    System.out.println(result);
                    if (result == "") {
                        Random temp = new Random();
                        startWorld(temp.nextLong());
                    } else {
                        startWorld(Long.parseLong(result));

                    }
                } else if (c == 'q') {
                    System.exit(0);
                }
            }
        }
    }

    public void startWorld(long seed) {
        if (menuClip != null && menuClip.isRunning()) {
            menuClip.stop();
        }

        if (worldClip != null) {
            worldClip.setFramePosition(0);
            worldClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        bigWorld = new World(seed);
        bigWorld.generateWorld();

        setZone();
        //setLights();
        spawnKeys();
        spawnOrbs();
        inBonusRoom = false;

        player = new Player(bigWorld.rooms.get(0).pos, bigWorld.getTile(bigWorld.rooms.get(0).pos));
        entity = new Entity(bigWorld.rooms.get(5).pos, bigWorld.getTile(bigWorld.rooms.get(5).pos));
        log = new Out("save.txt");
        log.println(bigWorld.seed);
        log.print('v');
        gameLoopInteractivity();
    }
    public void restart(long seed) {
        bigWorld = new World(seed);
        bigWorld.generateWorld();
        setZone();
        //setLights();
        spawnKeys();
        spawnOrbs();
        inBonusRoom = false;
        player = new Player(bigWorld.rooms.get(0).pos, bigWorld.getTile(bigWorld.rooms.get(0).pos));
        player.updateWorld();

        entity = new Entity(bigWorld.rooms.get(5).pos, bigWorld.getTile(bigWorld.rooms.get(5).pos));

        if (!inBonusRoom) {
            entity.updateWorld();
            entity.whereToGo(player);
        }

    }
    public void runStates(String str, int seed) {
        restart(seed);
        System.out.println(str);
        for (int i = 0; i < str.length(); i++) {
            entity.whereToGo(player);
            StdDraw.pause(1);
            char c = str.charAt(i);
            switch (c) {
                case 'w':
                    player.w();
                    if (!inBonusRoom && !entity.currPath.isEmpty()) {
                        entity.move();
                    }
                    break;
                case 'a':
                    player.a();
                    if (!inBonusRoom && !entity.currPath.isEmpty()) {
                        entity.move();
                    }
                    break;
                case 's':
                    player.s();
                    if (!inBonusRoom && !entity.currPath.isEmpty()) {
                        entity.move();
                    }
                    break;
                case 'd':
                    player.d();
                    if (!inBonusRoom && !entity.currPath.isEmpty()) {
                        entity.move();
                    }
                    break;
                case 'j':
                    player.interactKey();
                    break;
                case 'k':
                    player.moveUpUntilWall();
                    break;
                case 'm':
                    player.teleport();
                default:
                    break;
            }
            player.updateWorld();
            if (!inBonusRoom) {
                entity.updateWorld();
            }
        }
    }

    public String getTileFromMouse() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if (inBounds(x, y)) {
            if (inBonusRoom) {
                return bonusRoom[x][y].toString();
            } else {
                return bigWorld.world[x][y].toString();
            }
        } else {
            return "??";
        }
    }
    public String displayOrbs() {
        return "Orbs Collected: " + player.orbs;
    }
    public String displayKeysPlaced() {
        return "Keys Placed: " + player.keysPlaced + "/" + keys;
    }
    public String displayDirection() {
        if (player.directionFacing == 0) {
            return "Direction Facing: UP";
        } else if (player.directionFacing == 1) {
            return "Direction Facing: DOWN";
        } else if (player.directionFacing == 2) {
            return "Direction Facing: LEFT";
        } else {
            return "Direction Facing: RIGHT";
        }
    }
    public String displayCoins() {
        return "Coins: " + player.coins;
    }
    public String displayControls1() {
        return "Movement | W: UP | A: LEFT | S: DOWN | D: RIGHT";
    }
    public String displayControls2() {
        return "Abilities | K: DASH | M: TELEPORT | J: PICK UP OR PLACE KEY";
    }
    public String displayControls3() {
        return "Misc | Z: CHEAT | X: SEE ENTITY PATH | V: SAVE | L: LOAD | R: RESTART | Q: QUIT";
    }
    public void updateHUD() {
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Sans Serif", Font.PLAIN, 16);
        StdDraw.setFont(font);
        StdDraw.textRight(bigWorld.windowWidth - 3, bigWorld.windowHeight - 4, getTileFromMouse());
        StdDraw.textLeft(bigWorld.windowWidth - 12, bigWorld.windowHeight - 2, displayOrbs());
        StdDraw.textLeft(bigWorld.windowWidth - 23, bigWorld.windowHeight - 2, displayKeysPlaced());
        StdDraw.textLeft(bigWorld.windowWidth - 37, bigWorld.windowHeight - 2, displayDirection());
        StdDraw.textLeft(bigWorld.windowWidth - 45, bigWorld.windowHeight - 2, displayCoins());
        font = new Font("Sans Serif", Font.PLAIN, 14);
        StdDraw.setFont(font);
        StdDraw.textLeft(2, bigWorld.windowHeight - 2, displayControls3());
        StdDraw.textLeft(2, bigWorld.windowHeight - 4, displayControls1());
        StdDraw.textLeft(2, bigWorld.windowHeight - 6, displayControls2());


        StdDraw.show();
    }
    public boolean inBounds(int x, int y) {
        return x < bigWorld.world.length && y < bigWorld.world[0].length && x >= 0 && y >= 0;
    }

    public void toggleLights() {
        playAudioToggleLights();
        if (lightsOn) {
            lightsOff();
        } else {
            lightsOn();
        }
        lightsOn = !lightsOn;
    }
    public void lightsOn() {
        bigWorldLights = new TETile[gameWidth][gameHeight];
        for (int x = 0; x < bigWorldLights.length; x++) {
            for (int y = 0; y < bigWorldLights[0].length; y++) {
                bigWorldLights[x][y] = Tileset.NOTHING;
            }
        }
        for (Position pos: getLargeRadius()) {
            bigWorldLights[pos.x][pos.y] = bigWorld.getTile(pos);
        }

    }
    public void lightsOff() {
        bigWorldLights = new TETile[gameWidth][gameHeight];
        for (int x = 0; x < bigWorldLights.length; x++) {
            for (int y = 0; y < bigWorldLights[0].length; y++) {
                bigWorldLights[x][y] = Tileset.NOTHING;
            }
        }
        for (Position pos: getSmallRadius()) {
            bigWorldLights[pos.x][pos.y] = bigWorld.getTile(pos);
        }

    }
    public void fullLight() {
        bigWorldLights = new TETile[gameWidth][gameHeight];
        for (int x = 0; x < bigWorldLights.length; x++) {
            for (int y = 0; y < bigWorldLights[0].length; y++) {
                bigWorldLights[x][y] = bigWorld.world[x][y];
            }
        }
    }

    public ArrayList<Position> getSmallRadius() {
        return bigWorld.getSmallRadiusAroundPos(player.pos);
    }
    public ArrayList<Position> getLargeRadius() {
        return bigWorld.getLargeRadiusAroundPos(player.pos);
    }

    public void updateBigWorldLights() {
        if (bright) {
            fullLight();
        } else {
            if (lightsOn) {
                lightsOn();
            } else {
                lightsOff();
            }
        }
    }

    public void updateEntityPath() {
        if (entityPathOn) {
            displayPath();
        } else {
            normalPath();
        }
    }

    public void normalPath() {
        entityPath = new TETile[gameWidth][gameHeight];
        for (int x = 0; x < entityPath.length; x++) {
            for (int y = 0; y < entityPath[0].length; y++) {
                entityPath[x][y] = bigWorld.world[x][y];
            }
        }
    }

    public void displayPath() {
        Position e = entity.pos;
        Position p = player.pos;
        Position prev = entity.prev;

        entityPath[prev.x][prev.y] = bigWorld.world[prev.x][prev.y];

        for (Tile t : entity.prevPath) {
            if (t.equals(entity.currPath.getLast())) {
                continue;
            }
            entityPath[t.x][t.y] = bigWorld.world[t.x][t.y];
        }

        for (Tile t : entity.currPath) {
            if (t.equals(entity.currPath.getLast())) {
                continue;
            }
            entityPath[t.x][t.y] = Tileset.TREE;
        }

        entityPath[e.x][e.y] = Tileset.FLOWER;
        entityPath[p.x][p.y] = Tileset.AVATAR;


    }

    public void setZone() {
        int index = bigWorld.rand.nextInt(0, bigWorld.rooms.size());
        System.out.println(bigWorld.rooms.size());
        for (int i = 0; i < bigWorld.rooms.get(index).space.size(); i++) {
            int x = bigWorld.rooms.get(index).space.get(i).x;
            int y = bigWorld.rooms.get(index).space.get(i).y;
            bigWorld.world[x][y] = Tileset.ZONE;

        }
    }
    public void setLights() {
        for (int i = 0; i < lights; i++) {
            int index = bigWorld.rand.nextInt(0, bigWorld.rooms.size());
            int x = bigWorld.rooms.get(index).pos.x;
            int y = bigWorld.rooms.get(index).pos.y;
            if (bigWorld.world[x][y] == Tileset.FLOOR) {
                bigWorld.world[x][y] = Tileset.LIGHTSWITCH;
            } else {
                i--;
            }
        }
    }
    public void spawnKeys() {
        for (int i = 0; i < keys; i++) {
            int index = bigWorld.rand.nextInt(0, bigWorld.floors.size());
            int x = bigWorld.floors.get(index).x;
            int y = bigWorld.floors.get(index).y;
            if (bigWorld.world[x][y] == Tileset.FLOOR) {
                bigWorld.world[x][y] = Tileset.KEY;
            } else {
                i--;
            }
        }
    }
    public void spawnOrbs() {
        int teleports = bigWorld.rand.nextInt(minTeleports, maxTeleports);
        for (int i = 0; i < teleports; i++) {
            int index = bigWorld.rand.nextInt(0, bigWorld.floors.size());
            int x = bigWorld.floors.get(index).x;
            int y = bigWorld.floors.get(index).y;
            if (bigWorld.world[x][y] == Tileset.FLOOR) {
                bigWorld.world[x][y] = Tileset.ORB;
            }

        }
    }
    public void spawnTeleporter() {
        int index = bigWorld.rand.nextInt(0, bigWorld.floors.size());
        int x = bigWorld.floors.get(index).x;
        int y = bigWorld.floors.get(index).y;
        //spawn teleporter if on floor tile or distance from player is > 20
        if (bigWorld.world[x][y] == Tileset.FLOOR && bigWorld.getDistance(x - player.pos.x, y - player.pos.y) > 20) {
            bigWorld.world[x][y] = Tileset.TELEPORTER;
        } else {
            spawnTeleporter();
        }
    }
    public void activateTeleport() {
        bonusRoom = new TETile[gameWidth][gameHeight];
        int xstart = (int) (1.25/3 * gameWidth);
        int xend = (int) (1.75/3 * gameWidth);
        int ystart = (int) (1.25/3 * gameHeight);
        int yend = (int) (1.75/3 * gameHeight);
        //set world to empty
        for (int x = 0; x < gameWidth; x++) {
            for (int y = 0; y < gameHeight; y++) {
                bonusRoom[x][y] = Tileset.NOTHING;
            }
        }
        //build floor tiles
        for (int x = xstart; x < xend; x++) {
            for (int y = ystart; y < yend; y++) {
                bonusRoom[x][y] = Tileset.COIN;
            }
        }
        //build walls
        for (int x = xstart; x < xend; x++) {
            // bottom
            int yb = ystart - 1;
            bonusRoom[x][yb] = Tileset.WALL;
            // top
            int yt = yend;
            bonusRoom[x][yt] = Tileset.WALL;
        }
        for (int y = ystart; y < yend; y++) {
            // left
            int xl = xstart - 1;
            bonusRoom[xl][y] = Tileset.WALL;
            // right
            int xr = xend;
            bonusRoom[xr][y] = Tileset.WALL;
        }
        bigWorld.world[player.pos.x][player.pos.y] = Tileset.TELEPORTER;
        player.currentTile = Tileset.FLOOR;
        player.pos = bigWorld.getPosition(xstart, ystart);
        worldClip.stop();
        playAudioUseTeleporter();
        inBonusRoom = true;

    }
    public void playAudio(String str) {
        try {
            String path = "./src/core/audio/" + str;
            File audioFile = new File(path);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }

    }
    public void playAudioWalk() {
        playAudio("walk.wav");
    }
    public void playAudioSpawnTeleporter() {
        playAudio("end_portal_activation.wav");
    }
    public void playAudioUseTeleporter() {
        playAudio("use_teleporter.wav");
    }
    public void playAudioTeleport() {
        playAudio("teleport.wav");
    }
    public void playAudioDash() {
        playAudio("dash2.wav");
    }
    public void playAudioCollectOrb() {
        playAudio("orb.wav");
    }
    public void playAudioPickUpKey() {
        playAudio("pick_up.wav");
    }
    public void playAudioPlaceKey() {
        playAudio("pick_up.wav");
    }
    public void playAudioPlaceKeyInZone() {
        playAudio("place_key.wav");
    }
    public void playAudioToggleLights() {
        playAudio("click.wav");
    }

    public void gameLoopInteractivity() {

        long temp = -1;
        char c;
        boolean pendingExit = false;
        int count = 0;
        while (true) {
            player.updateWorld();
            if (!inBonusRoom) {
                entity.updateWorld();
                entity.whereToGo(player);
            }
//            entity.drawPath(entity.prevPath, entity.currPath);
            updateBigWorldLights();
            updateEntityPath();
            updateHUD();
            if (entity.caught) {
                mainMenu();
            }
            //if on teleporter for 2 seconds, teleport to bonus room
            if (player.currentTile == Tileset.TELEPORTER) {
                if (temp == -1) {
                    temp = System.currentTimeMillis();
                } else {
                    long elapsed = System.currentTimeMillis() - temp;
                    if (elapsed > 2000) {
                        //activateTeleport();
                        //log.print('B');
                        displayVictory();
                        temp = -1;
                    }
                }
            } else {
                temp = -1;
            }
            while (StdDraw.hasNextKeyTyped()) {
                count++;
                if (count % 15 == 0 && !inBonusRoom) {
                    toggleLights();
                }
                c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);
                if (c == 'w' || c == 'a' || c == 's' || c == 'd' || c == 'v' || c == 'j' || c == 'k' || c == 'm') {
                    log.print(c);
                }

                if (c == ':') {
                    pendingExit = true;
                    continue;
                }
                // if we saw “:” immediately before a “q”, quit
                if (pendingExit && c == 'q') {
                    log.print('v');
                    rewriteSave();
                    log.close();
                    System.exit(0);
                }
                //resets the pendingExit state if other key
                pendingExit = false;

                switch (c) {
                    case 'w':
                        player.w();
                        playAudioWalk();
                        if (!inBonusRoom) {
                            entity.move();
                        }
                        break;
                    case 'a':
                        player.a();
                        playAudioWalk();
                        if (!inBonusRoom) {
                            entity.move();
                        }
                        break;
                    case 's':
                        player.s();
                        playAudioWalk();
                        if (!inBonusRoom) {
                            entity.move();
                        }
                        break;
                    case 'd':
                        player.d();
                        playAudioWalk();
                        if (!inBonusRoom) {
                            entity.move();
                        }
                        break;
                    case 'v':
                        rewriteSave();
                        break;
                    case 'l':
                        load();
                        break;
                    case 'r':
                        restart(bigWorld.seed);
                        resetFile();
                        break;
                    case 'c':
                        toggleLights();
                        break;
                    case 'z':
                        bright = !bright;
                        break;
                    case 'j':
                        player.interactKey();
                        break;
                    case 'k':
                        player.moveUpUntilWall();
                        break;
                    case 'm':
                        player.teleport();
                        break;
                    case 'x':
                        entity.visualize = !entity.visualize;
                        entityPathOn = !entityPathOn;
                        break;
                    default:
                        break;
                }
            }
            StdDraw.pause(20);
            if (player.coins != 135) { //collected all coins
                if (inBonusRoom) {
                    bigWorld.ter.renderFrame(bonusRoom);
                } else if (entityPathOn) {
                bigWorld.ter.renderFrame(entityPath);
            } else {
                    bigWorld.ter.renderFrame(bigWorldLights);
                }
            } else {
                displayVictory();
            }
        }
    }
    public void displayVictory() {
        if (menuClip != null) {
            menuClip.setFramePosition(0);
            menuClip.start();
        }
        if (worldClip != null && worldClip.isRunning()) {
            worldClip.stop();
        }
        Font font = new Font("Sans Serif", Font.BOLD, 40);
        StdDraw.clear(Color.black);
        StdDraw.setFont(font);
        StdDraw.text(windowWidth / 2, windowHeight / 2, "YOU WIN");
        StdDraw.show();
        while (true) {
            char c;
            while (StdDraw.hasNextKeyTyped()) {
                c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);
                if (c == 'q') {
                    System.exit(0);
                }
            }
        }
    }

}
