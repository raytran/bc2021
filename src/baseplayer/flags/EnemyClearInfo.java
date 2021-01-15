package baseplayer.flags;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public class EnemyClearInfo {
    public final int timestamp;
    public final MapLocation location;
    public final RobotType enemyType;
    public EnemyClearInfo(int timestamp, MapLocation location, RobotType enemyType){
        this.timestamp = timestamp;
        this.location = location;
        this.enemyType = enemyType;
    }
}
