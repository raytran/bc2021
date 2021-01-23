package dlmoreram012321_01;

import dlmoreram012321_01.flags.EnemySpottedInfo;
import dlmoreram012321_01.flags.FlagType;
import dlmoreram012321_01.flags.Flags;
import battlecode.common.*;

import java.util.Map;

public class BotSlanderer extends BotController {
    Direction awayFromEnemy;
    MapLocation targetLoc;
    int closest = Integer.MAX_VALUE;
    boolean flagSet = false;
    public BotSlanderer(RobotController rc) throws GameActionException {
        super(rc);
        awayFromEnemy = Utilities.randomDirection();
        targetLoc = parentLoc.get().translate((int) (5 * Math.random()), (int) (5 * Math.random()));
    }

    @Override
    public BotController run() throws GameActionException {
        senseNearbyRobots(this::onNearbyEnemy, this::onNearbyFriendly, this::onNearbyNeutral);
        nav.moveTo(targetLoc);
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
            targetLoc = rc.getLocation().add(robotInfo.location.directionTo(rc.getLocation()));
        }
        if (!flagSet) {
            rc.setFlag(Flags.encodeEnemySpotted(rc.getRoundNum(), robotInfo.location, robotInfo.getType(), false));
            flagSet = true;
        }
    }

    private void onNearbyFriendly(RobotInfo robotInfo) throws GameActionException {
        if (rc.canGetFlag(robotInfo.ID)){
            int allyFlag = rc.getFlag(robotInfo.ID);
            if (allyFlag == 1){
                MapLocation allyLoc = robotInfo.location;
                if(parentLoc.isPresent()) targetLoc = allyLoc.add(allyLoc.directionTo(parentLoc.get()));
                else targetLoc = allyLoc;
            } else if (Flags.decodeFlagType(allyFlag) == FlagType.ENEMY_SPOTTED) {
                EnemySpottedInfo nearbyEnemy = Flags.decodeEnemySpotted(rc.getLocation(), allyFlag);
                if (!nearbyEnemy.isGuess) {
                    awayFromEnemy = nearbyEnemy.location.directionTo(rc.getLocation());
                    targetLoc = rc.getLocation().add(awayFromEnemy);
                }
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
