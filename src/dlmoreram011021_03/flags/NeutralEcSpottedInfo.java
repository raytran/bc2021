package dlmoreram011021_03.flags;

import battlecode.common.MapLocation;

public class NeutralEcSpottedInfo {
    public final MapLocation location;
    public final int conviction;
    public NeutralEcSpottedInfo(MapLocation location, int conviction){
        this.location = location;
        this.conviction = conviction;
    }
}
