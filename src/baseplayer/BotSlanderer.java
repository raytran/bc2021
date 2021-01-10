package baseplayer;

import battlecode.common.*;

public class BotSlanderer extends BotController {
    public BotSlanderer(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public BotController run() throws GameActionException {
        Direction toMove = Utilities.randomDirection();
        //nav.fuzzyMove(toMove);
        nav.spreadOut(toMove);
        if (rc.getType() == RobotType.POLITICIAN){
            // Just converted; return a politician controller instead
            return BotPolitician.fromSlanderer(this);
        }

        // Search for boundary if we can
        if (Clock.getBytecodesLeft() > 1000) {
            searchForNearbyBoundaries();
            flagBoundaries();
        }
        return this;
    }
}
