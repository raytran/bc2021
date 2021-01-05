package splitfileexamplefuncs;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class BotEnlightenment extends BotController {
    public BotEnlightenment(RobotController rc) {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        RobotType toBuild = Utilities.randomSpawnableRobotType();
        int influence = 50;
        for (Direction dir : Utilities.directions) {
            if (rc.canBuildRobot(toBuild, dir, influence)) {
                rc.buildRobot(toBuild, dir, influence);
            } else {
                break;
            }
        }
    }
}
