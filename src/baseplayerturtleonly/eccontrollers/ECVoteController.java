package baseplayerturtleonly.eccontrollers;

import baseplayerturtleonly.BotEnlightenment;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

/**
 * This class decides for the EC how many votes to bid at this point in the game.
 * A VoteController is created with the EC whenever the EC spawns.
 */
public class ECVoteController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final ECBudgetController bc;
    private final int delta;
    private int votePercent = 0;
    private int prevPercent = 0;
    private int voteFlat = 1;
    private int prevFlat = 0;
    private int prevVotes = 0;
    private double epsilon;
    private boolean prevVoted = false;
    private boolean prevAdjustFlat = true;
    private final static int MAX_VOTE_PERCENT = 25;
    private final static int MAX_VOTE_FLAT = 20;
    private final static int VOTE_WIN_COUNT = 1501;
    private final static int MAX_ROUNDS = 3000;
    private int[] percentCounts = new int[MAX_VOTE_PERCENT + 1];
    private int[] percentWins = new int[MAX_VOTE_PERCENT + 1];
    private int[] flatCounts = new int[MAX_VOTE_FLAT + 1];
    private int[] flatWins = new int[MAX_VOTE_FLAT + 1];


    /**
     *
     * @param delta determines how much to increment votes by
     * @param epsilon determines probability to randomly increment/decrement
     */
    public ECVoteController(RobotController rc, BotEnlightenment ec, ECBudgetController bc, int delta, double epsilon) {
        this.rc = rc;
        this.ec = ec;
        this.bc = bc;
        this.delta = delta;
        this.epsilon = epsilon;
    }


    /**
     * Runs a turn of voting for the EC
     */
    @Override
    public void run() throws GameActionException {
        int currentVotes = rc.getTeamVotes();
        int currentBudget = bc.getVoteBudget();
        int[] activeCounts;
        int[] activeWins;
        int activeValue;
        int prevValue;
        int MAX_VOTE;
        String active;

        if (prevAdjustFlat) {
            // if team won the previous voting round
            if (currentVotes - this.prevVotes == 1) {
                //System.out.println(rc.getTeam() + " won the last round");
                for (int i = this.prevFlat; i <= MAX_VOTE_FLAT; i++){
                    this.flatCounts[i]++;
                    this.flatWins[i]++;
                }
                if (this.epsilon >= Math.random() && currentVotes < VOTE_WIN_COUNT) {
                    //System.out.println(rc.getTeam() + " randomly decreased FLAT");
                    this.voteFlat -= this.delta;
                }
                // if team lost the previous voting round
            } else if (this.prevVoted) {
                //System.out.println(rc.getTeam() + " lost the last round");
                for (int i = 0; i<=this.prevFlat && i <= MAX_VOTE_FLAT; i++) {
                    this.flatCounts[i]++;
                }
                if (this.epsilon >= Math.random() && currentVotes < VOTE_WIN_COUNT) {
                    //System.out.println(rc.getTeam() + " randomly incremented FLAT");
                    this.voteFlat += this.delta;
                }
            }
        } else {
            // if team won the previous voting round
            if (currentVotes - this.prevVotes == 1) {
                //System.out.println(rc.getTeam() + " won the last round");
                for (int i = this.prevPercent; i <= MAX_VOTE_PERCENT; i++){
                    this.percentCounts[i]++;
                    this.percentWins[i]++;
                }
                if (this.epsilon >= Math.random() && currentVotes < VOTE_WIN_COUNT) {
                    //System.out.println(rc.getTeam() + " randomly decreased PERCENT");
                    this.votePercent -= this.delta;
                }
                // if team lost the previous voting round
            } else if (this.prevVoted) {
                //System.out.println(rc.getTeam() + " lost the last round");
                for (int i = 0; i<=this.prevPercent && i <= MAX_VOTE_FLAT; i++) {
                    this.percentCounts[i]++;
                }
                if (this.epsilon >= Math.random() && currentVotes < VOTE_WIN_COUNT) {
                    //System.out.println(rc.getTeam() + " randomly incremented PERCENT");
                    this.votePercent += this.delta;
                }
            }
        }
        this.prevVotes = currentVotes;

        boolean updateFlat = this.voteFlat < MAX_VOTE_FLAT;
        if (updateFlat) {
           // System.out.println("Adjusting flat vote amount");
            this.prevAdjustFlat = true;
            activeCounts = this.flatCounts;
            activeWins = this.flatWins;
            activeValue = this.voteFlat;
            prevValue = this.prevFlat;
            MAX_VOTE = MAX_VOTE_FLAT;
            active = "FLAT";
        } else {
            //System.out.println("Adjusting percent vote amount");
            this.prevAdjustFlat = false;
            activeCounts = this.percentCounts;
            activeWins = this.percentWins;
            activeValue = this.votePercent;
            prevValue = this.prevPercent;
            MAX_VOTE = MAX_VOTE_PERCENT;
            active = "PERCENT";
        }

        int bidAmount = (updateFlat ? (int) (this.votePercent * (double) (currentBudget-activeValue)/100 + activeValue)
                : (int) (activeValue * (double) (currentBudget-this.voteFlat)/100) +this.voteFlat);
        if (rc.canBid(bidAmount) && bidAmount <= currentBudget){
            //System.out.println(rc.getTeam() + " bid " + bidAmount);
            rc.bid(bidAmount);
            this.prevVoted = true;
            this.prevFlat = (updateFlat ? activeValue : this.prevFlat);
            this.prevPercent = (updateFlat ? this.prevPercent : activeValue);
        } else {
            //System.out.println("Could not cast bid of: " + bidAmount);
            this.prevVoted = false;
        }

        int[] setSizes = new int[MAX_VOTE + 1];
        int sum = 0;
        for (int i = MAX_VOTE; i>=0; i--){
            setSizes[i] = sum;
            sum += activeCounts[i] - activeWins[i];
        }
        int remaining = MAX_ROUNDS - rc.getRoundNum();
        double estimatedWinProb = 0.0;
        int a = (prevValue != 0 ? (prevValue - this.delta) : 0);
        while (currentVotes + remaining * estimatedWinProb < VOTE_WIN_COUNT) {
            if (a <= MAX_VOTE) {
                int setSize = activeCounts[a] + setSizes[a];
                if (setSize > 0) {
                    estimatedWinProb = (double) activeWins[a] / setSize;
                    ec.setVoteWinRate(estimatedWinProb);
                   // System.out.println(currentVotes + remaining * estimatedWinProb);
                    a++;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        //System.out.println(rc.getTeam() + " set their " + active + " amount to " + a);
        this.voteFlat = updateFlat ? (a > 0 ? a : 1) : this.voteFlat;
        this.votePercent = updateFlat ? this.votePercent : a;
    }
}
