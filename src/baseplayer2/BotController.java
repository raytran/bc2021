package baseplayer2;

import baseplayer2.ds.CircularLinkedList;
import baseplayer2.flags.BoundaryType;
import baseplayer2.flags.EnemySpottedInfo;
import baseplayer2.flags.Flags;
import baseplayer2.nav.NavigationController;
import battlecode.common.*;

import java.util.*;

public abstract class BotController {
    private final List<EnemySpottedInfo> reportedEnemies = new LinkedList<>();
    Optional<Integer> parentID = Optional.empty();
    Optional<MapLocation> parentLoc = Optional.empty();
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
                        parentLoc = Optional.of(neighbor);
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
                        recordNorthBoundary(boundary.get());
                        break;
                    case SOUTH:
                        recordSouthBoundary(boundary.get());
                        break;
                    case EAST:
                        recordEastBoundary(boundary.get());
                        break;
                    case WEST:
                        recordWestBoundary(boundary.get());
                        break;
                }
            }
        }
    }

    void flagBoundaries() throws GameActionException {
        if (boundariesToFlag.getSize() > 0) {
            Map.Entry<BoundaryType, Integer> boundary = boundariesToFlag.sampleWithMemory(1).get(0);
            int mapFlag = Flags.encodeBoundarySpotted(rc.getRoundNum(), boundary.getValue(), boundary.getKey());
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
            ////System.out.println("Max offset: " + maxOffset + "Min offset: " + minOffset);
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
     * Record the discovery of the north boundary
     * @param boundary exact location
     */
    public void recordNorthBoundary(int boundary) {
        if (!northBoundary.isPresent()){
            northBoundary = Optional.of(boundary);
            boundariesToFlag.addToTail(new AbstractMap.SimpleImmutableEntry<>(BoundaryType.NORTH, northBoundary.get()));
        }
    }

    /**
     * Record the discovery of the east boundary
     * @param boundary exact location
     */
    public void recordEastBoundary(int boundary) {
        if (!eastBoundary.isPresent()){
            eastBoundary = Optional.of(boundary);
            boundariesToFlag.addToTail(new AbstractMap.SimpleImmutableEntry<>(BoundaryType.EAST, eastBoundary.get()));
        }
    }

    /**
     * Report the discovery of the south boundary
     * @param boundary exact location
     */
    public void recordSouthBoundary(int boundary) {
        if (!southBoundary.isPresent()){
            southBoundary = Optional.of(boundary);
            boundariesToFlag.addToTail(new AbstractMap.SimpleImmutableEntry<>(BoundaryType.SOUTH, southBoundary.get()));
        }
    }

    /**
     * Report the discovery of the west boundary
     * @param boundary exact location
     */
    public void recordWestBoundary(int boundary) {
        if (!westBoundary.isPresent()){
            westBoundary = Optional.of(boundary);
            boundariesToFlag.addToTail(new AbstractMap.SimpleImmutableEntry<>(BoundaryType.WEST, westBoundary.get()));
        }
    }

    /**
     * @return north
     */
    public Optional<Integer> getNorthBoundary() {
        return northBoundary;
    }

    /**
     * @return south
     */
    public Optional<Integer> getSouthBoundary() {
        return southBoundary;
    }


    /**
     * @return east
     */
    public Optional<Integer> getEastBoundary() {
        return eastBoundary;
    }

    /**
     * @return west
     */
    public Optional<Integer> getWestBoundary() {
        return westBoundary;
    }

    /**
     * @return true if all boundaries are found
     */
    public boolean areAllBoundariesFound() {
        return getNorthBoundary().isPresent()
                && getSouthBoundary().isPresent()
                && getEastBoundary().isPresent()
                && getWestBoundary().isPresent();
    }

    /**
     * Record the death of an enemy
     * @param enemySpottedInfo the spotting info for the enemy
     */
    public void recordEnemy(EnemySpottedInfo enemySpottedInfo) {
        reportedEnemies.add(enemySpottedInfo);
    }

    /**
     * Get the latest recorded location of the enemy
     */
    public Optional<EnemySpottedInfo> getLatestRecordedEnemyLocation(){
        if (reportedEnemies.size() > 0){
            return Optional.of(reportedEnemies.get(reportedEnemies.size() - 1));
        }else{
            return Optional.empty();
        }
    }

    /**
     * Sense nearby robots
     * Calls the callback functions
     */
    public void senseNearbyRobots(SenseCallback onEnemy, SenseCallback onFriendly, SenseCallback onNeutral) throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        for (RobotInfo robotInfo : rc.senseNearbyRobots()){
            if (robotInfo.team.equals(Team.NEUTRAL)){
                //System.out.println("NEUTRAL HERE");
                onNeutral.run(robotInfo);
            } else if (robotInfo.team.equals(enemy)){
                onEnemy.run(robotInfo);
            } else {
                onFriendly.run(robotInfo);
            }
        }
    }
}
