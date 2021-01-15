package baseplayer.flags;

import battlecode.common.MapLocation;

public class NeutralEcClearInfo {
    public final int timestamp;
    public final MapLocation location;
    public final int conviction;
    public NeutralEcClearInfo(int timestamp, MapLocation location, int conviction){
        this.timestamp = timestamp;
        this.location = location;
        this.conviction = conviction;
    }
}
