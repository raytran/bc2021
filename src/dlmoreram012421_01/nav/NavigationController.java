package dlmoreram012421_01.nav;

import dlmoreram012421_01.MapLocPair;
import dlmoreram012421_01.Utilities;
import dlmoreram012421_01.ds.MapLocationArray;
import dlmoreram012421_01.ds.MinHeap;
import dlmoreram012421_01.nav.bellmanford.Pather;
import battlecode.common.*;

import java.util.*;

public class NavigationController {
    private final RobotController rc;
    private Queue<MapLocation> currentPath;

    //Bug
    static int MAX_BUG_TURNS = 100;
    int bugTurns = 0;
    private NavMode currentMode;
    private Direction heading = Direction.NORTH;
    private BugDirection bugDir = BugDirection.RIGHT;
    private int closestDistAtBugStart = 0;


    public NavigationController(RobotController rc) {
        this.rc = rc;
        Pather.init(rc);
        currentMode = NavMode.DIRECT;
    }

    public void bellmanFordTo(MapLocation target) throws GameActionException {
        Pather.pathTo(target);
    }

    /**
     * Bellman ford if enough bytecode, bug otherwise
     * @param target
     * @throws GameActionException
     */
    public void moveTo(MapLocation target) throws GameActionException {
        if (Clock.getBytecodesLeft() > 6700){
            Pather.pathTo(target);
        }else{
            bugTurn(target);
        }

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
     * Bugs towards a target location, then uses heuristics to reach
     * @param target
     * @return true if target isn't takent, false otherwise
     */
    public boolean bugAndHeuristicTo(MapLocation target) throws GameActionException {
        if (rc.getLocation().distanceSquaredTo(target) > rc.getType().sensorRadiusSquared){
            bugTo(target);
            return true;
        }else{
            return heuristicMoveTo(target);
        }
    }


    /**
     * Bugs if far away, BFS if close enough
     * @param target target location
     * @return true if possible to reach target, false otherwise
     * @throws GameActionException
     */
    public boolean bugAndBFSto(MapLocation target) throws GameActionException {
        if (rc.getLocation().distanceSquaredTo(target) > rc.getType().sensorRadiusSquared){
            bugTo(target);
            return true;
        }else{
            return localPathTo(target, false);
        }
    }

    /**
     * Bugs if far away, dijkstra if close enough
     * @param target target location
     * @return true if possible to reach target, false otherwise
     * @throws GameActionException
     */
    public boolean bugAndDijkstraTo(MapLocation target) throws GameActionException {
        if (rc.getLocation().distanceSquaredTo(target) > rc.getType().sensorRadiusSquared){
            bugTo(target);
            return true;
        }else{
            return localPathTo(target, true);
        }
    }

    /**
     * Dijkstra to a location
     * Only works within sensing range
     * @param target target location
     * @param isDijkstra uses dijkstra if true, bfs otherwise
     * @return true if possible to go to location, false otherwise
     * @throws GameActionException
     */
    public boolean localPathTo(MapLocation target, boolean isDijkstra) throws GameActionException {
        if (!rc.getLocation().equals(target)){
            if (currentPath == null) {
                if (isDijkstra)
                    currentPath = localDijkstra(target);
                else
                    currentPath = localBFS(target);
            }
            if (currentPath.size() > 0) {
                for (MapLocation loc : currentPath) {
                    rc.setIndicatorDot(loc, 0, 255, 0);
                }
                MapLocation nextLoc = currentPath.poll();
                Direction targetDir = rc.getLocation().directionTo(nextLoc);
                if (rc.canMove(targetDir)) {
                    rc.move(targetDir);
                }else{
                    currentPath = null;
                }
                return true;
            } else {
                // Impossible to reach target
                return false;
            }
        }
        return true;
    }

    /**
     * Generate a dijkstra path towards a location
     * @param target location to reach
     * @return path to location, starting at current loc and ending at target
     * @throws GameActionException
     */
    public Queue<MapLocation> localDijkstra(MapLocation target) throws GameActionException {
        int senseRadius = rc.getType().sensorRadiusSquared;
        if (rc.getLocation().distanceSquaredTo(target) > senseRadius) {
            throw new RuntimeException("Tried to pathfind outside of sense radius");
        }

        MapLocation startingLoc = rc.getLocation();

        MapLocationArray<Double> passabilityMap = new MapLocationArray<>(startingLoc, senseRadius);
        MapLocationArray<Double> costMap = new MapLocationArray<>(startingLoc, senseRadius);
        MapLocationArray<MapLocation> parentPointers = new MapLocationArray<>(startingLoc, senseRadius);
        MapLocationArray<Boolean> onTheMap = new MapLocationArray<>(startingLoc, senseRadius);

        MinHeap<MapLocPair> queue = new MinHeap<>(150);

        costMap.put(startingLoc, 0.0);
        queue.add(new MapLocPair(startingLoc, 0.0));

        while (queue.size() > 0) {
            MapLocPair current = queue.poll();
            MapLocation currentLoc = current.location;
            if (currentLoc.equals(target)) {
                break;
            }

            double currentCost = current.value;
            for (MapLocation neighborLoc :  Utilities.getPossibleNeighbors(current.location)) {
                if (!onTheMap.containsKey(neighborLoc)) {
                    if (neighborLoc.isWithinDistanceSquared(startingLoc, senseRadius)){
                        onTheMap.put(neighborLoc, rc.onTheMap(neighborLoc));
                    } else {
                        onTheMap.put(neighborLoc, false);
                    }
                }
                if (onTheMap.get(neighborLoc)){
                    if (!passabilityMap.containsKey(neighborLoc)) {
                        passabilityMap.put(neighborLoc, rc.isLocationOccupied(neighborLoc) ? 0 : rc.sensePassability(neighborLoc));
                    }

                    double newCost = currentCost + (1 - passabilityMap.get(neighborLoc));
                    if (!costMap.containsKey(neighborLoc) || newCost < costMap.get(neighborLoc)) {
                        costMap.put(neighborLoc, newCost);
                        queue.add(new MapLocPair(neighborLoc, newCost));
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
        return new LinkedList<>(path);
    }

    public Queue<MapLocation> localBFS(MapLocation target) throws GameActionException {
        MapLocation startingLoc = rc.getLocation();
        Queue<MapLocation> queue = new LinkedList<>();
        int senseRadius = rc.getType().sensorRadiusSquared;
        MapLocationArray<Boolean> visited = new MapLocationArray<>(startingLoc, senseRadius);
        MapLocationArray<MapLocation> parentPointers = new MapLocationArray<>(startingLoc, senseRadius);
        queue.add(startingLoc);

        while (queue.size() > 0) {
            MapLocation current = queue.poll();

            rc.setIndicatorDot(current, 255, 0, 0);
            if (current.equals(target)) {
                break;
            }
            for (MapLocation neighbor : Utilities.getPossibleNeighbors(current)) {
                if (neighbor.isWithinDistanceSquared(startingLoc, senseRadius)) {
                    if (rc.onTheMap(neighbor) && !rc.isLocationOccupied(neighbor) && !visited.containsKey(neighbor)) {
                        queue.add(neighbor);
                        parentPointers.put(neighbor, current);
                        visited.put(neighbor, true);
                    }
                }
            }
        }

        List<MapLocation> path = new LinkedList<>();
        MapLocation current = target;
        while (parentPointers.containsKey(current)) {
            path.add(current);
            current = parentPointers.get(current);
        }

        Collections.reverse(path);
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
        bugTurns += 1;
        if (bugTurnIntoEdge()){
            //System.out.println("Bug dir into edge");
            reverseBugDir();
        }

        for (int i = 0; i < 8; i ++) {
            if (!canMoveInDir(heading)) {
                if (bugDir == BugDirection.RIGHT)
                    heading = heading.rotateRight();
                else
                    heading = heading.rotateLeft();
            } else {
                break;
            }
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
            if (rc.getLocation().distanceSquaredTo(target) < closestDistAtBugStart || bugTurns > MAX_BUG_TURNS) {
                currentMode = NavMode.DIRECT;
                bugTurns = 0;
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
     * If not possible, tries to move in similar direction (+/- 2 cardinal)
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
            if (rc.canMove(dir.rotateLeft().rotateLeft())){
                rc.move(dir.rotateLeft().rotateLeft());
                return true;
            }
            if (rc.canMove(dir.rotateRight().rotateRight())){
                rc.move(dir.rotateRight().rotateRight());
                return true;
            }
        }
        return false;
    }

    /**
     * Try to move away from any nearby robots.
     * If there are no nearby robots, move in defaultDir
     * @param defaultDir direction to move in if there are no nearby bots
     * @throws GameActionException
     */
    public void spreadOut(Direction defaultDir) throws GameActionException {
        RobotInfo closestBot = null;
        MapLocation currentLoc = rc.getLocation();
        int closestFriendlyDist = Integer.MAX_VALUE;
        for (RobotInfo ri : rc.senseNearbyRobots()) {
            int currentDist = ri.location.distanceSquaredTo(currentLoc);
            if (closestBot == null ||  currentDist < closestFriendlyDist) {
                closestFriendlyDist = currentDist;
                closestBot = ri;
            }
        }

        if (closestBot != null) {
            fuzzyMove(closestBot.location.directionTo(currentLoc));
        } else {
            fuzzyMove(defaultDir);
        }

    }


    /**
     * Tries to move to a location using heuristic
     * Heuristic: min(distance + passability) of the tiles that are within +/- 2 turns
     * @return true if possible to move (not taken) false otherwise
     */
    public boolean heuristicMoveTo(MapLocation target) throws GameActionException {
        MapLocation currentLoc = rc.getLocation();
        if (currentLoc.isWithinDistanceSquared(target, rc.getType().sensorRadiusSquared)) {
           if (rc.isLocationOccupied(target)){
               return false;
           }
        }
        int xMin = currentLoc.x - 1;
        int yMin = currentLoc.y - 1;

        int[] scores = new int[8];
        for (int i = 0; i < scores.length; i++) {
            int x = i % 3 + xMin;
            int y = i / 3 + yMin;
            MapLocation current = new MapLocation(x, y);
            if (rc.onTheMap(current) && !rc.isLocationOccupied(current)){
                int score = current.distanceSquaredTo(target) + (int) ((1 - rc.sensePassability(current)) * 100);
                int minNeighbor = Integer.MAX_VALUE;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy =-1; dy <= 1; dy++) {
                        if (!(dx == 0 && dy == 0)) {
                            int neighborX = x + dx;
                            int neighborY = y + dy;
                            MapLocation neighbor = new MapLocation(neighborX, neighborY);
                            if (rc.onTheMap(neighbor) && !rc.isLocationOccupied(neighbor)) {
                                minNeighbor = Math.min(minNeighbor,
                                        neighbor.distanceSquaredTo(target) + (int) ((1 - rc.sensePassability(neighbor)) * 100));
                            }
                        }
                    }
                }
                scores[i] = score + (minNeighbor == Integer.MAX_VALUE ? 0 : minNeighbor);
            } else {
                scores[i] = Integer.MAX_VALUE;
            }
        }

        MapLocation bestTile = null;
        int bestNearbyScore = Integer.MAX_VALUE;
        for (int i = 0; i < scores.length; i++) {
            int x = i % 3 + xMin;
            int y = i / 3 + yMin;

            if (scores[i] < bestNearbyScore) {
                bestNearbyScore = scores[i];
                bestTile = new MapLocation(x, y);
            }
        }
        if (bestTile != null){
            Direction bestDir = currentLoc.directionTo(bestTile);
            if (rc.canMove(bestDir)){
                rc.move(bestDir);
            }
        }
        return true;
    }


    /**
     * Extremely simple heuristic: find squares that get you closer and choose the one with the best passability
     * @param target location
     * @throws GameActionException
     */
    public void easyNav(MapLocation target) throws GameActionException {
        int[][] deltas = {{0, 1},{1, 1},{1, 0},{1, -1},{0, -1},{-1, -1},{-1, 0},{-1, 1}};
        MapLocation currentLoc = rc.getLocation();
        int initDist = currentLoc.distanceSquaredTo(target);

        MapLocation oneStepTarget = null;
        MapLocation fallback = null;
        double maxPassability = 0;
        for (int[] delta : deltas) {
            MapLocation candidate = currentLoc.translate(delta[0], delta[1]);
            if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate)) {
                double candidatePassability = rc.sensePassability(candidate);
                double candidateDist = candidate.distanceSquaredTo(target);
                if (candidateDist < initDist && candidatePassability > maxPassability){
                    maxPassability = candidatePassability;
                    oneStepTarget = candidate;
                }
            }
        }

        if (oneStepTarget != null){
            currentMode = NavMode.DIRECT;
            if (rc.canMove(currentLoc.directionTo(oneStepTarget))){
                rc.move(currentLoc.directionTo(oneStepTarget));
            }
        }else{
            bugTo(target);
        }
    }


    /**
     * Moves in a vortex around a given center
     * @throws GameActionException
     */
    public void vortexMove(MapLocation center) throws GameActionException {
        if (rc.getLocation().distanceSquaredTo(center) < 5) {
            if (rc.canMove(center.directionTo(rc.getLocation()))){
                rc.move(center.directionTo(rc.getLocation()));
            }
        } else {
            Direction perpendicular = center.directionTo(rc.getLocation());
            MapLocation newLoc = rc.getLocation().add(perpendicular.rotateRight().rotateRight());
            /*
            if (newLoc.distanceSquaredTo(parentLoc) > 25){
                newLoc = newLoc.add(newLoc.directionTo(parentLoc));
            }

             */
            fuzzyMove(rc.getLocation().directionTo(newLoc));
        }

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
