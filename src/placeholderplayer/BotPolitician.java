package placeholderplayer;

import battlecode.common.*;
import placeholderplayer.flags.FlagType;
import placeholderplayer.flags.Flags;
import placeholderplayer.flags.RobotRole;

public class BotPolitician extends BotControllerWalking {
    RobotRole role = RobotRole.OFFENSE;
    public BotPolitician(RobotController rc) throws GameActionException {
        super(rc);
        // Not a conversion
        if (parentInfoAtSpawn != null) {
            System.out.println("PARENT IS " + parentInfoAtSpawn.ID);
            if (rc.canGetFlag(parentInfoAtSpawn.ID)){
                int parentFlag = rc.getFlag(parentInfoAtSpawn.ID);
                if (Flags.decodeFlagType(parentFlag) == FlagType.ROBOT_ROLE)
                    role = Flags.decodeRobotRole(parentFlag);
            }
        }
    }

    @Override
    public BotController run() throws GameActionException {
        switch (role){
            case OFFENSE:
                runOffense();
                break;
            case DEFENSE:
                runDefense();
                break;
            case SCOUT:
                runScout();
                break;
            default:
                throw new RuntimeException("Not a real role...");
        }
        return this;
    }

    private void runOffense() throws GameActionException {
        nav.fuzzyMove(Utilities.randomDirection());
    }

    private void runDefense() throws GameActionException {
        MapLocation muckrakerLoc = null;
        for (RobotInfo ri : rc.senseNearbyRobots(RobotType.POLITICIAN.sensorRadiusSquared, enemy)){
            if (ri.type == RobotType.MUCKRAKER){
                muckrakerLoc = ri.location;
                if (rc.getLocation().distanceSquaredTo(muckrakerLoc) < RobotType.POLITICIAN.actionRadiusSquared
                        && rc.canEmpower(RobotType.POLITICIAN.actionRadiusSquared)){
                    rc.empower(RobotType.POLITICIAN.actionRadiusSquared);
                }
            }
        }
        if (muckrakerLoc != null){
            nav.moveTo(muckrakerLoc);
        } else {
            latticeDefense();
        }
    }

    private void runScout() throws GameActionException {
        Direction targetDir = parentInfoAtSpawn.location.directionTo(rc.getLocation());
        nav.fuzzyMove(targetDir);
    }

    final static int[][] offsets = {{2,4},{3,1},{4,-2},{1,-3},{-2,-4},{-3,-1},{-4,2},{-1,3}};
    static boolean inGrid = false;
    static MapLocation latticeLoc;
    private void latticeDefense() throws GameActionException {
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
                if (parentInfoAtSpawn != null)
                    nav.fuzzyMove(parentInfoAtSpawn.location.directionTo(rc.getLocation()));
                else
                    nav.fuzzyMove(baseplayer.Utilities.randomDirection());
            }else{
                nav.moveTo(latticeLoc);
                if (rc.getLocation().equals(latticeLoc)){
                    rc.setFlag(1);
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

