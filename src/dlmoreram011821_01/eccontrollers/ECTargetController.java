package dlmoreram011821_01.eccontrollers;

import battlecode.common.RobotController;
import dlmoreram011821_01.BotEnlightenment;
import dlmoreram011821_01.Utilities;

public class ECTargetController {
    private final BotEnlightenment ec;
    private final RobotController rc;
    private final int SE_THRESHOLD = 100;
    private final double INF_THRESHOLD = 0.125;
    private double se;
    private double avgSE;
    private double avgInf;
    private double avgBot;
    private boolean canSpawn;
    private double voteTarget;
    private double botTarget;
    private double hpTarget;

    public ECTargetController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
    }

    public void updateHeuristics() {
        se = ec.getSafetyEval();
        avgSE = ec.getAvgSafetyEval();
        avgInf = ec.getAvgInfluenceChange();
        avgBot = ec.getAvgBotChange();
        canSpawn = ec.getCanSpawn();
    }

    public void updateTargets() {
        int currentInf = rc.getInfluence();
        voteTarget = rc.getTeamVotes();
        botTarget = ec.getLocalRobotCount() + 1;
        hpTarget = 0.1 * currentInf; // not sure what's a better system for this one
        int hpChange = 0;
        if (se > SE_THRESHOLD) {
            botTarget += 1;
            voteTarget += 1;
            hpChange -= 1;
        } else if (se > 0) {
            voteTarget -= 1;
            botTarget += 1;
        } else {
            voteTarget = 0;
        } if (avgSE > SE_THRESHOLD) {
            voteTarget += 1;
            hpChange -= 1;
        } else if (avgSE > 0) {
            botTarget += 1;
        } else {
            if (canSpawn) {
                voteTarget = 0;
                botTarget += 1;
            } else {
                voteTarget += 1;
                botTarget = 0;
            }
        } if (avgInf > INF_THRESHOLD) {
            voteTarget += 1;
            hpChange -= 1;
        } else if (avgInf > 0) {
            botTarget += 1;
        } if (avgBot > 0) {
            voteTarget += 1;
            hpChange -= 1;
        } else {
            voteTarget -= 1;
            botTarget += 1;
        }
        double voteBonus =  rc.getRoundNum() > 550 ? (Utilities.VOTE_WIN - rc.getTeamVotes()) * rc.getRoundNum()/ Utilities.MAX_ROUND : 0;
        voteTarget = Math.max(0, voteTarget) + voteBonus;
        botTarget = Math.max(0, botTarget);
        hpTarget = Math.max(0, hpTarget + hpChange/100. * currentInf);
    }

    public double getVoteTarget() { return voteTarget; }

    public double getBotTarget() { return botTarget; }

    public double getHpTarget() { return hpTarget; }
}
