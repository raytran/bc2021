package baseplayer;

import baseplayer.flags.Flags;
import battlecode.common.*;

public class BotMuckraker extends BotController {
    Direction scoutingDirection;
    MapLocation parentLoc;
    public BotMuckraker(RobotController rc) {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        MapLocation myLoc = rc.getLocation();
        //Find where our parent is
        if (age == 0) {
            for (MapLocation neighbor : Utilities.getPossibleNeighbors(myLoc)) {
                RobotInfo robotHere = rc.senseRobotAtLocation(neighbor);
                if (robotHere != null && robotHere.type.equals(RobotType.ENLIGHTENMENT_CENTER)) {
                    parentLoc = neighbor;
                    scoutingDirection = parentLoc.directionTo(myLoc);
                    break;
                }
            }

            assert parentLoc != null;
            assert scoutingDirection != null;
        }
        Team enemy = rc.getTeam().opponent();
        //Found a robot
        for (RobotInfo robot : rc.senseNearbyRobots()) {
            if (robot.team.equals(enemy)){
                MapLocation robotLoc = robot.getLocation();
                MapLocation delta = new MapLocation(robotLoc.x - parentLoc.x, robotLoc.y - parentLoc.y);
                rc.setFlag(Flags.encodeEnemySpotted(delta, robot.getType()));
                return;
            }
        }

        tryMove(scoutingDirection);
    }
}
