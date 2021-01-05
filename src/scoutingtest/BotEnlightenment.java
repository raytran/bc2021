package scoutingtest;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

import java.util.HashSet;
import java.util.Set;

public class BotEnlightenment extends BotController {

    Set<Integer> spawnedIds = new HashSet<>();
    boolean enemyFound = false;
    Direction nextSpawnDirection = Direction.NORTH;
    public BotEnlightenment(RobotController rc) {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        //RobotType toBuild = Utilities.randomSpawnableRobotType();
        RobotType toBuild = RobotType.MUCKRAKER;
        int influence = 50;
        Direction originalSpawnDir = nextSpawnDirection;
        while (!enemyFound) {
            nextSpawnDirection = nextSpawnDirection.rotateRight();
            if (originalSpawnDir.equals(nextSpawnDirection)){
                break;
            }
            if (rc.canBuildRobot(toBuild, nextSpawnDirection, influence)){
                //Built the robot, add id to total
                rc.buildRobot(toBuild, nextSpawnDirection, influence);
                spawnedIds.add(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID);
                break;
            }
        }

        // Check flags
        for (Integer id : spawnedIds){
            if (rc.canGetFlag(id)){
                if (rc.getFlag(id) == 69){
                    enemyFound = true;
                    System.out.println("Enemy found; flag read");
                }
            }
        }
    }
}
