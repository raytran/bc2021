package baseplayer;

import baseplayer.ds.CircularLinkedList;
import baseplayer.ds.LinkedListNode;
import baseplayer.eccontrollers.ECBudgetController;
import baseplayer.eccontrollers.ECFlagController;
import baseplayer.eccontrollers.ECSpawnController;
import baseplayer.eccontrollers.ECVoteController;
import baseplayer.flags.NeutralEcSpottedInfo;
import battlecode.common.*;

import java.util.*;

public class BotEnlightenment extends BotController {
    private final CircularLinkedList<Map.Entry<Integer,RobotType>> spawnedRobots = new CircularLinkedList<>();
    private final HashMap<Integer, LinkedListNode<Map.Entry<Integer, RobotType>>> robotIdToLLN = new HashMap<>();
    private LinkedListNode<Map.Entry<Integer, RobotType>> lastSample;

    private int muckrakerCount = 0;
    private int politicianCount = 0;
    private int slandererCount = 0;
    private double voteWinRate = 0;
    private int lastEnemySeen = 0;
    private int lastBotSpawn = 0;

    private final ECBudgetController budgetController;
    private final ECVoteController voteController;
    private final ECFlagController flagController;
    private final ECSpawnController spawnController;
    private Optional<NeutralEcSpottedInfo> thisRoundNeutralEcSpottedInfo = Optional.empty();


    public BotEnlightenment(RobotController rc) throws GameActionException {
        super(rc);
        budgetController = new ECBudgetController(rc, this);
        voteController = new ECVoteController(rc, this, this.budgetController);
        flagController = new ECFlagController(rc, this);
        spawnController = new ECSpawnController(rc,this, this.budgetController);
    }
    @Override
    public BotController run() throws GameActionException {
        //Run budget controller
        budgetController.run();
        // Read and update flags
        flagController.run();
        //Run spawn controller
        spawnController.run();
        // Bid for votes
        voteController.run();
        //Set the neutral spotted info
        thisRoundNeutralEcSpottedInfo = Optional.empty();
        // Search for boundary if we can
        if (Clock.getBytecodesLeft() > 1000){
            searchForNearbyBoundaries();
        }
        return this;
    }

    private void checkRep(){
        assert muckrakerCount >= 0;
        assert politicianCount >= 0;
        assert slandererCount >= 0;
    }

    /**
     * Record the spawning of a new robot
     * @param id id of the robot that spawned
     * @param robotType type of the robot that spawned
     */
    public void recordSpawn(int id, RobotType robotType) {
        LinkedListNode<Map.Entry<Integer, RobotType>> newLLN
                = spawnedRobots.addToTail(new AbstractMap.SimpleImmutableEntry<>(id, robotType));
        robotIdToLLN.put(id, newLLN);
        lastBotSpawn = rc.getRoundNum();

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
        checkRep();
    }

    /**
     * Report the death of a spawned robot
     * @param id of the robot that died
     */
    public void recordDeath(int id) {
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
        checkRep();
    }

    public int checkNearbyEnemies () {
        Team enemy = rc.getTeam().opponent();
        int sensorRadius = rc.getType().sensorRadiusSquared;
        int totalInfluence = 0;
        for (RobotInfo robot : rc.senseNearbyRobots(sensorRadius, enemy)) {
            totalInfluence += robot.type.equals(RobotType.POLITICIAN) ? robot.getInfluence() : 0;
        }
        lastEnemySeen = totalInfluence != 0 ? rc.getRoundNum() : lastEnemySeen;
        return totalInfluence;
    }

    public int getLastEnemySeen () { return lastEnemySeen; }

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

    public int getLastBotSpawn() { return lastBotSpawn; }

    public int getLocalRobotCount(){
        return slandererCount + politicianCount + muckrakerCount;
    }


    /**
     * Return the n sampled robot ids
     * If n <= num robot ids, then returns all
     * @return set of n randomly sampled robot ids
     */
    public List<Map.Entry<Integer, RobotType>> getNSpawnedRobotInfos(int n) {
        return spawnedRobots.sampleWithMemory(n);
    }


    /**
     * Update the estimated vote win ratio
     * @param voteWinRate from ECVoteController
     */
    public void setVoteWinRate(double voteWinRate) {
        this.voteWinRate = voteWinRate;
    }

    /**
     * @return current voting win ratio
     */
    public double getVoteWinRate() {
        return voteWinRate;
    }
    public Optional<NeutralEcSpottedInfo> getThisRoundNeutralEcSpottedInfo(){ return this.thisRoundNeutralEcSpottedInfo; }
    public void setThisRoundNeutralEcSpottedInfo(Optional<NeutralEcSpottedInfo> info){
        this.thisRoundNeutralEcSpottedInfo = info;
    }
}
