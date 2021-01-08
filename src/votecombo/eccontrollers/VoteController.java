package votecombo.eccontrollers;

import votecombo.BotEnlightenment;
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
    private int prev_percent = 0;
    private int prev_votes = 0;
    private boolean prev_voted = false;
    private final static int MAX_VOTE_AMOUNT = 25;
    private final static int VOTE_WIN_COUNT = 1501;
    private final static int MAX_ROUNDS = 3000;
    private int[] counts = new int[MAX_VOTE_AMOUNT + 1];
    private int[] wins = new int[MAX_VOTE_AMOUNT + 1];


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
        int current_votes = rc.getTeamVotes();
        int current_influence = rc.getInfluence();

        // if team won the previous voting round
        if (current_votes - this.prev_votes == 1) {
            System.out.println(rc.getTeam() + " won the last round");
            for (int i = this.prev_percent; i<= MAX_VOTE_AMOUNT; i++){
                this.counts[i]++;
                this.wins[i]++;
            }
            if (this.epsilon >= Math.random() && current_votes < VOTE_WIN_COUNT) {
                System.out.println(rc.getTeam() + " randomly decremented");
                this.amount -= this.delta;
            }
        // if team lost the previous voting round
        } else if (this.prev_voted) {
            System.out.println(rc.getTeam() + " lost the last round");
            for (int i = 0; i<=this.prev_percent && i<= MAX_VOTE_AMOUNT; i++) {
                this.counts[i]++;
            }
            if (this.epsilon >= Math.random() && current_votes < VOTE_WIN_COUNT) {
                System.out.println(rc.getTeam() + " randomly incremented");
                this.amount += this.delta;
            }
        }
        this.prev_votes = current_votes;

        double alpha =  Math.pow((double) rc.getRoundNum()/MAX_ROUNDS, 3);
        double flatContribution = (1-alpha) * this.amount;
        double percentContribution = alpha * this.amount * (double) current_influence/100;
        if (rc.canBid((int) (flatContribution + percentContribution))){
            System.out.println(rc.getTeam() + " bid " + (int) (flatContribution + percentContribution));
            rc.bid((int) (flatContribution + percentContribution));
            this.prev_voted = true;
            this.prev_percent = this.amount;
        } else {
            this.prev_voted = false;
        }

        int[] setSizes = new int[MAX_VOTE_AMOUNT +1];
        int sum = 0;
        for (int i = MAX_VOTE_AMOUNT; i>=0; i--){
            setSizes[i] = sum;
            sum += this.counts[i] - this.wins[i];
        }
        int remaining = MAX_ROUNDS-rc.getRoundNum();
        double estimatedWinProb = 0.0;
        int a = this.prev_percent-this.delta;
        while (current_votes + remaining*estimatedWinProb <= VOTE_WIN_COUNT) {
            if (a <= MAX_VOTE_AMOUNT) {
                int setSize = this.counts[a] + setSizes[a];
                if (setSize > 0) {
                    estimatedWinProb = (double) this.wins[a] / setSize;
                    System.out.println(current_votes + remaining * estimatedWinProb);
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
