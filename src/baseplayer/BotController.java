package baseplayer;

import baseplayer.flags.BoundaryType;
import baseplayer.flags.FlagAddress;
import baseplayer.flags.Flags;
import baseplayer.nav.NavigationController;
import battlecode.common.*;

import java.util.Optional;

public abstract class BotController {
    Optional<Integer> parentID = Optional.empty();
    MapLocation parentLoc;
    RobotController rc;
    NavigationController nav;
    int age;

    public BotController(RobotController rc) throws GameActionException {
        age = 0;
        this.rc = rc;
        this.nav = new NavigationController(rc);
        if (rc.getType() != RobotType.ENLIGHTENMENT_CENTER){
            MapLocation myLoc = rc.getLocation();
            for (MapLocation neighbor : Utilities.getPossibleNeighbors(myLoc)) {
                if (rc.onTheMap(neighbor)){
                    RobotInfo robotHere = rc.senseRobotAtLocation(neighbor);
                    if (robotHere != null && robotHere.type.equals(RobotType.ENLIGHTENMENT_CENTER)) {
                        parentLoc = neighbor;
                        parentID = Optional.of(robotHere.ID);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Runs the bot. When the function ends, Clock.yield() is called from the main thread
     * Must return this or a new bot for the next round (used to convert slanderers)
     *
     * @throws GameActionException
     */
    public abstract BotController run() throws GameActionException;

    /**
     * Increases the recorded age of the robot by 1
     */
    public void incrementAge(){
        age += 1;
    }

    // Binary search along the sensor radius to find boundary, if any
    // Sets boundary flag in direction if found.
    void signalForBoundary(BoundaryType requestedBoundary) throws GameActionException {
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
