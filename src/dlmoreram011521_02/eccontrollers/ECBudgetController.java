package dlmoreram011521_02.eccontrollers;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import dlmoreram011521_02.BotEnlightenment;

public class ECBudgetController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final PIDDelta voteDelta = new PIDDelta(12.759624481201172, 1.302085518836975 , 21.483863830566406);
    private final PIDDelta botDelta = new PIDDelta(6.498876571655273, 6.483767509460449, 28.79730796813965);
    private final PIDDelta hpDelta = new PIDDelta( 5.50677490234375, 11.705179214477539, 1.2709863185882568);

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
        int income = currentInfluence - voteBudget - botBudget - hpBudget;

        //TODO: update PID targets
        /** add alpha, beta scaling factor to adjust early voting and bot budgets
         double alpha = Math.pow((double) rc.getRoundNum() / MAX_ROUNDS, 0.2);
         double beta = 1 + (1 - alpha);
         voteDelta *= alpha;
         botDelta *= beta; **/
        double voteTarget = 0.5 * currentRound < 250 ? (double) currentRound/250 : 1;
        double botTarget = currentRound * 0.25;
        double hpTarget = 1 + currentRound - ec.getLastEnemySeen() < 500 ? (int) (rc.getRoundNum() * 0.25) : (int) (rc.getRoundNum() * 0.05);

        // set new targets
        voteDelta.setTarget(voteTarget);
        botDelta.setTarget(botTarget);
        hpDelta.setTarget(hpTarget);

        // update deltas
        voteDelta.update((double) rc.getTeamVotes()/currentRound);
        botDelta.update(ec.getLocalRobotCount());
        hpDelta.update(hpBudget);

        // normalize to positive percentages
        double voteDValue = voteDelta.getValue();
        double botDValue = botDelta.getValue();
        double hpDValue = hpDelta.getValue();
        //System.out.println("Deltas:\nVote: " + voteDValue + "\nBot: " + botDValue + "\nHP: " + hpDValue);
        if (voteDValue < 0 || botDValue < 0 || hpDValue < 0 ) {
            double min = Math.min(voteDValue, Math.min(botDValue, hpDValue));
            voteDValue -= min;
            botDValue -= min;
            hpDValue -= min;
        }
        double total = voteDValue + botDValue + hpDValue;
        voteDValue *= total > 0 ? 1. / total : 1;
        botDValue *= total > 0 ? 1. / total : 0;
        //System.out.println("Normalized Deltas:\n" + "Vote: " + voteDValue + "\nBot: " + botDValue);

        int voteAllocation;
        int botAllocation;
        int hpAllocation;
        try {
            voteAllocation = (int) Math.round(voteDValue * income);
            botAllocation = (int) Math.round(botDValue * income);
            hpAllocation = income - voteAllocation - botAllocation;
            //System.out.println("initial allocations: " + voteAllocation + ' ' + botAllocation + ' ' + hpAllocation);
            //assert income <= voteAllocation + botAllocation + hpAllocation;
            assert voteAllocation >= 0 && botAllocation >= 0 && hpAllocation >= 0;
        } catch (AssertionError e){
            //System.out.println("assertion error");
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
        }

        if (rc.getTeamVotes() > 1500) {
            botBudget += voteAllocation + botAllocation + voteBudget;
            voteBudget = 0;
        } else if (currentRound - ec.getLastBotSpawn() > 150) {
            voteBudget = voteAllocation + botAllocation + botBudget;
            botBudget = 0;
        } else {
            voteBudget += voteAllocation;
            botBudget += botAllocation;
        }**/

        voteBudget += voteAllocation;
        botBudget += botAllocation;
        hpBudget += hpAllocation;

        //System.out.println("Total influence: " + currentInfluence + "\nVoting Budget: "
//                + voteBudget + "\nBot Budget: " + botBudget + "\nSaving: " + hpBudget);
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
