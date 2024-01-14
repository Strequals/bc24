package egg;

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

    static final Direction[] diagonals = {
        Direction.NORTHEAST,
        Direction.SOUTHEAST,
        Direction.SOUTHWEST,
        Direction.NORTHWEST,
    };

    //static final Direction[] 

    static final Random rng = new Random();
    static final int READY_ROUNDS = 30;
    static final int ROUNDS_NEW_SEEK = 5;
    static final double SEEK_CHANCE = 1;
    static final int TURNS_SWITCH_DEFENDER = 100;
    static final int ROUNDS_DONT_CLAIM = 30; // after leaving defense dont go back for this many rounds
    static final int NUM_BUILDERS = 3;
    static final int DIG_ROUND = 1950;

    static RobotInfo[] nearbyRobots;
    static MapLocation[] nearbyCrumbs;
    static FlagInfo[] enemyFlags;
    static Team team;
    static int round;

    static MapLocation exploreTarget = null;
    static Explore explore;
    static Micro micro;
    static boolean setup;
    static int turnsQuietlyDefended;

    static int roundSeek = -100;
    static boolean seekingEnemy;

    static MapLocation defendSpot = null;
    static int lastRoundDefended = 0;
    static int lastRoundJailed = 0;
    static int jailedRounds = 0;
    static boolean isBuilder = false;
    static int numAllies;
    static int numEnemies;

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
                    if (lastRoundJailed < round - 1) {
                        jailedRounds = 1;
                    } else {
                        jailedRounds++;
                    }
                    lastRoundJailed = round;
                    Communications.updateRespawn(rc, GameConstants.JAILED_ROUNDS - jailedRounds);
                    rc.setIndicatorString("JAIL: " + jailedRounds);
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
        if (!isBuilder && defendSpot == null) {
            defendSpot = Communications.tryClaimDefender(rc, false);
            turnsQuietlyDefended = 0;
        }

        if (defendSpot != null) {
            if (rc.canSpawn(defendSpot)) {
                rc.spawn(defendSpot);
            }
            MapLocation l;

            for (Direction d : directions) {
                l = defendSpot.add(d);
                if (rc.canSpawn(l)) {
                    rc.spawn(l);
                }
            }
            return;
        }

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
        
        int rr0 = Communications.getRespawnRound(rc, 0);
        int rr1 = Communications.getRespawnRound(rc, 1);
        int rr2 = Communications.getRespawnRound(rc, 2);
        int ind = 0;

        if (rr1 < rr0) {
            if (rr2 < rr1) {
                ind = 2;
            } else {
                ind = 1;
            }
        } else {
            if (rr2 < rr0) {
                ind = 2;
            } else {
                ind = 0;
            }
        }
        
        MapLocation respawnFlagLoc = Communications.getRespawnLocation(rc, ind);

        if (respawnFlagLoc != null) {
            MapLocation bestAdj = null;
            int baDist = 1000000;

            enemyLoc = Communications.readEnemies(rc, respawnFlagLoc, true, explore);
            if (enemyLoc == null) {
                enemyLoc = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
            }

            MapLocation rflAdj;
            int aDist;
            for (Direction d : directions) {
                rflAdj = respawnFlagLoc.add(d);
                if (rc.canSpawn(rflAdj)) {
                    aDist = rflAdj.distanceSquaredTo(enemyLoc);
                    if (aDist < baDist) {
                        bestAdj = rflAdj;
                        baDist = aDist;
                    }
                }
            }

            if (bestAdj != null) {
                rc.spawn(bestAdj);
                Communications.updateRespawnRound(rc, ind);
                return;
            }
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

        FlagInfo[] nearbyFlags = rc.senseNearbyFlags(-1, team);
        enemyFlags = rc.senseNearbyFlags(-1, team.opponent());
        if (round < GameConstants.SETUP_ROUNDS) {
            if (nearbyFlags.length > 0) {
                Communications.updateFlagSpawn(rc, nearbyFlags);
            }
        }

        if (defendSpot == null && Communications.countBuilders() < NUM_BUILDERS) {
            Communications.claimBuilder(rc);
            isBuilder = true;
        }


        if (!isBuilder && defendSpot == null && lastRoundDefended < round - ROUNDS_DONT_CLAIM) {
            defendSpot = Communications.tryClaimDefender(rc, true);
            turnsQuietlyDefended = 0;
        }


        nearbyRobots = rc.senseNearbyRobots();
        nearbyCrumbs = rc.senseNearbyCrumbs(-1);
        Communications.updateEnemies(rc, nearbyRobots, nearbyFlags);
        numAllies = 0;
        numEnemies = 0;
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
        boolean isThreatened = nearestEnemy != null && nearestEnemy.add(nearestEnemy.directionTo(curr)).isWithinDistanceSquared(curr, GameConstants.ATTACK_RADIUS_SQUARED);
        boolean isAttacked = nearestEnemy != null && nearestEnemy.isWithinDistanceSquared(curr, GameConstants.ATTACK_RADIUS_SQUARED);

        if (defendSpot != null && round >= GameConstants.SETUP_ROUNDS) {
            turnsQuietlyDefended++;
            lastRoundDefended = round;
            if (numEnemies > 0) turnsQuietlyDefended = 0;

            if (turnsQuietlyDefended > TURNS_SWITCH_DEFENDER && Communications.getRespawn(rc) <= 2) {
                if (nearbyFlags.length > 0) {
                    // if no flags nearby and no enemies have been seen for 100 rounds, it's probably captured.
                    Communications.unclaimDefender(rc, defendSpot);
                    Communications.resetRespawn(rc);
                }
                defendSpot = null;
            }
        }

        if (rc.hasFlag() || (rc.isActionReady() && rc.isMovementReady() && numAllies > numEnemies && tryTakeFlag(rc))) {
            if (rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT) tryReturnBase(rc);
        }

        if (numEnemies > 0 && round >= GameConstants.SETUP_ROUNDS - 4) {
            if (isBuilder && !isAttacked && rc.isActionReady() && rc.getCrumbs() >= TrapType.EXPLOSIVE.buildCost) {
                tryBuildAggro(rc, nearestEnemy);
            }

            if (rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                tryAttack(rc);
            }
            if (rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT && nearbyCrumbs.length > 0) {
                if (nearestEnemy.isWithinDistanceSquared(curr, 4)) {
                    if (tryCollectCrumbs(rc, true)) {
                        nearbyRobots = rc.senseNearbyRobots();
                        Communications.updateEnemies(rc, nearbyRobots, nearbyFlags);
                    }
                } else {
                    if (tryCollectCrumbs(rc, false)) {
                        nearbyRobots = rc.senseNearbyRobots();
                        Communications.updateEnemies(rc, nearbyRobots, nearbyFlags);
                    }
                }
            }
            boolean aggro = nearbyCrumbs.length > 0;
            boolean isMicro = micro.doMicro(nearbyRobots, defendSpot, aggro);
            if (isMicro && rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                nearbyRobots = rc.senseNearbyRobots();
                Communications.updateEnemies(rc, nearbyRobots, nearbyFlags);
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

            if (defendSpot != null) {
                if (rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT
                        && !rc.getLocation().equals(defendSpot)) {
                    BugNavigation.move(rc, defendSpot, false);
                }
                //tryBuildDefenses(rc);
            } else {
                if (rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                    collecting = tryCollectCrumbs(rc, true);
                    if (rc.getMovementCooldownTurns() >= GameConstants.COOLDOWN_LIMIT) {
                        nearbyRobots = rc.senseNearbyRobots();
                        Communications.updateEnemies(rc, nearbyRobots, nearbyFlags);
                    }
                }

                if (isBuilder) {
                    tryBuildBuilderDefense(rc, nearbyFlags);
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
                            Communications.updateEnemies(rc, nearbyRobots, nearbyFlags);
                        }
                    }
                }
                if (!collecting && rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                    rc.setIndicatorString("EXPLORING. seeking enemy? " + seekingEnemy);
                    explore(rc);
                    if (rc.getMovementCooldownTurns() >= GameConstants.COOLDOWN_LIMIT) {
                        nearbyRobots = rc.senseNearbyRobots();
                        Communications.updateEnemies(rc, nearbyRobots, nearbyFlags);
                    }
                }
            }
        }
        /*if (numEnemies > 0 && rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT
                && round >= GameConstants.SETUP_ROUNDS - READY_ROUNDS
                && rc.getExperience(SkillType.BUILD) >= Communications.readAverageBuildExp()) {
            if (rc.getExperience(SkillType.BUILD) >= SkillType.BUILD.getExperience(3)) {
                tryBuild(rc, 0, TrapType.EXPLOSIVE);
            } else if (rc.getCrumbs() > TrapType.EXPLOSIVE.buildCost) {
                tryBuild(rc, 5, TrapType.STUN);
            }
        }*/


        if (tryTakeFlag(rc)) {
            tryReturnBase(rc);
        }

        if (isBuilder) {
            tryDigAndBuild(rc);
        }

        if (!isThreatened && rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
            tryHeal(rc);
        }

        if (round > DIG_ROUND && !isThreatened && rc.isActionReady()) {
            tryDig(rc);
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

    public static MapLocation getAllyTarget(RobotController rc, MapLocation loc) {
        MapLocation best = null;
        int bestScore = 1000000;
        int score;
        //int healThreshold = (rc.getExperience(SkillType.HEAL) < SkillType.HEAL.getExperience(4)-1 || rc.getExperience(SkillType.ATTACK) >= SkillType.ATTACK.getExperience(4) || rc.getExperience(SkillType.BUILD) >= SkillType.BUILD.getExperience(4) ? GameConstants.DEFAULT_HEALTH - 80 : 0);
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
        MapLocation best = getAllyTarget(rc, rc.getLocation());
        if (best != null && rc.canHeal(best)) {
            rc.heal(best);
            return true;
        }
        return false;
    }

    public static boolean tryCollectCrumbs(RobotController rc, boolean fill) throws GameActionException {
        MapLocation loc = rc.getLocation();
        
        MapLocation best = null;
        double bestScore = 1000000;
        
        double score;
        for (MapLocation nearbyCrumb : nearbyCrumbs) {
            score = loc.distanceSquaredTo(nearbyCrumb) + 1.0 / rc.senseMapInfo(nearbyCrumb).getCrumbs();
            if (score < bestScore) {
                bestScore = score;
                best = nearbyCrumb;
            }
        }

        if (best != null) {
            BugNavigation.move(rc, best, true, fill);
            return true;
        }
        return false;
    }

    public static boolean seekEnemy(RobotController rc) throws GameActionException {
        MapLocation loc = Communications.readEnemies(rc, rc.getLocation(), true, explore);

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

    public static boolean hasLineOfSight(RobotController rc, MapLocation from, MapLocation to) throws GameActionException {
        MapLocation m = from.add(from.directionTo(to));
        MapInfo mi;
        while (!m.equals(to)) {
            mi = rc.senseMapInfo(m);
            if (mi.isWater() || mi.isWall()) return false;
            m = m.add(m.directionTo(to));
        }
        return true;
    }

    public static boolean tryBuild(RobotController rc, int separation, TrapType type) throws GameActionException {
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

        if (nearest != null && hasLineOfSight(rc, loc, nearest)) {
            Direction d = loc.directionTo(nearest);
            //MapLocation[] pos = {loc.add(d), loc.add(d.rotateLeft()), loc.add(d.rotateRight()), loc, loc.add(d.rotateLeft().rotateLeft()), loc.add(d.rotateRight().rotateRight())};
            MapLocation[] pos = new MapLocation[9];
            for (int i = 0; i < 8; i++) pos[i] = loc.add(directions[i]);
            pos[8] = loc;
            boolean[] elim = new boolean[pos.length];

            if (separation > 0) {
                MapInfo[] infos = rc.senseNearbyMapInfos(18);
                for (MapInfo info : infos) {
                    if (info.getTrapType() == type) {
                        for (int i = pos.length; i-->0;) {
                            if (pos[i].isWithinDistanceSquared(info.getMapLocation(), separation)) elim[i] = true;
                        }
                    }
                }
            }
            
            int closestDist = 1000000;
            int closestI = -1;

            for (int i = 0; i < pos.length; i++) {
                if (!elim[i] && rc.canBuild(type, pos[i])) {
                    dist = pos[i].distanceSquaredTo(loc);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closestI = i;
                    }
                }
            }
            if (closestI >= 0) {
                rc.build(type, pos[closestI]);
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

        
        MapLocation flagLoc = null;
        int nearestDist = 1000000;

        int dist;
        for (FlagInfo flag : enemyFlags) {
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
            if (flag.getTeam() == team && !rc.senseMapInfo(flag.getLocation()).isSpawnZone()
                    && rc.getLocation().distanceSquaredTo(flag.getLocation()) > 2) {
                return BugNavigation.move(rc, flag.getLocation(), true);
            } else if (flag.isPickedUp()
                    && rc.getLocation().distanceSquaredTo(flag.getLocation()) > 8) {
                return BugNavigation.move(rc, flag.getLocation(), true);
            }
        }
        return false;
    }

    public static boolean tryBuildDefenses(RobotController rc) throws GameActionException {
        MapLocation l;
        for (int i = 1; i < 8; i += 2) {
            l = defendSpot.add(directions[i]);
            if (rc.canBuild(TrapType.STUN, l)) {
                rc.build(TrapType.STUN, l);
                return true;
            }
        }
        /*if (rc.canBuild(TrapType.STUN, defendSpot)) {
            rc.build(TrapType.STUN, defendSpot);
            return true;
        }*/
        /*for (int i = 0; i < 8; i += 2) {
            l = defendSpot.add(directions[i]);
            if (rc.canBuild(TrapType.EXPLOSIVE, l)) {
                rc.build(TrapType.EXPLOSIVE, l);
                return true;
            }
        }*/
        return false;
    }

    public static void tryBuildAggro(RobotController rc, MapLocation target) throws GameActionException {
        if (rc.getExperience(SkillType.BUILD) < SkillType.BUILD.getExperience(6) + SkillType.BUILD.getPenalty(6)) return;
        MapLocation curr = rc.getLocation();
        Direction d = curr.directionTo(target);
        MapLocation next = curr.add(curr.directionTo(target));
        if (!next.isWithinDistanceSquared(target, GameConstants.ATTACK_RADIUS_SQUARED) && rc.canMove(d)) {
            MapLocation ml1 = next.add(d);
            MapLocation ml2 = next.add(d.rotateLeft());
            MapLocation ml3 = next.add(d.rotateRight());
            MapInfo mi1 = (rc.onTheMap(ml1) ? rc.senseMapInfo(ml1) : null);
            MapInfo mi2 = (rc.onTheMap(ml2) ? rc.senseMapInfo(ml2) : null);
            MapInfo mi3 = (rc.onTheMap(ml3) ? rc.senseMapInfo(ml3) : null);
            if ((mi1 != null && mi1.isPassable() && mi1.getTrapType() == TrapType.NONE)
                    || (mi2 != null && mi2.isPassable() && mi2.getTrapType() == TrapType.NONE)
                    || (mi3 != null && mi3.isPassable() && mi3.getTrapType() == TrapType.NONE)) {
                rc.move(d);
                //System.out.println("Moving to build aggressively");
            }
        }

        tryBuild(rc, 2, TrapType.STUN);
        tryBuild(rc, 0, TrapType.EXPLOSIVE);
        rc.setIndicatorString("Build aggressive");
    }

    public static void tryDigAndBuild(RobotController rc) throws GameActionException {
        MapLocation curr = rc.getLocation();
        if (rc.getExperience(SkillType.BUILD) >= SkillType.BUILD.getExperience(6)) {
            boolean shouldBuild = (numEnemies > 1 && numAllies >= 1);
            if (shouldBuild && rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT
                    && round >= GameConstants.SETUP_ROUNDS - READY_ROUNDS
                    && rc.getCrumbs() > TrapType.EXPLOSIVE.buildCost + 50) {
                if (!tryBuild(rc, 5, TrapType.STUN)) {
                    if (!tryBuild(rc, 13, TrapType.EXPLOSIVE)) {
                        /*if (rc.getCrumbs() > 2 * TrapType.EXPLOSIVE.buildCost + 50) {
                            tryBuild(rc, 0, TrapType.EXPLOSIVE);
                        }*/
                    }
                }
            }
        } else {
            MapLocation m;
            for (Direction d : directions) {
                m = curr.add(d);
                if (rc.canDig(m)) {
                    rc.dig(m);
                }
            }
        }
    }

    public static void tryBuildBuilderDefense(RobotController rc, FlagInfo[] nearbyFlags) throws GameActionException {
        if (rc.getExperience(SkillType.BUILD) < SkillType.BUILD.getExperience(6)
                || rc.getCrumbs() < TrapType.STUN.buildCost) return;
        MapLocation curr = rc.getLocation();
        
        int closestDist = 1000000;
        MapLocation closest = null;

        MapLocation m;
        MapInfo mi;
        int dist;
        for (FlagInfo nearbyFlag : nearbyFlags) {
            if (!nearbyFlag.getLocation().isWithinDistanceSquared(curr, 18)) continue;
            for (Direction d : diagonals) {
                m = nearbyFlag.getLocation().add(d);
                if (!rc.canSenseLocation(m)) continue;
                if (rc.canBuild(TrapType.STUN, m)) {
                    rc.build(TrapType.STUN, m);
                    continue;
                }

                mi = rc.senseMapInfo(m);
                if (mi.getTrapType() == TrapType.NONE) {
                    dist = m.distanceSquaredTo(curr);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = m;
                    }
                }
            }
        }

        if (closest != null) BugNavigation.move(rc, closest, true, true);
    }

    public static void tryDig(RobotController rc) throws GameActionException {
        MapLocation m;
        MapLocation curr = rc.getLocation();
        for (Direction d : directions) {
            m = curr.add(d);
            if ((m.x + m.y) % 2 == 0 && rc.canDig(m)) {
                rc.dig(m);
                return;
            }
        }
    }
}
