package baseplayer;

import baseplayer.flags.*;
import battlecode.common.*;

import java.util.ArrayList;
import java.util.List;

public class BotMuckraker extends BotController {
    Direction scoutingDirection;
    MapLocation parentLoc;
    int parentID;
    public BotMuckraker(RobotController rc) throws GameActionException {
        super(rc);
        MapLocation myLoc = rc.getLocation();
        for (MapLocation neighbor : Utilities.getPossibleNeighbors(myLoc)) {
            RobotInfo robotHere = rc.senseRobotAtLocation(neighbor);
            if (robotHere != null && robotHere.type.equals(RobotType.ENLIGHTENMENT_CENTER)) {
                parentLoc = neighbor;
                scoutingDirection = parentLoc.directionTo(myLoc);
                parentID = robotHere.ID;
                break;
            }
        }

        assert parentLoc != null;
        assert scoutingDirection != null;
    }

    @Override
    public void run() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        // Found a robot ?
        for (RobotInfo robot : rc.senseNearbyRobots()) {
            if (robot.team.equals(enemy)){
                MapLocation robotLoc = robot.getLocation();
                MapLocation delta = new MapLocation(robotLoc.x - parentLoc.x, robotLoc.y - parentLoc.y);
                rc.setFlag(Flags.encodeEnemySpotted(FlagAddress.PARENT_ENLIGHTENMENT_CENTER, delta, robot.getType()));
                return;
            }
        }

        int parentFlag = rc.getFlag(parentID);
        FlagType parentFlagType = Flags.decodeFlagType(parentFlag);
        if (parentFlagType.equals(FlagType.BOUNDARY_REQUIRED)) {
            BoundaryRequiredInfo info = Flags.decodeBoundaryRequired(parentFlag);
            List<BoundaryType> boundaryDirectionsToSearch = new ArrayList<>();
            if (!info.northFound) boundaryDirectionsToSearch.add(BoundaryType.NORTH);
            if (!info.eastFound) boundaryDirectionsToSearch.add(BoundaryType.EAST);
            if (!info.southFound) boundaryDirectionsToSearch.add(BoundaryType.SOUTH);
            if (!info.westFound) boundaryDirectionsToSearch.add(BoundaryType.WEST);

            for (BoundaryType searchBoundaryDirection : boundaryDirectionsToSearch)
                signalForBoundary(searchBoundaryDirection);
        }

        nav.fuzzyMove(scoutingDirection);
    }


    // Binary search along the sensor radius to find boundary, if any
    // Sets boundary flag in direction if found.
    private void signalForBoundary(BoundaryType requestedBoundary) throws GameActionException {
        Direction searchDirection = BoundaryType.toDirection(requestedBoundary);
        int maxOffset = (int) Math.sqrt(rc.getType().sensorRadiusSquared);
        int minOffset = 0;
        MapLocation extreme = Utilities.offsetLocation(rc.getLocation(), searchDirection, maxOffset);

        // Nothing to do if the extreme didn't find anything...
        if (rc.onTheMap(extreme)) {
            return;
        }

        // We found something that's not on the map!
        // Start a binary search to pinpoint the location
        // We shouldn't need to loop more than sense radius times (if we do, something is very wrong)
        int steps = 0;
        for (int n = 0; n < Math.sqrt(rc.getType().sensorRadiusSquared); n++) {
            // Throughout the search, maintain that maxOffset is off the map while minOffset is on the map
            //System.out.println("Max offset: " + maxOffset + "Min offset: " + minOffset);
            int newOffset = (maxOffset + minOffset)/2;
            if (maxOffset == minOffset + 1) {
                // Boundary pinpointed! maxOffset is off grid while minOffset is on grid
                MapLocation boundaryLoc = Utilities.offsetLocation(rc.getLocation(), searchDirection, minOffset);
                if (searchDirection == Direction.NORTH || searchDirection == Direction.SOUTH){
                    rc.setFlag(Flags.encodeBoundarySpotted(FlagAddress.PARENT_ENLIGHTENMENT_CENTER, boundaryLoc.y, requestedBoundary));
                }else{
                    rc.setFlag(Flags.encodeBoundarySpotted(FlagAddress.PARENT_ENLIGHTENMENT_CENTER, boundaryLoc.x, requestedBoundary));
                }
                return;
            }
            MapLocation newLoc = Utilities.offsetLocation(rc.getLocation(), searchDirection, newOffset);
            if (rc.onTheMap(newLoc)) {
                minOffset = newOffset;
            } else {
                maxOffset = newOffset;
            }
            steps += 1;
        }

        System.err.println("Too many steps: " + steps + " steps.");
        throw new RuntimeException("Binary search too many times! Check code.");
    }
}
