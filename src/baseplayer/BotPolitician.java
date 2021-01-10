package baseplayer;

import baseplayer.flags.*;
import battlecode.common.*;

import java.util.Optional;

public class BotPolitician extends BotController {
    Optional<MapLocation> enemyLocation;
    Direction scoutingDirection;
    boolean enemyLocIsGuess = true;

    boolean enemyFound = false;
    boolean flagSet = false;
    public BotPolitician(RobotController rc) throws GameActionException {
        super(rc);
        enemyLocation = Optional.empty();
        if (parentLoc.isPresent()){
            scoutingDirection = parentLoc.get().directionTo(rc.getLocation());
            assert scoutingDirection != null;
        }
    }

    public static BotPolitician fromSlanderer(BotSlanderer slanderer) throws GameActionException {
        BotPolitician newPolitician = new BotPolitician(slanderer.rc);
        newPolitician.parentID = slanderer.parentID;
        newPolitician.parentLoc = slanderer.parentLoc;
        return newPolitician;
    }

    @Override
    public BotController run() throws GameActionException {
        if (enemyLocation.isPresent() && rc.getLocation().equals(enemyLocation.get())) {
            enemyLocation = Optional.empty();
        }
        senseNearbyRobots(this::onEnemyNearby, this::onFriendlyNearby, this::onNeutralNearby);
        if (parentID.isPresent()) talkToParent();
        if (enemyLocation.isPresent()){
            nav.bugTo(enemyLocation.get());
        } else {
            if (scoutingDirection != null) {
                nav.spreadOut(scoutingDirection);
            } else {
                Direction random = Utilities.randomDirection();
                nav.spreadOut(random);
            }
        }
        // Search for boundary if we can
        if (Clock.getBytecodesLeft() > 1000 && !enemyFound && !flagSet){
            searchForNearbyBoundaries();
            flagBoundaries();
        }

        enemyFound = false;
        flagSet = false;
        return this;
    }

    // sets enemy loc if !present or if enemy loc is closer
    private void setEnemyLocIfCloser(MapLocation newLoc, boolean isGuess){
        MapLocation currentLoc = rc.getLocation();
        if (!enemyLocation.isPresent()
                || enemyLocIsGuess
                || newLoc.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(enemyLocation.get())){
            enemyLocation = Optional.of(newLoc);
            enemyLocIsGuess = isGuess;
        }
    }

    private void talkToParent() throws GameActionException {
        if (!rc.canGetFlag(parentID.get())){
            // Oh my god our parents died
            parentID = Optional.empty();
        }
        int parentFlag = rc.getFlag(parentID.get());
        if (Flags.addressedForCurrentBot(rc, parentFlag, false)) {
            FlagType parentFlagType = Flags.decodeFlagType(parentFlag);
            switch (parentFlagType) {
                case ENEMY_SPOTTED:
                    EnemySpottedInfo enemySpottedInfo = Flags.decodeEnemySpotted(rc.getLocation(), parentFlag);
                    recordEnemy(enemySpottedInfo);
                    setEnemyLocIfCloser(enemySpottedInfo.location, enemySpottedInfo.isGuess);
                    break;
                default:
                    break;
            }
        }
    }

    private void onEnemyNearby(RobotInfo robotInfo) throws GameActionException {
        setEnemyLocIfCloser(robotInfo.location, false);
        int actionRadius = rc.getType().actionRadiusSquared;
        recordEnemy(new EnemySpottedInfo(robotInfo.location, robotInfo.getType(), false));

        if (!flagSet) {
            rc.setFlag(Flags.encodeEnemySpotted(FlagAddress.ANY, robotInfo.location, robotInfo.getType(), false));
            flagSet = true;
        }
        if (robotInfo.location.distanceSquaredTo(rc.getLocation()) < actionRadius
                && rc.canEmpower(actionRadius)) {
            rc.empower(actionRadius);
        }
    }

    private void onFriendlyNearby(RobotInfo robotInfo) throws GameActionException {
        MapLocation currentLoc = rc.getLocation();
        if (rc.canGetFlag(robotInfo.ID)) {
            int nearbyFlag = rc.getFlag(robotInfo.ID);
            if (Flags.addressedForCurrentBot(rc, nearbyFlag, false)) {
                if (Flags.decodeFlagType(nearbyFlag) == FlagType.ENEMY_SPOTTED) {
                    EnemySpottedInfo enemySpottedInfo = Flags.decodeEnemySpotted(currentLoc, nearbyFlag);
                    recordEnemy(enemySpottedInfo);
                    setEnemyLocIfCloser(enemySpottedInfo.location, enemySpottedInfo.isGuess);
                }
            }
        }
    }

    private void onNeutralNearby(RobotInfo robotInfo) throws GameActionException{
        if (!flagSet) {
            rc.setFlag(Flags.encodeNeutralEcSpotted(FlagAddress.ANY, robotInfo.location, robotInfo.conviction));
            flagSet = true;
        }
    }
}
