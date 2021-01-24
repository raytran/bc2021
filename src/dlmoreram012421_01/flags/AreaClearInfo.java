package dlmoreram012421_01.flags;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public class AreaClearInfo {
    public final int timestamp;
    public final MapLocation location;
    public AreaClearInfo(int timestamp, MapLocation location) {
        this.timestamp = timestamp;
        this.location = location;
    }
}
