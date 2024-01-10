package egg;

import battlecode.common.*;

public strictfp class Communications {
    
    public static final int ENEMIES_START = 0;
    public static final int ENEMIES_NUM = 8;
    public static final int INF = 1000000;
    public static final int UPDATE_DIST_SQ = 9;

    public static final int EXP_INDEX = 62;

    public static int[] array = new int[64];
    static Team team;
    static int round;

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
    public static void updateEnemies(RobotController rc, RobotInfo[] robots) throws GameActionException {
        int totalScore = 0;
        MapLocation target = null;
        int targetScore = 0;
        
        int score;
        for (RobotInfo robot : robots) {
            if (robot.team != team) {
                score = 1;
                if (robot.hasFlag) score += 77;
            } else {
                score = 0;
            }
            totalScore += score;
            if (score > targetScore) {
                targetScore = score;
                target = robot.location;
            }
        }

        if (target == null) return;

        int lowestScore = INF;
        int lowestIndex = -1;

        
        int reportRound;
        MapLocation l;
        for (int i = ENEMIES_START + ENEMIES_NUM * 2 - 2; i >= ENEMIES_START; i -= 2) {
            reportRound = array[i] >> 5;
            l = new MapLocation((array[i+1] >> 6) % 64, array[i+1] % 64);
            score = (array[i] % 32) * 16 + (array[i+1] >> 12);
            score = score >> (round - reportRound);

            if (l.distanceSquaredTo(target) <= UPDATE_DIST_SQ) {
                if (totalScore > score) {
                    array[i] = (totalScore >> 4) + (round << 5);
                    array[i+1] = target.y + ((target.x + ((totalScore % 16) << 6)) << 6);
                    rc.writeSharedArray(i, array[i]);
                    rc.writeSharedArray(i+1, array[i+1]);
                }
                return;
            }

            if (score < lowestScore) {
                lowestScore = score;
                lowestIndex = i;
            }
        }

        if (lowestScore < totalScore) {
            array[lowestIndex] = (totalScore >> 4) + (round << 5);
            array[lowestIndex+1] = target.y + ((target.x + ((totalScore % 16) << 6)) << 6);
            rc.writeSharedArray(lowestIndex, array[lowestIndex]);
            rc.writeSharedArray(lowestIndex+1, array[lowestIndex+1]);
        }
    }

    public static MapLocation readEnemies(MapLocation curr) throws GameActionException {
        int bestIndex = -1;
        double bestScore = 0;
        MapLocation bestLoc = null;

        int reportRound;
        double score;
        MapLocation l;
        for (int i = ENEMIES_START + ENEMIES_NUM * 2 - 2; i >= ENEMIES_START; i -= 2) {
            reportRound = array[i] >> 5;
            l = new MapLocation((array[i+1] >> 6) % 64, array[i+1] % 64);
            score = (array[i] % 32) * 16 + (array[i+1] >> 12);
            score /= (1 + Math.sqrt(curr.distanceSquaredTo(l)) + 2 * (round - reportRound));
            if (score > bestScore) {
                bestScore = score;
                bestIndex = i;
                bestLoc = l;
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

}
