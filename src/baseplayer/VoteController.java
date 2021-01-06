package baseplayer;

import battlecode.common.RobotController;

/**
 * This class decides for the EC how many votes to bid at this point in the game.
 * A VoteController is created with the EC whenever the EC spawns.
 */
public class VoteController {
    private final RobotController rc;

    public VoteController(RobotController rc) {
        this.rc = rc;
    }

    /**
     * @return the appropriate amount of influence to bid for votes at this point in the game
     */
    public int influenceToBid() {
        return 0;
    }
}
