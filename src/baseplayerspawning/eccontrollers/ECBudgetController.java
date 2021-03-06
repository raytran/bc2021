package baseplayerspawning.eccontrollers;

import baseplayerspawning.BotEnlightenment;
import baseplayerspawning.Utilities;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class ECBudgetController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final PIDBudgetVariable voteDelta = new PIDBudgetVariable(50.135841369628906, 0, 4.649940013885498);
    private final PIDBudgetVariable botDelta = new PIDBudgetVariable(8.244284629821777, 0, 103.1036376953125);
    private final PIDBudgetVariable hpDelta = new PIDBudgetVariable( 34.32314682006836, 0, 35.57890319824219);

    private int voteBudget;
    private int botBudget;
    private int hpBudget;


    public ECBudgetController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
    }

    @Override
    public void run() throws GameActionException {
        int currentInfluence = rc.getInfluence();
        int currentRound = rc.getRoundNum();
        if (currentRound == 1) {
            botBudget = 88;
            voteBudget = 20;
            hpBudget = 42;
        }

        int income = currentInfluence - voteBudget - botBudget - hpBudget;
        if (income > 0) {
            //TODO: update smart PID targets
            double voteTarget = (double) currentRound / 2;
            double botTarget = 0.25 * currentRound;
            double hpTarget = 1 + currentRound * 0.125;

            if (hpBudget > hpTarget) {
                int diff = (int) (hpBudget - hpTarget);
                hpBudget -= diff;
                income += diff;
            }

            // set new targets
            voteDelta.setTarget(voteTarget);
            botDelta.setTarget(botTarget);
            hpDelta.setTarget(hpTarget);

            // update deltas
            voteDelta.update(rc.getTeamVotes());
            botDelta.update(ec.getLocalRobotCount());
            hpDelta.update(hpBudget);

            // normalize to positive percentages
            double voteDValue = voteDelta.getValue();
            double botDValue = botDelta.getValue();
            double hpDValue = hpDelta.getValue();
            System.out.println("Deltas:\nVote: " + voteDValue + "\nBot: " + botDValue + "\nHP: " + hpDValue);
            double total = voteDValue + botDValue + hpDValue;
            if (total > 0) {
                voteDValue *= 1. / total;
                botDValue *= 1. / total;
            } else {
                voteDValue = 0;
                botDValue = 1;
            }
            System.out.println("Normalized Deltas:\n" + "Vote: " + voteDValue + "\nBot: " + botDValue);

            int voteAllocation;
            int botAllocation;
            int hpAllocation;
            try {
                voteAllocation = (int) Math.round(voteDValue * income);
                botAllocation = (int) Math.round(botDValue * income);
                hpAllocation = income - voteAllocation - botAllocation;
                assert voteAllocation >= 0 && botAllocation >= 0 && hpAllocation >= 0;
            } catch (AssertionError e) {
                voteAllocation = (int) Math.floor(voteDValue * income);
                botAllocation = (int) Math.floor(botDValue * income);
                hpAllocation = income - voteAllocation - botAllocation;
            }

            /** if there are nearby enemy politicians, make sure we have enough hp
             int enemyInfluence = ec.checkNearbyEnemies();
             if (hpBudget <= enemyInfluence && hpBudget != 0) {
             if (currentInfluence > enemyInfluence + 1) {
             hpBudget = enemyInfluence + 1;
             int remainingInfluence = currentInfluence - hpBudget;
             double newTotal = voteDValue + botDValue;
             voteAllocation = (int) (voteDValue / newTotal * remainingInfluence);
             botAllocation = remainingInfluence - (voteBudget + voteAllocation);
             } else {
             voteAllocation = 0;
             botAllocation = 0;
             hpBudget += income - 1;
             botBudget += 1;
             }
             }**/

            if (rc.getTeamVotes() >= Utilities.VOTE_WIN) {
                botBudget += voteAllocation + botAllocation + voteBudget;
                voteBudget = 0;
            } else if (currentRound - ec.getLastBotSpawn() > 150) {
                voteBudget = voteAllocation + botAllocation + botBudget;
                botBudget = 0;
            } else {
                voteBudget += voteAllocation;
                botBudget += botAllocation;
            }

            voteBudget += voteAllocation;
            botBudget += botAllocation;
            hpBudget += hpAllocation;

            System.out.println("Total influence: " + currentInfluence + "\nVoting Budget: "
                    + voteBudget + "\nBot Budget: " + botBudget + "\nSaving: " + hpBudget);
        }
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

    public boolean canSpend (ECSpawnController sc, int amount) { return botBudget >= amount; }

    public boolean canSpend (ECVoteController vc, int amount) { return voteBudget >= amount; }
}
