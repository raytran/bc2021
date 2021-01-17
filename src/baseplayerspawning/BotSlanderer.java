package baseplayerspawning;

import baseplayerspawning.flags.Flags;
import battlecode.common.*;

public class BotSlanderer extends BotController {
    Direction awayFromEnemy;
    MapLocation targetLoc;
    int closest = Integer.MAX_VALUE;
    boolean flagSet = false;
    public BotSlanderer(RobotController rc) throws GameActionException {
        super(rc);
        awayFromEnemy = Utilities.randomDirection();
    }

    @Override
    public BotController run() throws GameActionException {
        senseNearbyRobots(this::onNearbyEnemy, this::onNearbyFriendly, this::onNearbyNeutral);
        if (targetLoc == null){
            nav.spreadOut(awayFromEnemy);
        }else{
            nav.moveTo(targetLoc);
        }
        if (rc.getType() == RobotType.POLITICIAN){
            // Just converted; return a politician controller instead
            return BotPolitician.fromSlanderer(this);
        }

        // Search for boundary if we can
        if (Clock.getBytecodesLeft() > 1000) {
            searchForNearbyBoundaries();
            flagBoundaries();
        }
        closest = Integer.MAX_VALUE;
        flagSet = false;
        return this;
    }

    private void onNearbyEnemy(RobotInfo robotInfo) throws GameActionException {
        if (rc.getLocation().distanceSquaredTo(robotInfo.location) < closest){
            awayFromEnemy = robotInfo.location.directionTo(rc.getLocation());
            closest = rc.getLocation().distanceSquaredTo(robotInfo.location);
        }
        if (!flagSet) {
            rc.setFlag(Flags.encodeEnemySpotted(rc.getRoundNum(), robotInfo.location, robotInfo.getType(), false));
            flagSet = true;
        }
    }

    private void onNearbyFriendly(RobotInfo robotInfo) throws GameActionException {
        if (rc.canGetFlag(robotInfo.ID)){
            if (rc.getFlag(robotInfo.ID) == 1){
                targetLoc = robotInfo.location;
            }
        }

    }

    private void onNearbyNeutral(RobotInfo robotInfo) throws GameActionException {
        if (!flagSet) {
            rc.setFlag(Flags.encodeNeutralEcSpotted(rc.getRoundNum(), robotInfo.location, robotInfo.conviction));
            flagSet = true;
        }
    }

}
