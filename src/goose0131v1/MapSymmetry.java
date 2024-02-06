package goose0131v1;

import battlecode.common.*;

public strictfp class MapSymmetry {

    static boolean v;
    static boolean h;
    static boolean r;

    static int width;
    static int height;

    static int[] flippedTile = {0, 2, 1, 3, 4};
    static MapLocation[] spawnLocs;

    static int[][] map;

    public static void initSymms(RobotController rc) {
        width = rc.getMapWidth();
        height = rc.getMapHeight();
        map = new int[width][height];

        int v = (rc.getTeam() == Team.A? 1 : 2);

        spawnLocs = rc.getAllySpawnLocations();

        for (MapLocation m : spawnLocs) {
            map[m.x][m.y] = v;
        }
    }

    public static void updateSymms(RobotController rc) {
        MapLocation curr = rc.getLocation();
        MapLocation m;
        int val;
        int dx;
        int dy;
        int rr;
        int rh;
        int rv;
        for (MapInfo mi : rc.senseNearbyMapInfos()) {
            m = mi.getMapLocation();
            if (m.distanceSquaredTo(curr) < 13) continue;

            if (mi.isSpawnZone()) {
                val = mi.getSpawnZoneTeam();
            } else {
                if (mi.isWall()) {
                    val = 3;
                } else {
                    val = 4;
                }
            }
            map[m.x][m.y] = val;

            dx = width - 2 * m.x - 1;
            dy = height - 2 * m.y - 1;

            rr = map[m.x+dx][m.y+dy];
            rh = map[m.x+dx][m.y];
            rv = map[m.x][m.y+dy];

            if (r && rr > 0 && flippedTile[rr] != val) {
                r = false;
            }
            if (h && rh > 0 && flippedTile[rh] != val) {
                h = false;
            }
            if (v && rv > 0 && flippedTile[rv] != val) {
                v = false;
            }
        }
    }

    public static MapLocation reflect(MapLocation m) {
        if (r) {
            return new MapLocation(width - m.x - 1, height - m.y - 1);
        }
        if (h) {
            return new MapLocation(width - m.x - 1, m.y);
        }
        if (v) {
            return new MapLocation(m.y, height - m.y - 1);
        }
        return null;
    }
}
