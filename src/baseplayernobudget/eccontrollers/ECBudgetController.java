package baseplayernobudget.eccontrollers;

import baseplayernobudget.BotEnlightenment;
import baseplayernobudget.Utilities;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class ECBudgetController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final ECTargetController tc;
    private final PIDBudgetVariable voteDelta = new PIDBudgetVariable(3.683225393295288, 12.875300407409668, 25.46172523498535);
    private final PIDBudgetVariable botDelta = new PIDBudgetVariable(8.831119537353516, 0.5120320916175842, 20.05985450744629);
    private final PIDBudgetVariable hpDelta = new PIDBudgetVariable( 0.050030481070280075, 0.07245972752571106, 0.29944539070129395);

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
            voteBudget = 1;
        }

        int income = currentInfluence - voteBudget - botBudget - hpBudget;
        System.out.println("Income is: " + income);
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
            System.out.println("Game state vars:\nSafety Eval: " + ec.getSafetyEval() + "\nAvg Safety: " + ec.getAvgSafetyEval()
            + "\nAvg Influence Change: " + ec.getAvgInfluenceChange() + "\nAvg Bot Change: " + ec.getAvgBotChange());

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
                assert income >= voteAllocation + botAllocation + hpAllocation;
            } catch (AssertionError e) {
                voteAllocation = (int) Math.floor(voteDValue * income);
                botAllocation = (int) Math.floor(botDValue * income);
                hpAllocation = income - voteAllocation - botAllocation;
            }

            //if there are nearby enemy politicians, make sure we have enough hp
            /**double safetyEval = ec.getSafetyEval();
            if (hpBudget <= safetyEval && hpBudget != 0) {
                if (currentInfluence > safetyEval + 1) {
                    hpBudget = (int) safetyEval + 1;
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
            } else {
                voteBudget += voteAllocation;
                botBudget += botAllocation;
            }

            if(voteBudget > 2 * ec.getPrevVoteAmount() * ec.getPrevVoteMult() + 1) {
                int diff = voteBudget - 2 * ec.getPrevVoteAmount() * ec.getPrevVoteMult() - 1;
                voteBudget = 2 * ec.getPrevVoteAmount() * ec.getPrevVoteMult() + 1;
                botBudget += diff;
            }

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
