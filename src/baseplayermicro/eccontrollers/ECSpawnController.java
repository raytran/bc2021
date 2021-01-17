package baseplayermicro.eccontrollers;

import baseplayermicro.BotEnlightenment;
import battlecode.common.*;

public class ECSpawnController implements ECController {
    private final int[] SLANDERER_VALUES = {21, 42, 63, 85, 107, 130, 154, 178, 203, 228, 255, 282, 310, 339, 368, 399, 431, 463, 497};
    private final int MAX_BUILD_AMOUNT = 500;
    private final int MIN_BUILD_AMOUNT = 20;
    private static final RobotType[] robotQueue = {RobotType.SLANDERER,RobotType.POLITICIAN,RobotType.MUCKRAKER};
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final ECBudgetController bc;
    private Direction nextSpawnDirection = Direction.NORTH;
    private int prevBudget = 0;
    private int numSpawned = 0;
    private int currentSpawnIndex = 0;
    private boolean bigPoliticianNeeded = false;
    public ECSpawnController(RobotController rc, BotEnlightenment ec, ECBudgetController bc) {
        this.rc = rc;
        this.ec = ec;
        this.bc = bc;
    }
    @Override
    public void run() throws GameActionException {
        RobotType toBuild = robotToSpawn();
        MapLocation myLoc = rc.getLocation();
        int budget = bc.getBotBudget();
        int roundNum = rc.getRoundNum();
        boolean givenOne = budget - prevBudget == 1;
        int minAmount = 11;
        prevBudget = budget;
        if(budget == 1) toBuild = RobotType.MUCKRAKER;
        if(numSpawned > 24 ){
            minAmount = roundNum - ec.getLastEnemySeen() > 250 ? (int) (MAX_BUILD_AMOUNT * Math.pow(roundNum / 3000, 2) + MIN_BUILD_AMOUNT) : MIN_BUILD_AMOUNT;
        }
        int buildAmount = toBuild.equals(RobotType.MUCKRAKER) ? 1 : budget;
        if (toBuild.equals(RobotType.SLANDERER)){
            int i = 0;
            while (minAmount > SLANDERER_VALUES[i] && i < 18){
                i++;
            }
            buildAmount = SLANDERER_VALUES[i];
        }
        //if(numSpawned < 8){
          //  buildAmount = 11;
        //}
        if(bigPoliticianNeeded && ec.getThisRoundNeutralEcSpottedInfo().isPresent()){
            //buildAmount = ec.getThisRoundNeutralEcSpottedInfo().get().conviction + 10;
        }
        if(buildAmount >= minAmount || toBuild.equals(RobotType.MUCKRAKER)) {
            for (int i = 0; i < 8; i++) {
                if (rc.canBuildRobot(toBuild, nextSpawnDirection, buildAmount)) {
                    rc.buildRobot(toBuild, nextSpawnDirection, buildAmount);
                    bc.withdrawBudget(this, buildAmount);
                    ec.recordSpawn(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID, robotToSpawn());
                    currentSpawnIndex = (currentSpawnIndex == 3) ? 0 : ++currentSpawnIndex;
                    numSpawned++;
                    bigPoliticianNeeded = false;
                    break;
                } else {
                    nextSpawnDirection = nextSpawnDirection.rotateRight();
                }
            }
        }
        rc.setFlag(numSpawned/3 + (numSpawned % 3));
    }
    //Round by round hard coding / changing the spawn rates over time
    private RobotType robotToSpawn() throws GameActionException{
        //Do the special checks
        if(ec.getSafetyEval() < 0){
            return RobotType.POLITICIAN;
        }
        if(ec.getThisRoundNeutralEcSpottedInfo().isPresent()){
            bigPoliticianNeeded = true;
            return RobotType.POLITICIAN;
        }
        if(numSpawned < 8){
            return RobotType.MUCKRAKER;
        }
        else return robotQueue[currentSpawnIndex];
    }
}
