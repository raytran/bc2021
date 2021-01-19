package placeholderplayer;

import battlecode.common.*;

public class BotMuckraker extends BotControllerWalking {
    Direction targetDir;
    public BotMuckraker(RobotController rc) throws GameActionException {
        super(rc);
        targetDir = parentInfoAtSpawn.location.directionTo(rc.getLocation());
    }

    @Override
    public BotController run() throws GameActionException {
        if (rc.onTheMap(rc.getLocation().add(targetDir)) && !rc.isLocationOccupied(rc.getLocation().add(targetDir))){
            if (rc.canMove(targetDir))
                rc.move(targetDir);
        } else {
            targetDir = Utilities.randomDirection();
        }
        return this;
    }
}
