package baseplayer.eccontrollers;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import baseplayer.BotEnlightenment;
import baseplayer.eccontrollers.ECController;

public class ECBudgetController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final int MAX_ROUNDS = 2999;
    private int voteBudget;
    private int botBudget;
    private int hpBudget;
    private double prevVotePercent;
    private int prevBotAmount = 0;


    public ECBudgetController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
    }

    @Override
    public void run() throws GameActionException {
        int currentInfluence = rc.getInfluence();
        int income = currentInfluence - voteBudget - botBudget - hpBudget;
        int hpThreshold = rc.getRoundNum() - ec.getLastEnemySeen() < 500 ? (int) (rc.getRoundNum() * 0.25) : (int) (rc.getRoundNum() * 0.05);
        double voteWinRate = ec.getVoteWinRate();
        int botAmount = ec.getPoliticianCount() + ec.getMuckrakerCount() + ec.getSlandererCount();

        // calculate deltas
        double voteDelta = (voteWinRate - prevVotePercent) * 100;
        double botDelta = botAmount - prevBotAmount;
        double hpDelta = hpBudget - hpThreshold;

        prevVotePercent = voteWinRate;
        prevBotAmount = botAmount;

        // offset deltas
        double maxDelta = Math.max(voteDelta, Math.max(botDelta, hpDelta));
        voteDelta -= maxDelta != 0 ? 2 * maxDelta : 1;
        botDelta -= maxDelta != 0 ? 2 * maxDelta : 1;
        hpDelta -= maxDelta != 0 ? 2 * maxDelta : 1;

        // add alpha, beta scaling factor to adjust early voting and bot budgets
        double alpha = Math.pow((double) rc.getRoundNum() / MAX_ROUNDS, 0.2);
        double beta = 1 + (1 - alpha);
        voteDelta *= alpha;
        botDelta *= beta;

        // normalize to positive percentages
        double total = Math.abs(voteDelta) + Math.abs(botDelta) + Math.abs(hpDelta);
        voteDelta *= -1/total;
        botDelta *= -1/total;

        int voteAllocation;
        int botAllocation;
        if (hpBudget < hpThreshold) {
            voteAllocation = rc.getTeamVotes() > 1500 ? 0 : (int) Math.floor(voteDelta * income);
            botAllocation = (int) Math.floor(botDelta * income);
            hpBudget = currentInfluence - (voteBudget + voteAllocation) - (botBudget + botAllocation);
        } else {
            double newTotal = voteDelta + botDelta;
            voteAllocation = (int) Math.floor(voteDelta / newTotal * (income - 1));
            botAllocation = income - voteAllocation;
        }

        // if there are nearby enemy politicians, make sure we have enough hp
        int enemyInfluence = ec.checkNearbyEnemies();
        if (hpBudget <= enemyInfluence && hpBudget != 0) {
            if (currentInfluence > enemyInfluence + 1) {
                hpBudget = enemyInfluence + 1;
                int remainingInfluence = currentInfluence - hpBudget;
                double newTotal = voteDelta + botDelta;
                voteAllocation = (int) (voteDelta / newTotal * remainingInfluence);
                botAllocation = remainingInfluence - voteBudget;
            } else {
                voteAllocation = 0;
                botAllocation = 0;
                hpBudget += income - 1;
                botBudget += 1;
            }
        }

        if (rc.getTeamVotes() > 1500) {
            botBudget += voteAllocation + botAllocation + voteBudget;
            voteBudget = 0;
        } else {
            voteBudget += voteAllocation;
            botBudget += botAllocation;
        }
        System.out.println("Total influence: " + currentInfluence + "\nVoting Budget: "
                + voteBudget + "\nBot Budget: " + botBudget + "\nSaving: " + hpBudget);
    }

    /**
     * @return budget for making bids
     */
    public int getVoteBudget() {
        return voteBudget;
    }

    /**
     * @return budget for making bots
     */
    public int getBotBudget() {
        return botBudget;
    }

    /**
     * @return how much influence to save
     */
    public int getHpBudget() {
        return hpBudget;
    }

    public void withdrawBudget(ECSpawnController sc, int amount) { botBudget -= amount; }

    public void withdrawBudget(ECVoteController vc, int amount) { voteBudget -= amount; }
}
