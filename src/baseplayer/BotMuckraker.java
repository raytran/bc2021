package baseplayer;

import baseplayer.flags.BoundaryRequiredInfo;
import baseplayer.flags.FlagType;
import baseplayer.flags.Flags;
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
                rc.setFlag(Flags.encodeEnemySpotted(delta, robot.getType()));
                return;
            }
        }

        int parentFlag = rc.getFlag(parentID);
        FlagType parentFlagType = Flags.decodeFlagType(parentFlag);
        if (parentFlagType.equals(FlagType.BOUNDARY_REQUIRED)) {
            BoundaryRequiredInfo info = Flags.decodeBoundaryRequired(parentFlag);
            List<Direction> boundaryDirectionsToSearch = new ArrayList<>();
            if (!info.northFound) boundaryDirectionsToSearch.add(Direction.NORTH);
            if (!info.eastFound) boundaryDirectionsToSearch.add(Direction.EAST);
            if (!info.southFound) boundaryDirectionsToSearch.add(Direction.SOUTH);
            if (!info.westFound) boundaryDirectionsToSearch.add(Direction.WEST);

            for (Direction searchBoundaryDirection : boundaryDirectionsToSearch)
                signalForBoundary(searchBoundaryDirection);
        }


        //tryMove(scoutingDirection);
        moveTo(new MapLocation(1, 29));
    }


    // Binary search along the sensor radius to find boundary, if any
    // Sets boundary flag in direction if found.
    private void signalForBoundary(Direction searchDirection) throws GameActionException {
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
                MapLocation boundaryDelta = new MapLocation(boundaryLoc.x - parentLoc.x, boundaryLoc.y - parentLoc.y);
                //System.out.println(searchDirection + " boundary found at " + boundaryLoc);
                // Signal here
                rc.setFlag(Flags.encodeBoundarySpotted(boundaryDelta, searchDirection));
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
