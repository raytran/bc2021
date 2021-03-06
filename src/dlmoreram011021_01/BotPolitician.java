package dlmoreram011021_01;

import battlecode.common.*;
import dlmoreram011021_01.flags.EnemySpottedInfo;
import dlmoreram011021_01.flags.FlagAddress;
import dlmoreram011021_01.flags.FlagType;
import dlmoreram011021_01.flags.Flags;

import java.util.List;
import java.util.Optional;

public class BotPolitician extends BotController {
    List<MapLocation> circleLocs;
    int currentRadius = 3;
    Optional<MapLocation> enemyLocation;
    Direction scoutingDirection;
    MapLocation circleTargetLoc;
    boolean isDefending;
    boolean enemyLocIsGuess = true;
    public BotPolitician(RobotController rc) throws GameActionException {
        super(rc);
        enemyLocation = Optional.empty();
        isDefending = Math.random() > 0.45;
        if (parentLoc.isPresent()){
            if (isDefending) {
                circleLocs = Utilities.getFilteredCircleLocs(1,parentLoc.get().x, parentLoc.get().y, parentLoc.get(), currentRadius);
            }
            scoutingDirection = parentLoc.get().directionTo(rc.getLocation());
            assert scoutingDirection != null;
        }else{
            isDefending = false;
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
        MapLocation currentLoc = rc.getLocation();
        if (enemyLocation.isPresent() &&  currentLoc.equals(enemyLocation.get())){
            enemyLocation = Optional.empty();
        }
        boolean enemyFound = false;
        boolean flagSet = false;
        for (RobotInfo robotInfo : rc.senseNearbyRobots()) {
            if (robotInfo.getTeam().equals(rc.getTeam())) {
                //Nearby friendly
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
            } else {
                //Nearby enemy
                enemyFound = true;
                setEnemyLocIfCloser(robotInfo.location, false);
                int actionRadius = rc.getType().actionRadiusSquared;
                recordEnemy(new EnemySpottedInfo(robotInfo.location, robotInfo.getType(), false));

                if (!flagSet) {
                    rc.setFlag(Flags.encodeEnemySpotted(FlagAddress.ANY, robotInfo.location, robotInfo.getType(), false));
                    flagSet = true;
                }
                //TODO more advanced stuff later
                if (robotInfo.location.distanceSquaredTo(currentLoc) < actionRadius
                        && rc.canEmpower(actionRadius)) {
                    rc.empower(actionRadius);
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
                        setEnemyLocIfCloser(enemySpottedInfo.location, enemySpottedInfo.isGuess);
                        break;
                    default:
                        break;
                }
            }
        }

        if (isDefending){
            runCircleDefense();
        } else {
            if (enemyLocation.isPresent()){
                ////System.out.println("GOING TO ENEMY");
                nav.bugTo(enemyLocation.get());
            } else {
                if (scoutingDirection != null){
                    nav.spreadOut(scoutingDirection);
                }else{
                    Direction random = Utilities.randomDirection();
                    nav.spreadOut(random);
                }
            }
        }

        // Search for boundary if we can
        if (Clock.getBytecodesLeft() > 1000 && !enemyFound){
            searchForNearbyBoundaries();
            flagBoundaries();
        }
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

    private void runCircleDefense() throws GameActionException {
        if (circleLocs.size() == 0){
            ////System.out.println("CIRCLE DONE");
            currentRadius = currentRadius +4;
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
            nav.bugAndDijkstraTo(circleTargetLoc);
        }
    }
}
