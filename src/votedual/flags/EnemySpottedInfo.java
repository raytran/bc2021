package votedual.flags;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public class EnemySpottedInfo {
    public final MapLocation delta;
    public final RobotType enemyType;
    public EnemySpottedInfo(MapLocation location, RobotType enemyType){
        this.delta = location;
        this.enemyType = enemyType;
    }
}
