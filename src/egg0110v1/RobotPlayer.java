package egg0110v1;

import battlecode.common.*;
import java.util.Random;

public strictfp class RobotPlayer {

    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    static final Random rng = new Random();
    static final int READY_ROUNDS = 30;
    static final int ROUNDS_NEW_SEEK = 5;
    static final double SEEK_CHANCE = 0.8;

    static RobotInfo[] nearbyRobots;
    static Team team;
    static int round;

    static MapLocation exploreTarget = null;
    static Explore explore;
    static Micro micro;
    static boolean setup;

    static int roundSeek = -100;
    static boolean seekingEnemy;

    public static void run(RobotController rc) throws GameActionException {
        team = rc.getTeam();
        explore = new Explore(rc);
        micro = new Micro(rc);
        while (true) {
            try {
                round = rc.getRoundNum();
                setup = round <= GameConstants.SETUP_ROUNDS;
                Communications.readArray(rc);
                if (rc.canBuyGlobal(GlobalUpgrade.ACTION)) {
                    rc.buyGlobal(GlobalUpgrade.ACTION);
                } else if (rc.canBuyGlobal(GlobalUpgrade.HEALING)) {
                    rc.buyGlobal(GlobalUpgrade.HEALING);
                } else if (rc.canBuyGlobal(GlobalUpgrade.CAPTURING)) {
                    rc.buyGlobal(GlobalUpgrade.CAPTURING);
                }
                if (!rc.isSpawned()) {
                    BugNavigation.reset();
                    trySpawn(rc);
                }
                if (rc.isSpawned()) {
                    turn(rc);
                }
                Communications.updateExp(rc);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Clock.yield();
            }
        }
    }

    public static void trySpawn(RobotController rc) throws GameActionException {
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();

        MapLocation enemyLoc = Communications.readEnemies();

        if (enemyLoc != null) {
            int minDist = 1000000;
            MapLocation closest = null;

            int dist;
            for (MapLocation spawnLoc : spawnLocs) {
                if (!rc.canSpawn(spawnLoc)) continue;
                dist = spawnLoc.distanceSquaredTo(enemyLoc);
                if (dist < minDist) {
                    minDist = dist;
                    closest = spawnLoc;
                }
            }

            if (closest != null) {
                if (rc.canSpawn(closest)) {
                    rc.spawn(closest);
                }
            }

            return;
        }


        int l = spawnLocs.length;
        int s = rng.nextInt(l);
        MapLocation spawnLoc;
        for (int i = spawnLocs.length-1; i-->0;) {
            spawnLoc = spawnLocs[(s + i) % l];
            if (rc.canSpawn(spawnLoc)) {
                rc.spawn(spawnLoc);
                break;
            }
        }
    }

    public static void turn(RobotController rc) throws GameActionException {
        explore.update(rc);
        nearbyRobots = rc.senseNearbyRobots();
        Communications.updateEnemies(rc, nearbyRobots);
        int numAllies = 0;
        int numEnemies = 0;
        MapLocation curr = rc.getLocation();

        MapLocation nearestEnemy = null;
        int neDist = 1000000;
        
        int eDist;
        for (RobotInfo info : nearbyRobots) {
            if (info.team == team) {
                numAllies++;
            } else {
                numEnemies++;
                eDist = info.location.distanceSquaredTo(curr);
                if (eDist < neDist) {
                    neDist = eDist;
                    nearestEnemy = info.location;
                }
            }
        }

        if (rc.hasFlag()) {
            if (rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT) tryReturnBase(rc);
        }

        if (numEnemies > 0 && round >= GameConstants.SETUP_ROUNDS - 4) {
            if (rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                tryAttack(rc);
            }
            boolean isMicro = micro.doMicro(nearbyRobots);
            if (isMicro && rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                nearbyRobots = rc.senseNearbyRobots();
                Communications.updateEnemies(rc, nearbyRobots);
                tryAttack(rc);
            }
            if (!isMicro && rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                BugNavigation.move(rc, nearestEnemy, true);
            }
        } else {
            if (rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                tryDefendFlags(rc);
            }

            boolean collecting = false;

            if (rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                tryHeal(rc);
            }
            if (rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                collecting = tryCollectCrumbs(rc);
                if (rc.getMovementCooldownTurns() >= GameConstants.COOLDOWN_LIMIT) {
                    nearbyRobots = rc.senseNearbyRobots();
                    Communications.updateEnemies(rc, nearbyRobots);
                }
            }
            
            if (round - roundSeek > ROUNDS_NEW_SEEK) {
                seekingEnemy = rng.nextDouble() < SEEK_CHANCE;
            }

            roundSeek = round;

            if (seekingEnemy) {
                if (round >= GameConstants.SETUP_ROUNDS - READY_ROUNDS
                        && rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                    seekEnemy(rc);
                    if (rc.getMovementCooldownTurns() >= GameConstants.COOLDOWN_LIMIT) {
                        nearbyRobots = rc.senseNearbyRobots();
                        Communications.updateEnemies(rc, nearbyRobots);
                    }
                }
            }
            if (!collecting && rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                explore(rc);
                if (rc.getMovementCooldownTurns() >= GameConstants.COOLDOWN_LIMIT) {
                    nearbyRobots = rc.senseNearbyRobots();
                    Communications.updateEnemies(rc, nearbyRobots);
                }
            }
        }
        if (rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
            tryHeal(rc);
        }
        if (numEnemies > 0 && rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT
                && rc.getExperience(SkillType.BUILD) >= Communications.readAverageBuildExp()) {
            tryBuild(rc, TrapType.EXPLOSIVE);
        }
        if (tryTakeFlag(rc)) {
            tryReturnBase(rc);
        }
    }
    
    public static MapLocation getEnemyTarget(MapLocation loc) {
        MapLocation best = null;
        int bestScore = 1000000;
        int score;
        for (RobotInfo info : nearbyRobots) {
            if (info.team != team && info.location.isWithinDistanceSquared(loc, GameConstants.ATTACK_RADIUS_SQUARED)) {
                score = info.health;
                if (info.hasFlag) score -= 1000;

                if (score < bestScore) {
                    bestScore = score;
                    best = info.location;
                }
            }
        }
        return best;
    }

    public static boolean tryAttack(RobotController rc) throws GameActionException {
        MapLocation best = getEnemyTarget(rc.getLocation());
        if (best != null && rc.canAttack(best)) {
            rc.attack(best);
            return true;
        }
        return false;
    }

    public static MapLocation getAllyTarget(MapLocation loc) {
        MapLocation best = null;
        int bestScore = 1000000;
        int score;
        for (RobotInfo info : nearbyRobots) {
            if (info.team == team && info.health < GameConstants.DEFAULT_HEALTH && info.location.isWithinDistanceSquared(loc, GameConstants.HEAL_RADIUS_SQUARED)) {
                score = info.health;
                if (info.hasFlag) score -= 1000;

                if (score < bestScore) {
                    bestScore = score;
                    best = info.location;
                }
            }
        }
        return best;
    }

    public static boolean tryHeal(RobotController rc) throws GameActionException {
        MapLocation best = getAllyTarget(rc.getLocation());
        if (best != null && rc.canHeal(best)) {
            rc.heal(best);
            return true;
        }
        return false;
    }

    public static boolean tryCollectCrumbs(RobotController rc) throws GameActionException {
        MapLocation[] nearbyCrumbs = rc.senseNearbyCrumbs(-1);
        MapLocation loc = rc.getLocation();
        
        MapLocation nearest = null;
        int nearestDist = 1000000;
        
        int dist;
        for (MapLocation nearbyCrumb : nearbyCrumbs) {
            dist = loc.distanceSquaredTo(nearbyCrumb);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = nearbyCrumb;
            }
        }

        if (nearest != null) {
            BugNavigation.move(rc, nearest);
            return true;
        }
        return false;
    }

    public static boolean seekEnemy(RobotController rc) throws GameActionException {
        MapLocation loc = Communications.readEnemies(rc.getLocation());
        return loc != null && BugNavigation.move(rc, loc, true);
    }

    public static boolean explore(RobotController rc) throws GameActionException {
        MapLocation target = explore.getExploreTarget(rc, rng);
        if (BugNavigation.move(rc, target)) {
            return true;
        } else {
            explore.resetExploreTarget(rc, rng);
            return BugNavigation.move(rc, target, true);
        }
    }

    public static boolean tryBuild(RobotController rc, TrapType type) throws GameActionException {
        MapLocation loc = rc.getLocation();

        MapLocation nearest = null;
        int nearestDist = 1000000;

        int dist;
        for (RobotInfo robot : nearbyRobots) {
            if (robot.team != team) {
                dist = loc.distanceSquaredTo(robot.location);
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = robot.location;
                }
            }
        }

        if (nearest != null) {
            Direction d = loc.directionTo(nearest);
            MapLocation l = loc.add(d);
            if (rc.canBuild(type, l)) {
                rc.build(type, l);
                return true;
            }
            
            Direction dr = d.rotateRight();
            l = loc.add(dr);
            if (rc.canBuild(type, l)) {
                rc.build(type, l);
                return true;
            }
            
            d = d.rotateLeft();
            l = loc.add(d);
            if (rc.canBuild(type, l)) {
                rc.build(type, l);
                return true;
            }
        }

        return false;
    }
    
    /**
     * Try to pickup a nearby flag, and if there are no adjacent flags, move towards a flag.
     * Returns: true if flag was picked up, false otherwise.
     */
    public static boolean tryTakeFlag(RobotController rc) throws GameActionException {
        MapLocation loc = rc.getLocation();

        FlagInfo[] flags = rc.senseNearbyFlags(-1, team.opponent());
        
        MapLocation flagLoc = null;
        int nearestDist = 1000000;

        int dist;
        for (FlagInfo flag : flags) {
            if (!flag.isPickedUp()) {
                dist = flag.getLocation().distanceSquaredTo(loc);
                if (dist < nearestDist) {
                    nearestDist = dist;
                    flagLoc = flag.getLocation();
                }
            }
        }

        if (flagLoc != null) {
            if (loc.isWithinDistanceSquared(flagLoc, GameConstants.INTERACT_RADIUS_SQUARED)) {
                if (rc.canPickupFlag(flagLoc)) {
                    rc.pickupFlag(flagLoc);
                    return true;
                }
            } else if (rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                BugNavigation.move(rc, flagLoc, true);
            }
        }
        return false;
    }

    public static boolean tryReturnBase(RobotController rc) throws GameActionException {
        MapLocation loc = rc.getLocation();

        MapLocation[] spawnLocs = rc.getAllySpawnLocations();

        MapLocation nearestSpawn = null;
        int nearestDist = 1000000;

        int dist;
        for (MapLocation spawnLoc : spawnLocs) {
            dist = loc.distanceSquaredTo(spawnLoc);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearestSpawn = spawnLoc;
            }
        }
        
        return nearestSpawn != null && BugNavigation.move(rc, nearestSpawn, true);
    }

    public static boolean tryDefendFlags(RobotController rc) throws GameActionException {
        FlagInfo[] flags = rc.senseNearbyFlags(-1);

        for (FlagInfo flag : flags) {
            if (flag.getTeam() == team && !rc.senseMapInfo(flag.getLocation()).isSpawnZone()) {
                return BugNavigation.move(rc, flag.getLocation(), true);
            } else if (flag.isPickedUp()
                    && rc.getLocation().distanceSquaredTo(flag.getLocation()) > 2) {
                return BugNavigation.move(rc, flag.getLocation(), true);
            }
        }
        return false;
    }
}
