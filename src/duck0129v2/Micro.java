package duck0129v2;

import battlecode.common.*;

public strictfp class Micro {

    static final int INF = 1000000;
    static final int attackRadius = 4;
    static final int extendedAttackRadius = 18;
    static final int healRadius = 4;
    static final int hurtHealth = 450;
    static final int DEFEND_RADIUS = 10;

    static MicroInfo[] mi;
    static RobotController rc;
    static MapLocation curr;

    static Team team;
    static boolean hurt;
    static boolean canAttack;
    static boolean flagTaken;
    static MapLocation defendSpot;
    static boolean canAttackNext;
    static boolean canMoveNext;
    static int numAllies = 0;
    static int numEnemies = 0;
    static boolean healer;
    static boolean stunned;
    static boolean isSetup;
    static int myDamage;
    static boolean defendingCarrier;
    static boolean shouldEnterRange;
    //static int adjacentAllies = 0;

    static Direction[] dirs = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
        Direction.CENTER,
    };

    public Micro(RobotController rc) {
        this.rc = rc;
        team = rc.getTeam();

        mi = new MicroInfo[9];
    }

    class MicroInfo {
        Direction d;
        MapLocation l;
        boolean canMove;
        int minDistToEnemy = INF;
        int minDistToAlly = INF;
        int inRange = 0;
        int enemiesAttacking = 0;
        int enemiesTargeting = 0;
        int alliesTargeting = 0;
        int minDistToFlag = INF;
        boolean isDefense = false;
        boolean isSpace = false;
        boolean diagonal = false;
        boolean isJailable = false;
        int jailableAttackers = 0;
        MapLocation nearestEnemy = null;
        int alliesAttacking = 0;

        public MicroInfo(Direction d) throws GameActionException {
            this.d = d;
            if (d == Direction.NORTHEAST || d == Direction.SOUTHEAST || d == Direction.SOUTHWEST || d == Direction.NORTHWEST) diagonal = true;
            l = curr.add(d);
            canMove = d == Direction.CENTER || rc.canMove(d);
            if (defendSpot != null) {
                isDefense = l.isWithinDistanceSquared(defendSpot, DEFEND_RADIUS);
                isSpace = !l.isWithinDistanceSquared(defendSpot, 2);
            }
        }

        void updateEnemy(RobotInfo robot) {
            if (!canMove) return;
            int dist = robot.location.distanceSquaredTo(l);
            if (dist < minDistToEnemy) {
                minDistToEnemy = dist;
                nearestEnemy = robot.location;
            }
            if (robot.hasFlag && dist < minDistToFlag) minDistToFlag = dist;
            if (dist <= attackRadius) {
                if (robot.hasFlag) inRange = 2;
                else if (inRange == 0) inRange = 1;
                if (!stunned) {
                    enemiesAttacking++;
                    enemiesTargeting++;
                    if (robot.health <= myDamage) {
                        isJailable = true;
                        jailableAttackers = 1;
                    }
                } else {
                    if (robot.health <= myDamage) isJailable = true;
                }
            } else if (robot.location.add(robot.location.directionTo(l)).isWithinDistanceSquared(l, attackRadius) && !stunned) {
                enemiesTargeting++;
            }
        }

        void updateAlly(RobotInfo robot) {
            if (!canMove) return;
            int dist = robot.location.distanceSquaredTo(l);
            if (dist < minDistToAlly) minDistToAlly = dist;
            if (dist <= healRadius && healer) {
                alliesTargeting++;
            }
            if (nearestEnemy != null && robot.location.isWithinDistanceSquared(nearestEnemy, 10)) {
                alliesAttacking++;
            }
        }

        boolean betterThan(MicroInfo other) {
            if (!canMove) return false;
            if (!other.canMove) return true;

            if (isSetup) {
                if (minDistToEnemy < other.minDistToEnemy) return true;
                if (minDistToEnemy > other.minDistToEnemy) return false;
            }

            if (flagTaken) {
                if (inRange == 2 && other.inRange < 2) return true;
                if (inRange < 2 && other.inRange == 2) return false;
                if (inRange < 2) {
                    if (minDistToFlag < other.minDistToFlag) return true;
                    if (minDistToFlag > other.minDistToFlag) return false;
                }
            }

            if (defendSpot != null) {
                if (isDefense && !other.isDefense) return true;
                if (!isDefense && other.isDefense) return false;
            }

            if (canAttack && !hurt) {
                if (inRange > other.inRange) return true;
                if (inRange < other.inRange) return false;
                /*if (isAggro) {
                    if (inRange > other.inRange) return true;
                    if (inRange < other.inRange) return false;
                } else {
                    if (inRange > other.inRange && enemiesAttacking - jailableAttackers - alliesAttacking <= 2) return true;
                    if (inRange < other.inRange && other.enemiesAttacking - other.jailableAttackers - other.alliesAttacking <= 2) return false;
                }*/
            }

            if (defendSpot != null && defendingCarrier) {
                if (isSpace && !other.isSpace) return true;
                if (!isSpace && other.isSpace) return false;
            }
            
            if (canAttack) {
                if (enemiesAttacking - jailableAttackers < other.enemiesAttacking - other.jailableAttackers) return true;
                if (enemiesAttacking - jailableAttackers > other.enemiesAttacking - other.jailableAttackers) return false;
            } else {
                if (enemiesAttacking < other.enemiesAttacking) return true;
                if (enemiesAttacking > other.enemiesAttacking) return false;
            }

            /*if (!hurt && canAttack && numAllies >= 4 && inRange == 0) {
                if (!diagonal && other.diagonal) return true;
                if (diagonal && !other.diagonal) return false;
                if (minDistToEnemy < other.minDistToEnemy) return true;
                if (minDistToEnemy > other.minDistToEnemy) return false;
            }*/
            /*if (canAttack) {
                if (isJailable && !other.isJailable) return true;
                if (!isJailable && other.isJailable) return false;
            }*/
            
            /*if (canAttack && hurt && enemiesAttacking == 0 && enemiesTargeting == 0) {
                if (inRange > other.inRange) return true;
                if (inRange < other.inRange) return false;
            }*/
            if (canAttack) {
                if (enemiesTargeting - jailableAttackers < other.enemiesTargeting - other.jailableAttackers) return true;
                if (enemiesTargeting - jailableAttackers > other.enemiesTargeting - other.jailableAttackers) return false;
            } else {
                if (enemiesTargeting < other.enemiesTargeting) return true;
                if (enemiesTargeting > other.enemiesTargeting) return false;
            }

            if (canAttack) {
                if (enemiesTargeting - jailableAttackers == 0) {
                    if (inRange > other.inRange) return true;
                    if (inRange < other.inRange) return false;
                }
                //if (isJailable && !other.isJailable) return true;
                //if (!isJailable && other.isJailable) return false;
            }
            
            if (!hurt && canAttackNext) {
                if (inRange == 0 && other.inRange == 0) {
                    if (!diagonal && other.diagonal) return true;
                    if (diagonal && !other.diagonal) return false;
                    if (minDistToEnemy < other.minDistToEnemy) return true;
                    if (minDistToEnemy > other.minDistToEnemy) return false;
                }
            } else {
                if (minDistToEnemy > other.minDistToEnemy) return true;
                if (minDistToEnemy < other.minDistToEnemy) return false;
            }

            if (alliesTargeting > 0 && other.alliesTargeting == 0) return true;
            if (alliesTargeting == 0 &&  other.alliesTargeting > 0) return false;

            if (!diagonal && other.diagonal) return true;
            if (diagonal && !other.diagonal) return false;

            if (alliesTargeting == 0) {
                if (minDistToAlly < other.minDistToAlly) return true;
                if (minDistToAlly > other.minDistToAlly) return false;
            }

            
            return minDistToEnemy > other.minDistToEnemy;

            
        }
    }

    boolean doMicro(RobotInfo[] nearbyAllies, RobotInfo[] nearbyEnemies) throws GameActionException {
        return doMicro(nearbyAllies, nearbyEnemies, null, "", false, true);
    }

    boolean doMicro(RobotInfo[] nearbyAllies, RobotInfo[] nearbyEnemies, MapLocation defSpot, String stunnedList, boolean defCarrier, boolean aggro) throws GameActionException {
        curr = rc.getLocation();
        hurt = rc.getHealth() <= hurtHealth;
        canAttack = rc.isActionReady();
        flagTaken = false;
        isSetup = rc.getRoundNum() < GameConstants.SETUP_ROUNDS;
        defendSpot = defSpot;
        canAttackNext = rc.getActionCooldownTurns() - GameConstants.COOLDOWNS_PER_TURN < GameConstants.COOLDOWN_LIMIT;
        numAllies = 0;
        numEnemies = 0;
        defendingCarrier = defCarrier;
        myDamage = rc.getAttackDamage();
        //adjacentAllies = 0;

        mi[0] = new MicroInfo(Direction.NORTH);
        mi[1] = new MicroInfo(Direction.NORTHEAST);
        mi[2] = new MicroInfo(Direction.EAST);
        mi[3] = new MicroInfo(Direction.SOUTHEAST);
        mi[4] = new MicroInfo(Direction.SOUTH);
        mi[5] = new MicroInfo(Direction.SOUTHWEST);
        mi[6] = new MicroInfo(Direction.WEST);
        mi[7] = new MicroInfo(Direction.NORTHWEST);
        mi[8] = new MicroInfo(Direction.CENTER);

        for (RobotInfo robot : nearbyEnemies) {
            if (robot.hasFlag) {flagTaken = true; stunned = true;}
            else {
                stunned = stunnedList.contains(Integer.toString(robot.ID));
                if (stunned) rc.setIndicatorDot(robot.location, 255, 255, 0);
                //stunned = false;
            }
            mi[0].updateEnemy(robot);
            mi[1].updateEnemy(robot);
            mi[2].updateEnemy(robot);
            mi[3].updateEnemy(robot);
            mi[4].updateEnemy(robot);
            mi[5].updateEnemy(robot);
            mi[6].updateEnemy(robot);
            mi[7].updateEnemy(robot);
            mi[8].updateEnemy(robot);
            numEnemies++;
        }
        
        for (RobotInfo robot : nearbyAllies) {
            healer = robot.getAttackLevel() >= 4 || robot.getBuildLevel() >= 4 || robot.getHealLevel() <= 2 || robot.getHealLevel() >= 4;
            mi[0].updateAlly(robot);
            mi[1].updateAlly(robot);
            mi[2].updateAlly(robot);
            mi[3].updateAlly(robot);
            mi[4].updateAlly(robot);
            mi[5].updateAlly(robot);
            mi[6].updateAlly(robot);
            mi[7].updateAlly(robot);
            mi[8].updateAlly(robot);
            numAllies++;
        }


        shouldEnterRange = rc.getRoundNum() % 2 == 0 || numAllies == 0;

        MicroInfo best = mi[8];

        if (mi[7].betterThan(best)) best = mi[7];
        if (mi[6].betterThan(best)) best = mi[6];
        if (mi[5].betterThan(best)) best = mi[5];
        if (mi[4].betterThan(best)) best = mi[4];
        if (mi[3].betterThan(best)) best = mi[3];
        if (mi[2].betterThan(best)) best = mi[2];
        if (mi[1].betterThan(best)) best = mi[1];
        if (mi[0].betterThan(best)) best = mi[0];

        /*String s = "";

        if (hurt) s += "H";
        if (canAttack) s += "X";

        for (int i = 0; i < 9; i++) {
            if (!rc.canMove(mi[i].d)) {
                s += "B";
            } else if (mi[i].enemiesAttacking > 0) {
                s += "A";
            } else {
                s += "S";
            }
        }

        rc.setIndicatorString(s + ":" + best.d);*/

        if (best.d == Direction.CENTER) return true;
        if (rc.canMove(best.d)) {
            rc.move(best.d);
            return true;
        }
        return false;
    }
}
