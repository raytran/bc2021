package baseplayer;

import baseplayer.flags.EnemySpottedInfo;
import baseplayer.flags.FlagType;
import baseplayer.flags.Flags;
import battlecode.common.*;

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
        MapLocation myLoc = rc.getLocation();
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

        // Check baseplayer.flags
        for (Integer id : spawnedIds){
            if (rc.canGetFlag(id)){
                int flag = rc.getFlag(id);
                switch (Flags.decodeFlagType(flag)){
                    case ENEMY_SPOTTED:
                        enemyFound = true;
                        EnemySpottedInfo info = Flags.decodeEnemySpotted(flag);
                        System.out.println("Enemy of type " + info.enemyType
                                + " found at " + (info.delta.x + myLoc.x) + " , " + (info.delta.y + myLoc.y)
                                + " by " + id);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
