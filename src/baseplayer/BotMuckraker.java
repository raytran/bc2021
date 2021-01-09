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
        scoutingDirection = parentLoc.get().directionTo(rc.getLocation());
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
                rc.setFlag(Flags.encodeEnemySpotted(FlagAddress.ANY, robotLoc, robot.getType()));

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
                case ENEMY_SPOTTED:
                    if (!enemyLocation.isPresent()){
                        EnemySpottedInfo enemySpottedInfo = Flags.decodeEnemySpotted(rc.getLocation(), parentFlag);
                        enemyLocation = Optional.of(enemySpottedInfo.location);
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

        // Search for boundary if we can
        if (Clock.getBytecodesLeft() > 1000){
            searchForNearbyBoundaries();
            if (!enemyFound) {
                flagBoundaries();
            }
        }
        return this;
    }
}
