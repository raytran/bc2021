package votemultiply.flags;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public class EnemySpottedInfo {
    public final MapLocation location;
    public final RobotType enemyType;
    public final boolean isGuess;
    public EnemySpottedInfo(MapLocation location, RobotType enemyType, boolean isGuess){
        this.location = location;
        this.enemyType = enemyType;
        this.isGuess = isGuess;
    }
}
