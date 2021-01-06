package baseplayer.nav;

import baseplayer.Utilities;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class NavigationController {
    private final RobotController rc;
    private NavMode currentMode;
    private Direction heading = Direction.NORTH;
    private BugDirection bugDir = BugDirection.RIGHT;
    private int closestDistAtBugStart = 0;
    public NavigationController(RobotController rc) {
        this.rc = rc;
        currentMode = NavMode.DIRECT;
    }

    public void moveTo(MapLocation target) throws GameActionException {
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
            System.out.println("Bug dir into edge");
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
