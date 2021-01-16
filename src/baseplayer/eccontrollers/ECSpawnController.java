package baseplayer.eccontrollers;

import baseplayer.BotEnlightenment;
import battlecode.common.*;

public class ECSpawnController implements ECController {
    private final int[] SLANDERER_VALUES = {21, 42, 63, 85, 107, 130, 154, 178, 203, 228, 255, 282, 310, 339, 368, 399, 431, 463, 497};
    private final int MAX_BUILD_AMOUNT = 500;
    private final int MIN_BUILD_AMOUNT = 20;
    private static double POLITICIAN_RATE = 0.35;
    private static double MUCKRAKER_RATE = 0.35;
    private static double SLANDERER_RATE = 0.30;
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final ECBudgetController bc;
    private Direction nextSpawnDirection = Direction.NORTH;
    private int prevBudget = 0;
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
        if(roundNum > 100 ) {
            minAmount = roundNum - ec.getLastEnemySeen() > 250 ? (int) (MAX_BUILD_AMOUNT * Math.pow(roundNum / 1500, 2) + MIN_BUILD_AMOUNT) : MIN_BUILD_AMOUNT;
        }
        int buildAmount = toBuild.equals(RobotType.MUCKRAKER) ? 1 : budget;
        if (toBuild.equals(RobotType.SLANDERER)){
            int i = 0;
            while (minAmount > SLANDERER_VALUES[i] && i < 18){
                i++;
            }
            buildAmount = SLANDERER_VALUES[i];
        }
        if(roundNum <= 16 / rc.sensePassability(rc.getLocation())){
            buildAmount = 11;
        }
        if(buildAmount >= minAmount || toBuild.equals(RobotType.MUCKRAKER)) {
            for (int i = 0; i < 8; i++) {
                if (rc.canBuildRobot(toBuild, nextSpawnDirection, buildAmount)) {
                    rc.buildRobot(toBuild, nextSpawnDirection, buildAmount);
                    bc.withdrawBudget(this, buildAmount);
                    ec.recordSpawn(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID, robotToSpawn());
                    break;
                } else {
                    nextSpawnDirection = nextSpawnDirection.rotateRight();
                }
            }
        }
    }
    //Round by round hard coding / changing the spawn rates over time
    private RobotType robotToSpawn() throws GameActionException{
        int roundNumber = rc.getRoundNum();
        //Spawn 8 Politicians early for scouting
        if(roundNumber <= 16 / rc.sensePassability(rc.getLocation())){
            return RobotType.POLITICIAN;
        }
        //Do the checks for safety
        if(ec.getSafetyEval() < 0 ){
            POLITICIAN_RATE = 0.75;
            MUCKRAKER_RATE = 0.25;
            SLANDERER_RATE = 0;
            System.out.println("VERY BAD");
        }
        else if(ec.getThisRoundNeutralEcSpottedInfo().isPresent()){
            POLITICIAN_RATE = 1.0;
            MUCKRAKER_RATE = 0.0;
            SLANDERER_RATE = 0.0;
        }
        else{
            POLITICIAN_RATE = 0.45;
            MUCKRAKER_RATE = 0.10;
            SLANDERER_RATE = 0.45;
        }
        if((double) ec.getSlandererCount() / ec.getLocalRobotCount() < SLANDERER_RATE){
            return RobotType.SLANDERER;
        }
        else if((double)ec.getMuckrakerCount() /  ec.getLocalRobotCount()  < MUCKRAKER_RATE){
            return RobotType.MUCKRAKER;
        }
        else return RobotType.POLITICIAN;

    }
}
