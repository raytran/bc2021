package dlmoreram012421_01.eccontrollers;

import battlecode.common.*;
import dlmoreram012421_01.BotEnlightenment;

public class ECSpawnController implements ECController {
    private final int[] SLANDERER_VALUES = {21, 42, 63, 85, 107, 130, 154, 178, 203, 228, 255, 282, 310, 339, 368, 399, 431, 463, 497, 532, 568, 605, 644, 683, 724, 766, 810, 855, 902, 949};
    private final RobotType[] EARLY_BUILD_Q = {RobotType.SLANDERER, RobotType.MUCKRAKER, RobotType.MUCKRAKER, RobotType.MUCKRAKER};
    private final RobotType[] NORMAL_BUILD_Q = {RobotType.SLANDERER, RobotType.POLITICIAN, RobotType.SLANDERER, RobotType.MUCKRAKER, RobotType.SLANDERER, RobotType.POLITICIAN, RobotType.SLANDERER, RobotType.POLITICIAN};
    private final RobotType[] SAFE_BUILD_Q = {RobotType.SLANDERER, RobotType.MUCKRAKER, RobotType.SLANDERER, RobotType.POLITICIAN, RobotType.POLITICIAN, RobotType.SLANDERER};
    private final int MIN_BUILD_AMOUNT = 21;
    private final int minDefenderCount = 5;
    private final RobotController rc;
    private final BotEnlightenment ec;
    private Direction nextSpawnDirection = Direction.NORTH;
    private int numSpawned = 0;
    public ECSpawnController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
    }
    @Override
    public void run() throws GameActionException {
        RobotType toBuild = robotToSpawn();
        int inf;

        switch(toBuild) {
            case SLANDERER:
                inf = getSlandererBuildInfluence();
                break;
            case POLITICIAN:
                inf = getPoliticianBuildInfluence();
                break;
            case MUCKRAKER:
                inf = getMuckrakerBuildInfluence();
                break;
            default: throw new RuntimeException("Type error in spawn controller");
        }

        //System.out.println("BUILDING " + toBuild + " WITH " + inf);
        for (int i = 0;i<8;i++) {
            if (rc.canBuildRobot(toBuild, nextSpawnDirection, inf)) {
                rc.buildRobot(toBuild, nextSpawnDirection, inf);
                if (toBuild.equals(RobotType.POLITICIAN) && inf < MIN_BUILD_AMOUNT) {
                    ec.recordDefender(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID);
                } else {
                    ec.recordSpawn(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID, toBuild);
                }
                numSpawned++;
                break;
            } else if (rc.canBuildRobot(RobotType.MUCKRAKER, nextSpawnDirection, 1) && rc.getRoundNum() % 5 == 0) {
                rc.buildRobot(RobotType.MUCKRAKER, nextSpawnDirection, 1);
                ec.recordSpawn(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID, RobotType.MUCKRAKER);
            } else {
                nextSpawnDirection = nextSpawnDirection.rotateRight();
            }
        }
    }

    //Round by round hard coding / changing the spawn rates over time
    private RobotType robotToSpawn() throws GameActionException{
        int roundNum = rc.getRoundNum();
        RobotType toBuild;

        //pick from build queues most of the time
        if (numSpawned < 9 && roundNum < 50) {
            toBuild = EARLY_BUILD_Q[numSpawned % EARLY_BUILD_Q.length];
        } else if (ec.getAvgSafetyEval() > 150 && ec.getSafetyEval() > 100 && roundNum - ec.getLastEnemySeen() > 25) {
            toBuild = SAFE_BUILD_Q[numSpawned % SAFE_BUILD_Q.length];
        } else {
            toBuild = NORMAL_BUILD_Q[numSpawned % NORMAL_BUILD_Q.length];
        }

        if(ec.getNearbyEnemyMuckrakers() > 3 * ec.getNearbyAllyPoliticians()){
            return RobotType.POLITICIAN;
        }

        if (ec.getSafetyEval() < 0 && rc.getInfluence() < Math.abs(ec.getSafetyEval())) {
            return RobotType.MUCKRAKER;
        }

        // curb spawning too many individual slanderers in the midgame
        if(ec.getSlandererCount() > 2 * (ec.getPoliticianCount() + ec.getDefenderCount()) && roundNum > 150) {
            return RobotType.POLITICIAN;
        }

        return toBuild;
    }

    private int getSlandererBuildInfluence() {
        if (rc.getRoundNum() == 1) {
            return 130;
        } else {
            int inf;
            int budget = ec.getAvgSafetyEval() > 50 ? (int) (rc.getInfluence() * 0.9) : (int) (rc.getInfluence() * 0.7);
            int i = SLANDERER_VALUES.length - 1;
            while (SLANDERER_VALUES[i] > budget) {
                i -= 1;
                if (i < 0) break;
            }
            if (i < 0) inf = 0;
            else inf = SLANDERER_VALUES[i];
            return inf;
        }
    }

    private int getMuckrakerBuildInfluence(){
        if (rc.getRoundNum() < 50 || (ec.getSafetyEval() < 0 && rc.getInfluence() < Math.abs(ec.getSafetyEval()))) {
            return 1;
        } else {
            return Math.max(1, (int) (0.1 * rc.getInfluence()));
        }
    }

    private int getPoliticianBuildInfluence() {
        // normal amount
        int inf = ec.getThisRoundNeutralEcSpottedInfo().isPresent() ? Math.max((int) (0.65 * rc.getInfluence()), MIN_BUILD_AMOUNT) : Math.max((int) (0.4 * rc.getInfluence()), MIN_BUILD_AMOUNT);

        // build a defender if needed
        if (ec.getDefenderCount() < minDefenderCount && ec.getAvgSafetyEval() < 50) {
            inf = MIN_BUILD_AMOUNT - 1;
        }

        // proceed with big politician strat if on map like CrossStitch
        if (ec.isSpawnNextToEnemyEC()) {
            inf = 131;
            System.out.println("Spawned next to enemy ec");
        } else if (inf == 131) {
            inf = 130;
        }

        return inf;
    }
}
