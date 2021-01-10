package dlmoreram011021_02.eccontrollers;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import dlmoreram011021_02.BotEnlightenment;

/**
 * This class decides for the EC how many votes to bid at this point in the game.
 * A VoteController is created with the EC whenever the EC spawns.
 */
public class ECVoteController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final ECBudgetController bc;
    private final int initialAmount = 1;
    private int multiplier = 1;
    private int amount = initialAmount;
    private int prevVotes = 0;
    private boolean prevVoted = false;
    private boolean prevAdjustMultiplier = false;
    private final static int MAX_VOTE = 20;
    private final static int VOTE_WIN_COUNT = 1501;
    private final static int MAX_ROUNDS = 2999;
    private final int offset = 1 - initialAmount;
    private int[] counts = new int[MAX_VOTE + offset];
    private int[] wins = new int[MAX_VOTE + offset];

    public ECVoteController(RobotController rc, BotEnlightenment ec, ECBudgetController bc) {
        this.rc = rc;
        this.ec = ec;
        this.bc = bc;
    }


    /**
     * Runs a turn of voting for the EC
     */
    @Override
    public void run() throws GameActionException {
        int currentBudget = bc.getVoteBudget();
        int currentVotes = rc.getTeamVotes();
        if (currentBudget > 0 && currentVotes < VOTE_WIN_COUNT) {
            boolean wonLast = currentVotes - prevVotes == 1;
            if (wonLast) {
                for(int i=amount;i<=MAX_VOTE;i++) {
                    counts[i - initialAmount]++;
                    wins[i - initialAmount]++;
                }
                if (!prevAdjustMultiplier && multiplier > 1 && amount == initialAmount) {
                    prevAdjustMultiplier = true;
                    multiplier--;
                    amount = MAX_VOTE;
                    counts = new int[MAX_VOTE + offset];
                    wins = new int[MAX_VOTE + offset];
                } else {
                    prevAdjustMultiplier = false;
                }
            } else if (prevVoted) {
                for(int i=initialAmount;i<=amount;i++) {
                    counts[amount - initialAmount]++;
                }
                if (!prevAdjustMultiplier && amount == MAX_VOTE) {
                    prevAdjustMultiplier = true;
                    multiplier++;
                    counts = new int[MAX_VOTE + offset];
                    wins = new int[MAX_VOTE + offset];
                } else {
                    prevAdjustMultiplier = false;
                }
            }
            prevVotes = currentVotes;

            // generate list of set sizes that represent the total number of losses of values to the right of index
            int[] setSizes = new int[MAX_VOTE + offset];
            int sum = 0;
            for (int i = MAX_VOTE + offset; i>=initialAmount; i--){
                setSizes[i - initialAmount] = sum;
                sum += counts[i - initialAmount] - wins[i - initialAmount];
            }
            int remaining = MAX_ROUNDS - rc.getRoundNum();
            double estimatedWinProb = 0.0;
            amount = amount != 1 ? amount - 1 : amount;
            while (currentVotes + remaining * estimatedWinProb < VOTE_WIN_COUNT) {
                if (amount < MAX_VOTE) {
                    int setSize = counts[amount - initialAmount] + setSizes[amount - initialAmount];
                    if (setSize > 0) {
                        estimatedWinProb = (double) wins[amount - initialAmount] / setSize;
                        ec.setVoteWinRate(estimatedWinProb);
                        if (currentVotes + remaining * estimatedWinProb >= VOTE_WIN_COUNT) {
                            break;
                        } else {
                            amount++;
                        }
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }

            int bidAmount = amount * multiplier;
            if (rc.canBid(bidAmount) && bidAmount <= currentBudget) {
                rc.bid(bidAmount);
                prevVoted = true;
            } else {
                prevVoted = false;
            }
        }
    }
}
