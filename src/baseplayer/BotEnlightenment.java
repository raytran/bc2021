package baseplayer;

import baseplayer.flags.*;
import battlecode.common.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BotEnlightenment extends BotController {

    private Set<Integer> spawnedIds = new HashSet<>();
    private Optional<Integer> northBoundary;
    private Optional<Integer> eastBoundary;
    private Optional<Integer> southBoundary;
    private Optional<Integer> westBoundary;
    private boolean enemyFound = false;
    private Direction nextSpawnDirection = Direction.NORTH;
    private VoteController voteController;


    public BotEnlightenment(RobotController rc) throws GameActionException {
        super(rc);
        northBoundary = Optional.empty();
        eastBoundary = Optional.empty();
        southBoundary = Optional.empty();
        westBoundary = Optional.empty();
        voteController = new VoteController(rc);

        // Don't know any bounds
        rc.setFlag(Flags.encodeBoundaryRequired(FlagAddress.ANY, false, false, false, false));
    }

    @Override
    public void run() throws GameActionException {
        //RobotType toBuild = Utilities.randomSpawnableRobotType();
        RobotType toBuild = RobotType.MUCKRAKER;
        MapLocation myLoc = rc.getLocation();
        int influence = 1;
        Direction originalSpawnDir = nextSpawnDirection;
        while (!enemyFound) {
            nextSpawnDirection = nextSpawnDirection.rotateRight();
            if (originalSpawnDir.equals(nextSpawnDirection)){
                break;
            }
            if (rc.canBuildRobot(toBuild, nextSpawnDirection, influence)){
                //Built the robot, add id to total
                rc.buildRobot(toBuild, nextSpawnDirection, influence);
                spawnedIds.add(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID);
                break;
            }
        }

        // Check baseplayer.flags
        for (Integer id : spawnedIds){
            if (rc.canGetFlag(id)){
                int flag = rc.getFlag(id);
                if (Flags.addressedForCurrentBot(rc, flag,true)) {
                    switch (Flags.decodeFlagType(flag)){
                        case ENEMY_SPOTTED:
                            enemyFound = true;
                            EnemySpottedInfo info = Flags.decodeEnemySpotted(flag);
                            //System.out.println("Enemy of type " + info.enemyType
                            //        + " found at " + (info.delta.x + myLoc.x) + " , " + (info.delta.y + myLoc.y)
                            //        + " by " + id);
                            break;
                        case BOUNDARY_SPOTTED:
                            BoundarySpottedInfo info2 = Flags.decodeBoundarySpotted(flag);
                            boolean changed = false;
                            switch (info2.boundaryType) {
                                case NORTH:
                                    if (!northBoundary.isPresent()) {
                                        northBoundary = Optional.of(info2.exactBoundaryLocation);
                                        changed = true;
                                        System.out.println("NORTH BOUNDARY FOUND at " + northBoundary.get());
                                    }
                                    break;
                                case EAST:
                                    if (!eastBoundary.isPresent()) {
                                        eastBoundary = Optional.of((info2.exactBoundaryLocation));
                                        changed = true;
                                        System.out.println("EAST BOUNDARY FOUND at " + eastBoundary.get());
                                    }
                                    break;
                                case SOUTH:
                                    if (!southBoundary.isPresent()) {
                                        southBoundary = Optional.of(info2.exactBoundaryLocation);
                                        changed = true;
                                        System.out.println("SOUTH BOUNDARY FOUND at " + southBoundary.get());
                                    }
                                    break;
                                case WEST:
                                    if (!westBoundary.isPresent()) {
                                        westBoundary = Optional.of(info2.exactBoundaryLocation);
                                        changed = true;
                                        System.out.println("WEST BOUNDARY FOUND at " + westBoundary.get());
                                    }
                                    break;
                                default:
                                    //TODO refactor boundary spotted flag?
                                    throw new RuntimeException("Shouldn't be here...");
                            }

                            if (changed) {
                                rc.setFlag(Flags.encodeBoundaryRequired(
                                        FlagAddress.ANY,
                                        northBoundary.isPresent(),
                                        eastBoundary.isPresent(),
                                        southBoundary.isPresent(),
                                        westBoundary.isPresent())
                                );
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        // Bid for votes
        int influenceToBid = voteController.influenceToBid();
        if (influenceToBid > 0) {
            rc.bid(influenceToBid);
        }
    }
}
