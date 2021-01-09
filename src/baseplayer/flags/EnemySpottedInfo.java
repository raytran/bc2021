package baseplayer.flags;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public class EnemySpottedInfo {
    public final MapLocation location;
    public final RobotType enemyType;
    public EnemySpottedInfo(MapLocation location, RobotType enemyType){
        this.location = location;
        this.enemyType = enemyType;
    }
}
