package duck0125v2;

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
    static final int NUM_HEALERS = 25;
    static final int DIG_ROUND = 1950;
    static final int ROUNDS_MICRO_START_PATHING = 10;
    static final int AVOID_DIST = GameConstants.VISION_RADIUS_SQUARED;
    //static final int MAX_BATCH_ROUNDS = 12;
    //static final int BATCH_DELAY = 4;

    static RobotInfo[] nearbyRobots;
    static MapLocation[] nearbyCrumbs;
    static MapInfo[] nearbyMapInfos;
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
    static boolean isMaxedBuild = false;
    static int numAllies;
    static int numCombatAllies;
    static int numEnemies;
    static int roundsMicroNoAction = 0;
    static MapLocation nearestEnemy;
    static MapLocation nearestAlly;
    static MapLocation nearestHealingAlly;
    static MapLocation nearestBuilder;
    static MapLocation nearestFlagHolder;
    static int nfDist;
    static boolean isThreatened;
    static boolean isAttacked;
    static boolean isHealer = false;
    static MapLocation center;
    
    static MapLocationMultiSet previousTraps;
    static String stunned0 = "";
    static String stunned1 = "";
    static String stunned2 = "";
    static String stunned3 = "";

    public static void run(RobotController rc) throws GameActionException {
        team = rc.getTeam();
        explore = new Explore(rc);
        micro = new Micro(rc);
        previousTraps = new MapLocationMultiSet();
        center = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
        while (true) {
            try {
                if (rc.getRoundNum() - round > 1) {
                    System.out.println("OUT OF BYTECODE");
                }
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
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        if (round < GameConstants.SETUP_ROUNDS) {
            MapLocation rfl1 = Communications.getRespawnLocation(rc, 0);
            MapLocation rfl2 = Communications.getRespawnLocation(rc, 1);
            MapLocation rfl3 = Communications.getRespawnLocation(rc, 2);
            for (MapLocation m : spawnLocs) {
                if ((rfl1 != null && m.isAdjacentTo(rfl1))
                        || (rfl2 != null && m.isAdjacentTo(rfl2))
                        || (rfl3 != null && m.isAdjacentTo(rfl3))) {
                    continue;
                }
                if (rc.canSpawn(m)) {
                    rc.spawn(m);
                    return;
                }
            }
        }

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


        if (round >= GameConstants.SETUP_ROUNDS) {
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
                    enemyLoc = center;
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
        nearbyMapInfos = rc.senseNearbyMapInfos(-1);
        Communications.updateEnemies(rc, nearbyRobots, nearbyFlags);
        checkDetonated(rc);

        numAllies = 0;
        numEnemies = 0;
        MapLocation curr = rc.getLocation();
        isMaxedBuild = rc.getExperience(SkillType.BUILD) >= SkillType.BUILD.getExperience(6);

        nearestEnemy = null;
        int neDist = 1000000;

        int leastEnemyHealth = GameConstants.DEFAULT_HEALTH;
        
        nearestAlly = null;
        int naDist = 1000000;

        nearestHealingAlly = null;
        int haDist = 1000000;

        nearestBuilder = null;
        int nbDist = 1000000;

        boolean flagTaken = false;
        
        nearestFlagHolder = null;
        nfDist = 1000000;
        
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
                if (info.getAttackLevel() >= 4 || info.getBuildLevel() >= 4 || info.getHealLevel() <= 2/* || info.getHealLevel() >= 4*/) {
                    if (aDist < haDist) {
                        haDist = aDist;
                        nearestHealingAlly = info.location;
                    }
                }
                if (info.getBuildLevel() == 6) {
                    if (aDist < nbDist) {
                        nbDist = aDist;
                        nearestBuilder = info.location;
                    }
                }
                if (info.hasFlag) {
                    if (aDist < nfDist) {
                        nfDist = aDist;
                        nearestFlagHolder = info.location;
                    }
                }
            } else {
                numEnemies++;
                eDist = info.location.distanceSquaredTo(curr);
                if (eDist < neDist) {
                    neDist = eDist;
                    nearestEnemy = info.location;
                }
                if (info.hasFlag) flagTaken = true;
                if (info.health < leastEnemyHealth) leastEnemyHealth = info.health;
            }
        }
        numCombatAllies = 0;
        if (nearestEnemy != null && numAllies > 0) {
            for (RobotInfo info : nearbyRobots) {
                if (info.team == team && info.location.isWithinDistanceSquared(nearestEnemy, GameConstants.VISION_RADIUS_SQUARED)) {
                    numCombatAllies += 1;
                }
            }
        }
        isThreatened = nearestEnemy != null && nearestEnemy.add(nearestEnemy.directionTo(curr)).isWithinDistanceSquared(curr, GameConstants.ATTACK_RADIUS_SQUARED);
        isAttacked = nearestEnemy != null && nearestEnemy.isWithinDistanceSquared(curr, GameConstants.ATTACK_RADIUS_SQUARED);

        if (defendSpot != null) {
            checkNeedsDefense(rc);
        }


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
        if (rc.hasFlag() || (rc.isActionReady() && tryTakeFlag(rc))) {
            if (rc.isMovementReady()) {
                tryReturnBase(rc);
                retbase = true;
            }
        }
        boolean fb = false;
        if (numEnemies > 0 && round >= GameConstants.SETUP_ROUNDS - READY_ROUNDS) {
            if (isBuilder && !isAttacked && rc.isActionReady() && rc.getCrumbs() >= TrapType.EXPLOSIVE.buildCost) {
                //tryBuildAggro(rc, nearestEnemy);

                //tryBuildStuns(rc);
            }

            if (nearbyCrumbs.length > 0 && rc.isActionReady()) tryStunForCrumbs(rc);

            if (rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                tryAttack(rc);
            }

            if (isBuilder && !isAttacked && rc.getCrumbs() >= TrapType.STUN.buildCost + 50) {
                findAndBuild(rc);
                fb = true;
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
                BugNavigation.move(rc, nearestEnemy, true, isMaxedBuild);
            }
            boolean aggro = nearbyCrumbs.length > 0;
            if (rc.isMovementReady()) {
                if (defendSpot == null && nearestFlagHolder == null && !isThreatened && !flagTaken && numCombatAllies == 0 && (numEnemies > 1 || rc.getHealth() <= leastEnemyHealth)) {
                    seekEnemy(rc);
                }
                if (rc.isMovementReady()) {
                    if (defendSpot == null && nearestFlagHolder != null) {
                        micro.doMicro(nearbyRobots, nearestFlagHolder, stunned0 + stunned1 + stunned2 + stunned3, true);
                    } else {
                        micro.doMicro(nearbyRobots, defendSpot, stunned0 + stunned1 + stunned2 + stunned3, false);
                    }
                }
                /*if (defendSpot != null || numAllies > 0 || isThreatened || flagTaken) {
                    micro.doMicro(nearbyRobots, defendSpot, stunned0 + stunned1 + stunned2 + stunned3);
                } else {
                    if (!seekEnemy(rc)) {
                        micro.doMicro(nearbyRobots, defendSpot, stunned0 + stunned1 + stunned2 + stunned3);
                    }
                }*/
            }
            if (rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                nearbyRobots = rc.senseNearbyRobots();
                Communications.updateEnemies(rc, nearbyRobots, nearbyFlags);
                tryAttack(rc);
            }

            if (nearbyCrumbs.length > 0 && rc.isActionReady()) {
                tryStunForCrumbs(rc);
            }

            /*if (isBuilder && round > BREAK_TURTLE_ROUND) {

            }*/

            if (rc.isActionReady() && round >= GameConstants.SETUP_ROUNDS) {
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
                    BugNavigation.move(rc, defendSpot, false, false);
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

                if (isBuilder && isMaxedBuild) {
                    if (round < GameConstants.SETUP_ROUNDS - READY_ROUNDS) {
                        moveBuildDefense(rc);
                    }
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
                        BugNavigation.move(rc, nearestHealingAlly, true, isMaxedBuild);
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

        if (isBuilder && !fb) {
            tryDigAndBuild(rc);
        }

        if (!isThreatened && rc.isActionReady()) {
            tryHeal(rc);
        }

        if (round > DIG_ROUND && !isThreatened && rc.isActionReady()) {
            tryDig(rc);
        }

        updateTraps();

    }

    //static MapLocation secondTarget = null;
    
    public static MapLocation getEnemyTarget(RobotController rc, MapLocation loc) throws GameActionException {
        MapLocation best = null;
        //secondTarget = null;
        MapLocation curr = rc.getLocation();
        int myDamage = rc.getAttackDamage();
        //System.out.println("MY DAMAGE IS: " + myDamage);
        double bestScore = 1000000;
        double score;
        //int dps;
        RobotInfo[] attackingAllies;
        int allyDmg = (round > 600 ? 210 : 150);
        for (RobotInfo info : nearbyRobots) {
            if (info.team != team && info.location.isWithinDistanceSquared(loc, GameConstants.ATTACK_RADIUS_SQUARED)) {
                //dps = 150 + SkillType.ATTACK.getSkillEffect(info.attackLevel);
                //score = info.attackLevel / 7.0;
                score = StrictMath.max(info.health - myDamage, 0) + 1.0/(1+info.healLevel);
                /*if (info.health > myDamage) {
                    score += info.health - myDamage;
                }*/
                
                attackingAllies = rc.senseNearbyRobots(info.location, 4, team);

                score = StrictMath.max(info.health - myDamage, 0) + 1.0 / (1+attackingAllies.length);
                if (score - attackingAllies.length * allyDmg <= 0) score -= allyDmg;

                if (info.hasFlag) score -= 1000;

                if (score < bestScore) {
                    bestScore = score;
                    /*if (info.health > myDamage) {
                        secondTarget = info.location;
                    } else {
                        secondTarget = best;
                    }*/
                    best = info.location;
                }
            }
        }
        return best;
    }

    public static boolean tryAttack(RobotController rc) throws GameActionException {
        MapLocation best = getEnemyTarget(rc, rc.getLocation());
        if (best != null && rc.canAttack(best)) {
            rc.attack(best);
            /*if (secondTarget != null && rc.isActionReady()) {
                rc.attack(secondTarget);
                System.out.println("ATTACK 2");
            }*/
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

    public static boolean shouldHeal(RobotController rc) throws GameActionException {
        if (rc.hasFlag()) return false;
        if (rc.getExperience(SkillType.HEAL) == SkillType.HEAL.getExperience(4)-1
                && rc.getExperience(SkillType.ATTACK) < SkillType.ATTACK.getExperience(4)
                && rc.getExperience(SkillType.BUILD) < SkillType.BUILD.getExperience(4)) {
            if (Communications.countHealers() >= NUM_HEALERS) return false;
        }
        return true;
    }

    public static boolean tryHeal(RobotController rc) throws GameActionException {
        if (!shouldHeal(rc)) return false;
        MapLocation best = getAllyTarget(rc, rc.getLocation());
        if (best != null) {
            rc.heal(best);
            return true;
        }
        if (!isHealer && rc.getExperience(SkillType.HEAL) >= SkillType.HEAL.getExperience(4)) {
            isHealer = true;
            Communications.claimHealer(rc);
        }
        return false;
    }

    public static boolean tryMoveHeal(RobotController rc) throws GameActionException {
        if (!shouldHeal(rc)) return false;
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
        if (nearestEnemy == null) {
            MapLocation loc = Communications.readEnemies(rc, rc.getLocation(), true, explore);
            return loc != null && BugNavigation.move(rc, loc, true, isMaxedBuild);
        } else {
            MapLocation loc = Communications.readEnemies(rc, rc.getLocation(), true, explore);
            return loc != null && loc.distanceSquaredTo(rc.getLocation()) > AVOID_DIST && BugNavigation.move(rc, loc, true, isMaxedBuild, nearbyRobots, 10);
        }
    }

    /*public static boolean seekEnemy(RobotController rc) throws GameActionException {

        MapLocation curr = rc.getLocation();
        MapLocation loc = Communications.readEnemies(rc, rc.getLocation(), true, explore);
        if (loc == null) return false;


        if (nearestEnemy == null) {
            int myHealth = rc.getHealth();
            MapLocation closest = null;
            int closestDist = curr.distanceSquaredTo(loc);
            int dist;
            for (RobotInfo robot : nearbyRobots) {
                if (robot.team == team && robot.health == GameConstants.DEFAULT_HEALTH) {
                    dist = robot.location.distanceSquaredTo(loc);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = robot.location;
                    }
                }
            }
            if (closest != null && closest.distanceSquaredTo(curr) > 2) loc = closest;
            return BugNavigation.move(rc, loc, true, isMaxedBuild);
        } else {
            return loc.distanceSquaredTo(rc.getLocation()) > AVOID_DIST && BugNavigation.move(rc, loc, true, isMaxedBuild, nearbyRobots, 10);
        }
    }*/

    public static boolean explore(RobotController rc) throws GameActionException {
        MapLocation target = explore.getExploreTarget(rc, rng);
        if (BugNavigation.move(rc, target)) {
            return true;
        } else {
            explore.resetExploreTarget(rc, rng);
            return BugNavigation.move(rc, target, true, isMaxedBuild);
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

    public static boolean tryBuild(RobotController rc, int separation, TrapType type, boolean close) throws GameActionException {
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
                for (MapInfo info : nearbyMapInfos) {
                    if (info.getTrapType() == type) {
                        for (int i = pos.length; i-->0;) {
                            if (pos[i].isWithinDistanceSquared(info.getMapLocation(), separation)) elim[i] = true;
                        }
                    }
                }
            }
            
            int closestDist;
            if (close) closestDist = 1000000;
            else closestDist = 0;
            int closestI = -1;
            int dist;
            for (int i = 0; i < pos.length; i++) {
                if (!elim[i] && rc.canBuild(type, pos[i])) {
                    dist = pos[i].distanceSquaredTo(nearestEnemy);
                    if (close) {
                        if (dist < closestDist) {
                            closestDist = dist;
                            closestI = i;
                        }
                    } else {
                        if (dist > closestDist) {
                            closestDist = dist;
                            closestI = i;
                        }
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
            } else if (rc.isMovementReady() && numAllies >= numEnemies && rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                BugNavigation.move(rc, flagLoc, true, isMaxedBuild);
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

        if (false && rc.hasFlag() && isThreatened && nearestAlly != null && nearestAlly.distanceSquaredTo(loc) > 2 && nearestSpawn.distanceSquaredTo(loc) > 8) {
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

            /*int bestIndex = -1;
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
            }*/

            int bestIndex = -1;
            double bestScore = -1000000;
            boolean isCenter;
            for (int i = 9; i-->0;) {
                isCenter = allDirections[i] == Direction.CENTER;
                if ((isCenter || rc.canMove(allDirections[i]))
                        && (scores[i] > bestScore ||
                            (scores[i] == bestScore && isCenter))) {
                    bestScore = scores[i];
                    bestIndex = i;
                }
            }

            if (bestIndex >= 0) {
                if (allDirections[bestIndex] != Direction.CENTER) {
                    rc.move(allDirections[bestIndex]);
                }
            }
            return true;
        }
        int ar = (isThreatened ? 4 : 10);
        return nearestSpawn != null && BugNavigation.move(rc, nearestSpawn, true, true, nearbyRobots, ar);
        //return nearestSpawn != null && BugNavigation.move(rc, nearestSpawn, true, true);
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
                return BugNavigation.move(rc, flag.getLocation(), true, isMaxedBuild);
            } else if (flag.isPickedUp()
                    && rc.getLocation().distanceSquaredTo(flag.getLocation()) > 8) {
                return BugNavigation.move(rc, flag.getLocation(), true, isMaxedBuild);
            }
        }
        
        /*MapLocation curr = rc.getLocation();
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
        }*/
        if (nearestFlagHolder != null) {
            if (nfDist <= 2) return tryMoveAway(rc, nearestFlagHolder) || tryReturnBase(rc);
            else if (nfDist > 8) return BugNavigation.move(rc, nearestFlagHolder, true, isMaxedBuild);
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

        tryBuild(rc, 0, TrapType.STUN, true);
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
        
        for (MapInfo mi : nearbyMapInfos) {
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
            BugNavigation.move(rc, best, true, isMaxedBuild);
        }

        if (bestBuild != null) {
            rc.build(TrapType.STUN, bestBuild);
        }

    }
    
    /**
     * Call this at the end of the round to update traps.
     */
    public static void updateTraps() {
        previousTraps.clear();
        for (MapInfo mi : nearbyMapInfos) {
            if (mi.getTrapType() == TrapType.STUN) {
                previousTraps.add(mi.getMapLocation());
            }
        }
        stunned3 = stunned2;
        stunned2 = stunned1;
        stunned1 = stunned0;
        stunned0 = "";
    }

    public static void checkDetonated(RobotController rc) {
        MapLocation curr = rc.getLocation();
        //detonatedTrap = null;
        MapLocation det;
        for (MapInfo mi : nearbyMapInfos) {
            if (mi.getTrapType() == TrapType.NONE && previousTraps.contains(mi.getMapLocation())) {
                rc.setIndicatorDot(mi.getMapLocation(), 0, 0, 255);
                det = mi.getMapLocation();
                for (RobotInfo info : nearbyRobots) {
                    if (info.team != team && det.isWithinDistanceSquared(info.location, 5)) {
                        stunned0 += info.ID + ",";
                    }
                }
            }
        }
        /*if (detonatedRound - round + 1 >= TrapType.STUN.opponentCooldown / GameConstants.COOLDOWNS_PER_TURN) {
            detonatedTrap = null;
        }*/
        //System.out.println("intRadSq: " + TrapType.STUN.interactRadius);
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
            if (!rc.onTheMap(m)) continue;
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

    public static void tryBuildWater(RobotController rc) throws GameActionException {
        boolean isWater = false;
        MapLocation build = null;
        MapLocation curr = rc.getLocation();
        MapLocation m;
        int dist;
        int buildDist = 0;
        for (MapInfo mi : nearbyMapInfos) {
            if (mi.getTrapType() == TrapType.WATER) {
                isWater = true;
                break;
            }
            m = mi.getMapLocation();
            if (m.isAdjacentTo(curr) && mi.getTrapType() == TrapType.NONE) {
                dist = m.distanceSquaredTo(nearestEnemy);
                if (dist > buildDist) {
                    buildDist = dist;
                    build = m;
                }
            }
        }

        if (!isWater) {
            if (build != null) {
                Direction to = curr.directionTo(build);
                if (rc.canMove(to)) rc.move(to);

                MapLocation best = null;
                int bestDist = 0;
                for (Direction d : allDirections) {
                    m = curr.add(d);
                    dist = m.distanceSquaredTo(nearestEnemy);
                    if (dist > bestDist && rc.canBuild(TrapType.WATER, m)) {
                        bestDist = dist;
                        best = m;
                    }
                }

                if (best != null) {
                    rc.build(TrapType.WATER, best);
                }
            }
        }
    }

    public static boolean tryBuildStunDiagonals(RobotController rc) throws GameActionException {
        MapLocation best = null;
        int bestDist = 1000000;
        int dist;
        MapLocation m;
        MapLocation curr = rc.getLocation();
        int buildrad = 8;
        if (round < 200 || rc.getCrumbs() > 2000) buildrad = 16;

        if (!rc.canSenseLocation(nearestEnemy) || !hasLineOfSight(rc, curr, nearestEnemy)) return false;
        for (Direction d : allDirections) {
            m = curr.add(d);
            if ((m.x + m.y) % 2 == 0 && m.isWithinDistanceSquared(nearestEnemy, buildrad)) {
                if (rc.canBuild(TrapType.STUN, m)) {
                    dist = m.distanceSquaredTo(nearestEnemy);
                    if (dist < bestDist) {
                        bestDist = dist;
                        best = m;
                    }
                }
            }
        }
        if (best != null) {
            rc.build(TrapType.STUN, best);
            return true;
        }
        return false;
    }

    public static boolean findAndBuild(RobotController rc) throws GameActionException {
        if (numCombatAllies < 2 || numEnemies < 2) return false;
        if (rc.isActionReady()) tryBuildStunDiagonals(rc);
        if (!rc.isMovementReady()) return false;

        int buildrad = (round < 200 ? 16 : 8);

        MapLocation curr = rc.getLocation();
        MapLocation best = null;
        int bestDist = 1000000;
        int dist;
        boolean hurt = rc.getHealth() <= 750;
        MapLocation m;
        for (MapInfo mi : nearbyMapInfos) {
            if (mi.isPassable() && mi.getTrapType() == TrapType.NONE) {
                m = mi.getMapLocation();
                if ((m.x + m.y) % 2 == 1) continue;
                dist = m.distanceSquaredTo(nearestEnemy);
                if (dist < bestDist && dist <= buildrad && dist > 2) {
                    bestDist = dist;
                    best = m;
                }
            }
        }
        if (best != null) {
            int radius = (hurt ? 10 : 4);
            if (BugNavigation.move(rc, best, true, true, nearbyRobots, radius) && rc.isActionReady()) {
                tryBuildStunDiagonals(rc);
            }
        }
        return false;
    }

    public static void tryDigAndBuild(RobotController rc) throws GameActionException {
        MapLocation curr = rc.getLocation();
        boolean isMaxed = rc.getExperience(SkillType.BUILD) >= SkillType.BUILD.getExperience(6);
        if (isMaxed) {
            boolean shouldBuild = (numEnemies >= 2 && numCombatAllies >= 2);
            if (shouldBuild && rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT
                    && round >= GameConstants.SETUP_ROUNDS - READY_ROUNDS
                    && rc.getCrumbs() > TrapType.EXPLOSIVE.buildCost + 50) {
                findAndBuild(rc);
                //tryBuild(rc, 5, TrapType.STUN, true);
                    //if (!tryBuild(rc, 13, TrapType.EXPLOSIVE)) {
                        /*if (rc.getCrumbs() > 2 * TrapType.EXPLOSIVE.buildCost + 50) {
                            tryBuild(rc, 0, TrapType.EXPLOSIVE);
                        }*/
                    //}
                //tryBuildStuns(rc);
                /*if (numEnemies >= 3 && round < GameConstants.SETUP_ROUNDS) {
                    //tryBuild(rc, 16, TrapType.WATER, false);
                    tryBuildWater(rc);
                }*/
            }
        }
        if (!isMaxed) {
            if (!isThreatened) {
                MapLocation m;
                for (Direction d : directions) {
                    m = curr.add(d);
                    if ((m.x + m.y) % 2 == 1 && rc.canDig(m)) {
                        rc.dig(m);
                    }
                }
            }
            MapLocation best = null;
            int bestDist = 1000000;
            int dist;
            MapLocation m;
            for (MapInfo mi : nearbyMapInfos) {
                m = mi.getMapLocation();
                dist = m.distanceSquaredTo(curr);
                if ((m.x + m.y) % 2 == 1 && mi.isPassable() && !mi.isSpawnZone()) {
                    if (dist < bestDist || (dist == bestDist && m.distanceSquaredTo(center) > best.distanceSquaredTo(center))) {
                        bestDist = dist;
                        best = m;
                    }
                }
            }

            if (best != null && best.distanceSquaredTo(curr) > 2) {
                BugNavigation.move(rc, best, true, true);
            }
        }
        
    }

    public static void tryBuildBuilderDefense(RobotController rc, FlagInfo[] nearbyFlags) throws GameActionException {
        if (!isMaxedBuild
                || rc.getCrumbs() < TrapType.STUN.buildCost) return;
        MapLocation curr = rc.getLocation();
        
        int closestDist = 1000000;
        MapLocation closest = null;

        MapLocation m;
        MapInfo mi;
        int dist;
        for (FlagInfo nearbyFlag : nearbyFlags) {
            if (!nearbyFlag.getLocation().isWithinDistanceSquared(curr, 18)) continue;
            
            /*if (round < GameConstants.SETUP_ROUNDS) {
                boolean isWater = false;

                for (MapInfo nmi : nearbyMapInfos) {
                    if (nmi.getTrapType() == TrapType.WATER && nmi.getMapLocation().isAdjacentTo(nearbyFlag.getLocation())) isWater = true;
                }

                if (!isWater) {
                    for (Direction d : diagonals) {
                        m = nearbyFlag.getLocation().add(d);
                        if (!rc.canSenseLocation(m)) continue;
                        if (rc.canBuild(TrapType.WATER, m)) {
                            rc.build(TrapType.WATER, m);
                            break;
                        }
                    }
                }
            }*/

            if (rc.canBuild(TrapType.WATER, nearbyFlag.getLocation())) {
                rc.build(TrapType.WATER, nearbyFlag.getLocation());
            } else {
                mi = rc.senseMapInfo(nearbyFlag.getLocation());
                if (mi.getTrapType() == TrapType.NONE) {
                    dist = nearbyFlag.getLocation().distanceSquaredTo(curr);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = nearbyFlag.getLocation();
                    }
                }
            }
            
            if (round > GameConstants.SETUP_ROUNDS || rc.getCrumbs() > 4000) {
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

    public static void checkNeedsDefense(RobotController rc) throws GameActionException {
        if (nearestBuilder != null && nearestBuilder.distanceSquaredTo(defendSpot) <= 10) return;
        boolean needsDefense = true;
        MapLocation m;
        MapInfo mi;
        for (Direction d : diagonals) {
            m = defendSpot.add(d);
            if (rc.canSenseLocation(m)) {
                mi = rc.senseMapInfo(m);
                if (mi.getTrapType() == TrapType.WATER) {
                    needsDefense = false;;
                    break;
                }
            }
        }
        /*if (rc.canSenseLocation(defendSpot)) {
            mi = rc.senseMapInfo(defendSpot);
            if (mi.getTrapType() == TrapType.NONE) {
                needsDefense = true;
            }
        }*/
        Communications.markBuildDefense(rc, defendSpot, needsDefense);
    }

    public static void moveBuildDefense(RobotController rc) throws GameActionException {
        MapLocation m = Communications.getBuilderNeededSpawn(rc, rc.getLocation());
        if (m != null) {
            BugNavigation.move(rc, m, true, true);
        }
    }
}
