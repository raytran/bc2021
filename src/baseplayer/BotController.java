package baseplayer;

import baseplayer.nav.NavigationController;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public abstract class BotController {
    RobotController rc;
    NavigationController nav;
    int age;

    public BotController(RobotController rc) {
        age = 0;
        this.rc = rc;
        this.nav = new NavigationController(rc);
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
        this.nav.moveTo(target);
    }

    /**
     * Increases the recorded age of the robot by 1
     */
    public void incrementAge(){
        age += 1;
    }

}
