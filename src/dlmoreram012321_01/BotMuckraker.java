package dlmoreram012321_01;

import dlmoreram012321_01.flags.*;
import battlecode.common.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BotMuckraker extends BotController {
    // Circle
    int currentRadius = 3;
    MapLocation circleTargetLoc;
    List<MapLocation> circleLocs;
    boolean isDefending;

    int mostRecentEnemyReportRebroadcastTimestamp = 0;
    int mostRecentEnemyReportRebroadcast = 0;

    Optional<MapLocation> targetLocation;
    double bestTargetScore = 0;
    Direction scoutingDirection;
    boolean enemyFound = false;
    boolean flagSet = false;
    boolean targetLocIsGuess = true;
    boolean isScout;

    int thisRoundNearbyEnemyCount = 0;
    int thisRoundNearbyNeutralCount = 0;
    int thisRoundNearbyFriendlyCount = 0;
    public BotMuckraker(RobotController rc) throws GameActionException {
        super(rc);
        isScout = rc.getRoundNum() < 25;
        targetLocation = Optional.empty();
        scoutingDirection = parentLoc.get().directionTo(rc.getLocation());
        if (isDefending) {
            circleLocs = Utilities.getFilteredCircleLocs(1,parentLoc.get().x, parentLoc.get().y, parentLoc.get(), currentRadius);
        }
        assert scoutingDirection != null;
    }

    @Override
    public BotController run() throws GameActionException {
        if (targetLocation.isPresent() && rc.getLocation().distanceSquaredTo(targetLocation.get()) < rc.getType().actionRadiusSquared - 5) {
            targetLocation = Optional.empty();
        }
        senseNearbyRobots(this::onEnemyNearby, this::onFriendlyNearby, this::onNeutralNearby);
        if (parentID.isPresent()) talkToParent();


        if (targetLocation.isPresent() && rc.getLocation().distanceSquaredTo(targetLocation.get()) < rc.getType().actionRadiusSquared) {
            if (thisRoundNearbyEnemyCount == 0 && thisRoundNearbyNeutralCount == 0) {
                rc.setFlag(Flags.encodeAreaClear(rc.getRoundNum(), targetLocation.get()));
                flagSet = true;
                targetLocation = Optional.empty();
            }
        }
        //if (isDefending){
         //   runCircleDefense();
        //} else {
            if (targetLocation.isPresent() && !isScout){
                nav.moveTo(targetLocation.get());
                //nav.bugTo(enemyLocation.get());
            } else {
                if (scoutingDirection != null) {
                    if (!rc.onTheMap(rc.getLocation().add(scoutingDirection))
                        || rc.isLocationOccupied(rc.getLocation().add(scoutingDirection))){
                        scoutingDirection = Utilities.randomDirection();
                    }
                    if (rc.canMove(scoutingDirection)){
                        rc.move(scoutingDirection);
                    }
                } else {
                    Direction random = Utilities.randomDirection();
                    nav.spreadOut(random);
                }
            }
        //}

        // Search for boundary if we can
        if (Clock.getBytecodesLeft() > 1000 && !enemyFound && !flagSet){
            searchForNearbyBoundaries();
            flagBoundaries();
        }


        if (!flagSet){
            //System.out.println("REBROADCAST");
            rc.setFlag(mostRecentEnemyReportRebroadcast);
        }

        enemyFound = false;
        flagSet = false;
        thisRoundNearbyEnemyCount = 0;
        thisRoundNearbyNeutralCount = 0;
        thisRoundNearbyFriendlyCount = 0;
        return this;
    }

    // sets enemy loc if !present or if enemy loc is closer
    /*private void setEnemyLocIfCloser(MapLocation newLoc, RobotType rt, boolean isGuess) {
        if (rt == RobotType.SLANDERER || rt == RobotType.POLITICIAN){
            MapLocation currentLoc = rc.getLocation();
            if (!enemyLocation.isPresent()
                    || enemyLocIsGuess
                    || (newLoc.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(enemyLocation.get()))) {
                enemyLocation = Optional.of(newLoc);
                enemyLocIsGuess = isGuess;
            }
        }
    }*/

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
        switch (type) {
            case POLITICIAN:
                typeMulti = 0.5;
                break;
            case MUCKRAKER:
                typeMulti = 0;
                break;
            case ENLIGHTENMENT_CENTER:
                typeMulti = 0.3;
                break;
            case SLANDERER:
                typeMulti = 0.7;
                break;
        }
        return (1 - distNorm) * typeMulti + (targetTeam.equals(Team.NEUTRAL) ? -1 : 0);
    }


    private void onEnemyNearby(RobotInfo robotInfo) throws GameActionException {
        thisRoundNearbyEnemyCount += 1;
        setTargetLocIfBetter(robotInfo.getTeam(), robotInfo.location, robotInfo.getType(), false);
        int actionRadius = rc.getType().actionRadiusSquared;
        //recordEnemy(new EnemySpottedInfo(rc.getRoundNum(), robotInfo.location, robotInfo.getType(), false));

        if (!flagSet) {
            rc.setFlag(Flags.encodeEnemySpotted(rc.getRoundNum(), robotInfo.location, robotInfo.getType(), false));
            flagSet = true;
        }
        if (robotInfo.location.distanceSquaredTo(rc.getLocation()) < actionRadius
                && rc.canExpose(robotInfo.ID)) {
            rc.expose(robotInfo.ID);
        }
    }

    private void onFriendlyNearby(RobotInfo robotInfo) throws GameActionException {
        thisRoundNearbyFriendlyCount += 1;
        MapLocation currentLoc = rc.getLocation();
        if (rc.canGetFlag(robotInfo.ID)) {
            int nearbyFlag = rc.getFlag(robotInfo.ID);
            switch (Flags.decodeFlagType(nearbyFlag)){
                case ENEMY_SPOTTED:
                    EnemySpottedInfo enemySpottedInfo = Flags.decodeEnemySpotted(currentLoc, nearbyFlag);
                    if (mostRecentEnemyReportRebroadcastTimestamp <= enemySpottedInfo.timestamp){
                        mostRecentEnemyReportRebroadcastTimestamp = enemySpottedInfo.timestamp;
                        mostRecentEnemyReportRebroadcast = nearbyFlag;
                    }else{
                        // Check if this is too old to consider
                        if (mostRecentEnemyReportRebroadcastTimestamp - enemySpottedInfo.timestamp > Flags.REBROADCAST_ROUND_LIMIT) {
                            //System.out.println("TOO OLD!");
                            return;
                        }
                    }
                    //recordEnemy(enemySpottedInfo);
                    setTargetLocIfBetter(rc.getTeam().opponent(), enemySpottedInfo.location, enemySpottedInfo.enemyType, enemySpottedInfo.isGuess);
                    break;
                case AREA_CLEAR:
                    if (targetLocation.isPresent()){
                        AreaClearInfo areaClearInfo = Flags.decodeAreaClear(currentLoc, nearbyFlag);
                        if (areaClearInfo.location.distanceSquaredTo(targetLocation.get()) < 5) {
                            targetLocation = Optional.empty();
                            //System.out.println("CLEARING TARGET");
                        }
                    }
                    break;
            }
        }
    }

    private void onNeutralNearby(RobotInfo robotInfo) throws GameActionException{
        thisRoundNearbyNeutralCount += 1;
        if (!flagSet){
            //System.out.println("FLAGGING NEUTRAl");
            rc.setFlag(Flags.encodeNeutralEcSpotted(rc.getRoundNum(), robotInfo.location, robotInfo.conviction));
            flagSet = true;
        }
    }

    private void talkToParent() throws GameActionException {
        if (!rc.canGetFlag(parentID.get())){
            // Oh my god our parents died
            parentID = Optional.empty();
            return;
        }
        int parentFlag = rc.getFlag(parentID.get());
        FlagType parentFlagType = Flags.decodeFlagType(parentFlag);
        switch (parentFlagType) {
            case ENEMY_SPOTTED:
                EnemySpottedInfo enemySpottedInfo = Flags.decodeEnemySpotted(rc.getLocation(), parentFlag);
                //recordEnemy(enemySpottedInfo);
                setTargetLocIfBetter(rc.getTeam().opponent(), enemySpottedInfo.location, enemySpottedInfo.enemyType, enemySpottedInfo.isGuess);
                break;
            default:
                break;
        }
    }

    private void runCircleDefense() throws GameActionException {
        if (circleLocs.size() == 0){
            //System.out.println("CIRCLE DONE");
            currentRadius = currentRadius +3;
            circleLocs =
                    Utilities.getFilteredCircleLocs(1, parentLoc.get().x, parentLoc.get().y, parentLoc.get(), currentRadius);
        }
        if (circleTargetLoc == null
                || (rc.getLocation().distanceSquaredTo(circleTargetLoc) < rc.getType().sensorRadiusSquared
                && (!rc.onTheMap(circleTargetLoc) || rc.isLocationOccupied(circleTargetLoc) && !rc.getLocation().equals(circleTargetLoc)))
        ){
            circleLocs.remove(circleTargetLoc);
            int closest = Integer.MAX_VALUE;
            for (MapLocation loc : circleLocs) {
                if (loc.distanceSquaredTo(rc.getLocation()) < closest){
                    closest = loc.distanceSquaredTo(rc.getLocation());
                    circleTargetLoc = loc;
                }
            }
        }else{
            int closest = Integer.MAX_VALUE;
            for (MapLocation loc : circleLocs) {
                if (loc.distanceSquaredTo(rc.getLocation()) < closest){
                    closest = loc.distanceSquaredTo(rc.getLocation());
                    circleTargetLoc = loc;
                }
            }
            //nav.bugTo(circleTargetLoc);

            nav.moveTo(circleTargetLoc);
            //if (!nav.moveTo(circleTargetLoc)){
            //    circleTargetLoc = null;
            //}
            //if (!nav.bugAndDijkstraTo(circleTargetLoc)){
            //    circleTargetLoc = null;
            //}
        }
    }
}
