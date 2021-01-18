package baseplayer3.eccontrollers;

import baseplayer3.BotEnlightenment;
import battlecode.common.RobotController;

public class ECTargetController {
    private final BotEnlightenment ec;
    private final RobotController rc;
    private final int SE_THRESHOLD = 100;
    private final double INF_THRESHOLD = 0.5;
    private double se;
    private double avgSE;
    private double avgInf;
    private double avgBot;
    private boolean canSpawn;
    private int voteTarget;
    private int botTarget;
    private int hpTarget;

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
        voteTarget = rc.getTeamVotes() + 1;
        botTarget = ec.getLocalRobotCount() + 1;
        hpTarget = (int) (0.05 * rc.getRoundNum()); // not sure what's a better system for this one
        int hpChange = 0;
        if (se > SE_THRESHOLD) {
            botTarget++;
            hpChange--;
        } else if (se > 0) {
            voteTarget--;
            botTarget++;
        } else {
            voteTarget = 0;
        } if (avgSE > SE_THRESHOLD) {
            voteTarget++;
            hpChange--;
        } else if (avgSE > 0) {
            voteTarget--;
            botTarget++;
        } else {
            if (canSpawn) {
                voteTarget = 0;
                botTarget++;
            } else {
                voteTarget++;
                botTarget = 0;
            }
        } if (avgInf > INF_THRESHOLD) {
            botTarget++;
            hpChange--;
        } else if (avgInf > 0) {
            voteTarget--;
            botTarget++;
        } else {
            voteTarget = 0;
            botTarget++;
            hpChange++;
        } if (avgBot > 0) {
            voteTarget++;
            hpChange--;
        } else {
            voteTarget--;
            botTarget++;
        }
        voteTarget = Math.max(0, voteTarget);
        botTarget = Math.max(0, botTarget);
        hpTarget = Math.max(0, hpTarget + hpChange * 15);
    }

    public int getVoteTarget() { return voteTarget; }

    public int getBotTarget() { return botTarget; }

    public int getHpTarget() { return hpTarget; }
}
