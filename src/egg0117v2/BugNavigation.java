package egg0117v2;

import battlecode.common.*;

public strictfp class BugNavigation {

    static class MapLocationMultiSet {
        String data;
        
        MapLocationMultiSet() {
            data = new String();
        }

        void add(MapLocation loc) {
            data += loc.toString();
        }

        int count(MapLocation loc) {
            int i = 0;
            String loc_s = loc.toString();
            int c = 0;
            while ((i = data.indexOf(loc_s, i) + 1) > 0) {
                c++;
            }
            return c;
        }

        boolean contains(MapLocation loc) {
            return data.contains(loc.toString());
        }

        void clear() {
            data = new String();
        }
    }

    public static boolean bugging = false;
    public static boolean rotateRight = false;
    public static MapLocation lastObstacle = null;
    public static MapLocation prevTarget = null;
    public static int minDist;
    public static MapLocationMultiSet visited = new MapLocationMultiSet();
    public static MapLocationMultiSet banned = new MapLocationMultiSet();
    public static int round;

    public static boolean canMove(RobotController rc, Direction d) {
        MapLocation loc = rc.adjacentLocation(d);
        return rc.canMove(d) || (/*(loc.x + loc.y) % 2 == 1 && */rc.canFill(loc));
    }

    /*public static boolean canFill(RobotController rc, MapLocation l) throws GameActionException {
        if (!rc.canFill(l)) return false;
        if ((l.x + l.y) % 2 == 1) return true;
        MapInfo mN = rc.senseLocation(l.add(Direction.NORTH));
        MapInfo mE = rc.senseLocation(l.add(Direction.EAST));
        MapInfo mS = rc.senseLocation(l.add(Direction.SOUTH));
        MapInfo mW = rc.senseLocation(l.add(Direction.WEST));
        return mN.isWall() || mN.isDam()
                || mE.isWall() || mE.isDam()
                || mS.isWall() || mS.isDam()
                || mW.isWall() || mW.isDam();
    }*/

    public static boolean tryMove(RobotController rc, Direction d) throws GameActionException {
        if (rc.canMove(d)) {
            rc.move(d);
            return true;
        }
        MapLocation l = rc.adjacentLocation(d);
        if (/*(l.x + l.y) % 2 == 1 && */rc.canFill(l)) {
            rc.fill(l);
            return true;
        }
        return false;
    }
    
    /**
     * Completely reset the bugnav, use when respawning.
     */
    public static void reset() {
        prevTarget = null;
        lastObstacle = null;
        minDist = 1000000;
        bugging = false;
        visited.clear();
        banned.clear();
    }

    public static void clearBanned() {
        banned.clear();
    }
    
    public static void reset(MapLocation loc, MapLocation target) {
        lastObstacle = null;
        minDist = loc.distanceSquaredTo(target);
        bugging = false;
        visited.clear();
    }

    public static boolean move(RobotController rc, MapLocation target) throws GameActionException {
        return move(rc, target, false, true);
    }

    public static boolean move(RobotController rc, MapLocation target, boolean persistence) throws GameActionException {
        return move(rc, target, persistence, true);
    }
    
    public static boolean move(RobotController rc, MapLocation target, boolean persistence, boolean fill) throws GameActionException {
        MapLocation loc = rc.getLocation();
        round = rc.getRoundNum();
        if (persistence && bugging) {
            /*if (prevTarget == null) {
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
            }*/

            double dot = (prevTarget.x - loc.x) * (target.x - loc.x) + (prevTarget.y - loc.y) * (target.y - loc.y);
            double cos2 = dot * dot / prevTarget.distanceSquaredTo(loc) / target.distanceSquaredTo(loc);
            if (prevTarget == null || target.distanceSquaredTo(loc) < minDist || cos2 < 0.5) {
                prevTarget = target;
                reset(loc, target);
            } else {
                target = prevTarget;
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
        if (dist < minDist) {
            if (canMove(rc, dirTarget)) reset(loc, target);
            minDist = dist;
        }
        else if (bugging) {
            Direction dirObstacle = loc.directionTo(lastObstacle);
            if (canMove(rc, dirObstacle)) {reset(loc, target);}
        }

        MapLocation next;
        MapInfo mi;
        if (bugging) {
            Direction dirObstacle = loc.directionTo(lastObstacle);
            next = loc.add(dirObstacle);
            if (rc.onTheMap(next)) {
                mi = rc.senseMapInfo(next);
                if (round > 100 && mi.isDam()) {
                    // Wait for dam to come down;
                    return true;
                }
            }
        } else {
            next = loc.add(dirTarget);
            if (rc.onTheMap(next)) {
                mi = rc.senseMapInfo(next);
                if (round > 100 && mi.isDam()) {
                    // Wait for dam to come down;
                    return true;
                }
            }

        }



        if (visited.count(loc) >= 2) {
            MapLocation l;
            for (Direction d : RobotPlayer.allDirections) {
                l = target.add(d);
                if (!banned.contains(l)) {
                    banned.add(l);
                }
            }
        } else {
            visited.add(loc);
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

    public static boolean isBanned(MapLocation loc) {
        return banned.contains(loc);
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
