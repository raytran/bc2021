package baseplayer.eccontrollers;

import baseplayer.BotEnlightenment;
import baseplayer.flags.BoundarySpottedInfo;
import baseplayer.flags.EnemySpottedInfo;
import baseplayer.flags.FlagAddress;
import baseplayer.flags.Flags;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

import java.util.Map;
import java.util.Optional;


/**
 * This class decides for the EC how to respond to flags and what flags to send
 * A FlagController is created with the EC whenever the EC spawns.
 */
public class FlagController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;


    public FlagController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
    }

    /**
     * Runs a turn of reading and sending flags for the EC
     */
    @Override
    public void run() throws GameActionException {
        readFlags();
        setFlags();
    }

    private void readFlags() throws GameActionException{
        // Check baseplayer.flags
        for (Map.Entry<Integer, RobotType> entry : ec.getNSpawnedRobotInfos(50)) {
            int id = entry.getKey();
            if (rc.canGetFlag(id)) {
                int flag = rc.getFlag(id);
                if (Flags.addressedForCurrentBot(rc, flag,true)) {
                    switch (Flags.decodeFlagType(flag)){
                        case ENEMY_SPOTTED:
                            ec.reportEnemy(Flags.decodeEnemySpotted(rc.getLocation(), flag));
                            //System.out.println("Enemy spotted received");
                            break;
                        case BOUNDARY_SPOTTED:
                            BoundarySpottedInfo info2 = Flags.decodeBoundarySpotted(flag);
                            //System.out.println("BOUNDARY REPORT" + info2.boundaryType + " AT " + info2.exactBoundaryLocation);
                            switch (info2.boundaryType) {
                                case NORTH:
                                    ec.reportNorthBoundary(info2.exactBoundaryLocation);
                                    break;
                                case EAST:
                                    ec.reportEastBoundary(info2.exactBoundaryLocation);
                                    break;
                                case SOUTH:
                                    ec.reportSouthBoundary(info2.exactBoundaryLocation);
                                    break;
                                case WEST:
                                    ec.reportWestBoundary(info2.exactBoundaryLocation);
                                    break;
                                default:
                                    //TODO refactor boundary spotted flag?
                                    throw new RuntimeException("Shouldn't be here...");
                            }
                            break;
                        default:
                            break;
                    }
                }
            } else {
                //Can't get flag; must be dead!
                System.out.println("Death report for " + id);
                ec.reportDeath(id);
            }
        }
    }

    private void setFlags() throws GameActionException {
        //TODO more sophisticated flagging
        Optional<EnemySpottedInfo> enemyReport = ec.getLatestReportedEnemyLocation();
        if (enemyReport.isPresent() && (ec.areAllBoundariesFound() || rc.getRoundNum() % 2 == 0)) {
            //System.out.println("EC Flagging enemy");
            rc.setFlag(Flags.encodeEnemySpotted(FlagAddress.ANY, enemyReport.get().location, enemyReport.get().enemyType));
        }
    }

}
