package baseplayer.eccontrollers;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import baseplayer.BotEnlightenment;
import baseplayer.eccontrollers.ECController;

public class ECBudgetController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private int voteBudget;
    private int botBudget;
    private int hpBudget;
    private double voteDelta;
    private double botDelta;
    private double hpDelta;


    public ECBudgetController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
    }

    @Override
    public void run() throws GameActionException {
        int currentInfluence = rc.getInfluence();
        this.voteBudget = (int) (0.33 * currentInfluence);
        this.botBudget = (int) (0.33 * currentInfluence);
        this.hpBudget = currentInfluence - this.voteBudget - this.botBudget;
        System.out.println("Total influence: " + currentInfluence + "\nVoting Budget: "
                + this.voteBudget + "\nBot Budget: " + this.botBudget + "\nSaving: " + this.hpBudget);
    }

    public int getVoteBudget() {
        return voteBudget;
    }

    public int getBotBudget() {
        return botBudget;
    }

    public int getHpBudget() {
        return hpBudget;
    }
}
