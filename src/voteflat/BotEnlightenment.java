package voteflat;

import voteflat.eccontrollers.FlagController;
import voteflat.eccontrollers.SpawnController;
import voteflat.eccontrollers.VoteController;
import voteflat.flags.*;
import battlecode.common.*;

import java.util.*;

public class BotEnlightenment extends BotController {
    private List<EnemySpottedInfo> reportedEnemies = new LinkedList<>();
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
    private SpawnController spawnController;

    public BotEnlightenment(RobotController rc) throws GameActionException {
        super(rc);
        voteController = new VoteController(rc, this, 1, 1, .1);
        flagController = new FlagController(rc, this);
        spawnController = new SpawnController(rc,this);
        northBoundary = Optional.empty();
        eastBoundary = Optional.empty();
        southBoundary = Optional.empty();
        westBoundary = Optional.empty();

        // Don't know any bounds
        rc.setFlag(Flags.encodeBoundaryRequired(FlagAddress.ANY, false, false, false, false));
    }
    @Override
    public void run() throws GameActionException {
        //Run spawn controller
        //spawnController.run();
        // Read and update flags
        //flagController.run();
        // Bid for votes
        voteController.run();
    }
    private void checkRep(){
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
        switch(robotType){
            case MUCKRAKER:
                muckrakerCount++;
                break;
            case POLITICIAN:
                politicianCount++;
                break;
            case SLANDERER:
                slandererCount++;
                break;
            default: throw new RuntimeException("SPAWNING ILLEGAL UNIT");
        }
    }

    /**
     * Report the death of a spawned robot
     * @param id of the robot that died
     */
    public void reportDeath(int id) {
        RobotType robotType = spawnedRobots.get(id);
        switch(robotType){
            case MUCKRAKER:
                muckrakerCount--;
                break;
            case POLITICIAN:
                politicianCount--;
                break;
            case SLANDERER:
                slandererCount--;
                break;
            default: throw new RuntimeException("AN ILLEGAL UNIT DIED");
        }
        spawnedRobots.remove(id);
    }

    /**
     * @return known muckraker count
     */
    public int getMuckrakerCount() {
        assert muckrakerCount >= 0;
        return muckrakerCount;
    }

    /**
     * @return known politician count
     */
    public int getPoliticianCount() {
        assert politicianCount >= 0;
        return politicianCount;
    }

    /**
     * @return known slanderer count
     */
    public int getSlandererCount() {
        assert slandererCount >= 0;
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
     * @return true if all boundaries are found
     */
    public boolean areAllBoundariesFound() {
        return isNorthBoundaryFound()
                || isSouthBoundaryFound()
                || isWestBoundaryFound()
                || isEastBoundaryFound();
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
        //MapLocation location = new MapLocation(enemySpottedInfo.delta.x + myLoc.x, enemySpottedInfo.delta.y + myLoc.y);
        reportedEnemies.add(enemySpottedInfo);
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

    /**
     * Get the latest reported location
     */
    public Optional<EnemySpottedInfo> getLatestReportedEnemyLocation(){
        if (reportedEnemies.size() > 0){
            return Optional.of(reportedEnemies.get(reportedEnemies.size() - 1));
        }else{
            return Optional.empty();
        }
    }

}
