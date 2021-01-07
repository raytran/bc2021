package baseplayer;

import baseplayer.ds.CircularLinkedList;
import baseplayer.ds.LinkedListNode;
import baseplayer.eccontrollers.FlagController;
import baseplayer.eccontrollers.SpawnController;
import baseplayer.eccontrollers.VoteController;
import baseplayer.flags.*;
import battlecode.common.*;

import java.util.*;

public class BotEnlightenment extends BotController {
    private final List<EnemySpottedInfo> reportedEnemies = new LinkedList<>();
    private final CircularLinkedList<Map.Entry<Integer,RobotType>> spawnedRobots = new CircularLinkedList<>();
    private final HashMap<Integer, LinkedListNode<Map.Entry<Integer, RobotType>>> robotIdToLLN = new HashMap<>();
    private LinkedListNode<Map.Entry<Integer, RobotType>> lastSample;


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
        voteController = new VoteController(rc, this);
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
    public BotController run() throws GameActionException {
        //Run spawn controller
        spawnController.run();
        // Read and update flags
        flagController.run();
        // Bid for votes
        voteController.run();
        return this;
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
        LinkedListNode<Map.Entry<Integer, RobotType>> newLLN
                = spawnedRobots.addToTail(new AbstractMap.SimpleImmutableEntry<>(id, robotType));
        robotIdToLLN.put(id, newLLN);

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
        LinkedListNode<Map.Entry<Integer, RobotType>> toBeRemoved = robotIdToLLN.get(id);
        robotIdToLLN.remove(id);
        spawnedRobots.remove(toBeRemoved);
        switch(toBeRemoved.getData().getValue()){
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
     * Return the n sampled robot ids
     * If n <= num robot ids, then returns all
     * @return set of n randomly sampled robot ids
     */
    public List<Map.Entry<Integer, RobotType>> getNSpawnedRobotInfos(int n) {
        if (n <= 0){
            throw new IllegalArgumentException("Cannot sample <= 0 ids");
        } else {
            if (spawnedRobots.getSize() == 0){
                return new ArrayList<>();
            } else {
                //int before = Clock.getBytecodeNum();
                List<Map.Entry<Integer, RobotType>> samples = spawnedRobots.sampleWithMemory(n);
                //int after = Clock.getBytecodeNum();
                //System.out.println("Used bytecode : " + (after-before));
                return samples;
            }
        }
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
