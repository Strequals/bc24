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

    static final Direction[] allDirections = {
        Direction.CENTER,
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
    static final int ROUNDS_MICRO_START_PATHING = 10;
    //static final int MAX_BATCH_ROUNDS = 12;
    //static final int BATCH_DELAY = 4;

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
    static int roundsMicroNoAction = 0;
    static MapLocation nearestEnemy;
    static MapLocation nearestAlly;
    static MapLocation nearestHealingAlly;
    static boolean isThreatened;
    static boolean isAttacked;

    public static void run(RobotController rc) throws GameActionException {
        team = rc.getTeam();
        explore = new Explore(rc);
        micro = new Micro(rc);
        while (true) {
            try {
                round = rc.getRoundNum();
                setup = round <= GameConstants.SETUP_ROUNDS;
                Communications.readArray(rc);
                if (rc.canBuyGlobal(GlobalUpgrade.ATTACK)) {
                    rc.buyGlobal(GlobalUpgrade.ATTACK);
                } else if (rc.canBuyGlobal(GlobalUpgrade.HEALING)) {
                    rc.buyGlobal(GlobalUpgrade.HEALING);
                } else if (rc.canBuyGlobal(GlobalUpgrade.CAPTURING)) {
                    rc.buyGlobal(GlobalUpgrade.CAPTURING);
                }
                if (!rc.isSpawned()) {
                    if (defendSpot != null) {
                        Communications.unclaimDefender(rc, defendSpot);
                        Communications.resetRespawn(rc);
                        defendSpot = null;
                    }
                    BugNavigation.reset();
                    trySpawn(rc);
                    if (lastRoundJailed < round - 1) {
                        jailedRounds = 1;
                    } else {
                        jailedRounds++;
                    }
                    lastRoundJailed = round;
                    Communications.updateRespawn(rc, GameConstants.JAILED_ROUNDS - jailedRounds);
                    //rc.setIndicatorString("JAIL: " + jailedRounds);
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
        MapLocation enemyLoc = Communications.readEnemies(rc, true, explore);

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
            rc.setIndicatorString("SPAWN CLOSEST: " + enemyLoc);

            return;
        } else {
            rc.setIndicatorString("NO NEAREST");
        }
        
        int rr0 = Communications.getRespawnRound(rc, 0);
        int rr1 = Communications.getRespawnRound(rc, 1);
        int rr2 = Communications.getRespawnRound(rc, 2);
        int ind = 0;
        int rec = 0;
        int indRound;
        int recRound;

        if (rr1 < rr0) {
            if (rr2 < rr1) {
                ind = 2;
                indRound = rr2;
                rec = 0;
                recRound = rr0;
            } else {
                ind = 1;
                indRound = rr1;
                if (rr0 < rr2) {
                    rec = 2;
                    recRound = rr2;
                } else {
                    rec = 0;
                    recRound = rr0;
                }
            }
        } else {
            if (rr2 < rr0) {
                ind = 2;
                indRound = rr2;
                rec = 1;
                recRound = rr1;
            } else {
                ind = 0;
                indRound = rr0;
                if (rr1 < rr2) {
                    rec = 2;
                    recRound = rr2;
                } else {
                    rec = 0;
                    recRound = rr0;
                }
            }
        }

        MapLocation respawnFlagLoc;
        /*if (round - indRound <= MAX_BATCH_ROUNDS && round - recRound <= BATCH_DELAY && round >= GameConstants.SETUP_ROUNDS) {
            respawnFlagLoc = Communications.getRespawnLocation(rc, rec);
        } else {
            respawnFlagLoc = Communications.getRespawnLocation(rc, ind);
        }*/
        respawnFlagLoc = Communications.getRespawnLocation(rc, ind);
        
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

        if (round == 200) BugNavigation.clearBanned();

        nearbyRobots = rc.senseNearbyRobots();
        nearbyCrumbs = rc.senseNearbyCrumbs(-1);
        Communications.updateEnemies(rc, nearbyRobots, nearbyFlags);
        numAllies = 0;
        numEnemies = 0;
        MapLocation curr = rc.getLocation();

        nearestEnemy = null;
        int neDist = 1000000;
        
        nearestAlly = null;
        int naDist = 1000000;

        nearestHealingAlly = null;
        int haDist = 1000000;
        
        int eDist;
        int aDist;
        for (RobotInfo info : nearbyRobots) {
            if (info.team == team) {
                numAllies++;
                aDist = info.location.distanceSquaredTo(curr);
                if (aDist < naDist) {
                    naDist = aDist;
                    nearestAlly = info.location;
                }
                if (info.getAttackLevel() >= 4 || info.getBuildLevel() >= 4 || info.getHealLevel() <= 2) {
                    if (aDist < haDist) {
                        haDist = aDist;
                        nearestHealingAlly = info.location;
                    }
                }
            } else {
                numEnemies++;
                eDist = info.location.distanceSquaredTo(curr);
                if (eDist < neDist) {
                    neDist = eDist;
                    nearestEnemy = info.location;
                }
            }
        }
        isThreatened = nearestEnemy != null && nearestEnemy.add(nearestEnemy.directionTo(curr)).isWithinDistanceSquared(curr, GameConstants.ATTACK_RADIUS_SQUARED);
        isAttacked = nearestEnemy != null && nearestEnemy.isWithinDistanceSquared(curr, GameConstants.ATTACK_RADIUS_SQUARED);

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
        
        boolean retbase = false;
        if (rc.hasFlag() || (rc.isActionReady() && rc.isMovementReady() && numAllies >= numEnemies && tryTakeFlag(rc))) {
            if (rc.isMovementReady()) {
                tryReturnBase(rc);
                retbase = true;
            }
        }

        if (numEnemies > 0 && round >= GameConstants.SETUP_ROUNDS - 4) {
            if (isBuilder && !isAttacked && rc.isActionReady() && rc.getCrumbs() >= TrapType.EXPLOSIVE.buildCost) {
                tryBuildAggro(rc, nearestEnemy);
                //tryBuildStuns(rc);
            }

            if (nearbyCrumbs.length > 0 && rc.isActionReady()) tryStunForCrumbs(rc);

            if (rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                tryAttack(rc);
            }
            if (nearbyCrumbs.length > 0) {
                //if (rc.isActionReady()) tryStunForCrumbs(rc);
                if (rc.isMovementReady()) {
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
            }

            if (roundsMicroNoAction > ROUNDS_MICRO_START_PATHING && rc.canSenseLocation(nearestEnemy) && !hasLineOfSight(rc, curr, nearestEnemy) && rc.isMovementReady()) {
                BugNavigation.move(rc, nearestEnemy, true);
            }
            boolean aggro = nearbyCrumbs.length > 0;
            boolean isMicro = rc.isMovementReady() && micro.doMicro(nearbyRobots, defendSpot, aggro);
            if (isMicro && rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                nearbyRobots = rc.senseNearbyRobots();
                Communications.updateEnemies(rc, nearbyRobots, nearbyFlags);
                tryAttack(rc);
            }
            if (!isMicro && rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                BugNavigation.move(rc, nearestEnemy, true);
            }

            if (nearbyCrumbs.length > 0 && rc.isActionReady()) {
                tryStunForCrumbs(rc);
            }

            if (rc.isActionReady()) {
                roundsMicroNoAction++;
            } else {
                roundsMicroNoAction = 0;
            }
        } else {
            
            boolean defendingFlag = false;
            if (rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                defendingFlag = defendingFlag || tryDefendFlags(rc);
            }
            

            if (rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                if (!tryHeal(rc) && rc.isMovementReady() && tryMoveHeal(rc)) {
                    tryHeal(rc);
                }
            }

            if (defendSpot != null) {
                if (rc.isMovementReady()
                        && !rc.getLocation().equals(defendSpot)) {
                    BugNavigation.move(rc, defendSpot, false);
                }
                //tryBuildDefenses(rc);
            } else {
                boolean collecting = false;
                if (rc.isMovementReady() && !defendingFlag) {
                    if (tryCollectCrumbs(rc, true)) {
                        collecting = true;
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

                if (rc.getHealth() > 750 || nearestHealingAlly == null) {
                    boolean seeking = false;
                    if (!collecting && seekingEnemy) {
                        if (round >= GameConstants.SETUP_ROUNDS - READY_ROUNDS
                                && rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT
                                && !defendingFlag) {
                            if (seekEnemy(rc)) {
                                seeking = true;
                                nearbyRobots = rc.senseNearbyRobots();
                                Communications.updateEnemies(rc, nearbyRobots, nearbyFlags);
                            }
                        }
                    }
                    if (!collecting && !seeking && rc.isMovementReady() && !defendingFlag) {
                        rc.setIndicatorString("EXPLORING. seeking enemy? " + seekingEnemy);
                        explore(rc);
                        if (rc.getMovementCooldownTurns() >= GameConstants.COOLDOWN_LIMIT) {
                            nearbyRobots = rc.senseNearbyRobots();
                            Communications.updateEnemies(rc, nearbyRobots, nearbyFlags);
                        }
                    }
                } else {
                    if (nearestHealingAlly.distanceSquaredTo(curr) > GameConstants.HEAL_RADIUS_SQUARED) {
                        BugNavigation.move(rc, nearestHealingAlly, true, true);
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


        if (tryTakeFlag(rc) && rc.isMovementReady()) {
            tryReturnBase(rc);
        }

        if (isBuilder) {
            tryDigAndBuild(rc);
        }

        if (!isThreatened && rc.isActionReady()) {
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
            if (info.team == team && rc.canHeal(info.location)) {
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
        if (rc.hasFlag()) return false;
        /*if (rc.getExperience(SkillType.HEAL) == SkillType.HEAL.getExperience(4)-1
                && rc.getExperience(SkillType.ATTACK) < SkillType.ATTACK.getExperience(4)
                && rc.getExperience(SkillType.BUILD) < SkillType.BUILD.getExperience(4)) {
            return false;
        }*/
        MapLocation best = getAllyTarget(rc, rc.getLocation());
        if (best != null) {
            rc.heal(best);
            return true;
        }
        return false;
    }

    public static boolean tryMoveHeal(RobotController rc) throws GameActionException {
        if (rc.hasFlag()) return false;
        //if (rc.getExperience(SkillType.HEAL) == SkillType.HEAL.getExperience(4)-1) return false;
        /*if (rc.getExperience(SkillType.HEAL) == SkillType.HEAL.getExperience(4)-1
                && rc.getExperience(SkillType.ATTACK) < SkillType.ATTACK.getExperience(4)
                && rc.getExperience(SkillType.BUILD) < SkillType.BUILD.getExperience(4)) {
            return false;
        }*/
        /*MapLocation best = null;
        MapLocation curr = rc.getLocation();
        double bestScore = 1000000;
        double score;
        for (RobotInfo info : nearbyRobots) {
            if (info.team == team && info.health < GameConstants.DEFAULT_HEALTH - 80) {
                score = info.location.distanceSquaredTo(curr) + ((double) info.health) / GameConstants.DEFAULT_HEALTH;
                if (score < bestScore) {
                    bestScore = score;
                    best = info.location;
                }
            }
        }

        if (best != null && bestScore > GameConstants.HEAL_RADIUS_SQUARED) {
            MapLocation m;
            for (Direction d : directions) {
                m = curr.add(d);
                if (rc.canMove(d) && m.isWithinDistanceSquared(best, GameConstants.HEAL_RADIUS_SQUARED)) {
                    rc.move(d);
                    return true;
                }
            }
        }
        return false;*/

        MapLocation curr = rc.getLocation();
        MapLocation[] m = new MapLocation[8];
        double[] scores = new double[8];
        for (int i = 8; i-->0;) {
            m[i] = curr.add(directions[i]);
            scores[i] = 1000000;
        }
        double score;
        int dist;
        for (RobotInfo info : nearbyRobots) {
            if (info.team == team && info.health < GameConstants.DEFAULT_HEALTH - 80) {
                for (int i = 8; i-->0;) {
                    dist = m[i].distanceSquaredTo(info.location);
                    if (dist <= GameConstants.HEAL_RADIUS_SQUARED) {
                        //score = info.health + ((double) dist / 100);
                        score = info.health;
                        if (info.hasFlag) score -= 1000;
                        if (score < scores[i]) scores[i] = score;
                    }
                }
            }
        }

        for (int i = 0; i < 8; i += 2) {
            scores[i] += 0.5;
        }

        int bestIndex = -1;
        double bestScore = 1000000;
        for (int i = 8; i-->0;) {
            if (rc.canMove(directions[i]) && scores[i] < bestScore) {
                bestScore = scores[i];
                bestIndex = i;
            }
        }

        if (bestIndex >= 0) {
            rc.move(directions[bestIndex]);
            return true;
        }
        return false;
    }

    public static int manhattan(MapLocation a, MapLocation b) {
        return StrictMath.abs(a.x - b.x) + StrictMath.abs(a.y - b.y);
    }

    public static boolean tryCollectCrumbs(RobotController rc, boolean fill) throws GameActionException {
        MapLocation loc = rc.getLocation();
        
        MapLocation best = null;
        double bestScore = 1000000;
        
        double score;
        MapInfo mi;
        for (MapLocation nearbyCrumb : nearbyCrumbs) {
            mi = rc.senseMapInfo(nearbyCrumb);
            score = loc.distanceSquaredTo(nearbyCrumb) + 1.0 / mi.getCrumbs();
            if (mi.getTrapType() == TrapType.STUN) score -= 4; // Lower priority if trap is built
            if (score < bestScore && !BugNavigation.isBanned(nearbyCrumb)) {
                bestScore = score;
                best = nearbyCrumb;
            }
        }

        if (best != null) {
            return BugNavigation.move(rc, best, true, fill);
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
        if (rc.hasFlag()) return false;
        MapLocation loc = rc.getLocation();

        if (nearestEnemy != null && rc.canSenseLocation(nearestEnemy) && hasLineOfSight(rc, loc, nearestEnemy)) {
            Direction d = loc.directionTo(nearestEnemy);
            MapLocation[] pos = {loc.add(d), loc.add(d.rotateLeft()), loc.add(d.rotateRight()),
                loc, loc.add(d.rotateLeft().rotateLeft()), loc.add(d.rotateRight().rotateRight())};
            //MapLocation[] pos = new MapLocation[9];
            //for (int i = 0; i < 9; i++) pos[i] = loc.add(allDirections[i]);
            //pos[8] = loc;
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
            int dist;
            for (int i = 0; i < pos.length; i++) {
                if (!elim[i] && rc.canBuild(type, pos[i])) {
                    dist = pos[i].distanceSquaredTo(nearestEnemy);
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

        if (rc.hasFlag() && isThreatened && nearestAlly != null && nearestAlly.distanceSquaredTo(loc) > 2) {
            MapLocation[] m = new MapLocation[9];
            for (int i = 9; i-->0;) m[i] = loc.add(allDirections[i]);
            double[] scores = new double[9];
            for (int i = 9; i-->0;) scores[i] = 0;
            MapLocation next;
            for (RobotInfo robot : nearbyRobots) {
                for (int i = 9; i-->0;) {
                    if (robot.team == team) {
                        scores[i] -= m[i].distanceSquaredTo(robot.location);
                    } else {
                        scores[i] += m[i].distanceSquaredTo(robot.location);
                    }
                }
            }

            int bestIndex = -1;
            double bestScore = -1000000;
            for (int i = 9; i-->0;) {
                if (rc.canMove(allDirections[i]) && scores[i] > bestScore) {
                    bestScore = scores[i];
                    bestIndex = i;
                }
            }

            if (bestIndex >= 0 && allDirections[bestIndex] != Direction.CENTER) {
                rc.move(allDirections[bestIndex]);
                return true;
            }
        }

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

    public static boolean tryMoveAway(RobotController rc, MapLocation loc) throws GameActionException {
        Direction farthest = Direction.CENTER;
        int farthestDist = 0;
        int dist;
        MapLocation curr = rc.getLocation();
        MapLocation l;
        for (Direction d : directions) {
            if (!rc.canMove(d)) continue;
            l = curr.add(d);
            dist = l.distanceSquaredTo(loc);
            if (dist > farthestDist) {
                farthestDist = dist;
                farthest = d;
            }
        }

        if (farthestDist > 0) {
            rc.move(farthest);
            return true;
        }
        return false;
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
        
        MapLocation curr = rc.getLocation();
        MapLocation closest = null;
        int closestDist = 1000000;
        int dist;
        for (RobotInfo nearbyRobot : nearbyRobots) {
            if (nearbyRobot.team == team && nearbyRobot.hasFlag) {
                dist = nearbyRobot.location.distanceSquaredTo(curr);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = nearbyRobot.location;
                }
            }
        }
        if (closest != null) {
            if (closestDist <= 2) return tryMoveAway(rc, closest) || tryReturnBase(rc);
            else if (closestDist > 8) return BugNavigation.move(rc, closest, true);
            else return true;
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

        tryBuild(rc, 0, TrapType.STUN);
        //tryBuild(rc, 0, TrapType.EXPLOSIVE);
        rc.setIndicatorString("Build aggressive");
    }

    public static void tryBuildStuns(RobotController rc) throws GameActionException {
        MapLocation curr = rc.getLocation();
        if (!rc.canSenseLocation(nearestEnemy) || !hasLineOfSight(rc, curr, nearestEnemy)) {
            return;
        }
        /*int i = (curr.x + curr.y) % 2;
        MapLocation m;
        MapInfo mi;
        for (int i = 4; i-->-4;) {
            for (int j = 4; j-->-4;) {
                if ((i + j) % 2 == 1) continue;
                m = curr.translate(i, j);
                if (rc.canSenseLocation(m)) {
                    mi = rc.senseMapInfo(
                }
            }
        }*/



        MapLocation m;
        double score;
        MapLocation best = null;
        double bestScore = 0;
        MapLocation bestBuild = null;
        double bestBuildScore = 0;
        int dist;

        for (Direction d : allDirections) {
            m = curr.add(d);
            if ((m.x + m.y) % 2 == 0 && rc.canBuild(TrapType.STUN, m)) {
                score = 1.0 / m.distanceSquaredTo(nearestEnemy);
                if (score > bestBuildScore) {
                    bestBuildScore = score;
                    bestBuild = m;
                }
            }
        }

        if (bestBuild != null) {
            rc.build(TrapType.STUN, bestBuild);
            bestBuild = null;
        }

        for (MapInfo mi : rc.senseNearbyMapInfos(-1)) {
            m = mi.getMapLocation();
            dist = m.distanceSquaredTo(nearestEnemy);
            if ((m.x + m.y) % 2 == 0 && mi.isPassable() && mi.getTrapType() == TrapType.NONE
                    && dist > 2 && dist < GameConstants.VISION_RADIUS_SQUARED) {
                score = 1.0 / dist;
                if (score > bestScore) {
                    bestScore = score;
                    best = m;
                }
                if (rc.canBuild(TrapType.STUN, m) && score > bestBuildScore) {
                    bestBuildScore = score;
                    bestBuild = m;
                }
            }
        }

        if (rc.isMovementReady() && best != null) {
            BugNavigation.move(rc, best, true, true);
        }

        if (bestBuild != null) {
            rc.build(TrapType.STUN, bestBuild);
        }

    }

    public static void tryStunForCrumbs(RobotController rc) throws GameActionException {
        MapLocation curr = rc.getLocation();
        MapLocation m;
        MapInfo mi;

        MapLocation closest = null;
        int closestDist = 1000000;
        int dist;
        int num = 0;
        for (Direction d : directions) {
            m = curr.add(d);
            mi = rc.senseMapInfo(m);
            if (mi.getCrumbs() >= 25) {
                num++;
                dist = m.distanceSquaredTo(nearestEnemy);
                if (dist < closestDist && rc.canBuild(TrapType.STUN, m)) {
                    closestDist = dist;
                    closest = m;
                }
            }
        }

        if (closest != null && num >= 2 && closestDist < GameConstants.VISION_RADIUS_SQUARED) {
            rc.build(TrapType.STUN, closest);
        }
    }

    public static void tryDigAndBuild(RobotController rc) throws GameActionException {
        MapLocation curr = rc.getLocation();
        boolean isMaxed = rc.getExperience(SkillType.BUILD) >= SkillType.BUILD.getExperience(6);
        if (isMaxed) {
            boolean shouldBuild = (numEnemies >= 2 && numAllies >= 2);
            if (shouldBuild && rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT
                    && round >= GameConstants.SETUP_ROUNDS - READY_ROUNDS
                    && rc.getCrumbs() > TrapType.EXPLOSIVE.buildCost + 50) {
                if (!tryBuild(rc, 5, TrapType.STUN)) {
                    //if (!tryBuild(rc, 13, TrapType.EXPLOSIVE)) {
                        /*if (rc.getCrumbs() > 2 * TrapType.EXPLOSIVE.buildCost + 50) {
                            tryBuild(rc, 0, TrapType.EXPLOSIVE);
                        }*/
                    //}
                }
                //tryBuildStuns(rc);
            }
        }
        if (!isMaxed) {
            if (!isThreatened) {
            MapLocation m;
                for (Direction d : directions) {
                    m = curr.add(d);
                    if (/*(m.x + m.y) % 2 == 1 &&*/ rc.canDig(m)) {
                        rc.dig(m);
                    }
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
            /*if (rc.canBuild(TrapType.STUN, nearbyFlag.getLocation())) {
                rc.build(TrapType.STUN, nearbyFlag.getLocation());
                continue;
            }
            if (rc.canSenseLocation(nearbyFlag.getLocation())) {
                mi = rc.senseMapInfo(nearbyFlag.getLocation());
                if (mi.getTrapType() == TrapType.NONE) {
                    dist = nearbyFlag.getLocation().distanceSquaredTo(curr);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = nearbyFlag.getLocation();
                    }
                }
            }*/
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
