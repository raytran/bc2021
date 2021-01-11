package dlmoreram011021_03;

import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;

public interface SenseCallback {
    void run(RobotInfo robotInfo) throws GameActionException;
}
