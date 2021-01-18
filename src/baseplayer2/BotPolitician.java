package baseplayer2;

import baseplayer2.flags.*;
import battlecode.common.*;

import java.util.Optional;

public class BotPolitician extends BotController {


    Optional<MapLocation> targetLocation;
    Direction scoutingDirection;
    double bestTargetScore = 0;
    int thisRoundNearbyFriendlyCount = 0;
    int thisRoundNearbyEnemyCount = 0;
    int thisRoundNearbyNeutralCount = 0;
    int totalNearbyEnemyConviction = 0;
    int totalNearbyFriendlyConviction = 0;


    int mostRecentEnemyReportRebroadcastTimestamp = 0;
    int mostRecentEnemyReportRebroadcast = 0;

    boolean targetLocIsGuess = true;
    boolean enemyFound = false;
    boolean flagSet = false;
    boolean isLattice = false;
    public BotPolitician(RobotController rc) throws GameActionException {
        super(rc);
        targetLocation = Optional.empty();
        isLattice = rc.getRoundNum() % 10 == 0;
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
        totalNearbyFriendlyConviction = rc.getConviction();
        senseNearbyRobots(this::onEnemyNearby, this::onFriendlyNearby, this::onNeutralNearby);

        if (parentID.isPresent()) talkToParent();
        if (targetLocation.isPresent() && rc.getLocation().distanceSquaredTo(targetLocation.get()) < rc.getType().actionRadiusSquared) {
            if (thisRoundNearbyEnemyCount == 0 && thisRoundNearbyNeutralCount == 0) {
                rc.setFlag(Flags.encodeAreaClear(rc.getRoundNum(), targetLocation.get()));
                flagSet = true;
                targetLocation = Optional.empty();
                bestTargetScore = 0;
            }
        }

        if (isLattice){
            //System.out.println("LATTICE DEF");
            latticeDefense();
        } else {
            if (targetLocation.isPresent()){
                nav.moveTo(targetLocation.get());
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

            if (!flagSet){
                //System.out.println("REBROADCAST");
                rc.setFlag(mostRecentEnemyReportRebroadcast);
            }
        }

        thisRoundNearbyEnemyCount = 0;
        thisRoundNearbyNeutralCount = 0;
        thisRoundNearbyFriendlyCount =0;
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
        FlagType parentFlagType = Flags.decodeFlagType(parentFlag);
        switch (parentFlagType) {
            case ENEMY_SPOTTED:
                EnemySpottedInfo enemySpottedInfo = Flags.decodeEnemySpotted(rc.getLocation(), parentFlag);
                //recordEnemy(enemySpottedInfo);
                setTargetLocIfBetter(rc.getTeam().opponent(), enemySpottedInfo.location, enemySpottedInfo.enemyType, enemySpottedInfo.isGuess);
                break;
            case NEUTRAL_EC_SPOTTED:
            case OP_SPAWNED:
                baseplayerspawning.flags.NeutralEcSpottedInfo neutralEcSpottedInfo = baseplayerspawning.flags.Flags.decodeNeutralEcSpotted(rc.getLocation(), parentFlag);
                setTargetLocIfBetter(Team.NEUTRAL, neutralEcSpottedInfo.location, RobotType.ENLIGHTENMENT_CENTER, false);
                break;
            default:
                break;
        }
    }

    private void onEnemyNearby(RobotInfo robotInfo) throws GameActionException {
        thisRoundNearbyEnemyCount += 1;
        setTargetLocIfBetter(robotInfo.team, robotInfo.location, robotInfo.type, false);
        //recordEnemy(new EnemySpottedInfo(rc.getRoundNum(), robotInfo.location, robotInfo.getType(), false));

        if (!flagSet) {
            rc.setFlag(Flags.encodeEnemySpotted(rc.getRoundNum(), robotInfo.location, robotInfo.getType(), false));
            flagSet = true;
        }

        int actionRadius = rc.getType().actionRadiusSquared;
        if (robotInfo.location.distanceSquaredTo(rc.getLocation()) < actionRadius){
            totalNearbyEnemyConviction += robotInfo.conviction;
            if (rc.canEmpower(actionRadius) && (totalNearbyEnemyConviction > 3 || (double) rc.getConviction()/totalNearbyFriendlyConviction < 0.25)) {
                rc.empower(actionRadius);
            }
        }
    }

    private void onFriendlyNearby(RobotInfo robotInfo) throws GameActionException {
        thisRoundNearbyFriendlyCount += 1;
        MapLocation currentLoc = rc.getLocation();
        totalNearbyFriendlyConviction += robotInfo.conviction;
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

        int actionRadius = rc.getType().actionRadiusSquared;
        if (robotInfo.location.distanceSquaredTo(rc.getLocation()) < 4
                && rc.canEmpower(4)) {
            rc.empower(4);
        }
    }

    final static int[][] offsets = {{2,4},{3,1},{4,-2},{1,-3},{-2,-4},{-3,-1},{-4,2},{-1,3}};
    static boolean inGrid = false;
    static MapLocation latticeLoc;
    private void latticeDefense() throws GameActionException {
        if (!inGrid) {
            if (latticeLoc == null) {
                int closestDist = Integer.MAX_VALUE;
                MapLocation closestBot = null;
                for (RobotInfo robotInfo : rc.senseNearbyRobots()){
                    if (robotInfo.type == RobotType.ENLIGHTENMENT_CENTER || rc.getFlag(robotInfo.ID) == 1){
                        if (robotInfo.location.distanceSquaredTo(rc.getLocation()) < closestDist){
                            closestDist = robotInfo.location.distanceSquaredTo(rc.getLocation());
                            closestBot = robotInfo.location;
                        }
                    }
                }

                if (closestBot != null){
                    for (int[] offset : offsets){
                        MapLocation possible = closestBot.translate(offset[0], offset[1]);
                        if (rc.canSenseLocation(possible) && rc.onTheMap(possible) && !rc.isLocationOccupied(possible)){
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
                        if (rc.canSenseLocation(possible) && rc.onTheMap(possible) && !rc.isLocationOccupied(possible)){
                            latticeLoc = possible;
                        }
                    }
                }
            }
        }
    }
}
