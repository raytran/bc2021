package placeholderplayer;

import battlecode.common.*;
import placeholderplayer.nav.NavigationController;

public abstract class BotControllerWalking extends BotController{
    NavigationController nav;
    RobotInfo parentInfoAtSpawn = null;
    public BotControllerWalking(RobotController rc) throws GameActionException {
        super(rc);
        nav = new NavigationController(rc);
        for (Direction dir : Utilities.directions){
            RobotInfo possibleParent = rc.senseRobotAtLocation(rc.getLocation().add(dir));
            if (possibleParent != null){
                if (possibleParent.team == rc.getTeam() && possibleParent.type == RobotType.ENLIGHTENMENT_CENTER) {
                    parentInfoAtSpawn = possibleParent;
                }
            }
        }
    }
}
