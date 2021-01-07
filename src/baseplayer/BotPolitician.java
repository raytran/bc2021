package baseplayer;

import baseplayer.flags.*;
import battlecode.common.*;

import java.util.ArrayList;
import java.util.List;
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
            int parentFlag = rc.getFlag(parentID);
            if (Flags.addressedForCurrentBot(rc, parentFlag, false)) {
                FlagType parentFlagType = Flags.decodeFlagType(parentFlag);
                switch (parentFlagType){
                    case ENEMY_SPOTTED:
                        if (!enemyLocation.isPresent()){
                            EnemySpottedInfo enemySpottedInfo = Flags.decodeEnemySpotted(parentFlag);
                            enemyLocation = Optional.of(parentLoc.translate(enemySpottedInfo.delta.x, enemySpottedInfo.delta.y));
                        }
                        break;
                    default:
                        break;
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
        return this;
    }
}
