package baseplayer.eccontrollers;

import baseplayer.BotEnlightenment;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

/**
 * This class decides for the EC how many votes to bid at this point in the game.
 * A VoteController is created with the EC whenever the EC spawns.
 */
public class ECVoteController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;

    public ECVoteController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
    }

    /**
     * Runs a turn of voting for the EC
     */
    @Override
    public void run() throws GameActionException {
    }
}
