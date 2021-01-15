package dlmoreram011521_01.flags;

import battlecode.common.MapLocation;

public class AreaClearInfo {
    public final int timestamp;
    public final MapLocation location;
    public AreaClearInfo(int timestamp, MapLocation location) {
        this.timestamp = timestamp;
        this.location = location;
    }
}
