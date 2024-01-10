package egg0109v1;

import battlecode.common.*;

public strictfp class BugNavigation {

    public static boolean bugging = false;
    
    public static boolean move(RobotController rc, MapLocation target) throws GameActionException {
        MapLocation loc = rc.getLocation();
        int currentDist = loc.distanceSquaredTo(target);

        /*if (currentDist <= 2) {
            MapInfo info = rc.senseMapInfo(target);
            Direction d = loc.directionTo(target);
            if (info.isWater()) {
                if (rc.canFill(target)) {
                    rc.fill(target);
                    return true;
                }
            }
            else if (rc.canMove(d)) {
                rc.move(d);
                return true;
            }
        }*/
        
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
                    rc.setIndicatorDot(l, 80, 60, 20);
                } else if (i.isWater()) {
                    score = 1.0 / (dist + 2);
                    rc.setIndicatorDot(l, 0, 0, 255);
                } else {
                    rc.setIndicatorDot(l, 0, 0, 0);
                    continue;
                }
                if (score > bestScore) {
                    bestScore = score;
                    bestDir = d;
                }
            } else {
                rc.setIndicatorDot(l, 255, 255, 255);
            }
        }

        rc.setIndicatorLine(rc.getLocation(), loc.add(bestDir), 0, 0, 0);

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

        //TODO: start bugging if greedy fails
        return false;
    }
}
