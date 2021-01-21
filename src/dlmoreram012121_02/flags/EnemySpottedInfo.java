package dlmoreram012121_02.flags;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public class EnemySpottedInfo {
    public final int timestamp;
    public final MapLocation location;
    public final RobotType enemyType;
    public final boolean isGuess;
    public EnemySpottedInfo(int timestamp, MapLocation location, RobotType enemyType, boolean isGuess){
        this.timestamp = timestamp;
        this.location = location;
        this.enemyType = enemyType;
        this.isGuess = isGuess;
    }
}
