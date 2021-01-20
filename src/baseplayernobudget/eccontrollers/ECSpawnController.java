package baseplayernobudget.eccontrollers;

import baseplayernobudget.BotEnlightenment;
import battlecode.common.*;

public class ECSpawnController implements ECController {
    private static double POLITICIAN_RATE = 0.2;
    private static double MUCKRAKER_RATE = 0.5;
    private static double SLANDERER_RATE = 0.3;
    private final int[] SLANDERER_VALUES = {21, 42, 63, 85, 107, 130, 154, 178, 203, 228, 255, 282, 310, 339, 368, 399, 431, 463, 497};
    private final int MAX_BUILD_AMOUNT = 250;
    private final int MIN_BUILD_AMOUNT = 21;
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final ECBudgetController bc;
    private Direction nextSpawnDirection = Direction.NORTH;
    private int prevBudget = 0;
    private boolean opPoliticianNeeded = false;
    private int opAmount = 150;
    private int numSpawned = 0;
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
        int minAmount = ec.getAvgSafetyEval() > 50 ? (int) (MAX_BUILD_AMOUNT * Math.pow(roundNum/1500.0, 2) + MIN_BUILD_AMOUNT) : (int) (budget * 0.5);
        int buildAmount = toBuild.equals(RobotType.MUCKRAKER) ? 1 : Math.max(minAmount, MIN_BUILD_AMOUNT);


        if (toBuild.equals(RobotType.SLANDERER)){
            int i = 0;
            while (minAmount > SLANDERER_VALUES[i] && i < 18){
                i++;
            }
            buildAmount = SLANDERER_VALUES[i];
        }
        if(toBuild.equals(RobotType.POLITICIAN) && ec.getThisRoundNeutralEcSpottedInfo().isPresent() && ec.getAvgSafetyEval() > 100){
            opPoliticianNeeded = true;
            opAmount = (int) (ec.getThisRoundNeutralEcSpottedInfo().get().conviction * 1.25);
            opAmount = (opAmount < rc.getInfluence() / 2)? opAmount : (int) (opAmount * 0.5);
            opAmount = Math.max(minAmount, Math.max(budget, opAmount));
            buildAmount = opAmount;
        }
        if(opPoliticianNeeded){
            buildAmount = opAmount;
        }

        if(bc.canSpend(this, buildAmount)) {
            for (int i = 0; i < 8; i++) {
                if (rc.canBuildRobot(toBuild, nextSpawnDirection, buildAmount)) {
                    //Built the robot, add id to total
                    rc.buildRobot(toBuild, nextSpawnDirection, buildAmount);
                    bc.withdrawBudget(this, buildAmount);
                    ec.recordSpawn(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID, robotToSpawn());
                    if (opPoliticianNeeded) {
                        ec.setOpSpawned(true);
                        opPoliticianNeeded = false;
                    }
                    numSpawned++;
                    break;
                } else {
                    nextSpawnDirection = nextSpawnDirection.rotateRight();
                }
            }
        } else if (bc.canSpend(this,1) && roundNum < 500) {
            toBuild = RobotType.MUCKRAKER;
            buildAmount = 1;
            for (int i = 0; i < 8; i++) {
                if (rc.canBuildRobot(toBuild, nextSpawnDirection, buildAmount)) {
                    //Built the robot, add id to total
                    rc.buildRobot(toBuild, nextSpawnDirection, buildAmount);
                    bc.withdrawBudget(this, buildAmount);
                    ec.recordSpawn(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID, robotToSpawn());
                    if (opPoliticianNeeded) {
                        ec.setOpSpawned(true);
                        opPoliticianNeeded = false;
                    }
                    numSpawned++;
                    break;
                } else {
                    nextSpawnDirection = nextSpawnDirection.rotateRight();
                }
            }
        }

    }
    //Round by round hard coding / changing the spawn rates over time
    private RobotType robotToSpawn() throws GameActionException{
        int roundNum = rc.getRoundNum();
        if (numSpawned < 18 ) {
            MUCKRAKER_RATE = 1.0;
            POLITICIAN_RATE = 0.0;
            SLANDERER_RATE = 0.0;
        }
        //"Normal" Rates
        if (numSpawned > 9 && roundNum <= 1500 ){
            MUCKRAKER_RATE = 0.2;
            POLITICIAN_RATE = 0.8;
            SLANDERER_RATE = 0;
        }
        if (ec.getAvgSafetyEval() > 25 && roundNum > 100) {
            MUCKRAKER_RATE = 0.1;
            POLITICIAN_RATE = 0.6;
            SLANDERER_RATE = 0.3;
        }

        RobotInfo[] robots = rc.senseNearbyRobots(RobotType.ENLIGHTENMENT_CENTER.sensorRadiusSquared,rc.getTeam().opponent());
        for(RobotInfo robot : robots){
            if(robot.getType() == RobotType.MUCKRAKER){
                MUCKRAKER_RATE = 0.35;
                POLITICIAN_RATE = 0.65;
                SLANDERER_RATE = 0;
            }
        }

        if(ec.getSafetyEval() < 0){
            return RobotType.POLITICIAN;
        }
        if(ec.getThisRoundNeutralEcSpottedInfo().isPresent()){
            opPoliticianNeeded = true;
            return RobotType.POLITICIAN;
        }
        if((double)ec.getMuckrakerCount() /  ec.getLocalRobotCount()  < MUCKRAKER_RATE){
            return RobotType.MUCKRAKER;
        }
        if((double) ec.getSlandererCount() / ec.getLocalRobotCount() < SLANDERER_RATE){
            return RobotType.SLANDERER;
        }
        if((double) ec.getPoliticianCount() / ec.getLocalRobotCount() < POLITICIAN_RATE){
            return RobotType.POLITICIAN;
        }
        else{
            ////System.out.println("TRYING TO SPAWN AN ENLIGHTENMENT CENTER");
            return RobotType.MUCKRAKER;
        }
    }
}
