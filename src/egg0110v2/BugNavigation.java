package egg0110v2;

import battlecode.common.*;

public strictfp class BugNavigation {

    public static boolean bugging = false;
    public static boolean rotateRight = false;
    public static MapLocation lastObstacle = null;
    public static MapLocation prevTarget = null;
    public static int minDist;

    public static boolean canMove(RobotController rc, Direction d) {
        MapLocation loc = rc.adjacentLocation(d);
        return rc.canMove(d) || rc.canFill(loc);
    }

    public static boolean tryMove(RobotController rc, Direction d) throws GameActionException {
        if (rc.canMove(d)) {
            rc.move(d);
            return true;
        }
        MapLocation l = rc.adjacentLocation(d);
        if (rc.canFill(l)) {
            rc.fill(l);
            return true;
        }
        return false;
    }

    public static void reset() {
        prevTarget = null;
        lastObstacle = null;
        minDist = 1000000;
        bugging = false;
    }

    public static void reset(MapLocation loc, MapLocation target) {
        lastObstacle = null;
        minDist = loc.distanceSquaredTo(target);
        bugging = false;
    }

    public static boolean move(RobotController rc, MapLocation target) throws GameActionException {
        return move(rc, target, false);
    }
    
    public static boolean move(RobotController rc, MapLocation target, boolean persistence) throws GameActionException {
        MapLocation loc = rc.getLocation();
        if (persistence && bugging) {
            if (prevTarget == null) {
                prevTarget = target;
                reset(loc, target);
            } else {
                double dot = (prevTarget.x - loc.x) * (target.x - loc.x) + (prevTarget.y - loc.y) * (target.y - loc.y);
                double cos2 = dot * dot / prevTarget.distanceSquaredTo(loc) / target.distanceSquaredTo(loc);
                if (cos2 > 0.5) {
                    target = prevTarget;
                } else {
                    prevTarget = target;
                    reset(loc, target);
                }
            }
        } else {
            if (prevTarget == null || !target.equals(prevTarget)) {
                prevTarget = target;
                reset(loc, target);
            }
        }
        rc.setIndicatorLine(loc, target, 255, 0, 0);
        Direction dirTarget = loc.directionTo(target);
        
        int dist = loc.distanceSquaredTo(target);
        if (dist < minDist && canMove(rc, dirTarget)) {reset(loc, target);}
        else if (bugging) {
            Direction dirObstacle = loc.directionTo(lastObstacle);
            if (canMove(rc, dirObstacle)) {reset(loc, target);}
        }

        // Try greedy, or else start bugging
        if (!bugging) {
            if (tryMove(rc, dirTarget)) {
                return true;
            }
            bugging = true;

            Direction dr = loc.directionTo(target);
            MapLocation obstacleR = loc.add(dr);
            for (int i = 4; i-->0;) {
                if (canMove(rc, dr)) break;
                obstacleR = loc.add(dr);
                dr = dr.rotateRight();
            }
            
            rc.setIndicatorString("START BUG");

            Direction dl = loc.directionTo(target);
            MapLocation obstacleL = loc.add(dl);
            for (int i = 4; i-->0;) {
                if (canMove(rc, dl)) break;
                obstacleL = loc.add(dl);
                dl = dl.rotateLeft();
            }

            if (loc.add(dl).distanceSquaredTo(target) <= loc.add(dr).distanceSquaredTo(target)) {
                rotateRight = false;
                lastObstacle = obstacleL;
                return tryMove(rc, dl);
            } else {
                rotateRight = true;
                lastObstacle = obstacleR;
                return tryMove(rc, dr);
            }
        }

        if (rotateRight) {
            rc.setIndicatorString("R");

        } else {
            rc.setIndicatorString("L");
        }

        Direction dir = loc.directionTo(lastObstacle);
        MapLocation nextLoc;
        for (int i = 8; i-->0;) {
            if (canMove(rc, dir)) return tryMove(rc, dir);
            nextLoc = loc.add(dir);
            if (!rc.onTheMap(nextLoc)) rotateRight = !rotateRight;
            else lastObstacle = nextLoc;
            if (rotateRight) dir = dir.rotateRight();
            else dir = dir.rotateLeft();
        }
        return tryMove(rc, dir);
    }

    public static boolean greedy(RobotController rc, MapLocation target) throws GameActionException {
        MapLocation loc = rc.getLocation();
        int currentDist = loc.distanceSquaredTo(target);

        Direction bestDir = Direction.CENTER;
        double bestScore = 0;

        double score;
        MapLocation l;
        MapInfo i;
        int dist;
        for (Direction d : RobotPlayer.directions) {
            l = loc.add(d);
            if (!rc.onTheMap(l)) continue;
            i = rc.senseMapInfo(l);
            dist = l.distanceSquaredTo(target);
            if (dist < currentDist) {
                if (rc.canMove(d)) {
                    score = 1.0 / (dist + 2) + 1;
                } else if (i.isWater()) {
                    score = 1.0 / (dist + 2);
                } else {
                    continue;
                }
                if (score > bestScore) {
                    bestScore = score;
                    bestDir = d;
                }
            }
        }

        if (bestScore > 1) {
            if (rc.canMove(bestDir)) {
                rc.move(bestDir);
                return true;
            }
            return false;
        } else if (bestScore > 0) {
            MapLocation fill = loc.add(bestDir);
            if (rc.canFill(fill)) {
                rc.fill(fill);
                return true;
            }
            return false;
        }
        return false;
    }
}
