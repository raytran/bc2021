package dlmoreram011121_01.eccontrollers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import dlmoreram011121_01.BotEnlightenment;
import dlmoreram011121_01.flags.*;

import java.util.Map;
import java.util.Optional;


/**
 * This class decides for the EC how to respond to flags and what flags to send
 * A FlagController is created with the EC whenever the EC spawns.
 */
public class ECFlagController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    Optional<NeutralEcSpottedInfo> thisRoundNeutralEcSpottedInfo = Optional.empty();


    public ECFlagController(RobotController rc, BotEnlightenment ec) {
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
        thisRoundNeutralEcSpottedInfo = Optional.empty();
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
                            ec.recordEnemy(Flags.decodeEnemySpotted(rc.getLocation(), flag));
                            break;
                        case BOUNDARY_SPOTTED:
                            BoundarySpottedInfo info2 = Flags.decodeBoundarySpotted(flag);
                            switch (info2.boundaryType) {
                                case NORTH:
                                    ec.recordNorthBoundary(info2.exactBoundaryLocation);
                                    break;
                                case EAST:
                                    ec.recordEastBoundary(info2.exactBoundaryLocation);
                                    break;
                                case SOUTH:
                                    ec.recordSouthBoundary(info2.exactBoundaryLocation);
                                    break;
                                case WEST:
                                    ec.recordWestBoundary(info2.exactBoundaryLocation);
                                    break;
                                default:
                                    //TODO refactor boundary spotted flag?
                                    throw new RuntimeException("Shouldn't be here...");
                            }
                            break;
                        case NEUTRAL_EC_SPOTTED:
                            thisRoundNeutralEcSpottedInfo = Optional.of(Flags.decodeNeutralEcSpotted(rc.getLocation(), flag));
                            ////System.out.println("NEUTRAL EC FOUND " + .location + " WITH " + neutralEcSpottedInfo.conviction + " HP");
                            break;
                        default:
                            break;
                    }
                }
            } else {
                //Can't get flag; must be dead!
               // //System.out.println("Death report for " + id);
                ec.recordDeath(id);
            }
        }
    }

    private void setFlags() throws GameActionException {
        //TODO more sophisticated flagging
        Optional<EnemySpottedInfo> enemyReport = ec.getLatestRecordedEnemyLocation();
        if (thisRoundNeutralEcSpottedInfo.isPresent()){
            rc.setFlag(Flags.encodeNeutralEcSpotted(FlagAddress.ANY, this.thisRoundNeutralEcSpottedInfo.get().location, this.thisRoundNeutralEcSpottedInfo.get().conviction));
        }else if (enemyReport.isPresent()) {
            ////System.out.println("EC Flagging enemy");
            rc.setFlag(Flags.encodeEnemySpotted(FlagAddress.ANY, enemyReport.get().location, enemyReport.get().enemyType, false));
        }else if (ec.getEastBoundary().isPresent() && ec.getWestBoundary().isPresent()
                || ec.getNorthBoundary().isPresent() && ec.getSouthBoundary().isPresent()) {
            //System.out.println("TRYING REFLECTION");
            if (ec.getEastBoundary().isPresent() && ec.getWestBoundary().isPresent()) {
                int xDelta = rc.getLocation().x - ec.getWestBoundary().get();
                int targetX = ec.getEastBoundary().get() - xDelta;
                rc.setFlag(Flags.encodeEnemySpotted(FlagAddress.ANY, new MapLocation(targetX, rc.getLocation().y), RobotType.ENLIGHTENMENT_CENTER, true));
            } else {
                int yDelta = rc.getLocation().y - ec.getSouthBoundary().get();
                int targetY = ec.getNorthBoundary().get() - yDelta;
                rc.setFlag(Flags.encodeEnemySpotted(FlagAddress.ANY, new MapLocation(rc.getLocation().x, targetY), RobotType.ENLIGHTENMENT_CENTER, true));
            }
        }
    }
}
