package votepercent.nav;

import votepercent.Utilities;
import battlecode.common.*;

import java.util.*;

public class NavigationController {
    private final RobotController rc;

    //Bug
    private NavMode currentMode;
    private Direction heading = Direction.NORTH;
    private BugDirection bugDir = BugDirection.RIGHT;
    private int closestDistAtBugStart = 0;


    // Local Dijkstra
    private Queue<MapLocation> currentPath = new LinkedList<>();
    private MapLocation lastTarget;


    public NavigationController(RobotController rc) {
        this.rc = rc;
        currentMode = NavMode.DIRECT;
    }

    /**
     * Bug move towards a target location
     * @param target location to reach
     * @throws GameActionException
     */
    public void bugTo(MapLocation target) throws GameActionException {
        // Try bug0
        // move in direction
        // if we see a wall, move along wall until we can move directly again
        switch(currentMode) {
            case DIRECT:
                directTurn(target);
                break;
            case BUGGING:
                bugTurn(target);
                break;
        }
    }

    /**
     * Dijkstra to a location
     * Only works within sensing range
     * @param target target location
     * @throws GameActionException
     */
    public void localDijkstraTo(MapLocation target) throws GameActionException {
        if (lastTarget == null || !lastTarget.equals(target)) {
            lastTarget = target;
            currentPath = localDijkstra(target);

            if (currentPath != null) {
                for (MapLocation loc : currentPath) {
                    rc.setIndicatorDot(loc, 0, 255, 0);
                }
            }
        }
        if (currentPath != null && currentPath.size() > 0) {
            MapLocation nextLoc = currentPath.peek();
            Direction targetDir = rc.getLocation().directionTo(nextLoc);
            if (rc.canMove(targetDir)) {
                if (fuzzyMove(targetDir)){
                   currentPath.poll();
                }
            }
        }
    }

    /**
     * Generate a dijkstra path towards a location
     * @param target location to reach
     * @return path to location, starting at current loc and ending at target
     * @throws GameActionException
     */
    public Queue<MapLocation> localDijkstra(MapLocation target) throws GameActionException {
        int initBytecode = Clock.getBytecodesLeft();
        int senseRadius = rc.getType().sensorRadiusSquared;
        if (rc.getLocation().distanceSquaredTo(target) > senseRadius) {
            //System.err.println("Tried to pathfind outside of sense radius");
            return null;
        }

        Map<MapLocation, Double> passabilityMap = new HashMap<>();
        Map<MapLocation, Double> costMap = new HashMap<>();
        Map<MapLocation, MapLocation> parentPointers = new HashMap<>();

        MapLocation startingLoc = rc.getLocation();
        PriorityQueue<Map.Entry<Double, MapLocation>> queue = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getKey));

        costMap.put(startingLoc, 0.0);
        queue.add(new AbstractMap.SimpleImmutableEntry<>(0.0, startingLoc));

        while (queue.size() > 0) {
            Map.Entry<Double, MapLocation> current = queue.poll();
            MapLocation currentLoc = current.getValue();

            if (currentLoc.equals(target)) {
                break;
            }

            double currentCost = current.getKey();
            for (MapLocation neighborLoc :  Utilities.getPossibleNeighbors(current.getValue())) {
                if (neighborLoc.isWithinDistanceSquared(startingLoc, senseRadius) && rc.onTheMap(neighborLoc)){
                    if (!passabilityMap.containsKey(neighborLoc)) {
                        passabilityMap.put(neighborLoc, rc.sensePassability(neighborLoc));
                    }
                    double newCost = currentCost + (1 - passabilityMap.get(neighborLoc));
                    if (!costMap.containsKey(neighborLoc) || newCost < costMap.get(neighborLoc)) {
                        costMap.put(neighborLoc, newCost);
                        queue.add(new AbstractMap.SimpleImmutableEntry<>(newCost, neighborLoc));
                        parentPointers.put(neighborLoc, currentLoc);
                    }
                }
            }
        }

        // Rebuild path from parent pointers
        List<MapLocation> path = new LinkedList<>();
        MapLocation current = target;
        while (parentPointers.containsKey(current)) {
            path.add(current);
            current = parentPointers.get(current);
        }
        Collections.reverse(path);

        System.out.println("TOOK " + (initBytecode - Clock.getBytecodesLeft()) + " bytecode for " + path);

        return new LinkedList<>(path);
    }


    private void directTurn(MapLocation target) throws GameActionException {
        heading = rc.getLocation().directionTo(target);
        // normal movement
        if (canMoveInDir(heading)) {
            if (rc.canMove(heading)) {
                rc.move(heading);
            }
        } else {
            // Switch to bugging
            closestDistAtBugStart = rc.getLocation().distanceSquaredTo(target);
            currentMode = NavMode.BUGGING;
        }
    }

    private void bugTurn(MapLocation target) throws GameActionException {
        if (bugTurnIntoEdge()){
            //System.out.println("Bug dir into edge");
            reverseBugDir();
        }

        while (!canMoveInDir(heading)) {
            if (bugDir == BugDirection.RIGHT)
                heading = heading.rotateRight();
            else
                heading = heading.rotateLeft();
        }
        // Now we can move forward, but should we?
        if (bugDir == BugDirection.RIGHT) {
            if (canMoveInDir(heading.rotateLeft())) {
                heading = heading.rotateLeft();
            }
        } else {
            if (canMoveInDir(heading.rotateRight())) {
                heading = heading.rotateRight();
            }
        }

        if (rc.canMove(heading)){
            rc.move(heading);
            if (rc.getLocation().distanceSquaredTo(target) < closestDistAtBugStart) {
                currentMode = NavMode.DIRECT;
            }
        }
    }

    private boolean bugTurnIntoEdge() throws GameActionException {
        if (canMoveInDir(heading)) return false;
        if (Utilities.isDiagonal(heading)) {
            if (bugDir == BugDirection.LEFT) {
                return !canMoveInDir(heading.rotateLeft());
            } else {
                return !canMoveInDir(heading.rotateRight());
            }
        } else {
            return false;
        }
    }

    private void reverseBugDir() {
        if (bugDir == BugDirection.LEFT)
            bugDir = BugDirection.RIGHT;
        else
            bugDir = BugDirection.LEFT;
    }




    /**
     * Attempts to move in a given direction.
     * If not possible, tries to move in similar direction (+/- 1 cardinal)
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    public boolean fuzzyMove(Direction dir) throws GameActionException {
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else {
            if (rc.canMove(dir.rotateLeft())){
                rc.move(dir.rotateLeft());
                return true;
            }
            if (rc.canMove(dir.rotateRight())){
                rc.move(dir.rotateRight());
                return true;
            }
        }
        return false;
    }

    private double getMovementSlope(MapLocation target) {
        return Utilities.getSlope(target, rc.getLocation());
    }

    //Checks to see if we can move in desiredDirection
    private boolean canMoveInDir(Direction desiredDirection) throws GameActionException {
        MapLocation loc = rc.adjacentLocation(desiredDirection);
        return rc.onTheMap(loc) && !rc.isLocationOccupied(loc) && rc.sensePassability(loc) > 0.2;
    }
}
