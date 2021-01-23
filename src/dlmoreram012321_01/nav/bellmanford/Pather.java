package dlmoreram012321_01.nav.bellmanford;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Pather {
    static RobotController rc;
    public static void init(RobotController rc){
        Pather.rc = rc;
        NorthPather.rc = rc;
        NortheastPather.rc = rc;
        EastPather.rc = rc;
        SoutheastPather.rc = rc;
        SouthPather.rc = rc;
        SouthwestPather.rc = rc;
        WestPather.rc = rc;
        NorthwestPather.rc = rc;
    }

    public static void pathTo(MapLocation loc) throws GameActionException {
        switch (rc.getLocation().directionTo(loc)){
            case NORTH:
                NorthPather.pathTo(loc);
                break;
            case NORTHEAST:
                NortheastPather.pathTo(loc);
                break;
            case EAST:
                EastPather.pathTo(loc);
                break;
            case SOUTHEAST:
                SoutheastPather.pathTo(loc);
                break;
            case SOUTH:
                SouthPather.pathTo(loc);
                break;
            case SOUTHWEST:
                SouthwestPather.pathTo(loc);
                break;
            case WEST:
                WestPather.pathTo(loc);
                break;
            case NORTHWEST:
                NorthwestPather.pathTo(loc);
                break;
            case CENTER:
                break;
        }
    }
}
