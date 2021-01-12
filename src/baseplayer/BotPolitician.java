package baseplayer;

import baseplayer.flags.*;
import battlecode.common.*;
import com.sun.tools.doclint.Checker;

import java.util.Optional;

public class BotPolitician extends BotController {
    Optional<MapLocation> targetLocation;
    Direction scoutingDirection;
    double bestTargetScore = 0;
    int totalNearbyEnemyConviction = 0;
    int totalNearbyFriendlyConviction = 0;
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
        totalNearbyFriendlyConviction = rc.getConviction();
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
    private void setTargetLocIfBetter(Team targetTeam, MapLocation newLoc, RobotType type,  boolean isGuess, int enemyConviciton){
        if (!targetLocation.isPresent()
                || targetLocIsGuess
                || scoreTarget(targetTeam, newLoc, type) > bestTargetScore) {
            targetLocation = Optional.of(newLoc);
            targetLocIsGuess = isGuess;
            bestTargetScore = scoreTarget(targetTeam, newLoc, type);
        }
    }

    private double scoreTarget(Team targetTeam, MapLocation newLoc, RobotType type) {
            return scoreTarget(targetTeam,newLoc,type,0);
    }

    //Underloaded constructor
    private void setTargetLocIfBetter(Team opponent, MapLocation location, RobotType enemyType, boolean isGuess) {
        setTargetLocIfBetter(opponent,location,enemyType,isGuess,0);
    }
    private double scoreTarget(Team targetTeam, MapLocation location, RobotType type,int enemyConviction) {
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
        //return (1 - distNorm) * typeMulti + ((rc.getConviction() >= enemyConviction || targetTeam.equals(Team.NEUTRAL)) ? 0.4 : 0);
        return (1 - distNorm) * typeMulti + (targetTeam.equals(Team.NEUTRAL) ? 0.7 : 0);
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
                case NEUTRAL_EC_SPOTTED:
                    System.out.println("Looks like our parents are reporting a neutral EC");
                    NeutralEcSpottedInfo neutralEcSpottedInfo = Flags.decodeNeutralEcSpotted(rc.getLocation(),parentFlag);
                    //if(rc.getConviction() >= neutralEcSpottedInfo.conviction ) {
                        //Change the exact location to the location of the EC perhaps
                        rc.setFlag(Flags.encodeGoingToNeutralEC(FlagAddress.ANY, 0));
                        setTargetLocIfBetter(Team.NEUTRAL, neutralEcSpottedInfo.location, RobotType.ENLIGHTENMENT_CENTER, false, neutralEcSpottedInfo.conviction);
                        flagSet = true;
                    //}
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
        if (!Flags.decodeFlagType(rc.getFlag(rc.getID())).equals(FlagType.GOING_TO_NEUTRAL_EC) && robotInfo.location.distanceSquaredTo(rc.getLocation()) < actionRadius){
            totalNearbyEnemyConviction += robotInfo.conviction;
            if (rc.canEmpower(actionRadius) && (totalNearbyEnemyConviction > 3 || (double) rc.getConviction()/totalNearbyFriendlyConviction < 0.25)) {
                rc.empower(actionRadius);
            }
        }
    }

    private void onFriendlyNearby(RobotInfo robotInfo) throws GameActionException {
        MapLocation currentLoc = rc.getLocation();
        totalNearbyFriendlyConviction += robotInfo.conviction;
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
        int sumOfConviction = 0;
        int numPoliticians = 0;
        for(RobotInfo r : rc.senseNearbyRobots()){
            if(robotInfo.getTeam().equals(rc.getTeam()) && r.getType() == RobotType.POLITICIAN){
                sumOfConviction+= r.getConviction();
                numPoliticians++;
            }
        }
        int actionRadius = rc.getType().actionRadiusSquared;
        if (robotInfo.location.distanceSquaredTo(rc.getLocation()) < actionRadius
                && rc.canEmpower(actionRadius)) {
            rc.empower(actionRadius);
        }
    }
}
