package dlmoreram012421_01;

import dlmoreram012421_01.ds.CircularLinkedList;
import dlmoreram012421_01.ds.LinkedListNode;
import dlmoreram012421_01.eccontrollers.*;
import dlmoreram012421_01.flags.NeutralEcSpottedInfo;
import battlecode.common.*;

import java.util.*;

public class BotEnlightenment extends BotController {
    private final CircularLinkedList<Map.Entry<Integer,RobotType>> spawnedRobots = new CircularLinkedList<>();
    private final HashMap<Integer, LinkedListNode<Map.Entry<Integer, RobotType>>> robotIdToLLN = new HashMap<>();
    private final List<Integer> currentDefenders = new LinkedList<>();
    private LinkedListNode<Map.Entry<Integer, RobotType>> lastSample;

    private int muckrakerCount = 0;
    private int politicianCount = 0;
    private int defenderCount = 0;
    private int slandererCount = 0;
    private double voteWinRate = 0;
    private int nearbyEnemyMuckrakers = 0;
    private int nearbyAllyPoliticians = 0;
    private int lastEnemySeen = 0;

    private final ECVoteController voteController;
    private final ECFlagController flagController;
    private final ECSpawnController spawnController;
    private final ECSenseController senseController;
    private double safetyEval = 0;
    private double avgSafetyEval = 0;
    private double avgBotChange = 0;
    private double avgInfluenceChange = 0;
    private boolean canSpawn = true;
    private Optional<NeutralEcSpottedInfo> thisRoundNeutralEcSpottedInfo = Optional.empty();
    private boolean inCorner = false;
    private boolean spawnNextToEnemyEC = false;


    public BotEnlightenment(RobotController rc) throws GameActionException {
        super(rc);
        senseController = new ECSenseController(rc, this);
        voteController = new ECVoteController(rc, this);
        flagController = new ECFlagController(rc, this);
        spawnController = new ECSpawnController(rc,this);
    }
    @Override
    public BotController run() throws GameActionException {
        // Sense danger level
        senseController.run();
        // Read and update flags
        flagController.run();
        // Bid for votes
        voteController.run();
        //Run spawn controller
        spawnController.run();
        //Set the neutral spotted info
        thisRoundNeutralEcSpottedInfo = Optional.empty();
        // Search for boundary if we can
        //System.out.println("DLMORERAM 012421_01");
        if (Clock.getBytecodesLeft() > 1000){
            searchForNearbyBoundaries();
            if (!inCorner && inCorner() != null) {
                inCorner = true;
                //System.out.println("We are in a corner");
            }
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

    public void recordDefender(int id) {
        currentDefenders.add(id);
    }

    public void updateDefenders() {
        List<Integer> toRemove = new LinkedList<>();
        for (Integer id : currentDefenders) {
            if (!rc.canGetFlag(id)) {
                toRemove.add(id);
            }
        }
        for (Integer id : toRemove) {
            currentDefenders.remove(id);
        }
        defenderCount = currentDefenders.size();
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

    public Optional<NeutralEcSpottedInfo> getThisRoundNeutralEcSpottedInfo() { return this.thisRoundNeutralEcSpottedInfo; }

    public void setThisRoundNeutralEcSpottedInfo(Optional<NeutralEcSpottedInfo> info){
        this.thisRoundNeutralEcSpottedInfo = info;
    }

    public void setSafetyEval(double newSafety){
        safetyEval = newSafety;
    }

    public double getSafetyEval(){ return safetyEval; }

    public void setAvgSafetyEval(double newAvg) { avgSafetyEval = newAvg; }

    public double getAvgSafetyEval() { return avgSafetyEval; }

    public void setAvgBotChange(double newAvg) { avgBotChange = newAvg; }

    public double getAvgBotChange() { return avgBotChange; }

    public void setAvgInfluenceChange(double newAvg) { avgInfluenceChange = newAvg; }

    public double getAvgInfluenceChange() { return avgInfluenceChange; }

    public void setCanSpawn(boolean spawn) { canSpawn = spawn; }

    public boolean getCanSpawn() { return canSpawn; }

    public int getDefenderCount() {
        return defenderCount;
    }

    public void setDefenderCount(int amount) { defenderCount = amount; }

    public boolean isInCorner() { return inCorner; }

    public boolean isSpawnNextToEnemyEC() {
        return spawnNextToEnemyEC;
    }

    public void setSpawnNextToEnemyEC(boolean spawnNextToEnemyEC) {
        this.spawnNextToEnemyEC = spawnNextToEnemyEC;
    }

    public int getNearbyEnemyMuckrakers() {
        return nearbyEnemyMuckrakers;
    }

    public void setNearbyEnemyMuckrakers(int nearbyEnemyMuckrakers) {
        this.nearbyEnemyMuckrakers = nearbyEnemyMuckrakers;
    }

    public void setLastEnemySeen(int lastEnemySeen) {
        this.lastEnemySeen = lastEnemySeen;
    }

    public int getLastEnemySeen() {
        return lastEnemySeen;
    }

    public int getNearbyAllyPoliticians() {
        return nearbyAllyPoliticians;
    }

    public void setNearbyAllyPoliticians(int nearbyAllyPoliticians) {
        this.nearbyAllyPoliticians = nearbyAllyPoliticians;
    }
}
