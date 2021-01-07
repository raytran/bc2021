package baseplayer;

import baseplayer.eccontrollers.FlagController;
import baseplayer.eccontrollers.VoteController;
import baseplayer.flags.*;
import battlecode.common.*;

import java.util.*;

public class BotEnlightenment extends BotController {
    private List<Map.Entry<MapLocation, RobotType>> reportedEnemies = new LinkedList<>();
    private Map<Integer, RobotType> spawnedRobots = new HashMap<>();
    private int muckrakerCount = 0;
    private int politicianCount = 0;
    private int slandererCount = 0;
    private Optional<Integer> northBoundary;
    private Optional<Integer> eastBoundary;
    private Optional<Integer> southBoundary;
    private Optional<Integer> westBoundary;


    private VoteController voteController;
    private FlagController flagController;


    public BotEnlightenment(RobotController rc) throws GameActionException {
        super(rc);
        voteController = new VoteController(rc, this);
        flagController = new FlagController(rc, this);

        northBoundary = Optional.empty();
        eastBoundary = Optional.empty();
        southBoundary = Optional.empty();
        westBoundary = Optional.empty();

        // Don't know any bounds
        rc.setFlag(Flags.encodeBoundaryRequired(FlagAddress.ANY, false, false, false, false));
    }

    public void checkRep(){
        assert muckrakerCount > 0;
        assert politicianCount > 0;
        assert slandererCount > 0;
    }

    /**
     * Report the spawning of a new robot
     * @param id id of the robot that spawned
     * @param robotType type of the robot that spawned
     */
    public void reportSpawn(int id, RobotType robotType) {
        spawnedRobots.put(id, robotType);
    }

    /**
     * Report the death of a spawned robot
     * @param id of the robot that died
     */
    public void reportDeath(int id) {
        spawnedRobots.get(id);
    }

    /**
     * @return known muckraker count
     */
    public int getMuckrakerCount() {
        assert muckrakerCount > 0;
        return muckrakerCount;
    }

    /**
     * @return known politician count
     */
    public int getPoliticianCount() {
        assert politicianCount > 0;
        return politicianCount;
    }

    /**
     * @return known slanderer count
     */
    public int getSlandererCount() {
        assert slandererCount > 0;
        return slandererCount;
    }

    /**
     * @return true if north found
     */
    public boolean isNorthBoundaryFound() {
        return northBoundary.isPresent();
    }

    /**
     * @return true if south found
     */
    public boolean isSouthBoundaryFound() {
        return southBoundary.isPresent();
    }


    /**
     * @return true if east found
     */
    public boolean isEastBoundaryFound() {
        return eastBoundary.isPresent();
    }

    /**
     * @return true if west found
     */
    public boolean isWestBoundaryFound() {
        return westBoundary.isPresent();
    }

    /**
     * @return set of spawned robot ids
     */
    public Set<Integer> getSpawnedIds() {
        return spawnedRobots.keySet();
    }

    /**
     * Report the death of an enemy
     * @param enemySpottedInfo the spotting info for the enemy
     */
    public void reportEnemy(EnemySpottedInfo enemySpottedInfo) {
        MapLocation myLoc = rc.getLocation();
        MapLocation location = new MapLocation(enemySpottedInfo.delta.x + myLoc.x, enemySpottedInfo.delta.y + myLoc.y);
        reportedEnemies.add(new AbstractMap.SimpleImmutableEntry<>(location, enemySpottedInfo.enemyType));
    }

    /**
     * Report the discovery of the north boundary
     * @param boundary exact location
     */
    public void reportNorthBoundary(int boundary) {
        if (!northBoundary.isPresent()){
            northBoundary = Optional.of(boundary);
        }
    }

    /**
     * Report the discovery of the east boundary
     * @param boundary exact location
     */
    public void reportEastBoundary(int boundary) {
        if (!eastBoundary.isPresent()){
            eastBoundary = Optional.of(boundary);
        }
    }

    /**
     * Report the discovery of the south boundary
     * @param boundary exact location
     */
    public void reportSouthBoundary(int boundary) {
        if (!southBoundary.isPresent()){
            southBoundary = Optional.of(boundary);
        }
    }

    /**
     * Report the discovery of the west boundary
     * @param boundary exact location
     */
    public void reportWestBoundary(int boundary) {
        if (!westBoundary.isPresent()){
            westBoundary = Optional.of(boundary);
        }
    }

    @Override
    public void run() throws GameActionException {
        //RobotType toBuild = Utilities.randomSpawnableRobotType();
        /*
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
         */
        // Read and update flags
        flagController.run();
        // Bid for votes
        voteController.run();
    }
}
