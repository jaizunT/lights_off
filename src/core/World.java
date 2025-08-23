package core;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import core.Interactivity.Player;
import core.Interactivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class World {

    public static class Position {
        int x;
        int y;
        public Position (int x, int y) {
            this.x = x;
            this.y = y;
        }
        public boolean equals(Position other) {
            return this.x == other.x && this.y == other.y;
        }
    }
    public class Room {
        Position pos; //bottom left corner position
        int width;
        int height;
        ArrayList<Position> walls;
        ArrayList<Position> northWalls;
        ArrayList<Position> southWalls;
        ArrayList<Position> eastWalls;
        ArrayList<Position> westWalls;
        ArrayList<Position> interior;
        ArrayList<Position> space;

        public Room (Position pos, int width, int height) {
            this.pos = pos;
            this.width = width;
            this.height = height;
            walls = getWalls(this);
            northWalls = getNorthWalls(this);
            southWalls = getSouthWalls(this);
            eastWalls = getEastWalls(this);
            westWalls = getWestWalls(this);
            interior = getInterior(this);
            space = new ArrayList<Position>();
            addSpace();
//            System.out.println(northWalls.size() + " " + southWalls.size() + " " + eastWalls.size() + " " + westWalls.size() + " " + interior.size());
        }

        public ArrayList<Position> getWalls(Room room) {
            ArrayList<Position> walls = new ArrayList<>();
            for (int x = room.pos.x; x < room.pos.x + room.width; x++) {
                for (int y = room.pos.y; y < room.pos.y + room.height; y++) {
                    if (x == room.pos.x || y == room.pos.y || x == room.pos.x + room.width - 1 || y == room.pos.y + room.height - 1 ) {
                        walls.add(new Position(x, y));
                    }

                }
            }
            return walls;
        }
        public ArrayList<Position> getNorthWalls(Room room) {
            ArrayList<Position> walls = new ArrayList<>();
            for (int x = room.pos.x; x < room.pos.x + room.width; x++) {
                walls.add(new Position(x, room.pos.y + room.height - 1));
                }
            return walls;

        }
        public ArrayList<Position> getSouthWalls(Room room) {
            ArrayList<Position> walls = new ArrayList<>();
            for (int x = room.pos.x; x < room.pos.x + room.width; x++) {
                walls.add(new Position(x, room.pos.y));
            }
            return walls;
        }
        public ArrayList<Position> getWestWalls(Room room) {
            ArrayList<Position> walls = new ArrayList<>();
            for (int y = room.pos.y; y < room.pos.y + room.height; y++) {
                walls.add(new Position(room.pos.x, y));
            }
            return walls;

        }
        public ArrayList<Position> getEastWalls(Room room) {
            ArrayList<Position> walls = new ArrayList<>();
            for (int y = room.pos.y; y < room.pos.y + room.height; y++) {
                walls.add(new Position(room.pos.x + room.width - 1, y));
            }
            return walls;

        }
        public ArrayList<Position> getInterior(Room room) {
            ArrayList<Position> tiles = new ArrayList<>();
            for (int x = room.pos.x + 1; x < room.pos.x + room.width - 1; x++) {
                for (int y = room.pos.y + 1; y < room.pos.y + room.height - 1; y++) {
                    tiles.add(new Position(x, y));
                }
            }
            return tiles;
        }

        public void addSpace() {
            space.addAll(walls);
            space.addAll(interior);
        }


    }
    Random rand;
    long seed;
    public final int windowWidth = 106;
    public final int windowHeight = 58;
    public final int gameWidth = 106;
    public final int gameHeight = 53;
    int K = 25;

    TERenderer ter;
    TETile[][] world;
    ArrayList<Room> rooms;
    ArrayList<Position> floors;

    public World(long seed) {
        this.seed = seed;
        rand = new Random(seed);
        rooms = new ArrayList<>();
        floors = new ArrayList<>();

    }

    public void generateWorld() {
        ter = new TERenderer();
        ter.initialize(windowWidth, windowHeight);

        // initialize tiles
        world = new TETile[gameWidth][gameHeight];
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        for (int i = 0; i < K; i++) {
            generateRoom();
        }

        buildHallways();
        buildWalls();

        for (int x = 0; x < gameWidth; x++) {
            for (int y = 0; y < gameHeight; y++) {
                if (getTile(new Position(x, y)) == Tileset.FLOOR) {
                    floors.add(new Position(x, y));
                }
            }
        }

    }

    public void gameLoop() {

            char c;
            int count = 0;
            while (true) {
                while (StdDraw.hasNextKeyTyped()) {
                    c = StdDraw.nextKeyTyped();
                    c = Character.toLowerCase(c);

                    switch (c) {
                        case 'n':
                            generateRoom();
//                            buildWalls();
                            count++;
                            break;
                        case 'z':
                            buildHallways();
//                            buildWalls();
                            break;
                        case 'x':
                            buildWalls();
                            break;
                        case 'q':
                            System.exit(0);
                            break;
                        case 'w':
                            //w
                            break;
                        case 'a':
                            //a
                            break;
                        case 's':
                            //s
                            break;
                        case 'd':
                            //d
                            break;
                        default:
                            break;
                    }

                }
                String text = "Number of squares: " + count;
                StdDraw.textLeft(1, 45, text);
                StdDraw.show();
                StdDraw.pause(20);
                ter.renderFrame(world);

            }


    }
    public void generateRoom() {
        int width = rand.nextInt(5,10);
        int height = rand.nextInt(5, 10);
        int startX = rand.nextInt(0, world.length);
        int startY = rand.nextInt(0, world[0].length);

        Room room = new Room(new Position(startX, startY), width, height);
        if (!intrudesBoundary(room) && !overlaps(room)) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Position pos = new Position(startX + x, startY + y);
                    if (inBounds(pos)) {
                        if (pos.x == startX || pos.y == startY || pos.x == startX + width - 1 || pos.y == startY + height - 1) {
                            world[pos.x][pos.y] = Tileset.FLOOR;
                        } else {
                            world[pos.x][pos.y] = Tileset.FLOOR;
                        }
                    }
                }
            }
            if (rooms.size() != 0) {
                int randomRoomIndex = rand.nextInt(0, rooms.size());
                Room randomRoom = rooms.get(randomRoomIndex);
                rooms.add(room);

                if (!interiorConnected(room, randomRoom)) {
//                    connectRooms(room, rooms.get(randomRoomIndex));
//                    buildWalls();
                }
            } else {
                rooms.add(room);

            }

        }
    }

    public void connectRooms(Room roomA, Room roomB) {
        int Ax = roomA.pos.x;
        int Ay = roomA.pos.y;
        int Bx = roomB.pos.x;
        int By = roomB.pos.y;
        boolean north = false;
        boolean south = false;
        boolean west = false;
        boolean east = false;
        int iDirection = rand.nextInt(0,2);

        if (Ax < Bx) {
            east = true;
        } else if (Ax > Bx) {
            west = true;
        }

        if (Ay < By) {
            north = true;
        } else if (Ay > By) {
            south = true;
        }

        if (iDirection == 0) {
            if (north) {
                int n = rand.nextInt(1, roomA.northWalls.size()-1);
                Position nn = roomA.northWalls.get(n);
                if (east) {
                    int w = rand.nextInt(1, roomB.westWalls.size()-1);
                    Position ww = roomB.westWalls.get(w);
                    for (int i = nn.y; i < ww.y+1; i++) {
                        world[nn.x][i] = Tileset.FLOOR;
                    }
                    for (int j = nn.x; j < ww.x+1; j++) {
                        world[j][ww.y] = Tileset.FLOOR;
                    }
                } else if (west) {
                    int e = rand.nextInt(1, roomB.eastWalls.size()-1);
                    Position ee = roomB.eastWalls.get(e);
                    for (int i = nn.y; i < ee.y+1; i++) {
                        world[nn.x][i] = Tileset.FLOOR;
                    }
                    for (int j = ee.x; j < nn.x+1; j++) {
                        world[j][ee.y] = Tileset.FLOOR;
                    }
                } else {
                    int s = rand.nextInt(1, Math.min(roomA.northWalls.size(), roomB.southWalls.size())-1);
                    Position ss = roomB.southWalls.get(s);
                    for (int i = nn.y; i < ss.y+1; i++) {
                        world[ss.x][i] = Tileset.FLOOR;
                    }
                }

            } else if (south) {
                int s = rand.nextInt(1, roomA.southWalls.size()-1);
                Position ss = roomA.southWalls.get(s);
                if (east) {
                    int w = rand.nextInt(1, roomB.westWalls.size()-1);
                    Position ww = roomB.westWalls.get(w);
                    for (int i = ww.y; i < ss.y+1; i++) {
                        world[ss.x][i] = Tileset.FLOOR;
                    }
                    for (int j = ss.x; j < ww.x+1; j++) {
                        world[j][ww.y] = Tileset.FLOOR;
                    }
                } else if (west) {
                    int e = rand.nextInt(1, roomB.eastWalls.size()-1);
                    Position ee = roomB.eastWalls.get(e);
                    for (int i = ee.y; i < ss.y+1; i++) {
                        world[ss.x][i] = Tileset.FLOOR;
                    }
                    for (int j = ee.x; j < ss.x+1; j++) {
                        world[j][ee.y] = Tileset.FLOOR;
                    }
                } else {
                    int n = rand.nextInt(1, Math.min(roomA.southWalls.size(), roomB.northWalls.size())-1);
                    Position nn = roomB.southWalls.get(n);
                    for (int i = nn.y; i < ss.y+1; i++) {
                        world[ss.x][i] = Tileset.FLOOR;
                    }
                }
            }
        } else if (iDirection == 1) {
            if (west) {
                int w = rand.nextInt(1, roomA.westWalls.size()-1);
                Position ww = roomA.westWalls.get(w);
                if (south) {
                    int n = rand.nextInt(1, roomB.northWalls.size()-1);
                    Position nn = roomB.northWalls.get(n);
                    for (int i = nn.y; i < ww.y+1; i++) {
                        world[nn.x][i] = Tileset.FLOOR;
                    }
                    for (int j = nn.x; j < ww.x+1; j++) {
                        world[j][ww.y] = Tileset.FLOOR;
                    }
                } else if (north) {
                    int s = rand.nextInt(1, roomB.southWalls.size()-1);
                    Position ss = roomB.southWalls.get(s);
                    for (int i = ww.y; i < ss.y+1; i++) {
                        world[ss.x][i] = Tileset.FLOOR;
                    }
                    for (int j = ss.x; j < ww.x+1; j++) {
                        world[j][ww.y] = Tileset.FLOOR;
                    }
                } else {
                    int x = rand.nextInt(1, Math.min(roomA.westWalls.size(), roomB.eastWalls.size())-1);
                    Position xx = roomB.eastWalls.get(x);
                    for (int i = xx.x; i < ww.x+1; i++) {
                        world[i][ww.y] = Tileset.FLOOR;
                    }
                }

            } else if (east) {
                int e = rand.nextInt(1, roomA.eastWalls.size()-1);
                Position ee = roomA.eastWalls.get(e);
                if (south) {
                    int n = rand.nextInt(1, roomB.northWalls.size()-1);
                    Position nn = roomB.northWalls.get(n);
                    for (int i = nn.y; i < ee.y+1; i++) {
                        world[nn.x][i] = Tileset.FLOOR;
                    }
                    for (int j = ee.x; j < nn.x+1; j++) {
                        world[j][ee.y] = Tileset.FLOOR;
                    }
                } else if (north) {
                    int s = rand.nextInt(1, roomB.southWalls.size()-1);
                    Position ss = roomB.southWalls.get(s);
                    for (int i = ee.y; i < ss.y+1; i++) {
                        world[ss.x][i] = Tileset.FLOOR;
                    }
                    for (int j = ee.x; j < ss.x+1; j++) {
                        world[j][ee.y] = Tileset.FLOOR;
                    }
                } else {
                    int x = rand.nextInt(1, Math.min(roomA.eastWalls.size(), roomB.westWalls.size())-1);
                    Position xx = roomB.westWalls.get(x);
                    for (int i = ee.x; i < xx.x+1; i++) {
                        world[i][ee.y] = Tileset.FLOOR;
                    }
                }
            }
        }
    }

    public void connectRooms2(Room roomA, Room roomB) {
//        System.out.println("hallway generated");
        int Ax = roomA.pos.x;
        int Ay = roomA.pos.y;
        int Bx = roomB.pos.x;
        int By = roomB.pos.y;
        boolean north = false;
        boolean south = false;
        boolean west = false;
        boolean east = false;

        if (Ax < Bx) {
            east = true;
        } else if (Ax > Bx) {
            west = true;
        }

        if (Ay < By) {
            north = true;
        } else if (Ay > By) {
            south = true;
        }
        if (north) {
            int n = rand.nextInt(1, roomA.northWalls.size()-1);
            Position nn = roomA.northWalls.get(n);

            if (east) {
                int w = rand.nextInt(1, roomB.westWalls.size()-1);
                Position ww = roomB.westWalls.get(w);
                for (int i = nn.y; i < ww.y+1; i++) {
                    world[nn.x][i] = Tileset.FLOOR;
                }
                for (int j = ww.x; j > nn.x-1; j--) {
                    world[j][ww.y] = Tileset.FLOOR;
                }
            } else if (west) {
                int e = rand.nextInt(1, roomB.eastWalls.size()-1);
                Position ee = roomB.eastWalls.get(e);
                for (int i = nn.y; i < ee.y+1; i++) {
                    world[nn.x][i] = Tileset.FLOOR;
                }
                for (int j = ee.x; j < nn.x+1; j++) {
                    world[j][ee.y] = Tileset.FLOOR;
                }
            } else {
                int s = rand.nextInt(1, Math.min(roomA.northWalls.size(), roomB.southWalls.size())-1);
                Position ss = roomB.southWalls.get(s);
                nn = roomA.northWalls.get(s);
                for (int i = ss.y; i < ss.y+1; i++) {
                    world[ss.x][i] = Tileset.FLOOR;
                }
                for (int j = ss.y; j > nn.y-1; j--) {
                    world[ss.x][j] = Tileset.FLOOR;
                }
            }

        } else if (south) {
            int s = rand.nextInt(1, roomA.southWalls.size()-1);
            Position ss = roomA.southWalls.get(s);

            if (east) {
                int w = rand.nextInt(1, roomB.westWalls.size()-1);
                Position ww = roomB.westWalls.get(w);
                for (int i = ss.y; i > ww.y-1; i--) {
                    world[ss.x][i] = Tileset.FLOOR;
                }
                for (int j = ww.x; j > ss.x-1; j--) {
                    world[j][ww.y] = Tileset.FLOOR;
                }
            } else if (west) {
                int e = rand.nextInt(1, roomB.eastWalls.size()-1);
                Position ee = roomB.eastWalls.get(e);
                for (int i = ss.y; i > ee.y-1; i--) {
                    world[ss.x][i] = Tileset.FLOOR;
                }
                for (int j = ee.x; j < ss.x+1; j++) {
                    world[j][ee.y] = Tileset.FLOOR;
                }
            } else {
                int n = rand.nextInt(1, Math.min(roomA.southWalls.size(), roomB.northWalls.size())-1);
                Position nn = roomB.northWalls.get(n);

                for (int i = ss.y; i > nn.y-1; i--) {
                    world[ss.x][i] = Tileset.FLOOR;
                }
            }
        } else {
            if (east) {
                int e = rand.nextInt(1, Math.min(roomA.eastWalls.size(), roomB.westWalls.size())-1);
                Position ee = roomA.eastWalls.get(e);
                Position ww = roomB.westWalls.get(e);
                for (int i = ee.x; i < ww.x+1; i++) {
                    world[i][ee.y] = Tileset.FLOOR;
                }
            } else {
                int w = rand.nextInt(1, Math.min(roomA.westWalls.size(), roomB.eastWalls.size())-1);
                Position ee = roomB.eastWalls.get(w);
                Position ww = roomA.westWalls.get(w);
                for (int i = ee.x; i < ww.x+1; i++) {
                    world[i][ee.y] = Tileset.FLOOR;
                }
            }
        }
    }

    public double getDistance2(int x, int y, int x1, int y1) {
        return Math.sqrt(Math.pow((double)x-x1, 2) + Math.pow((double)y-y1, 2));
    }

    public int closestIndex(int x, int y, ArrayList<Position> pos) {
        ArrayList<Double> distances = new ArrayList<>();
        for (Position a : pos) {
            double d = getDistance2(x, y, a.x, a.y);
            distances.add(d);
        }
        return distances.indexOf(Collections.min(distances));
    }

    public void buildHallways() {
        ArrayList<Position> p = new ArrayList<>();
        for (Room a: rooms) {
            p.add(a.pos);
        }
        int ox = 0;
        int oy = 0;

        int m = closestIndex(ox, oy, p);
        ox = p.get(m).x;
        oy = p.get(m).y;

        p.set(m, new Position(10000, 10000));

        for (int i = 0; i < p.size() - 1; i++) {
            int c = closestIndex(ox, oy, p);
            connectRooms2(rooms.get(m), rooms.get(c));
            m = c;
            p.set(m, new Position(10000, 10000));
        }

    }

    public void buildWalls() {
        for (int i = 0; i < gameWidth; i++) {
            for (int j = 0; j < gameHeight; j++) {
                Position pos = new Position(i, j);
                if (isNeighboringFloor(pos) && !isFloor(pos) || isFloor(pos) && atBoundary(pos)) {
                    makeWall(pos);
                }
            }
        }
    }

    public boolean inBounds(Position pos) {
        return pos.x < world.length && pos.y < world[0].length && pos.x >= 0 && pos.y >= 0;
    }
    public boolean atBoundary(Position pos) {
        return pos.x == world.length - 1 || pos.y == world[0].length - 1 || pos.x == 0 || pos.y == 0;
    }
    public Position getPosition(int x, int y) {
        return new Position(x, y);
    }
    public Position getWestPos(Position pos) {
        return new Position(pos.x - 1, pos.y);
    }
    public Position getEastPos(Position pos) {
        return new Position(pos.x + 1, pos.y);
    }
    public Position getNorthPos(Position pos) {
        return new Position(pos.x, pos.y + 1);
    }
    public Position getSouthPos(Position pos) {
        return new Position(pos.x, pos.y - 1);
    }
    public void makeFloor(Position pos) {
        world[pos.x][pos.y] = Tileset.FLOOR;
    }
    public void makeWall(Position pos) {
        world[pos.x][pos.y] = Tileset.WALL;
    }
    public boolean isNeighboringFloor(Position pos) {
        return isFloor(getWestPos(pos)) || isFloor(getEastPos(pos)) || isFloor(getSouthPos(pos)) || isFloor(getNorthPos(pos))
                || isFloor(getEastPos(getNorthPos(pos))) || isFloor(getWestPos(getNorthPos(pos)))
                || isFloor(getEastPos(getSouthPos(pos))) || isFloor(getWestPos(getSouthPos(pos)));
    }

    public TETile getTile(Position pos) {
        return world[pos.x][pos.y];
    }
    public TETile getTile(Position pos, TETile[][] temp) {
        return temp[pos.x][pos.y];
    }

    public boolean isFloor(Position pos) {
        return inBounds(pos) && getTile(pos).equals(Tileset.FLOOR);
    }

    public boolean interiorConnected(Room room1, Room room2) {
        for (Position pos1: room1.walls) {
            for (Position pos2: room2.walls) {
                if (pos1.equals(pos2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean overlaps(Room room) {
        for (Room test: rooms) {
            if (interiorConnected(room, test)) {
                return true;
            }
        }
        return false;
    }

    public boolean intrudesBoundary(Room room) {
        for (Position pos: room.walls) {
            if (!inBounds(pos) || atBoundary(pos)) {
                return true;
            }
        }
        return false;
    }



    public void addRandomSquare() {
        int size = rand.nextInt(3,8);
        int startX = rand.nextInt(0, world.length);
        int startY = rand.nextInt(0, world[0].length);
        int type = rand.nextInt(0,3);
        TETile tile = null;
        switch (type) {
            case 0: tile = Tileset.FLOWER;
                break;
            case 1: tile = Tileset.WALL;
                break;
            case 2: tile = Tileset.WATER;
                break;
        }
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int xpos = startX + x;
                int ypos = startY + y;
                if (inBounds(new Position(xpos, ypos))) {
                    world[xpos][ypos] = tile;
                }
            }
        }
    }

    public ArrayList<Position> getSmallRadiusAroundPos(Position pos) {
        ArrayList<Position> temp = new ArrayList<>();
        int radius = 6;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int x = pos.x + dx;
                int y = pos.y + dy;
                // only add if inside world bounds
                if (x >= 0 && x < gameWidth && y >= 0 && y < gameHeight) {
                    if (getDistance2(pos.x, pos.y, x, y) < radius) {
                        temp.add(new Position(x, y));

                    }
                }
            }
        }
        return temp;
    }
    public ArrayList<Position> getLargeRadiusAroundPos(Position pos) {
        ArrayList<Position> temp = new ArrayList<>();
        int radius = 15;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int x = pos.x + dx;
                int y = pos.y + dy;
                // only add if inside world bounds
                if (x >= 0 && x < gameWidth && y >= 0 && y < gameHeight) {
                    if (getDistance2(pos.x, pos.y, x, y) < radius) {
                        temp.add(new Position(x, y));

                    }
                }
            }
        }
        return temp;
    }

    public int getDistance(int x, int y) {
        return (int) Math.sqrt(x * 1.0 * x + y * 1.0 + y);
    }

    public Position getNextFloorAfterWall(int direction, Position pos, boolean inBonusRoom, TETile[][] bonusRoom) {
        //player can't teleport in bonus room anyway, but this is here for extra in case updating game
        if (inBonusRoom) {
            if (direction == 0) { // up
                while (getTile(pos, bonusRoom) != Tileset.WALL) {
                    pos = getNorthPos(pos);
                }
                while (inBounds(pos) && !isValidFloor(pos, inBonusRoom, bonusRoom)) {
                    pos = getNorthPos(pos);
                }
            } else if (direction == 1) { // down
                while (getTile(pos, bonusRoom) != Tileset.WALL) {
                    pos = getSouthPos(pos);
                }
                while (inBounds(pos) && !isValidFloor(pos, inBonusRoom, bonusRoom)) {
                    pos = getSouthPos(pos);
                }
            } else if (direction == 2) { // left
                while (getTile(pos, bonusRoom) != Tileset.WALL) {
                    pos = getWestPos(pos);
                }
                while (inBounds(pos) && !isValidFloor(pos, inBonusRoom, bonusRoom)) {
                    pos = getWestPos(pos);
                }
            } else if (direction == 3) { // right
                while (getTile(pos, bonusRoom) != Tileset.WALL) {
                    pos = getEastPos(pos);
                }
                while (inBounds(pos) && !isValidFloor(pos, inBonusRoom, bonusRoom)) {
                    pos = getEastPos(pos);
                }
            }
        } else {
            if (direction == 0) { // up
                while (getTile(pos) != Tileset.WALL) {
                    pos = getNorthPos(pos);
                }
                while (inBounds(pos) && !isValidFloor(pos, inBonusRoom, bonusRoom)) {
                    pos = getNorthPos(pos);
                }
            } else if (direction == 1) { // down
                while (getTile(pos) != Tileset.WALL) {
                    pos = getSouthPos(pos);
                }
                while (inBounds(pos) && !isValidFloor(pos, inBonusRoom, bonusRoom)) {
                    pos = getSouthPos(pos);
                }
            } else if (direction == 2) { // left
                while (getTile(pos) != Tileset.WALL) {
                    pos = getWestPos(pos);
                }
                while (inBounds(pos) && !isValidFloor(pos, inBonusRoom, bonusRoom)) {
                    pos = getWestPos(pos);
                }
            } else if (direction == 3) { // right
                while (getTile(pos) != Tileset.WALL) {
                    pos = getEastPos(pos);
                }
                while (inBounds(pos) && !isValidFloor(pos, inBonusRoom, bonusRoom)) {
                    pos = getEastPos(pos);
                }
            }
            }
        if (!inBounds(pos)) {
            return new Position(10000, 10000);
        } else {
            return pos;
        }
    }
    public boolean isValidFloor(Position pos, boolean inBonusRoom, TETile[][] bonusRoom) {
        if (inBonusRoom) {
            return getTile(pos, bonusRoom) == Tileset.FLOOR || getTile(pos, bonusRoom) == Tileset.LIGHTSWITCH || getTile(pos, bonusRoom) == Tileset.KEY
                    || getTile(pos, bonusRoom) == Tileset.ORB || getTile(pos, bonusRoom) == Tileset.TELEPORTER || getTile(pos, bonusRoom) == Tileset.ZONE
                    || getTile(pos, bonusRoom) == Tileset.COIN;
        } else {
            return getTile(pos) == Tileset.FLOOR || getTile(pos) == Tileset.LIGHTSWITCH || getTile(pos) == Tileset.KEY
                    || getTile(pos) == Tileset.ORB || getTile(pos) == Tileset.TELEPORTER || getTile(pos) == Tileset.ZONE
                    || getTile(pos) == Tileset.COIN;
        }

    }

}
