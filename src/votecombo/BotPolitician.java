package votecombo;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

public class BotPolitician extends BotController {
    public BotPolitician(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        int actionRadius = rc.getType().actionRadiusSquared;
        RobotInfo[] attackable = rc.senseNearbyRobots(actionRadius, enemy);
        int numEnemies = attackable.length;
        int convictionLeft = rc.getConviction() - 10;
        if (attackable.length != 0 && convictionLeft > 0) {
            for(RobotInfo robot : attackable){
                if(convictionLeft / numEnemies > robot.getConviction()){
                    rc.empower(actionRadius);
                }
            }
        }
        else{
            nav.bugTo(rc.getLocation().translate(1,1));
        }
    }
}
