package dlmoreram011021_01;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class ECBudgetController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private int voteBudget;
    private int botBudget;
    private int hpBudget;
    private int MEMORY_SIZE;
    private double[] voteMemory;
    private int[] botMemory;
    private int[] hpMemory;


    public ECBudgetController(RobotController rc, BotEnlightenment ec, int MEMORY_SIZE) {
        this.rc = rc;
        this.ec = ec;
        this.MEMORY_SIZE = MEMORY_SIZE;
        voteMemory = new double[MEMORY_SIZE];
        botMemory = new int[MEMORY_SIZE];
        hpMemory = new int[MEMORY_SIZE];
    }

    @Override
    public void run() throws GameActionException {
        int currentInfluence = rc.getInfluence();
        int currentIndex = (rc.getRoundNum() - 1) % MEMORY_SIZE;
        ////System.out.println("currentIndex: " + currentIndex);

        double prevVote;
        int prevBot;
        int prevHp;
        if (rc.getRoundNum() > MEMORY_SIZE) {
            prevVote = voteMemory[currentIndex];
            prevBot = botMemory[currentIndex];
            prevHp = hpMemory[currentIndex];
        } else {
            prevVote = voteMemory[0];
            prevBot = botMemory[0];
            prevHp = hpMemory[0];
        }

        voteMemory[currentIndex] = ec.getVoteWinRate();
        botMemory[currentIndex] = ec.getSlandererCount() + ec.getMuckrakerCount() + ec.getPoliticianCount();
        hpMemory[currentIndex] = currentInfluence;

        // calculate deltas
        double voteDelta = (voteMemory[currentIndex] - prevVote) / MEMORY_SIZE;
        double botDelta = (double) (botMemory[currentIndex] - prevBot) / MEMORY_SIZE;
        double hpDelta = (double) (hpMemory[currentIndex] - prevHp) / MEMORY_SIZE;

        // offset deltas
        double maxDelta = Math.max(voteDelta, Math.max(botDelta, hpDelta));
        voteDelta -= maxDelta != 0 ? 2 * maxDelta : 1;
        botDelta -= maxDelta != 0 ? 2 * maxDelta : 1;
        hpDelta -= maxDelta != 0 ? 2 * maxDelta : 1;

        // check if we have secured a vote win
        voteDelta = rc.getTeamVotes() > 1500 ? 0 : voteDelta;

        // normalize to positive percentages
        double total = Math.abs(voteDelta) + Math.abs(botDelta) + Math.abs(hpDelta);
        voteDelta *= -1/total;
        botDelta *= -1/total;
        hpDelta *= -1/total;
        ////System.out.println("voteDelta: " + voteDelta + "\nbotDelta: " + botDelta + "\nhpDelta: " + hpDelta);

        voteBudget = (int) (voteDelta * currentInfluence);
        botBudget = (int) (botDelta * currentInfluence);
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
                hpBudget = currentInfluence;
                voteBudget = 0;
                botBudget = 0;
            }
        }

        ////System.out.println("Total influence: " + currentInfluence + "\nVoting Budget: "
        //        + voteBudget + "\nBot Budget: " + botBudget + "\nSaving: " + hpBudget);
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
