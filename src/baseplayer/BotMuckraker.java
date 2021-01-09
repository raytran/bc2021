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
        MapLocation currentLoc = rc.getLocation();
        boolean enemyFound = false;
        boolean flagSet = false;

        if (enemyLocation.isPresent() &&  currentLoc.equals(enemyLocation.get())){
            enemyLocation = Optional.empty();
        }
        for (RobotInfo robotInfo : rc.senseNearbyRobots()) {
            if (robotInfo.getTeam().equals(rc.getTeam())) {
                //Nearby friendly
                if (rc.canGetFlag(robotInfo.ID)) {
                    int nearbyFlag = rc.getFlag(robotInfo.ID);
                    if (Flags.addressedForCurrentBot(rc, nearbyFlag, false)) {
                        if (Flags.decodeFlagType(nearbyFlag) == FlagType.ENEMY_SPOTTED) {
                            //System.out.println("NEARBY FRIENDLY REPORTING ENEMY");
                            EnemySpottedInfo enemySpottedInfo = Flags.decodeEnemySpotted(currentLoc, nearbyFlag);
                            recordEnemy(enemySpottedInfo);
                            setEnemyLocIfCloser(enemySpottedInfo.location);
                        }
                    }
                }
            } else {
                //Nearby enemy
                enemyFound = true;
                setEnemyLocIfCloser(robotInfo.location);
                int actionRadius = rc.getType().actionRadiusSquared;
                recordEnemy(new EnemySpottedInfo(robotInfo.location, robotInfo.getType()));

                if (!flagSet) {
                    rc.setFlag(Flags.encodeEnemySpotted(FlagAddress.ANY, robotInfo.location, robotInfo.getType()));
                    flagSet = true;
                }
                //TODO more advanced stuff later
                if (robotInfo.type == RobotType.SLANDERER && robotInfo.location.distanceSquaredTo(currentLoc) < actionRadius
                        && rc.canExpose(robotInfo.ID)) {
                    rc.expose(robotInfo.ID);
                }
            }
        }

        if (parentID.isPresent()) {
            if (!rc.canGetFlag(parentID.get())){
                // Oh my god our parents died
                parentID = Optional.empty();
            }
            int parentFlag = rc.getFlag(parentID.get());
            if (Flags.addressedForCurrentBot(rc, parentFlag, false)) {
                FlagType parentFlagType = Flags.decodeFlagType(parentFlag);
                switch (parentFlagType){
                    case ENEMY_SPOTTED:
                        EnemySpottedInfo enemySpottedInfo = Flags.decodeEnemySpotted(rc.getLocation(), parentFlag);
                        recordEnemy(enemySpottedInfo);
                        setEnemyLocIfCloser(enemySpottedInfo.location);
                        break;
                    default:
                        break;
                }
            }
        }

        if (enemyLocation.isPresent()){
            //System.out.println("GOING TO ENEMY");
            nav.bugTo(enemyLocation.get());
        } else {
            nav.fuzzyMove(scoutingDirection);
        }

        // Search for boundary if we can
        if (Clock.getBytecodesLeft() > 1000 && !enemyFound){
            searchForNearbyBoundaries();
            flagBoundaries();
        }
        return this;
    }

    // sets enemy loc if !present or if enemy loc is closer
    private void setEnemyLocIfCloser(MapLocation newLoc){
        MapLocation currentLoc = rc.getLocation();
        if (!enemyLocation.isPresent()
                || newLoc.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(enemyLocation.get())){
            enemyLocation = Optional.of(newLoc);
        }
    }
}
