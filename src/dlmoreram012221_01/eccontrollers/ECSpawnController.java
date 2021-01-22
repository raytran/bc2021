package dlmoreram012221_01.eccontrollers;

import battlecode.common.*;
import dlmoreram012221_01.BotEnlightenment;

import java.awt.*;

public class ECSpawnController implements ECController {
    private static double POLITICIAN_RATE = 0.2;
    private static double MUCKRAKER_RATE = 0.5;
    private static double SLANDERER_RATE = 0.3;
    private final int[] SLANDERER_VALUES = {21, 42, 63, 85, 107, 130, 154, 178, 203, 228, 255, 282, 310, 339, 368, 399, 431, 463, 497, 532, 568, 605, 644, 683, 724, 766, 810, 855, 902, 949};
    private final RobotType[] EARLY_BUILD_Q = {RobotType.SLANDERER, RobotType.MUCKRAKER};
    private final int MIN_BUILD_AMOUNT = 21;
    private final int MIN_DEFENDER_COUNT = 5;
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
        int budget = (int) Math.max((rc.getInfluence() * 0.5), MIN_BUILD_AMOUNT);
        if (ec.getDefenderCount() < MIN_DEFENDER_COUNT) {
            if (!ec.isInCorner() || (ec.isInCorner() && ec.getSafetyEval() < 25)) {
                //System.out.println("spawning defender at eval: " + ec.getSafetyEval());
                budget = MIN_BUILD_AMOUNT;
            } else {
                budget++;
            }
        }
        int buildAmount = toBuild.equals(RobotType.MUCKRAKER) ? (int) (0.2 * budget) : budget;

        if (ec.getSafetyEval() < 0 && rc.getInfluence() < Math.abs(ec.getSafetyEval())) {
            toBuild = RobotType.MUCKRAKER;
            buildAmount = 1;
        }

        if (toBuild.equals(RobotType.SLANDERER)){
            budget = (int) (rc.getInfluence() * 0.8);
            int i = 0;
            while (budget > SLANDERER_VALUES[i] && i < SLANDERER_VALUES.length - 1){
                i++;
            }
            buildAmount = SLANDERER_VALUES[i];
        }

        if (rc.getRoundNum() == 1) {
            toBuild = RobotType.SLANDERER;
            buildAmount = 130;
        }

        for (int i = 0; i < 8; i++) {
            if (rc.canBuildRobot(toBuild, nextSpawnDirection, buildAmount)) {
                //Built the robot, add id to total
                rc.buildRobot(toBuild, nextSpawnDirection, buildAmount);
                ec.recordSpawn(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID, toBuild);
                numSpawned++;
                break;
            } else if (rc.canBuildRobot(RobotType.MUCKRAKER, nextSpawnDirection, 1) && rc.getRoundNum() % 4 == 0) {
                rc.buildRobot(RobotType.MUCKRAKER, nextSpawnDirection, 1);
                ec.recordSpawn(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID, RobotType.MUCKRAKER);
            }else {
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
            return EARLY_BUILD_Q[rc.getRoundNum() % 2];
        }
        //"Normal" Rates
        if (numSpawned > 9){
            MUCKRAKER_RATE = 0.1;
            POLITICIAN_RATE = 0.6;
            SLANDERER_RATE = 0.3;
        }
        if (ec.getAvgSafetyEval() > 200 && ec.getSafetyEval() > 0) {
            MUCKRAKER_RATE = 0.1;
            POLITICIAN_RATE = 0.3;
            SLANDERER_RATE = 0.6;
        }

        if(ec.getSafetyEval() < 0 || ec.getAvgSafetyEval() < 0 || nearbyEnemy){
            return RobotType.POLITICIAN;
        }
        if(ec.getThisRoundNeutralEcSpottedInfo().isPresent()){
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
