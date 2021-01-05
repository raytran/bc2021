package baseplayer.flags;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

public class BoundarySpottedInfo {
    public final MapLocation delta;
    public final Direction boundaryDirection;
    public BoundarySpottedInfo(MapLocation delta, Direction boundaryDirection){
        this.delta = delta;
        this.boundaryDirection = boundaryDirection;
    }
}
