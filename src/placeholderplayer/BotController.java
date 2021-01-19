package placeholderplayer;

import battlecode.common.*;

public abstract class BotController {
    int age;
    RobotController rc;
    Team enemy;

    public BotController(RobotController rc) {
        enemy = rc.getTeam().opponent();
        age = 0;
        this.rc = rc;
    }

    /**
     * Runs the bot. When the function ends, Clock.yield() is called from the main thread
     *
     * @throws GameActionException
     */
    public abstract BotController run() throws GameActionException;

    /**
     * Increases the recorded age of the robot by 1
     */
    public void incrementAge(){
        age += 1;
    }

    /**
     * Sense nearby robots
     * Calls the callback functions
     */
    public void senseNearbyRobots(SenseCallback onEnemy, SenseCallback onFriendly, SenseCallback onNeutral) throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        for (RobotInfo robotInfo : rc.senseNearbyRobots()){
            if (robotInfo.team.equals(Team.NEUTRAL)){
                onNeutral.run(robotInfo);
            } else if (robotInfo.team.equals(enemy)){
                onEnemy.run(robotInfo);
            } else {
                onFriendly.run(robotInfo);
            }
        }
    }

}
