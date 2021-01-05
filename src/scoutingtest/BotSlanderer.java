package scoutingtest;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class BotSlanderer extends BotController {
    public BotSlanderer(RobotController rc) {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        if (tryMove(Utilities.randomDirection()))
            System.out.println("I moved! And I am " + age + "turns old!");
    }
}
