package baseplayer2.eccontrollers;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import baseplayer2.BotEnlightenment;
import baseplayer2.Utilities;

public class ECBudgetController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final ECTargetController tc;
    private final PIDBudgetVariable voteDelta = new PIDBudgetVariable(13.523940155406732, 2.4530253571083382, 0.0005292761350429615);
    private final PIDBudgetVariable botDelta = new PIDBudgetVariable(0.0006567908358610917, 0.0033456354379550013, 0.04452756892096015);
    private final PIDBudgetVariable hpDelta = new PIDBudgetVariable(0.0015652349097965413, 7.942558895131163, 0.7614312978308346);

    private int voteBudget;
    private int botBudget;
    private int hpBudget;


    public ECBudgetController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
        this.tc = new ECTargetController(rc, ec);
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
            tc.updateHeuristics();
            tc.updateTargets();

            double voteTarget = tc.getVoteTarget();
            double botTarget = tc.getBotTarget();
            double hpTarget = tc.getHpTarget();

            if (hpBudget > hpTarget) {
                int diff = (int) (hpBudget - hpTarget);
                hpBudget -= diff;
                income += diff;
            }

            // check
            //System.out.println("Game state vars:\nSafety Eval: " + ec.getSafetyEval() + "\nAvg Safety: " + ec.getAvgSafetyEval()
            //+ "\nAvg Influence Change: " + ec.getAvgInfluenceChange() + "\nAvg Bot Change: " + ec.getAvgBotChange());

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
            //System.out.println("Deltas:\nVote: " + voteDValue + "\nBot: " + botDValue + "\nHP: " + hpDValue);
            double total = voteDValue + botDValue + hpDValue;
            if (total > 0) {
                voteDValue *= 1. / total;
                botDValue *= 1. / total;
            } else {
                voteDValue = 0;
                botDValue = 1;
            }
            //System.out.println("Normalized Deltas:\n" + "Vote: " + voteDValue + "\nBot: " + botDValue);

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

            //System.out.println("Total influence: " + currentInfluence + "\nVoting Budget: "
                    //+ voteBudget + "\nBot Budget: " + botBudget + "\nSaving: " + hpBudget);
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
