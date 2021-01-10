package baseplayer;

import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;

public interface SenseCallback {
    void run(RobotInfo robotInfo) throws GameActionException;
}
