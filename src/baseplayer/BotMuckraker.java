package baseplayer;

import baseplayer.flags.Flags;
import battlecode.common.*;

public class BotMuckraker extends BotController {
    Direction scoutingDirection;
    MapLocation parentLoc;
    public BotMuckraker(RobotController rc) {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        MapLocation myLoc = rc.getLocation();
        //Find where our parent is
        if (age == 0) {
            for (MapLocation neighbor : Utilities.getPossibleNeighbors(myLoc)) {
                RobotInfo robotHere = rc.senseRobotAtLocation(neighbor);
                if (robotHere != null && robotHere.type.equals(RobotType.ENLIGHTENMENT_CENTER)) {
                    parentLoc = neighbor;
                    scoutingDirection = parentLoc.directionTo(myLoc);
                    break;
                }
            }

            assert parentLoc != null;
            assert scoutingDirection != null;
        }
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

        signalForBoundary(Direction.NORTH);

        tryMove(scoutingDirection);
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
            System.out.println("Max offset: " + maxOffset + "Min offset: " + minOffset);
            int newOffset = (maxOffset + minOffset)/2;
            if (maxOffset == minOffset + 1) {
                // Boundary pinpointed! maxOffset is off grid while minOffset is on grid
                MapLocation boundaryLoc = Utilities.offsetLocation(rc.getLocation(), searchDirection, minOffset);
                System.out.println(searchDirection + " boundary found at " + boundaryLoc);
                // Signal here
                rc.setFlag(Flags.encodeBoundarySpotted(boundaryLoc, searchDirection));
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
