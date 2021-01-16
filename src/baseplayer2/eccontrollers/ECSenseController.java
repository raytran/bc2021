package baseplayer2.eccontrollers;

import baseplayer2.BotEnlightenment;
import baseplayer2.flags.EnemySpottedInfo;
import baseplayer2.flags.FlagType;
import baseplayer2.flags.Flags;
import battlecode.common.*;

/**
 * Senses nearby environment and generates a safety evaluation
 * Where more positive = safer
 *   and more negative = danger
 */
public class ECSenseController implements ECController{
    RobotController rc;
    BotEnlightenment ec;
    public ECSenseController(RobotController rc, BotEnlightenment ec) {
        this.ec = ec;
        this.rc = rc;
    }

    @Override
    public void run() throws GameActionException {
        double safetyEval = 1;
        int radius = rc.getType().sensorRadiusSquared;
        Team enemy = rc.getTeam().opponent();
        MapLocation currentLoc = rc.getLocation();
        for (RobotInfo ri : rc.senseNearbyRobots(radius)) {
            if (ri.team.equals(enemy)) {
                safetyEval -= ri.influence;
            } else {
                safetyEval += ri.influence;
                int friendFlag = rc.getFlag(ri.ID);
                if (Flags.decodeFlagType(rc.getFlag(ri.ID)) == FlagType.ENEMY_SPOTTED) {
                    EnemySpottedInfo esi = Flags.decodeEnemySpotted(currentLoc, friendFlag);
                    if (esi.enemyType != RobotType.SLANDERER) {
                        if (esi.location.distanceSquaredTo(currentLoc) < rc.getType().sensorRadiusSquared + 20){
                            safetyEval -= 1/(double) esi.location.distanceSquaredTo(currentLoc);
                        }
                    }
                }
            }
        }
        System.out.println("SAFETY EVAL: " + safetyEval);
        ec.setSafetyEval(safetyEval);
    }
}
