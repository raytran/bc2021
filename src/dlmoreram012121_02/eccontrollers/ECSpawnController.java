package dlmoreram012121_02.eccontrollers;

import battlecode.common.*;
import dlmoreram012121_02.BotEnlightenment;

import java.awt.*;

public class ECSpawnController implements ECController {
    private static double POLITICIAN_RATE = 0.2;
    private static double MUCKRAKER_RATE = 0.5;
    private static double SLANDERER_RATE = 0.3;
    private final int[] SLANDERER_VALUES = {21, 42, 63, 85, 107, 130, 154, 178, 203, 228, 255, 282, 310, 339, 368, 399, 431, 463, 497, 532, 568, 605, 644, 683, 724, 766, 810, 855, 902, 949};
    private final int MAX_BUILD_AMOUNT = 250;
    private final int MIN_BUILD_AMOUNT = 21;
    private final RobotController rc;
    private final BotEnlightenment ec;
    private Direction nextSpawnDirection = Direction.NORTH;
    private int prevBudget = 0;
    private boolean opPoliticianNeeded = false;
    private int opAmount = 150;
    private int numSpawned = 0;
    public ECSpawnController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
    }
    @Override
    public void run() throws GameActionException {
        RobotType toBuild = robotToSpawn();
        int budget = (int) (rc.getInfluence() * Math.min(0.9, ec.getAvgSafetyEval()/100.));
        int minAmount = ec.getAvgSafetyEval() > 50 ? budget : (int) (budget * 0.5);
        int buildAmount = toBuild.equals(RobotType.MUCKRAKER) ? (rc.getRoundNum() < 500 ? 1 : Math.max(minAmount, MIN_BUILD_AMOUNT)) : Math.max(minAmount, MIN_BUILD_AMOUNT);

        if (ec.getSafetyEval() < 0) {
            toBuild = RobotType.MUCKRAKER;
            buildAmount = 1;
        }

        if (toBuild.equals(RobotType.SLANDERER)){
            int i = 0;
            while (minAmount > SLANDERER_VALUES[i] && i < 18){
                i++;
            }
            buildAmount = SLANDERER_VALUES[i];
        }

        for (int i = 0; i < 8; i++) {
            if (rc.canBuildRobot(toBuild, nextSpawnDirection, buildAmount)) {
                //Built the robot, add id to total
                rc.buildRobot(toBuild, nextSpawnDirection, buildAmount);
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
    //Round by round hard coding / changing the spawn rates over time
    private RobotType robotToSpawn() throws GameActionException{
        int roundNum = rc.getRoundNum();
        RobotInfo[] robots = rc.senseNearbyRobots(RobotType.ENLIGHTENMENT_CENTER.sensorRadiusSquared,rc.getTeam().opponent());
        boolean nearbyEnemy = false;
        for(RobotInfo robot : robots) {
            nearbyEnemy = true;
        }
        if (rc.getRoundNum() == 1 && !nearbyEnemy) {
            return RobotType.SLANDERER;
        }
        if (numSpawned < 9 && roundNum < 100) {
            MUCKRAKER_RATE = 0.25;
            POLITICIAN_RATE = 0;
            SLANDERER_RATE = 0.75;
        }
        //"Normal" Rates
        if (numSpawned > 9 && roundNum <= 1500 ){
            MUCKRAKER_RATE = 0.1;
            POLITICIAN_RATE = 0.4;
            SLANDERER_RATE = 0.5;
        }
        if (ec.getAvgSafetyEval() < 0 || ec.getSafetyEval() < 0) {
            MUCKRAKER_RATE = 0.2;
            POLITICIAN_RATE = 0.8;
            SLANDERER_RATE = 0;
        }


        for(RobotInfo robot : robots){
            if(robot.getType() == RobotType.MUCKRAKER){
                MUCKRAKER_RATE = 0.1;
                POLITICIAN_RATE = 0.9;
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
        if((double) ec.getSlandererCount() / ec.getLocalRobotCount() < SLANDERER_RATE){
            return RobotType.SLANDERER;
        }
        if((double)ec.getMuckrakerCount() /  ec.getLocalRobotCount()  < MUCKRAKER_RATE){
            return RobotType.MUCKRAKER;
        }
        if((double) ec.getPoliticianCount() / ec.getLocalRobotCount() < POLITICIAN_RATE){
            return RobotType.POLITICIAN;
        }
        else{
            //////System.out.println("TRYING TO SPAWN AN ENLIGHTENMENT CENTER");
            return RobotType.MUCKRAKER;
        }
    }
}
