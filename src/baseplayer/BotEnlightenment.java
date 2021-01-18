package baseplayer;

import baseplayer.ds.CircularLinkedList;
import baseplayer.ds.LinkedListNode;
import baseplayer.eccontrollers.*;
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
    private int lastBotSpawn = 0;

    private final ECBudgetController budgetController;
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
    private boolean opSpawned = false;


    public BotEnlightenment(RobotController rc) throws GameActionException {
        super(rc);
        senseController = new ECSenseController(rc, this);
        budgetController = new ECBudgetController(rc, this);
        voteController = new ECVoteController(rc, this, this.budgetController);
        flagController = new ECFlagController(rc, this);
        spawnController = new ECSpawnController(rc,this, this.budgetController);
    }
    @Override
    public BotController run() throws GameActionException {
        // Sense danger level
        senseController.run();
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

    public boolean getOpSpawned(){ return opSpawned; }

    public void setOpSpawned(boolean spawned){ opSpawned = spawned; }

    public void setCanSpawn(boolean spawn) { canSpawn = spawn; }

    public boolean getCanSpawn() { return canSpawn; }
}
