package baseplayer;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class BotSlanderer extends BotController {
    public BotSlanderer(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public BotController run() throws GameActionException {
        Direction toMove = Utilities.randomDirection();
        nav.fuzzyMove(toMove);
        if (rc.getType() == RobotType.POLITICIAN){
            // Just converted; return a politician controller instead
            return BotPolitician.fromSlanderer(this);
        }
        return this;
    }
}
