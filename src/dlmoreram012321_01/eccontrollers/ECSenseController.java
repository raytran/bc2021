package dlmoreram012321_01.eccontrollers;

import dlmoreram012321_01.BotEnlightenment;
import dlmoreram012321_01.flags.EnemySpottedInfo;
import dlmoreram012321_01.flags.FlagType;
import dlmoreram012321_01.flags.Flags;
import battlecode.common.*;

/**
 * Senses nearby environment and generates a safety evaluation
 * Where more positive = safer
 *   and more negative = danger
 */
public class ECSenseController implements ECController{
    RobotController rc;
    BotEnlightenment ec;
    private double averageSafety;
    private double averageBotChange;
    private double averageInfluenceChange;
    private int prevBotCount;
    private int prevInfluence;
    private Direction nextSpawnDirection = Direction.NORTH;
    private final int MEMORY = 100;
    public ECSenseController(RobotController rc, BotEnlightenment ec) {
        this.ec = ec;
        this.rc = rc;
    }

    @Override
    public void run() throws GameActionException {
        // update safety metric
        double safetyEval = ec.isInCorner() ? 10 : 0;
        int enemyMucks = 0;
        int radius = rc.getType().sensorRadiusSquared;
        Team enemy = rc.getTeam().opponent();
        MapLocation currentLoc = rc.getLocation();
        boolean ecPresent = false;
        for (RobotInfo ri : rc.senseNearbyRobots(radius)) {
            if (ri.team.equals(enemy)) {
                ec.setLastEnemySeen(rc.getRoundNum());
                if (ri.getType().equals(RobotType.MUCKRAKER)){
                    enemyMucks++;
                }
                if (ri.getType().equals(RobotType.ENLIGHTENMENT_CENTER)) {
                    ecPresent = true;
                    if(rc.getRoundNum() == 1) {
                        ec.setSpawnNextToEnemyEC(true);
                    }
                }
                if(ri.getType().equals(RobotType.POLITICIAN)) safetyEval -= ri.influence;
            } else {
                if(ri.getType().equals(RobotType.POLITICIAN)) safetyEval += ri.influence;
                int friendFlag = rc.getFlag(ri.ID);
                if (Flags.decodeFlagType(rc.getFlag(ri.ID)) == FlagType.ENEMY_SPOTTED) {
                    EnemySpottedInfo esi = Flags.decodeEnemySpotted(currentLoc, friendFlag);
                    if (esi.enemyType != RobotType.SLANDERER) {
                        if (esi.location.distanceSquaredTo(currentLoc) < rc.getType().sensorRadiusSquared + 20){
                            safetyEval -= 1/(1+(double) esi.location.distanceSquaredTo(currentLoc));
                        }
                    }
                }
            }
        }
        if (!(ecPresent && ec.isSpawnNextToEnemyEC())) {
            ec.setSpawnNextToEnemyEC(false);
        }
        ec.setNearbyEnemyMuckrakers(enemyMucks);
        //System.out.println("SAFETY EVAL: " + safetyEval);
        ec.setSafetyEval(safetyEval);
        updateAverageSafety(safetyEval);

        // update bot metrics
        int currentBotCount = ec.getLocalRobotCount();
        int botChange = currentBotCount - prevBotCount;
        updateAverageBotChange(botChange);
        prevBotCount = currentBotCount;

        // update influence metrics
        int currentInfluence = rc.getInfluence();
        int influenceChange = rc.getRoundNum() != 1 ? currentInfluence - prevInfluence : 0;
        updateAverageInfluenceChange(influenceChange);
        prevInfluence = currentInfluence;

        // check we are not surrounded
        checkSpawn();

        ec.updateDefenders();
    }

    /**
     * checks if our EC is able to spawn
     */
    private void checkSpawn() {
        boolean canSpawn = false;
        for (int i = 0; i < 8; i++) {
            if (rc.canBuildRobot(RobotType.MUCKRAKER, nextSpawnDirection, 1)) {
                canSpawn = true;
                break;
            } else {
                nextSpawnDirection = nextSpawnDirection.rotateRight();
            }
        }
        ec.setCanSpawn(canSpawn);
    }

    /**
     * computes approximate moving average of influence change of size MEMORY
     * @param change new change in influence
     */
    private void updateAverageInfluenceChange(double change) {
        if (rc.getRoundNum() == 1) {
            averageInfluenceChange = change;
        } else {
            averageInfluenceChange += (change - averageInfluenceChange) / MEMORY;
            ec.setAvgInfluenceChange(averageInfluenceChange);
        }
    }

    /**
     * computes approximate moving average bot change of size MEMORY
     * @param change new change in bot count
     */
    private void updateAverageBotChange(double change) {
        if (rc.getRoundNum() == 2) {
            averageBotChange = change;
        } else {
            averageBotChange += (change - averageBotChange) / MEMORY;
            ec.setAvgBotChange(averageBotChange);
        }
    }

    /**
     * computes approximate moving average of size MEMORY
     * @param eval new safety evaluation
     */
    private void updateAverageSafety(double eval) {
        if (rc.getRoundNum() == 1) {
            averageSafety = eval;
        } else {
            averageSafety += (eval - averageSafety) / MEMORY;
            ec.setAvgSafetyEval(averageSafety);
        }
    }
}
