package dlmoreram010921;

import dlmoreram010921.flags.*;
import battlecode.common.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BotMuckraker extends BotController {
    Optional<MapLocation> enemyLocation;
    Direction scoutingDirection;
    boolean enemyLocIsGuess = true;
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
        RobotInfo nearestPolitician = null;
        int nearestPoliticianDist = Integer.MAX_VALUE;
        for (RobotInfo robotInfo : rc.senseNearbyRobots()) {
            if (robotInfo.type == RobotType.POLITICIAN
                    && (nearestPolitician == null || nearestPoliticianDist < currentLoc.distanceSquaredTo(robotInfo.location)) ) {
                nearestPolitician = robotInfo;
                nearestPoliticianDist = currentLoc.distanceSquaredTo(robotInfo.location);
            }
            if (robotInfo.getTeam().equals(rc.getTeam())) {
                //Nearby friendly
                if (rc.canGetFlag(robotInfo.ID)) {
                    int nearbyFlag = rc.getFlag(robotInfo.ID);
                    if (Flags.addressedForCurrentBot(rc, nearbyFlag, false)) {
                        if (Flags.decodeFlagType(nearbyFlag) == FlagType.ENEMY_SPOTTED) {
                            EnemySpottedInfo enemySpottedInfo = Flags.decodeEnemySpotted(currentLoc, nearbyFlag);
                            recordEnemy(enemySpottedInfo);
                            setEnemyLocIfCloser(enemySpottedInfo.location, robotInfo.type, enemySpottedInfo.isGuess);
                        }
                    }
                }
            } else {
                //Nearby enemy
                enemyFound = true;
                setEnemyLocIfCloser(robotInfo.location, robotInfo.type, false);
                int actionRadius = rc.getType().actionRadiusSquared;
                recordEnemy(new EnemySpottedInfo(robotInfo.location, robotInfo.getType(), false));

                if (!flagSet) {
                    rc.setFlag(Flags.encodeEnemySpotted(FlagAddress.ANY, robotInfo.location, robotInfo.getType(), false));
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
                        setEnemyLocIfCloser(enemySpottedInfo.location, enemySpottedInfo.enemyType, enemySpottedInfo.isGuess);
                        break;
                    default:
                        break;
                }
            }
        }

        if (nearestPolitician != null && nearestPoliticianDist < RobotType.POLITICIAN.detectionRadiusSquared) {
            nav.fuzzyMove(nearestPolitician.location.directionTo(currentLoc));
        } else if (enemyLocation.isPresent()) {
            nav.bugTo(enemyLocation.get());
        } else {
            nav.spreadOut(scoutingDirection);
        }

        // Search for boundary if we can
        if (Clock.getBytecodesLeft() > 1000 && !enemyFound){
            searchForNearbyBoundaries();
            flagBoundaries();
        }
        return this;
    }

    // sets enemy loc if !present or if enemy loc is closer
    private void setEnemyLocIfCloser(MapLocation newLoc, RobotType rt, boolean isGuess){
        MapLocation currentLoc = rc.getLocation();
        if (!enemyLocation.isPresent()
                || enemyLocIsGuess
                || (newLoc.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(enemyLocation.get())
                    && rt == RobotType.SLANDERER)){
            enemyLocation = Optional.of(newLoc);
            enemyLocIsGuess = isGuess;
        }
    }
}
