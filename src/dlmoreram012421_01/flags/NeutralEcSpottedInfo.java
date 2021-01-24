package dlmoreram012421_01.flags;

import battlecode.common.MapLocation;

public class NeutralEcSpottedInfo {
    public final int timestamp;
    public final MapLocation location;
    public final int conviction;
    public NeutralEcSpottedInfo(int timestamp, MapLocation location, int conviction){
        this.timestamp = timestamp;
        this.location = location;
        this.conviction = conviction;
    }
}
