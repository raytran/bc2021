package baseplayernobudget;

import baseplayernobudget.ds.CircularLinkedList;
import baseplayernobudget.ds.LinkedListNode;
import baseplayernobudget.eccontrollers.*;
import baseplayernobudget.flags.NeutralEcSpottedInfo;
import battlecode.common.*;
import baseplayernobudget.Utilities;
import baseplayernobudget.flags.Flags;

import java.util.*;

public class BotEnlightenment extends BotController {
    private final CircularLinkedList<Map.Entry<Integer,RobotType>> spawnedRobots = new CircularLinkedList<>();
    private final HashMap<Integer, LinkedListNode<Map.Entry<Integer, RobotType>>> robotIdToLLN = new HashMap<>();
    private LinkedListNode<Map.Entry<Integer, RobotType>> lastSample;

    private int muckrakerCount = 0;
    private int politicianCount = 0;
    private int slandererCount = 0;
    private double voteWinRate = 0;
    private int prevVoteAmount = 0;
    private int prevVoteMult = 1;
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

    private static final int[] SLANDERER_VALUES = {21, 42, 63, 85, 107, 130, 154, 178, 203, 228, 255, 282, 310, 339, 368, 399, 431, 463, 497, 532, 568, 605, 644, 683, 724, 766, 810, 855, 902, 949};
    public static final RobotType[] buildQ = {RobotType.SLANDERER, /*RobotType.MUCKRAKER, RobotType.MUCKRAKER,*/ RobotType.POLITICIAN};
    public static int buildQIndex = 0;


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
        /*
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
        */


        RobotType toBuild = buildQ[buildQIndex];
        int inf = 1;
        switch(toBuild){
            case SLANDERER:
                inf = getSlandererBuildInfluence();
                break;
            case MUCKRAKER:
                inf = getMuckrakerBuildInfluence();
                break;
            case POLITICIAN:
                inf = getPoliticianBuildInfluence();
                break;
        }



        System.out.println("BUILDING " + toBuild + " WITH " + inf);
        for (Direction dir : Utilities.directions) {
            if (rc.canBuildRobot(toBuild, dir, inf)) {
                rc.buildRobot(toBuild, dir, inf);
                buildQIndex = (buildQIndex + 1) % buildQ.length;
                RobotInfo ri = rc.senseRobotAtLocation(rc.getLocation().add(dir));
                recordSpawn(ri.ID, ri.type);
                break;
            }
        }


        return this;
    }

    private int getSlandererBuildInfluence(){
        int inf = 1;
        int i = SLANDERER_VALUES.length - 1;
        while (SLANDERER_VALUES[i] > rc.getInfluence()){
            i -= 1;
            if (i < 0) break;
        }
        if (i < 0) inf = 1;
        else inf = SLANDERER_VALUES[i];
        return inf;
    }

    private int getMuckrakerBuildInfluence(){
        return Math.max(1, (int) (rc.getInfluence() * 0.01));
    }

    private int getPoliticianBuildInfluence(){
        return Math.max(15, (int) (rc.getInfluence() * 0.1));
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

    public void setPrevVoteAmount(int amount) { prevVoteAmount = amount; }

    public int getPrevVoteAmount() { return prevVoteAmount; }

    public void setPrevVoteMult(int mult) { prevVoteMult = mult; }

    public int getPrevVoteMult() { return prevVoteMult; }
}
