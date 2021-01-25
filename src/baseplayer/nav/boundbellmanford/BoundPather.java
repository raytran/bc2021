package baseplayer.nav.boundbellmanford;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import baseplayer.nav.BoundingBox;

public class BoundPather {
    static RobotController rc;
    public static void init(RobotController rc){
        BoundPather.rc = rc;
        NorthPather.rc = rc;
        NortheastPather.rc = rc;
        EastPather.rc = rc;
        SoutheastPather.rc = rc;
        SouthPather.rc = rc;
        SouthwestPather.rc = rc;
        WestPather.rc = rc;
        NorthwestPather.rc = rc;
    }

    public static void pathTo(MapLocation loc, BoundingBox box) throws GameActionException {
        switch (rc.getLocation().directionTo(loc)){
            case NORTH:
                NorthPather.pathTo(loc, box);
                break;
            case NORTHEAST:
                NortheastPather.pathTo(loc, box);
                break;
            case EAST:
                EastPather.pathTo(loc, box);
                break;
            case SOUTHEAST:
                SoutheastPather.pathTo(loc, box);
                break;
            case SOUTH:
                SouthPather.pathTo(loc, box);
                break;
            case SOUTHWEST:
                SouthwestPather.pathTo(loc, box);
                break;
            case WEST:
                WestPather.pathTo(loc, box);
                break;
            case NORTHWEST:
                NorthwestPather.pathTo(loc, box);
                break;
            case CENTER:
                break;
        }
    }
}
