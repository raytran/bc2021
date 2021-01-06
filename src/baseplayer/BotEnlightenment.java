package baseplayer;

import baseplayer.flags.BoundarySpottedInfo;
import baseplayer.flags.EnemySpottedInfo;
import baseplayer.flags.FlagType;
import baseplayer.flags.Flags;
import battlecode.common.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BotEnlightenment extends BotController {

    Set<Integer> spawnedIds = new HashSet<>();

    Optional<MapLocation> northBoundary;
    Optional<MapLocation> eastBoundary;
    Optional<MapLocation> southBoundary;
    Optional<MapLocation> westBoundary;

    boolean enemyFound = false;
    Direction nextSpawnDirection = Direction.NORTH;
    public BotEnlightenment(RobotController rc) throws GameActionException {
        super(rc);
        northBoundary = Optional.empty();
        eastBoundary = Optional.empty();
        southBoundary = Optional.empty();
        westBoundary = Optional.empty();

        // Don't know any bounds
        rc.setFlag(Flags.encodeBoundaryRequired(false, false, false, false));
    }

    @Override
    public void run() throws GameActionException {
        //RobotType toBuild = Utilities.randomSpawnableRobotType();
        RobotType toBuild = RobotType.MUCKRAKER;
        MapLocation myLoc = rc.getLocation();
        int influence = 50;
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
                        switch (info2.boundaryDirection) {
                            case NORTH:
                                if (!northBoundary.isPresent()) {
                                    northBoundary = Optional.of(
                                            new MapLocation(myLoc.x + info2.delta.x, myLoc.y + info2.delta.y)
                                    );
                                    changed = true;

                                    System.out.println("NORTH BOUNDARY FOUND at " + northBoundary.get().y );
                                }
                                break;
                            case EAST:
                                if (!eastBoundary.isPresent()) {
                                    eastBoundary = Optional.of(
                                            new MapLocation(myLoc.x + info2.delta.x, myLoc.y + info2.delta.y)
                                    );
                                    changed = true;

                                    System.out.println("EAST BOUNDARY FOUND at " + eastBoundary.get().x);
                                }
                                break;
                            case SOUTH:
                                if (!southBoundary.isPresent()) {
                                    southBoundary = Optional.of(
                                            new MapLocation(myLoc.x + info2.delta.x, myLoc.y + info2.delta.y)
                                    );
                                    changed = true;

                                    System.out.println("SOUTH BOUNDARY FOUND at " + southBoundary.get().y );
                                }
                                break;
                            case WEST:
                                if (!westBoundary.isPresent()) {
                                    westBoundary = Optional.of(
                                            new MapLocation(myLoc.x + info2.delta.x, myLoc.y + info2.delta.y)
                                    );
                                    changed = true;
                                    System.out.println("WEST BOUNDARY FOUND at " + westBoundary.get().x);
                                }
                                break;
                            default:
                                //TODO refactor boundary spotted flag?
                                throw new RuntimeException("Shouldn't be here...");
                        }

                        if (changed) {
                            rc.setFlag(Flags.encodeBoundaryRequired(
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
}
