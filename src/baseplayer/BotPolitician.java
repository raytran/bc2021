package baseplayer;

import baseplayer.flags.*;
import battlecode.common.*;

import java.util.Optional;

public class BotPolitician extends BotController {
    Optional<MapLocation> enemyLocation;

    public BotPolitician(RobotController rc) throws GameActionException {
        super(rc);
        enemyLocation = Optional.empty();
    }

    public static BotPolitician fromSlanderer(BotSlanderer slanderer) throws GameActionException {
        BotPolitician newPolitician = new BotPolitician(slanderer.rc);
        newPolitician.parentID = slanderer.parentID;
        newPolitician.parentLoc = slanderer.parentLoc;
        return newPolitician;
    }

    @Override
    public BotController run() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        int actionRadius = rc.getType().actionRadiusSquared;
        RobotInfo[] attackable = rc.senseNearbyRobots(actionRadius, enemy);
        int numEnemies = attackable.length;
        int convictionLeft = rc.getConviction() - 10;

        if (attackable.length != 0 && enemyLocation.isPresent() && enemyLocation.get().distanceSquaredTo(rc.getLocation()) < 5) {
            enemyLocation = Optional.empty();
        }

        if (attackable.length != 0 && convictionLeft > 0) {
            if (rc.canEmpower(actionRadius)){
                rc.empower(actionRadius);
            }
            /*
            for(RobotInfo robot : attackable){
                if(convictionLeft / numEnemies > robot.getConviction()){
                    rc.empower(actionRadius);
                }
            }

             */
        } else {
            // Politicians get converted; they may be converted from the enemy therefore losing parentID
            //TODO setup (lost parent message/response)
            if (parentID.isPresent()) {
                int parentFlag = rc.getFlag(parentID.get());
                if (Flags.addressedForCurrentBot(rc, parentFlag, false)) {
                    FlagType parentFlagType = Flags.decodeFlagType(parentFlag);
                    switch (parentFlagType){
                        case ENEMY_SPOTTED:
                            if (!enemyLocation.isPresent()){
                                EnemySpottedInfo enemySpottedInfo = Flags.decodeEnemySpotted(rc.getLocation(), parentFlag);
                                enemyLocation = Optional.of(enemySpottedInfo.location);
                            }
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
                Direction random = Utilities.randomDirection();
                nav.fuzzyMove(random);
            }
        }

        // Search for boundary if we can
        if (Clock.getBytecodesLeft() > 1000){
            searchForNearbyBoundaries();
            flagBoundaries();
        }
        return this;
    }
}
