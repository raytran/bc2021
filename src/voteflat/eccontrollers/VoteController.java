package voteflat.eccontrollers;

import battlecode.common.Clock;
import voteflat.BotEnlightenment;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

/**
 * This class decides for the EC how many votes to bid at this point in the game.
 * A VoteController is created with the EC whenever the EC spawns.
 */
public class VoteController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final double epsilon;
    private final int delta;
    private int amount;
    private int prev_amount = 0;
    private int prev_votes = 0;
    private final static int MAX_VOTE = 50;
    private final static int VOTE_WIN_COUNT = 1501;
    private final static int MAX_ROUNDS = 3000;
    private int[] counts = new int[MAX_VOTE +1];
    private int[] wins = new int[MAX_VOTE +1];


    /**
     *
     * @param start_vote to initialize vote amount
     * @param delta determines how much to increment votes by
     * @param epsilon affects VoteController willingness to change vote count
     */
    public VoteController(RobotController rc, BotEnlightenment ec, int start_vote, int delta, double epsilon) {
        this.rc = rc;
        this.ec = ec;
        this.amount = start_vote;
        this.epsilon = epsilon;
        this.delta = delta;
    }


    /**
     * Runs a turn of voting for the EC
     */
    @Override
    public void run() throws GameActionException {
        int initialByte = Clock.getBytecodesLeft();
        int current_votes = rc.getTeamVotes();

        // if team won the previous voting round
        if (current_votes - this.prev_votes == 1) {
            System.out.println(rc.getTeam() + " won the last round");
            for (int i = this.prev_amount; i<= MAX_VOTE; i++){
                this.counts[i]++;
                this.wins[i]++;
            }
            if (this.epsilon >= Math.random() && current_votes < VOTE_WIN_COUNT) {
                System.out.println(rc.getTeam() + " randomly decremented");
                this.amount -= this.delta;
            }
        // if team lost the previous voting round
        } else {
            System.out.println(rc.getTeam() + " lost the last round");
            for (int i = 0; i<=this.prev_amount; i++) {
                this.counts[i]++;
            }
            if (this.epsilon >= Math.random() && current_votes < VOTE_WIN_COUNT) {
                System.out.println(rc.getTeam() + " randomly incremented");
                this.amount += this.delta;
            }
        }
        this.prev_votes = current_votes;
        System.out.println("First block used Bytecode of " + (initialByte - Clock.getBytecodesLeft()));

        if (rc.canBid(this.amount)){
            System.out.println(rc.getTeam() + " bid " + this.amount);
            rc.bid(this.amount);
            this.prev_amount = this.amount;
        }

        int[] setSizes = new int[MAX_VOTE +1];
        int sum = 0;
        for (int i = MAX_VOTE; i>=0; i--){
            setSizes[i] = sum;
            sum += this.counts[i] - this.wins[i];
        }
        int remaining = MAX_ROUNDS-rc.getRoundNum();
        double estimatedWinProb = 0.0;
        int a = 0;
        while (current_votes + remaining*estimatedWinProb <= VOTE_WIN_COUNT) {
            if (a<= MAX_VOTE) {
                int setSize = this.counts[a] + setSizes[a];
                if (setSize > 0) {
                    estimatedWinProb = (double) this.wins[a] / setSize;
                    System.out.println(current_votes + remaining*estimatedWinProb);
                    a++;
                } else {
                    break;
                }
            } else {
                System.out.println("Vote win unlikely");
                break;
            }
        }
        System.out.println(rc.getTeam() + " set their amount to " + a);
        this.amount = a;
    }
}
