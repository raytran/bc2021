package votedoubler.eccontrollers;

import votedoubler.BotEnlightenment;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

/**
 * This class decides for the EC how many votes to bid at this point in the game.
 * A VoteController is created with the EC whenever the EC spawns.
 */
public class VoteController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private int amount = 1;
    private int prevVotes = 0;
    private boolean prevVoted = false;


    public VoteController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
    }


    /**
     * Runs a turn of voting for the EC
     */
    @Override
    public void run() throws GameActionException {
        int currentVotes = rc.getTeamVotes();
        int currentInfluence = rc.getInfluence();

        amount = currentVotes - prevVotes != 1 && prevVoted ? amount * 2 : amount;
        prevVotes = currentVotes;

        if (rc.canBid(amount)){
            System.out.println(rc.getTeam() + " bid " + amount);
            rc.bid(amount);
            prevVoted = true;
        } else {
            System.out.println("could not bid " + amount);
            prevVoted = false;
        }
    }
}
