package pathingprofiler;
import battlecode.common.*;
import baseplayer.nav.NavigationController;

import java.util.Arrays;
import java.util.Map;
import java.util.Queue;

public strictfp class RobotPlayer {
    static RobotController rc;
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController theRc) throws GameActionException {
        rc = theRc;
        int spawnCount = 0;
        int age = 0;
        NavigationController nav = new NavigationController(rc);
        int[][] deltas = {{0,0},{1,0},{0,-1},{-1,0},{0,1},{2,0},{1,-1},{0,-2},{-1,-1},{-2,0},{-1,1},{0,2},{1,1},{3,0},{2,-1},{1,-2},{0,-3},{-1,-2},{-2,-1},{-3,0},{-2,1},{-1,2},{0,3},{1,2},{2,1},{4,0},{3,-1},{2,-2},{1,-3},{0,-4},{-1,-3},{-2,-2},{-3,-1},{-4,0},{-3,1},{-2,2},{-1,3},{0,4},{1,3},{2,2},{3,1},{4,-1},{3,-2},{2,-3},{1,-4},{-1,-4},{-2,-3},{-3,-2},{-4,-1},{-4,1},{-3,2},{-2,3},{-1,4},{1,4},{2,3},{3,2},{4,1},{4,-2},{3,-3},{2,-4},{-2,-4},{-3,-3},{-4,-2},{-4,2},{-3,3},{-2,4},{2,4},{3,3},{4,2}};
        MapLocation target = rc.getLocation().translate(20, 40);
        while(true){

            if (spawnCount < 1 && rc.canBuildRobot(RobotType.POLITICIAN, Direction.NORTH, 1)){
                rc.buildRobot(RobotType.POLITICIAN, Direction.NORTH, 1);
                spawnCount += 1;
            };

            if (rc.getType() == RobotType.POLITICIAN){

                System.out.println("STARTING ON TURN " + rc.getRoundNum());
                int byteCodeStart = Clock.getBytecodeNum();
                matrixNav(rc, target);
                System.out.println("TOOK " + (Clock.getBytecodeNum() - byteCodeStart) + " ON " + rc.getRoundNum());

            }


            Clock.yield();
            age += 1;
        }
    }
    // radius = Matrix radius size
    static void matrixNav(RobotController rc, MapLocation target) throws GameActionException {
    }
}
