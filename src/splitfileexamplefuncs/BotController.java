package splitfileexamplefuncs;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public abstract class BotController {
    int age;
    RobotController rc;

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
     * @param loc Location to move to
     * @throws GameActionException
     */
    public void moveTo(MapLocation loc) throws GameActionException {
        //TODO
    }

    /**
     * Increases the recorded age of the robot by 1
     */
    public void incrementAge(){
        age += 1;
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMove(Direction dir) throws GameActionException {
        System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }
}
