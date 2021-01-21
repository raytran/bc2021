package baseplayernobudget;

import baseplayernobudget.flags.*;
import battlecode.common.*;

public class BotSlanderer extends BotController {
    Direction awayFromEnemy;
    MapLocation targetLoc;
    MapLocation homeLoc;
    BoundarySpottedInfo[] spottedBoundaries = new BoundarySpottedInfo[4];
    int closest = Integer.MAX_VALUE;
    int lastEnemySpotted = 0;
    boolean flagSet = false;
    public BotSlanderer(RobotController rc) throws GameActionException {
        super(rc);
        Utilities.checkBoundaries(rc, spottedBoundaries);
        Direction dir = Utilities.toNearestBoundary(rc, spottedBoundaries);
        if (dir != null) awayFromEnemy = dir;
        else awayFromEnemy = Utilities.randomDirection();
        homeLoc = parentLoc.get();
        targetLoc = homeLoc.translate((int) (5 * Math.random()), (int) (5 * Math.random()));
    }

    @Override
    public BotController run() throws GameActionException {
        if (rc.getRoundNum() > 5 && rc.getRoundNum() - lastEnemySpotted > 5) {
            senseNearbyRobots(this::onNearbyEnemy, this::onNearbyFriendly, this::onNearbyNeutral);
            latticePosition();
        } else {
            System.out.println("SCARED");
            nav.moveTo(rc.getLocation().add(awayFromEnemy));
        }
        return this;
    }

    private void onNearbyEnemy(RobotInfo robotInfo) throws GameActionException {
        if (rc.getLocation().distanceSquaredTo(robotInfo.location) < closest){
            awayFromEnemy = robotInfo.location.directionTo(rc.getLocation());
            closest = rc.getLocation().distanceSquaredTo(robotInfo.location);
            nav.moveTo(rc.getLocation().add(awayFromEnemy));
            lastEnemySpotted = rc.getRoundNum();
            inGrid = false;
            latticeLoc = null;
        }
        if (!flagSet) {
            rc.setFlag(Flags.encodeEnemySpotted(rc.getRoundNum(), robotInfo.location, robotInfo.getType(), false));
            flagSet = true;
        }
    }

    private void onNearbyFriendly(RobotInfo robotInfo) throws GameActionException {
        /*if(rc.canGetFlag(robotInfo.ID)) {
            if(rc.getFlag(robotInfo.ID) == 2) {
                rc.move(robotInfo.location.directionTo(rc.getLocation()));
                inGrid = false;
                lastEnemySpotted = rc.getRoundNum();
                rc.setFlag(2);
                flagSet = true;
            }
        }*/
    }

    private void onNearbyNeutral(RobotInfo robotInfo) throws GameActionException {
    }

    final static int[][] offsets = {{-1,-1},{1,-1},{-1,1},{1,1}};
    static boolean inGrid = false;
    static MapLocation latticeLoc;
    private void latticePosition() throws GameActionException {
        if (!inGrid) {
            if (latticeLoc == null) {
                int closestDist = Integer.MAX_VALUE;
                MapLocation closestBot = null;
                for (RobotInfo robotInfo : rc.senseNearbyRobots()){
                    if (robotInfo.type == RobotType.ENLIGHTENMENT_CENTER || rc.getFlag(robotInfo.ID) == 1){
                        if (robotInfo.location.distanceSquaredTo(rc.getLocation()) < closestDist){
                            closestDist = robotInfo.location.distanceSquaredTo(rc.getLocation());
                            closestBot = robotInfo.location;
                        }
                    }
                }

                if (closestBot != null){
                    for (int[] offset : offsets){
                        MapLocation possible = closestBot.translate(offset[0], offset[1]);
                        if (rc.canSenseLocation(possible) && rc.onTheMap(possible) && !rc.isLocationOccupied(possible)){
                            latticeLoc = possible;
                        }
                    }
                }
            }

            if (latticeLoc == null){
                if (parentLoc.isPresent())
                    nav.fuzzyMove(parentLoc.get().directionTo(rc.getLocation()));
                else
                    nav.fuzzyMove(Utilities.randomDirection());
            }else{
                nav.moveTo(latticeLoc);
                if (rc.getLocation().equals(latticeLoc)){
                    rc.setFlag(1);
                    flagSet = true;
                    inGrid = true;
                }else if (rc.canSenseLocation(latticeLoc)
                        && rc.isLocationOccupied(latticeLoc)
                        && rc.getFlag(rc.senseRobotAtLocation(latticeLoc).ID) == 1){
                    MapLocation original = rc.senseRobotAtLocation(latticeLoc).location;
                    for (int[] offset : offsets){
                        MapLocation possible = original.translate(offset[0], offset[1]);
                        if (rc.canSenseLocation(possible) && rc.onTheMap(possible) && !rc.isLocationOccupied(possible)){
                            latticeLoc = possible;
                        }
                    }
                }
            }
        }
    }

}
