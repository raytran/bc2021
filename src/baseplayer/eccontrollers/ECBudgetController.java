package baseplayer.eccontrollers;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import baseplayer.BotEnlightenment;
import baseplayer.eccontrollers.ECController;

public class ECBudgetController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final int MAX_ROUNDS = 2999;
    private final int MEMORY_SIZE = 100;
    private int voteBudget;
    private int botBudget;
    private int hpBudget;
    private double[] voteMemory = new double[MEMORY_SIZE];
    private int[] botMemory = new int[MEMORY_SIZE];
    private int[] hpMemory = new int[MEMORY_SIZE];


    public ECBudgetController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
    }

    @Override
    public void run() throws GameActionException {
        int currentInfluence = rc.getInfluence();
        int currentIndex = (rc.getRoundNum() - 1) % MEMORY_SIZE;
        System.out.println("currentIndex: " + currentIndex);

        double prevVote;
        int prevBot;
        int prevHp;
        if (rc.getRoundNum() > MEMORY_SIZE) {
            prevVote = voteMemory[currentIndex];
            prevBot = botMemory[currentIndex];
            prevHp = hpMemory[currentIndex];
        } else {
            prevVote = voteMemory[0];
            prevBot = botMemory[currentIndex != 0 ? currentIndex - 1 : 0];
            prevHp = hpMemory[0];
        }

        voteMemory[currentIndex] = ec.getVoteWinRate();
        botMemory[currentIndex] = ec.getSlandererCount() + ec.getMuckrakerCount() + ec.getPoliticianCount();
        hpMemory[currentIndex] = currentInfluence;

        // calculate deltas
        double voteDelta = (voteMemory[currentIndex] - prevVote) / MEMORY_SIZE;
        double botDelta = rc.getRoundNum() > MEMORY_SIZE ? (double) (botMemory[currentIndex] - prevBot) / MEMORY_SIZE : 0;
        double hpDelta = (double) (hpMemory[currentIndex] - prevHp) / MEMORY_SIZE;

        // offset deltas
        double maxDelta = Math.max(voteDelta, Math.max(botDelta, hpDelta));
        voteDelta -= maxDelta != 0 ? 2 * maxDelta : 1;
        botDelta -= maxDelta != 0 ? 2 * maxDelta : 1;
        hpDelta -= maxDelta != 0 ? 2 * maxDelta : 1;

        // check if we have secured a vote win
        voteDelta = rc.getTeamVotes() > 1500 ? 0 : voteDelta;

        // add alpha, beta scaling factor to adjust early voting and bot budgets
        double alpha = Math.pow((double) rc.getRoundNum() / MAX_ROUNDS, 0.2);
        double beta = 1 + (1 - alpha);
        voteDelta *= alpha;
        botDelta *= beta;

        // normalize to positive percentages
        double total = Math.abs(voteDelta) + Math.abs(botDelta) + Math.abs(hpDelta);
        voteDelta *= -1/total;
        botDelta *= -1/total;
        hpDelta *= -1/total;
        System.out.println("voteDelta: " + voteDelta + "\nbotDelta: " + botDelta + "\nhpDelta: " + hpDelta);

        voteBudget = (int) Math.ceil(voteDelta * currentInfluence);
        botBudget = (int) Math.ceil(botDelta * currentInfluence);
        hpBudget = currentInfluence - voteBudget - botBudget;

        // if there are nearby enemy politicians, make sure we have enough hp
        int enemyInfluence = ec.checkNearbyEnemies();
        if (hpBudget <= enemyInfluence) {
            if (currentInfluence > enemyInfluence + 1) {
                hpBudget = enemyInfluence + 1;
                int remainingInfluence = currentInfluence - hpBudget;
                double newTotal = voteDelta + botDelta;
                voteBudget = (int) (voteDelta / newTotal * remainingInfluence);
                botBudget = remainingInfluence - voteBudget;
            } else {
                hpBudget = currentInfluence - 1;
                voteBudget = 0;
                botBudget = 1;
            }
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
}
