package pathingprofiler;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import baseplayer.nav.NavigationController;

import java.util.Queue;

public strictfp class RobotPlayer {
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        NavigationController nav = new NavigationController(rc);
        while(true){

            int byteCodeStart = Clock.getBytecodeNum();
            System.out.println("STARTING PATHFINDING ON TURN " + rc.getRoundNum());
            //Queue<MapLocation> path = nav.localDijkstra(new MapLocation(10010, 23929));
            Queue<MapLocation> path = nav.localBFS(new MapLocation(10010, 23929));
            System.out.println("TOOK " + (Clock.getBytecodeNum() - byteCodeStart) + " ON " + rc.getRoundNum());
            System.out.println(path.size());


            for (MapLocation loc : path) {
                rc.setIndicatorDot(loc, 0, 255, 0);
            }


            Clock.yield();
        }
    }
}
