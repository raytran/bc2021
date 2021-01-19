package placeholderplayer;

import baseplayer.flags.Flags;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class BotSlanderer extends BotControllerWalking {
    MapLocation targetLoc;
    public BotSlanderer(RobotController rc) throws GameActionException {
        super(rc);
        targetLoc = parentInfoAtSpawn.location.translate((int) (5 * Math.random()), (int) (5 * Math.random()));
    }

    @Override
    public BotController run() throws GameActionException {
        senseNearbyRobots(this::onNearbyEnemy, this::onNearbyFriendly, this::onNearbyNeutral);
        nav.moveTo(targetLoc);

        return this;
    }

    private void onNearbyEnemy(RobotInfo robotInfo) throws GameActionException {

    }

    private void onNearbyFriendly(RobotInfo robotInfo) throws GameActionException {
        if (rc.canGetFlag(robotInfo.ID)){
            if (rc.getFlag(robotInfo.ID) == 1){
                targetLoc = robotInfo.location;
            }
        }
    }

    private void onNearbyNeutral(RobotInfo robotInfo) throws GameActionException {
    }
}
