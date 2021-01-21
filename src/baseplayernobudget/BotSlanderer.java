package baseplayernobudget;

import baseplayernobudget.flags.*;
import battlecode.common.*;

public class BotSlanderer extends BotController {
    Direction awayFromEnemy;
    BoundarySpottedInfo[] spottedBoundaries = new BoundarySpottedInfo[4];
    MapLocation targetLoc;
    MapLocation homeLoc;
    int lastEnemySighting;
    int closest = Integer.MAX_VALUE;
    boolean flagSet = false;
    public BotSlanderer(RobotController rc) throws GameActionException {
        super(rc);
        awayFromEnemy = Utilities.randomDirection();
        homeLoc = parentLoc.get();
        targetLoc = homeLoc.translate((int) (5 * Math.random()), (int) (5 * Math.random()));
    }

    @Override
    public BotController run() throws GameActionException {
        Utilities.checkBoundaries(rc, spottedBoundaries);
        senseNearbyRobots(this::onNearbyEnemy, this::onNearbyFriendly, this::onNearbyNeutral);
        /*if (rc.getRoundNum() - lastEnemySighting < 5) {
            targetLoc = rc.getLocation().add(awayFromEnemy);
        }*/
        Direction toBoundary = Utilities.toNearestBoundary(rc, spottedBoundaries);
        /*nav.moveTo(targetLoc);*/
        if (toBoundary != null) nav.moveTo(rc.getLocation().add(toBoundary));
        else nav.moveTo(targetLoc);
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
        lastEnemySighting = rc.getRoundNum();
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
        /*if (rc.canGetFlag(robotInfo.ID)){
            int flag = rc.getFlag(robotInfo.ID);
            if (flag == 1){
                robotInfo.location.add(robotInfo.location.directionTo(homeLoc));
            }
            /*if (Flags.decodeFlagType(flag) == FlagType.ENEMY_SPOTTED) {
               EnemySpottedInfo enemyInfo = Flags.decodeEnemySpotted(rc.getLocation(), flag);
               targetLoc = rc.getLocation().add(enemyInfo.location.directionTo(rc.getLocation()));
               System.out.println("running from reported enemy");
            }
        }*/
    }

    private void onNearbyNeutral(RobotInfo robotInfo) throws GameActionException {
        if (!flagSet) {
            rc.setFlag(Flags.encodeNeutralEcSpotted(rc.getRoundNum(), robotInfo.location, robotInfo.conviction));
            flagSet = true;
        }
    }

}
