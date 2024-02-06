package duck0127v3;

import battlecode.common.*;
import java.util.Random;

public strictfp class Explore {
    public static int[][] roundSeen;

    public static final int NEW_EXPLORE_DIST = 4;
    public static final int NEW_EXPLORE_ROUNDS = 60;

    public static int width;
    public static int height;

    public static MapLocation target;
    public static int startRound;

    public Explore(RobotController rc) {
        width = rc.getMapWidth();
        height = rc.getMapHeight();
        roundSeen = new int[width][height];
        target = null;
        for (MapLocation l : rc.getAllySpawnLocations()) {
            roundSeen[l.x][l.y] = 2000;
        }
    }

    public void update(RobotController rc) throws GameActionException {
        int round = rc.getRoundNum();
        for (MapLocation l : rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), GameConstants.VISION_RADIUS_SQUARED)) {
            roundSeen[l.x][l.y] = round;
        }
    }

    public void resetExploreTarget(RobotController rc, Random rng) {
        int bestX = rng.nextInt(width);
        int bestY = rng.nextInt(height);
        int bestRound = roundSeen[bestX][bestY];
        
        int randomX;
        int randomY;
        for (int i = 10; i-->0;) {
            randomX = rng.nextInt(width);
            randomY = rng.nextInt(height);
            if (roundSeen[randomX][randomY] < bestRound) {
                bestX = randomX;
                bestY = randomY;
                bestRound = roundSeen[bestX][bestY];
            }
        }
        target = new MapLocation(bestX, bestY);
        startRound = rc.getRoundNum();
    }

    public MapLocation getExploreTarget(RobotController rc, Random rng) throws GameActionException {
        MapLocation loc = rc.getLocation();

        if (target == null || loc.isWithinDistanceSquared(target, NEW_EXPLORE_DIST) || startRound + NEW_EXPLORE_ROUNDS < rc.getRoundNum()) {
            resetExploreTarget(rc, rng);
        }

        return target;
    }

    public MapLocation getLeastRecentlyVisitedWithinRadius(RobotController rc, MapLocation center, int radius) throws GameActionException {
        double bestScore = 1000000;
        MapLocation best = null;

        double score;

        int rsq = radius*radius;
        int bestX;
        int bestY;
        MapLocation m;
        int locs = 0;
        int totalcons = 0;
        for (int i = -radius; i <= radius; i += 3) {
            for (int j = -radius; j <= radius; j += 3) {
                totalcons++;
                if (i*i+j*j > rsq) continue;
                m = center.translate(i, j);

                if (!rc.onTheMap(m)) continue;
                locs++;
                score = roundSeen[m.x][m.y] - 1.0 / (1 + m.distanceSquaredTo(center));
                if (score < bestScore) {
                    bestScore = score;
                    best = m;
                }
            }
        }
        if (best == null) System.out.println("WTF?");
        return best;
    }

    public int getRoundVisited(MapLocation loc) {
        return roundSeen[loc.x][loc.y];
    }
}
