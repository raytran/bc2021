package baseplayermicro;

import baseplayermicro.flags.Flags;
import battlecode.common.*;
import baseplayermicro.GroupInfo;

public class BotSlanderer extends BotController {
    Direction awayFromEnemy;
    int closest = Integer.MAX_VALUE;
    boolean flagSet = false;
    public BotSlanderer(RobotController rc) throws GameActionException {
        super(rc);
        awayFromEnemy = Utilities.randomDirection();
    }

    @Override
    public BotController run() throws GameActionException {
        parentID.ifPresent(this::setGroup);
        Direction targetDir = Direction.CENTER;
        senseNearbyRobots(this::onNearbyEnemy, this::onNearbyFriendly, this::onNearbyNeutral);
        nav.spreadOut(awayFromEnemy);
        if (rc.getType() == RobotType.POLITICIAN){
            // Just converted; return a politician controller instead
            return BotPolitician.fromSlanderer(this);
        }
        int groupMembers = 0;
        int groupDist = 0;
        for (RobotInfo robotInfo : rc.senseNearbyRobots()){
            if (robotInfo.team.equals(rc.getTeam().opponent())){
            } else {
                GroupInfo groupInfo = Flags.decodeGroupFlag(rc.getFlag(robotInfo.ID));
                if (groupInfo.group == getGroup()){
                    groupDist = Math.max(rc.getLocation().distanceSquaredTo(robotInfo.location), groupDist);
                    groupMembers += 1;

                    if (groupInfo.dir != Direction.CENTER) targetDir = groupInfo.dir;
                }
            }
        }

        if (groupMembers == 2 && groupDist < 7)
            if (targetDir != Direction.CENTER)
                nav.fuzzyMove(targetDir);
            else
                nav.fuzzyMove(parentLoc.get().directionTo(rc.getLocation()));

        System.out.println("I AM IN GROUP " + getGroup() + " WITH " + groupMembers);
        age += 1;
        rc.setFlag(getGroup());

        return this;
    }

    private void onNearbyEnemy(RobotInfo robotInfo) throws GameActionException {
        if (rc.getLocation().distanceSquaredTo(robotInfo.location) < closest){
            awayFromEnemy = robotInfo.location.directionTo(rc.getLocation());
            closest = rc.getLocation().distanceSquaredTo(robotInfo.location);
        }
        if (!flagSet) {
            rc.setFlag(Flags.encodeEnemySpotted(rc.getRoundNum(), robotInfo.location, robotInfo.getType(), false));
            flagSet = true;
        }
    }

    private void onNearbyFriendly(RobotInfo robotInfo) throws GameActionException {

    }

    private void onNearbyNeutral(RobotInfo robotInfo) throws GameActionException {
        if (!flagSet) {
            rc.setFlag(Flags.encodeNeutralEcSpotted(rc.getRoundNum(), robotInfo.location, robotInfo.conviction));
            flagSet = true;
        }
    }

}
