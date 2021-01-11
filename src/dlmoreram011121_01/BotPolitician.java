package dlmoreram011121_01;

import battlecode.common.*;
import dlmoreram011121_01.flags.EnemySpottedInfo;
import dlmoreram011121_01.flags.FlagAddress;
import dlmoreram011121_01.flags.FlagType;
import dlmoreram011121_01.flags.Flags;

import java.util.Optional;

public class BotPolitician extends BotController {
    Optional<MapLocation> targetLocation;
    Direction scoutingDirection;
    double bestTargetScore = 0;
    int totalNearbyEnemyConviction = 0;
    boolean targetLocIsGuess = true;

    boolean enemyFound = false;
    boolean flagSet = false;
    public BotPolitician(RobotController rc) throws GameActionException {
        super(rc);
        targetLocation = Optional.empty();
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
        if (targetLocation.isPresent() && rc.getLocation().equals(targetLocation.get())) {
            targetLocation = Optional.empty();
            bestTargetScore = 0;
        }
        senseNearbyRobots(this::onEnemyNearby, this::onFriendlyNearby, this::onNeutralNearby);



        if (parentID.isPresent()) talkToParent();
        if (targetLocation.isPresent()){
            nav.bugTo(targetLocation.get());
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

        totalNearbyEnemyConviction = 0;
        enemyFound = false;
        flagSet = false;
        return this;
    }

    // sets enemy loc if !present or if enemy loc is closer
    private void setTargetLocIfBetter(Team targetTeam, MapLocation newLoc, RobotType type,  boolean isGuess){
        if (!targetLocation.isPresent()
                || targetLocIsGuess
                || scoreTarget(targetTeam, newLoc, type) > bestTargetScore) {
            targetLocation = Optional.of(newLoc);
            targetLocIsGuess = isGuess;
            bestTargetScore = scoreTarget(targetTeam, newLoc, type);
        }
    }

    private double scoreTarget(Team targetTeam, MapLocation location, RobotType type) {
        double distNorm = ((double) rc.getLocation().distanceSquaredTo(location) / (double) (64 * 64));
        double typeMulti = 0;
        switch (type){
            case POLITICIAN:
                typeMulti = 0.55;
                break;
            case MUCKRAKER:
                typeMulti = 0.5;
                break;
            case ENLIGHTENMENT_CENTER:
                typeMulti = 0.7;
                break;
            case SLANDERER:
                typeMulti = 0.4;
                break;
        }
        return (1 - distNorm) * typeMulti + (targetTeam.equals(Team.NEUTRAL) ? 0.4 : 0);
    }

    private void talkToParent() throws GameActionException {
        if (!rc.canGetFlag(parentID.get())){
            // Oh my god our parents died
            parentID = Optional.empty();
            return;
        }
        int parentFlag = rc.getFlag(parentID.get());
        if (Flags.addressedForCurrentBot(rc, parentFlag, false)) {
            FlagType parentFlagType = Flags.decodeFlagType(parentFlag);
            switch (parentFlagType) {
                case ENEMY_SPOTTED:
                    EnemySpottedInfo enemySpottedInfo = Flags.decodeEnemySpotted(rc.getLocation(), parentFlag);
                    recordEnemy(enemySpottedInfo);
                    setTargetLocIfBetter(rc.getTeam().opponent(), enemySpottedInfo.location, enemySpottedInfo.enemyType, enemySpottedInfo.isGuess);
                    break;
                default:
                    break;
            }
        }
    }

    private void onEnemyNearby(RobotInfo robotInfo) throws GameActionException {
        setTargetLocIfBetter(robotInfo.team, robotInfo.location, robotInfo.type, false);
        recordEnemy(new EnemySpottedInfo(robotInfo.location, robotInfo.getType(), false));

        if (!flagSet) {
            rc.setFlag(Flags.encodeEnemySpotted(FlagAddress.ANY, robotInfo.location, robotInfo.getType(), false));
            flagSet = true;
        }

        int actionRadius = rc.getType().actionRadiusSquared;
        if (robotInfo.location.distanceSquaredTo(rc.getLocation()) < actionRadius){
            totalNearbyEnemyConviction += robotInfo.conviction;
            if (rc.canEmpower(actionRadius) && totalNearbyEnemyConviction > 3) {
                rc.empower(actionRadius);
            }
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
                    setTargetLocIfBetter(rc.getTeam().opponent(), enemySpottedInfo.location, enemySpottedInfo.enemyType, enemySpottedInfo.isGuess);
                }
            }
        }
    }

    private void onNeutralNearby(RobotInfo robotInfo) throws GameActionException {
        if (!flagSet) {
            rc.setFlag(Flags.encodeNeutralEcSpotted(FlagAddress.ANY, robotInfo.location, robotInfo.conviction));
            flagSet = true;
        }
        setTargetLocIfBetter(Team.NEUTRAL, robotInfo.location, robotInfo.type, false);

        int actionRadius = rc.getType().actionRadiusSquared;
        if (robotInfo.location.distanceSquaredTo(rc.getLocation()) < actionRadius
                && rc.canEmpower(actionRadius)) {
            rc.empower(actionRadius);
        }
    }
}
