package baseplayer;

import baseplayer.flags.*;
import battlecode.common.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BotMuckraker extends BotController {
    Optional<MapLocation> enemyLocation;
    Direction scoutingDirection;
    public BotMuckraker(RobotController rc) throws GameActionException {
        super(rc);
        enemyLocation = Optional.empty();
        scoutingDirection = parentLoc.directionTo(rc.getLocation());
        assert scoutingDirection != null;
    }

    @Override
    public BotController run() throws GameActionException {
        Team enemy = rc.getTeam().opponent();

        boolean enemyFound = false;
        // Found a robot ?
        for (RobotInfo robot : rc.senseNearbyRobots()) {
            if (robot.team.equals(enemy)){
                MapLocation robotLoc = robot.getLocation();
                MapLocation delta = new MapLocation(robotLoc.x - parentLoc.x, robotLoc.y - parentLoc.y);
                rc.setFlag(Flags.encodeEnemySpotted(FlagAddress.PARENT_ENLIGHTENMENT_CENTER, delta, robot.getType()));

                if (robot.type == RobotType.POLITICIAN
                        && robot.influence - 10 > robot.getConviction()
                        && robot.location.distanceSquaredTo(rc.getLocation()) < robot.type.actionRadiusSquared) {
                    nav.fuzzyMove(robot.location.directionTo(rc.getLocation()));
                    return this;
                }

                if (rc.canExpose(robot.ID)) {
                    rc.expose(robot.ID);
                }

                enemyFound = true;
            }
        }

        if (!enemyFound && enemyLocation.isPresent() && enemyLocation.get().distanceSquaredTo(rc.getLocation()) < 5) {
            enemyLocation = Optional.empty();
        }

        int parentFlag = rc.getFlag(parentID.get());
        if (Flags.addressedForCurrentBot(rc, parentFlag, false)) {
            FlagType parentFlagType = Flags.decodeFlagType(parentFlag);
            switch (parentFlagType){
                case BOUNDARY_REQUIRED:
                    BoundaryRequiredInfo info = Flags.decodeBoundaryRequired(parentFlag);
                    List<BoundaryType> boundaryDirectionsToSearch = new ArrayList<>();
                    if (!info.northFound) boundaryDirectionsToSearch.add(BoundaryType.NORTH);
                    if (!info.eastFound) boundaryDirectionsToSearch.add(BoundaryType.EAST);
                    if (!info.southFound) boundaryDirectionsToSearch.add(BoundaryType.SOUTH);
                    if (!info.westFound) boundaryDirectionsToSearch.add(BoundaryType.WEST);

                    for (BoundaryType searchBoundaryDirection : boundaryDirectionsToSearch)
                        signalForBoundary(searchBoundaryDirection);
                    break;
                case ENEMY_SPOTTED:
                    if (!enemyLocation.isPresent()){
                        EnemySpottedInfo enemySpottedInfo = Flags.decodeEnemySpotted(parentFlag);
                        enemyLocation = Optional.of(parentLoc.translate(enemySpottedInfo.delta.x, enemySpottedInfo.delta.y));
                    }
                    break;
            }
        }

        if (enemyLocation.isPresent()){
            //System.out.println("GOING TO ENEMY");
            nav.bugTo(enemyLocation.get());
        } else {
            nav.fuzzyMove(scoutingDirection);
        }

        return this;
    }
}
