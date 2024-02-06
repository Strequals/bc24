package ineedtodomypdehw0201v1;

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
    public static boolean isFill;
    public static boolean shouldFill;
    public static MapLocation lastObstacle = null;
    public static MapLocation prevTarget = null;
    public static int minDist;
    public static MapLocationMultiSet visited = new MapLocationMultiSet();
    public static MapLocationMultiSet banned = new MapLocationMultiSet();
    public static int round;
    public static final int FILL_ROUND = 200;
    public static boolean[] danger = new boolean[8];
    public static boolean prevDanger = false;
    public static boolean bugging_danger = false;
    public static MapLocation center = null;

    public static int failed = 0;

    public static int canMove(RobotController rc, Direction d) {
        MapLocation loc = rc.adjacentLocation(d);
        switch (d) {
            case NORTH:
                if (danger[0]) return -1;
                break;
            case NORTHEAST:
                if (danger[1]) return -1;
                break;
            case EAST:
                if (danger[2]) return -1;
                break;
            case SOUTHEAST:
                if (danger[3]) return -1;
                break;
            case SOUTH:
                if (danger[4]) return -1;
                break;
            case SOUTHWEST:
                if (danger[5]) return -1;
                break;
            case WEST:
                if (danger[6]) return -1;
                break;
            case NORTHWEST:
                if (danger[7]) return -1;
                break;
        }
        if (rc.canMove(d) || ((shouldFill && (isFill || (loc.x + loc.y) % 2 == 0)) && rc.canFill(loc))) {
            return 1;
        }
        return 0;
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
        switch (d) {
            case NORTH:
                if (danger[0]) return false;
                break;
            case NORTHEAST:
                if (danger[1]) return false;
                break;
            case EAST:
                if (danger[2]) return false;
                break;
            case SOUTHEAST:
                if (danger[3]) return false;
                break;
            case SOUTH:
                if (danger[4]) return false;
                break;
            case SOUTHWEST:
                if (danger[5]) return false;
                break;
            case WEST:
                if (danger[6]) return false;
                break;
            case NORTHWEST:
                if (danger[7]) return false;
                break;
        }
        if (rc.canMove(d)) {
            rc.move(d);
            return true;
        }

        MapLocation l = rc.adjacentLocation(d);
        if ((shouldFill && (isFill || (l.x + l.y) % 2 == 0)) && rc.canFill(l)) {
            rc.fill(l);
            /*if (rc.canMove(d)) {
                rc.move(d);
            }*/
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
        bugging_danger = false;
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
        bugging_danger = false;
        visited.clear();
    }

    public static boolean move(RobotController rc, MapLocation target) throws GameActionException {
        return move(rc, target, false, false, null, null, 0);
    }

    public static boolean move(RobotController rc, MapLocation target, boolean persistence) throws GameActionException {
        return move(rc, target, persistence, false, null, null, 0);
    }

    public static boolean move(RobotController rc, MapLocation target, boolean persistence, boolean fill) throws GameActionException {
        return move(rc, target, persistence, fill, null, null, 0);
    }

    public static boolean move(RobotController rc, MapLocation target, boolean persistence, boolean fill, RobotInfo[] robots, int avoidRad) throws GameActionException {
        return move(rc, target, persistence, fill, robots, null, avoidRad);
    }

    
    public static boolean move(RobotController rc, MapLocation target, boolean persistence, boolean fill, RobotInfo[] robots, MapLocation avoidLoc, int avoidRad) throws GameActionException {
        if (!rc.isMovementReady()) return false;
        MapLocation loc = rc.getLocation();
        round = rc.getRoundNum();
        isFill = fill;
        shouldFill = fill || (round > FILL_ROUND && rc.getCrumbs() > 1000) || (failed > 10);
        Team team = rc.getTeam();

        if (robots != null || avoidLoc != null) {
            MapLocation[] m = new MapLocation[8];
            for (int i = 8; i-->0;) {
                danger[i] = false;
                m[i] = loc.add(RobotPlayer.directions[i]);
            }
            /*for (RobotInfo robot : robots) {
                if (robot.team != team) {
                    for (int i = 8; i-->0;) {
                        danger[i] = danger[i] || robot.location.isWithinDistanceSquared(m[i], avoidRad);
                    }
                }
            }*/
            //robots = rc.senseNearbyRobots(-1, team.opponent());
            if (robots != null) {
                for (int i = 8; i-->0;) {
                    for (RobotInfo robot : robots) {
                        if (robot.location.isWithinDistanceSquared(m[i], avoidRad)) {
                            danger[i] = true;
                            break;
                        }
                    }
                }
            }


            if (avoidLoc != null) {
                for (int i = 8; i-->0;) {
                    if (m[i].isWithinDistanceSquared(avoidLoc, avoidRad)) {
                        danger[i] = true;
                    }
                }
            }
            
            /*for (int i = 8; i-->0;) {
                if (danger[i]) rc.setIndicatorDot(m[i], 255, 0, 0);
                else rc.setIndicatorDot(m[i], 0, 0, 0);
            }*/
        } else {
            for (int i = 8; i-->0;) danger[i] = false;
        }

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
            /*if (canMove(rc, dirTarget) > 0)*/ reset(loc, target);
            minDist = dist;
        }
        else if (bugging) {
            Direction dirObstacle = loc.directionTo(lastObstacle);
            if (canMove(rc, dirObstacle) > 0) {reset(loc, target);}
        }

        int vcount = visited.count(loc);
        if (vcount >= 2) {
            MapLocation l;
            for (Direction d : RobotPlayer.allDirections) {
                l = target.add(d);
                if (!banned.contains(l)) {
                    banned.add(l);
                }
            }
            if (round >= FILL_ROUND) shouldFill = true;
            reset(loc, target);
        } else {
            visited.add(loc);
        }

        MapLocation next;
        MapInfo mi;
        if (bugging) {
            Direction dirObstacle = loc.directionTo(lastObstacle);
            next = loc.add(dirObstacle);
            if (rc.onTheMap(next)) {
                mi = rc.senseMapInfo(next);
                if (round > 180 && mi.isDam()) {
                    // Wait for dam to come down;
                    return true;
                }
            }
        } else {
            next = loc.add(dirTarget);
            if (rc.onTheMap(next)) {
                mi = rc.senseMapInfo(next);
                if (round > 180 && mi.isDam()) {
                    // Wait for dam to come down;
                    return true;
                }
            }

        }



        int cm;
        boolean shouldAvoid = rc.hasFlag();
        // Try greedy, or else start bugging
        if (!bugging) {
            /*if (rc.hasFlag()) {
                next = loc.add(dirTarget);
                if (rc.canDropFlag(next)) {
                    rc.dropFlag(next);
                    Communications.markDropped(rc, RobotPlayer.flagCarrying);
                    RobotPlayer.flagCarrying = 0;
                }
            }*/
            if (tryMove(rc, dirTarget)) return true;
            bugging = true;

            Direction dr = loc.directionTo(target);
            MapLocation obstacleR = loc.add(dr);
            boolean dangerR = false;
            for (int i = 4; i-->0;) {
                cm = canMove(rc, dr);
                if (cm > 0) break;
                dangerR = cm < 0;
                obstacleR = loc.add(dr);
                dr = dr.rotateRight();
            }
            
            rc.setIndicatorString("START BUG");

            Direction dl = loc.directionTo(target);
            MapLocation obstacleL = loc.add(dl);
            boolean dangerL = false;
            for (int i = 4; i-->0;) {
                cm = canMove(rc, dl);
                if (cm > 0) break;
                dangerL = cm < 0;
                obstacleL = loc.add(dl);
                dl = dl.rotateLeft();
            }

            int leftDist = loc.add(dl).distanceSquaredTo(target);
            int rightDist = loc.add(dr).distanceSquaredTo(target);
            
            //if (shouldAvoid && dangerL && !dangerR) rotateRight = true;
            //else if (shouldAvoid && !dangerL && dangerR) rotateRight = false;
            if (leftDist < rightDist) rotateRight = false;
            else if (rightDist < leftDist) rotateRight = true;
            else {
                if (center == null) {
                    center = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
                }
                int leftCDist = loc.add(dl).distanceSquaredTo(center);
                int rightCDist = loc.add(dr).distanceSquaredTo(center);
                if (leftCDist < rightCDist) rotateRight = false;
                if (leftCDist > rightCDist) rotateRight = true;
                else rotateRight = true;
            }

            if (!rotateRight) {
                lastObstacle = obstacleL;
                bugging_danger = dangerL;
                if (tryMove(rc, dl)) {
                    failed = 0;
                    return true;
                } else {
                    failed++;
                    return false;
                }
            } else {
                lastObstacle = obstacleR;
                bugging_danger = dangerR;
                if (tryMove(rc, dr)) {
                    failed = 0;
                    return true;
                } else {
                    failed++;
                    return false;
                }
            }
        }

        if (rotateRight) {
            rc.setIndicatorString("R: " + minDist);

        } else {
            rc.setIndicatorString("L: " + minDist);
        }

        Direction dir = loc.directionTo(lastObstacle);
        MapLocation nextLoc;
        for (int i = 8; i-->0;) {
            cm = canMove(rc, dir);
            if (cm > 0) return tryMove(rc, dir);
            if (!bugging_danger && cm < 0) {
                bugging_danger = true;
                if (shouldAvoid) rotateRight = !rotateRight;
            }
            nextLoc = loc.add(dir);
            rc.setIndicatorDot(nextLoc, 0, 0, 0);
            if (!rc.onTheMap(nextLoc)) rotateRight = !rotateRight;
            else lastObstacle = nextLoc;
            if (rotateRight) dir = dir.rotateRight();
            else dir = dir.rotateLeft();
        }
        if (tryMove(rc, dir)) {
            failed = 0;
            return true;
        } else {
            failed++;
            return false;
        }
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
