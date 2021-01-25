package dlmoreram012521_01;

import dlmoreram012521_01.flags.*;
import dlmoreram012521_01.nav.BoundingBox;
import battlecode.common.*;

import java.util.Optional;

public class BotPolitician extends BotController {


    Optional<MapLocation> targetLocation;
    static Direction initialDirection;
    double bestTargetScore = 0;
    int thisRoundNearbyFriendlyCount = 0;
    int thisRoundNearbyEnemyCount = 0;
    int thisRoundNearbyNeutralCount = 0;
    int totalNearbyEnemyInfluence = 0;
    int totalNearbyFriendlyInfluence = 0;


    int mostRecentEnemyReportRebroadcastTimestamp = 0;
    int mostRecentEnemyReportRebroadcast = 0;
    RobotInfo closestEnemy = null;
    BoundingBox guardPerimeter;

    boolean targetLocIsGuess = true;
    boolean enemyFound = false;
    boolean flagSet = false;
    boolean isDefending;
    boolean onlyTargetEC;
    public BotPolitician(RobotController rc) throws GameActionException {
        super(rc);
        targetLocation = Optional.empty();
        isDefending = rc.getInfluence() <= 20 && parentLoc.isPresent();
        if (isDefending) {
            guardPerimeter = new BoundingBox(parentLoc.get().translate(5, 5), parentLoc.get().translate(-5, -5));
        }
        onlyTargetEC = rc.getInfluence() == 131;
        if (parentLoc.isPresent()){
            initialDirection = parentLoc.get().directionTo(rc.getLocation());
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
        totalNearbyFriendlyInfluence = rc.getInfluence();
        senseNearbyRobots(this::onEnemyNearby, this::onFriendlyNearby, this::onNeutralNearby);

        if (parentID.isPresent()) talkToParent();
        if(isDefending) {
            if (closestEnemy != null) {
                nav.moveTo(closestEnemy.getLocation());
            } else {
                boundaryDefense();
            }
        } else {
            if (targetLocation.isPresent() && rc.getLocation().distanceSquaredTo(targetLocation.get()) < rc.getType().actionRadiusSquared) {
                if (thisRoundNearbyEnemyCount == 0 && thisRoundNearbyNeutralCount == 0) {
                    rc.setFlag(Flags.encodeAreaClear(rc.getRoundNum(), targetLocation.get()));
                    flagSet = true;
                    targetLocation = Optional.empty();
                    bestTargetScore = 0;
                }
            }

            if (targetLocation.isPresent()) {
                nav.moveTo(targetLocation.get());
            } else {
                if (initialDirection != null) {
                    nav.spreadOut(initialDirection);
                } else {
                    Direction random = Utilities.randomDirection();
                    nav.spreadOut(random);
                }
            }
            // Search for boundary if we can
            if (Clock.getBytecodesLeft() > 1000 && !enemyFound && !flagSet) {
                searchForNearbyBoundaries();
                flagBoundaries();
            }

            if (!flagSet) {
                rc.setFlag(mostRecentEnemyReportRebroadcast);
            }

        }
        thisRoundNearbyEnemyCount = 0;
        thisRoundNearbyNeutralCount = 0;
        thisRoundNearbyFriendlyCount = 0;
        totalNearbyEnemyInfluence = 0;
        enemyFound = false;
        flagSet = false;
        closestEnemy = null;
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
                typeMulti = onlyTargetEC ? 0 : 0.5;
                break;
            case MUCKRAKER:
                /*if (parentLoc.isPresent()) typeMulti = Math.min(1, 0.05 * Math.pow((location.distanceSquaredTo(parentLoc.get()) / (double) (64 * 64)), -0.5));
                else*/ typeMulti = onlyTargetEC ? 0 : 0.7;
                break;
            case ENLIGHTENMENT_CENTER:
                typeMulti = onlyTargetEC ? 10 : 0.75;
                break;
            case SLANDERER:
                typeMulti = onlyTargetEC ? 0 : 0.4;
                break;
        }
        return (1 - distNorm) * typeMulti + (targetTeam.equals(Team.NEUTRAL) ? onlyTargetEC ? 10 : 0.7 : 0);
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
            case NEUTRAL_EC_SPOTTED:
            case OP_SPAWNED:
                NeutralEcSpottedInfo neutralEcSpottedInfo = Flags.decodeNeutralEcSpotted(rc.getLocation(), parentFlag);
                setTargetLocIfBetter(Team.NEUTRAL, neutralEcSpottedInfo.location, RobotType.ENLIGHTENMENT_CENTER, false);
                break;
            default:
                break;
        }
    }

    private void onEnemyNearby(RobotInfo robotInfo) throws GameActionException {
        if (closestEnemy == null || robotInfo.getLocation().distanceSquaredTo(rc.getLocation()) < closestEnemy.getLocation().distanceSquaredTo(rc.getLocation())) {
            closestEnemy = robotInfo;
        }
        thisRoundNearbyEnemyCount += 1;
        setTargetLocIfBetter(robotInfo.team, robotInfo.location, robotInfo.type, false);
        //recordEnemy(new EnemySpottedInfo(rc.getRoundNum(), robotInfo.location, robotInfo.getType(), false));

        if (!flagSet) {
            rc.setFlag(Flags.encodeEnemySpotted(rc.getRoundNum(), robotInfo.location, robotInfo.getType(), false));
            flagSet = true;
        }

        if (!onlyTargetEC) {
            int actionRadius = robotInfo.getType().equals(RobotType.ENLIGHTENMENT_CENTER) ? 2 : rc.getType().actionRadiusSquared;
            if (robotInfo.location.distanceSquaredTo(rc.getLocation()) < actionRadius) {
                totalNearbyEnemyInfluence += robotInfo.influence;
                if (rc.canEmpower(actionRadius) && ((double) totalNearbyEnemyInfluence / rc.getInfluence() >= .05 || (double) rc.getInfluence() / totalNearbyFriendlyInfluence < 0.25 || totalNearbyEnemyInfluence > 3)) {
                    rc.empower(actionRadius);
                }
            }
        } else {
            if (robotInfo.location.distanceSquaredTo(rc.getLocation()) == 1
                    && rc.canEmpower(1) && robotInfo.getType().equals(RobotType.ENLIGHTENMENT_CENTER)
                    && rc.getInfluence() > robotInfo.getInfluence()) {
                rc.empower(1);
            }
        }
    }

    private void onFriendlyNearby(RobotInfo robotInfo) throws GameActionException {
        thisRoundNearbyFriendlyCount += 1;
        MapLocation currentLoc = rc.getLocation();
        totalNearbyFriendlyInfluence += robotInfo.influence;
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

    private void onNeutralNearby(RobotInfo robotInfo) throws GameActionException {
        thisRoundNearbyNeutralCount += 1;
        if (!flagSet) {
            rc.setFlag(Flags.encodeNeutralEcSpotted(rc.getRoundNum(), robotInfo.location, robotInfo.conviction));
            flagSet = true;
        }
        setTargetLocIfBetter(Team.NEUTRAL, robotInfo.location, robotInfo.type, false);
        if (robotInfo.location.distanceSquaredTo(rc.getLocation()) == 1
                && rc.canEmpower(1)) {
            rc.empower(1);
        }
    }

    final static int[][] offsets = {{2,4},{3,1},{4,-2},{1,-3},{-2,-4},{-3,-1},{-4,2},{-1,3}};
    static boolean inGrid = false;
    static MapLocation latticeLoc;
    private void latticeDefense() throws GameActionException {
        if (!inGrid) {
            boolean senseOtherFlag = false;
            if (latticeLoc == null) {
                int closestDist = Integer.MAX_VALUE;
                MapLocation closestBot = null;
                for (RobotInfo robotInfo : rc.senseNearbyRobots()){
                    if (robotInfo.type == RobotType.ENLIGHTENMENT_CENTER || rc.getFlag(robotInfo.ID) == 1){
                        senseOtherFlag = true;
                        if (robotInfo.location.distanceSquaredTo(rc.getLocation()) < closestDist){
                            closestDist = robotInfo.location.distanceSquaredTo(rc.getLocation());
                            closestBot = robotInfo.location;
                        }
                    }
                }

                if (closestBot != null){
                    for (int[] offset : offsets){
                        MapLocation possible = closestBot.translate(offset[0], offset[1]);
                        if (rc.canSenseLocation(possible) && rc.onTheMap(possible) && !rc.isLocationOccupied(possible) && !guardPerimeter.isContained(possible) && possible.distanceSquaredTo(parentLoc.get()) > 25){
                            latticeLoc = possible;
                        }
                    }
                }
            }

            if (latticeLoc == null){
                if (parentLoc.isPresent())
                    nav.fuzzyMove(parentLoc.get().directionTo(rc.getLocation()));
                else
                    nav.fuzzyMove(Utilities.randomDirection());
                if (!guardPerimeter.isContained(rc.getLocation()) && !senseOtherFlag) {
                    latticeLoc = rc.getLocation();
                }
            }else{
                nav.moveTo(latticeLoc);
                if (rc.getLocation().equals(latticeLoc)){
                    rc.setFlag(1);
                    flagSet = true;
                    inGrid = true;
                }else if (rc.canSenseLocation(latticeLoc)
                        && rc.isLocationOccupied(latticeLoc)
                        && rc.getFlag(rc.senseRobotAtLocation(latticeLoc).ID) == 1){
                    MapLocation original = rc.senseRobotAtLocation(latticeLoc).location;
                    for (int[] offset : offsets){
                        MapLocation possible = original.translate(offset[0], offset[1]);
                        if (rc.canSenseLocation(possible) && rc.onTheMap(possible) && !rc.isLocationOccupied(possible) && !guardPerimeter.isContained(possible) && possible.distanceSquaredTo(parentLoc.get()) > 25){
                            latticeLoc = possible;
                        }
                    }
                }
            }
        }
    }

    static boolean inPosition = false;
    static MapLocation defenseLocation;
    private void boundaryDefense() throws GameActionException {
        if (!inPosition) {
            if (defenseLocation == null) {
                switch (initialDirection) {
                    case NORTH:
                        defenseLocation = rc.getLocation().translate(3, 5);
                        break;
                    case NORTHEAST:
                        defenseLocation = rc.getLocation().translate(5, 2);
                        break;
                    case NORTHWEST:
                        defenseLocation = rc.getLocation().translate(-2, 5);
                        break;
                    case EAST:
                        defenseLocation = rc.getLocation().translate(5, -3);
                        break;
                    case SOUTH:
                        defenseLocation = rc.getLocation().translate(-3, -5);
                        break;
                    case SOUTHEAST:
                        defenseLocation = rc.getLocation().translate(2, -5);
                        break;
                    case SOUTHWEST:
                        defenseLocation = rc.getLocation().translate(-5, -2);
                        break;
                    case WEST:
                        defenseLocation = rc.getLocation().translate(-5, 3);
                        break;
                    default:
                        throw new RuntimeException();
                }
            }
            if (rc.getLocation().distanceSquaredTo(defenseLocation) <= RobotType.POLITICIAN.sensorRadiusSquared && !rc.onTheMap(defenseLocation)) {
                defenseLocation = rc.getLocation().add(initialDirection);
            }
            nav.moveTo(defenseLocation);
            if (rc.getLocation().equals(defenseLocation)) {
                inPosition = true;
                flagSet = true;
                rc.setFlag(1);
            } else if (rc.canSenseLocation(defenseLocation) && rc.isLocationOccupied(defenseLocation)
                    && rc.getFlag(rc.senseRobotAtLocation(defenseLocation).ID) == 1) {
                isDefending = false;
                defenseLocation = null;
            } else if (rc.getLocation().distanceSquaredTo(parentLoc.get()) >= 50) {
                defenseLocation = rc.getLocation();
            }
        } else {
            for (RobotInfo ri : rc.senseNearbyRobots()) {
                if (rc.canGetFlag(ri.ID) && rc.getFlag(ri.ID) == 2 && defenseLocation.distanceSquaredTo(ri.getLocation()) < 9) {
                    defenseLocation = rc.getLocation().add(ri.getLocation().directionTo(rc.getLocation()));
                    inPosition = false;
                }
            }
        }
    }
}
