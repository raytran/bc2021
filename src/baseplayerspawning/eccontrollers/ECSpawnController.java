package baseplayerspawning.eccontrollers;

import battlecode.common.*;
import baseplayerspawning.BotEnlightenment;

public class ECSpawnController implements ECController {
    private static double POLITICIAN_RATE = 0.2;
    private static double MUCKRAKER_RATE = 0.5;
    private static double SLANDERER_RATE = 0.3;
    private final int[] SLANDERER_VALUES = {21, 42, 63, 85, 107, 130, 154, 178, 203, 228, 255, 282, 310, 339, 368, 399, 431, 463, 497};
    private final int MAX_BUILD_AMOUNT = 500;
    private final int MIN_BUILD_AMOUNT = 20;
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final ECBudgetController bc;
    private Direction nextSpawnDirection = Direction.NORTH;
    private int prevBudget = 0;
    private boolean opPoliticianNeeded = false;
    private int opAmount = 0;
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
        prevBudget = budget;
        // if only given one influence to spend, spawn MUCKRAKER
        toBuild = givenOne ? RobotType.MUCKRAKER : toBuild;
        int buildAmount = toBuild.equals(RobotType.MUCKRAKER) ? 1 : budget;
        int minAmount = roundNum - ec.getSafetyEval() > 250 ? (int) (MAX_BUILD_AMOUNT * Math.pow(roundNum/2999., 2) + MIN_BUILD_AMOUNT) : MIN_BUILD_AMOUNT;

        if (toBuild.equals(RobotType.SLANDERER)){
            int i = 0;
            while (minAmount > SLANDERER_VALUES[i] && i < 18){
                i++;
            }
            buildAmount = SLANDERER_VALUES[i];
        }
        if(rc.getRoundNum() > 400 && toBuild.equals(RobotType.POLITICIAN) && ec.getThisRoundNeutralEcSpottedInfo().isPresent()){
          //  rc.setIndicatorDot(rc.getLocation(),255,0,0);
            opPoliticianNeeded = true;
            opAmount = (int) (ec.getThisRoundNeutralEcSpottedInfo().get().conviction * 1.05);
            buildAmount = opAmount;
            //System.out.println("Trying to spawn the super politicians with " + buildAmount + " Influence");
        }
        if(opPoliticianNeeded){
            buildAmount = opAmount;
        }

        if(buildAmount > minAmount || toBuild.equals(RobotType.MUCKRAKER)) {
            for (int i = 0; i < 8; i++) {
                //TODO This line is causing us not to spawn every turn we are not spawning even if it costs 1
                if (rc.canBuildRobot(toBuild, nextSpawnDirection, buildAmount)) {
                    //Built the robot, add id to total
                    rc.buildRobot(toBuild, nextSpawnDirection, buildAmount);
                    bc.withdrawBudget(this, buildAmount);
                    ec.recordSpawn(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID, robotToSpawn());
                    if(opPoliticianNeeded) {
                        ec.setOpSpawned(true);
                        opPoliticianNeeded = false;
                    }
                    break;
                }
                else{
                    nextSpawnDirection = nextSpawnDirection.rotateRight();
                }
            }
        }
    }
    //Round by round hard coding / changing the spawn rates over time
    private RobotType robotToSpawn() throws GameActionException{
        int roundNum = rc.getRoundNum();
        if (roundNum < 18 / rc.sensePassability(rc.getLocation())) {
            MUCKRAKER_RATE = 1.0;
            POLITICIAN_RATE = 0.0;
            SLANDERER_RATE = 0.0;
        }
        if (roundNum >= 18 / rc.sensePassability(rc.getLocation()) && roundNum <= 1200 ){
            MUCKRAKER_RATE = 0.35;
            POLITICIAN_RATE = 0.35;
            SLANDERER_RATE = 0.3;
        }
       if(roundNum > 1200) {
            MUCKRAKER_RATE = 0;
            POLITICIAN_RATE = 1;
            SLANDERER_RATE = 0.0;
        }
        //Special conditions
        RobotInfo[] robots = rc.senseNearbyRobots(RobotType.ENLIGHTENMENT_CENTER.sensorRadiusSquared,rc.getTeam().opponent());
        for(RobotInfo robot : robots){
            if(robot.getType() == RobotType.MUCKRAKER){
                ////System.out.println("Disabled Slanderer spawning");
                MUCKRAKER_RATE = 0.25;
                POLITICIAN_RATE = 0.75;
                SLANDERER_RATE = 0.0;
            }
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
