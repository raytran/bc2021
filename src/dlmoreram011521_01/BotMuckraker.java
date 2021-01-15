package dlmoreram011521_01;

import battlecode.common.*;
import dlmoreram011521_01.flags.AreaClearInfo;
import dlmoreram011521_01.flags.EnemySpottedInfo;
import dlmoreram011521_01.flags.FlagType;
import dlmoreram011521_01.flags.Flags;

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

    Optional<MapLocation> enemyLocation;
    Direction scoutingDirection;
    boolean enemyFound = false;
    boolean flagSet = false;
    boolean enemyLocIsGuess = true;

    int thisRoundNearbyEnemyCount = 0;
    int thisRoundNearbyNeutralCount = 0;
    int thisRoundNearbyFriendlyCount =0;
    public BotMuckraker(RobotController rc) throws GameActionException {
        super(rc);
        isDefending = rc.getRoundNum() > 100 && Math.random() > Math.pow((double) rc.getRoundNum() / 3000, 0.5);
        enemyLocation = Optional.empty();
        scoutingDirection = parentLoc.get().directionTo(rc.getLocation());
        if (isDefending) {
            circleLocs = Utilities.getFilteredCircleLocs(1,parentLoc.get().x, parentLoc.get().y, parentLoc.get(), currentRadius);
        }
        assert scoutingDirection != null;
    }

    @Override
    public BotController run() throws GameActionException {
        if (enemyLocation.isPresent() && rc.getLocation().distanceSquaredTo(enemyLocation.get()) < rc.getType().actionRadiusSquared - 5) {
            enemyLocation = Optional.empty();
        }
        senseNearbyRobots(this::onEnemyNearby, this::onFriendlyNearby, this::onNeutralNearby);
        if (parentID.isPresent()) talkToParent();


        if (enemyLocation.isPresent() && rc.getLocation().distanceSquaredTo(enemyLocation.get()) < rc.getType().actionRadiusSquared) {
            if (thisRoundNearbyEnemyCount == 0 && thisRoundNearbyNeutralCount == 0) {
                rc.setFlag(Flags.encodeAreaClear(rc.getRoundNum(), enemyLocation.get()));
                flagSet = true;
                enemyLocation = Optional.empty();
            }
        }
        if (isDefending){
            runCircleDefense();
        } else {
            if (enemyLocation.isPresent()){
                nav.moveTo(enemyLocation.get());
                //nav.bugTo(enemyLocation.get());
            } else {
                if (scoutingDirection != null) {
                    nav.spreadOut(scoutingDirection);
                } else {
                    Direction random = Utilities.randomDirection();
                    nav.spreadOut(random);
                }
            }
        }

        // Search for boundary if we can
        if (Clock.getBytecodesLeft() > 1000 && !enemyFound && !flagSet){
            searchForNearbyBoundaries();
            flagBoundaries();
        }


        if (!flagSet){
            ////System.out.println("REBROADCAST");
            rc.setFlag(mostRecentEnemyReportRebroadcast);
        }

        enemyFound = false;
        flagSet = false;
        thisRoundNearbyEnemyCount = 0;
        thisRoundNearbyNeutralCount = 0;
        thisRoundNearbyFriendlyCount =0;
        return this;
    }

    // sets enemy loc if !present or if enemy loc is closer
    private void setEnemyLocIfCloser(MapLocation newLoc, RobotType rt, boolean isGuess) {
        if (rt == RobotType.SLANDERER || rt == RobotType.POLITICIAN){
            MapLocation currentLoc = rc.getLocation();
            if (!enemyLocation.isPresent()
                    || enemyLocIsGuess
                    || (newLoc.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(enemyLocation.get()))) {
                enemyLocation = Optional.of(newLoc);
                enemyLocIsGuess = isGuess;
            }
        }
    }


    private void onEnemyNearby(RobotInfo robotInfo) throws GameActionException {
        thisRoundNearbyEnemyCount += 1;
        setEnemyLocIfCloser(robotInfo.location, robotInfo.getType(), false);
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
                            ////System.out.println("TOO OLD!");
                            return;
                        }
                    }
                    //recordEnemy(enemySpottedInfo);
                    setEnemyLocIfCloser(enemySpottedInfo.location, enemySpottedInfo.enemyType, enemySpottedInfo.isGuess);
                    break;
                case AREA_CLEAR:
                    if (enemyLocation.isPresent()){
                        AreaClearInfo areaClearInfo = Flags.decodeAreaClear(currentLoc, nearbyFlag);
                        if (areaClearInfo.location.distanceSquaredTo(enemyLocation.get()) < 5) {
                            enemyLocation = Optional.empty();
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
                setEnemyLocIfCloser(enemySpottedInfo.location, enemySpottedInfo.enemyType, enemySpottedInfo.isGuess);
                break;
            default:
                break;
        }
    }

    private void runCircleDefense() throws GameActionException {
        if (circleLocs.size() == 0){
            ////System.out.println("CIRCLE DONE");
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
