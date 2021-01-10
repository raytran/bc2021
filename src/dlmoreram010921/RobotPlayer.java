package dlmoreram010921;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public strictfp class RobotPlayer {
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        ////System.out.println("Just spawned " + rc.getType() + "! @Location " + rc.getLocation());
        BotController controller;
        switch (rc.getType()) {
            case ENLIGHTENMENT_CENTER:
                controller = new BotEnlightenment(rc);
                break;
            case POLITICIAN:
                controller = new BotPolitician(rc);
                break;
            case SLANDERER:
                controller = new BotSlanderer(rc);
                break;
            case MUCKRAKER:
                controller = new BotMuckraker(rc);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + rc.getType());
        }

        while (true) {
            try {
                controller = controller.run();
                controller.incrementAge();
                Clock.yield();
            } catch (Exception e) {
                //System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }
}
