package dino;
import battlecode.common.*;
public strictfp class ThreatCalculator {

    static boolean passable_m3_m1;
    static boolean passable_m3_0;
    static boolean passable_m3_1;
    static boolean passable_m2_m2;
    static boolean passable_m2_m1;
    static boolean passable_m2_0;
    static boolean passable_m2_1;
    static boolean passable_m2_2;
    static boolean passable_m1_m3;
    static boolean passable_m1_m2;
    static boolean passable_m1_m1;
    static boolean passable_m1_0;
    static boolean passable_m1_1;
    static boolean passable_m1_2;
    static boolean passable_m1_3;
    static boolean passable_0_m3;
    static boolean passable_0_m2;
    static boolean passable_0_m1;
    static boolean passable_0_0;
    static boolean passable_0_1;
    static boolean passable_0_2;
    static boolean passable_0_3;
    static boolean passable_1_m3;
    static boolean passable_1_m2;
    static boolean passable_1_m1;
    static boolean passable_1_0;
    static boolean passable_1_1;
    static boolean passable_1_2;
    static boolean passable_1_3;
    static boolean passable_2_m2;
    static boolean passable_2_m1;
    static boolean passable_2_0;
    static boolean passable_2_1;
    static boolean passable_2_2;
    static boolean passable_3_m1;
    static boolean passable_3_0;
    static boolean passable_3_1;
    static boolean inRange_m4_m2;
    static boolean inRange_m4_m1;
    static boolean inRange_m4_0;
    static boolean inRange_m4_1;
    static boolean inRange_m4_2;
    static boolean inRange_m3_m3;
    static boolean inRange_m3_m2;
    static boolean inRange_m3_m1;
    static boolean inRange_m3_0;
    static boolean inRange_m3_1;
    static boolean inRange_m3_2;
    static boolean inRange_m3_3;
    static boolean inRange_m2_m4;
    static boolean inRange_m2_m3;
    static boolean inRange_m2_m2;
    static boolean inRange_m2_m1;
    static boolean inRange_m2_0;
    static boolean inRange_m2_1;
    static boolean inRange_m2_2;
    static boolean inRange_m2_3;
    static boolean inRange_m2_4;
    static boolean inRange_m1_m4;
    static boolean inRange_m1_m3;
    static boolean inRange_m1_m2;
    static boolean inRange_m1_m1;
    static boolean inRange_m1_0;
    static boolean inRange_m1_1;
    static boolean inRange_m1_2;
    static boolean inRange_m1_3;
    static boolean inRange_m1_4;
    static boolean inRange_0_m4;
    static boolean inRange_0_m3;
    static boolean inRange_0_m2;
    static boolean inRange_0_m1;
    static boolean inRange_0_0;
    static boolean inRange_0_1;
    static boolean inRange_0_2;
    static boolean inRange_0_3;
    static boolean inRange_0_4;
    static boolean inRange_1_m4;
    static boolean inRange_1_m3;
    static boolean inRange_1_m2;
    static boolean inRange_1_m1;
    static boolean inRange_1_0;
    static boolean inRange_1_1;
    static boolean inRange_1_2;
    static boolean inRange_1_3;
    static boolean inRange_1_4;
    static boolean inRange_2_m4;
    static boolean inRange_2_m3;
    static boolean inRange_2_m2;
    static boolean inRange_2_m1;
    static boolean inRange_2_0;
    static boolean inRange_2_1;
    static boolean inRange_2_2;
    static boolean inRange_2_3;
    static boolean inRange_2_4;
    static boolean inRange_3_m3;
    static boolean inRange_3_m2;
    static boolean inRange_3_m1;
    static boolean inRange_3_0;
    static boolean inRange_3_1;
    static boolean inRange_3_2;
    static boolean inRange_3_3;
    static boolean inRange_4_m2;
    static boolean inRange_4_m1;
    static boolean inRange_4_0;
    static boolean inRange_4_1;
    static boolean inRange_4_2;
    
    public static void calculate(RobotController rc, RobotInfo[] nearbyEnemies, RobotInfo[] nearbyAllies, Micro.MicroInfo[] mi, String stunned) throws GameActionException {
        MapLocation curr = rc.getLocation();
        MapLocation m;
        m = curr.translate(-3, -1);
        passable_m3_m1 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(-3, 0);
        passable_m3_0 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(-3, 1);
        passable_m3_1 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(-2, -2);
        passable_m2_m2 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(-2, -1);
        passable_m2_m1 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(-2, 0);
        passable_m2_0 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(-2, 1);
        passable_m2_1 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(-2, 2);
        passable_m2_2 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(-1, -3);
        passable_m1_m3 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(-1, -2);
        passable_m1_m2 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(-1, -1);
        passable_m1_m1 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(-1, 0);
        passable_m1_0 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(-1, 1);
        passable_m1_1 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(-1, 2);
        passable_m1_2 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(-1, 3);
        passable_m1_3 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(0, -3);
        passable_0_m3 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(0, -2);
        passable_0_m2 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(0, -1);
        passable_0_m1 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(0, 1);
        passable_0_1 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(0, 2);
        passable_0_2 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(0, 3);
        passable_0_3 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(1, -3);
        passable_1_m3 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(1, -2);
        passable_1_m2 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(1, -1);
        passable_1_m1 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(1, 0);
        passable_1_0 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(1, 1);
        passable_1_1 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(1, 2);
        passable_1_2 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(1, 3);
        passable_1_3 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(2, -2);
        passable_2_m2 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(2, -1);
        passable_2_m1 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(2, 0);
        passable_2_0 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(2, 1);
        passable_2_1 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(2, 2);
        passable_2_2 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(3, -1);
        passable_3_m1 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(3, 0);
        passable_3_0 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        m = curr.translate(3, 1);
        passable_3_1 = rc.onTheMap(m) && rc.senseMapInfo(m).isPassable() && !rc.canSenseRobotAtLocation(m);
        inRange_m4_m2 = false;
        inRange_m4_m1 = false;
        inRange_m4_0 = false;
        inRange_m4_1 = false;
        inRange_m4_2 = false;
        inRange_m3_m3 = false;
        inRange_m3_m2 = false;
        inRange_m3_m1 = false;
        inRange_m3_0 = false;
        inRange_m3_1 = false;
        inRange_m3_2 = false;
        inRange_m3_3 = false;
        inRange_m2_m4 = false;
        inRange_m2_m3 = false;
        inRange_m2_m2 = false;
        inRange_m2_m1 = false;
        inRange_m2_0 = false;
        inRange_m2_1 = false;
        inRange_m2_2 = false;
        inRange_m2_3 = false;
        inRange_m2_4 = false;
        inRange_m1_m4 = false;
        inRange_m1_m3 = false;
        inRange_m1_m2 = false;
        inRange_m1_m1 = false;
        inRange_m1_0 = false;
        inRange_m1_1 = false;
        inRange_m1_2 = false;
        inRange_m1_3 = false;
        inRange_m1_4 = false;
        inRange_0_m4 = false;
        inRange_0_m3 = false;
        inRange_0_m2 = false;
        inRange_0_m1 = false;
        inRange_0_0 = false;
        inRange_0_1 = false;
        inRange_0_2 = false;
        inRange_0_3 = false;
        inRange_0_4 = false;
        inRange_1_m4 = false;
        inRange_1_m3 = false;
        inRange_1_m2 = false;
        inRange_1_m1 = false;
        inRange_1_0 = false;
        inRange_1_1 = false;
        inRange_1_2 = false;
        inRange_1_3 = false;
        inRange_1_4 = false;
        inRange_2_m4 = false;
        inRange_2_m3 = false;
        inRange_2_m2 = false;
        inRange_2_m1 = false;
        inRange_2_0 = false;
        inRange_2_1 = false;
        inRange_2_2 = false;
        inRange_2_3 = false;
        inRange_2_4 = false;
        inRange_3_m3 = false;
        inRange_3_m2 = false;
        inRange_3_m1 = false;
        inRange_3_0 = false;
        inRange_3_1 = false;
        inRange_3_2 = false;
        inRange_3_3 = false;
        inRange_4_m2 = false;
        inRange_4_m1 = false;
        inRange_4_0 = false;
        inRange_4_1 = false;
        inRange_4_2 = false;
        for (RobotInfo robot : nearbyAllies) {
            switch (robot.location.x - curr.x) {
                case -4:
                    switch (robot.location.y - curr.y) {
                        case -2:
                            inRange_m4_m2 = true;
                            inRange_m4_m1 = true;
                            inRange_m4_0 = true;
                            inRange_m3_m3 = true;
                            inRange_m3_m2 = true;
                            inRange_m3_m1 = true;
                            inRange_m2_m2 = true;
                            break;
                        case -1:
                            inRange_m4_m2 = true;
                            inRange_m4_m1 = true;
                            inRange_m4_0 = true;
                            inRange_m4_1 = true;
                            inRange_m3_m2 = true;
                            inRange_m3_m1 = true;
                            inRange_m3_0 = true;
                            inRange_m2_m1 = true;
                            break;
                        case 0:
                            inRange_m4_m2 = true;
                            inRange_m4_m1 = true;
                            inRange_m4_0 = true;
                            inRange_m4_1 = true;
                            inRange_m4_2 = true;
                            inRange_m3_m1 = true;
                            inRange_m3_0 = true;
                            inRange_m3_1 = true;
                            inRange_m2_0 = true;
                            break;
                        case 1:
                            inRange_m4_m1 = true;
                            inRange_m4_0 = true;
                            inRange_m4_1 = true;
                            inRange_m4_2 = true;
                            inRange_m3_0 = true;
                            inRange_m3_1 = true;
                            inRange_m3_2 = true;
                            inRange_m2_1 = true;
                            break;
                        case 2:
                            inRange_m4_0 = true;
                            inRange_m4_1 = true;
                            inRange_m4_2 = true;
                            inRange_m3_1 = true;
                            inRange_m3_2 = true;
                            inRange_m3_3 = true;
                            inRange_m2_2 = true;
                            break;
                    }
                    break;
                case -3:
                    switch (robot.location.y - curr.y) {
                        case -3:
                            inRange_m4_m2 = true;
                            inRange_m3_m3 = true;
                            inRange_m3_m2 = true;
                            inRange_m3_m1 = true;
                            inRange_m2_m4 = true;
                            inRange_m2_m3 = true;
                            inRange_m2_m2 = true;
                            inRange_m1_m3 = true;
                            break;
                        case -2:
                            inRange_m4_m2 = true;
                            inRange_m4_m1 = true;
                            inRange_m3_m3 = true;
                            inRange_m3_m2 = true;
                            inRange_m3_m1 = true;
                            inRange_m3_0 = true;
                            inRange_m2_m3 = true;
                            inRange_m2_m2 = true;
                            inRange_m2_m1 = true;
                            inRange_m1_m2 = true;
                            break;
                        case -1:
                            inRange_m4_m2 = true;
                            inRange_m4_m1 = true;
                            inRange_m4_0 = true;
                            inRange_m3_m3 = true;
                            inRange_m3_m2 = true;
                            inRange_m3_m1 = true;
                            inRange_m3_0 = true;
                            inRange_m3_1 = true;
                            inRange_m2_m2 = true;
                            inRange_m2_m1 = true;
                            inRange_m2_0 = true;
                            inRange_m1_m1 = true;
                            break;
                        case 0:
                            inRange_m4_m1 = true;
                            inRange_m4_0 = true;
                            inRange_m4_1 = true;
                            inRange_m3_m2 = true;
                            inRange_m3_m1 = true;
                            inRange_m3_0 = true;
                            inRange_m3_1 = true;
                            inRange_m3_2 = true;
                            inRange_m2_m1 = true;
                            inRange_m2_0 = true;
                            inRange_m2_1 = true;
                            inRange_m1_0 = true;
                            break;
                        case 1:
                            inRange_m4_0 = true;
                            inRange_m4_1 = true;
                            inRange_m4_2 = true;
                            inRange_m3_m1 = true;
                            inRange_m3_0 = true;
                            inRange_m3_1 = true;
                            inRange_m3_2 = true;
                            inRange_m3_3 = true;
                            inRange_m2_0 = true;
                            inRange_m2_1 = true;
                            inRange_m2_2 = true;
                            inRange_m1_1 = true;
                            break;
                        case 2:
                            inRange_m4_1 = true;
                            inRange_m4_2 = true;
                            inRange_m3_0 = true;
                            inRange_m3_1 = true;
                            inRange_m3_2 = true;
                            inRange_m3_3 = true;
                            inRange_m2_1 = true;
                            inRange_m2_2 = true;
                            inRange_m2_3 = true;
                            inRange_m1_2 = true;
                            break;
                        case 3:
                            inRange_m4_2 = true;
                            inRange_m3_1 = true;
                            inRange_m3_2 = true;
                            inRange_m3_3 = true;
                            inRange_m2_2 = true;
                            inRange_m2_3 = true;
                            inRange_m2_4 = true;
                            inRange_m1_3 = true;
                            break;
                    }
                    break;
                case -2:
                    switch (robot.location.y - curr.y) {
                        case -4:
                            inRange_m3_m3 = true;
                            inRange_m2_m4 = true;
                            inRange_m2_m3 = true;
                            inRange_m2_m2 = true;
                            inRange_m1_m4 = true;
                            inRange_m1_m3 = true;
                            inRange_0_m4 = true;
                            break;
                        case -3:
                            inRange_m3_m3 = true;
                            inRange_m3_m2 = true;
                            inRange_m2_m4 = true;
                            inRange_m2_m3 = true;
                            inRange_m2_m2 = true;
                            inRange_m2_m1 = true;
                            inRange_m1_m4 = true;
                            inRange_m1_m3 = true;
                            inRange_m1_m2 = true;
                            inRange_0_m3 = true;
                            break;
                        case -2:
                            inRange_m4_m2 = true;
                            inRange_m3_m3 = true;
                            inRange_m3_m2 = true;
                            inRange_m3_m1 = true;
                            inRange_m2_m4 = true;
                            inRange_m2_m3 = true;
                            inRange_m2_m2 = true;
                            inRange_m2_m1 = true;
                            inRange_m2_0 = true;
                            inRange_m1_m3 = true;
                            inRange_m1_m2 = true;
                            inRange_m1_m1 = true;
                            inRange_0_m2 = true;
                            break;
                        case -1:
                            inRange_m4_m1 = true;
                            inRange_m3_m2 = true;
                            inRange_m3_m1 = true;
                            inRange_m3_0 = true;
                            inRange_m2_m3 = true;
                            inRange_m2_m2 = true;
                            inRange_m2_m1 = true;
                            inRange_m2_0 = true;
                            inRange_m2_1 = true;
                            inRange_m1_m2 = true;
                            inRange_m1_m1 = true;
                            inRange_m1_0 = true;
                            inRange_0_m1 = true;
                            break;
                        case 0:
                            inRange_m4_0 = true;
                            inRange_m3_m1 = true;
                            inRange_m3_0 = true;
                            inRange_m3_1 = true;
                            inRange_m2_m2 = true;
                            inRange_m2_m1 = true;
                            inRange_m2_0 = true;
                            inRange_m2_1 = true;
                            inRange_m2_2 = true;
                            inRange_m1_m1 = true;
                            inRange_m1_0 = true;
                            inRange_m1_1 = true;
                            inRange_0_0 = true;
                            break;
                        case 1:
                            inRange_m4_1 = true;
                            inRange_m3_0 = true;
                            inRange_m3_1 = true;
                            inRange_m3_2 = true;
                            inRange_m2_m1 = true;
                            inRange_m2_0 = true;
                            inRange_m2_1 = true;
                            inRange_m2_2 = true;
                            inRange_m2_3 = true;
                            inRange_m1_0 = true;
                            inRange_m1_1 = true;
                            inRange_m1_2 = true;
                            inRange_0_1 = true;
                            break;
                        case 2:
                            inRange_m4_2 = true;
                            inRange_m3_1 = true;
                            inRange_m3_2 = true;
                            inRange_m3_3 = true;
                            inRange_m2_0 = true;
                            inRange_m2_1 = true;
                            inRange_m2_2 = true;
                            inRange_m2_3 = true;
                            inRange_m2_4 = true;
                            inRange_m1_1 = true;
                            inRange_m1_2 = true;
                            inRange_m1_3 = true;
                            inRange_0_2 = true;
                            break;
                        case 3:
                            inRange_m3_2 = true;
                            inRange_m3_3 = true;
                            inRange_m2_1 = true;
                            inRange_m2_2 = true;
                            inRange_m2_3 = true;
                            inRange_m2_4 = true;
                            inRange_m1_2 = true;
                            inRange_m1_3 = true;
                            inRange_m1_4 = true;
                            inRange_0_3 = true;
                            break;
                        case 4:
                            inRange_m3_3 = true;
                            inRange_m2_2 = true;
                            inRange_m2_3 = true;
                            inRange_m2_4 = true;
                            inRange_m1_3 = true;
                            inRange_m1_4 = true;
                            inRange_0_4 = true;
                            break;
                    }
                    break;
                case -1:
                    switch (robot.location.y - curr.y) {
                        case -4:
                            inRange_m2_m4 = true;
                            inRange_m2_m3 = true;
                            inRange_m1_m4 = true;
                            inRange_m1_m3 = true;
                            inRange_m1_m2 = true;
                            inRange_0_m4 = true;
                            inRange_0_m3 = true;
                            inRange_1_m4 = true;
                            break;
                        case -3:
                            inRange_m3_m3 = true;
                            inRange_m2_m4 = true;
                            inRange_m2_m3 = true;
                            inRange_m2_m2 = true;
                            inRange_m1_m4 = true;
                            inRange_m1_m3 = true;
                            inRange_m1_m2 = true;
                            inRange_m1_m1 = true;
                            inRange_0_m4 = true;
                            inRange_0_m3 = true;
                            inRange_0_m2 = true;
                            inRange_1_m3 = true;
                            break;
                        case -2:
                            inRange_m3_m2 = true;
                            inRange_m2_m3 = true;
                            inRange_m2_m2 = true;
                            inRange_m2_m1 = true;
                            inRange_m1_m4 = true;
                            inRange_m1_m3 = true;
                            inRange_m1_m2 = true;
                            inRange_m1_m1 = true;
                            inRange_m1_0 = true;
                            inRange_0_m3 = true;
                            inRange_0_m2 = true;
                            inRange_0_m1 = true;
                            inRange_1_m2 = true;
                            break;
                        case -1:
                            inRange_m3_m1 = true;
                            inRange_m2_m2 = true;
                            inRange_m2_m1 = true;
                            inRange_m2_0 = true;
                            inRange_m1_m3 = true;
                            inRange_m1_m2 = true;
                            inRange_m1_m1 = true;
                            inRange_m1_0 = true;
                            inRange_m1_1 = true;
                            inRange_0_m2 = true;
                            inRange_0_m1 = true;
                            inRange_0_0 = true;
                            inRange_1_m1 = true;
                            break;
                        case 0:
                            inRange_m3_0 = true;
                            inRange_m2_m1 = true;
                            inRange_m2_0 = true;
                            inRange_m2_1 = true;
                            inRange_m1_m2 = true;
                            inRange_m1_m1 = true;
                            inRange_m1_0 = true;
                            inRange_m1_1 = true;
                            inRange_m1_2 = true;
                            inRange_0_m1 = true;
                            inRange_0_0 = true;
                            inRange_0_1 = true;
                            inRange_1_0 = true;
                            break;
                        case 1:
                            inRange_m3_1 = true;
                            inRange_m2_0 = true;
                            inRange_m2_1 = true;
                            inRange_m2_2 = true;
                            inRange_m1_m1 = true;
                            inRange_m1_0 = true;
                            inRange_m1_1 = true;
                            inRange_m1_2 = true;
                            inRange_m1_3 = true;
                            inRange_0_0 = true;
                            inRange_0_1 = true;
                            inRange_0_2 = true;
                            inRange_1_1 = true;
                            break;
                        case 2:
                            inRange_m3_2 = true;
                            inRange_m2_1 = true;
                            inRange_m2_2 = true;
                            inRange_m2_3 = true;
                            inRange_m1_0 = true;
                            inRange_m1_1 = true;
                            inRange_m1_2 = true;
                            inRange_m1_3 = true;
                            inRange_m1_4 = true;
                            inRange_0_1 = true;
                            inRange_0_2 = true;
                            inRange_0_3 = true;
                            inRange_1_2 = true;
                            break;
                        case 3:
                            inRange_m3_3 = true;
                            inRange_m2_2 = true;
                            inRange_m2_3 = true;
                            inRange_m2_4 = true;
                            inRange_m1_1 = true;
                            inRange_m1_2 = true;
                            inRange_m1_3 = true;
                            inRange_m1_4 = true;
                            inRange_0_2 = true;
                            inRange_0_3 = true;
                            inRange_0_4 = true;
                            inRange_1_3 = true;
                            break;
                        case 4:
                            inRange_m2_3 = true;
                            inRange_m2_4 = true;
                            inRange_m1_2 = true;
                            inRange_m1_3 = true;
                            inRange_m1_4 = true;
                            inRange_0_3 = true;
                            inRange_0_4 = true;
                            inRange_1_4 = true;
                            break;
                    }
                    break;
                case 0:
                    switch (robot.location.y - curr.y) {
                        case -4:
                            inRange_m2_m4 = true;
                            inRange_m1_m4 = true;
                            inRange_m1_m3 = true;
                            inRange_0_m4 = true;
                            inRange_0_m3 = true;
                            inRange_0_m2 = true;
                            inRange_1_m4 = true;
                            inRange_1_m3 = true;
                            inRange_2_m4 = true;
                            break;
                        case -3:
                            inRange_m2_m3 = true;
                            inRange_m1_m4 = true;
                            inRange_m1_m3 = true;
                            inRange_m1_m2 = true;
                            inRange_0_m4 = true;
                            inRange_0_m3 = true;
                            inRange_0_m2 = true;
                            inRange_0_m1 = true;
                            inRange_1_m4 = true;
                            inRange_1_m3 = true;
                            inRange_1_m2 = true;
                            inRange_2_m3 = true;
                            break;
                        case -2:
                            inRange_m2_m2 = true;
                            inRange_m1_m3 = true;
                            inRange_m1_m2 = true;
                            inRange_m1_m1 = true;
                            inRange_0_m4 = true;
                            inRange_0_m3 = true;
                            inRange_0_m2 = true;
                            inRange_0_m1 = true;
                            inRange_0_0 = true;
                            inRange_1_m3 = true;
                            inRange_1_m2 = true;
                            inRange_1_m1 = true;
                            inRange_2_m2 = true;
                            break;
                        case -1:
                            inRange_m2_m1 = true;
                            inRange_m1_m2 = true;
                            inRange_m1_m1 = true;
                            inRange_m1_0 = true;
                            inRange_0_m3 = true;
                            inRange_0_m2 = true;
                            inRange_0_m1 = true;
                            inRange_0_0 = true;
                            inRange_0_1 = true;
                            inRange_1_m2 = true;
                            inRange_1_m1 = true;
                            inRange_1_0 = true;
                            inRange_2_m1 = true;
                            break;
                        case 0:
                            inRange_m2_0 = true;
                            inRange_m1_m1 = true;
                            inRange_m1_0 = true;
                            inRange_m1_1 = true;
                            inRange_0_m2 = true;
                            inRange_0_m1 = true;
                            inRange_0_0 = true;
                            inRange_0_1 = true;
                            inRange_0_2 = true;
                            inRange_1_m1 = true;
                            inRange_1_0 = true;
                            inRange_1_1 = true;
                            inRange_2_0 = true;
                            break;
                        case 1:
                            inRange_m2_1 = true;
                            inRange_m1_0 = true;
                            inRange_m1_1 = true;
                            inRange_m1_2 = true;
                            inRange_0_m1 = true;
                            inRange_0_0 = true;
                            inRange_0_1 = true;
                            inRange_0_2 = true;
                            inRange_0_3 = true;
                            inRange_1_0 = true;
                            inRange_1_1 = true;
                            inRange_1_2 = true;
                            inRange_2_1 = true;
                            break;
                        case 2:
                            inRange_m2_2 = true;
                            inRange_m1_1 = true;
                            inRange_m1_2 = true;
                            inRange_m1_3 = true;
                            inRange_0_0 = true;
                            inRange_0_1 = true;
                            inRange_0_2 = true;
                            inRange_0_3 = true;
                            inRange_0_4 = true;
                            inRange_1_1 = true;
                            inRange_1_2 = true;
                            inRange_1_3 = true;
                            inRange_2_2 = true;
                            break;
                        case 3:
                            inRange_m2_3 = true;
                            inRange_m1_2 = true;
                            inRange_m1_3 = true;
                            inRange_m1_4 = true;
                            inRange_0_1 = true;
                            inRange_0_2 = true;
                            inRange_0_3 = true;
                            inRange_0_4 = true;
                            inRange_1_2 = true;
                            inRange_1_3 = true;
                            inRange_1_4 = true;
                            inRange_2_3 = true;
                            break;
                        case 4:
                            inRange_m2_4 = true;
                            inRange_m1_3 = true;
                            inRange_m1_4 = true;
                            inRange_0_2 = true;
                            inRange_0_3 = true;
                            inRange_0_4 = true;
                            inRange_1_3 = true;
                            inRange_1_4 = true;
                            inRange_2_4 = true;
                            break;
                    }
                    break;
                case 1:
                    switch (robot.location.y - curr.y) {
                        case -4:
                            inRange_m1_m4 = true;
                            inRange_0_m4 = true;
                            inRange_0_m3 = true;
                            inRange_1_m4 = true;
                            inRange_1_m3 = true;
                            inRange_1_m2 = true;
                            inRange_2_m4 = true;
                            inRange_2_m3 = true;
                            break;
                        case -3:
                            inRange_m1_m3 = true;
                            inRange_0_m4 = true;
                            inRange_0_m3 = true;
                            inRange_0_m2 = true;
                            inRange_1_m4 = true;
                            inRange_1_m3 = true;
                            inRange_1_m2 = true;
                            inRange_1_m1 = true;
                            inRange_2_m4 = true;
                            inRange_2_m3 = true;
                            inRange_2_m2 = true;
                            inRange_3_m3 = true;
                            break;
                        case -2:
                            inRange_m1_m2 = true;
                            inRange_0_m3 = true;
                            inRange_0_m2 = true;
                            inRange_0_m1 = true;
                            inRange_1_m4 = true;
                            inRange_1_m3 = true;
                            inRange_1_m2 = true;
                            inRange_1_m1 = true;
                            inRange_1_0 = true;
                            inRange_2_m3 = true;
                            inRange_2_m2 = true;
                            inRange_2_m1 = true;
                            inRange_3_m2 = true;
                            break;
                        case -1:
                            inRange_m1_m1 = true;
                            inRange_0_m2 = true;
                            inRange_0_m1 = true;
                            inRange_0_0 = true;
                            inRange_1_m3 = true;
                            inRange_1_m2 = true;
                            inRange_1_m1 = true;
                            inRange_1_0 = true;
                            inRange_1_1 = true;
                            inRange_2_m2 = true;
                            inRange_2_m1 = true;
                            inRange_2_0 = true;
                            inRange_3_m1 = true;
                            break;
                        case 0:
                            inRange_m1_0 = true;
                            inRange_0_m1 = true;
                            inRange_0_0 = true;
                            inRange_0_1 = true;
                            inRange_1_m2 = true;
                            inRange_1_m1 = true;
                            inRange_1_0 = true;
                            inRange_1_1 = true;
                            inRange_1_2 = true;
                            inRange_2_m1 = true;
                            inRange_2_0 = true;
                            inRange_2_1 = true;
                            inRange_3_0 = true;
                            break;
                        case 1:
                            inRange_m1_1 = true;
                            inRange_0_0 = true;
                            inRange_0_1 = true;
                            inRange_0_2 = true;
                            inRange_1_m1 = true;
                            inRange_1_0 = true;
                            inRange_1_1 = true;
                            inRange_1_2 = true;
                            inRange_1_3 = true;
                            inRange_2_0 = true;
                            inRange_2_1 = true;
                            inRange_2_2 = true;
                            inRange_3_1 = true;
                            break;
                        case 2:
                            inRange_m1_2 = true;
                            inRange_0_1 = true;
                            inRange_0_2 = true;
                            inRange_0_3 = true;
                            inRange_1_0 = true;
                            inRange_1_1 = true;
                            inRange_1_2 = true;
                            inRange_1_3 = true;
                            inRange_1_4 = true;
                            inRange_2_1 = true;
                            inRange_2_2 = true;
                            inRange_2_3 = true;
                            inRange_3_2 = true;
                            break;
                        case 3:
                            inRange_m1_3 = true;
                            inRange_0_2 = true;
                            inRange_0_3 = true;
                            inRange_0_4 = true;
                            inRange_1_1 = true;
                            inRange_1_2 = true;
                            inRange_1_3 = true;
                            inRange_1_4 = true;
                            inRange_2_2 = true;
                            inRange_2_3 = true;
                            inRange_2_4 = true;
                            inRange_3_3 = true;
                            break;
                        case 4:
                            inRange_m1_4 = true;
                            inRange_0_3 = true;
                            inRange_0_4 = true;
                            inRange_1_2 = true;
                            inRange_1_3 = true;
                            inRange_1_4 = true;
                            inRange_2_3 = true;
                            inRange_2_4 = true;
                            break;
                    }
                    break;
                case 2:
                    switch (robot.location.y - curr.y) {
                        case -4:
                            inRange_0_m4 = true;
                            inRange_1_m4 = true;
                            inRange_1_m3 = true;
                            inRange_2_m4 = true;
                            inRange_2_m3 = true;
                            inRange_2_m2 = true;
                            inRange_3_m3 = true;
                            break;
                        case -3:
                            inRange_0_m3 = true;
                            inRange_1_m4 = true;
                            inRange_1_m3 = true;
                            inRange_1_m2 = true;
                            inRange_2_m4 = true;
                            inRange_2_m3 = true;
                            inRange_2_m2 = true;
                            inRange_2_m1 = true;
                            inRange_3_m3 = true;
                            inRange_3_m2 = true;
                            break;
                        case -2:
                            inRange_0_m2 = true;
                            inRange_1_m3 = true;
                            inRange_1_m2 = true;
                            inRange_1_m1 = true;
                            inRange_2_m4 = true;
                            inRange_2_m3 = true;
                            inRange_2_m2 = true;
                            inRange_2_m1 = true;
                            inRange_2_0 = true;
                            inRange_3_m3 = true;
                            inRange_3_m2 = true;
                            inRange_3_m1 = true;
                            inRange_4_m2 = true;
                            break;
                        case -1:
                            inRange_0_m1 = true;
                            inRange_1_m2 = true;
                            inRange_1_m1 = true;
                            inRange_1_0 = true;
                            inRange_2_m3 = true;
                            inRange_2_m2 = true;
                            inRange_2_m1 = true;
                            inRange_2_0 = true;
                            inRange_2_1 = true;
                            inRange_3_m2 = true;
                            inRange_3_m1 = true;
                            inRange_3_0 = true;
                            inRange_4_m1 = true;
                            break;
                        case 0:
                            inRange_0_0 = true;
                            inRange_1_m1 = true;
                            inRange_1_0 = true;
                            inRange_1_1 = true;
                            inRange_2_m2 = true;
                            inRange_2_m1 = true;
                            inRange_2_0 = true;
                            inRange_2_1 = true;
                            inRange_2_2 = true;
                            inRange_3_m1 = true;
                            inRange_3_0 = true;
                            inRange_3_1 = true;
                            inRange_4_0 = true;
                            break;
                        case 1:
                            inRange_0_1 = true;
                            inRange_1_0 = true;
                            inRange_1_1 = true;
                            inRange_1_2 = true;
                            inRange_2_m1 = true;
                            inRange_2_0 = true;
                            inRange_2_1 = true;
                            inRange_2_2 = true;
                            inRange_2_3 = true;
                            inRange_3_0 = true;
                            inRange_3_1 = true;
                            inRange_3_2 = true;
                            inRange_4_1 = true;
                            break;
                        case 2:
                            inRange_0_2 = true;
                            inRange_1_1 = true;
                            inRange_1_2 = true;
                            inRange_1_3 = true;
                            inRange_2_0 = true;
                            inRange_2_1 = true;
                            inRange_2_2 = true;
                            inRange_2_3 = true;
                            inRange_2_4 = true;
                            inRange_3_1 = true;
                            inRange_3_2 = true;
                            inRange_3_3 = true;
                            inRange_4_2 = true;
                            break;
                        case 3:
                            inRange_0_3 = true;
                            inRange_1_2 = true;
                            inRange_1_3 = true;
                            inRange_1_4 = true;
                            inRange_2_1 = true;
                            inRange_2_2 = true;
                            inRange_2_3 = true;
                            inRange_2_4 = true;
                            inRange_3_2 = true;
                            inRange_3_3 = true;
                            break;
                        case 4:
                            inRange_0_4 = true;
                            inRange_1_3 = true;
                            inRange_1_4 = true;
                            inRange_2_2 = true;
                            inRange_2_3 = true;
                            inRange_2_4 = true;
                            inRange_3_3 = true;
                            break;
                    }
                    break;
                case 3:
                    switch (robot.location.y - curr.y) {
                        case -3:
                            inRange_1_m3 = true;
                            inRange_2_m4 = true;
                            inRange_2_m3 = true;
                            inRange_2_m2 = true;
                            inRange_3_m3 = true;
                            inRange_3_m2 = true;
                            inRange_3_m1 = true;
                            inRange_4_m2 = true;
                            break;
                        case -2:
                            inRange_1_m2 = true;
                            inRange_2_m3 = true;
                            inRange_2_m2 = true;
                            inRange_2_m1 = true;
                            inRange_3_m3 = true;
                            inRange_3_m2 = true;
                            inRange_3_m1 = true;
                            inRange_3_0 = true;
                            inRange_4_m2 = true;
                            inRange_4_m1 = true;
                            break;
                        case -1:
                            inRange_1_m1 = true;
                            inRange_2_m2 = true;
                            inRange_2_m1 = true;
                            inRange_2_0 = true;
                            inRange_3_m3 = true;
                            inRange_3_m2 = true;
                            inRange_3_m1 = true;
                            inRange_3_0 = true;
                            inRange_3_1 = true;
                            inRange_4_m2 = true;
                            inRange_4_m1 = true;
                            inRange_4_0 = true;
                            break;
                        case 0:
                            inRange_1_0 = true;
                            inRange_2_m1 = true;
                            inRange_2_0 = true;
                            inRange_2_1 = true;
                            inRange_3_m2 = true;
                            inRange_3_m1 = true;
                            inRange_3_0 = true;
                            inRange_3_1 = true;
                            inRange_3_2 = true;
                            inRange_4_m1 = true;
                            inRange_4_0 = true;
                            inRange_4_1 = true;
                            break;
                        case 1:
                            inRange_1_1 = true;
                            inRange_2_0 = true;
                            inRange_2_1 = true;
                            inRange_2_2 = true;
                            inRange_3_m1 = true;
                            inRange_3_0 = true;
                            inRange_3_1 = true;
                            inRange_3_2 = true;
                            inRange_3_3 = true;
                            inRange_4_0 = true;
                            inRange_4_1 = true;
                            inRange_4_2 = true;
                            break;
                        case 2:
                            inRange_1_2 = true;
                            inRange_2_1 = true;
                            inRange_2_2 = true;
                            inRange_2_3 = true;
                            inRange_3_0 = true;
                            inRange_3_1 = true;
                            inRange_3_2 = true;
                            inRange_3_3 = true;
                            inRange_4_1 = true;
                            inRange_4_2 = true;
                            break;
                        case 3:
                            inRange_1_3 = true;
                            inRange_2_2 = true;
                            inRange_2_3 = true;
                            inRange_2_4 = true;
                            inRange_3_1 = true;
                            inRange_3_2 = true;
                            inRange_3_3 = true;
                            inRange_4_2 = true;
                            break;
                    }
                    break;
                case 4:
                    switch (robot.location.y - curr.y) {
                        case -2:
                            inRange_2_m2 = true;
                            inRange_3_m3 = true;
                            inRange_3_m2 = true;
                            inRange_3_m1 = true;
                            inRange_4_m2 = true;
                            inRange_4_m1 = true;
                            inRange_4_0 = true;
                            break;
                        case -1:
                            inRange_2_m1 = true;
                            inRange_3_m2 = true;
                            inRange_3_m1 = true;
                            inRange_3_0 = true;
                            inRange_4_m2 = true;
                            inRange_4_m1 = true;
                            inRange_4_0 = true;
                            inRange_4_1 = true;
                            break;
                        case 0:
                            inRange_2_0 = true;
                            inRange_3_m1 = true;
                            inRange_3_0 = true;
                            inRange_3_1 = true;
                            inRange_4_m2 = true;
                            inRange_4_m1 = true;
                            inRange_4_0 = true;
                            inRange_4_1 = true;
                            inRange_4_2 = true;
                            break;
                        case 1:
                            inRange_2_1 = true;
                            inRange_3_0 = true;
                            inRange_3_1 = true;
                            inRange_3_2 = true;
                            inRange_4_m1 = true;
                            inRange_4_0 = true;
                            inRange_4_1 = true;
                            inRange_4_2 = true;
                            break;
                        case 2:
                            inRange_2_2 = true;
                            inRange_3_1 = true;
                            inRange_3_2 = true;
                            inRange_3_3 = true;
                            inRange_4_0 = true;
                            inRange_4_1 = true;
                            inRange_4_2 = true;
                            break;
                    }
                    break;
            }
        }
        for (RobotInfo robot : nearbyEnemies) {
            if (stunned.contains(Integer.toString(robot.ID))) continue;
            switch (robot.location.x - curr.x) {
                case -4:
                    switch (robot.location.y - curr.y) {
                        case -2:
                            if (!inRange_m4_m2) {
                                if (passable_m3_m1) mi[5].enemiesTargeting++;
                            }
                            break;
                        case -1:
                            if (!inRange_m4_m1) {
                                if (passable_m3_m1) mi[5].enemiesTargeting++;
                                if (passable_m3_0) mi[6].enemiesTargeting++;
                            }
                            break;
                        case 0:
                            if (!inRange_m4_0) {
                                if (passable_m3_m1) mi[5].enemiesTargeting++;
                                if (passable_m3_0) mi[6].enemiesTargeting++;
                                if (passable_m3_1) mi[7].enemiesTargeting++;
                            }
                            break;
                        case 1:
                            if (!inRange_m4_1) {
                                if (passable_m3_0) mi[6].enemiesTargeting++;
                                if (passable_m3_1) mi[7].enemiesTargeting++;
                            }
                            break;
                        case 2:
                            if (!inRange_m4_2) {
                                if (passable_m3_1) mi[7].enemiesTargeting++;
                            }
                            break;
                    }
                    break;
                case -3:
                    switch (robot.location.y - curr.y) {
                        case -3:
                            if (!inRange_m3_m3) {
                                if (passable_m2_m2) mi[5].enemiesTargeting++;
                            }
                            break;
                        case -2:
                            if (!inRange_m3_m2) {
                                if (passable_m2_m1) mi[4].enemiesTargeting++;
                                if (passable_m3_m1 || passable_m2_m2 || passable_m2_m1) mi[5].enemiesTargeting++;
                                if (passable_m2_m1) mi[6].enemiesTargeting++;
                            }
                            break;
                        case -1:
                            if (!inRange_m3_m1) {
                                if (passable_m2_m1) mi[4].enemiesTargeting++;
                                mi[5].enemiesTargeting++;
                                if (passable_m3_0 || passable_m2_m1 || passable_m2_0) mi[6].enemiesTargeting++;
                                if (passable_m2_0) mi[7].enemiesTargeting++;
                                if (passable_m2_0) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 0:
                            if (!inRange_m3_0) {
                                if (passable_m2_1) mi[0].enemiesTargeting++;
                                if (passable_m2_m1) mi[4].enemiesTargeting++;
                                if (passable_m3_m1 || passable_m2_m1 || passable_m2_0) mi[5].enemiesTargeting++;
                                mi[6].enemiesTargeting++;
                                if (passable_m3_1 || passable_m2_0 || passable_m2_1) mi[7].enemiesTargeting++;
                                if (passable_m2_0) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 1:
                            if (!inRange_m3_1) {
                                if (passable_m2_1) mi[0].enemiesTargeting++;
                                if (passable_m2_0) mi[5].enemiesTargeting++;
                                if (passable_m3_0 || passable_m2_0 || passable_m2_1) mi[6].enemiesTargeting++;
                                mi[7].enemiesTargeting++;
                                if (passable_m2_0) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 2:
                            if (!inRange_m3_2) {
                                if (passable_m2_1) mi[0].enemiesTargeting++;
                                if (passable_m2_1) mi[6].enemiesTargeting++;
                                if (passable_m3_1 || passable_m2_1 || passable_m2_2) mi[7].enemiesTargeting++;
                            }
                            break;
                        case 3:
                            if (!inRange_m3_3) {
                                if (passable_m2_2) mi[7].enemiesTargeting++;
                            }
                            break;
                    }
                    break;
                case -2:
                    switch (robot.location.y - curr.y) {
                        case -4:
                            if (!inRange_m2_m4) {
                                if (passable_m1_m3) mi[5].enemiesTargeting++;
                            }
                            break;
                        case -3:
                            if (!inRange_m2_m3) {
                                if (passable_m1_m2) mi[4].enemiesTargeting++;
                                if (passable_m2_m2 || passable_m1_m3 || passable_m1_m2) mi[5].enemiesTargeting++;
                                if (passable_m1_m2) mi[6].enemiesTargeting++;
                            }
                            break;
                        case -2:
                            if (!inRange_m2_m2) {
                                if (passable_m1_m1) mi[3].enemiesTargeting++;
                                if (passable_m2_m1 || passable_m1_m2 || passable_m1_m1) mi[4].enemiesTargeting++;
                                mi[5].enemiesTargeting++;
                                if (passable_m2_m1 || passable_m1_m2 || passable_m1_m1) mi[6].enemiesTargeting++;
                                if (passable_m1_m1) mi[7].enemiesTargeting++;
                                if (passable_m1_m1) mi[8].enemiesTargeting++;
                            }
                            break;
                        case -1:
                            if (!inRange_m2_m1) {
                                if (passable_m1_0) mi[0].enemiesTargeting++;
                                if (passable_m1_0) mi[2].enemiesTargeting++;
                                if (passable_m1_m1) mi[3].enemiesTargeting++;
                                mi[4].enemiesTargeting++;
                                mi[5].enemiesTargeting++;
                                mi[6].enemiesTargeting++;
                                if (passable_m2_0 || passable_m1_m1 || passable_m1_0) mi[7].enemiesTargeting++;
                                if (passable_m2_0 || passable_m1_m1 || passable_m1_0) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 0:
                            if (!inRange_m2_0) {
                                if (passable_m2_1 || passable_m1_0 || passable_m1_1) mi[0].enemiesTargeting++;
                                if (passable_m1_1) mi[1].enemiesTargeting++;
                                if (passable_m1_0) mi[2].enemiesTargeting++;
                                if (passable_m1_m1) mi[3].enemiesTargeting++;
                                if (passable_m2_m1 || passable_m1_m1 || passable_m1_0) mi[4].enemiesTargeting++;
                                mi[5].enemiesTargeting++;
                                mi[6].enemiesTargeting++;
                                mi[7].enemiesTargeting++;
                                mi[8].enemiesTargeting++;
                            }
                            break;
                        case 1:
                            if (!inRange_m2_1) {
                                mi[0].enemiesTargeting++;
                                if (passable_m1_1) mi[1].enemiesTargeting++;
                                if (passable_m1_0) mi[2].enemiesTargeting++;
                                if (passable_m1_0) mi[4].enemiesTargeting++;
                                if (passable_m2_0 || passable_m1_0 || passable_m1_1) mi[5].enemiesTargeting++;
                                mi[6].enemiesTargeting++;
                                mi[7].enemiesTargeting++;
                                if (passable_m2_0 || passable_m1_0 || passable_m1_1) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 2:
                            if (!inRange_m2_2) {
                                if (passable_m2_1 || passable_m1_1 || passable_m1_2) mi[0].enemiesTargeting++;
                                if (passable_m1_1) mi[1].enemiesTargeting++;
                                if (passable_m1_1) mi[5].enemiesTargeting++;
                                if (passable_m2_1 || passable_m1_1 || passable_m1_2) mi[6].enemiesTargeting++;
                                mi[7].enemiesTargeting++;
                                if (passable_m1_1) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 3:
                            if (!inRange_m2_3) {
                                if (passable_m1_2) mi[0].enemiesTargeting++;
                                if (passable_m1_2) mi[6].enemiesTargeting++;
                                if (passable_m2_2 || passable_m1_2 || passable_m1_3) mi[7].enemiesTargeting++;
                            }
                            break;
                        case 4:
                            if (!inRange_m2_4) {
                                if (passable_m1_3) mi[7].enemiesTargeting++;
                            }
                            break;
                    }
                    break;
                case -1:
                    switch (robot.location.y - curr.y) {
                        case -4:
                            if (!inRange_m1_m4) {
                                if (passable_0_m3) mi[4].enemiesTargeting++;
                                if (passable_m1_m3) mi[5].enemiesTargeting++;
                            }
                            break;
                        case -3:
                            if (!inRange_m1_m3) {
                                if (passable_0_m2) mi[3].enemiesTargeting++;
                                if (passable_m1_m2 || passable_0_m3 || passable_0_m2) mi[4].enemiesTargeting++;
                                mi[5].enemiesTargeting++;
                                if (passable_m1_m2) mi[6].enemiesTargeting++;
                                if (passable_0_m2) mi[8].enemiesTargeting++;
                            }
                            break;
                        case -2:
                            if (!inRange_m1_m2) {
                                if (passable_0_m1) mi[0].enemiesTargeting++;
                                if (passable_0_m1) mi[2].enemiesTargeting++;
                                if (passable_m1_m1 || passable_0_m2 || passable_0_m1) mi[3].enemiesTargeting++;
                                mi[4].enemiesTargeting++;
                                mi[5].enemiesTargeting++;
                                mi[6].enemiesTargeting++;
                                if (passable_m1_m1) mi[7].enemiesTargeting++;
                                if (passable_m1_m1 || passable_0_m2 || passable_0_m1) mi[8].enemiesTargeting++;
                            }
                            break;
                        case -1:
                            if (!inRange_m1_m1) {
                                if (passable_m1_0 || passable_0_m1 || passable_0_0) mi[0].enemiesTargeting++;
                                if (passable_0_0) mi[1].enemiesTargeting++;
                                if (passable_m1_0 || passable_0_m1 || passable_0_0) mi[2].enemiesTargeting++;
                                mi[3].enemiesTargeting++;
                                mi[4].enemiesTargeting++;
                                mi[5].enemiesTargeting++;
                                mi[6].enemiesTargeting++;
                                mi[7].enemiesTargeting++;
                                mi[8].enemiesTargeting++;
                            }
                            break;
                        case 0:
                            if (!inRange_m1_0) {
                                mi[0].enemiesTargeting++;
                                if (passable_m1_1 || passable_0_0 || passable_0_1) mi[1].enemiesTargeting++;
                                mi[2].enemiesTargeting++;
                                if (passable_m1_m1 || passable_0_m1 || passable_0_0) mi[3].enemiesTargeting++;
                                mi[4].enemiesTargeting++;
                                mi[5].enemiesTargeting++;
                                mi[6].enemiesTargeting++;
                                mi[7].enemiesTargeting++;
                                mi[8].enemiesTargeting++;
                            }
                            break;
                        case 1:
                            if (!inRange_m1_1) {
                                mi[0].enemiesTargeting++;
                                mi[1].enemiesTargeting++;
                                if (passable_m1_0 || passable_0_0 || passable_0_1) mi[2].enemiesTargeting++;
                                if (passable_0_0) mi[3].enemiesTargeting++;
                                if (passable_m1_0 || passable_0_0 || passable_0_1) mi[4].enemiesTargeting++;
                                mi[5].enemiesTargeting++;
                                mi[6].enemiesTargeting++;
                                mi[7].enemiesTargeting++;
                                mi[8].enemiesTargeting++;
                            }
                            break;
                        case 2:
                            if (!inRange_m1_2) {
                                mi[0].enemiesTargeting++;
                                if (passable_m1_1 || passable_0_1 || passable_0_2) mi[1].enemiesTargeting++;
                                if (passable_0_1) mi[2].enemiesTargeting++;
                                if (passable_0_1) mi[4].enemiesTargeting++;
                                if (passable_m1_1) mi[5].enemiesTargeting++;
                                mi[6].enemiesTargeting++;
                                mi[7].enemiesTargeting++;
                                if (passable_m1_1 || passable_0_1 || passable_0_2) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 3:
                            if (!inRange_m1_3) {
                                if (passable_m1_2 || passable_0_2 || passable_0_3) mi[0].enemiesTargeting++;
                                if (passable_0_2) mi[1].enemiesTargeting++;
                                if (passable_m1_2) mi[6].enemiesTargeting++;
                                mi[7].enemiesTargeting++;
                                if (passable_0_2) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 4:
                            if (!inRange_m1_4) {
                                if (passable_0_3) mi[0].enemiesTargeting++;
                                if (passable_m1_3) mi[7].enemiesTargeting++;
                            }
                            break;
                    }
                    break;
                case 0:
                    switch (robot.location.y - curr.y) {
                        case -4:
                            if (!inRange_0_m4) {
                                if (passable_1_m3) mi[3].enemiesTargeting++;
                                if (passable_0_m3) mi[4].enemiesTargeting++;
                                if (passable_m1_m3) mi[5].enemiesTargeting++;
                            }
                            break;
                        case -3:
                            if (!inRange_0_m3) {
                                if (passable_1_m2) mi[2].enemiesTargeting++;
                                if (passable_0_m2 || passable_1_m3 || passable_1_m2) mi[3].enemiesTargeting++;
                                mi[4].enemiesTargeting++;
                                if (passable_m1_m3 || passable_m1_m2 || passable_0_m2) mi[5].enemiesTargeting++;
                                if (passable_m1_m2) mi[6].enemiesTargeting++;
                                if (passable_0_m2) mi[8].enemiesTargeting++;
                            }
                            break;
                        case -2:
                            if (!inRange_0_m2) {
                                if (passable_0_m1) mi[0].enemiesTargeting++;
                                if (passable_1_m1) mi[1].enemiesTargeting++;
                                if (passable_0_m1 || passable_1_m2 || passable_1_m1) mi[2].enemiesTargeting++;
                                mi[3].enemiesTargeting++;
                                mi[4].enemiesTargeting++;
                                mi[5].enemiesTargeting++;
                                if (passable_m1_m2 || passable_m1_m1 || passable_0_m1) mi[6].enemiesTargeting++;
                                if (passable_m1_m1) mi[7].enemiesTargeting++;
                                mi[8].enemiesTargeting++;
                            }
                            break;
                        case -1:
                            if (!inRange_0_m1) {
                                mi[0].enemiesTargeting++;
                                if (passable_0_0 || passable_1_m1 || passable_1_0) mi[1].enemiesTargeting++;
                                mi[2].enemiesTargeting++;
                                mi[3].enemiesTargeting++;
                                mi[4].enemiesTargeting++;
                                mi[5].enemiesTargeting++;
                                mi[6].enemiesTargeting++;
                                if (passable_m1_m1 || passable_m1_0 || passable_0_0) mi[7].enemiesTargeting++;
                                mi[8].enemiesTargeting++;
                            }
                            break;
                        case 0:
                            if (!inRange_0_0) {
                                mi[0].enemiesTargeting++;
                                mi[1].enemiesTargeting++;
                                mi[2].enemiesTargeting++;
                                mi[3].enemiesTargeting++;
                                mi[4].enemiesTargeting++;
                                mi[5].enemiesTargeting++;
                                mi[6].enemiesTargeting++;
                                mi[7].enemiesTargeting++;
                                mi[8].enemiesTargeting++;
                            }
                            break;
                        case 1:
                            if (!inRange_0_1) {
                                mi[0].enemiesTargeting++;
                                mi[1].enemiesTargeting++;
                                mi[2].enemiesTargeting++;
                                if (passable_0_0 || passable_1_0 || passable_1_1) mi[3].enemiesTargeting++;
                                mi[4].enemiesTargeting++;
                                if (passable_m1_0 || passable_m1_1 || passable_0_0) mi[5].enemiesTargeting++;
                                mi[6].enemiesTargeting++;
                                mi[7].enemiesTargeting++;
                                mi[8].enemiesTargeting++;
                            }
                            break;
                        case 2:
                            if (!inRange_0_2) {
                                mi[0].enemiesTargeting++;
                                mi[1].enemiesTargeting++;
                                if (passable_0_1 || passable_1_1 || passable_1_2) mi[2].enemiesTargeting++;
                                if (passable_1_1) mi[3].enemiesTargeting++;
                                if (passable_0_1) mi[4].enemiesTargeting++;
                                if (passable_m1_1) mi[5].enemiesTargeting++;
                                if (passable_m1_1 || passable_m1_2 || passable_0_1) mi[6].enemiesTargeting++;
                                mi[7].enemiesTargeting++;
                                mi[8].enemiesTargeting++;
                            }
                            break;
                        case 3:
                            if (!inRange_0_3) {
                                mi[0].enemiesTargeting++;
                                if (passable_0_2 || passable_1_2 || passable_1_3) mi[1].enemiesTargeting++;
                                if (passable_1_2) mi[2].enemiesTargeting++;
                                if (passable_m1_2) mi[6].enemiesTargeting++;
                                if (passable_m1_2 || passable_m1_3 || passable_0_2) mi[7].enemiesTargeting++;
                                if (passable_0_2) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 4:
                            if (!inRange_0_4) {
                                if (passable_0_3) mi[0].enemiesTargeting++;
                                if (passable_1_3) mi[1].enemiesTargeting++;
                                if (passable_m1_3) mi[7].enemiesTargeting++;
                            }
                            break;
                    }
                    break;
                case 1:
                    switch (robot.location.y - curr.y) {
                        case -4:
                            if (!inRange_1_m4) {
                                if (passable_1_m3) mi[3].enemiesTargeting++;
                                if (passable_0_m3) mi[4].enemiesTargeting++;
                            }
                            break;
                        case -3:
                            if (!inRange_1_m3) {
                                if (passable_1_m2) mi[2].enemiesTargeting++;
                                mi[3].enemiesTargeting++;
                                if (passable_0_m3 || passable_0_m2 || passable_1_m2) mi[4].enemiesTargeting++;
                                if (passable_0_m2) mi[5].enemiesTargeting++;
                                if (passable_0_m2) mi[8].enemiesTargeting++;
                            }
                            break;
                        case -2:
                            if (!inRange_1_m2) {
                                if (passable_0_m1) mi[0].enemiesTargeting++;
                                if (passable_1_m1) mi[1].enemiesTargeting++;
                                mi[2].enemiesTargeting++;
                                mi[3].enemiesTargeting++;
                                mi[4].enemiesTargeting++;
                                if (passable_0_m2 || passable_0_m1 || passable_1_m1) mi[5].enemiesTargeting++;
                                if (passable_0_m1) mi[6].enemiesTargeting++;
                                if (passable_0_m2 || passable_0_m1 || passable_1_m1) mi[8].enemiesTargeting++;
                            }
                            break;
                        case -1:
                            if (!inRange_1_m1) {
                                if (passable_0_m1 || passable_0_0 || passable_1_0) mi[0].enemiesTargeting++;
                                mi[1].enemiesTargeting++;
                                mi[2].enemiesTargeting++;
                                mi[3].enemiesTargeting++;
                                mi[4].enemiesTargeting++;
                                mi[5].enemiesTargeting++;
                                if (passable_0_m1 || passable_0_0 || passable_1_0) mi[6].enemiesTargeting++;
                                if (passable_0_0) mi[7].enemiesTargeting++;
                                mi[8].enemiesTargeting++;
                            }
                            break;
                        case 0:
                            if (!inRange_1_0) {
                                mi[0].enemiesTargeting++;
                                mi[1].enemiesTargeting++;
                                mi[2].enemiesTargeting++;
                                mi[3].enemiesTargeting++;
                                mi[4].enemiesTargeting++;
                                if (passable_0_m1 || passable_0_0 || passable_1_m1) mi[5].enemiesTargeting++;
                                mi[6].enemiesTargeting++;
                                if (passable_0_0 || passable_0_1 || passable_1_1) mi[7].enemiesTargeting++;
                                mi[8].enemiesTargeting++;
                            }
                            break;
                        case 1:
                            if (!inRange_1_1) {
                                mi[0].enemiesTargeting++;
                                mi[1].enemiesTargeting++;
                                mi[2].enemiesTargeting++;
                                mi[3].enemiesTargeting++;
                                if (passable_0_0 || passable_0_1 || passable_1_0) mi[4].enemiesTargeting++;
                                if (passable_0_0) mi[5].enemiesTargeting++;
                                if (passable_0_0 || passable_0_1 || passable_1_0) mi[6].enemiesTargeting++;
                                mi[7].enemiesTargeting++;
                                mi[8].enemiesTargeting++;
                            }
                            break;
                        case 2:
                            if (!inRange_1_2) {
                                mi[0].enemiesTargeting++;
                                mi[1].enemiesTargeting++;
                                mi[2].enemiesTargeting++;
                                if (passable_1_1) mi[3].enemiesTargeting++;
                                if (passable_0_1) mi[4].enemiesTargeting++;
                                if (passable_0_1) mi[6].enemiesTargeting++;
                                if (passable_0_1 || passable_0_2 || passable_1_1) mi[7].enemiesTargeting++;
                                if (passable_0_1 || passable_0_2 || passable_1_1) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 3:
                            if (!inRange_1_3) {
                                if (passable_0_2 || passable_0_3 || passable_1_2) mi[0].enemiesTargeting++;
                                mi[1].enemiesTargeting++;
                                if (passable_1_2) mi[2].enemiesTargeting++;
                                if (passable_0_2) mi[7].enemiesTargeting++;
                                if (passable_0_2) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 4:
                            if (!inRange_1_4) {
                                if (passable_0_3) mi[0].enemiesTargeting++;
                                if (passable_1_3) mi[1].enemiesTargeting++;
                            }
                            break;
                    }
                    break;
                case 2:
                    switch (robot.location.y - curr.y) {
                        case -4:
                            if (!inRange_2_m4) {
                                if (passable_1_m3) mi[3].enemiesTargeting++;
                            }
                            break;
                        case -3:
                            if (!inRange_2_m3) {
                                if (passable_1_m2) mi[2].enemiesTargeting++;
                                if (passable_1_m3 || passable_1_m2 || passable_2_m2) mi[3].enemiesTargeting++;
                                if (passable_1_m2) mi[4].enemiesTargeting++;
                            }
                            break;
                        case -2:
                            if (!inRange_2_m2) {
                                if (passable_1_m1) mi[1].enemiesTargeting++;
                                if (passable_1_m2 || passable_1_m1 || passable_2_m1) mi[2].enemiesTargeting++;
                                mi[3].enemiesTargeting++;
                                if (passable_1_m2 || passable_1_m1 || passable_2_m1) mi[4].enemiesTargeting++;
                                if (passable_1_m1) mi[5].enemiesTargeting++;
                                if (passable_1_m1) mi[8].enemiesTargeting++;
                            }
                            break;
                        case -1:
                            if (!inRange_2_m1) {
                                if (passable_1_0) mi[0].enemiesTargeting++;
                                if (passable_1_m1 || passable_1_0 || passable_2_0) mi[1].enemiesTargeting++;
                                mi[2].enemiesTargeting++;
                                mi[3].enemiesTargeting++;
                                mi[4].enemiesTargeting++;
                                if (passable_1_m1) mi[5].enemiesTargeting++;
                                if (passable_1_0) mi[6].enemiesTargeting++;
                                if (passable_1_m1 || passable_1_0 || passable_2_0) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 0:
                            if (!inRange_2_0) {
                                if (passable_1_0 || passable_1_1 || passable_2_1) mi[0].enemiesTargeting++;
                                mi[1].enemiesTargeting++;
                                mi[2].enemiesTargeting++;
                                mi[3].enemiesTargeting++;
                                if (passable_1_m1 || passable_1_0 || passable_2_m1) mi[4].enemiesTargeting++;
                                if (passable_1_m1) mi[5].enemiesTargeting++;
                                if (passable_1_0) mi[6].enemiesTargeting++;
                                if (passable_1_1) mi[7].enemiesTargeting++;
                                mi[8].enemiesTargeting++;
                            }
                            break;
                        case 1:
                            if (!inRange_2_1) {
                                mi[0].enemiesTargeting++;
                                mi[1].enemiesTargeting++;
                                mi[2].enemiesTargeting++;
                                if (passable_1_0 || passable_1_1 || passable_2_0) mi[3].enemiesTargeting++;
                                if (passable_1_0) mi[4].enemiesTargeting++;
                                if (passable_1_0) mi[6].enemiesTargeting++;
                                if (passable_1_1) mi[7].enemiesTargeting++;
                                if (passable_1_0 || passable_1_1 || passable_2_0) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 2:
                            if (!inRange_2_2) {
                                if (passable_1_1 || passable_1_2 || passable_2_1) mi[0].enemiesTargeting++;
                                mi[1].enemiesTargeting++;
                                if (passable_1_1 || passable_1_2 || passable_2_1) mi[2].enemiesTargeting++;
                                if (passable_1_1) mi[3].enemiesTargeting++;
                                if (passable_1_1) mi[7].enemiesTargeting++;
                                if (passable_1_1) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 3:
                            if (!inRange_2_3) {
                                if (passable_1_2) mi[0].enemiesTargeting++;
                                if (passable_1_2 || passable_1_3 || passable_2_2) mi[1].enemiesTargeting++;
                                if (passable_1_2) mi[2].enemiesTargeting++;
                            }
                            break;
                        case 4:
                            if (!inRange_2_4) {
                                if (passable_1_3) mi[1].enemiesTargeting++;
                            }
                            break;
                    }
                    break;
                case 3:
                    switch (robot.location.y - curr.y) {
                        case -3:
                            if (!inRange_3_m3) {
                                if (passable_2_m2) mi[3].enemiesTargeting++;
                            }
                            break;
                        case -2:
                            if (!inRange_3_m2) {
                                if (passable_2_m1) mi[2].enemiesTargeting++;
                                if (passable_2_m2 || passable_2_m1 || passable_3_m1) mi[3].enemiesTargeting++;
                                if (passable_2_m1) mi[4].enemiesTargeting++;
                            }
                            break;
                        case -1:
                            if (!inRange_3_m1) {
                                if (passable_2_0) mi[1].enemiesTargeting++;
                                if (passable_2_m1 || passable_2_0 || passable_3_0) mi[2].enemiesTargeting++;
                                mi[3].enemiesTargeting++;
                                if (passable_2_m1) mi[4].enemiesTargeting++;
                                if (passable_2_0) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 0:
                            if (!inRange_3_0) {
                                if (passable_2_1) mi[0].enemiesTargeting++;
                                if (passable_2_0 || passable_2_1 || passable_3_1) mi[1].enemiesTargeting++;
                                mi[2].enemiesTargeting++;
                                if (passable_2_m1 || passable_2_0 || passable_3_m1) mi[3].enemiesTargeting++;
                                if (passable_2_m1) mi[4].enemiesTargeting++;
                                if (passable_2_0) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 1:
                            if (!inRange_3_1) {
                                if (passable_2_1) mi[0].enemiesTargeting++;
                                mi[1].enemiesTargeting++;
                                if (passable_2_0 || passable_2_1 || passable_3_0) mi[2].enemiesTargeting++;
                                if (passable_2_0) mi[3].enemiesTargeting++;
                                if (passable_2_0) mi[8].enemiesTargeting++;
                            }
                            break;
                        case 2:
                            if (!inRange_3_2) {
                                if (passable_2_1) mi[0].enemiesTargeting++;
                                if (passable_2_1 || passable_2_2 || passable_3_1) mi[1].enemiesTargeting++;
                                if (passable_2_1) mi[2].enemiesTargeting++;
                            }
                            break;
                        case 3:
                            if (!inRange_3_3) {
                                if (passable_2_2) mi[1].enemiesTargeting++;
                            }
                            break;
                    }
                    break;
                case 4:
                    switch (robot.location.y - curr.y) {
                        case -2:
                            if (!inRange_4_m2) {
                                if (passable_3_m1) mi[3].enemiesTargeting++;
                            }
                            break;
                        case -1:
                            if (!inRange_4_m1) {
                                if (passable_3_0) mi[2].enemiesTargeting++;
                                if (passable_3_m1) mi[3].enemiesTargeting++;
                            }
                            break;
                        case 0:
                            if (!inRange_4_0) {
                                if (passable_3_1) mi[1].enemiesTargeting++;
                                if (passable_3_0) mi[2].enemiesTargeting++;
                                if (passable_3_m1) mi[3].enemiesTargeting++;
                            }
                            break;
                        case 1:
                            if (!inRange_4_1) {
                                if (passable_3_1) mi[1].enemiesTargeting++;
                                if (passable_3_0) mi[2].enemiesTargeting++;
                            }
                            break;
                        case 2:
                            if (!inRange_4_2) {
                                if (passable_3_1) mi[1].enemiesTargeting++;
                            }
                            break;
                    }
                    break;
            }
        }
    }

}
