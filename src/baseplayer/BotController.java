package baseplayer;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import java.util.Optional;

public abstract class BotController {
    int age;
    RobotController rc;
    int movementState = 0;
    Direction heading = Direction.NORTH;
    int closest = 0;
    double movementSlope = 0;


    public BotController(RobotController rc) {
        age = 0;
        this.rc = rc;
    }

    /**
     * Runs the bot. When the function ends, Clock.yield() is called from the main thread
     *
     * @throws GameActionException
     */
    public abstract void run() throws GameActionException;

    /**
     * Moves robot to a specified location
     *
     * @param target Location to move to
     * @throws GameActionException
     */
    public void moveTo(MapLocation target) throws GameActionException {
        // Try bug0
        // move in direction
        // if we see a wall, move along wall until we can move directly again

        switch(movementState){
            case 0:
                heading = rc.getLocation().directionTo(target);
                // normal movement
                if (canMoveInDir(heading)) {
                    if (rc.canMove(heading)) {
                        rc.move(heading);
                    }
                } else {
                    movementState = 1;
                    closest = rc.getLocation().distanceSquaredTo(target);
                    movementSlope = getMovementSlope(target);
                }
                break;
            case 1:
                while (!canMoveInDir(heading)) {
                    heading = heading.rotateRight();
                }
                // Now we can move forward, but should we?
                if (canMoveInDir(heading.rotateLeft())) {
                   heading = heading.rotateLeft();
                }

                if (rc.canMove(heading)){
                    rc.move(heading);
                    if (/*Utilities.isClose(getMovementSlope(target), movementSlope) &&*/
                            rc.getLocation().distanceSquaredTo(target) < closest) {
                        movementState = 0;
                    }
                }
                break;
        }
    }

    private double getMovementSlope(MapLocation target) {
        return Utilities.getSlope(target, rc.getLocation());
    }



    //Checks to see if we can move in desiredDirection
    private boolean canMoveInDir(Direction desiredDirection) throws GameActionException {
        MapLocation loc = rc.adjacentLocation(desiredDirection);
        //TODO set to Util constant
        return rc.onTheMap(loc) && !rc.isLocationOccupied(loc) && rc.sensePassability(loc) > 0.2;
    }

    /**
     * Increases the recorded age of the robot by 1
     */
    public void incrementAge(){
        age += 1;
    }

    /**
     * Attempts to move in a given direction.
     * If not possible, tries to move in similar direction (+/- 1 cardinal)
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMove(Direction dir) throws GameActionException {
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
}
