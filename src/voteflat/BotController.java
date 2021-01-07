package voteflat;

import baseplayer.nav.NavigationController;
import battlecode.common.*;

public abstract class BotController {
    int parentID;
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
                        parentID = robotHere.ID;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Runs the bot. When the function ends, Clock.yield() is called from the main thread
     *
     * @throws GameActionException
     */
    public abstract void run() throws GameActionException;

    /**
     * Increases the recorded age of the robot by 1
     */
    public void incrementAge(){
        age += 1;
    }

}
