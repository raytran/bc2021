package baseplayer;

import baseplayer.ds.CircularLinkedList;
import baseplayer.flags.BoundaryType;
import baseplayer.flags.FlagAddress;
import baseplayer.flags.Flags;
import baseplayer.nav.NavigationController;
import battlecode.common.*;

import java.util.*;

public abstract class BotController {
    Optional<Integer> parentID = Optional.empty();
    MapLocation parentLoc;
    Optional<Integer> northBoundary;
    Optional<Integer> eastBoundary;
    Optional<Integer> southBoundary;
    Optional<Integer> westBoundary;
    CircularLinkedList<Map.Entry<BoundaryType, Integer>> boundariesToFlag = new CircularLinkedList<>();
    RobotController rc;
    NavigationController nav;

    int age;

    public BotController(RobotController rc) throws GameActionException {
        age = 0;
        this.rc = rc;
        this.nav = new NavigationController(rc);
        northBoundary = Optional.empty();
        eastBoundary = Optional.empty();
        southBoundary = Optional.empty();
        westBoundary = Optional.empty();
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

    void searchForNearbyBoundaries() throws GameActionException {
        List<BoundaryType> boundaryDirectionsToSearch = new LinkedList<>();
        if (!northBoundary.isPresent()) boundaryDirectionsToSearch.add(BoundaryType.NORTH);
        if (!eastBoundary.isPresent()) boundaryDirectionsToSearch.add(BoundaryType.EAST);
        if (!southBoundary.isPresent()) boundaryDirectionsToSearch.add(BoundaryType.SOUTH);
        if (!westBoundary.isPresent()) boundaryDirectionsToSearch.add(BoundaryType.WEST);

        for (BoundaryType boundariesNeeded : boundaryDirectionsToSearch) {
            Optional<Integer> boundary = searchForBoundary(boundariesNeeded);
            if (boundary.isPresent()) {
                switch (boundariesNeeded) {
                    case NORTH:
                        reportNorthBoundary(boundary.get());
                        break;
                    case SOUTH:
                        reportSouthBoundary(boundary.get());
                        break;
                    case EAST:
                        reportEastBoundary(boundary.get());
                        break;
                    case WEST:
                        reportWestBoundary(boundary.get());
                        break;
                }
            }
        }
    }

    void flagBoundaries() throws GameActionException {
        if (boundariesToFlag.getSize() > 0) {
            Map.Entry<BoundaryType, Integer> boundary = boundariesToFlag.sampleWithMemory(1).get(0);
            int mapFlag = Flags.encodeBoundarySpotted(FlagAddress.ANY, boundary.getValue(), boundary.getKey());
            if (rc.canSetFlag(mapFlag)) {
                rc.setFlag(mapFlag);
            }
        }
    }

    /** Binary search along the sensor radius to find boundary, if any
     * @param requestedBoundary boundary type to search for
     * @return (maybe) boundary
     * @throws GameActionException
     */
    Optional<Integer> searchForBoundary(BoundaryType requestedBoundary) throws GameActionException {
        Direction searchDirection = BoundaryType.toDirection(requestedBoundary);
        int maxOffset = (int) Math.sqrt(rc.getType().sensorRadiusSquared);
        int minOffset = 0;
        MapLocation extreme = Utilities.offsetLocation(rc.getLocation(), searchDirection, maxOffset);

        // Nothing to do if the extreme didn't find anything...
        if (rc.onTheMap(extreme)) {
            return Optional.empty();
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
                if (searchDirection == Direction.NORTH || searchDirection == Direction.SOUTH) {
                    return Optional.of(boundaryLoc.y);
                } else {
                    return Optional.of(boundaryLoc.x);
                }
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

    /**
     * Report the discovery of the north boundary
     * @param boundary exact location
     */
    public void reportNorthBoundary(int boundary) {
        if (!northBoundary.isPresent()){
            northBoundary = Optional.of(boundary);
            boundariesToFlag.addToTail(new AbstractMap.SimpleImmutableEntry<>(BoundaryType.NORTH, northBoundary.get()));
        }
    }

    /**
     * Report the discovery of the east boundary
     * @param boundary exact location
     */
    public void reportEastBoundary(int boundary) {
        if (!eastBoundary.isPresent()){
            eastBoundary = Optional.of(boundary);
            boundariesToFlag.addToTail(new AbstractMap.SimpleImmutableEntry<>(BoundaryType.EAST, eastBoundary.get()));
        }
    }

    /**
     * Report the discovery of the south boundary
     * @param boundary exact location
     */
    public void reportSouthBoundary(int boundary) {
        if (!southBoundary.isPresent()){
            southBoundary = Optional.of(boundary);
            boundariesToFlag.addToTail(new AbstractMap.SimpleImmutableEntry<>(BoundaryType.SOUTH, southBoundary.get()));
        }
    }

    /**
     * Report the discovery of the west boundary
     * @param boundary exact location
     */
    public void reportWestBoundary(int boundary) {
        if (!westBoundary.isPresent()){
            westBoundary = Optional.of(boundary);
            boundariesToFlag.addToTail(new AbstractMap.SimpleImmutableEntry<>(BoundaryType.WEST, westBoundary.get()));
        }
    }

    /**
     * @return true if north found
     */
    public boolean isNorthBoundaryFound() {
        return northBoundary.isPresent();
    }

    /**
     * @return true if south found
     */
    public boolean isSouthBoundaryFound() {
        return southBoundary.isPresent();
    }


    /**
     * @return true if east found
     */
    public boolean isEastBoundaryFound() {
        return eastBoundary.isPresent();
    }

    /**
     * @return true if west found
     */
    public boolean isWestBoundaryFound() {
        return westBoundary.isPresent();
    }

    /**
     * @return true if all boundaries are found
     */
    public boolean areAllBoundariesFound() {
        return isNorthBoundaryFound()
                || isSouthBoundaryFound()
                || isWestBoundaryFound()
                || isEastBoundaryFound();
    }
}
