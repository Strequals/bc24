package duck0125v2;

import battlecode.common.*;

public strictfp class Communications {
    
    public static final int ENEMIES_START = 0;
    public static final int ENEMIES_NUM = 8;
    public static final int INF = 1000000;
    public static final int UPDATE_DIST_SQ = 9;
    
    public static final int HEALERCOUNT_INDEX = 53;
    public static final int BUILDERCOUNT_INDEX = 54;
    public static final int RESPAWNROUND_INDEX = 55;
    public static final int RESPAWN_INDEX = 58;
    public static final int FLAGSPAWN_INDEX = 59;
    public static final int EXP_INDEX = 62;
    

    public static final int SPAWN_SCORE = 10;
    public static final int HOLDING_FLAG_SCORE = 60;
    public static final int STOLEN_FLAG_SCORE = 400;
    public static final int THREATENED_FLAG_BASE_SCORE = 60;


    public static final int CLAIM_DEFENDER_RANGE = 8;
    public static final double DECAY_RATE = 0.7;

    public static int[] array = new int[64];
    static Team team;
    static int round;
    static MapLocation[] spawns = new MapLocation[3];

    public static void readArray(RobotController rc) throws GameActionException {
        team = rc.getTeam();
        round = rc.getRoundNum();
        array[0] = rc.readSharedArray(0);
        array[1] = rc.readSharedArray(1);
        array[2] = rc.readSharedArray(2);
        array[3] = rc.readSharedArray(3);
        array[4] = rc.readSharedArray(4);
        array[5] = rc.readSharedArray(5);
        array[6] = rc.readSharedArray(6);
        array[7] = rc.readSharedArray(7);
        array[8] = rc.readSharedArray(8);
        array[9] = rc.readSharedArray(9);
        array[10] = rc.readSharedArray(10);
        array[11] = rc.readSharedArray(11);
        array[12] = rc.readSharedArray(12);
        array[13] = rc.readSharedArray(13);
        array[14] = rc.readSharedArray(14);
        array[15] = rc.readSharedArray(15);
        array[16] = rc.readSharedArray(16);
        array[17] = rc.readSharedArray(17);
        array[18] = rc.readSharedArray(18);
        array[19] = rc.readSharedArray(19);
        array[20] = rc.readSharedArray(20);
        array[21] = rc.readSharedArray(21);
        array[22] = rc.readSharedArray(22);
        array[23] = rc.readSharedArray(23);
        array[24] = rc.readSharedArray(24);
        array[25] = rc.readSharedArray(25);
        array[26] = rc.readSharedArray(26);
        array[27] = rc.readSharedArray(27);
        array[28] = rc.readSharedArray(28);
        array[29] = rc.readSharedArray(29);
        array[30] = rc.readSharedArray(30);
        array[31] = rc.readSharedArray(31);
        array[32] = rc.readSharedArray(32);
        array[33] = rc.readSharedArray(33);
        array[34] = rc.readSharedArray(34);
        array[35] = rc.readSharedArray(35);
        array[36] = rc.readSharedArray(36);
        array[37] = rc.readSharedArray(37);
        array[38] = rc.readSharedArray(38);
        array[39] = rc.readSharedArray(39);
        array[40] = rc.readSharedArray(40);
        array[41] = rc.readSharedArray(41);
        array[42] = rc.readSharedArray(42);
        array[43] = rc.readSharedArray(43);
        array[44] = rc.readSharedArray(44);
        array[45] = rc.readSharedArray(45);
        array[46] = rc.readSharedArray(46);
        array[47] = rc.readSharedArray(47);
        array[48] = rc.readSharedArray(48);
        array[49] = rc.readSharedArray(49);
        array[50] = rc.readSharedArray(50);
        array[51] = rc.readSharedArray(51);
        array[52] = rc.readSharedArray(52);
        array[53] = rc.readSharedArray(53);
        array[54] = rc.readSharedArray(54);
        array[55] = rc.readSharedArray(55);
        array[56] = rc.readSharedArray(56);
        array[57] = rc.readSharedArray(57);
        array[58] = rc.readSharedArray(58);
        array[59] = rc.readSharedArray(59);
        array[60] = rc.readSharedArray(60);
        array[61] = rc.readSharedArray(61);
        array[62] = rc.readSharedArray(62);
        array[63] = rc.readSharedArray(63);
    }

    /**
     * Format of enemy data:
     * round reported (11) | score[8:4] (5)
     * score[3:0] (4) | x (6) | y(6)
     */
    public static void updateEnemies(RobotController rc, RobotInfo[] robots, FlagInfo[] myNearbyFlags) throws GameActionException {
        double totalScore = 0;
        MapLocation target = null;
        double targetScore = 0;
        
        /*for (FlagInfo flag : myNearbyFlags) {
            if (!flag.isPickedUp()) {
                totalScore += THREATENED_FLAG_SCORE;
                break;
            }
        }*/
        
        double score;
        int allies = 0;
        int enemies = 0;
        for (RobotInfo robot : robots) {
            if (robot.team != team) {
                score = 2;
                enemies++;
                if (robot.hasFlag) score += STOLEN_FLAG_SCORE;
                for (FlagInfo flag : myNearbyFlags) {
                    if (!flag.isPickedUp()) {
                        totalScore += THREATENED_FLAG_BASE_SCORE / (1 + robot.location.distanceSquaredTo(flag.getLocation()));
                    }
                }
            } else {
                score = -1;
                allies++;
                if (robot.hasFlag) score += HOLDING_FLAG_SCORE;
            }
            totalScore += score;
            if (score > targetScore) {
                targetScore = score;
                target = robot.location;
            }
        }

        if (rc.hasFlag()) {
            totalScore += HOLDING_FLAG_SCORE;
            if (allies <= 8 || enemies > 0) target = rc.getLocation();
        }

        int totalScore_i = (int) StrictMath.round(totalScore);

        double lowestScore = INF;
        int lowestIndex = -1;
        
        int reportRound;
        MapLocation l;
        double reportScore;
        for (int i = ENEMIES_START + ENEMIES_NUM * 2 - 2; i >= ENEMIES_START; i -= 2) {
            reportRound = array[i] >> 5;
            l = new MapLocation((array[i+1] >> 6) % 64, array[i+1] % 64);
            score = ((array[i] % 32) * 16 + (array[i+1] >> 12))
                * StrictMath.pow(DECAY_RATE, (round - reportRound));
            rc.setIndicatorDot(l, 255, 0, 0);
            if (target != null) {
                if (l.distanceSquaredTo(target) <= UPDATE_DIST_SQ) {
                    if (totalScore > score) {
                        array[i] = (totalScore_i >> 4) + (round << 5);
                        array[i+1] = target.y + ((target.x + ((totalScore_i % 16) << 6)) << 6);
                        rc.writeSharedArray(i, array[i]);
                        rc.writeSharedArray(i+1, array[i+1]);
                    }
                    return;
                }
                if (score < lowestScore) {
                    lowestScore = score;
                    lowestIndex = i;
                }
            } else {
                if (rc.getLocation().distanceSquaredTo(l) <= UPDATE_DIST_SQ) {
                    array[i] = 0;
                    array[i+1] = 0;
                    rc.writeSharedArray(i, 0);
                    rc.writeSharedArray(i+1, 0);
                }
                return;
            }
        }

        if (target == null) return;

        if (lowestScore < totalScore) {
            array[lowestIndex] = (totalScore_i >> 4) + (round << 5);
            array[lowestIndex+1] = target.y + ((target.x + ((totalScore_i % 16) << 6)) << 6);
            rc.writeSharedArray(lowestIndex, array[lowestIndex]);
            rc.writeSharedArray(lowestIndex+1, array[lowestIndex+1]);
        }
    }

    public static MapLocation readEnemies(RobotController rc, boolean useSpawns, Explore explore) throws GameActionException {
        int bestIndex = -1;
        double bestScore = 0;
        MapLocation bestLoc = null;

        int reportRound;
        int rawScore;
        double score;
        MapLocation l;
        for (int i = ENEMIES_START + ENEMIES_NUM * 2 - 2; i >= ENEMIES_START; i -= 2) {
            reportRound = array[i] >> 5;
            l = new MapLocation((array[i+1] >> 6) % 64, array[i+1] % 64);
            rawScore = (array[i] % 32) * 16 + (array[i+1] >> 12);
            //if (rawScore < HOLDING_FLAG_SCORE) continue;
            score = rawScore * StrictMath.pow(DECAY_RATE, (round - reportRound));
            if (score > bestScore) {
                bestScore = score;
                bestIndex = i;
                bestLoc = l;
            }
        }

        if (useSpawns && bestScore < SPAWN_SCORE) {
            MapLocation[] bfls = rc.senseBroadcastFlagLocations();
            /*MapLocation nearest = null;
            int nearestDist = 1000000;
            
            int dist;
            for (MapLocation bfl : bfls) {
                dist = curr.distanceSquaredTo(bfl);
                if (dist < nearestDist) {
                    nearest = bfl;
                    nearestDist = dist;
                }
            }*/

            if (bfls.length > 0) {
                MapLocation leastRecentlySeen = null;
                int leastRound = 1000000;
                int roundSeen;
                for (MapLocation bfl : bfls) {
                    roundSeen = explore.getRoundVisited(bfl);
                    if (roundSeen < leastRound) {
                        leastRound = roundSeen;
                        leastRecentlySeen = bfl;
                    }
                }
                return explore.getLeastRecentlyVisitedWithinRadius(rc, leastRecentlySeen, 10);
            }
        }

        return bestLoc;
    }

    public static MapLocation readEnemies(RobotController rc, MapLocation curr, boolean useSpawns, Explore explore) throws GameActionException {
        int bestIndex = -1;
        double bestScore = 0;
        MapLocation bestLoc = null;

        int reportRound;
        double score;
        MapLocation l;
        for (int i = ENEMIES_START + ENEMIES_NUM * 2 - 2; i >= ENEMIES_START; i -= 2) {
            reportRound = array[i] >> 5;
            l = new MapLocation((array[i+1] >> 6) % 64, array[i+1] % 64);
            rc.setIndicatorDot(l, 255, 0, 0);
            score = ((array[i] % 32) * 16 + (array[i+1] >> 12)) * StrictMath.pow(DECAY_RATE, (round - reportRound));
            score /= (1 + StrictMath.sqrt(curr.distanceSquaredTo(l)));
            if (score > bestScore) {
                bestScore = score;
                bestIndex = i;
                bestLoc = l;
            }
        }

        if ((useSpawns || bestLoc == null) && bestScore < SPAWN_SCORE) {
            MapLocation[] bfls = rc.senseBroadcastFlagLocations();
            MapLocation nearest = null;
            int nearestDist = 1000000;
            
            int dist;
            for (MapLocation bfl : bfls) {
                dist = curr.distanceSquaredTo(bfl);
                if (dist < nearestDist) {
                    nearest = bfl;
                    nearestDist = dist;
                }
            }

            if (nearest != null) {
                return explore.getLeastRecentlyVisitedWithinRadius(rc, nearest, 10);
            }
        }
        return bestLoc;
    }

    /**
     * Format of exp data:
     * Attack exp (16)
     * Build exp (7) | Heal exp (9)
     */
    public static void updateExp(RobotController rc) throws GameActionException {
        int expA = array[EXP_INDEX];
        int expB = array[EXP_INDEX+1] >> 9;
        int expH = array[EXP_INDEX+1] % 512;

        expA = (int) (0.98 * expA + 0.02 * 4 * rc.getExperience(SkillType.ATTACK));
        expB = (int) (0.98 * expB + 0.02 * 4 * rc.getExperience(SkillType.BUILD));
        expH = (int) (0.98 * expH + 0.02 * 4 * rc.getExperience(SkillType.HEAL));

        array[EXP_INDEX] = expA;
        array[EXP_INDEX+1] = expH + (expB << 9);
        rc.writeSharedArray(EXP_INDEX, array[EXP_INDEX]);
        rc.writeSharedArray(EXP_INDEX, array[EXP_INDEX+1]);
    }

    public static int readAverageAttackExp() {
        return array[EXP_INDEX] / 4;
    }


    public static int readAverageBuildExp() {
        return (array[EXP_INDEX+1] >> 9) / 4;
    }

    public static int readAverageHealExp() {
        return (array[EXP_INDEX+1] % 512) / 4;
    }
    
    /**
     * Format of flag spawn data:
     * defended (1) | x1 (6) | y1 (6)
     * defended (1) | x2 (6) | y2 (6)
     * defended (1) | x3 (6) | y3 (6)
     */
    public static void updateFlagSpawn(RobotController rc, FlagInfo[] flags) throws GameActionException {
        for (int i = 3; i-->0;) {
            if (array[FLAGSPAWN_INDEX+i]>0) {
                spawns[i] = new MapLocation(((array[FLAGSPAWN_INDEX+i]-1) >> 6) % 64, (array[FLAGSPAWN_INDEX+i]-1) % 64);
            } else {
                spawns[i] = null;
            }
        }
        for (FlagInfo flag : flags) {
            if (flag.getTeam() == team && !flag.isPickedUp()) {
                int loc = -1000000; // default, array full but also not found???
                for (int i = 3; i-->0;) {
                    if (spawns[i] == null) {
                        loc = i;
                    } else if (spawns[i].equals(flag.getLocation())) {
                        loc = -1000000;
                        break;
                    }
                }
                if (loc >= 0) {
                    spawns[loc] = flag.getLocation();
                    array[FLAGSPAWN_INDEX+loc] = (spawns[loc].x << 6) + spawns[loc].y + 1;
                    rc.writeSharedArray(FLAGSPAWN_INDEX+loc, array[FLAGSPAWN_INDEX+loc]);
                }
            }
        }
    }

    static final int defender_mask = 0b1000000000000;

    public static MapLocation tryClaimDefender(RobotController rc, boolean isSpawned) throws GameActionException {
        for (int i = 3; i-->0;) {
            if (array[FLAGSPAWN_INDEX+i]>0) {
                if (((array[FLAGSPAWN_INDEX+i]-1) & defender_mask) == 0) {
                    MapLocation m = new MapLocation(((array[FLAGSPAWN_INDEX+i]-1) >> 6) % 64, (array[FLAGSPAWN_INDEX+i]-1) % 64);
                    if (!isSpawned || m.distanceSquaredTo(rc.getLocation()) <= CLAIM_DEFENDER_RANGE) {
                        array[FLAGSPAWN_INDEX+i] = ((array[FLAGSPAWN_INDEX+i]-1) | defender_mask) + 1;
                        rc.writeSharedArray(FLAGSPAWN_INDEX+i, array[FLAGSPAWN_INDEX+i]);
                        return m;
                    }
                }
            }
        }
        return null;
    }

    public static void unclaimDefender(RobotController rc, MapLocation defendLoc) throws GameActionException {
        for (int i = 3; i-->0;) {
            if (array[FLAGSPAWN_INDEX+i]>0) {
                int val = array[FLAGSPAWN_INDEX+i]-1;
                if ((val >> 6) % 64 == defendLoc.x
                        && val % 64 == defendLoc.y) {
                    array[FLAGSPAWN_INDEX+i] = (val ^ defender_mask) + 1;
                    rc.writeSharedArray(FLAGSPAWN_INDEX+i, array[FLAGSPAWN_INDEX+i]);
                    return;
                }
            }
        }
    }

    static final int builder_mask = 0b10000000000000;
    static final int inv_builder_mask = 0b1101111111111111;

    public static void markBuildDefense(RobotController rc, MapLocation defendLoc, boolean shouldBuild) throws GameActionException {
        for (int i = 3; i-->0;) {
            if (array[FLAGSPAWN_INDEX+i]>0) {
                int val = array[FLAGSPAWN_INDEX+i]-1;
                if ((val >> 6) % 64 == defendLoc.x
                        && val % 64 == defendLoc.y) {
                    if (shouldBuild) {
                        array[FLAGSPAWN_INDEX+i] = (val | builder_mask) + 1;
                    } else {
                        array[FLAGSPAWN_INDEX+i] = (val & inv_builder_mask) + 1;
                    }
                    rc.writeSharedArray(FLAGSPAWN_INDEX+i, array[FLAGSPAWN_INDEX+i]);
                    return;
                }
            }
        }
    }

    public static MapLocation getBuilderNeededSpawn(RobotController rc, MapLocation curr) throws GameActionException {
        MapLocation best = null;
        int bestI = -1;
        int bestDist = 1000000;
        MapLocation m;
        int dist;
        for (int i = 3; i-->0;) {
            if (array[FLAGSPAWN_INDEX+i]>0) {
                int val = array[FLAGSPAWN_INDEX+i]-1;
                if ((val & builder_mask) > 0) {
                    m = getRespawnLocation(rc, i);
                    rc.setIndicatorDot(m, 0, 0, 255);
                    dist = m.distanceSquaredTo(curr);
                    if (dist < bestDist) {
                        bestDist = dist;
                        best = m;
                        bestI = i;
                    }
                }
            }
        }

        if (best != null) {
            int val = array[FLAGSPAWN_INDEX+bestI]-1;
            array[FLAGSPAWN_INDEX+bestI] = (val ^ builder_mask) + 1;
            rc.writeSharedArray(FLAGSPAWN_INDEX+bestI, array[FLAGSPAWN_INDEX+bestI]);
        }
        return best;
    }
    
    /**
     * Format: round (11) | turns (5)
     */
    public static void updateRespawn(RobotController rc, int turnsL) throws GameActionException {
        int round = array[RESPAWN_INDEX] >> 5;
        int turns = array[RESPAWN_INDEX] % 32;
        int i = turns - (rc.getRoundNum() - round);
        if (i <= 0) {
            array[RESPAWN_INDEX] = (rc.getRoundNum() << 5) + turnsL;
            rc.writeSharedArray(RESPAWN_INDEX, array[RESPAWN_INDEX]);
        }
    }

    public static int getRespawn(RobotController rc) throws GameActionException {
        int round = array[RESPAWN_INDEX] >> 5;
        int turns = array[RESPAWN_INDEX] % 32;
        int i = turns - (rc.getRoundNum() - round);
        if (i > 0) return i;
        return 2000;
    }

    public static void resetRespawn(RobotController rc) throws GameActionException {
        rc.writeSharedArray(RESPAWN_INDEX, 0);
    }

    public static void updateRespawnRound(RobotController rc, int i) throws GameActionException {
        array[RESPAWNROUND_INDEX+i] = rc.getRoundNum();
        rc.writeSharedArray(RESPAWNROUND_INDEX+i, array[RESPAWNROUND_INDEX+i]);
    }

    public static int getRespawnRound(RobotController rc, int i) throws GameActionException {
        return array[RESPAWNROUND_INDEX+i];
    }

    public static MapLocation getRespawnLocation(RobotController rc, int i) throws GameActionException {
        MapLocation m;
        if (array[RESPAWN_INDEX+i] > 0) {
            return new MapLocation(((array[FLAGSPAWN_INDEX+i]-1) >> 6) % 64, (array[FLAGSPAWN_INDEX+i]-1) % 64);
        } else {
            return null;
        }
    }

    public static void claimBuilder(RobotController rc) throws GameActionException {
        array[BUILDERCOUNT_INDEX]++;
        rc.writeSharedArray(BUILDERCOUNT_INDEX, array[BUILDERCOUNT_INDEX]);
    }

    public static int countBuilders() throws GameActionException {
        return array[BUILDERCOUNT_INDEX];
    }

    public static void claimHealer(RobotController rc) throws GameActionException {
        array[HEALERCOUNT_INDEX]++;
        rc.writeSharedArray(HEALERCOUNT_INDEX, array[HEALERCOUNT_INDEX]);
    }

    public static int countHealers() throws GameActionException {
        return array[HEALERCOUNT_INDEX];
    }
}
